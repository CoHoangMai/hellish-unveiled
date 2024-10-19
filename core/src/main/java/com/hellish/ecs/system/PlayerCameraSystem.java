package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
		gameCamera.position.set(ECSEngine.b2dCmpMapper.get(entity).renderPosition, 0);
		gameCamera.update();
	}
	
}
