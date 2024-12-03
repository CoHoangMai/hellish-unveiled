package com.hellish.ai.steer.steerer;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.ecs.component.PhysicsComponent;

public class PursueSteerer extends RaycastObstacleAvoidanceSteererBase{
	final Pursue<Vector2> pursueSB;
	
	boolean keepPursuing;
	
	public PursueSteerer(PhysicsComponent steerable, World world) {
		super(steerable, world);
		
		this.pursueSB = new Pursue<Vector2>(steerable, null, 10);
		
		this.prioritySteering.add(pursueSB);
	}
	
	public Steerable<Vector2> getTarget(){
		return pursueSB.getTarget();
	}
	
	public void setTarget(Steerable<Vector2> target) {
		pursueSB.setTarget(target);
	}
	
	public void startPursuing() {
		steerable.currentSteerer = this;
		keepPursuing = true;
	}

	public void stopPursuing() {
		keepPursuing = false;
	}

	@Override
	public boolean processSteering(SteeringAcceleration<Vector2> steering) {
		return keepPursuing;
	}

	@Override
	public void startSteering() {
		
	}

	@Override
	public boolean stopSteering() {
		return true;
	}

}
