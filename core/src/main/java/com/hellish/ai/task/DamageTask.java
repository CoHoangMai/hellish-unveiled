package com.hellish.ai.task;

public class DamageTask extends Action{
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().startAttack();
			return Status.RUNNING;
		}
		
		if(getObject().isAnimationFinished()) {
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}
}
