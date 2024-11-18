package com.hellish.screen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.map.MapManager;
import com.hellish.map.MapType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.model.GameModel;
import com.hellish.ui.model.InventoryModel;
import com.hellish.ui.view.GameView;
import com.hellish.ui.view.InventoryView;

public class GameScreen extends AbstractScreen<Table>{
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
	protected Array<Table> getScreenViews(Main context) {
		Array<Table> views = new Array<>();
		
		GameView gameView = new GameView(new GameModel(context.getGameStage()), Scene2DSkin.defaultSkin);
        views.add(gameView);
        
        InventoryView invView = new InventoryView(new InventoryModel(context), Scene2DSkin.defaultSkin);
        invView.setVisible(false);
        views.add(invView);
        
		return views;
	}

	@Override
	public void keyPressed(InputManager manager, GameKeys key) {

	}

	@Override
	public void keyUp(InputManager manager, GameKeys key) {
		
	}
}
