package com.hellish.event;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Event;

public class MapChangeEvent extends Event{
	private final TiledMap map;
	public MapChangeEvent(TiledMap map) {
		this.map = map;
	}
	public TiledMap getTiledMap() {
		return map;
	}
}