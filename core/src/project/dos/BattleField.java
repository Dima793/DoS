package project.dos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;

import java.util.HashMap;

import static java.lang.Math.abs;

public class BattleField extends ApplicationAdapter implements ApplicationListener, InputProcessor {
	private OrthographicCamera camera;
	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;
	private int touchDownX;
	private int touchDownY;
	private SpriteBatch spriteBatch;
	private HashMap<Integer, Unit> units;
	private int currentUnit;//id
	public static int scrHeight;
	public static int scrWidth;
	public static int zeroX = 1015;
	public static int zeroY = 435;
	public static HexCoord cameraHexCoord;

	//private Sprite sprite;

	@Override
	public void create () {
		scrHeight = Gdx.graphics.getHeight();
		scrWidth = Gdx.graphics.getWidth();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, scrWidth, scrHeight);
		camera.update();
		cameraHexCoord = new HexCoord(0, 0, 0);
		updateCamera();
		tiledMap = new TmxMapLoader().load("BattleField1.tmx");
		tiledMapRenderer = new HexagonalTiledMapRenderer(tiledMap);
		Gdx.input.setInputProcessor(this);

		spriteBatch = new SpriteBatch();
		units = new HashMap<Integer, Unit>();
		units.put(1, new Unit("DarkKnight", new HexCoord(0,1,-1), 1, true));
		units.put(2, new Unit("DarkKnight", new HexCoord(2,1,-3), 1, false));
		currentUnit = 1;

		//sprite = new Sprite(new Texture(Gdx.files.internal("Arrow.png")));
		//sprite.setPosition(zeroX, zeroY);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		spriteBatch.setProjectionMatrix(camera.combined);

		spriteBatch.begin();
		for (Unit unit : units.values()) {
			unit.draw(spriteBatch);
		}
		//sprite.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("Info", "TouchDown: " + screenX + ", " + screenY);
		Gdx.app.log("Info", "(To: " + (camera.position.x + screenX) + ", "
				+ (camera.position.y + screenY) + ")");
		touchDownX = screenX;
		touchDownY = screenY;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("Info", "TouchUp: " + screenX + ", " + screenY);
		if (abs(touchDownX - screenX) + abs(screenY - touchDownY) > 10) {
			moveCameraBy(HexCoord.convertVectorToHex(touchDownX - screenX, screenY - touchDownY));
			return true;
		}

		//move/attack/other ability or something by current unit
		HexCoord touchUpHex = cameraHexCoord.sum(HexCoord.convertVectorToHex(// if hex
				screenX - scrWidth / 2 + (int)camera.position.x - zeroX,
				screenY - scrHeight / 2 + (int)camera.position.y - zeroY));
		Gdx.app.log("Info", "difX: " + (screenX - scrWidth / 2)
				+ ", difY: " + (screenY - scrHeight / 2)
				+ ", difCamX: " + ((int)camera.position.x - zeroX)
				+ ", difCamY: " + ((int)camera.position.y - zeroY));
		if (touchUpHex.equals(units.get(3 - currentUnit).coord)) {
			return true;
		}
		units.get(currentUnit).teleportTo(touchUpHex);
		currentUnit = 3 - currentUnit;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		//too sensitive
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	void updateCamera() {
		moveCameraToHex(cameraHexCoord);
	};

	void moveCameraBy(HexCoord hexCoord) {
		moveCameraToHex(cameraHexCoord.sum(hexCoord));
	}

	void moveCameraToHex(HexCoord hexCoord) {//try
		camera.position.set(zeroX + hexCoord.x * 80, zeroY - (hexCoord.y - hexCoord.z) * 16, 0);
		cameraHexCoord = HexCoord.convertVectorToHex(
				(int)camera.position.x - zeroX, (int)camera.position.y - zeroY);
		Gdx.app.log("Info", "CameraMovedTo: " + cameraHexCoord.x + ", "
				+ cameraHexCoord.y + ", " + cameraHexCoord.z);
		Gdx.app.log("Info", "(To: " + camera.position.x + ", " + camera.position.y + ")");
	}
}
