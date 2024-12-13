package com.hellish.screen;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.view.MainMenuView;

public class MainMenuScreen extends AbstractScreen<MainMenuView> {
	private final ECSEngine ecsEngine;
    private final AssetManager assetManager;
    private boolean isMusicLoaded;

    public MainMenuScreen(final Main context) {
        super(context);
        
        ecsEngine = context.getECSEngine();
		for(EntitySystem system : ecsEngine.getSystems()) {
			if(!(system instanceof RenderSystem)) {
				system.setProcessing(false);
			}
		}
        
        assetManager = context.getAssetManager();
        isMusicLoaded = false;
		for (final AudioType audioType : AudioType.values()) {
		    if (audioType.isMusic()) {
		        assetManager.load(audioType.getFilePath(), Music.class);
		    } else {
		        assetManager.load(audioType.getFilePath(), Sound.class);
		    }
		}
    }

    @Override
    protected Array<MainMenuView> getScreenViews(final Main context) {
        Array<MainMenuView> views = new Array<>();
        MainMenuView mainMenuView = new MainMenuView(Scene2DSkin.defaultSkin, context);
        views.add(mainMenuView);
        return views;
    }

    @Override
    public void render(float delta) {
        if(!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())) {
			isMusicLoaded = true;
			audioManager.playAudio(AudioType.INTRO);
		}
    }

    @Override
    public void dispose() {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
	public void keyPressed(InputManager manager, GameKeys key) {
	}

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
      }
}