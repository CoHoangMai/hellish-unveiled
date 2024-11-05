package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.DeadComponent;
import com.hellish.ecs.component.LifeComponent;

public class DeadSystem extends IteratingSystem{
	private ComponentManager componentManager;

	public DeadSystem(final Main context) {
		super(Family.all(DeadComponent.class).get());
		
		componentManager = context.getComponentManager();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final DeadComponent deadCmp = ECSEngine.deadCmpMapper.get(entity);

		if(deadCmp.reviveTime == null) {
			getEngine().removeEntity(entity);
			return;
		}		
		deadCmp.reviveTime -= deltaTime;
		if(deadCmp.reviveTime <= 0) {
			final LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(entity);
			lifeCmp.life = lifeCmp.max;
			
			entity.getComponents().forEach(component -> {
				componentManager.notifyComponentRemoved(entity, deadCmp);
			});
			entity.remove(DeadComponent.class);
		}
	}

}
