package project.dos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

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
        Intent intent = new Intent(this, AndroidLauncher.class);
        Creature starter;
        BattlefieldLogic.battlefieldLogic.getTurn();
        if (BattlefieldLogic.battlefieldLogic.owner == 0) {
            starter = new Creature(0, new Pair<Integer, Integer>(10, 1));
        }
        else {
            starter = new Creature(1, new Pair<Integer, Integer>(-10, -1));
        }
        if (BattlefieldLogic.battlefieldLogic.owner == 1)
            BattlefieldLogic.battlefieldLogic.hasTurn = false;
        BattlefieldLogic.battlefieldLogic.push(1, starter);
        startActivity(intent);
    }

}
