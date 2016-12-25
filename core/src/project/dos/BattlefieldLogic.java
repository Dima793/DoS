package project.dos;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static project.dos.BattleField.battleField;

/**
 * Created by ASUS on 28.11.2016.
 */

public final class BattlefieldLogic {
    public static BattlefieldLogic battlefieldLogic = new BattlefieldLogic();
    public static int freeID = 0;
    public boolean hasTurn = false;
    public String toOut = new String();
    public int owner;
    public HashMap<Integer, Creature> creatures;
    private EventsListener<String> messageSender;
    private EventsListener<Creature> creatureChanger;

    public void configure(EventsListener<String> mSender, EventsListener<Creature> cChanger) {
        creatures = new HashMap<Integer, Creature>();
        messageSender = mSender;
        creatureChanger = cChanger;
    }

    public void passTurn() {
        hasTurn = false;
        int curOwner = creatures.get(battleField.currentUnit).owner;
        battleField.currentUnitChanged();
        messageSender.listenEvent(0, "B");
        if (creatures.get(battleField.currentUnit).owner == curOwner) {
            getTurn();
            Gdx.app.log("Info", "getTurn in passTurn");
        }
        else {
            messageSender.listenEvent(0, "A");
        }
    }

    public void pushToDatabase (Creature creature) {
        if (owner == 0)
            creatureChanger.listenEvent(0, creature);
    }

    public void removeFromDatabase (Creature creature) {
        if (owner == 0)
            creatureChanger.listenEvent(1, creature);
    }

    public void getTurn() {
        hasTurn = true;
        for (Creature cr : creatures.values()) {
            if (cr.owner == owner) {
                cr.replenishAP();
            }
        }
    }

    public int get_dist(HexCoord a, HexCoord b) {
        return max(max(abs(a.x - b.x), abs(a.y - b.y)), abs(a.z - b.z));
    }

    public void push(int tp, Creature a) {
        if (!hasTurn)
            return;
        if (tp == 0) // death
            removeFromDatabase(a);
        else // birth or change
            pushToDatabase(a);
        String ans = Integer.toString(tp) + ";" + a.toString();
        messageSender.listenEvent(0, ans);
    }

    public void accept (String changes) {
        String[] realChanges = changes.split(";");
        Creature creature = new Creature().fromString(realChanges[1]);
        int tp = Integer.parseInt(realChanges[0]);
        if (tp == 0) { //death
            creatures.remove(creature.iD);
            removeFromDatabase(creature);
            //remove sprite
        }
        else if (tp == 1){ //birth
            creature.unit = new Unit(creature, true);
            creatures.put(creature.iD, creature);
            pushToDatabase(creature);
            creature.unit.teleportTo(creature.pos);
        }
        else {
            Creature creature1 = creatures.get(creature.iD);
            creature1.owner = creature.owner;
            creature1.pos = creature.pos;
            creature1.hp = creature.hp;
            creature1.ap = creature.ap;
            creature1.name = creature.name;
            creature1.unit.teleportTo(creature1.pos);
            pushToDatabase(creature1);
        }
    }

    public void kill(Creature killed) {
        creatures.remove(killed.pos);
        push(0, killed);
    }
}