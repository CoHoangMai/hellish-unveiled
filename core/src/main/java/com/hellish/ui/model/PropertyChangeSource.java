package com.hellish.ui.model;

import java.util.HashMap;
import java.util.function.Consumer;

import com.badlogic.gdx.utils.Array;

abstract class PropertyChangeSource {
	private final HashMap<String, Array<Consumer<Object>>> listenersMap = new HashMap<>();
	
	public void onPropertyChange(String property, Consumer<Object> action) {
		Array<Consumer<Object>> actions = listenersMap.computeIfAbsent(property, key -> new Array<>());
		actions.add(action);
	}
	
	public void notify(String property, Object value) {
		Array<Consumer<Object>> actions = listenersMap.get(property);
		if(actions != null) {
			for(Consumer<Object> action : actions) {
				action.accept(value);
			}
		}
	}
}
