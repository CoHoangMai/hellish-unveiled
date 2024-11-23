package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.DeadComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.event.EntityReviveEvent;

public class DeadSystem extends IteratingSystem{
	private final ComponentManager componentManager;
	private final Stage stage;

	public DeadSystem(final Main context) {
		super(Family.all(DeadComponent.class).get());
		
		stage = context.getGameStage();
		componentManager = context.getComponentManager();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final DeadComponent deadCmp = ECSEngine.deadCmpMapper.get(entity);
		final ImageComponent imageCmp = ECSEngine.imageCmpMapper.get(entity);
		final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
		
		if(aniCmp == null) {
			getEngine().removeEntity(entity);
			return;
		}
		
		if(aniCmp.isAnimationFinished()) {
			if(deadCmp.reviveTime == null) {
				imageCmp.image.addAction(Actions.sequence(
					Actions.delay(0.5f),
					Actions.fadeOut(0.75f),
					Actions.run(() -> getEngine().removeEntity(entity))
				));
				return;
			}		
			deadCmp.reviveTime -= deltaTime;
			if(deadCmp.reviveTime <= 0) {
				final LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(entity);
				lifeCmp.life = lifeCmp.max;
				System.out.println("...Nhưng đam mê trêu chó là không thể từ bỏ!!!");
				stage.getRoot().fire(new EntityReviveEvent(entity));
				
				entity.getComponents().forEach(component -> {
					componentManager.notifyComponentRemoved(entity, deadCmp);
				});
				entity.remove(DeadComponent.class);
			}
		}
	}
}
