package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class DeadComponent implements Component, Poolable{
	public Float reviveTime = null;
	@Override
	public void reset() {
		reviveTime = null;
	}

}
