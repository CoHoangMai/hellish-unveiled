package com.hellish.ecs.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import box2dLight.RayHandler;
import com.hellish.ecs.component.LightComponent;

public class LightSystem extends IteratingSystem {
    private final ComponentMapper<LightComponent> lightCmps = ComponentMapper.getFor(LightComponent.class);
    private final RayHandler rayHandler;

    private float ambientTransitionTime = 1f;
    private Color ambientColor = new Color(1f, 1f, 1f, 1f);
    private Color ambientFrom = ambientColor;
    private Color ambientTo = Color.CLEAR;
    

    private static Interpolation distanceInterpolation = Interpolation.smoother;
	private static Color dayLightColor = Color.BLACK;
    private static Color nightLightColor = new Color(0f, 0f, 0f, 0.0f);


    public LightSystem(RayHandler rayHandler) {
        super(Family.all(LightComponent.class).get());
        this.rayHandler = rayHandler;
        ambientColor.set(Color.BLACK); // Đặt ánh sáng ambient nhẹ
        rayHandler.setAmbientLight(ambientColor);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Ambient light transition logic
        if (Gdx.input.isKeyJustPressed(Input.Keys.N) && ambientTransitionTime == 1f) {
            ambientTransitionTime = 0f;
            ambientTo = nightLightColor;
            ambientFrom = ambientColor;
            System.out.println("Night");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D) && ambientTransitionTime == 1f) {
            ambientTransitionTime = 0f;
            ambientTo = dayLightColor;
            ambientFrom = ambientColor;
            System.out.println("Day");
        }

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
        LightComponent lightCmp = lightCmps.get(entity);

        float[] distance = lightCmp.getDistance();
        float time = lightCmp.getDistanceTime();
        float direction = lightCmp.getDistanceDirection();
        box2dLight.Light b2dLight = lightCmp.getLight();

        // Update light distance time
        lightCmp.setDistanceTime(MathUtils.clamp(time + direction * deltaTime, 0f, 1f));
        if (lightCmp.getDistanceTime() == 0f || lightCmp.getDistanceTime() == 1f) {
            // Change light expansion direction
            lightCmp.setDistanceDirection(lightCmp.getDistanceDirection() * -1);
        }

        // Apply interpolated distance to light
        b2dLight.setDistance(distanceInterpolation.apply(distance[0], distance[1], lightCmp.getDistanceTime()));
    }
}
