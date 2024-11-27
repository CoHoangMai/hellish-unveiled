package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LifeComponent implements Component, Poolable{
	public float life;
	public float max;
	public float regeneration;
	public float takeDamage;
	public boolean isInjured;
	
	public boolean isDead() {
		return life <= 0;
	}
	
	public LifeComponent() {
		life = 30;
		max = 30;
		regeneration = 0;
		takeDamage = 0;
		isInjured = false;
	}
	
	@Override
	public void reset() {
		life = 30;
		max = 30;
		regeneration = 0;
		takeDamage = 0;
		isInjured = false;
	}

}
