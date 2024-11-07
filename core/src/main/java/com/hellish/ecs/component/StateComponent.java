package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ai.AiEntity;
import com.hellish.ai.DefaultState;
import com.hellish.ai.EntityState;

public class StateComponent implements Component, Poolable{
	public EntityState nextState = DefaultState.IDLE;
	public DefaultStateMachine<AiEntity, EntityState> stateMachine = new DefaultStateMachine<AiEntity, EntityState>();
	
	@Override
	public void reset() {
		
	}
	
	public static class StateComponentListener implements ComponentListener<StateComponent> {
		@Override
		public void onComponentAdded(Entity entity, StateComponent component) {
			component.stateMachine.setOwner(new AiEntity(entity));
		}

		@Override
		public void onComponentRemoved(Entity entity, StateComponent component) {
			
		}	
	}
}
