package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.Box2DComponent;

public class LightSystem extends IteratingSystem{

	public LightSystem() {
		super(Family.all(Box2DComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final Box2DComponent b2dComponent = ECSEngine.b2dCmpMapper.get(entity);
		if (b2dComponent.light != null && b2dComponent.lightFluctuationDistance > 0) {
			b2dComponent.lightFluctuationTime += b2dComponent.lightFluctuationSpeed * deltaTime;
			if (b2dComponent.lightFluctuationTime > MathUtils.PI2) {
				b2dComponent.lightFluctuationTime = 0;
			}
			b2dComponent.light.setDistance(b2dComponent.lightDistance + 
					MathUtils.sin(b2dComponent.lightFluctuationTime) * b2dComponent.lightFluctuationDistance);	
		}
	}

}
