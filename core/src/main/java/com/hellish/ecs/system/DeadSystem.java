package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.DeadComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.LightComponent;
import com.hellish.ecs.component.TextComponent;
import com.hellish.event.EntityReviveEvent;
import com.hellish.event.LoseEvent;
import com.hellish.event.WinEvent;

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
		final LightComponent lightCmp = ECSEngine.lightCmpMapper.get(entity);
		final TextComponent txtCmp = ECSEngine.txtCmpMapper.get(entity);
		
		if(aniCmp == null) {
			getEngine().removeEntity(entity);
			return;
		}
		
		if(aniCmp.isAnimationFinished()) {
			if(ECSEngine.playerCmpMapper.get(entity) != null) {
            	LoseEvent loseEvent = LoseEvent.pool.obtain();
                stage.getRoot().fire(loseEvent);  
                LoseEvent.pool.free(loseEvent); 
            }
			
			if(deadCmp.reviveTime == null) {
				//Rage, rage against the dying of the light.
				if(lightCmp != null) {
					Color lightColor = lightCmp.light.getColor();
					if(lightColor.a > 0) {
						lightColor.a = Math.max(0, lightColor.a - 1.5f * deltaTime);
						lightCmp.light.setColor(lightColor);
					}
				}
				
				if(txtCmp != null) {
					txtCmp.label.addAction(Actions.sequence(
						Actions.delay(0.5f),
						Actions.fadeOut(0.75f)
					));
				}
				
				imageCmp.image.addAction(Actions.sequence(
					Actions.delay(0.5f),
					Actions.fadeOut(0.75f),
					Actions.run(() -> {
						//Tạm dùng cách này để nhận diện Boss
						if (txtCmp != null) {
		                    WinEvent winEvent = WinEvent.pool.obtain();
		                    stage.getRoot().fire(winEvent);  
		                    WinEvent.pool.free(winEvent); 
		                }
						
						getEngine().removeEntity(entity);
					})
				));		
                
                return;
			}		
			deadCmp.reviveTime -= deltaTime;
			if(deadCmp.reviveTime <= 0) {
				final LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(entity);
				lifeCmp.life = lifeCmp.max;
				
				EntityReviveEvent event = EntityReviveEvent.pool.obtain().set(entity);
				stage.getRoot().fire(event);
				EntityReviveEvent.pool.free(event);
				
				entity.getComponents().forEach(component -> {
					componentManager.notifyComponentRemoved(entity, deadCmp);
				});
				entity.remove(DeadComponent.class);
			}
		}
	}
}
