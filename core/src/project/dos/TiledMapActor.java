package project.dos;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TiledMapActor extends Actor{
    public TiledMapTileLayer.Cell cell;

    public TiledMapActor(TiledMapTileLayer.Cell tiledMapCell) {
        cell = tiledMapCell;
    }
}
