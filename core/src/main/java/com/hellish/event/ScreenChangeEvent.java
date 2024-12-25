package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;

public class ScreenChangeEvent extends Event {
    private final String screenName;

    public ScreenChangeEvent(String screenName) {
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }
}
