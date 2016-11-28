package project.dos;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ASUS on 17.11.2016.
 */

public class Creature {
    private int hp, ap, pos, owner;
    public String name;
    ArrayList<Pair<Integer, String>> abilities = new ArrayList<Pair<Integer, String>>();

    public Creature() {}

    public Creature(int newOwner, int newPos) { //for the demo version there'll be just one type of creatures
        hp = 100;
        ap = 5;
        name = "Target dummy";
        abilities.add(new Pair<Integer, String>(1, "move"));
        abilities.add(new Pair<Integer, String>(3, "hit"));
        owner = newOwner;
        pos = newPos;
    }
    public void apply(int ind, int targetPos) {
        Creature target = Battlefield.battlefield.getTiles().get(targetPos);
        if (ind == 1) {
            if (target != null)
                return;
            int oldPos = pos;
            pos = targetPos;
            Battlefield.battlefield.push(oldPos, null, pos, this);
        }
        else {
            if (target == null || target.owner == owner)
                return;
            target.takeHit(25);
        }
    }

    @Override
    public String toString() {
        return "" + pos + " " + owner + " " + hp + " " + ap + " " + name;
    }

    public static Creature fromString(String s) {
        String[] params = s.split(" ");
        Creature result = new Creature();
        result.pos = Integer.parseInt(params[0]);
        result.owner = Integer.parseInt(params[1]);
        result.hp = Integer.parseInt(params[2]);
        result.ap = Integer.parseInt(params[3]);
        result.name = params[4];
        result.abilities.add(new Pair<Integer, String>(1, "move"));
        result.abilities.add(new Pair<Integer, String>(3, "hit"));
        return result;
    }

    public void takeHit(int dmg) {
        hp -= dmg;
        if (hp <= 0)
            Battlefield.battlefield.kill(pos);
    }
    public void replenishAP() {
        ap = 5;
    }
    public int getOwner() {
        return owner;
    }
}
