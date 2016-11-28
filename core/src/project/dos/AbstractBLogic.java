package project.dos;


import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * Created by ASUS on 28.11.2016.
 */

public abstract class AbstractBLogic {
    public static AbstractBLogic battlefieldLogic;
    boolean isHost, hasTurn;
    int owner;
    Map<Pair<Integer, Integer>, AbstractCreature> creatures;

    public void passTurn() {
    }

    public void getTurn() {
    }

    public int get_dist(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {
        return 0;
    }

    public void push(int tp, AbstractCreature a) {
    }

    public void accept (String changes) {
    }

    public void kill(AbstractCreature killed) {
    }
}