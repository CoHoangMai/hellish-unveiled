package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class AttackComponent implements Component, Poolable{
	public enum AttackState {
		COOLDOWN, READY, PREPARE, ATTACKING, DEAL_DAMAGE
	}

	public boolean doAttack;
	public int damage;
	public float delay;
	public float maxDelay;
	public float extraRange;
	private AttackState state;
	
	public boolean canFire;
	
	public AttackComponent() {
		doAttack = false;
		damage = 0;
		delay = 0;
		maxDelay = 0;
		extraRange = 0;
		state = AttackState.READY;
		
		canFire = false;
	}
	
	@Override
	public void reset() {
		doAttack = false;
		damage = 0;
		delay = 0;
		maxDelay = 0;
		extraRange = 0;
		state = AttackState.READY;
		
		canFire = false;
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
