package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.FloatingTextComponent;

public class FloatingTextSystem extends IteratingSystem{
	private final Stage gameStage;
	private final Stage uiStage;
	private final Vector2 uiLocation;
	private final Vector2 uiTarget;
	
	public FloatingTextSystem(final Main context) {
		super(Family.all(FloatingTextComponent.class).get());
		
		gameStage = context.getGameStage();
		uiStage = context.getUIStage();
		
		uiLocation = new Vector2();
		uiTarget = new Vector2();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final FloatingTextComponent textCmp = ECSEngine.floatTxtCmpMapper.get(entity);
		if(textCmp.time >= textCmp.lifeSpan) {
			getEngine().removeEntity(entity);
			return;
		}
		
		uiLocation.set(textCmp.txtLocation);
		gameStage.getViewport().project(uiLocation);
		uiStage.getViewport().unproject(uiLocation);
		
		uiTarget.set(textCmp.txtTarget);
		gameStage.getViewport().project(uiTarget);
		uiStage.getViewport().unproject(uiTarget);
		
		uiLocation.lerp(uiTarget, Interpolation.elasticOut.apply(Math.min(textCmp.time / textCmp.lifeSpan, 1)));
		textCmp.label.setPosition(uiLocation.x, uiStage.getViewport().getWorldHeight() - uiLocation.y);
		
		textCmp.time += deltaTime;
		
		textCmp.label.toBack();
	}
}
