package com.hellish.ai.steer.steerer;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.ai.steer.Box2dRadiusProximity;
import com.hellish.ecs.component.PhysicsComponent;

public abstract class CollisionAvoidanceSteererBase extends Steerer{
	protected final CollisionAvoidance<Vector2> collisionAvoidanceSB;
	protected final Box2dRadiusProximity proximity;
	
	protected final PrioritySteering<Vector2> prioritySteering;
	
	public CollisionAvoidanceSteererBase(PhysicsComponent steerable, World world) {
		super(steerable);
		
		this.proximity = new Box2dRadiusProximity(steerable, world, steerable.getBoundingRadius() * 3);
		this.collisionAvoidanceSB = new CollisionAvoidance<Vector2>(steerable, proximity);
		
		this.prioritySteering = new PrioritySteering<Vector2>(steerable, 0.001f).add(collisionAvoidanceSB);
	}
	
	@Override
	public SteeringBehavior<Vector2> getSteeringBehavior() {
		return prioritySteering;
	}
}