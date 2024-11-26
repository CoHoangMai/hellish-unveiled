package com.hellish.event;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.scenes.scene2d.Event;

public class CollisionDespawnEvent extends Event{
	//Tạm dùng chung cho cả 2 loại cell và map object
	public final Cell cell;
	public final TiledMapTileMapObject terrainObject;
	
	public CollisionDespawnEvent(Cell cell) {
		this.cell = cell;
		this.terrainObject = null;
	}
	
	public CollisionDespawnEvent(TiledMapTileMapObject terrainObject) {
		this.terrainObject = terrainObject;
		this.cell = null;
	}
}
