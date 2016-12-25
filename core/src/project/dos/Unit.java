package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Unit {
    Integer id;
    HexCoord coord;
    String type;
    //int number;
    String textureFolder;
    Sprite sprite;

    Unit(Creature creature) {
        id = creature.iD;
        type = creature.name;
        //number = creature.number;
        textureFolder = "creatures/" + type;
        String filePath = textureFolder;
        if (creature.getOwner() == 0) {
            filePath += "/Right.png";
        }
        else {
            filePath += "/Left.png";
        }
        sprite = new Sprite(new Texture(Gdx.files.internal(filePath)));
        coord = creature.pos;
        updateSprite();
    }

    public void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
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
