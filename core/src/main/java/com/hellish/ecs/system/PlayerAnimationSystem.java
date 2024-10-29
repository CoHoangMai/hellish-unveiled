package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.Box2DComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.view.AnimationType;

public class PlayerAnimationSystem extends IteratingSystem{

	public PlayerAnimationSystem(final Main context) {
		super(Family.all(AnimationComponent.class, PlayerComponent.class, Box2DComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final Box2DComponent b2dComponent = ECSEngine.b2dCmpMapper.get(entity);	
		final AnimationComponent animationComponent = ECSEngine.aniCmpMapper.get(entity);
		
		if(b2dComponent.body.getLinearVelocity().equals(Vector2.Zero)) {
			animationComponent.aniType = AnimationType.DOWN_IDLE;
		} else if (b2dComponent.body.getLinearVelocity().x > 0) {
			animationComponent.aniType = AnimationType.RIGHT_WALK;
		} else if (b2dComponent.body.getLinearVelocity().x < 0) {
			animationComponent.aniType = AnimationType.LEFT_WALK;
		} else if (b2dComponent.body.getLinearVelocity().y > 0) {
			animationComponent.aniType = AnimationType.UP_WALK;
		} else if (b2dComponent.body.getLinearVelocity().y < 0) {
			animationComponent.aniType = AnimationType.DOWN_WALK;
		} 
	}	
}

