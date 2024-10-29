package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PhysicComponent implements Component, Poolable{
	public Body body;
	public Vector2 size = new Vector2();
	public Vector2 prevPosition = new Vector2();

	@Override
	public void reset() {
		// TODO Thêm nốt thuộc tính
		size.set(0, 0);
		prevPosition.set(0, 0);		
	}
	
	public static PhysicComponent physicCmpFromImage(World world, Image image, BodyType bodyType) {
		//TODO xem có reuse được BODY_DEF VÀ FIXTURE_DEF của Main không
		float x = image.getX();
		float y = image.getY();
		float w = image.getWidth();
		float h = image.getHeight();
		
		PhysicComponent physicCmp = new PhysicComponent();
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set(x + w*0.5f, y + h*0.5f);
		bodyDef.fixedRotation = true;
		bodyDef.allowSleep = false;
		physicCmp.body = world.createBody(bodyDef);
		
		return physicCmp;
	}
	
	public static class PhysicComponentListener implements ComponentListener<PhysicComponent> {
		@Override
		public void onComponentAdded(Entity entity, PhysicComponent component) {
			component.body.setUserData(entity);
		}

		@Override
		public void onComponentRemoved(Entity entity, PhysicComponent component) {
			Body body = component.body;
			body.getWorld().destroyBody(body);
			body.setUserData(null);
		}
		
	}

}
