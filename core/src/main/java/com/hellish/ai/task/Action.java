package com.hellish.ai.task;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.hellish.ai.AiEntity;

public abstract class Action extends LeafTask<AiEntity>{
	@Override
	public Task<AiEntity> copyTo(Task<AiEntity> task){
		return task;
	}
}
