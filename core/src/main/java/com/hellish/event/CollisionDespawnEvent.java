package com.hellish.event;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;
import com.hellish.ecs.system.CollisionSpawnSystem.MapObjectKey;

public class CollisionDespawnEvent extends Event{
	public static final Pool<CollisionDespawnEvent> pool = new Pool<CollisionDespawnEvent>() {
        @Override
        protected CollisionDespawnEvent newObject() {
            return new CollisionDespawnEvent();
        }
    };
	
    //Tạm dùng chung cho cả 2 loại cell và map object
	public Cell cell;
	public MapObjectKey mapObjectKey;
	
	@Override
	public void reset() {
		super.reset();
		cell = null;
		mapObjectKey = null;
	}
}
