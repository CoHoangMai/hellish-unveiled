package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.Box2DComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.input.GameKeyInputListener;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;

public class PlayerMovementSystem extends IteratingSystem implements GameKeyInputListener{
	private int xFactor;
	private int yFactor;
	
	public PlayerMovementSystem(final Main context) {
		super(Family.all(PlayerComponent.class, Box2DComponent.class).get());
		context.getInputManager().addInputListener(this);
		xFactor = yFactor = 0;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
			final PlayerComponent playerComponent = ECSEngine.playerCmpMapper.get(entity);
			final Box2DComponent b2dComponent = ECSEngine.b2dCmpMapper.get(entity);
			b2dComponent.prevPosition.set(b2dComponent.body.getPosition());
			
			b2dComponent.body.applyLinearImpulse(
				(xFactor*playerComponent.speed.x - b2dComponent.body.getLinearVelocity().x) * b2dComponent.body.getMass(),
				(yFactor*playerComponent.speed.y - b2dComponent.body.getLinearVelocity().y) * b2dComponent.body.getMass(),
				b2dComponent.body.getWorldCenter().x, b2dComponent.body.getWorldCenter().y, true
			);
	}

	@Override
	public void keyPressed(InputManager manager, GameKeys key) {
		switch (key) {
		case LEFT:
			xFactor = manager.isKeyPressed(GameKeys.RIGHT) ? 0 : -1;
			break;
		case RIGHT:
			xFactor = manager.isKeyPressed(GameKeys.LEFT) ? 0 : 1;
			break;
		case UP:
			yFactor = manager.isKeyPressed(GameKeys.DOWN) ? 0 : 1;
			break;
		case DOWN:
			yFactor = manager.isKeyPressed(GameKeys.UP) ? 0 : -1;
			break;
		default:
			return;
		}
	}

	@Override
	public void keyUp(InputManager manager, GameKeys key) {
		switch (key) {
		case LEFT:
			xFactor = manager.isKeyPressed(GameKeys.RIGHT) ? 1 : 0;
			break;
		case RIGHT:
			xFactor = manager.isKeyPressed(GameKeys.LEFT) ? -1 : 0;
			break;
		case UP:
			yFactor = manager.isKeyPressed(GameKeys.DOWN) ? -1 : 0;
			break;
		case DOWN:
			yFactor = manager.isKeyPressed(GameKeys.UP) ? 1 : 0;
			break;
		default:
			break;
		}
	}
	
}
