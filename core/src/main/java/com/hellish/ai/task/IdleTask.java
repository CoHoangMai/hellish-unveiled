package com.hellish.ai.task;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.utils.random.FloatDistribution;
import com.hellish.ai.AiEntity;
import com.hellish.ecs.component.AnimationComponent.AnimationType;

public class IdleTask extends Action{
	@TaskAttribute(required = true)
	public FloatDistribution duration;
	public float currentDuration;
	
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().animation(AnimationType.IDLE);
			currentDuration = (duration != null) ? duration.nextFloat() : 1;
			return Status.RUNNING;
		}
		
		currentDuration -= GdxAI.getTimepiece().getDeltaTime();
		
		if(getObject().hasNearbyEnemy() || currentDuration <= 0) {
			return Status.SUCCEEDED;
		}	
		
		return Status.RUNNING;
	}
	
	@Override
	public Task<AiEntity> copyTo(Task<AiEntity> task){
		((IdleTask)task).duration = duration;
		return task;
	}
}
