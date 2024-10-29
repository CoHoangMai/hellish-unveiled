package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pool;
import com.hellish.view.AnimationModel;
import com.hellish.view.AnimationType;

public class AnimationComponent implements Component, Pool.Poolable{
	public AnimationModel model;
	public AnimationType aniType;
	public float aniTime;
	public Animation.PlayMode mode;
	public Animation<TextureRegionDrawable> animation;
	public String nextAnimation;
	public float width;
	public float height;
	
	public static final String NO_ANIMATION = "";

	@Override
	public void reset() {
		model = AnimationModel.UNDEFINED;
		aniType = null;
		aniTime = 0;
		mode = Animation.PlayMode.LOOP;
		animation = null;
		nextAnimation = NO_ANIMATION;
		width = height = 0;
	}
	
	public void nextAnimation(AnimationModel model, AnimationType type) {
		this.model = model;
		this.nextAnimation = model.getModel() + "/" + type.getAtlasKey();	
	}
	
	public void clearAnimation() {
		this.nextAnimation = NO_ANIMATION;
	}
	
}
