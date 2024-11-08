package com.hellish.ai.task;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.hellish.ai.AiEntity;
import com.hellish.ecs.component.AnimationComponent.AnimationType;

public class WanderTask extends Action{
	@TaskAttribute(required = true)
	public float range;
	
	private final Vector2 startPos = new Vector2();
	private final Vector2 targetPos = new Vector2();

	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			if(startPos.isZero()) {
				startPos.set(getObject().getPosition());
			}
			getObject().animation(AnimationType.SIDE_WALK);
			targetPos.set(startPos);
			targetPos.x += MathUtils.random(-range, range);
			targetPos.y += MathUtils.random(-range, range);
			getObject().moveToPosition(targetPos);
			getObject().moveSlowly(true);
		}
		
		if(getObject().inRange(0, targetPos)) {
			getObject().stopMovement();
			return Status.SUCCEEDED;
		} else if(getObject().hasNearbyEnemy()) {
			return Status.SUCCEEDED;
		}
		
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		getObject().moveSlowly(false);
	}
	
	@Override
	public Task<AiEntity> copyTo(Task<AiEntity> task){
		((WanderTask)task).range = range;
		return task;
	}
}
