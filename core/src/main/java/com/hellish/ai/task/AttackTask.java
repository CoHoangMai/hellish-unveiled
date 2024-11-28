package com.hellish.ai.task;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.event.EntityAggroEvent;

public class AttackTask extends Action{
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().startAttack();
			getObject().stopMovement();
			getObject().animation(AnimationType.ATTACK, PlayMode.NORMAL, true);
			EntityAggroEvent aggroEvent = EntityAggroEvent.pool.obtain().set(getObject().entity);
			getObject().fireEvent(aggroEvent);
			EntityAggroEvent.pool.free(aggroEvent);
			return Status.RUNNING;
		}
		
		if(getObject().isAnimationFinished()) {
			getObject().animation(AnimationType.IDLE);
			getObject().stopMovement();
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}
}
