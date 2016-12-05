package project.dos;


import java.util.HashMap;
import java.util.function.Consumer;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * Created by ASUS on 28.11.2016.
 */

@SuppressWarnings("ALL")
public final class BattlefieldLogic {
    public static BattlefieldLogic battlefieldLogic = new BattlefieldLogic();

    public boolean hasTurn = false;
    public int owner;
    public HashMap<Pair<Integer, Integer>, Creature> creatures =
            new HashMap<Pair<Integer, Integer>, Creature>();
    private Consumer<String> messageSender;
    public Consumer<Creature> setCreature;
    public Consumer<Creature> removeCreature;

    public void configure(Consumer<String> sender) {
        messageSender = sender;
    }

    public void passTurn() {
        hasTurn = false;
        messageSender.accept("A");
    }

    public void pushToDatabase (Creature creature) {
        if (owner == 0)
            setCreature.accept(creature);
    }

    public void removeFromDatabase (Creature creature) {
        if (owner == 0)
            removeCreature.accept(creature);
    }

    public void getTurn() {
        hasTurn = true;
        BattleField.currentUnitChanged();
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
        if (tp == 0)
            removeFromDatabase(a);
        else
            pushToDatabase(a);
        String ans = Integer.toString(tp) + ";" + a.toString();
        messageSender.accept(ans);
    }

    public void accept (String changes) {
        String[] realChanges = changes.split(";");
        Creature creature = new Creature().fromString(realChanges[1]);
        if (Integer.parseInt(realChanges[0]) == 0) {
            creatures.remove(creature.pos);
            removeFromDatabase(creature);
        }
        else {
            creatures.put(creature.pos, creature);
            pushToDatabase(creature);
        }
    }

    public void kill(Creature killed) {
        creatures.remove(killed.pos);
        push(0, killed);
    }
}