package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;

public class GameResumeEvent extends Event{
	public static final Pool<GameResumeEvent> pool = new Pool<GameResumeEvent>() {
        @Override
        protected GameResumeEvent newObject() {
            return new GameResumeEvent();
        }
    };
}
