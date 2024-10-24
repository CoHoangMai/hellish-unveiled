package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.hellish.map.GameObjectType;

public class GameObjectComponent implements Component, Pool.Poolable{
	public GameObjectType type;
	public int animationIndex;

	@Override
	public void reset() {
		type = null;
		animationIndex = -1;
	}

}