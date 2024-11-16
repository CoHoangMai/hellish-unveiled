package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Stage;

public interface ComponentListener<C extends Component> {
	void onComponentAdded(Entity entity, C component, Stage stage);
	void onComponentRemoved(Entity entity, C component);
}
