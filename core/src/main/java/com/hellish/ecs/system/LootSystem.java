package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.LootComponent;
import com.hellish.event.EntityLootEvent;
import com.hellish.event.EventUtils;

public class LootSystem extends IteratingSystem{
	private final ComponentManager componentManager;
	private final Stage gameStage;
	
	public LootSystem(final Main context) {
		super(Family.all(LootComponent.class).get());
		
		componentManager = context.getComponentManager();
		gameStage = context.getGameStage();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final LootComponent lootCmp = ECSEngine.lootCmpMapper.get(entity);
		if(lootCmp.interactEntity == null) {
			return;
		}
		final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
		aniCmp.nextAnimation(AnimationType.OPEN);
		aniCmp.mode = Animation.PlayMode.NORMAL;
		
		EventUtils.fireEvent(gameStage, EntityLootEvent.pool, event -> {});
		
		entity.getComponents().forEach(component -> {
			componentManager.notifyComponentRemoved(entity, lootCmp);
		});
		entity.remove(LootComponent.class);
	}
}
