package project.dos;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class HexStage extends Stage{

    public HexStage() {
        for (int x = -10; x < 11; x++) {
            for (int y = max(-10, x - 10); y < min(10, x + 10); y++) {
                HexActor actor = new HexActor(x, y);
                addActor(actor);
                InputListener eventListener = new HexClickListener(actor);
                actor.addListener(eventListener);
            }
        }
    }
}
