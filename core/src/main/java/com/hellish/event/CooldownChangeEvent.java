package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class CooldownChangeEvent extends Event{
	public static final Pool<CooldownChangeEvent> pool = new Pool<CooldownChangeEvent>() {
        @Override
        protected CooldownChangeEvent newObject() {
            return new CooldownChangeEvent();
        }
    };
    
	private Entity entity;
	
	public CooldownChangeEvent set(Entity entity) {
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
