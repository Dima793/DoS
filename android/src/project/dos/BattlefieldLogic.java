package project.dos;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * Created by ASUS on 28.11.2016.
 */

public final class BattlefieldLogic {
    public static BattlefieldLogic battlefieldLogic;
    boolean isHost, hasTurn;
    int owner;
    Map<Pair<Integer, Integer>, Creature> creatures;

    public void passTurn() {
        hasTurn = false;
        NetworkActivity.networkController.sendMessageToAll("A");
    }

    public void getTurn() {
        hasTurn = true;
        for (Creature cr : creatures.values()) {
            if (cr.getOwner() == owner)
                cr.replenishAP();
            }
    }

    public int get_dist(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {
        int az = -a.first - a.second, bz = -b.first - b.second;
        return max(max(abs(a.first - b.first), abs(a.second - b.second)), abs(az - bz));
    }

    public void push(int tp, Creature a) {
        if (!hasTurn)
            return;
        String ans = Integer.toString(tp) + ";" + a.toString();
        NetworkActivity.networkController.sendMessageToAll(ans);
    }

    public void accept (String changes) {
        String[] realChanges = changes.split(";");
        Creature creature = Creature.fromString(realChanges[1]);
        if (Integer.parseInt(realChanges[0]) == 0) {
            creatures.remove(creature.pos);
        }
        else {
            creatures.put(creature.pos, creature);
        }
    }

    public void kill(Creature killed) {
        creatures.remove(killed.pos);
        push(0, killed);
    }
}