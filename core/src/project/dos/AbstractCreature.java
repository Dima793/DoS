package project.dos;

/**
 * Created by ASUS on 28.11.2016.
 */

import java.util.ArrayList;

import java.util.ArrayList;

/**
 * Created by ASUS on 17.11.2016.
 */

public abstract class AbstractCreature {
    private int hp, ap, owner;
    Pair<Integer, Integer> pos;
    public String name;
    ArrayList<Pair<Integer, String>> abilities = new ArrayList<Pair<Integer, String>>();
    public void apply(int ind, Pair<Integer, Integer> targetPos) {
    }

    @Override
    public String toString() {
        return "";
    }

    abstract public AbstractCreature fromString(String s);

    public void takeHit(int dmg) {
    }

    public void replenishAP() {
        ap = 5;
    }

    public int getOwner() {
        return owner;
    }
}
