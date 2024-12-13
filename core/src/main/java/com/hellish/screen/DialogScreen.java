package com.hellish.screen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.view.DialogView;

public class DialogScreen extends AbstractScreen<DialogView>{
    private final AssetManager assetManager;
    private boolean isMusicLoaded;

    public DialogScreen(final Main context) {
        super(context);
        assetManager = context.getAssetManager();
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
        if(!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())) {
			isMusicLoaded = true;
			audioManager.playAudio(AudioType.INTRO);
		}
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispose'");
    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyUp'");
    }

    @Override
    protected Array<DialogView> getScreenViews(final Main context) {
        Array<DialogView> views = new Array<>();
        DialogView dialogView = new DialogView(Scene2DSkin.defaultSkin, context);
        views.add(dialogView);
        return views;
    }  
}
