package com.hellish.ecs.system;

import static com.hellish.ecs.system.EntitySpawnSystem.AI_SENSOR;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AiComponent;
import com.hellish.ecs.component.CollisionComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.TiledComponent;

public class PhysicsSystem extends IteratingSystem implements ContactListener{
	private final World world;
	
	private static final float TIME_STEP = 1/60f;
	private float accumulator;

	public PhysicsSystem(final Main context) {
		super(Family.all(PhysicsComponent.class, ImageComponent.class).get());
		
		world = context.getWorld();
		world.setContactListener(this);
		accumulator = 0;
	}
	
	@Override
	public void update(float deltaTime) {
		if(world.getAutoClearForces()) {
			world.setAutoClearForces(false);
		}  
		super.update(deltaTime);
		accumulator += deltaTime;
		while (accumulator >= TIME_STEP) {
			for (Entity entity : getEntities()) {
				//Phải lưu prevPosition mỗi TIME_STEP. Nếu lưu prevPosition mỗi deltaTime,
				//interpolation sẽ không có tác dụng.
	            final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
	            physicsCmp.prevPosition.set(physicsCmp.body.getPosition());
	        }
			world.step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;
		}
		render(accumulator / TIME_STEP);
		world.clearForces();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		
		//Làm tạm cho player
		//TODO xử lý thứ này cho hợp lý hơn
		if(!physicsCmp.impulse.isZero() && ECSEngine.playerCmpMapper.has(entity)) {
			physicsCmp.body.applyLinearImpulse(physicsCmp.impulse, physicsCmp.body.getWorldCenter(), true);
			physicsCmp.impulse.setZero();
		}
	}

	public void render(final float alpha) {
		for (Entity entity : getEntities()) {
			final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
			final ImageComponent imageCmp = ECSEngine.imageCmpMapper.get(entity);
		
			float prevX = physicsCmp.prevPosition.x;
			float prevY = physicsCmp.prevPosition.y;
			float bodyX = physicsCmp.body.getPosition().x;
			float bodyY = physicsCmp.body.getPosition().y;
			Vector2 offset = physicsCmp.offset;
		
			imageCmp.image.setPosition(
				MathUtils.lerp(prevX, bodyX, alpha) - imageCmp.image.getWidth() * 0.5f - offset.x,
				MathUtils.lerp(prevY, bodyY, alpha) - imageCmp.image.getHeight() * 0.5f - offset.y);   
		}
	}
	
	private Entity getEntity(Fixture fixture) {
	    return (Entity) fixture.getBody().getUserData();
	}

	private boolean isSensorA(Contact contact) {
	    return contact.getFixtureA().isSensor();
	}

	private boolean isSensorB(Contact contact) {
	    return contact.getFixtureB().isSensor();
	}
	
	private boolean isStaticBody(Fixture fixture) {
		return fixture.getBody().getType() == BodyType.StaticBody;
	}
	
	private boolean isDynamicBody(Fixture fixture) {
		return fixture.getBody().getType() == BodyType.DynamicBody;
	}

	@Override
	public void beginContact(Contact contact) {
		final Entity entityA = getEntity(contact.getFixtureA());
		final Entity entityB = getEntity(contact.getFixtureB());
		final ComponentMapper<TiledComponent> tiledCmpMapper = ECSEngine.tiledCmpMapper;
		final ComponentMapper<CollisionComponent> collCmpMapper = ECSEngine.collisionCmpMapper;
		final ComponentMapper<AiComponent> aiCmpMapper = ECSEngine.aiCmpMapper;
		
		final boolean isEntityATiledCollisionSensor = tiledCmpMapper.has(entityA) && isSensorA(contact);
		final boolean isEntityBTiledCollisionSensor = tiledCmpMapper.has(entityB) && isSensorB(contact);
		final boolean isEntityACollisionFixture = collCmpMapper.has(entityA) && !isSensorA(contact);
		final boolean isEntityBCollisionFixture = collCmpMapper.has(entityB) && !isSensorB(contact);
		final boolean isEntityAAiSensor = aiCmpMapper.has(entityA) && isSensorA(contact) 
				&& contact.getFixtureA().getUserData() == AI_SENSOR;
		final boolean isEntityBAiSensor = aiCmpMapper.has(entityB) && isSensorB(contact)
				&& contact.getFixtureB().getUserData() == AI_SENSOR;
		
		if (isEntityATiledCollisionSensor && isEntityBCollisionFixture) {
			tiledCmpMapper.get(entityA).nearbyEntities.add(entityB);	
		}
		if (isEntityACollisionFixture && isEntityBTiledCollisionSensor) {
			tiledCmpMapper.get(entityB).nearbyEntities.add(entityA);
		}
		
		if(isEntityAAiSensor && isEntityBCollisionFixture) {
			aiCmpMapper.get(entityA).nearbyEntities.add(entityB);
		}
		if(isEntityBAiSensor && isEntityACollisionFixture) {
			aiCmpMapper.get(entityB).nearbyEntities.add(entityA);
		}
	}

	@Override
	public void endContact(Contact contact) {
		final Entity entityA = getEntity(contact.getFixtureA());
        final Entity entityB = getEntity(contact.getFixtureB());
        final ComponentMapper<TiledComponent> tiledCmpMapper = ECSEngine.tiledCmpMapper;
		final ComponentMapper<AiComponent> aiCmpMapper = ECSEngine.aiCmpMapper;
		final boolean isEntityATiledCollisionSensor = tiledCmpMapper.has(entityA) && isSensorA(contact);
		final boolean isEntityBTiledCollisionSensor = tiledCmpMapper.has(entityB) && isSensorB(contact);
		final boolean isEntityAAiSensor = aiCmpMapper.has(entityA) && isSensorA(contact) 
				&& contact.getFixtureA().getUserData() == AI_SENSOR;
		final boolean isEntityBAiSensor = aiCmpMapper.has(entityB) && isSensorB(contact)
				&& contact.getFixtureB().getUserData() == AI_SENSOR;
		
		if (isEntityATiledCollisionSensor && !isSensorB(contact)) {
			tiledCmpMapper.get(entityA).nearbyEntities.remove(entityB);	
		}
		if (isEntityBTiledCollisionSensor && !isSensorA(contact)) {
			tiledCmpMapper.get(entityB).nearbyEntities.remove(entityA);
		}
		
		if(isEntityAAiSensor && !isSensorB(contact)) {
			aiCmpMapper.get(entityA).nearbyEntities.remove(entityB);
		}
		if(isEntityBAiSensor && !isSensorA(contact)) {
			aiCmpMapper.get(entityB).nearbyEntities.remove(entityA);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		contact.setEnabled(
			(isStaticBody(contact.getFixtureA()) && isDynamicBody(contact.getFixtureB())) ||
			(isStaticBody(contact.getFixtureB()) && isDynamicBody(contact.getFixtureA()))
		);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
}
