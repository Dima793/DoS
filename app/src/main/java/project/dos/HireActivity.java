package project.dos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class HireActivity extends AppCompatActivity {
    Integer pointsLeft = 1000;
    ArrayList<Integer> number;
    ArrayList<Creature> creatures;
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
        creatures.add(new Creature());
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

}
