package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public interface ComponentListener<C extends Component> {
	void onComponentAdded(Entity entity, C component);
	void onComponentRemoved(Entity entity, C component);
}
