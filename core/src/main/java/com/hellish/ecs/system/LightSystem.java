package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import box2dLight.RayHandler;

import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.LightComponent;
import com.hellish.event.LightChangeEvent;

public class LightSystem extends IteratingSystem implements EventListener{
    private static final Interpolation distanceInterpolation = Interpolation.smoother;
	private static final Color dayLightColor = Color.WHITE;
    private static final Color nightLightColor = Color.ROYAL;
    
    private final RayHandler rayHandler;

    private float ambientTransitionTime = 1f;
    private Color ambientColor = new Color(1f, 1f, 1f, 1f);
    private Color ambientFrom = dayLightColor;
    private Color ambientTo = nightLightColor;
    
    private boolean isNight = false;

    public LightSystem(final Main context) {
        super(Family.all(LightComponent.class).get());
        rayHandler = context.getRayHandler();
        ambientColor.set(dayLightColor); // Tạm mặc định là ban ngày
        rayHandler.setAmbientLight(ambientColor);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (ambientTransitionTime < 1f) {
            ambientTransitionTime = Math.min(ambientTransitionTime + deltaTime * 0.5f, 1f);

            ambientColor.r = distanceInterpolation.apply(ambientFrom.r, ambientTo.r, ambientTransitionTime);
            ambientColor.g = distanceInterpolation.apply(ambientFrom.g, ambientTo.g, ambientTransitionTime);
            ambientColor.b = distanceInterpolation.apply(ambientFrom.b, ambientTo.b, ambientTransitionTime);
            ambientColor.a = distanceInterpolation.apply(ambientFrom.a, ambientTo.a, ambientTransitionTime);

            rayHandler.setAmbientLight(ambientColor);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LightComponent lightCmp = ECSEngine.lightCmpMapper.get(entity);

        float[] distance = lightCmp.getDistance();
        float time = lightCmp.getDistanceTime();
        float direction = lightCmp.getDistanceDirection();
        box2dLight.Light b2dLight = lightCmp.light;

        // Update light distance time
        lightCmp.setDistanceTime(MathUtils.clamp(time + direction * deltaTime, 0f, 1f));
        if (lightCmp.getDistanceTime() == 0f || lightCmp.getDistanceTime() == 1f) {
            // Change light expansion direction
            lightCmp.setDistanceDirection(lightCmp.getDistanceDirection() * -1);
        }

        // Apply interpolated distance to light
        b2dLight.setDistance(distanceInterpolation.apply(distance[0], distance[1], lightCmp.getDistanceTime()));
    }

	@Override
	public boolean handle(Event event) {
		if(event instanceof LightChangeEvent) {
			 ambientTransitionTime = 0f;
			 
			 ambientFrom = ambientColor;
			 if(isNight) {
				 ambientTo = dayLightColor;
			 } else {
				 ambientTo = nightLightColor;
			 }
			
			 isNight = !isNight;
		} else {
			return false;
		}
		return true;
	}
}
