package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.hellish.ecs.component.MoveComponent.Direction;

public class EntityDirectionChangedEvent extends Event{
	private final Entity entity;
	private final Direction direction;
	
	public EntityDirectionChangedEvent(Entity entity, Direction direction) {
		this.entity = entity;
		this.direction = direction;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public Direction getDirection() {
		return direction;
	}
}
