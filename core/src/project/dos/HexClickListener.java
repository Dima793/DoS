package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static project.dos.BattleField.battleField;

public class HexClickListener extends ClickListener{
    private HexActor actor;

    public HexClickListener(HexActor hexActor) {
        actor = hexActor;
        Gdx.app.log("Info", "(" + actor.getCoord().x + ", " + actor.getCoord().y + ", "
                + actor.getCoord().z + ")");
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        battleField.hexPressed(actor.getCoord());
    }
}
