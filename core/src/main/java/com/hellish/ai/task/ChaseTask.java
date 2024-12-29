package com.hellish.ai.task;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.hellish.ai.AiEntity;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.event.EntityAggroEvent;
import com.hellish.event.EventUtils;

public class ChaseTask extends Action{
	@TaskAttribute(required = true)
	public float range = 0;
	
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().animation(AnimationType.WALK);
			EventUtils.fireEvent(getObject().stage, EntityAggroEvent.pool, event -> event.set(getObject().entity));
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
		getObject().pursueSteerer.stopPursuing();
	}
	
	@Override
	public Task<AiEntity> copyTo(Task<AiEntity> task){
		((ChaseTask)task).range = range;
		return task;
	}
}
