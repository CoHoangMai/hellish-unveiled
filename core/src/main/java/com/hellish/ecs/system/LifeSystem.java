package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.DeadComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.PhysicsComponent;

public class LifeSystem extends IteratingSystem{
	public LifeSystem() {
		super(Family.all(LifeComponent.class, PhysicsComponent.class).exclude(DeadComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(entity);
		lifeCmp.life = Math.min(lifeCmp.life + lifeCmp.regeneration * deltaTime, lifeCmp.max);
		
		if (lifeCmp.takeDamage > 0) {
			lifeCmp.life -= lifeCmp.takeDamage;
			lifeCmp.takeDamage = 0;
		}
		if (lifeCmp.isDead()) {
			final DeadComponent deadCmp = new DeadComponent();
			entity.add(deadCmp);
			System.out.println("Xin vĩnh biệt cụ");
			if (ECSEngine.playerCmpMapper.has(entity)) {
				deadCmp.reviveTime = 7f;
			}
		}
	}
}
