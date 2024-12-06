package com.hellish.ecs.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

import box2dLight.Light;
import com.badlogic.ashley.core.Entity;




public class LightComponent implements Component, Poolable{
    public ClosedFloatingPointRange distance = new ClosedFloatingPointRange(2f, 3.5f);
    private float distanceTime = 0f;
    private int distanceDirection = -1;
    public Light light;

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    @Override
    public void reset() {
        // Reset the light to its default state
        if (light != null) {
            light.remove(); // Example: Remove light from rendering
        }
        light = null; // Reset light reference
    }

    public float[] getDistance() {
        return new float[]{distance.getStart(), distance.getEnd()};
    }

    public void setDistance(ClosedFloatingPointRange distance) {
        this.distance = distance;
    }

    public float getDistanceTime() {
        return distanceTime;
    }

    public void setDistanceTime(float distanceTime) {
        this.distanceTime = distanceTime;
    }

    public int getDistanceDirection() {
        return distanceDirection;
    }

    public void setDistanceDirection(int distanceDirection) {
        this.distanceDirection = distanceDirection;
    }

    public static final short b2dPlayer = 2;
    public static final short b2dSlime = 4;
    public static final short b2dEnvironment = 8;
    public static final Color lightColor = new Color(1f, 1f, 1f, 0.7f);

    public static class LightComponentListener implements ComponentListener<LightComponent> {
        @Override
        public void onComponentAdded(Entity entity, LightComponent component, Stage stage, World world) {
            // Handle component addition if needed
        }
    
        @Override
        public void onComponentRemoved(Entity entity, LightComponent component) {
            // Cleanup when the component is removed
            component.getLight().remove();
        }
    }

}
