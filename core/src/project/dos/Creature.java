package project.dos;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

import static project.dos.BattlefieldLogic.battlefieldLogic;

/**
 * Created by ASUS on 17.11.2016.
 */

public class Creature {
    public int hp, ap, owner, iD;
    HexCoord pos;
    public String name;
    Unit unit;
    ArrayList<Pair<Integer, String>> abilities = new ArrayList<Pair<Integer, String>>();

    public Creature() {}

    public Creature(int newOwner, HexCoord newPos, int id, boolean initial) { //for the demo version there'll be just one type of creatures
        hp = 100;
        ap = 5;
        name = "DarkKnight";
        abilities.add(new Pair<Integer, String>(1, "move"));
        abilities.add(new Pair<Integer, String>(3, "hit"));
        owner = newOwner;
        iD = id;
        pos = newPos;
        unit = new Unit(this, !initial);
    }

    public boolean apply(int ind, HexCoord targetPos) {
        if (ind == 0) {
            int pointsSpent = battlefieldLogic.get_dist(pos, targetPos);
            if (pointsSpent > ap) {
                Gdx.app.log("Info", "Teleport aborted, too far");
                return false;
            }
            pos = targetPos;
            ap -= pointsSpent;
            unit.updateSprite(targetPos);
            battlefieldLogic.push(2, this);
            return true;
        }
        else {
            int pointsSpent = abilities.get(ind).first;
            if (pointsSpent > ap || battlefieldLogic.get_dist(pos, targetPos) > 1)
                return false;
            ap -= pointsSpent;
            Creature target = battlefieldLogic.creatures.get(targetPos);
            if (target.owner != owner)
                target.takeHit(25);
            battlefieldLogic.push(2, this);
            return true;
        }
    }

    public String toString() {
        return "" + pos.x + " " + pos.y + " " + owner + " " + hp + " " + ap + " " + name + " " + iD;
    }

    public Creature fromString(String s) {
        String[] params = s.split(" ");
        Creature result = new Creature();
        result.pos = new HexCoord(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
        result.owner = Integer.parseInt(params[2]);
        result.hp = Integer.parseInt(params[3]);
        result.ap = Integer.parseInt(params[4]);
        result.name = params[5];
        result.iD = Integer.parseInt(params[6]);
        result.abilities.add(new Pair<Integer, String>(1, "move"));
        result.abilities.add(new Pair<Integer, String>(3, "hit"));
        return result;
    }

    public void takeHit(int dmg) {
        hp -= dmg;
        if (hp <= 0)
            battlefieldLogic.kill(this);
        else {
            battlefieldLogic.push(2, this);
        }
    }

    public void replenishAP() {
        ap = 5;
    }

    public int getOwner() {
        return owner;
    }
}