package project.dos;

import android.app.ApplicationErrorReport;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by ASUS on 28.11.2016.
 */

public final class Battlefield {
    public static Battlefield battlefield;
    private boolean isHost, hasTurn;
    private int owner;
    private ArrayList<Creature> tiles;
    public void passTurn() {
        hasTurn = false;
        // we need something to pass the turn
    }

    public void getTurn() {
        hasTurn = true;
        for (Creature cr : tiles) {
            if (cr != null && cr.getOwner() == owner)
                cr.replenishAP();
        }
    }

    public int get_dist(int a, int b) {
        return abs(b - a);
    }

    public void push(Integer posA, Creature a, Integer posB, Creature b) {
        if (!hasTurn)
            return;
        String ans = posA.toString() + ";" + a.toString() + ";" + posB.toString() + ";" + b.toString();
        //need to push
    }

    public ArrayList<Creature> getTiles() {
        return tiles;
    }

    public void accept (String changes) {
        String[] realChanges = changes.split(";");
        tiles.set(Integer.parseInt(realChanges[0]), Creature.fromString(realChanges[1]));
        tiles.set(Integer.parseInt(realChanges[2]), Creature.fromString(realChanges[3]));
    }

    public void kill(int pos) {
        if (tiles.get(pos) == null)
            return;
        tiles.set(pos, null);
        push(pos, null, pos, null);
    }
}
