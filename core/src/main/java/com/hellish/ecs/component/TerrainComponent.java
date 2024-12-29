package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TerrainComponent implements Component, Poolable{
	public TiledMapTileMapObject terrainObj;
	
	public TerrainComponent() {
		terrainObj = null;
	}
	
	@Override
	public void reset() {
		terrainObj = null;
	}
}
