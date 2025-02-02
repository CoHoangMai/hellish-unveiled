package com.hellish.ecs.component;

import java.util.HashSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ecs.system.CollisionSpawnSystem.MapObjectKey;

public class TiledComponent implements Component, Poolable{
	//Tạm dùng chung cho cả cell và object
	public Cell cell;
	public MapObjectKey objectKey;
	public HashSet<Entity> nearbyEntities;
	
	public TiledComponent() {
		cell = null;
		objectKey = null;
		nearbyEntities = new HashSet<Entity>();
	}
	
	@Override
	public void reset() {
		cell = null;
		objectKey = null;
		nearbyEntities.clear();
	}
}
