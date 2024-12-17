package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class LoseEvent extends Event{
	public static final Pool<LoseEvent> pool = new Pool<LoseEvent>() {
        @Override
        protected LoseEvent newObject() {
            return new LoseEvent();
        }
    };
}
