package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static project.dos.BattleField.battleField;

public class Unit {
    Integer id;
    String type;
    Vector2 point;
    //int number;
    String textureFolder;
    Sprite sprite;

    Unit(Creature creature, boolean drawNow) {
        id = creature.iD;
        type = creature.name;
        point = HexCoord.hexToPoint(creature.pos);
        //number = creature.number;
        textureFolder = "creatures/" + type;
        if (drawNow) {
            makeSprite(creature);
        }
    }

    public void makeSprite(Creature creature) {
        String filePath = textureFolder;
        if (creature.getOwner() == 0) {
            filePath += "/Right.png";
        }
        else {
            filePath += "/Left.png";
        }
        sprite = new Sprite(new Texture(Gdx.files.internal(filePath)));
        updateSprite(creature.pos);
    }

    public void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    public void teleportTo(HexCoord hexCoord) {
        updateSprite(hexCoord);
    }

    void updateSprite(HexCoord newCoord) {
        point = HexCoord.hexToPoint(newCoord);
        sprite.setPosition(point.x - 45, point.y - 14);
    }
    /*
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
                0, coord);
        updateSprite();
    }
    */
}
