package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.Box2DComponent;
import com.hellish.ecs.component.PlayerComponent;

public class PlayerCameraSystem extends IteratingSystem{
	private final OrthographicCamera gameCamera;

	public PlayerCameraSystem(final Main context) {
		super(Family.all(PlayerComponent.class, Box2DComponent.class).get());
		gameCamera = context.getGameCamera();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final Box2DComponent b2dCmp = ECSEngine.b2dCmpMapper.get(entity);
		
		float lerpFactor = 0.1f;
		gameCamera.position.x = MathUtils.lerp(gameCamera.position.x, b2dCmp.body.getPosition().x, lerpFactor);
		gameCamera.position.y = MathUtils.lerp(gameCamera.position.y, b2dCmp.body.getPosition().y, lerpFactor);
		gameCamera.update();
	}
}
