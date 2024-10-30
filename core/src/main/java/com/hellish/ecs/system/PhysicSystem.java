package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.PhysicComponent;
import java.util.logging.Logger;

public class PhysicSystem extends IteratingSystem{
	private final World world;
	
	//private static final float TIME_STEP = 1 / 60f;
	private static final Logger LOG = Logger.getLogger(PhysicSystem.class.getName());
	
	public PhysicSystem(final Main context) {
		super(Family.all(PhysicComponent.class, ImageComponent.class).get());
		
		world = context.getWorld();
	}
	
	@Override
	public void update(float deltaTime) {
		if(world.getAutoClearForces()) {
			LOG.severe("AutoClearForces được đặt thành false để đảm bảo physic step chính xác.");
			world.setAutoClearForces(false);
		}
		super.update(deltaTime);
		world.step(deltaTime, 6, 2);
		world.clearForces();
		
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final PhysicComponent physicCmp = ECSEngine.physicCmpMapper.get(entity);
		final ImageComponent imageCmp = ECSEngine.imageCmpMapper.get(entity);
		
		if(!physicCmp.impulse.isZero()) {
			physicCmp.body.applyLinearImpulse(physicCmp.impulse, physicCmp.body.getWorldCenter(), true);
			physicCmp.impulse.setZero();
		}
		
		imageCmp.image.setPosition(physicCmp.body.getPosition().x - imageCmp.image.getWidth() * 0.5f,
				physicCmp.body.getPosition().y - imageCmp.image.getHeight() * 0.5f);
	}

}
