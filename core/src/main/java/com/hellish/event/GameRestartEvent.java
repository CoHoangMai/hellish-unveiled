package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class GameRestartEvent extends Event{
	public static final Pool<GameRestartEvent> pool = new Pool<GameRestartEvent>() {
        @Override
        protected GameRestartEvent newObject() {
            return new GameRestartEvent();
        }
    };
}
