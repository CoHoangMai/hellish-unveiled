package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;

public class EntityAggroEvent extends Event{
	private final Entity aiEntity;
	
	public EntityAggroEvent(Entity aiEntity) {
		this.aiEntity = aiEntity;
	}

	public Entity getAiEntity() {
		return aiEntity;
	}
}
