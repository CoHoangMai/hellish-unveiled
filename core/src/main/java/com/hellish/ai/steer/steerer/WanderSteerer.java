package com.hellish.ai.steer.steerer;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.ecs.component.PhysicsComponent;

public class WanderSteerer extends CollisionAvoidanceSteererBase{
	private final Wander<Vector2> wanderSB;
	
	private boolean keepWandering;
	
	public WanderSteerer(PhysicsComponent steerable, World world) {
		super(steerable, world);
		
		this.wanderSB = new Wander<Vector2>(steerable);
		this.wanderSB.setWanderOffset(0)
					.setWanderOrientation(0)
					.setWanderRadius(1)
					.setWanderRate(MathUtils.PI2 * 4);
		
		this.prioritySteering.add(wanderSB);
	}
	
	public void startWandering() {
		steerable.currentSteerer = this;
		keepWandering = true;
	}

	public void stopWandering() {
		keepWandering = false;
	}

	@Override
	public boolean processSteering(SteeringAcceleration<Vector2> steering) {
		return keepWandering;
	}

	@Override
	public void startSteering() {
		
	}

	@Override
	public boolean stopSteering() {
		return true;
	}
}