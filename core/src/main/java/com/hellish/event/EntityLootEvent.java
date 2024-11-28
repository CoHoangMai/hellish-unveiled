package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class EntityLootEvent extends Event{
	public static final Pool<EntityLootEvent> pool = new Pool<EntityLootEvent>() {
        @Override
        protected EntityLootEvent newObject() {
            return new EntityLootEvent();
        }
    };
}
