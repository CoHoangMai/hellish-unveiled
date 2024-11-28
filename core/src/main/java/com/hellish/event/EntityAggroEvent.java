package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class EntityAggroEvent extends Event{
	public static final Pool<EntityAggroEvent> pool = new Pool<EntityAggroEvent>() {
        @Override
        protected EntityAggroEvent newObject() {
            return new EntityAggroEvent();
        }
    };
    
	private Entity aiEntity;
	
	public EntityAggroEvent set(Entity aiEntity) {
		this.aiEntity = aiEntity;
		return this;
	}

	public Entity getAiEntity() {
		return aiEntity;
	}
	
	@Override
	public void reset() {
		super.reset();
		aiEntity = null;
	}
}
