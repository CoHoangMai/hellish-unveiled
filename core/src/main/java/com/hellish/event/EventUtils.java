package com.hellish.event;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool;

public class EventUtils {
	public static <T extends Event> void fireEvent(Stage stage, Pool<T> pool, EventConfigurator<T> configurator) {
        T event = pool.obtain();
        configurator.configure(event);  
        stage.getRoot().fire(event);    
        pool.free(event);               
    }

    public interface EventConfigurator<T extends Event> {
        void configure(T event);
    }
}
