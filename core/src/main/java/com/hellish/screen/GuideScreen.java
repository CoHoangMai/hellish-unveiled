package com.hellish.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.view.GuideView;

public class GuideScreen extends AbstractScreen<Table>{
    private final AssetManager assetManager;
    private boolean isMusicLoaded;
    OrthographicCamera camera;
    SpriteBatch batch;

    public GuideScreen(final Main context) {
        super(context);
        assetManager = context.getAssetManager();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
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
    protected Array<Table> getScreenViews(final Main context) {
        Array<Table> views = new Array<>();
        GuideView guideView = new GuideView(Scene2DSkin.defaultSkin, context);
        views.add(guideView);
        return views;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resume() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(InputManager manager, GameKeys key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
