package com.hellish.view;

public enum AnimationModel {
	PLAYER, WOLF, UNDEFINED;
	
	private final String model = this.toString().toLowerCase();

	public String getModel() {
		return model;
	}
}
