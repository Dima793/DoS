package project.dos;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * Created by ASUS on 28.11.2016.
 */

public final class BattlefieldLogic extends AbstractBLogic {
    public static BattlefieldLogic battlefieldLogic;
    boolean hasTurn = false;
    int owner;
    HashMap<Pair<Integer, Integer>, Creature> creatures = new HashMap<>();

    @Override
    public void passTurn() {
        hasTurn = false;
        NetworkActivity.networkController.sendMessageToAll("A");
    }

    @Override
    public void getTurn() {
        hasTurn = true;
        BattleField.currentUnitChanged();
        for (Creature cr : creatures.values()) {
            if (cr.getOwner() == owner)
                cr.replenishAP();
            }
    }

    @Override
    public int get_dist(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {
        int az = -a.first - a.second, bz = -b.first - b.second;
        return max(max(abs(a.first - b.first), abs(a.second - b.second)), abs(az - bz));
    }

    @Override
    public void push(int tp, AbstractCreature a) {
        if (!hasTurn)
            return;
        String ans = Integer.toString(tp) + ";" + a.toString();
        NetworkActivity.networkController.sendMessageToAll(ans);
    }

    @Override
    public void accept (String changes) {
        String[] realChanges = changes.split(";");
        Creature creature = new Creature().fromString(realChanges[1]);
        if (Integer.parseInt(realChanges[0]) == 0) {
            creatures.remove(creature.pos);
        }
        else {
            creatures.put(creature.pos, creature);
        }
    }

    @Override
    public void kill(AbstractCreature killed) {
        creatures.remove(killed.pos);
        push(0, killed);
    }

    public static void configure() {
        battlefieldLogic = new BattlefieldLogic();
        AbstractBLogic.battlefieldLogic = battlefieldLogic;
    }
}