package com.hellish.ecs.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

import box2dLight.Light;

public class LightComponent implements Component, Poolable{
    public static final short PLAYER_BIT = 1 << 0; // = 2
    public static final short ENEMY_BIT = 1 << 1; // = 4
    public static final short ENVIRONMENT_BIT = 1 << 2; // = 8
    public static final Color LIGHT_COLOR = new Color(1f, 1f, 1f, 0.7f);
    
    public ClosedFloatingPointRange distance;
    private float distanceTime;
    private int distanceDirection;
    public Light light;
    
    public LightComponent() {
    	distance = new ClosedFloatingPointRange(2, 3.5f);
    	distanceTime = 0;
    	distanceDirection = -1;
    	light = null;
    }
    
    @Override
    public void reset() {
    	distance = null;
    	distanceTime = 0;
    	distanceDirection = -1;
    	light.remove();
        light = null;
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
    
    public static class ClosedFloatingPointRange {
        private final float start;
        private final float end;

        public ClosedFloatingPointRange(float start, float end) {
            if (start > end) {
                throw new IllegalArgumentException("Start must be less than or equal to end");
            }
            this.start = start;
            this.end = end;
        }

        public float getStart() {
            return start;
        }

        public float getEnd() {
            return end;
        }

        public boolean contains(float value) {
            return value >= start && value <= end;
        }

        public float clamp(float value) {
            if (value < start) return start;
            if (value > end) return end;
            return value;
        }

        @Override
        public String toString() {
            return "ClosedFloatingPointRange{" +
                   "start=" + start +
                   ", end=" + end +
                   '}';
        }
    }
}
