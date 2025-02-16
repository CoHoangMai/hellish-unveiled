package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.CooldownComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.event.CooldownChangeEvent;
import com.hellish.event.EventUtils;

public class CooldownSystem extends IteratingSystem{
	private final Stage stage;

	public CooldownSystem(final Main context) {
		super(Family.all(CooldownComponent.class, AttackComponent.class, PlayerComponent.class).get());
		stage = context.getGameStage();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CooldownComponent cdCmp = ECSEngine.cooldownCmpMapper.get(entity);
		
		float calculatedCd = Math.min(cdCmp.current + cdCmp.regeneration * deltaTime, cdCmp.max);
		
		if(cdCmp.current == calculatedCd) {
			return;
		} else {
			cdCmp.current = calculatedCd;
			EventUtils.fireEvent(stage, CooldownChangeEvent.pool, event -> event.set(entity, null));
		}
	}

}
