package com.hellish.ecs.system;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.ImageComponent;

public class AnimationSystem extends IteratingSystem{
	public static final String TAG = AnimationSystem.class.getSimpleName();
	private static final float DEFAULT_FRAME_DURATION = 1 / 8f;
	
	private final AssetManager assetManager;
	private final Map<String, Animation<TextureRegionDrawable>> animationCache;
	
	public AnimationSystem(final Main context) {
		super(Family.all(AnimationComponent.class, ImageComponent.class).get());
		
		assetManager = context.getAssetManager();
		animationCache = new HashMap<>();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
		final ImageComponent imgCmp = ECSEngine.imageCmpMapper.get(entity);
		
		if (aniCmp.nextAnimation == AnimationComponent.NO_ANIMATION) {
			aniCmp.aniTime += deltaTime;
		} else {
			aniCmp.animation = animation(aniCmp.nextAnimation);
			aniCmp.clearAnimation();
			aniCmp.aniTime = 0;
		}
		aniCmp.animation.setPlayMode(aniCmp.mode);
		imgCmp.image.setDrawable(aniCmp.animation.getKeyFrame(aniCmp.aniTime));
		//aniCmp.aniTime += deltaTime;
	}
	
	private Animation<TextureRegionDrawable> animation(String atlasKey){
		return animationCache.computeIfAbsent(atlasKey, key -> {
			Gdx.app.debug(TAG, "Tạo animation mới loại " + key);
			
			final TextureAtlas atlas = assetManager.get("characters_and_effects/gameObjects.atlas", TextureAtlas.class);
			final Array<AtlasRegion> atlasRegions = atlas.findRegions(key);
			if (atlasRegions.size == 0) {
				throw new GdxRuntimeException("Không có texture region cho " + key);
			}
			Array<TextureRegionDrawable> drawableRegions = new Array<>(atlasRegions.size);
			for(final AtlasRegion atlasRegion : atlasRegions) {
				drawableRegions.add(new TextureRegionDrawable(new TextureRegion(atlasRegion)));
			}
			return new Animation<>(DEFAULT_FRAME_DURATION, drawableRegions);
		});
	}
	
}
