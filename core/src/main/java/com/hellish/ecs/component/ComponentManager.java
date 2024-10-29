package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class ComponentManager {
	private final Array<ComponentListener<? extends Component>> listeners;
	
	public ComponentManager() {
		listeners = new Array<ComponentListener<? extends Component>>();
	}
		
	public <C extends Component> void addComponentListener(ComponentListener<C> listener) {
			listeners.add(listener);
	}
	
	@SuppressWarnings("unchecked")
	public <C extends Component> void addComponent(Entity entity, C component) {
		//entity.add(component);
		
		for(ComponentListener<? extends Component> listener : listeners) {
			if(listener instanceof ComponentListener) {
				((ComponentListener<C>)listener).onComponentAdded(entity, component);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <C extends Component> void removeComponent(Entity entity, C component) {
		//entity.remove(component.getClass());
		
		for(ComponentListener<? extends Component> listener : listeners) {
			if(listener instanceof ComponentListener) {
				((ComponentListener<C>)listener).onComponentRemoved(entity, component);
			}
		}
	}
}
