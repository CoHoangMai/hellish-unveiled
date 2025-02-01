package com.hellish.ui.model;

import java.util.HashMap;
import java.util.function.Consumer;

import com.badlogic.gdx.utils.Array;

public abstract class PropertyChangeSource {
	private final HashMap<String, Array<Consumer<Object>>> listenersMap = new HashMap<>();
	private final HashMap<String, Array<MultiConsumer<Object>>> mulListenersMap = new HashMap<>();
	
	public void onPropertyChange(String property, Consumer<Object> action) {
		Array<Consumer<Object>> actions = listenersMap.computeIfAbsent(property, key -> new Array<>());
		actions.add(action);
	}
	
	public void onPropertyChange(String property, MultiConsumer<Object> action) {
		Array<MultiConsumer<Object>> actions = mulListenersMap.computeIfAbsent(property, key -> new Array<>());
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
	
	public void notify(String property, Object... values) {
		Array<MultiConsumer<Object>> actions = mulListenersMap.get(property);
		if(actions != null) {
			for(MultiConsumer<Object> action : actions) {
				action.accept(values);
			}
		}
	}
	
	@FunctionalInterface
	public interface MultiConsumer<T>{
		@SuppressWarnings("unchecked")
		void accept(T... values);
	}
}
