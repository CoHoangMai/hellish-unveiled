package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.hellish.Main;
import com.hellish.WorldContactListener;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.GameObjectComponent;
import com.hellish.ecs.component.RemoveComponent;

public class PlayerCollisionSystem extends IteratingSystem implements WorldContactListener.PlayerCollisionListener{

	public PlayerCollisionSystem(final Main context) {
		super(Family.all(RemoveComponent.class).get());
		
		context.getWorldContactListener().addPlayerCollisionListener(this);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		getEngine().removeEntity(entity);
		
	}

	@Override
	public void playerCollision(Entity player, Entity gameObject) {
		final GameObjectComponent gameObjCmp = ECSEngine.gameObjCmpMapper.get(gameObject);
		
		switch (gameObjCmp.type) {
		case GEM:
			gameObject.add(((ECSEngine)getEngine()).createComponent(RemoveComponent.class));
			break;
		case SYMBOL:
			ECSEngine.playerCmpMapper.get(player).hasSymbol = true;
			Gdx.app.debug("PlayerCollision", "Đã nhận được khả năng phá đá từ biểu tượng :))");
			break;
		case ROCK:
			if (ECSEngine.playerCmpMapper.get(player).hasSymbol) {
				gameObject.add(((ECSEngine)getEngine()).createComponent(RemoveComponent.class));
			}
			break;
		}
	}

}
