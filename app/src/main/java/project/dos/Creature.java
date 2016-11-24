package project.dos;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ASUS on 17.11.2016.
 */

public class Creature {
    int hp, ap;
    String name;
    ArrayList<Pair<Integer, String>> abilities;
    public Creature() { //for the demo version there'll be just one type of creatures
        hp = 100;
        ap = 5;
        abilities.add(new Pair<Integer, String>(1, "move"));
    }
}
