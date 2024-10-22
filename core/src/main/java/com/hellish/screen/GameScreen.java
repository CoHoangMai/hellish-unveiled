package com.hellish.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.hellish.Main;
import com.hellish.PreferenceManager;
import com.hellish.audio.AudioType;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.map.Map;
import com.hellish.map.MapListener;
import com.hellish.map.MapManager;
import com.hellish.map.MapType;
import com.hellish.view.GameUI;

public class GameScreen extends AbstractScreen<GameUI> implements MapListener{
	private static final String TAG = GameScreen.class.getSimpleName();
	
	private final MapManager mapManager;
	private final PreferenceManager prefManager;
	private final Entity player;
	private final AssetManager assetManager;
	private boolean isMusicLoaded;
	
	public GameScreen(final Main context) {
		super(context);
		
		assetManager = context.getAssetManager();
		
		mapManager = context.getMapManager();
		mapManager.addMapListener(this);
		mapManager.setMap(MapType.MAP_1);
		prefManager = context.getPreferenceManager();	
		
		player = context.getECSEngine().createPlayer(mapManager.getCurrentMap().getStartLocation(), 0.75f, 0.75f);
		
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
		
		//TODO bỏ đoạn test map này
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			mapManager.setMap(MapType.MAP_1);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
			mapManager.setMap(MapType.MAP_2);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
			prefManager.saveGameState(player);
			Gdx.app.debug(TAG, "Đã tạo save file");
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
			prefManager.loadGameState(player);
			Gdx.app.debug(TAG, "Đã load save file");
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

	@Override
	public void mapChange(Map map) {
		
	}
	
}
