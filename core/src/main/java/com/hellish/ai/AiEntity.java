package com.hellish.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.StateComponent;

public class AiEntity {
	private final Entity entity;

	private final AnimationComponent aniCmp;
	private final StateComponent stateCmp;
	private final MoveComponent moveCmp;
	private final AttackComponent attackCmp;
	
	public AiEntity(Entity entity) {
		this.entity = entity;
		
		moveCmp = ECSEngine.moveCmpMapper.get(entity);
		attackCmp = ECSEngine.attackCmpMapper.get(entity);
		aniCmp = ECSEngine.aniCmpMapper.get(entity);
		stateCmp = ECSEngine.stateCmpMapper.get(entity);
	}
	
	public AttackComponent attackCmp() {
		return attackCmp;
	}
	
	public boolean wantsToMove() {
		return moveCmp != null && !(moveCmp.sine == 0 && moveCmp.cosine == 0);
	}
	
	public boolean wantsToAttack() {
		return attackCmp != null && attackCmp.doAttack;
	}
	
	public boolean isAnimationFinished() {
		return aniCmp != null && aniCmp.isAnimationFinished();
	}
	
	public void animation(AnimationType type, PlayMode mode, boolean resetAnimation) {
		if(aniCmp != null) {
			aniCmp.nextAnimation(type);
			aniCmp.mode = mode;
			if(resetAnimation) {
				aniCmp.aniTime = 0;
			}
		}
	}
	public void animation(AnimationType type) {
		animation(type, PlayMode.LOOP, false);
	}
	public void animation(AnimationType type, PlayMode mode) {
		animation(type, mode, false);
	}
	
	public void state(EntityState newState, boolean changeImmediate) {
		if(stateCmp != null) {
			stateCmp.nextState = newState;
			if(changeImmediate) {
				stateCmp.stateMachine.changeState(newState);
			}
		}
	}

	public void state(EntityState newState) {
		state(newState, false);
	}
	
	public void changeToPreviousState() {
		stateCmp.nextState = stateCmp.stateMachine.getPreviousState();
	}
	
	public void enableGlobalState(boolean enable) {
		if(enable) {
			stateCmp.stateMachine.setGlobalState(DefaultGlobalState.CHECK_ALIVE);
		} else {
			stateCmp.stateMachine.setGlobalState(null);
		}
	}
	
	public void root(boolean root) {
		moveCmp.rooted = root;
	}
	
	public void startAttack() {
		attackCmp.startAttack();
	}
	
	public boolean isDead() {
		final LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(entity);
		return lifeCmp.isDead();
	}
}
