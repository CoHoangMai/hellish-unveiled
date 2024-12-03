package com.hellish.ai.steer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.ecs.ECSEngine;

public class Box2dSquareAABBProximity implements Proximity<Vector2>, QueryCallback{
	
	protected Steerable<Vector2> owner;
	protected World world;
	protected ProximityCallback<Vector2> behaviorCallback;
	protected float detectionRadius;
	
	private int neighborCount;

	private static final AABB aabb = new AABB();
	
	public Box2dSquareAABBProximity (Steerable<Vector2> owner, World world, float detectionRadius) {
		this.owner = owner;
		this.world = world;
		this.detectionRadius = detectionRadius;
		this.behaviorCallback = null;
		this.neighborCount = 0;
	}

	@Override
	public Steerable<Vector2> getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Steerable<Vector2> owner) {
		this.owner = owner;
	}
	
	public World getWorld () {
		return world;
	}

	public void setWorld (World world) {
		this.world = world;
	}
	
	public float getDetectionRadius () {
		return detectionRadius;
	}

	public void setDetectionRadius (float detectionRadius) {
		this.detectionRadius = detectionRadius;
	}

	@Override
	public int findNeighbors(ProximityCallback<Vector2> callback) {
		this.behaviorCallback = callback;
		neighborCount = 0;
		prepareAABB(aabb);
		world.QueryAABB(this, aabb.lowerX, aabb.lowerY, aabb.upperX, aabb.upperY);
		this.behaviorCallback = null;
		return neighborCount;
	}
	
	protected void prepareAABB (AABB aabb) {
		Vector2 position = owner.getPosition();
		aabb.lowerX = position.x - detectionRadius;
		aabb.lowerY = position.y - detectionRadius;
		aabb.upperX = position.x + detectionRadius;
		aabb.upperY = position.y + detectionRadius;
	}
	
	protected Steerable<Vector2> getSteerable (Fixture fixture) {
		Entity entity = (Entity) fixture.getBody().getUserData();
		return ECSEngine.physicsCmpMapper.get(entity);
	}

	protected boolean accept (Steerable<Vector2> steerable) {
		return true;
	}
	
	@Override
	public boolean reportFixture(Fixture fixture) {
		Steerable<Vector2> steerable = getSteerable(fixture);
		if (steerable != owner && accept(steerable)) {
			if (behaviorCallback.reportNeighbor(steerable)) neighborCount++;
		}
		return true;
	}
	
	public static class AABB {
		float lowerX;
		float lowerY;
		float upperX;
		float upperY;
	}
}
