package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static project.dos.BattlefieldLogic.battlefieldLogic;

public class Unit {
    Integer id;
    String type;
    HexCoord coord;
    //int number;
    String textureFolder;
    Sprite sprite;
    CreatureHandler creatureHandler;

    Unit(CreatureHandler creature) {
        //id = creatireID;
        creatureHandler = creature;
        type = creature.get().name;
        //number = creature.number;
        textureFolder = "creatures/" + type;
        String filePath = textureFolder;
        if (creature.get().getOwner() == 0) {
            filePath += "/Right.png";
        }
        else {
            filePath += "/Left.png";
        }
        sprite = new Sprite(new Texture(Gdx.files.internal(filePath)));
        coord = new HexCoord(creature.get().pos.first, creature.get().pos.second,
                - creature.get().pos.first - creature.get().pos.second);
        updateSprite();
    }

    void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    void teleportBy(HexCoord hexCoord) {
        teleportTo(coord.sum(hexCoord));
    }

    void teleportTo(HexCoord hexCoord) {
        Gdx.app.log("Info", "" + new Pair<Integer, Integer>(coord.x, coord.y).hashCode() + " "
                + new Pair<Integer, Integer>(coord.x, coord.y).hashCode());
        if (creatureHandler.get().apply(0, new Pair<Integer, Integer>(hexCoord.x, hexCoord.y))) {
            coord = hexCoord;
            updateSprite();
        }
        else {
            Gdx.app.log("Info", "Teleport aborted, too far");
        }
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
        creatureHandler.get().apply(
                0, new Pair<Integer, Integer>(coord.x, coord.y));
        updateSprite();
    }
}
