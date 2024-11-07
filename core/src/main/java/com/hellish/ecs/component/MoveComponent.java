package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MoveComponent implements Component, Poolable{
	public float speed;
	public float sine;
	public float cosine;
	public boolean rooted;
	
	public MoveComponent() {
		speed = 0;
		sine = 0;
		cosine = 0;
		rooted = false;
	}
	
	@Override
	public void reset() {
		speed = 0;
		sine = 0;
		cosine = 0;
		rooted = false;
	}

}
