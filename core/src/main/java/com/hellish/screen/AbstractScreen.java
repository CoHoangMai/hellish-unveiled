package com.hellish.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.hellish.Main;
import com.hellish.audio.AudioManager;
import com.hellish.input.GameKeyInputListener;
import com.hellish.input.InputManager;

import box2dLight.RayHandler;

public abstract class AbstractScreen<T extends Table> implements Screen, GameKeyInputListener{
	protected final Main context;
	protected final World world;
	protected final RayHandler rayHandler;
	protected final Stage stage;
	protected final T screenUI;
	protected final InputManager inputManager;
	protected final AudioManager audioManager;
	
	public AbstractScreen(final Main context) {
		this.context = context;
		this.world = context.getWorld();
		rayHandler = context.getRayHandler();
		inputManager = context.getInputManager();
		
		stage = context.getStage();
		screenUI = getScreenUI(context);
		audioManager = context.getAudioManager();
	}
	
	protected abstract T getScreenUI(final Main context);
	
	@Override
	public void resize(final int width, final int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void show() {
		inputManager.addInputListener(this);
		stage.addActor(screenUI);
	}
	
	@Override
	public void hide() {
		inputManager.removeInputListener(this);
		stage.getRoot().removeActor(screenUI);
	}
}
