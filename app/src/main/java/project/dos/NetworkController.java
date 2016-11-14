package project.dos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class NetworkController implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener {

    @Retention(RetentionPolicy.CLASS)
    @IntDef({STATE_IDLE, STATE_READY, STATE_ADVERTISING, STATE_DISCOVERING, STATE_CONNECTED})
    public @interface NearbyConnectionState {}
    private static final int STATE_IDLE = 0;
    private static final int STATE_READY = 1;
    private static final int STATE_ADVERTISING = 2;
    private static final int STATE_DISCOVERING = 3;
    private static final int STATE_CONNECTED = 4;


    private static final long TIMEOUT_ADVERTISE = 1000L * 30L;
    private static final long TIMEOUT_DISCOVER = 1000L * 30L;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsHost = false;
    private String mOtherEndpointId;

    private Context context;
    private MyListDialog mMyListDialog;
    private AlertDialog mConnectionRequestDialog;

    @Override
    public void onEndpointFound(final String endpointId, String deviceId,
                                String serviceId, final String endpointName) {
        // This device is discovering endpoints and has located an advertiser. Display a dialog to
        // the user asking if they want to connect, and send a connection request if they do.
        if (mMyListDialog == null) {
            // Configure the AlertDialog that the MyListDialog wraps
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Endpoint(s) Found")
                    .setCancelable(true)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMyListDialog.dismiss();
                        }
                    });

            // Create the MyListDialog with a listener
            mMyListDialog = new MyListDialog(context, builder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedEndpointName = mMyListDialog.getItemKey(which);
                    String selectedEndpointId = mMyListDialog.getItemValue(which);

                    NetworkController.this.connectTo(selectedEndpointId, selectedEndpointName);
                    mMyListDialog.dismiss();
                }
            });
        }

        mMyListDialog.addItem(endpointName, endpointId);
        mMyListDialog.show();
    }

    @Override
    public void onEndpointLost(String endpointId) {
        NetworkActivity.showText(endpointId + " lost");

        // An endpoint that was previously available for connection is no longer. It may have
        // stopped advertising, gone out of range, or lost connectivity. Dismiss any dialog that
        // was offering a connection.
        if (mMyListDialog != null) {
            mMyListDialog.removeItemByValue(endpointId);
        }
    }

    @Override
    public void onConnectionRequest(final String endpointId, String deviceId,
                                    final String endpointName, byte[] payload) {
        if (!mIsHost) {
            return;
        }

        mConnectionRequestDialog = new AlertDialog.Builder(context)
                .setTitle("Connection Request")
                .setMessage("Do you want to connect to " + endpointName + "?")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] payload = null;
                        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient,
                                endpointId, payload, NetworkController.this)
                                .setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        if (status.isSuccess()) {
                                            NetworkActivity.showText("Connection succeed");

                                            mOtherEndpointId = endpointId;
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
                        Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, endpointId);
                    }
                }).create();

        mConnectionRequestDialog.show();
    }

    @Override
    public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {
        // A message has been received from a remote endpoint.
        NetworkActivity.showText(endpointId + ": " + new String(payload));
    }

    @Override
    public void onConnected(Bundle bundle) {
        NetworkActivity.showText("Ready");
    }

    @Override
    public void onConnectionSuspended(int i) {
        NetworkActivity.showText("Connection suspended: " + i);

        // Try to re-connect
        mGoogleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        NetworkActivity.showText("Connection failed: " + connectionResult);
    }

    @Override
    public void onDisconnected(String endpointId) {
        NetworkActivity.showText(endpointId + " disconnected");
    }

    public void onCreate(Context c) {
        context = c;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    public void onStart() {
        mGoogleApiClient.connect();
    }

    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void connectTo(final String endpointId, final String endpointName) {
        // Send a connection request to a remote endpoint. By passing 'null' for
        // the name, the Nearby Connections API will construct a default name
        // based on device model such as 'LGE Nexus 5'.
        String myName = null;
        byte[] myPayload = null;
        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, myName, endpointId, myPayload,
                new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String remoteEndpointId, Status status,
                                                     byte[] bytes) {
                        if (status.isSuccess()) {
                            NetworkActivity.showText("Connected to " + endpointName);
                            mOtherEndpointId = endpointId;
                        } else {
                            NetworkActivity.showText("Connection to " + endpointName + " failed");
                        }
                    }
                }, this);
    }

    public boolean startAdvertising() {
        if (!isConnectedToNetwork()) {
            NetworkActivity.showText("WI-FI connection required");
            return false;
        }

        mIsHost = true;

        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(context.getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        String name = null;
        Nearby.Connections.startAdvertising(mGoogleApiClient, name, appMetadata, TIMEOUT_ADVERTISE,
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
                        NetworkActivity.showText("Advertising failed. Status code: " + statusCode);
                    }
                }
            }
        });
        return true;
    }

    public boolean startDiscovery() {
        if (!isConnectedToNetwork()) {
            NetworkActivity.showText("WI-FI connection required");
            return false;
        }
        String serviceId = context.getString(R.string.service_id);

        // Set an appropriate timeout length in milliseconds
        long DISCOVER_TIMEOUT = 1000L;

        // Discover nearby apps that are advertising with the required service ID.
        Nearby.Connections.startDiscovery(mGoogleApiClient, serviceId, TIMEOUT_DISCOVER, this)
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
                                NetworkActivity.showText("Discovering failed. Status code: " + statusCode);
                            }
                        }
                    }
                });
        return true;
    }

    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (info != null && info.isConnectedOrConnecting());
    }

    public void sendMessage(String message) {// reliable message
        Nearby.Connections.sendReliableMessage(mGoogleApiClient, mOtherEndpointId, message.getBytes());
    }
}
