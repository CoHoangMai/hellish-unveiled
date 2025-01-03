package com.hellish.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.input.GameKeyInputListener;
import com.hellish.input.InputManager;

import box2dLight.RayHandler;

public abstract class AbstractScreen<T extends Table> implements Screen, GameKeyInputListener{
	protected final Main context;
	protected final World world;
	protected final RayHandler rayHandler;
	protected final Stage gameStage;
	protected final Stage uiStage;
	protected final Array<T> screenViews;
	protected final InputManager inputManager;
	
	public AbstractScreen(final Main context) {
		this.context = context;
		this.world = context.getWorld();
		rayHandler = context.getRayHandler();
		inputManager = context.getInputManager();
		
		gameStage = context.getGameStage();
		uiStage = context.getUIStage();
		screenViews = getScreenViews(context);
	}
	
	protected abstract Array<T> getScreenViews(final Main context);
	
	@Override
	public void resize(final int width, final int height) {
		gameStage.getViewport().update(width, height, true);
		uiStage.getViewport().update(width, height, true);
	}
	
	@Override
	public void show() {
		inputManager.addInputListener(this);
		for(T screenView : screenViews) {
			uiStage.addActor(screenView);
		}
	}
	
	@Override
	public void hide() {
		inputManager.removeInputListener(this);
		for(T screenView : screenViews) {
			uiStage.getRoot().removeActor(screenView);
		}
	}
}
