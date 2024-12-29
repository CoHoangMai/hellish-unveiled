package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TerrainSpawnComponent implements Component, Poolable{
	public TiledMapTileMapObject terrainObj;
	
	public TerrainSpawnComponent() {
		terrainObj = null;
	}
	
	@Override
	public void reset() {
		terrainObj = null;
	}
}
