package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class EntityAddItemEvent extends Event{
	public static final Pool<EntityAddItemEvent> pool = new Pool<EntityAddItemEvent>() {
        @Override
        protected EntityAddItemEvent newObject() {
            return new EntityAddItemEvent();
        }
    };
    
	private Entity entity;
	private Entity item;
	
	public EntityAddItemEvent set(Entity entity, Entity item) {
        this.entity = entity;
        this.item = item;
        return this;
    }

	public Entity getEntity() {
		return entity;
	}
	
	public Entity getItem() {
		return item;
	}
	
	@Override
	public void reset() {
		super.reset();
		entity = null;
		item = null;
	}
}
