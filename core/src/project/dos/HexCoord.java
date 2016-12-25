package project.dos;

import com.badlogic.gdx.Gdx;

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
        difY = - down * (down * (difY / 16) + 1 + right * (difX % 2)) / 2;//rounded to +- infinity full hexes, up
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

    public HexCoord sum(HexCoord hexCoord) {
        return new HexCoord(x + hexCoord.x, y + hexCoord.y, z + hexCoord.z);
    }

    public HexCoord dif(HexCoord hexCoord) {
        return new HexCoord(x - hexCoord.x, y - hexCoord.y, z - hexCoord.z);
    }

    public void shrink() {
        int size = BattleField.fieldRadius;
        while (x > size + 1) {
            x -= 2;
            y++;
            z++;
        }
        if (x > size) {
            x--;
            y++;
        }
        while (x < - size - 1) {
            x += 2;
            y--;
            z--;
        }
        if (x < - size) {
            x++;
            y--;
        }
        while (y > size + 1) {
            y -= 2;
            x++;
            z++;
        }
        if (y > size) {
            y--;
            x++;
        }
        while (y < - size - 1) {
            y += 2;
            x--;
            z--;
        }
        if (y < - size) {
            y++;
            x--;
        }while (z > size + 1) {
            z -= 2;
            x++;
            y++;
        }
        if (z > size) {
            z--;
            y++;
        }
        while (z < - size - 1) {
            z += 2;
            x--;
            y--;
        }
        if (z < - size) {
            z++;
            y--;
        }
    }
}
