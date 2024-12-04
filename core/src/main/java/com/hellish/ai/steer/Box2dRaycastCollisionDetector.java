package com.hellish.ai.steer;

import static com.hellish.ecs.system.EntitySpawnSystem.COLLISION_BOX;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.ecs.ECSEngine;

public class Box2dRaycastCollisionDetector implements RaycastCollisionDetector<Vector2> {

	World world;
	Box2dRaycastCallback callback;

	public Box2dRaycastCollisionDetector (World world) {
		this(world, new Box2dRaycastCallback());
	}

	public Box2dRaycastCollisionDetector (World world, Box2dRaycastCallback callback) {
		this.world = world;
		this.callback = callback;
	}

	@Override
	public boolean collides (Ray<Vector2> ray) {
		return findCollision(null, ray);
	}

	@Override
	public boolean findCollision (Collision<Vector2> outputCollision, Ray<Vector2> inputRay) {
		callback.collided = false;
		if (!inputRay.start.epsilonEquals(inputRay.end, MathUtils.FLOAT_ROUNDING_ERROR)) {
			callback.outputCollision = outputCollision;
			world.rayCast(callback, inputRay.start, inputRay.end);
		}
		return callback.collided;
	}

	public static class Box2dRaycastCallback implements RayCastCallback {
		public Collision<Vector2> outputCollision;
		public boolean collided;

		public Box2dRaycastCallback () {
		}

		@Override
		public float reportRayFixture (Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			//Chỉ xét những collision box và không phải của player
			Entity entity = (Entity) fixture.getBody().getUserData();
			if(fixture.getUserData() != null && fixture.getUserData() == COLLISION_BOX
					&& !ECSEngine.playerCmpMapper.has(entity)) {
				if (outputCollision != null) outputCollision.set(point, normal);
				collided = true;
				return fraction;
			}
			return -1;
		}
	}
}
