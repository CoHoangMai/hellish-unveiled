package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.TiledComponent;
import com.hellish.event.CollisionDespawnEvent;

public class CollisionDespawnSystem extends IteratingSystem{
	private final Stage gameStage;
	
	public CollisionDespawnSystem(final Main context) {
		super(Family.all(TiledComponent.class).get());
		
		gameStage = context.getGameStage();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final TiledComponent tiledCmp = ECSEngine.tiledCmpMapper.get(entity);
		if(tiledCmp!= null && tiledCmp.nearbyEntities.isEmpty()) {
			CollisionDespawnEvent colDespawnEvent = CollisionDespawnEvent.pool.obtain();
			if(tiledCmp.cell != null) {
				colDespawnEvent.cell = tiledCmp.cell;
			}
			if(tiledCmp.object != null) {
				colDespawnEvent.mapObject = tiledCmp.object;
			}
			gameStage.getRoot().fire(colDespawnEvent);
			CollisionDespawnEvent.pool.free(colDespawnEvent);
			
			getEngine().removeEntity(entity);
		}
	}
}
