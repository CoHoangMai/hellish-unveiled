package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;

public class AnimationSystem extends IteratingSystem{

	public AnimationSystem(final Main context) {
		super(Family.all(AnimationComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final AnimationComponent animationComponent = ECSEngine.aniCmpMapper.get(entity);
		if(animationComponent.aniType != null) {
			animationComponent.aniTime += deltaTime;
		}
	}
	
}
