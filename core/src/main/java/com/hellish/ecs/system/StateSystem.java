package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.StateComponent;

public class StateSystem extends IteratingSystem{
	public StateSystem() {
		super(Family.all(StateComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final StateComponent stateCmp = ECSEngine.stateCmpMapper.get(entity);
		
		if(stateCmp.stateMachine.getCurrentState() != stateCmp.nextState) {
			stateCmp.stateMachine.changeState(stateCmp.nextState);
		}
		
		stateCmp.stateMachine.update();
	}
}
