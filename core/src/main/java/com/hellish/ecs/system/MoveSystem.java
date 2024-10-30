package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PhysicComponent;

public class MoveSystem extends IteratingSystem{

	public MoveSystem() {
		super(Family.all(MoveComponent.class, PhysicComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final MoveComponent moveCmp = ECSEngine.moveCmpMapper.get(entity);
		final PhysicComponent physicCmp = ECSEngine.physicCmpMapper.get(entity);
		float mass = physicCmp.body.getMass();
		float velX = physicCmp.body.getLinearVelocity().x;
		float velY = physicCmp.body.getLinearVelocity().y;
		
		if (moveCmp.cosine == 0 && moveCmp.sine == 0) {
			physicCmp.impulse.set(mass * (0f - velX), mass * (0f - velY));
			return;
		}
		physicCmp.impulse.set(moveCmp.speed * moveCmp.cosine - velX, moveCmp.speed * moveCmp.sine - velY);
	}

}
