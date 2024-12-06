package com.hellish.ecs.component;

public class ClosedFloatingPointRange {
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