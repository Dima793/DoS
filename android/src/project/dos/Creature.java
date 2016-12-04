package project.dos;


import java.util.AbstractCollection;
import java.util.ArrayList;

/**
 * Created by ASUS on 17.11.2016.
 */

public class Creature extends AbstractCreature{
    private int hp, ap, owner;
    Pair<Integer, Integer> pos;
    public String name;
    ArrayList<Pair<Integer, String>> abilities = new ArrayList<Pair<Integer, String>>();

    public Creature() {}

    public Creature(int newOwner, Pair<Integer, Integer> newPos) { //for the demo version there'll be just one type of creatures
        hp = 100;
        ap = 5;
        name = "DarkKnight";
        abilities.add(new Pair<Integer, String>(1, "move"));
        abilities.add(new Pair<Integer, String>(3, "hit"));
        owner = newOwner;
        pos = newPos;
    }

    @Override
    public void apply(int ind, Pair<Integer, Integer> targetPos) {
        if (ind == 0) {
            int pointsSpent = BattlefieldLogic.battlefieldLogic.get_dist(pos, targetPos);
            if (pointsSpent > ap)
                return;
            BattlefieldLogic.battlefieldLogic.push(0, this);
            pos = targetPos;
            ap -= pointsSpent;
            BattlefieldLogic.battlefieldLogic.push(1, this);
        }
        else {
            int pointsSpent = abilities.get(ind).first;
            if (pointsSpent > ap || BattlefieldLogic.battlefieldLogic.get_dist(pos, targetPos) > 1)
                return;
            ap -= pointsSpent;
            Creature target = BattlefieldLogic.battlefieldLogic.creatures.get(targetPos);
            if (target.owner != owner)
                target.takeHit(25);
            BattlefieldLogic.battlefieldLogic.push(1, this);
            BattlefieldLogic.battlefieldLogic.push(1, target);
        }
    }

    @Override
    public String toString() {
        return "" + pos.first + " " + pos.second + " " + owner + " " + hp + " " + ap + " " + name;
    }

    @Override
    public Creature fromString(String s) {
        String[] params = s.split(" ");
        Creature result = new Creature();
        result.pos = new Pair<Integer, Integer>(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
        result.owner = Integer.parseInt(params[2]);
        result.hp = Integer.parseInt(params[3]);
        result.ap = Integer.parseInt(params[4]);
        result.name = params[5];
        result.abilities.add(new Pair<Integer, String>(1, "move"));
        result.abilities.add(new Pair<Integer, String>(3, "hit"));
        return result;
    }

    @Override
    public void takeHit(int dmg) {
        hp -= dmg;
        if (hp <= 0)
            BattlefieldLogic.battlefieldLogic.kill(this);
    }

    @Override
    public void replenishAP() {
        ap = 5;
    }

    @Override
    public int getOwner() {
        return owner;
    }
}