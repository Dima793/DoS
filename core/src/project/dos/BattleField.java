package project.dos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.math.Vector3;

import static java.lang.Math.abs;
import static project.dos.BattlefieldLogic.battlefieldLogic;

public final class BattleField extends ApplicationAdapter implements
		ApplicationListener,
		InputProcessor {
	public static BattleField battleField = new BattleField();
	private Vector3 last_touch_down = new Vector3();

	private OrthographicCamera camera;
	private TiledMapRenderer tiledMapRenderer;
	private int touchDownX;
	private int touchDownY;
	private SpriteBatch spriteBatch;
	private HexStage hexStage;
	public static int currentUnit;
	public static int totalUnitNumber;
	public static int scrHeight;
	public static int scrWidth;
	public static int zeroX = 1015;
	public static int zeroY = 547;
	public static int fieldRadius = 10;
	public static HexCoord cameraHexCoord;

	//public static Sprite sprite;

	@Override
	public void create () {
		scrHeight = Gdx.graphics.getHeight();
		scrWidth = Gdx.graphics.getWidth();
		Gdx.app.log("Info", "Width: " + scrWidth + ", Height: " + scrHeight);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, scrWidth, scrHeight);
		camera.update();
		camera.position.set(zeroX, zeroY, 0);
		TiledMap map = new TmxMapLoader().load("BattleField1.tmx");
		tiledMapRenderer = new HexagonalTiledMapRenderer(map);
		Gdx.input.setInputProcessor(this);

		spriteBatch = new SpriteBatch();
		Gdx.app.log("Info", "battlefieldLogic.creatures.size(): "
				+ battlefieldLogic.creatures.size());
		for (Creature creature : battlefieldLogic.creatures.values()) {
			creature.unit.makeSprite(creature);
			Gdx.app.log("Info", "battlefieldLogic.creatures have one at: ("
					+ creature.pos.x + ", " + creature.pos.y + ")");
		}
		totalUnitNumber = battlefieldLogic.creatures.size();
		currentUnit = 0;

		//sprite = new Sprite(new Texture(Gdx.files.internal("Arrow.png")));
		//sprite.setPosition(zeroX, zeroY);

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(battleField);
		hexStage = new HexStage();
		hexStage.getViewport().setCamera(camera);
		inputMultiplexer.addProcessor(hexStage);
		Gdx.input.setInputProcessor(inputMultiplexer);
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
		for (Creature creature : battlefieldLogic.creatures.values()) {
			creature.unit.draw(spriteBatch);
		}

		hexStage.act();
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
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("Info", "TouchUp: " + screenX + ", " + screenY);
		if (abs(touchDownX - screenX) + abs(screenY - touchDownY) > 10) {
			//should be in touchDragged
			camera.position.set((int) camera.position.x + touchDownX - screenX,
					(int) camera.position.y - touchDownY + screenY, 0);
			hexStage.getViewport().setCamera(camera);
			return true;
		}

		//move/attack/other ability or something by current unit
		if (!battlefieldLogic.hasTurn) {
			Gdx.app.log("Info", "NotMyTurn");
			return true;
		}
		return false;
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
		if (++currentUnit == battlefieldLogic.freeID) {
			currentUnit = 0;
		}
		while(battlefieldLogic.creatures.get(currentUnit) == null) {
			if (++currentUnit == battlefieldLogic.freeID) {
				currentUnit = 0;
			}
		}
	}

	public void hexPressed(HexCoord hexCoord) {
		Gdx.app.log("Info", "(" + hexCoord.x + ", " + hexCoord.y + ", " + hexCoord.z + ") pressed");
		if (battlefieldLogic.creatures.get(currentUnit).apply1(hexCoord)) {
			battlefieldLogic.passTurn();
		}
		Gdx.app.log("Info", "BD: " + battlefieldLogic.toOut);
		return;
	}
}
