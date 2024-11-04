package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
		public final float speedScaling;
		public final Vector2 physicsScaling;
		public final Vector2 physicsOffset;
		public final BodyType bodyType;
		
		
		public static final float DEFAULT_SPEED = 3;
		
		public SpawnConfiguration(AnimationModel model) {
			this.model = model;
			speedScaling = 1;
			physicsScaling = new Vector2(1, 1);
			physicsOffset = new Vector2(0, 0);
			bodyType = BodyType.DynamicBody;
		}
		
		public SpawnConfiguration(AnimationModel model, float speedScaling, Vector2 physicsScaling, 
				Vector2 physicsOffset, BodyType bodyType) {
			this.model = model;
			this.speedScaling = speedScaling;
			this.physicsScaling = physicsScaling;
			this.physicsOffset = physicsOffset;
			this.bodyType = bodyType;
		}
	}
}
