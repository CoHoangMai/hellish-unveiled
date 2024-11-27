package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MoveComponent implements Component, Poolable{
	public enum Direction {
		UP, DOWN, LEFT, RIGHT;
		
		public String getDirection() {
			return this.name().toLowerCase();
		}
	}
	
	public float speed;
	public float sine;
	public float cosine;
	public boolean rooted;
	public boolean slow;
	public Direction direction;
	
	public MoveComponent() {
		speed = 0;
		sine = 0;
		cosine = 0;
		rooted = false;
		slow = false;
		direction = Direction.DOWN;
	}
	
	@Override
	public void reset() {
		speed = 0;
		sine = 0;
		cosine = 0;
		rooted = false;
		slow = false;
		direction = Direction.DOWN;
	}
}
