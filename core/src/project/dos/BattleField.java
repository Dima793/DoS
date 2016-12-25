package project.dos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;

import java.util.HashMap;

import static java.lang.Math.abs;
import static project.dos.BattlefieldLogic.battlefieldLogic;

public final class BattleField extends ApplicationAdapter implements
		ApplicationListener,
		InputProcessor {
	public static BattleField battleField = new BattleField();

	private OrthographicCamera camera;
	private TiledMapRenderer tiledMapRenderer;
	private int touchDownX;
	private int touchDownY;
	private SpriteBatch spriteBatch;
	public static HashMap<Integer, Unit> units;
	public static int currentUnit;
	public static int totalUnitNumber;
	public static int scrHeight;
	public static int scrWidth;
	public static int zeroX = 1015;
	public static int zeroY = 467;
	public static int fieldRadius = 10;
	public static HexCoord cameraHexCoord;

	private Sprite sprite;

	@Override
	public void create () {
		scrHeight = Gdx.graphics.getHeight();
		scrWidth = Gdx.graphics.getWidth();
		Gdx.app.log("Info", "Width: " + scrWidth + ", Height: " + scrHeight);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, scrWidth, scrHeight);
		camera.update();
		cameraHexCoord = new HexCoord(0, 0, 0);
		updateCamera();
		tiledMapRenderer = new HexagonalTiledMapRenderer(
				new TmxMapLoader().load("BattleField1.tmx"));
		Gdx.input.setInputProcessor(this);

		spriteBatch = new SpriteBatch();
		units = new HashMap<Integer, Unit>();//shoulb be changed to SparseArrayCompat
		Gdx.app.log("Info", "battlefieldLogic.creatures.size(): "
				+ battlefieldLogic.creatures.size());
		for (CreatureHandler creature : battlefieldLogic.creatures.values()) {
			Gdx.app.log("Info", "battlefieldLogic.creatures have one at: ("
					+ creature.creature.pos.x + ", " + creature.creature.pos.y + ")");
		}
		for (CreatureHandler creature : battlefieldLogic.creatures.values()) {
			units.put(units.size(), new Unit(creature, units.size()));
		}
		totalUnitNumber = units.size();
		currentUnit = 0;

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
			//should be in touchDragged
			moveCameraBy(HexCoord.convertVectorToHex(touchDownX - screenX, screenY - touchDownY));
			return true;
		}

		//move/attack/other ability or something by current unit
		if (!battlefieldLogic.hasTurn) {
			Gdx.app.log("Info", "NotMyTurn");
			return true;
		}
		HexCoord touchUpHex = cameraHexCoord.sum(HexCoord.convertVectorToHex(// if hex
				screenX - scrWidth / 2 + (int)camera.position.x - zeroX,
				screenY - scrHeight / 2 + (int)camera.position.y - zeroY));
		Gdx.app.log("Info", "difX: " + (screenX - scrWidth / 2)
				+ ", difY: " + (screenY - scrHeight / 2)
				+ ", difCamX: " + ((int)camera.position.x - zeroX)
				+ ", difCamY: " + ((int)camera.position.y - zeroY));
		for(Unit unit : units.values()) {
			if (touchUpHex.equals(unit.coord)) {
				Gdx.app.log("Info", "Somebody is already here");
				return true;
			}
		}
		Unit currUnit = units.get(currentUnit);
		Gdx.app.log("Info", "Trying to teleport unit №" + currentUnit
				+ " from (" + currUnit.coord.x + ", " + currUnit.coord.y + ", " + currUnit.coord.z
				+ ") to (" + touchUpHex.x + ", " + touchUpHex.y + ", " + touchUpHex.z + ")");
		if (units.get(currentUnit).tryTeleportTo(touchUpHex)) {
			currentUnitChanged();
			battlefieldLogic.passTurn();
		}
		Gdx.app.log("Info", "BD: " + battlefieldLogic.toOut);
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

	public static void currentUnitChanged() {
		if (++currentUnit == totalUnitNumber) {
			currentUnit = 0;
		}
	}

	void updateCamera() {
		moveCameraToHex(cameraHexCoord);
	}

	void moveCameraBy(HexCoord hexCoord) {
		moveCameraToHex(cameraHexCoord.sum(hexCoord));
	}

	void moveCameraToHex(HexCoord hexCoord) {//try
		hexCoord.shrink();
		camera.position.set(zeroX + hexCoord.x * 80, zeroY - (hexCoord.y - hexCoord.z) * 16, 0);
		//cameraHexCoord = HexCoord.convertVectorToHex(
		//		(int)camera.position.x - zeroX, (int)camera.position.y - zeroY);
		//Gdx.app.log("Info", "CameraMovedTo: " + cameraHexCoord.x + ", "
		//		+ cameraHexCoord.y + ", " + cameraHexCoord.z);
		//Gdx.app.log("Info", "(To: " + camera.position.x + ", " + camera.position.y + ")");
	}
}
