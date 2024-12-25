package com.hellish.screen;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.map.MapType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.view.LoadingView;

public class LoadingScreen extends AbstractScreen<Table> {
	private final AssetManager assetManager;
	private final ECSEngine ecsEngine;
	private LoadingView loadingView;
	
	public LoadingScreen(final Main context) {
		super(context);
		
		ecsEngine = context.getECSEngine();
		for(EntitySystem system : ecsEngine.getSystems()) {
			if(!(system instanceof RenderSystem)) {
				system.setProcessing(false);
			}
		}
		
		this.assetManager = context.getAssetManager();
		
		assetManager.load("game_objects/gameObjects.atlas", TextureAtlas.class);

		for (final MapType mapType : MapType.values()) {
			if(mapType != MapType.NO_MAP) {
				assetManager.load(mapType.getFilePath(), TiledMap.class);
			}
		}
		
		for (final AudioType audioType : AudioType.values()) {
		    if (audioType.isMusic()) {
		        assetManager.load(audioType.getFilePath(), Music.class);
		    } else {
		        assetManager.load(audioType.getFilePath(), Sound.class);
		    }
		}
	}

	@Override
	public void render(float delta) {
		assetManager.update();
		// if(!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())) {
		// 	isMusicLoaded = true;
		// 	audioManager.playAudio(AudioType.INTRO);
		// }
		
		loadingView.setProgress(assetManager.getProgress());
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void hide() {
		super.hide();
		audioManager.stopCurrentMusic();
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
	protected Array<Table> getScreenViews(Main context) {
		Array<Table> views = new Array<>();
        
        LoadingView loadingView = new LoadingView(Scene2DSkin.defaultSkin);
        views.add(loadingView);
        this.loadingView = loadingView;
        
		return views;
	}

	@Override
	public void keyPressed(InputManager manager, GameKeys key) {
		if (assetManager.getProgress() >= 1) {
			audioManager.playAudio(AudioType.SELECT);
			context.setScreen(ScreenType.MAIN_MENU);
		}
	}

	@Override
	public void keyUp(InputManager manager, GameKeys key) {
		
	}	
}