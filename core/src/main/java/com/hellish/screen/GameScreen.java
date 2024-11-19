package com.hellish.screen;

import java.util.Arrays;
import java.util.HashSet;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.system.AnimationSystem;
import com.hellish.ecs.system.CameraSystem;
import com.hellish.ecs.system.DebugSystem;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.map.MapManager;
import com.hellish.map.MapType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.model.GameModel;
import com.hellish.ui.model.InventoryModel;
import com.hellish.ui.view.GameView;
import com.hellish.ui.view.InventoryView;
import com.hellish.ui.view.PauseView;

public class GameScreen extends AbstractScreen<Table>{
	private final MapManager mapManager;
	private final AssetManager assetManager;
	private final ECSEngine ecsEngine;
	private boolean isMusicLoaded;
	
	public GameScreen(final Main context) {
		super(context);
		
		assetManager = context.getAssetManager();
		ecsEngine = context.getECSEngine();
		
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
	
	private void pauseWorld(boolean pause) {
		HashSet<Class<?>> mandatorySystems = new HashSet<>(Arrays.asList(
			AnimationSystem.class,
			CameraSystem.class,
			RenderSystem.class,
			DebugSystem.class
		));
		
		for(EntitySystem system : ecsEngine.getSystems()) {
			if(!mandatorySystems.contains(system.getClass())) {
				system.setProcessing(!pause);
			}
		}
		
		for(Actor actor : uiStage.getActors()) {
			if(actor instanceof PauseView) {
				actor.setVisible(pause);
				break;
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
	public void pause() {
		pauseWorld(true);
	}

	@Override
	public void resume() {
		pauseWorld(false);
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
        
        PauseView pauseView = new PauseView(Scene2DSkin.defaultSkin);
        pauseView.setVisible(false);
        views.add(pauseView);
        
		return views;
	}

	@Override
	public void keyPressed(InputManager manager, GameKeys key) {

	}

	@Override
	public void keyUp(InputManager manager, GameKeys key) {
		
	}
}
