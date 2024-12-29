package com.hellish.ai.task;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.hellish.ecs.component.AnimationComponent.AnimationType;

public class AngryTask extends Action{
	@Override
	public Status execute() {
		if(getStatus() != Status.RUNNING) {
			getObject().animation(AnimationType.ANGRY, PlayMode.NORMAL, true);
			return Status.RUNNING;
		}
		
		if(getObject().isAnimationFinished()) {
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}
}
