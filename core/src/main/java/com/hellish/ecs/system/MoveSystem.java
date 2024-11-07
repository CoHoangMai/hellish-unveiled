package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PhysicsComponent;

public class MoveSystem extends IteratingSystem{

	public MoveSystem() {
		super(Family.all(MoveComponent.class, PhysicsComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final MoveComponent moveCmp = ECSEngine.moveCmpMapper.get(entity);
		final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		float mass = physicsCmp.body.getMass();
		float velX = physicsCmp.body.getLinearVelocity().x;
		float velY = physicsCmp.body.getLinearVelocity().y;
		
		if ((moveCmp.cosine == 0 && moveCmp.sine == 0) || moveCmp.rooted) {
			physicsCmp.impulse.set(mass * (0f - velX), mass * (0f - velY));
			return;
		}
		physicsCmp.impulse.set(moveCmp.speed * moveCmp.cosine - velX, moveCmp.speed * moveCmp.sine - velY);
		
		final ImageComponent imageCmp = ECSEngine.imageCmpMapper.get(entity);
		if(imageCmp != null) {
			if(moveCmp.cosine != 0) {
				imageCmp.image.setFlipX(moveCmp.cosine > 0);
			}
		}
	}
}
