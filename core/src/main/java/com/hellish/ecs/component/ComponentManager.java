package com.hellish.ecs.component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
	public <C extends Component> void notifyComponentAdded(Entity entity, C component) {
		for(ComponentListener<? extends Component> listener : listeners) {
			 Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
	            if (genericInterfaces.length > 0) {
	                Type genericInterface = genericInterfaces[0];

	                if (genericInterface instanceof ParameterizedType) {
	                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
	                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
	                    if (actualTypeArguments.length > 0 && actualTypeArguments[0] == component.getClass()) {
	                        ((ComponentListener<C>) listener).onComponentAdded(entity, component);
	                    }
	                }
	            }
		}
	}
	
	@SuppressWarnings("unchecked")
	public <C extends Component> void notifyComponentRemoved(Entity entity, C component) {
		for(ComponentListener<? extends Component> listener : listeners) {
			Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
            if (genericInterfaces.length > 0) {
                Type genericInterface = genericInterfaces[0];

                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0 && actualTypeArguments[0] == component.getClass()) {
                        ((ComponentListener<C>) listener).onComponentRemoved(entity, component);
                    }
                }
            }
		}
	}
}
