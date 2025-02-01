package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CooldownComponent implements Component, Poolable{
	public float current;
	public float max;
	public float reduceAmount;
	public float regeneration;
	
	public CooldownComponent() {
		current = 10;
		max = 10;
		regeneration = 1;
		reduceAmount = 0;
	}
	
	@Override
	public void reset() {
		current = 10;
		max = 10;
		regeneration = 1;
		reduceAmount = 0;
	}
}
