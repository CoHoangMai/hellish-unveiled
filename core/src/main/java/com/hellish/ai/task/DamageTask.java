package com.hellish.ai.task;

import com.hellish.event.EntityAggroEvent;

public class DamageTask extends Action{
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().startAttack();
			getObject().fireEvent(new EntityAggroEvent(getObject().entity));
			return Status.RUNNING;
		}
		
		if(getObject().isAnimationFinished()) {
			getObject().stopMovement();
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}
}
