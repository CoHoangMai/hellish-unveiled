package com.hellish.ecs.system;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
//import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.hellish.Main;
import com.hellish.actor.FlipImage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AnimationComponent.AnimationModel;
import com.hellish.ecs.component.PhysicsComponent.Direction;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.PhysicsComponent;

public class AnimationSystem extends IteratingSystem {
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
		final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		final LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(entity);
		
		if(aniCmp.nextModel == AnimationModel.FIRE) {
			if (aniCmp.currentModel == aniCmp.nextModel && aniCmp.currentAnimationType == aniCmp.nextAnimationType
					&& aniCmp.currentDirectionKey == aniCmp.nextDirectionKey && aniCmp.currentInjuredStatus == aniCmp.nextInjuredStatus) {
				aniCmp.aniTime += deltaTime;
			} else {
				aniCmp.animation = animationForFire();
				aniCmp.clearAnimation();
				aniCmp.aniTime = 0;
			}
			aniCmp.animation.setPlayMode(aniCmp.mode);
			imgCmp.image.setDrawable(aniCmp.animation.getKeyFrame(aniCmp.aniTime));
			return;
		}
		
		if(aniCmp.currentDirectionKey != getDirectionKey(physicsCmp.direction)) {
			aniCmp.nextDirectionKey = getDirectionKey(physicsCmp.direction);
		}
		
		if(lifeCmp != null && lifeCmp.isInjured != aniCmp.currentInjuredStatus) {
			aniCmp.nextInjuredStatus = lifeCmp.isInjured;
		}
		
		if (aniCmp.currentModel == aniCmp.nextModel && aniCmp.currentAnimationType == aniCmp.nextAnimationType
				&& aniCmp.currentDirectionKey == aniCmp.nextDirectionKey && aniCmp.currentInjuredStatus == aniCmp.nextInjuredStatus) {
			aniCmp.aniTime += deltaTime;
		} else {
			aniCmp.animation = animation(imgCmp.image, aniCmp, aniCmp.nextModel.getModel(), aniCmp.nextInjuredStatus, aniCmp.nextAnimationType.getAtlasKey(), physicsCmp.direction);
			aniCmp.clearAnimation();
			aniCmp.aniTime = 0;
		}
		aniCmp.animation.setPlayMode(aniCmp.mode);
		imgCmp.image.setDrawable(aniCmp.animation.getKeyFrame(aniCmp.aniTime));
	}
	
	private Animation<TextureRegionDrawable> animationForFire(){
		String fireKey = "fire/fire";
        if (animationCache.containsKey(fireKey)) {
            return animationCache.get(fireKey);  // Nếu đã có animation "fire", trả về
        }

        // Tạo animation mới cho fire
        final TextureAtlas atlas = assetManager.get("game_objects/gameObjects.atlas", TextureAtlas.class);
        final Array<AtlasRegion> fireAtlasRegions = atlas.findRegions(fireKey);
        
        if (fireAtlasRegions.size == 0) {
            throw new GdxRuntimeException("Không có texture region cho " + fireKey);
        }

        Array<TextureRegionDrawable> drawableRegions = new Array<>(fireAtlasRegions.size);
        for (final AtlasRegion atlasRegion : fireAtlasRegions) {
            drawableRegions.add(new TextureRegionDrawable(new TextureRegion(atlasRegion)));
        }

        Animation<TextureRegionDrawable> fireAnimation = new Animation<>(DEFAULT_FRAME_DURATION * 0.6f, drawableRegions);
        animationCache.put(fireKey, fireAnimation);
        return fireAnimation;
	}
	
	private Animation<TextureRegionDrawable> animation(FlipImage image, AnimationComponent aniCmp, 
			String modelKey, boolean isInjured, String typeKey, Direction direction){
		String directionAtlasKey;
		String noDirectionAtlasKey;
		
		String directionKey = getDirectionKey(direction);
		
		if(modelKey.contains("zombie") && isInjured) {
			directionAtlasKey = modelKey + "/injured_" + directionKey + typeKey;
			noDirectionAtlasKey = modelKey + "/injured_" + typeKey;
		} else {
			directionAtlasKey = modelKey + "/" + directionKey + typeKey;
			noDirectionAtlasKey = modelKey + "/" + typeKey;
		}
		
		if(animationCache.containsKey(directionAtlasKey)) {
			aniCmp.realDirection = direction;
			return animationCache.get(directionAtlasKey);
		}
		
		//Nếu đã từng biết là animation này không có loại có hướng
		if (noDirectionCache.containsKey(directionAtlasKey) && noDirectionCache.get(directionAtlasKey)) {
	        if(animationCache.containsKey(noDirectionAtlasKey)) {
	        	aniCmp.realDirection = image.isFlipX() ? Direction.RIGHT : Direction.LEFT;
	            return animationCache.get(noDirectionAtlasKey);
	        }
	    }
	
		final TextureAtlas atlas = assetManager.get("game_objects/gameObjects.atlas", TextureAtlas.class);
		
		final Array<AtlasRegion> atlasRegions = atlas.findRegions(directionAtlasKey);
		if (atlasRegions.size == 0) {
			//Nếu không có animation có hướng thì thử animation không có hướng
			//Gdx.app.debug(TAG, "Không có " + directionAtlasKey);
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
        	aniCmp.realDirection = image.isFlipX() ? Direction.RIGHT : Direction.LEFT;
    		//Gdx.app.debug(TAG, "Tạo animation mới loại " + noDirectionAtlasKey);
            return animation;
		}
		
		Array<TextureRegionDrawable> drawableRegions = new Array<>(atlasRegions.size);
		for(final AtlasRegion atlasRegion : atlasRegions) {
			drawableRegions.add(new TextureRegionDrawable(new TextureRegion(atlasRegion)));
		}
		
		Animation<TextureRegionDrawable> animation = new Animation<>(DEFAULT_FRAME_DURATION, drawableRegions);
        animationCache.put(directionAtlasKey, animation);
        aniCmp.realDirection = direction;
		//Gdx.app.debug(TAG, "Tạo animation mới loại " + directionAtlasKey);
        return animation;
	}
	
	public String getDirectionKey(Direction direction) {
		if(direction == Direction.UP) {
			return "up_";
		} else if(direction == Direction.DOWN) {
			return "down_";
		} else if(direction == Direction.LEFT || direction == Direction.RIGHT) {
			return "side_";
		} else {
			return "";
		}
	}
	
}
