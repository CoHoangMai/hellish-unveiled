package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ecs.component.AnimationComponent.AnimationModel;

public class SpawnComponent implements Component, Poolable{
	public String type;
	public Vector2 location = new Vector2(0, 0);

	@Override
	public void reset() {
		type = "";
		location = new Vector2(0, 0);
	}
	
	public static class SpawnConfiguration {
		public final AnimationModel model;	
		public float speedScaling = 1;
		
		public static final float DEFAULT_SPEED = 3;
		
		public SpawnConfiguration(AnimationModel model) {
			this.model = model;
			speedScaling = 1;
		}
	}
}
