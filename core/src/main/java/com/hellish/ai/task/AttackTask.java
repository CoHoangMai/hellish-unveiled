package com.hellish.ai.task;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.hellish.ecs.component.AnimationComponent.AnimationType;

public class AttackTask extends Action{
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().startAttack();
			getObject().stopMovement();
			getObject().animation(AnimationType.SIDE_ATTACK, PlayMode.NORMAL, true);
		}
		
		if(getObject().isAnimationFinished()) {
			getObject().animation(AnimationType.IDLE);
			getObject().stopMovement();
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}

}
