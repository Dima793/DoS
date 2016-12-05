package project.dos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static project.dos.BattlefieldLogic.battlefieldLogic;
import static project.dos.NetworkController.networkController;

public class HireActivity extends AppCompatActivity {
    Integer pointsLeft = 1000;
    ArrayList<Integer> number = new ArrayList<>();
    ArrayList<Creature> creatures = new ArrayList<>();
    TextView desc;
    Spinner spinner;
    TextView pointsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire);
        desc = (TextView) findViewById(R.id.descriptionText);
        pointsText = (TextView) findViewById(R.id.pointsText);
        pointsText.setText(pointsLeft.toString());
        creatures.add(new Creature(0, new Pair<Integer, Integer>(0, 0)));
        number.add(0);
        spinner = (Spinner) findViewById(R.id.spinner);
        //spinner.add
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onClickDec(View v) {

    }

    public void onClickInc(View v) {

    }

    public void onClickGo(View v) {
        battlefieldLogic.configure(networkController::sendMessageToAll);
        Intent intent = new Intent(this, AndroidLauncher.class);
        Creature starter1 = new Creature(0, new Pair<>(10, -1));
        Creature starter2 = new Creature(1, new Pair<>(-10, 1));
        battlefieldLogic.creatures.put(starter1.pos, starter1);
        battlefieldLogic.creatures.put(starter2.pos, starter2);
        if (networkController.isHost) {
            battlefieldLogic.getTurn();
        }
        if (battlefieldLogic.owner == 0)
            DBController.initialize(this);
        startActivity(intent);
    }

}
