package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;

public class EntityTakeDamageEvent extends Event{
	private final Entity entity;
	
	public EntityTakeDamageEvent(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
}
