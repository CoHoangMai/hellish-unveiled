package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.LootComponent;

public class LootSystem extends IteratingSystem{
	private final ComponentManager componentManager;
	
	public LootSystem(final Main context) {
		super(Family.all(LootComponent.class).get());
		
		componentManager = context.getComponentManager();
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
		
		entity.getComponents().forEach(component -> {
			componentManager.notifyComponentRemoved(entity, lootCmp);
		});
		entity.remove(LootComponent.class);
	}
}
