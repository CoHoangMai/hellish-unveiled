package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.hellish.map.Map;

public class MapChangeEvent extends Event{
	private final Map map;
	public MapChangeEvent(Map map) {
		this.map = map;
	}
	public Map getMap() {
		return map;
	}
}
