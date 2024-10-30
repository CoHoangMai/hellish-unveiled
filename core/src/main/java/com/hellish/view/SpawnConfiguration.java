package com.hellish.view;

public class SpawnConfiguration {
	public final AnimationModel model;	
	public float speedScaling = 1;
	
	public static final float DEFAULT_SPEED = 3;
	
	public SpawnConfiguration(AnimationModel model) {
		this.model = model;
		speedScaling = 1;
	}
}
