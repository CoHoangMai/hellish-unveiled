package com.hellish.screen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.ecs.component.ParticleEffectComponent.ParticleEffectType;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.map.MapType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.view.LoadingView;

public class LoadingScreen extends AbstractScreen<Table> {
	private final AssetManager assetManager;
	private LoadingView loadingView;
	private boolean isMusicLoaded;
	
	public LoadingScreen(final Main context) {
		super(context);
		
		this.assetManager = context.getAssetManager();
		
		assetManager.load("characters_and_effects/char_and_effect.atlas", TextureAtlas.class);
		assetManager.load("characters_and_effects/gameObjects.atlas", TextureAtlas.class);

		for (final MapType maptype : MapType.values()) {
			assetManager.load(maptype.getFilePath(), TiledMap.class);
		}
		
		
		isMusicLoaded = false;
		for (final AudioType audioType : AudioType.values()) {
		    if (audioType.isMusic()) {
		        assetManager.load(audioType.getFilePath(), Music.class);
		    } else {
		        assetManager.load(audioType.getFilePath(), Sound.class);
		    }
		}
		
		final ParticleEffectParameter peParameter = new ParticleEffectLoader.ParticleEffectParameter();
		peParameter.atlasFile = "characters_and_effects/char_and_effect.atlas";
		for (final ParticleEffectType type : ParticleEffectType.values()) {
			assetManager.load(type.getEffectFilePath(), ParticleEffect.class, peParameter);
		}
	}

	@Override
	public void render(float delta) {
		assetManager.update();
		if(!isMusicLoaded && assetManager.isLoaded(AudioType.INTRO.getFilePath())) {
			isMusicLoaded = true;
			audioManager.playAudio(AudioType.INTRO);
		}
		
		loadingView.setProgress(assetManager.getProgress());
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void hide() {
		super.hide();
		audioManager.stopCurrentMusic();
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
	protected Array<Table> getScreenViews(Main context) {
		Array<Table> views = new Array<>();
        
        LoadingView loadingView = new LoadingView(Scene2DSkin.defaultSkin);
        views.add(loadingView);
        this.loadingView = loadingView;
        
		return views;
	}

	@Override
	public void keyPressed(InputManager manager, GameKeys key) {
		if (assetManager.getProgress() >= 1) {
			audioManager.playAudio(AudioType.SELECT);
			context.setScreen(ScreenType.GAME);
		}
	}

	@Override
	public void keyUp(InputManager manager, GameKeys key) {
		
	}
	
}