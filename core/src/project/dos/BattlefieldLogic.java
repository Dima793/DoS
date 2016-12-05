package project.dos;


import com.badlogic.gdx.Gdx;

import java.util.HashMap;

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
    public HashMap<Pair<Integer, Integer>, CreatureHandler> creatures;
    public String message;
    public Creature creatureToSetOrRemove;
    private Runnable messageSender;
    private Runnable creatureSetter;
    private Runnable creatureRemover;

    public void configure(Runnable sender, Runnable setter, Runnable remover) {
        creatures = new HashMap<>();
        message = "";
        creatureToSetOrRemove = new Creature(0, new Pair<Integer, Integer>(1, 1));
        messageSender = sender;
        creatureSetter = setter;
        creatureRemover = remover;
    }

    public void passTurn() {
        hasTurn = false;
        sendMessage("A");
    }

    public void pushToDatabase (Creature creature) {
        if (owner == 0)
            setCreature(creature);
    }

    public void removeFromDatabase (Creature creature) {
        if (owner == 0)
            removeCreature(creature);
    }

    public void getTurn() {
        hasTurn = true;
        BattleField.currentUnitChanged();
        for (CreatureHandler crh : creatures.values()) {
            if (crh.get().getOwner() == owner)
                crh.get().replenishAP();
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
        sendMessage(ans);
    }

    public void accept (String changes) {
        String[] realChanges = changes.split(";");
        Creature creature = new Creature().fromString(realChanges[1]);
        if (Integer.parseInt(realChanges[0]) == 0) {
            creatures.remove(creature.pos);
            removeFromDatabase(creature);
        }
        else {
            creatures.put(creature.pos, new CreatureHandler(creature));
            pushToDatabase(creature);
        }
    }

    public void kill(Creature killed) {
        creatures.remove(killed.pos);
        push(0, killed);
    }

    public void sendMessage(String newMessage) {
        message = newMessage;
        messageSender.run();
    }

    public void setCreature(Creature creatureToSet) {
        creatureToSetOrRemove = creatureToSet;
        creatureSetter.run();
    }

    public void removeCreature(Creature creatureToRemove) {
        creatureToSetOrRemove = creatureToRemove;
        creatureRemover.run();
    }
}