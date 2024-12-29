package com.hellish.screen;

import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.event.EventUtils;
import com.hellish.event.SelectEvent;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.view.DialogView;

public class DialogScreen extends AbstractScreen<DialogView>{
    private DialogView dialogView;

    public DialogScreen(final Main context) {
        super(context);
    }

    @Override
    public void render(float delta) {
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
    public void keyPressed(InputManager manager, GameKeys key) {
    	if(key == GameKeys.SELECT || key == GameKeys.ATTACK) {
    		dialogView.changeImage();
            EventUtils.fireEvent(gameStage, SelectEvent.pool, event -> {
            });
    	}
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
    }

    @Override
    protected Array<DialogView> getScreenViews(final Main context) {
        Array<DialogView> views = new Array<>();
        DialogView dialogView = new DialogView(Scene2DSkin.defaultSkin, context);
        views.add(dialogView);
        this.dialogView = dialogView;
        return views;
    }  
}
