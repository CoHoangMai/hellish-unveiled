package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;

public class EntityAddItemEvent extends Event{
	private final Entity entity;
	private final Entity item;
	
	public EntityAddItemEvent(Entity entity, Entity item) {
		this.entity = entity;
		this.item = item;
	}

	public Entity getEntity() {
		return entity;
	}
	
	public Entity getItem() {
		return item;
	}
}
