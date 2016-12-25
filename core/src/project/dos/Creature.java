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
    ArrayList<Ability> abilities = new ArrayList<Ability>();

    public Creature() {
    }

    //for the demo version there'll be just one type of creatures
    public Creature(int newOwner, HexCoord newPos, int id, boolean initial) {
        hp = 100;
        ap = 5;
        name = "DarkKnight";
        abilities.add(new Ability(1, "move"));
        abilities.add(new Ability(3, "hit"));
        owner = newOwner;
        iD = id;
        pos = newPos;
        unit = new Unit(this, !initial);
    }

    /*
        public boolean apply(int ind, HexCoord targetPos) {
            if (ind == 0) {
                int pointsSpent = battlefieldLogic.getDist(pos, targetPos);
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
                if (pointsSpent > ap || battlefieldLogic.getDist(pos, targetPos) > 1)
                    return false;
                ap -= pointsSpent;
                Creature target = battlefieldLogic.creatures.get(targetPos);
                if (target.owner != owner)
                    target.takeHit(25);
                battlefieldLogic.push(2, this);
                return true;
            }
        }
    */
    public boolean apply1(HexCoord targetPos) {
        for (Creature creature : battlefieldLogic.creatures.values())
            if (creature.pos.equals(targetPos)) {
                if (creature.owner == owner) {
                    if (creature.iD == iD) {
                        battlefieldLogic.passTurn();
                    }
                    return false;
                }
                int pointsSpent = abilities.get(1).number;
                if (pointsSpent > ap || battlefieldLogic.getDist(pos, targetPos) > 1) {
                    return false;
                }
                if (creature.owner != owner) {
                    ap -= pointsSpent;
                    creature.takeHit(25);
                }
                battlefieldLogic.push(2, this);
                return true;
            }
        int pointsSpent = battlefieldLogic.getDist(pos, targetPos);
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
        result.abilities.add(new Ability(1, "move"));
        result.abilities.add(new Ability(3, "hit"));
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