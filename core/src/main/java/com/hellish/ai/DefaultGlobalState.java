package com.hellish.ai;

public enum DefaultGlobalState implements EntityState{
	CHECK_ALIVE;

	@Override
	public void enter(StateEntity stateEntity) {
		
	}

	@Override
	public void update(StateEntity stateEntity) {
		if(stateEntity.isDead()) {
			stateEntity.enableGlobalState(false);
			stateEntity.state(DefaultState.DIE, true);
		}
	}

	@Override
	public void exit(StateEntity stateEntity) {
		
	}
}
