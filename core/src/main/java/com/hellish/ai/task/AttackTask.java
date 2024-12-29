package com.hellish.ai.task;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.hellish.ecs.component.AnimationComponent.AnimationType;

public class AttackTask extends Action{
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().startAttack();
			getObject().animation(AnimationType.ATTACK, PlayMode.NORMAL, true);
			return Status.RUNNING;
		}
		
		if(getObject().isAnimationFinished()) {
			getObject().animation(AnimationType.IDLE);
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}
}
