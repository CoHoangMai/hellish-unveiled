package com.hellish.ai.steer;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

public class Box2dLocation implements Location<Vector2>{
	
	Vector2 position;
	float orientation;
	
	public Box2dLocation() {
		this.position = new Vector2();
		this.orientation = 0;
	}

	@Override
	public Vector2 getPosition() {
		return position;
	}

	@Override
	public float getOrientation() {
		return orientation;
	}

	@Override
	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return SteeringUtils.vectorToAngle(vector);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		return SteeringUtils.angleToVector(outVector, angle);
	}

	@Override
	public Location<Vector2> newLocation() {
		return new Box2dLocation();
	}
}
