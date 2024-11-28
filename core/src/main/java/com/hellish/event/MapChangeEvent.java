package com.hellish.event;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class MapChangeEvent extends Event{
	public static final Pool<MapChangeEvent> pool = new Pool<MapChangeEvent>() {
        @Override
        protected MapChangeEvent newObject() {
            return new MapChangeEvent();
        }
    };
    
	private TiledMap map;
	
	public MapChangeEvent set(TiledMap map) {
		this.map = map;
		return this;
	}
	
	public TiledMap getTiledMap() {
		return map;
	}
	
	@Override
	public void reset() {
		super.reset();
		map = null;
	}
}