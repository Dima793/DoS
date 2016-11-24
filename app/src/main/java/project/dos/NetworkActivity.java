package project.dos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NetworkActivity extends Activity {

    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    private static TextView textView;

    public static NetworkController networkController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        textView = (TextView) findViewById(R.id.textView);

        networkController = new NetworkController(this);
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

        Intent intent = new Intent(this, HireActivity.class);
        startActivity(intent);

        //connect

        //networkController.stopAdvertising();
    }

    public void onClientClick(View v) {
        networkController.startDiscovery();

        Intent intent = new Intent(this, HireActivity.class);
        startActivity(intent);
        //connect

        //stopDiscovery();
    }

    public void onSendClick(View v) {
        networkController.sendMessageToAll("!!!");
    }

    public static void showText(String message) {
        if (textView != null) {
            textView.setText(message);
        }
    }
}
