package project.dos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetworkController implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener {

    private static final long TIMEOUT_ADVERTISE = 1000L * 30L;
    private static final long TIMEOUT_DISCOVER = 1000L * 30L;
    private GoogleApiClient googleApiClient;
    private String serviceId;
    public static boolean isHost = false;
    private ArrayList<String> otherEndpointsIds = new ArrayList<>();
    private HashMap<String, String> otherEndpointsNames = new HashMap<>();// usernames

    private Activity activity;
    private MyListDialog myListDialog;
    private AlertDialog connectionRequestDialog;

    NetworkController(Activity a) {
        activity = a;
        serviceId = activity.getResources().getString(R.string.service_id);
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    @Override
    public void onEndpointFound(final String endpointId, String deviceId,
                                String serviceId, final String endpointName) {

        if (!serviceId.equals(this.serviceId)) {
            return;
        }

        if (myListDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setTitle("Endpoint(s) Found")
                    .setCancelable(true)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myListDialog.dismiss();
                        }
                    });

            myListDialog = new MyListDialog(activity, builder,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedEndpointName = myListDialog.getItemKey(which);
                    String selectedEndpointId = myListDialog.getItemValue(which);

                    NetworkController.this.connectTo(selectedEndpointId, selectedEndpointName);
                    myListDialog.dismiss();
                }
            });
        }

        myListDialog.addItem(endpointName, endpointId);
        myListDialog.show();
    }

    @Override
    public void onEndpointLost(String endpointId) {
        NetworkActivity.showText(otherEndpointsNames.get(endpointId) + " lost");

        if (myListDialog != null) {
            myListDialog.removeItemByValue(endpointId);
        }
    }

    @Override
    public void onConnectionRequest(final String endpointId, String deviceId,
                                    final String endpointName, byte[] payload) {

        connectionRequestDialog = new AlertDialog.Builder(activity)
                .setTitle("Connection Request")
                .setMessage("Do you want to connect to " + endpointName + "?")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] payload = null;
                        Nearby.Connections.acceptConnectionRequest(googleApiClient,
                                endpointId, payload, NetworkController.this)
                                .setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        if (status.isSuccess()) {
                                            NetworkActivity.showText("Connection succeed");
                                            otherEndpointsIds.add(endpointId);
                                            otherEndpointsNames.put(endpointId, endpointName);
                                            BattlefieldLogic.battlefieldLogic.owner = 0;
                                        } else {
                                            NetworkActivity.showText("Connection failed");
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Nearby.Connections.rejectConnectionRequest(googleApiClient, endpointId);
                    }
                }).create();

        connectionRequestDialog.show();
    }

    @Override
    public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {
        //NetworkActivity.showText(otherEndpointsNames.get(endpointId) + ": " + new String(payload));
        //otherEndpointsNames depends on endpointId
        String s = new String(payload);
        if (s.charAt(0) == 'A') {
            BattlefieldLogic.battlefieldLogic.getTurn();
        }
        else {
            BattlefieldLogic.battlefieldLogic.accept(s);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        NetworkActivity.showText("Ready to connect");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        NetworkActivity.showText("Connection suspended: " + cause);

        googleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        NetworkActivity.showText("Connection failed: " + connectionResult);
    }

    @Override
    public void onDisconnected(String endpointId) {
        NetworkActivity.showText(otherEndpointsNames.get(endpointId) + " disconnected");
    }

    public void onStart() {
        remindAboutNetworkConnection();
        googleApiClient.connect();
    }

    public void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void connectTo(final String endpointId, final String endpointName) {
        String myName = null;// username, 'null' -> device model
        byte[] myPayload = null;
        Nearby.Connections.sendConnectionRequest(googleApiClient, myName, endpointId, myPayload,
                new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String remoteEndpointId, Status status,
                                                     byte[] bytes) {
                        if (status.isSuccess()) {
                            NetworkActivity.showText("Connected to " + endpointName);
                            otherEndpointsIds.add(endpointId);
                            otherEndpointsNames.put(endpointId, endpointName);
                            BattlefieldLogic.battlefieldLogic.owner = 1;
                        } else {
                            NetworkActivity.showText("Connection to " + endpointName + " failed");
                        }
                    }
                }, this);
    }

    public void startAdvertising() {
        remindAboutNetworkConnection();

        isHost = true;

        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(activity.getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        String myName = null;// username, 'null' -> device model
        Nearby.Connections.startAdvertising(googleApiClient, myName, appMetadata, TIMEOUT_ADVERTISE,
                this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    NetworkActivity.showText("Advertising...");
                } else {
                    int statusCode = result.getStatus().getStatusCode();
                    if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                        NetworkActivity.showText("Already advertising");
                    } else {
                        NetworkActivity.showText("Advertising failed: " + statusCode);
                    }
                }
            }
        });
    }

    public void startDiscovery() {
        remindAboutNetworkConnection();

        if (isHost) {
            NetworkActivity.showText("Can't discover while advertising");
            return;
        }

        Nearby.Connections.startDiscovery(googleApiClient, serviceId, TIMEOUT_DISCOVER, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            NetworkActivity.showText("Discovering...");
                        } else {
                            int statusCode = status.getStatusCode();
                            if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING) {
                                NetworkActivity.showText("Already discovering");
                            } else {
                                NetworkActivity.showText("Discovering failed: " + statusCode);
                            }
                        }
                    }
                });
    }

    public void stopAdvertising() {
        Nearby.Connections.stopAdvertising(googleApiClient);
        isHost = false;
    }

    public void stopDiscovery() {
        Nearby.Connections.stopDiscovery(googleApiClient, serviceId);
    }

    private void remindAboutNetworkConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                networkInfo.isConnectedOrConnecting()) {
            return;// Wi-Fi connection is alright
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Wi-Fi connection required")
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                activity.finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void sendMessageTo(Integer endpointNumber, String message) {// reliable message
        Nearby.Connections.sendReliableMessage(googleApiClient,
                otherEndpointsIds.get(endpointNumber), message.getBytes());
    }

    public void sendMessageToAll(String message) {// reliable message
        for (String endpointId : otherEndpointsIds) {
            Nearby.Connections.sendReliableMessage(googleApiClient,
                    endpointId, message.getBytes());
        }
    }
}