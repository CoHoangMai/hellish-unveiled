package com.hellish.ai.steer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Box2dRadiusProximity extends Box2dSquareAABBProximity{

	public Box2dRadiusProximity(Steerable<Vector2> owner, World world, float detectionRadius) {
		super(owner, world, detectionRadius);
	}
	
	@Override
	protected boolean accept (Steerable<Vector2> steerable) {
		float range = detectionRadius + steerable.getBoundingRadius();
		float distanceSquare = steerable.getPosition().dst2(owner.getPosition());

		return distanceSquare <= range * range;
	}
}