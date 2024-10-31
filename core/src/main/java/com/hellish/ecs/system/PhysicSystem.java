package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.PhysicComponent;
import java.util.logging.Logger;

public class PhysicSystem extends IteratingSystem implements ContactListener{
	private final World world;
	
	private static final float TIME_STEP = 1/60f;
	private float accumulator;
	private static final Logger LOG = Logger.getLogger(PhysicSystem.class.getName());
	
	public PhysicSystem(final Main context) {
		super(Family.all(PhysicComponent.class, ImageComponent.class).get());
		
		world = context.getWorld();
		accumulator = 0;
	}
	
	@Override
	public void update(float deltaTime) {
		if(world.getAutoClearForces()) {
			LOG.severe("AutoClearForces được đặt thành false để đảm bảo physic step chính xác.");
			world.setAutoClearForces(false);
		}  
		super.update(deltaTime);
		accumulator += deltaTime;
		while (accumulator >= TIME_STEP) {
			for (Entity entity : getEntities()) {
				//Phải lưu prevPosition mỗi TIME_STEP. Nếu lưu prevPosition mỗi deltaTime,
				//interpolation sẽ không có tác dụng.
	            final PhysicComponent physicCmp = ECSEngine.physicCmpMapper.get(entity);
	            physicCmp.prevPosition.set(physicCmp.body.getPosition());
	        }
			world.step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;
		}
		render(accumulator / TIME_STEP);
		world.clearForces();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final PhysicComponent physicCmp = ECSEngine.physicCmpMapper.get(entity);
		
		if(!physicCmp.impulse.isZero()) {
			physicCmp.body.applyLinearImpulse(physicCmp.impulse, physicCmp.body.getWorldCenter(), true);
			physicCmp.impulse.setZero();
		}
	}
	
	public void render(final float alpha) {
		for (Entity entity : getEntities()) {
			final PhysicComponent physicCmp = ECSEngine.physicCmpMapper.get(entity);
			final ImageComponent imageCmp = ECSEngine.imageCmpMapper.get(entity);
		
			float prevX = physicCmp.prevPosition.x;
			float prevY = physicCmp.prevPosition.y;
			float bodyX = physicCmp.body.getPosition().x;
			float bodyY = physicCmp.body.getPosition().y;
		
			imageCmp.image.setPosition(MathUtils.lerp(prevX, bodyX, alpha) - imageCmp.image.getWidth() * 0.5f,
				MathUtils.lerp(prevY, bodyY, alpha) - imageCmp.image.getHeight() * 0.5f);   
		}
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
