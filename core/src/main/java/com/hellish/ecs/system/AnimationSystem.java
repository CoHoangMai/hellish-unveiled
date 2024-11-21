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
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.event.EntityDirectionChangedEvent;

public class AnimationSystem extends IteratingSystem implements EventListener{
	public static final String TAG = AnimationSystem.class.getSimpleName();
	private static final float DEFAULT_FRAME_DURATION = 1 / 8f;
	
	private final AssetManager assetManager;
	private final Map<String, Animation<TextureRegionDrawable>> animationCache;
	private final Map<String, Boolean> noDirectionCache;
	
	public AnimationSystem(final Main context) {
		super(Family.all(AnimationComponent.class, ImageComponent.class).get());
		
		assetManager = context.getAssetManager();
		animationCache = new HashMap<>();
		noDirectionCache = new HashMap<>();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
		final ImageComponent imgCmp = ECSEngine.imageCmpMapper.get(entity);
		
		if (aniCmp.currentModel == aniCmp.nextModel && aniCmp.currentAnimationType == aniCmp.nextAnimationType
				&& aniCmp.currentDirectionKey == aniCmp.nextDirectionKey) {
			aniCmp.aniTime += deltaTime;
		} else {
			aniCmp.animation = animation(aniCmp.nextModel.getModel(), aniCmp.nextAnimationType.getAtlasKey(), aniCmp.nextDirectionKey);
			aniCmp.clearAnimation();
			aniCmp.aniTime = 0;
		}
		aniCmp.animation.setPlayMode(aniCmp.mode);
		imgCmp.image.setDrawable(aniCmp.animation.getKeyFrame(aniCmp.aniTime));
	}
	
	private Animation<TextureRegionDrawable> animation(String modelKey, String typeKey, String directionKey){
		String directionAtlasKey = modelKey + "/" + directionKey + typeKey;
		String noDirectionAtlasKey = modelKey + "/" + typeKey;
		
		if(animationCache.containsKey(directionAtlasKey)) {
			return animationCache.get(directionAtlasKey);
		}
		
		//Nếu đã từng biết là animation này không có loại có hướng
		if (noDirectionCache.containsKey(directionAtlasKey) && noDirectionCache.get(directionAtlasKey)) {
	        if(animationCache.containsKey(noDirectionAtlasKey)) {
	            return animationCache.get(noDirectionAtlasKey);
	        }
	    }
	
		final TextureAtlas atlas = assetManager.get("characters_and_effects/gameObjects.atlas", TextureAtlas.class);
		
		final Array<AtlasRegion> atlasRegions = atlas.findRegions(directionAtlasKey);
		if (atlasRegions.size == 0) {
			//Nếu không có animation có hướng thì thử animation không có hướng
			Gdx.app.debug(TAG, "Không có " + directionAtlasKey);
			noDirectionCache.put(directionAtlasKey, true);
			
			if(animationCache.containsKey(noDirectionAtlasKey)) {
				return animationCache.get(noDirectionAtlasKey);
			}

			final Array<AtlasRegion> typeAtlasRegions = atlas.findRegions(noDirectionAtlasKey);
			if(typeAtlasRegions.size == 0) {
				throw new GdxRuntimeException("Không có texture region cho " + directionAtlasKey + " hay " + noDirectionAtlasKey);
			}
			
			Array<TextureRegionDrawable> drawableRegions = new Array<>(typeAtlasRegions.size);
            for (final AtlasRegion atlasRegion : typeAtlasRegions) {
                drawableRegions.add(new TextureRegionDrawable(new TextureRegion(atlasRegion)));
            }
            
            Animation<TextureRegionDrawable> animation = new Animation<>(DEFAULT_FRAME_DURATION, drawableRegions);
            animationCache.put(noDirectionAtlasKey, animation);
    		Gdx.app.debug(TAG, "Tạo animation mới loại " + noDirectionAtlasKey);
            return animation;
		}
		
		Array<TextureRegionDrawable> drawableRegions = new Array<>(atlasRegions.size);
		for(final AtlasRegion atlasRegion : atlasRegions) {
			drawableRegions.add(new TextureRegionDrawable(new TextureRegion(atlasRegion)));
		}
		
		Animation<TextureRegionDrawable> animation = new Animation<>(DEFAULT_FRAME_DURATION, drawableRegions);
        animationCache.put(directionAtlasKey, animation);
		Gdx.app.debug(TAG, "Tạo animation mới loại " + directionAtlasKey);
        return animation;
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof EntityDirectionChangedEvent) {
			EntityDirectionChangedEvent directionEvent = (EntityDirectionChangedEvent) event;
			AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(directionEvent.getEntity());
			
			if(aniCmp.currentDirectionKey == aniCmp.getDirectionKey(directionEvent.getDirection())) {
				return true;
			}
			
			aniCmp.nextDirectionKey = aniCmp.getDirectionKey(directionEvent.getDirection());
			return true;
		}
		return false;
	}
}
