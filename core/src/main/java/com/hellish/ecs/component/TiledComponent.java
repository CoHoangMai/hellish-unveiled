package com.hellish.ecs.component;

import java.util.HashSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TiledComponent implements Component, Poolable{
	public Cell cell;
	public HashSet<Entity> nearbyEntities = new HashSet<>();
	
	@Override
	public void reset() {
		
	}
}
