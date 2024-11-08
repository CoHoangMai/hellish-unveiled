package com.hellish.ai.condition;

public class IsEnemyNearby extends Condition{
	@Override
	public boolean condition() {
		return getObject().hasNearbyEnemy();
	}

}
