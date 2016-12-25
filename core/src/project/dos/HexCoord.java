package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

import static project.dos.BattleField.battleField;

public class HexCoord {
    public int x;
    public int y;
    public int z;

    public HexCoord(int hexX, int hexY, int hexZ) {
        x = hexX;
        y = hexY;
        z = hexZ;
    }

    public HexCoord(int hexX, int hexY) {
        this(hexX, hexY, - hexX - hexY);
    }

    @Override
    public int hashCode() {
        return 11500 * x + y;
    }

    @Override
    public boolean equals(Object a) {//maybe should check the class of parameter
        return (x == ((HexCoord) a).x) && (y == ((HexCoord) a).y);
    }

    public static HexCoord pointToHex(int pointX, int pointY) {
        pointX -= battleField.zeroX;
        pointY -= battleField.zeroY;
        HexCoord hexCoord = new HexCoord(0, 0, 0);
        if (pointY == 0) {
            pointY++;//for controversial signum cases
        }
        int right = Integer.signum(pointX);
        int up = Integer.signum(pointY);
        pointX = (pointX + right * 40) / 80;//halfhexes, right
        pointY = up * (up * pointY / 16 + 1 + right * (pointX % 2)) / 2;//rounded to +- infinity full hexes, up
        int parityFix = ((pointX % 2) + up) / 2;//1 from difY if difX is odd
        hexCoord.x = pointX;
        hexCoord.y = - pointX / 2 + pointY - parityFix;
        hexCoord.z = - hexCoord.x - hexCoord.y;
        return hexCoord;
    }

    public static Vector2 hexToPoint(HexCoord hexCoord) {
        return new Vector2(battleField.zeroX + 80 * hexCoord.x,
                battleField.zeroY + (hexCoord.y - hexCoord.z) * 16);
    }

    public HexCoord sum(HexCoord hexCoord) {
        return new HexCoord(x + hexCoord.x, y + hexCoord.y, z + hexCoord.z);
    }

    public HexCoord dif(HexCoord hexCoord) {
        return new HexCoord(x - hexCoord.x, y - hexCoord.y, z - hexCoord.z);
    }

    public void shrink() {
        if (x + y > 10) {
            int fix = (x + y - 10 + 1) / 2;
            x -= fix;
            y -= fix;
        }
        if (x > 10) {
            y += x - 10;
            x = 10;
        }
        if (y > 10) {
            x += y - 10;
            y = 10;
        }
        if (x + y < -10) {
            int fix = (x + y + 10 - 1) / 2;
            x += fix;
            y += fix;
        }
        if (x < -10) {
            y += x + 10;
            x = -10;
        }
        if (y < -10) {
            x += y + 10;
            y = -10;
        }
        z = - x - y;
    }
}
