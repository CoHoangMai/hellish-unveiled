package com.hellish.ecs.component;

import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.ecs.system.CollisionSpawnSystem.SPAWN_AREA_SIZE;
import static com.hellish.ecs.system.EntitySpawnSystem.HIT_BOX_SENSOR;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
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
import com.hellish.ecs.component.EntitySpawnComponent.SpawnConfiguration;

public class PhysicsComponent implements Component, Poolable{
	public Body body;
	public Vector2 impulse = new Vector2();
	public Vector2 size = new Vector2();
	public Vector2 offset = new Vector2();
	public Vector2 prevPosition = new Vector2();
	
	public PhysicsComponent() {
		body = null;
		impulse = new Vector2();
		size = new Vector2();
		offset = new Vector2();
		prevPosition = new Vector2();
	}

	@Override
	public void reset() {
		body = null;
		impulse.set(0, 0);
		size.set(0, 0);
		offset.set(0, 0);
		prevPosition.set(0, 0);
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
		    Vector2 collOffset = new Vector2(0, - h * 0.5f + collH * 0.5f);
		    
		    PolygonShape collisionBoxShape = new PolygonShape();
		    collisionBoxShape.setAsBox(w * 0.5f, collH * 0.5f, collOffset, 0);
		    
		    physicsCmp.body.createFixture(collisionBoxShape, 0.0f);

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
		physicsCmp.body.createFixture(loopShape, 0.0f);
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
		public void onComponentAdded(Entity entity, PhysicsComponent component, Stage stage) {
			component.body.setUserData(entity);
		}

		@Override
		public void onComponentRemoved(Entity entity, PhysicsComponent component) {
			Body body = component.body;
			body.getWorld().destroyBody(body);
			body.setUserData(null);
		}
		
	}
}
