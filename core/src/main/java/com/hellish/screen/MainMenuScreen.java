package com.hellish.screen;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.system.AudioSystem;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.view.MainMenuView;
import com.hellish.ui.view.PauseView;
import com.hellish.ui.view.SettingView;

public class MainMenuScreen extends AbstractScreen<Table> {
	private final ECSEngine ecsEngine;

    public MainMenuScreen(final Main context) {
        super(context);
        
        ecsEngine = context.getECSEngine();
		for(EntitySystem system : ecsEngine.getSystems()) {
			if(!(system instanceof RenderSystem)  && !(system instanceof AudioSystem)) {
				system.setProcessing(false);
			}
		}
    }

    @Override
    protected Array<Table> getScreenViews(final Main context) {
        Array<Table> views = new Array<>();
        
        MainMenuView mainMenuView = new MainMenuView(Scene2DSkin.defaultSkin, context);
        views.add(mainMenuView);
        
        SettingView settingView = new SettingView(Scene2DSkin.defaultSkin, context);
        settingView.setVisible(false);
        views.add(settingView);
        
        return views;
    }
    
    public void showSettingView(boolean show) {
    	for(Actor actor : uiStage.getActors()) {
			if(actor instanceof SettingView) {
				actor.setVisible(show);
				break;
			}
		}
    }

    @Override
    public void render(float delta) {
    	
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