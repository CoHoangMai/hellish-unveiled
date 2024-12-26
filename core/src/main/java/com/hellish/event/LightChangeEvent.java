package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class LightChangeEvent extends Event{
	public static final Pool<LightChangeEvent> pool = new Pool<LightChangeEvent>() {
        @Override
        protected LightChangeEvent newObject() {
            return new LightChangeEvent();
        }
    };
}
