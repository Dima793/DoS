package project.dos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static project.dos.NetworkController.networkController;

public class NetworkActivity extends Activity {

    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    private static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        textView = (TextView) findViewById(R.id.textView);

        networkController.configure(this);
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
        networkController.startAdvertising();

        //connect

        //networkController.stopAdvertising();
    }

    public void onClientClick(View v) {
        networkController.startDiscovery();

        //connect

        //networkController.stopDiscovery();
    }

    public void onStartClick(View v) {
        networkController.stopDiscovery();
        networkController.stopAdvertising();
        Intent intent = new Intent(this, HireActivity.class);
        startActivity(intent);
    }

    public static void showText(String message) {
        if (textView != null) {
            textView.setText(message);
        }
    }
}
