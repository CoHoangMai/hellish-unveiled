package com.hellish.ai.steer.steerer;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.ai.steer.Box2dRaycastCollisionDetector;
import com.hellish.ecs.component.PhysicsComponent;

public abstract class RaycastObstacleAvoidanceSteererBase extends Steerer{
	protected final RaycastObstacleAvoidance<Vector2> obstacleAvoidanceSB;
	
	protected final PrioritySteering<Vector2> prioritySteering;
	
	public RaycastObstacleAvoidanceSteererBase(PhysicsComponent steerable, World world) {
		super(steerable);
		
		RaycastCollisionDetector<Vector2> raycastCollisionDetector = new Box2dRaycastCollisionDetector(world);
		CentralRayWithWhiskersConfiguration<Vector2> rayConfiguration = 
				new CentralRayWithWhiskersConfiguration<Vector2>(steerable, 2, 1, 60 * MathUtils.degreesToRadians);
		this.obstacleAvoidanceSB = new RaycastObstacleAvoidance<Vector2>(steerable, rayConfiguration, raycastCollisionDetector, 0.5f);
		
		this.prioritySteering = new PrioritySteering<Vector2>(steerable, 0.001f).add(obstacleAvoidanceSB);
	}

	@Override
	public SteeringBehavior<Vector2> getSteeringBehavior() {
		return prioritySteering;
	}
	
	public CentralRayWithWhiskersConfiguration<Vector2> getRayConfiguration() {
	    return (CentralRayWithWhiskersConfiguration<Vector2>) obstacleAvoidanceSB.getRayConfiguration();
	}
}
