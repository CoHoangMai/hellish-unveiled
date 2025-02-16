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
	private Float duration;
	
	public CooldownChangeEvent set(Entity entity, Float duration) {
		this.entity = entity;
		this.duration = duration;
		return this;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public Float getDuration() {
		return duration;
	}
	
	@Override
	public void reset() {
		super.reset();
		entity = null;
	}
}
