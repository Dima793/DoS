package project.dos;

import com.badlogic.gdx.Gdx;

import static project.dos.BattleField.battleField;
import static project.dos.BattleField.zeroX;
import static project.dos.BattleField.zeroY;

public class HexCoord {
    public int x;
    public int y;
    public int z;

    public HexCoord(int hexX, int hexY, int hexZ) {
        x = hexX;
        y = hexY;
        z = hexZ;
    }

    @Override
    public int hashCode() {
        return 11500 * x + y;
    }

    @Override
    public boolean equals(Object a) {//should check the class of parameter
        HexCoord b = (HexCoord) a;
        return (x == b.x) && (y == b.y);
    }

    public HexCoord(int hexX, int hexY) {
        x = hexX;
        y = hexY;
        z = -x - y;
    }

    public static HexCoord convertVectorToHex(int difX, int difY) {
        Gdx.app.log("Info", "Convert: " + difX + ", " + difY);
        HexCoord hexCoord = new HexCoord(0, 0, 0);
        if (difY == 0) {
            difY++;//for controversial signum cases
        }
        int right = Integer.signum(difX);
        int down = Integer.signum(difY);
        difX = (difX + right * 40) / 80;//halfhexes, right
        difY = - down * (down * difY / 16 + 1 + right * (difX % 2)) / 2;//rounded to +- infinity full hexes, up
        if (difY == 0 && difX % 2 != 0) {
            difY = down;
        }
        int parityFix = - down * right * (difX % 2);//1 from difY if difX is odd
        hexCoord.x = difX;
        hexCoord.y = (- difX + parityFix) / 2 + (difY - parityFix);
        hexCoord.z = - hexCoord.x - hexCoord.y;
        Gdx.app.log("Info", "To: " + hexCoord.x + ", " + hexCoord.y + ", " + hexCoord.z);
        return hexCoord;
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

    public static HexCoord hexToPoint(HexCoord hexCoord) {//returns not hex but (pointX, pointY, 0)
        return new HexCoord(zeroX + 80 * hexCoord.x, zeroY + (hexCoord.y - hexCoord.z) * 16, 0);
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
        while (x + y < -10) {
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
    }
}
