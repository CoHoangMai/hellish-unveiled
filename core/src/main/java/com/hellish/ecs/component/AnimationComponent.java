package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pool;

public class AnimationComponent implements Component, Pool.Poolable{
	public enum AnimationModel {
		PLAYER, WOLF, CHEST, UNDEFINED;

		public String getModel() {
			return this.name().toLowerCase();
		}
	}
	
	public enum AnimationType {
		DOWN_WALK, UP_WALK, SIDE_WALK, IDLE, DOWN_IDLE, UP_IDLE, SIDE_IDLE;
		
		public String getAtlasKey() {
            return this.name().toLowerCase();
        }
	}

	
	public AnimationModel model;
	public float aniTime;
	public Animation.PlayMode mode;
	public Animation<TextureRegionDrawable> animation;
	public String nextAnimation;
	
	public static final String NO_ANIMATION = "";

	@Override
	public void reset() {
		model = AnimationModel.UNDEFINED;
		aniTime = 0;
		mode = Animation.PlayMode.LOOP;
		animation = null;
		nextAnimation = NO_ANIMATION;
	}
	
	public void nextAnimation(AnimationModel model, AnimationType type) {
		this.model = model;
		this.nextAnimation = model.getModel() + "/" + type.getAtlasKey();	
	}
	
	public void clearAnimation() {
		this.nextAnimation = NO_ANIMATION;
	}
	
}
