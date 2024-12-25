package com.hellish.screen;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.system.AudioSystem;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.view.SettingView;

public class SettingScreen extends AbstractScreen<SettingView>{

    private final ECSEngine ecsEngine;
    private AudioSystem audioSystem;
    
        public SettingScreen(final Main context) {
            super(context);
    
            ecsEngine = context.getECSEngine();
            for(EntitySystem system : ecsEngine.getSystems()) {
                if(!(system instanceof RenderSystem)) {
                    system.setProcessing(false);
                }
                if(!(system instanceof AudioSystem)) {
                    system.setProcessing(true);
                }
            }
            
            audioSystem = ecsEngine.getSystem(AudioSystem.class);
            if (audioSystem == null) {
                throw new IllegalArgumentException("AudioSystem cannot be null");
            }
        }

    @Override
    protected Array<SettingView> getScreenViews(final Main context) {
        Array<SettingView> views = new Array<>();
        SettingView settingView = new SettingView(Scene2DSkin.defaultSkin, context);
        views.add(settingView);
        return views;
    }

    @Override
    public void render(float delta) {
        // if(!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())) {
		// 	isMusicLoaded = true;
		// 	audioManager.playAudio(AudioType.INTRO);
		// }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
        
    }
    
}
