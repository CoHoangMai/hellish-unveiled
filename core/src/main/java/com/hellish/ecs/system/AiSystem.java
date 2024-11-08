package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AiComponent;
import com.hellish.ecs.component.DeadComponent;

public class AiSystem extends IteratingSystem{

	public AiSystem() {
		super(Family.all(AiComponent.class).exclude(DeadComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final AiComponent aiCmp = ECSEngine.aiCmpMapper.get(entity);
		aiCmp.behaviorTree.step();
	}
}
