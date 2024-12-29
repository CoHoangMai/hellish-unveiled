package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;
import com.hellish.screen.ScreenType;

public class ScreenChangeEvent extends Event {
    
    public static final Pool<ScreenChangeEvent> pool = new Pool<ScreenChangeEvent>() {
        @Override
        protected ScreenChangeEvent newObject() {
            return new ScreenChangeEvent();
        }
    };
    
	private ScreenType screenType;
	
	public ScreenChangeEvent set(ScreenType screenType) {
		this.screenType = screenType;
		return this;
	}
	
	public ScreenType getScreenType() {
		return screenType;
	}
	
	@Override
	public void reset() {
		super.reset();
		screenType = null;
	}
}
