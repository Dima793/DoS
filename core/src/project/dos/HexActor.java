package project.dos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HexActor extends Actor{
    private HexCoord coord;

    public HexActor(int x, int y) {
        coord = new HexCoord(x, y, -x -y);
        Vector2 coordToBound = HexCoord.hexToPoint(coord);
        setBounds(coordToBound.x - 40, coordToBound.y - 16, 80, 32);

    }

    public HexCoord getCoord() {
        return coord;
    }
}
