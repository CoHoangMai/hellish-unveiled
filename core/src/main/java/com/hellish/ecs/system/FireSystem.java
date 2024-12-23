package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.FireComponent;

public class FireSystem extends IteratingSystem{

	public FireSystem() {
		super(Family.all(FireComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
		
		if(aniCmp.animation != null && aniCmp.isAnimationFinished()) {
			getEngine().removeEntity(entity);
		}
	}

}
