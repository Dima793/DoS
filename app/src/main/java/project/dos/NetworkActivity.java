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

public class NetworkActivity extends Activity {

    private static TextView textView;

    NetworkController networkController = new NetworkController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        textView = (TextView) findViewById(R.id.textView);

        networkController.onCreate(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        networkController.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        networkController.onStop();
    }

    public void onHostClick(View v) {
        if (!networkController.startAdvertising()) {
            return;
        }

        //connect

        //networkController.stopAdvertising();
    }

    public void onClientClick(View v) {
        if (!networkController.startDiscovery()) {
            return;
        }

        //connect

        //stopDiscovery(); (?)
    }

    public void onSendClick(View v) {
        networkController.sendMessage("!!!");
    }

    public static void showText(String message) {
        if (textView != null) {
            textView.setText(message);
        }
    }
}
