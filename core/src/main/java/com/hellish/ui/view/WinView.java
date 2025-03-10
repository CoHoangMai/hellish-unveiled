package com.hellish.ui.view;

import static com.hellish.ui.Scene2DSkin.OVERLAY_KEY;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.event.EventUtils;
import com.hellish.event.GameRestartEvent;
import com.hellish.event.GameResumeEvent;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.Scene2DSkin.ImageDrawables;

public class WinView extends Table{
	private final Stage gameStage;
	
	private final ImageButton goBackButton;
    private final ImageButton restartButton;
	private final Image congratulation;
	
	public WinView(Skin skin, final Main context) {
		super(skin);
		
		gameStage = context.getGameStage();
		
		setFillParent(true);
		
		setBackground(skin.get(OVERLAY_KEY, TextureRegionDrawable.class));
		
		Stack stack = new Stack();
		
		congratulation = new Image(new Texture("ui/screen/CONGRATULATION.png"));
	    stack.add(congratulation);
	    
	    goBackButton = Scene2DSkin.createButton(ImageDrawables.SMALL_BUTTON_QUIT, 50, 40);
        restartButton = Scene2DSkin.createButton(ImageDrawables.SMALL_BUTTON_RESTART, 50, 40);
        
        goBackButton.setVisible(false);
        restartButton.setVisible(false);
        goBackButton.getColor().a = 0;
        restartButton.getColor().a = 0;
        goBackButton.setTouchable(Touchable.disabled);
        restartButton.setTouchable(Touchable.disabled);
        
        Table table = new Table();
        table.add(goBackButton).size(40, 40).pad(20).padTop(160);
        table.add(restartButton).size(40, 40).pad(20).padTop(160);
		stack.add(table);
		
		this.add(stack).fill().expand().padBottom(40);
		
		 goBackButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					EventUtils.fireEvent(gameStage, GameRestartEvent.pool, e -> {});
					EventUtils.fireEvent(gameStage, GameResumeEvent.pool, e -> {});
					context.setScreen(ScreenType.MAIN_MENU);
	            }	
		 });

		 restartButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					EventUtils.fireEvent(gameStage, GameRestartEvent.pool, e -> {});
					EventUtils.fireEvent(gameStage, GameResumeEvent.pool, e -> {});
				}		
		 });
	}
	
	public void playShowAnimation() {
	    this.addAction(Actions.sequence(
	    	Actions.alpha(0),
	        Actions.fadeIn(0.5f),
	        Actions.delay(0.5f),
	        Actions.run(new Runnable() {
	            @Override
	            public void run() {
	                goBackButton.setVisible(true);
	                restartButton.setVisible(true);
	                goBackButton.addAction(Actions.fadeIn(0.5f));
	                restartButton.addAction(Actions.fadeIn(0.5f));
	            }
	        }),
	        Actions.delay(0.5f),
	        Actions.run(new Runnable() {
	            @Override
	            public void run() {
	                goBackButton.setTouchable(Touchable.enabled);
	                restartButton.setTouchable(Touchable.enabled);
	            }
	        })
	    ));
	}
}
