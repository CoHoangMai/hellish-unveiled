package com.hellish.ecs.system;

import static com.hellish.Main.UNIT_SCALE;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.TextComponent;

public class TextSystem extends IteratingSystem{
	private final Stage gameStage;
	private final Stage uiStage;
	
	private final Vector2 uiLocation;
	
	public TextSystem(final Main context) {
		super(Family.all(TextComponent.class).get());
		gameStage = context.getGameStage();
		uiStage = context.getUIStage();
		
		uiLocation = new Vector2();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final TextComponent textCmp = ECSEngine.txtCmpMapper.get(entity);
		final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		
		uiLocation.set(physicsCmp.body.getPosition().x - 25 * UNIT_SCALE, physicsCmp.body.getPosition().y + 30 * UNIT_SCALE);
		gameStage.getViewport().project(uiLocation);
		uiStage.getViewport().unproject(uiLocation);
		
		textCmp.label.setPosition(uiLocation.x, uiStage.getViewport().getWorldHeight() - uiLocation.y);
		
		textCmp.label.toBack();
	}
}
