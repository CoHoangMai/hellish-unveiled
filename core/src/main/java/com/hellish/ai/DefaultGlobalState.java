package com.hellish.ai;

public enum DefaultGlobalState implements EntityState{
	CHECK_ALIVE;

	@Override
	public void enter(AiEntity aiEntity) {
		
	}

	@Override
	public void update(AiEntity aiEntity) {
		if(aiEntity.isDead()) {
			aiEntity.enableGlobalState(false);
			aiEntity.state(DefaultState.DIE, true);
		}
	}

	@Override
	public void exit(AiEntity aiEntity) {
		
	}

}
