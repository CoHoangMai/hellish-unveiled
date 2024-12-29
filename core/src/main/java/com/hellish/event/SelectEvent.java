package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class SelectEvent extends Event{
	public static final Pool<SelectEvent> pool = new Pool<SelectEvent>() {
        @Override
        protected SelectEvent newObject() {
            return new SelectEvent();
        }
    };
}
