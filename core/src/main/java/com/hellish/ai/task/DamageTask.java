package com.hellish.ai.task;

import com.hellish.event.EntityAggroEvent;

public class DamageTask extends Action{
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().startAttack();
			EntityAggroEvent aggroEvent = EntityAggroEvent.pool.obtain().set(getObject().entity);
			getObject().fireEvent(aggroEvent);
			EntityAggroEvent.pool.free(aggroEvent);
			return Status.RUNNING;
		}
		
		if(getObject().isAnimationFinished()) {
			getObject().stopMovement();
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}
}
