package com.hellish.event;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.scenes.scene2d.Event;

public class CollisionDespawnEvent extends Event{
	public final Cell cell;
	
	public CollisionDespawnEvent(Cell cell) {
		this.cell = cell;
	}
}
