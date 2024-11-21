package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.MoveComponent.Direction;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.event.EntityDirectionChangedEvent;

public class MoveSystem extends IteratingSystem{
	private final Stage stage;

	public MoveSystem(final Main context) {
		super(Family.all(MoveComponent.class, PhysicsComponent.class).get());
		
		stage = context.getGameStage();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final MoveComponent moveCmp = ECSEngine.moveCmpMapper.get(entity);
		final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		float mass = physicsCmp.body.getMass();
		float velX = physicsCmp.body.getLinearVelocity().x;
		float velY = physicsCmp.body.getLinearVelocity().y;
		float speedFactor = (moveCmp.slow) ? 0.4f : 1;
		
		if ((moveCmp.cosine == 0 && moveCmp.sine == 0) || moveCmp.rooted) {
			physicsCmp.impulse.set(mass * (0f - velX), mass * (0f - velY));
			return;
		}
		
		if(moveCmp.direction != getDirection(moveCmp)) {
			moveCmp.direction = getDirection(moveCmp);
			stage.getRoot().fire(new EntityDirectionChangedEvent(entity, moveCmp.direction));
		}
		
		
		physicsCmp.impulse.set(
				moveCmp.speed * speedFactor * moveCmp.cosine - velX, 
				moveCmp.speed * speedFactor * moveCmp.sine - velY);
		
		final ImageComponent imageCmp = ECSEngine.imageCmpMapper.get(entity);
		if(imageCmp != null) {
			if(moveCmp.cosine != 0) {
				imageCmp.image.setFlipX(moveCmp.cosine > 0);
			}
		}
	}
	
	private Direction getDirection(MoveComponent moveCmp) {
		final double threshold = Math.sin(Math.PI / 4);
		
		if(moveCmp.cosine < 0 && Math.abs(moveCmp.sine) <= threshold) {
			return Direction.LEFT;
		}
		if(moveCmp.cosine > 0 && Math.abs(moveCmp.sine) <= threshold) {
			return Direction.RIGHT;
		}
		if(moveCmp.sine > 0 && Math.abs(moveCmp.cosine) <= threshold) {
			return Direction.UP;
		}
		if(moveCmp.sine < 0 && Math.abs(moveCmp.cosine) <= threshold) {
			return Direction.DOWN;
		}
		
		//Trong trường hợp.
		else {
			if(moveCmp.cosine > 0) {
				return Direction.RIGHT;
			} else {
				return Direction.LEFT;
			}
		}
	}
}
