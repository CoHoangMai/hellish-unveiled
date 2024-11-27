package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LootComponent implements Component, Poolable{
	public Entity interactEntity;
	
	public LootComponent() {
		interactEntity = null;
	}
	
	@Override
	public void reset() {
		interactEntity = null;
	}
}
