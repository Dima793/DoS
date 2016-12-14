package project.dos;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class HireFragment extends Fragment {

    Integer pointsLeft = 1000;
    ArrayList<Integer> number = new ArrayList<Integer>();
    ArrayList<Creature> creatures = new ArrayList<Creature>();
    TextView desc;
    Spinner spinner;
    TextView pointsText;

    EventsListener<Integer> fragmentChanger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hire, null);

        desc = (TextView) v.findViewById(R.id.descriptionText);
        pointsText = (TextView) v.findViewById(R.id.pointsText);
        pointsText.setText(Integer.toString(pointsLeft));
        creatures.add(new Creature(0, new Pair<Integer, Integer>(0, 0)));
        number.add(0);
        spinner = (Spinner) v.findViewById(R.id.spinner);
        //spinner.add


        Button decButton = (Button) v.findViewById(R.id.decButton);
        Button incButton= (Button) v.findViewById(R.id.incButton);
        Button goButton = (Button) v.findViewById(R.id.goButton);

        decButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //dec
            }
        });
        incButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //inc
            }
        });
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fragmentChanger.listenEvent(0, 2);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {//context is an instance of FragmentActivity
        super.onAttach(context);
        try {
            fragmentChanger = (EventsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EventsListener");
        }
    }

}
