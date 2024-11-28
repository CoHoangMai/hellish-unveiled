package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class EntityReviveEvent extends Event{
	public static final Pool<EntityReviveEvent> pool = new Pool<EntityReviveEvent>() {
        @Override
        protected EntityReviveEvent newObject() {
            return new EntityReviveEvent();
        }
    };
    
	private Entity entity;
	
	public EntityReviveEvent set(Entity entity) {
		this.entity = entity;
		return this;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	@Override
	public void reset() {
		super.reset();
		entity = null;
	}
}
