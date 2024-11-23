package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ecs.component.MoveComponent.Direction;

public class AnimationComponent implements Component, Poolable{
	public enum AnimationModel {
		PLAYER, WOLF, CHEST,
		FLAG_ZOMBIE, RUNNING_ZOMBIE, TREE_ZOMBIE,
		UNDEFINED;

		public String getModel() {
			return this.name().toLowerCase();
		}
	}
	
	public enum AnimationType {
		IDLE, WALK, ATTACK, OPEN, DIE;
		
		public String getAtlasKey() {
            return this.name().toLowerCase();
        }
	}
	
	public float aniTime;
	public Animation.PlayMode mode;
	public Animation<TextureRegionDrawable> animation;
	public AnimationModel currentModel;
	public AnimationModel nextModel;
	public boolean currentInjuredStatus;
	public boolean nextInjuredStatus;
	public AnimationType currentAnimationType;
	public AnimationType nextAnimationType;
	public String currentDirectionKey;
	public String nextDirectionKey;
	
	public AnimationComponent() {
		aniTime = 0;
		mode = Animation.PlayMode.LOOP;
		animation = null;
		currentModel = AnimationModel.UNDEFINED;
		nextModel = AnimationModel.UNDEFINED;
		currentInjuredStatus = false;
		nextInjuredStatus = false;
		currentAnimationType = AnimationType.IDLE;
		nextAnimationType = AnimationType.IDLE;
		currentDirectionKey = "down_";
		nextDirectionKey = "down_";
	}

	@Override
	public void reset() {
		aniTime = 0;
		mode = Animation.PlayMode.LOOP;
		animation = null;
		currentModel = AnimationModel.UNDEFINED;
		nextModel = AnimationModel.UNDEFINED;
		currentInjuredStatus = false;
		nextInjuredStatus = false;
		currentAnimationType = AnimationType.IDLE;
		nextAnimationType = AnimationType.IDLE;
		currentDirectionKey = "down_";
		nextDirectionKey = "down_";
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
	
	public void nextAnimation(AnimationType type) {
		this.nextAnimationType = type;
	}
	
	public void nextAnimation(AnimationModel model, AnimationType type) {
		this.nextModel = model;
		this.nextAnimationType = type;
	} 
	
	public void clearAnimation() {
		this.currentModel = this.nextModel;
		this.currentInjuredStatus = this.nextInjuredStatus;
		this.currentAnimationType = this.nextAnimationType;
		this.currentDirectionKey = this.nextDirectionKey;
	}
	
	public boolean isAnimationFinished() {
		return animation.isAnimationFinished(aniTime);
	}
}
