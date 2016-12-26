package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;

import static project.dos.BattleField.battleField;

public class Unit {
    enum State {STAND, MOVE}
    enum Orientation {RIGHT, LEFT}

    private Integer id;
    //private int number;
    private String type;
    private Vector2 position;
    Vector2 initialPosition;
    Vector2 deltaPosition;
    private String textureFolder;
    private Sprite spriteLeft;
    private Sprite spriteRight;
    private Animation moveLeft;
    private Animation moveRight;
    private State state;
    private Orientation orientation;
    private float animationTime;


    Unit(Creature creature, boolean drawNow) {
        id = creature.iD;
        //number = creature.number;
        type = creature.name;
        position = getHexCorner(HexCoord.hexToPoint(creature.pos));
        state = State.STAND;
        if (creature.pos.x < 0) {
            orientation = Orientation.LEFT;
        }
        else {
            orientation = Orientation.RIGHT;
        }
        textureFolder = "creatures/" + type + creature.getOwner() + "/";
        if (drawNow) {
            updateSprite(creature.pos);
        }
    }

    public void makeSprite(HexCoord coord) {
        spriteLeft = new Sprite(new Texture(
                Gdx.files.internal(textureFolder + "STANDLEFT.png")));
        spriteRight = new Sprite(new Texture(
                Gdx.files.internal(textureFolder + "STANDRIGHT.png")));
        TextureRegion[][] tmp = TextureRegion.split(new Texture(
                Gdx.files.internal(textureFolder + "MOVELEFT.png")), 96, 64);
        TextureRegion[] walkFrames = new TextureRegion[8];
        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        moveLeft = new Animation(0.05f, walkFrames);
        tmp = TextureRegion.split(new Texture(
                Gdx.files.internal(textureFolder + "MOVERIGHT" + ".png")), 96, 64);
        walkFrames = new TextureRegion[8];
        index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        moveRight = new Animation(0.05f, walkFrames);
        updateSprite(coord);
    }

    public void draw(SpriteBatch spriteBatch, float time) {
        if (state == State.STAND) {
            getSprite().draw(spriteBatch);
            return;
        }
        setAnimationPosition(time - animationTime);
        spriteBatch.draw(getAnimation().getKeyFrame(time, true), position.x, position.y);
    }

    public void teleportTo(HexCoord hexCoord) {
        updateSprite(hexCoord);
    }

    public void moveTo(HexCoord hexCoord) {
        initialPosition = new Vector2(position);
        deltaPosition = getHexCorner(HexCoord.hexToPoint(hexCoord));
        deltaPosition.x -= position.x;
        deltaPosition.y -= position.y;
        animationTime = battleField.stateTime;
        if (deltaPosition.x < 0) {
            orientation = Orientation.LEFT;
        }
        else {
            orientation = Orientation.RIGHT;
        }
        state = State.MOVE;
    }

    public void updateSprite(HexCoord newCoord) {
        position = getHexCorner(HexCoord.hexToPoint(newCoord));
        getSprite().setPosition(position.x, position.y);
    }

    private void setAnimationPosition(float delta) {
        if (delta > 1f) {
            position = new Vector2(
                    initialPosition.x + deltaPosition.x, initialPosition.y + deltaPosition.y);
            state = State.STAND;
            getSprite().setPosition(position.x, position.y);
            return;
        }
        position.x = initialPosition.x + (int) (delta * deltaPosition.x);
        position.y = initialPosition.y + (int) (delta * deltaPosition.y);
    }

    private Animation getAnimation() {
        if (orientation == Orientation.LEFT) {
            return moveLeft;
        }
        return moveRight;
    }

    private Sprite getSprite() {
        if (orientation == Orientation.LEFT) {
            return spriteLeft;
        }
        return spriteRight;
    }

    private Vector2 getHexCorner(Vector2 point) {
        return new Vector2(point.x - 45, point.y - 14);
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
