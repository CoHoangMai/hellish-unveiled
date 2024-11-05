package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LifeComponent implements Component, Poolable{
	public float life = 30;
	public float max = 30;
	public float regeneration = 1;
	public float takeDamage = 0;
	
	public boolean isDead() {
		return life <= 0;
	}
	
	@Override
	public void reset() {
		life = 30;
		max = 30;
		regeneration = 1;
		takeDamage = 0;
	}

}
