package com.hellish.ecs.component;

import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.ecs.system.CollisionSpawnSystem.SPAWN_AREA_SIZE;
import static com.hellish.ecs.system.EntitySpawnSystem.COLLISION_BOX;
import static com.hellish.ecs.system.EntitySpawnSystem.HIT_BOX_SENSOR;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ai.steer.Box2dLocation;
import com.hellish.ai.steer.SteeringUtils;
import com.hellish.ai.steer.steerer.Steerer;
import com.hellish.ecs.component.EntitySpawnComponent.SpawnConfiguration;

public class PhysicsComponent implements Component, Poolable, Steerable<Vector2> {
	public Body body;
	
	public Vector2 impulse;
	public Vector2 size;
	public Vector2 offset;
	public Vector2 prevPosition;
	
	//Những thứ về Steering
	public Steerer currentSteerer;
	
	public final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	
	public boolean isSteering;
	
	private float boundingRadius;
	private boolean tagged;

	private float maxLinearSpeed = 1;
	private float maxLinearAcceleration = 8;
	private float maxAngularSpeed = 0;
	private float maxAngularAcceleration = 0;
	
	public boolean wasSteering = false;
	
	private Vector2 collisionOffset;

	public boolean slow;

	
	public PhysicsComponent() {
		body = null;
		impulse = new Vector2();
		size = new Vector2();
		offset = new Vector2();
		prevPosition = new Vector2();
		
		boundingRadius = 0;
		
		collisionOffset = new Vector2();
	}

	@Override
	public void reset() {
		body = null;
		impulse.set(0, 0);
		size.set(0, 0);
		offset.set(0, 0);
		prevPosition.set(0, 0);
		
		boundingRadius = 0;
	}
	
	public static PhysicsComponent physicsCmpFromImgandCfg(Engine engine, World world, Image image, SpawnConfiguration cfg) {
		float x = image.getX();
		float y = image.getY();
		float w = image.getWidth() * cfg.physicsScaling.x;
		float h = image.getHeight() * cfg.physicsScaling.y;
		
		PhysicsComponent physicsCmp = engine.createComponent(PhysicsComponent.class);
		
		physicsCmp.offset.set(cfg.physicsOffset);
		physicsCmp.size.set(w, h);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = cfg.bodyType;
		bodyDef.position.set(x + w * 0.5f + cfg.physicsOffset.x, y + h * 0.5f + cfg.physicsOffset.y);
		bodyDef.fixedRotation = true;	
		bodyDef.allowSleep = false;
		physicsCmp.body = world.createBody(bodyDef);
		
		//hit box
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = (cfg.bodyType != BodyType.StaticBody);
		final PolygonShape pShape = new PolygonShape();
		pShape.setAsBox(w * 0.5f, h * 0.5f);
		fixtureDef.shape = pShape;
		Fixture fixture = physicsCmp.body.createFixture(fixtureDef);
		fixture.setUserData(HIT_BOX_SENSOR);
		pShape.dispose();
		
		if (cfg.bodyType != BodyType.StaticBody) {
			//collision box
		    float collH = h * 0.3f;
		    physicsCmp.collisionOffset.set(0, - h * 0.5f + collH * 0.5f);
		    
		    PolygonShape collisionBoxShape = new PolygonShape();
		    collisionBoxShape.setAsBox(w * 0.5f, collH * 0.5f, physicsCmp.collisionOffset, 0);
		    
		    Fixture collisionFixture = physicsCmp.body.createFixture(collisionBoxShape, 0.0f);
		    collisionFixture.setUserData(COLLISION_BOX);    
		    physicsCmp.boundingRadius = Math.max(w, collH);

		    collisionBoxShape.dispose();
		}
		
		return physicsCmp;
	}

	public static PhysicsComponent physicsCmpFromShape2D(Engine engine, World world, float x, float y, MapObject mapObj) {
		Shape2D shape = null;
		
		if (mapObj instanceof RectangleMapObject) {
	        shape = ((RectangleMapObject) mapObj).getRectangle();
	    } else {
	        throw new GdxRuntimeException("MapObject shape không được hỗ trợ: " + mapObj);
	    }
		
		final Rectangle rectangle = (Rectangle)shape;
		final float bodyX = x + rectangle.x * UNIT_SCALE;
        final float bodyY = y + rectangle.y * UNIT_SCALE;
        final float bodyW = rectangle.width * UNIT_SCALE;
        final float bodyH = rectangle.height * UNIT_SCALE;
		
		PhysicsComponent physicsCmp = engine.createComponent(PhysicsComponent.class);
		
		//Khá là lỗi tbh
	    physicsCmp.boundingRadius = Math.max(bodyW, bodyH);
	    
		physicsCmp.body = world.createBody(new BodyDef() {{
			type = BodyDef.BodyType.StaticBody;
			position.set(bodyX, bodyY);
			fixedRotation = true;
			allowSleep = false;
		}});
		
		ChainShape loopShape = new ChainShape();
		loopShape.createChain(new Vector2[]{
		    new Vector2(0f, 0f),
		    new Vector2(bodyW, 0f),
		    new Vector2(bodyW, bodyH),
		    new Vector2(0f, bodyH),
		    new Vector2(0f, 0f)
		});
		Fixture collisionFixture = physicsCmp.body.createFixture(loopShape, 0.0f);
		collisionFixture.setUserData(COLLISION_BOX);    
		loopShape.dispose();

		FixtureDef spawnAreaFixDef = new FixtureDef();
		PolygonShape spawnAreaShape = new PolygonShape();
		Vector2 center = new Vector2(bodyW * 0.5f, bodyH * 0.5f);
		spawnAreaShape.setAsBox(SPAWN_AREA_SIZE + 1, SPAWN_AREA_SIZE + 1, center, 0);
	    spawnAreaFixDef.shape = spawnAreaShape;
	    spawnAreaFixDef.isSensor = true;
	    physicsCmp.body.createFixture(spawnAreaFixDef);
		
		return physicsCmp;
	}

	public static class PhysicsComponentListener implements ComponentListener<PhysicsComponent> {
		@Override
		public void onComponentAdded(Entity entity, PhysicsComponent component, Stage stage, World world) {
			component.body.setUserData(entity);
		}

		@Override
		public void onComponentRemoved(Entity entity, PhysicsComponent component) {
			Body body = component.body;
			body.getWorld().destroyBody(body);
			body.setUserData(null);
		}
	}

	public void startSteering() {
		wasSteering = true;
		if(currentSteerer != null) {
			currentSteerer.startSteering();
		}
	}

	public void stopSteering(boolean clearLinearVelocity) {
		wasSteering = false;
		body.setAngularVelocity(0);
		
		if(currentSteerer != null) {
			clearLinearVelocity = currentSteerer.stopSteering();
		}
		
		currentSteerer = null;
		steeringOutput.setZero();
		if(clearLinearVelocity) {
			body.setLinearVelocity(Vector2.Zero);
		}
	}

	public void applySteering(SteeringAcceleration<Vector2> steeringOutput, float deltaTime) {
		//Cập nhật vận tốc
		body.setLinearVelocity(body.getLinearVelocity().mulAdd(steeringOutput.linear, deltaTime)
				.limit(getMaxLinearSpeed()));
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition().cpy().add(collisionOffset);
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public void setOrientation(float orientation) {
		body.setTransform(getPosition(), orientation);
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return SteeringUtils.vectorToAngle(vector);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		return SteeringUtils.angleToVector(outVector, angle);
	}

	@Override
	public Location<Vector2> newLocation() {
		return new Box2dLocation();
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		return 0.001f;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}
}