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
import com.badlogic.gdx.math.Vector2;
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
	private int touchDraggedX;
	private int touchDraggedY;
	private SpriteBatch spriteBatch;
	private HexStage hexStage;
	public int currentUnit;
	public int totalUnitNumber;
	public int scrHeight;
	public int scrWidth;
	public int zeroX = 1015;
	public int zeroY = 547;

	//public static Sprite sprite;

	@Override
	public void create () {
		scrHeight = Gdx.graphics.getHeight();
		scrWidth = Gdx.graphics.getWidth();
		Gdx.app.log("Info", "Width: " + scrWidth + ", Height: " + scrHeight);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, scrWidth, scrHeight);
		camera.update();
		TiledMap map = new TmxMapLoader().load("BattleField1.tmx");
		tiledMapRenderer = new HexagonalTiledMapRenderer(map);

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(battleField);
		hexStage = new HexStage();
		moveCameraTo(zeroX, zeroY);
		inputMultiplexer.addProcessor(hexStage);
		Gdx.input.setInputProcessor(inputMultiplexer);

		spriteBatch = new SpriteBatch();
		for (Creature creature : battlefieldLogic.creatures.values()) {
			creature.unit.makeSprite(creature);
			if (creature.owner == battlefieldLogic.owner) {
				moveCameraToHex(creature.pos);
			}
		}
		totalUnitNumber = battlefieldLogic.creatures.size();
		currentUnit = 0;

		Gdx.app.log("Info", "battlefieldLogic.owner == " + battlefieldLogic.owner);

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
		//Gdx.app.log("Info", "TouchDown: " + screenX + ", " + screenY);
		//Gdx.app.log("Info", "(To: " + (camera.position.x + screenX) + ", "
		//		+ (camera.position.y + screenY) + ")");
		touchDownX = screenX;
		touchDownY = screenY;
		touchDraggedX = touchDownX;
		touchDraggedY = touchDownY;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//Gdx.app.log("Info", "TouchUp: " + screenX + ", " + screenY);
		if (abs(touchDownX - screenX) + abs(screenY - touchDownY) > 10) {
			return true;//it was scroll, processed in touchDragged
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
		if (abs(touchDraggedX - screenX) + abs(screenY - touchDraggedY) > 10) {
			moveCameraTo((int) camera.position.x + touchDraggedX - screenX,
					(int) camera.position.y - touchDraggedY + screenY);
			touchDraggedX = screenX;
			touchDraggedY = screenY;
			return true;
		}
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

	public void currentUnitChanged() {
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
		battlefieldLogic.creatures.get(currentUnit).apply1(hexCoord);
		//Gdx.app.log("Info", "BD: " + battlefieldLogic.toOut);
		return;
	}

	private void moveCameraTo(float newCamX, float newCamY) {
		if (newCamX < scrWidth / 2) {
			newCamX = scrWidth / 2;
		}
		if (newCamX > 2000 - scrWidth / 2) {
			newCamX = 2000 - scrWidth / 2;
		}
		if (newCamY < scrHeight / 2) {
			newCamY = scrHeight / 2;
		}
		if (newCamY > 1000 - scrHeight / 2) {
			newCamY = 1000 - scrHeight / 2;
		}
		camera.position.set(newCamX, newCamY, 0);
		hexStage.getViewport().setCamera(camera);
	}

	private void moveCameraToHex(HexCoord hexCoord) {
		Vector2 newCameraPosition = HexCoord.hexToPoint(hexCoord);
		moveCameraTo(newCameraPosition.x, newCameraPosition.y);
	}
}
