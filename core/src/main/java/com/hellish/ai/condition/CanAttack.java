package com.hellish.ai.condition;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.hellish.ai.AiEntity;

public class CanAttack extends Condition{
	@TaskAttribute(required = true)
	public float range;

	@Override
	public boolean condition() {
		return getObject().canAttack(range);
	}
	
	@Override
	public Task<AiEntity> copyTo(Task<AiEntity> task){
		((CanAttack)task).range = range;
		return task;
	}
}
