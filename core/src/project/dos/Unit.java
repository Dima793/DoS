package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Unit {
    //Integer id;
    String type;
    HexCoord coord;
    int number;
    String textureFolder;
    Sprite sprite;

    Unit(String unitType, HexCoord hexCoord, int unitNumber, boolean isRight) {
        //id = unitId;
        type = unitType;
        textureFolder = "creatures/" + type;
        String filePath = textureFolder;
        if (isRight) {
            filePath += "/Right.png";
        }
        else {
            filePath += "/Left.png";
        }
        sprite = new Sprite(new Texture(Gdx.files.internal(filePath)));
        teleportTo(hexCoord);
    }

    void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    void teleportBy(HexCoord hexCoord) {
        teleportTo(coord.sum(hexCoord));
    }

    void teleportTo(HexCoord hexCoord) {
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
        switch (direction){
            case 0: coord.x += 1;
                    break;
            case 1: coord.y += 1;
                    break;
            case 2: coord.z += 1;
                    break;
            case 3: coord.x -= 1;
                    break;
            case 4: coord.y -= 1;
                    break;
            case 5: coord.z -= 1;
                    break;
        }
        //animation
        updateSprite();
    }
}
