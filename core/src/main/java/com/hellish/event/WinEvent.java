package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class WinEvent extends Event{
	public static final Pool<WinEvent> pool = new Pool<WinEvent>() {
        @Override
        protected WinEvent newObject() {
            return new WinEvent();
        }
    };
}
