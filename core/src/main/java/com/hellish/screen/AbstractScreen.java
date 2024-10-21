package com.hellish.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hellish.Main;
import com.hellish.audio.AudioManager;
import com.hellish.input.GameKeyInputListener;
import com.hellish.input.InputManager;

import box2dLight.RayHandler;

public abstract class AbstractScreen<T extends Table> implements Screen, GameKeyInputListener{
	protected final Main context;
	protected final FitViewport viewport;
	protected final World world;
	protected final RayHandler rayHandler;
	protected final Box2DDebugRenderer box2DDebugRenderer;
	protected final Stage stage;
	protected final T screenUI;
	protected final InputManager inputManager;
	protected final AudioManager audioManager;
	
	public AbstractScreen(final Main context) {
		this.context = context;
		viewport = context.getScreenViewport();
		this.world = context.getWorld();
		rayHandler = context.getRayHandler();
		this.box2DDebugRenderer = context.getBox2DDebugRenderer();
		inputManager = context.getInputManager();
		
		stage = context.getStage();
		screenUI = getScreenUI(context);
		audioManager = context.getAudioManager();
	}
	
	protected abstract T getScreenUI(final Main context);
	
	@Override
	public void resize(final int width, final int height) {
		viewport.update(width, height);
		stage.getViewport().update(width, height, true);
		rayHandler.useCustomViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
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
