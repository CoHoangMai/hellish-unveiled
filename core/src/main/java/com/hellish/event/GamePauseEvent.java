package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class GamePauseEvent extends Event{
	public static final Pool<GamePauseEvent> pool = new Pool<GamePauseEvent>() {
        @Override
        protected GamePauseEvent newObject() {
            return new GamePauseEvent();
        }
    };
}
