package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.PhysicsComponent;

public class SteeringSystem extends IteratingSystem{
	public SteeringSystem() {
		super(Family.all(PhysicsComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		
		if(physicsCmp.currentSteerer == null) {
			return;
		}
		
		//Tính toán gia tốc steering
		physicsCmp.isSteering = physicsCmp.currentSteerer.calculateSteering(physicsCmp.steeringOutput);
		
		if(physicsCmp.isSteering) {
			if(!physicsCmp.wasSteering) {
				physicsCmp.startSteering();
			}
			physicsCmp.applySteering(physicsCmp.steeringOutput, deltaTime);
		} else {
			physicsCmp.stopSteering(true);
		}
	}
}
