package com.hellish.map;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class Map {
	public static final String TAG = Map.class.getSimpleName();
	
	private final TiledMap tiledMap;
	
	public Map(final TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}
}
