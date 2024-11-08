package com.hellish.ai.condition;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.hellish.ai.AiEntity;

public abstract class Condition extends LeafTask<AiEntity>{
	public abstract boolean condition();
	
	@Override
	public Status execute() {
		return condition() ? Status.SUCCEEDED : Status.FAILED;
	}
	
	@Override
	public Task<AiEntity> copyTo(Task<AiEntity> task){
		return task;
	}
}
