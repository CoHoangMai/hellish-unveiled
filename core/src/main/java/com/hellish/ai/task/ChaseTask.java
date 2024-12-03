package com.hellish.ai.task;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.hellish.ai.AiEntity;
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
	
	@Override
	public void end() {
		getObject().seekSteerer.stopPursuing();
	}
	
	@Override
	public Task<AiEntity> copyTo(Task<AiEntity> task){
		((ChaseTask)task).range = range;
		return task;
	}
}
