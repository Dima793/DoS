package project.dos;

import java.util.HashMap;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static project.dos.BattleField.battleField;

/**
 * Created by ASUS on 28.11.2016.
 */

@SuppressWarnings("ALL")
public final class BattlefieldLogic {
    public static BattlefieldLogic battlefieldLogic = new BattlefieldLogic();

    public boolean hasTurn = false;
    public String toOut = new String();
    public int owner;
    public HashMap<Pair<Integer, Integer>, CreatureHandler> creatures;
    private EventsListener<String> messageSender;
    private EventsListener<Creature> creatureChanger;

    public void configure(EventsListener<String> mSender, EventsListener<Creature> cChanger) {
        creatures = new HashMap<Pair<Integer, Integer>, CreatureHandler>();
        messageSender = mSender;
        creatureChanger = cChanger;
    }

    public void passTurn() {
        hasTurn = false;
        BattleField.currentUnitChanged();
        messageSender.listenEvent(0, "A");
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
        BattleField.currentUnitChanged();
        for (CreatureHandler crh : creatures.values()) {
            if (crh.creature.getOwner() == owner)
                crh.creature.replenishAP();
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
        messageSender.listenEvent(0, ans);
    }

    public void accept (String changes) {
        String[] realChanges = changes.split(";");
        Creature creature = new Creature().fromString(realChanges[1]);
        if (Integer.parseInt(realChanges[0]) == 0) {
            creatures.remove(creature.pos);
            removeFromDatabase(creature);
            battleField.units.remove(creature.turnID);
            BattleField.currentUnitChanged();
        }
        else {
            CreatureHandler creatureHandler = new CreatureHandler(creature);
            creatures.put(creature.pos, creatureHandler);
            pushToDatabase(creature);
            battleField.units.put(creature.turnID, new Unit(creatureHandler, creature.turnID));
            battleField.units.get(creature.turnID).teleportBy(new HexCoord(0, 0, 0));
        }
    }

    public void kill(Creature killed) {
        creatures.remove(killed.pos);
        push(0, killed);
    }
}