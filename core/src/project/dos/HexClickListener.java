package project.dos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static project.dos.BattleField.battleField;

public class HexClickListener extends ClickListener{
    private HexActor actor;

    public HexClickListener(HexActor hexActor) {
        actor = hexActor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        battleField.hexPressed(actor.getCoord());
    }
}
