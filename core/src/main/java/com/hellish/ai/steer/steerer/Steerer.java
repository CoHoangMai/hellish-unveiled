package com.hellish.ai.steer.steerer;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.hellish.ecs.component.PhysicsComponent;

public abstract class Steerer {
	protected final PhysicsComponent steerable;
	
	public Steerer(PhysicsComponent steerable) {
		this.steerable = steerable;
	}
	
	public boolean calculateSteering(SteeringAcceleration<Vector2> steering) {
		return processSteering(getSteeringBehavior().calculateSteering(steering));	
	}
	
	public abstract SteeringBehavior<Vector2> getSteeringBehavior();
	
	public abstract boolean processSteering(SteeringAcceleration<Vector2> steering);
	
	public abstract void startSteering();
	
	public abstract boolean stopSteering();
}