package com.hellish.screen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.map.MapManager;
import com.hellish.map.MapType;
import com.hellish.view.GameUI;

public class GameScreen extends AbstractScreen<GameUI>{
	private final MapManager mapManager;
	private final AssetManager assetManager;
	private boolean isMusicLoaded;
	
	public GameScreen(final Main context) {
		super(context);
		
		assetManager = context.getAssetManager();
		
		mapManager = context.getMapManager();
		mapManager.setMap(MapType.MAP_1);	
		
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
		if(!isMusicLoaded && assetManager.isLoaded(AudioType.GAME.getFilePath())) {
			isMusicLoaded = true;
			audioManager.playAudio(AudioType.GAME);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
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
	protected GameUI getScreenUI(Main context) {
		return new GameUI(context);
	}

	@Override
	public void keyPressed(InputManager manager, GameKeys key) {

	}

	@Override
	public void keyUp(InputManager manager, GameKeys key) {
		
	}
}
