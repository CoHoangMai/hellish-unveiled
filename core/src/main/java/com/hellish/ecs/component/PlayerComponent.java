package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Component, Pool.Poolable{
	public boolean hasGun;
	public Vector2 speed = new Vector2();
	
	@Override
	public void reset() {
		hasGun = false;
		speed.set(0, 0);
	}

}
