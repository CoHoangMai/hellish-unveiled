package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ecs.component.AnimationComponent.AnimationModel;

public class EntitySpawnComponent implements Component, Poolable{
	public String type;
	public Vector2 location;
	
	public EntitySpawnComponent() {
		type = "";
		location = new Vector2(0, 0);
	}

	@Override
	public void reset() {
		type = "";
		location.set(0, 0);
	}
	
	public static class SpawnConfiguration {
		public final AnimationModel model;	
		public final float speedScaling;
		public final float accelerationScaling;
		public final boolean canAttack;
		public final float attackScaling;
		public final float attackDelay;
		public final float attackExtraRange;
		public final boolean canFire;
		public final float lifeScaling;
		public final boolean lootable;
		public final String aiTreePath;
		public final Vector2 physicsScaling;
		public final Vector2 physicsOffset;
		public final BodyType bodyType;
		public final boolean hasLight;
		public final short physicsCategory;
		
		public static final float DEFAULT_MAX_SPEED = 3;
		public static final float DEFAULT_MAX_ACCELERATION = 24;
		public static final float DEFAULT_ATTACK_DAMAGE = 80;
		public static final int DEFAULT_LIFE = 200;
		
		public SpawnConfiguration(Builder builder) {
			this.model = builder.model;
			this.speedScaling = builder.speedScaling;
			this.accelerationScaling = builder.accelerationScaling;
			this.canAttack = builder.canAttack;
			this.attackScaling = builder.attackScaling;
			this.attackDelay = builder.attackDelay;
			this.attackExtraRange = builder.attackExtraRange;
			this.canFire = builder.canFire;
			this.lifeScaling = builder.lifeScaling;
			this.lootable = builder.lootable;
			this.aiTreePath = builder.aiTreePath;
			this.physicsScaling = builder.physicsScaling;
			this.physicsOffset = builder.physicsOffset;
			this.bodyType = builder.bodyType;
			this.hasLight = builder.hasLight;
			this.physicsCategory = builder.physicsCategory;
		}
		
		public static class Builder {
			//Cố chấp để hết public cho đơn giản
			public AnimationModel model;
			
			public float speedScaling = 1;
			public float accelerationScaling = 1;
			
			public boolean canAttack = true;
			public float attackScaling = 1;
			public float attackDelay = 0.2f;
			public float attackExtraRange = 0;
			public boolean canFire = false;
			
			public float lifeScaling = 1;
			public boolean lootable = false;
			public String aiTreePath = "";
			public Vector2 physicsScaling = new Vector2(1, 1);
			public Vector2 physicsOffset = new Vector2(0, 0);
			public BodyType bodyType = BodyType.DynamicBody;
			public boolean hasLight = false;
			public short physicsCategory = 0;

			public Builder(AnimationModel model) {
				if (model == null) {
					throw new GdxRuntimeException("AnimationModel không thể là null");
				}
				this.model = model;
			}
		}
	}
}
