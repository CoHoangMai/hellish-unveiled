package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class AttackComponent implements Component, Poolable{
	public enum AttackState {
		READY, PREPARE, ATTACKING, DEAL_DAMAGE
	}

	public boolean doAttack;
	public int damage;
	public float delay;
	public float maxDelay;
	public float extraRange;
	private AttackState state;
	
	public AttackComponent() {
		doAttack = false;
		damage = 0;
		delay = 0;
		maxDelay = 0;
		extraRange = 0;
		state = AttackState.READY;
	}
	
	@Override
	public void reset() {
		doAttack = false;
		damage = 0;
		delay = 0;
		maxDelay = 0;
		extraRange = 0;
		state = AttackState.READY;
	}
	
	public AttackState getState() {
		return state;
	}
	
	public void setState(AttackState state) {
		this.state = state;
	}

	public boolean isReady() {
		return state == AttackState.READY;
	}
	
	public boolean isPrepared() {
		return state == AttackState.PREPARE;
	}
	
	public boolean isAttacking() {
		return state == AttackState.ATTACKING;
	}
	public void startAttack() {
		state = AttackState.PREPARE;
	}
}
