package com.hellish.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.map.MapType;
import com.hellish.view.LoadingUI;

public class LoadingScreen extends AbstractScreen<LoadingUI> {
	private final AssetManager assetManager;
	private boolean isMusicLoaded;
	
	public LoadingScreen(final Main context) {
		super(context);
		
		this.assetManager = context.getAssetManager();
		
		assetManager.load("characters_and_effects/char_and_effect.atlas", TextureAtlas.class);

		for (final MapType maptype : MapType.values()) {
			assetManager.load(maptype.getFilePath(), TiledMap.class);
		}
		
		
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
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		assetManager.update();
		if(!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())) {
			isMusicLoaded = true;
			audioManager.playAudio(AudioType.INTRO);
		}
		
		screenUI.setProgress(assetManager.getProgress());
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
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
	protected LoadingUI getScreenUI(Main context) {
		return new LoadingUI(context);
	}

	@Override
	public void keyPressed(InputManager manager, GameKeys key) {
		if (assetManager.getProgress() >= 1) {
			audioManager.playAudio(AudioType.SELECT);
			context.setScreen(ScreenType.GAME);
		}
	}

	@Override
	public void keyUp(InputManager manager, GameKeys key) {
		
	}
	
}
