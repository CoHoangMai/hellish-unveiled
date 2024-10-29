package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class SpawnComponent implements Component, Poolable{
	public String type;
	public Vector2 location = new Vector2(0, 0);

	@Override
	public void reset() {
		type = "";
		location = new Vector2(0, 0);
	}
}
