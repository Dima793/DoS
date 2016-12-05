package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Unit {
    Integer id;
    String type;
    HexCoord coord;
    //int number;
    String textureFolder;
    Sprite sprite;
    CreatureHandler creatureHandler;

    Unit(CreatureHandler creature, int turnID) {
        creature.creature.turnID = turnID;
        creatureHandler = creature;
        type = creature.creature.name;
        //number = creature.number;
        textureFolder = "creatures/" + type;
        String filePath = textureFolder;
        if (creature.creature.getOwner() == 0) {
            filePath += "/Right.png";
        }
        else {
            filePath += "/Left.png";
        }
        sprite = new Sprite(new Texture(Gdx.files.internal(filePath)));
        coord = new HexCoord(creature.creature.pos.first, creature.creature.pos.second,
                - creature.creature.pos.first - creature.creature.pos.second);
        updateSprite();
    }

    public void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    public boolean tryTeleportBy(HexCoord hexCoord) {
        return tryTeleportTo(coord.sum(hexCoord));
    }

    public boolean tryTeleportTo(HexCoord hexCoord) {
        Gdx.app.log("Info", "" + new Pair<Integer, Integer>(coord.x, coord.y).hashCode() + " "
                + new Pair<Integer, Integer>(coord.x, coord.y).hashCode());
        if (creatureHandler.creature.apply(0, new Pair<Integer, Integer>(hexCoord.x, hexCoord.y))) {
            teleportTo(hexCoord);
            return true;
        }
        else {
            Gdx.app.log("Info", "Teleport aborted, too far");
            return false;
        }
    }

    public void teleportBy(HexCoord hexCoord) {
        teleportTo(coord.sum(hexCoord));
    }

    public void teleportTo(HexCoord hexCoord) {
        coord = hexCoord;
        updateSprite();
    }

    void updateSprite() {
        sprite.setPosition(BattleField.zeroX - 45 + coord.x * 80,
                BattleField.zeroY - 14 + (coord.y - coord.z) * 16);
    }

    void move(int[] path) {
        for (int next : path) {
            moveByOne(next);
        }
    }
    void moveByOne(int direction) {
        HexCoord oldCoord = coord;
        switch (direction){
            case 0://right-down, then clockwise
                coord.x += 1;
                coord.y -= 1;
                break;
            case 1:
                coord.y -= 1;
                coord.z += 1;
                break;
            case 2:
                coord.x -= 1;
                coord.z += 1;
                break;
            case 3:
                coord.x -= 1;
                coord.y += 1;
                break;
            case 4:
                coord.y += 1;
                coord.z -= 1;
                break;
            case 5:
                coord.x += 1;
                coord.z -= 1;
                break;
        }
        //animation
        creatureHandler.creature.apply(
                0, new Pair<Integer, Integer>(coord.x, coord.y));
        updateSprite();
    }
}
