package project.dos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

public class GameActivity extends FragmentActivity implements
        AndroidFragmentApplication.Callbacks,
        EventsListener<Integer> {

    private Fragment[] fragments;
    private FragmentTransaction fragmentTransaction;

    public NetworkController networkController;
    public DBController dataBaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        networkController = new NetworkController(this);
        dataBaseController = new DBController(this);

        fragments = new Fragment[3];
        fragments[0] = new NetworkFragment();
        fragments[1] = new HireFragment();
        fragments[2] = new GraphicsFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentLayout, fragments[0]);
        fragmentTransaction.commit();
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

    @Override
    public void listenEvent(int eventCase, Integer fragmentNumber) {//replace fragment
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, fragments[fragmentNumber]);
        fragmentTransaction.commit();
    }

    @Override
    public void exit() {

    }
}
