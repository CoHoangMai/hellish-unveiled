package com.hellish.ai.task;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.hellish.ecs.component.AnimationComponent.AnimationType;

public class ChaseTask extends Action{
	@TaskAttribute(required = true)
	public float range = 0;
	
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().animation(AnimationType.WALK);
			return Status.RUNNING;
		}

		getObject().checkTargetStillNearby();
		getObject().moveToTarget();
		if(getObject().inTargetRange(range)) {
			return Status.SUCCEEDED;
		}

		return Status.RUNNING;
	}

}
