package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LifeComponent implements Component, Poolable{
	public int life;
	public int max;
	public int takeDamage;
	public float regeneration;
	public boolean isInjured;
	
	public boolean isDead() {
		return life <= 0;
	}
	
	public LifeComponent() {
		life = 0;
		max = 0;
		regeneration = 0;
		takeDamage = 0;
		isInjured = false;
	}
	
	@Override
	public void reset() {
		life = 0;
		max = 0;
		regeneration = 0;
		takeDamage = 0;
		isInjured = false;
	}

}
