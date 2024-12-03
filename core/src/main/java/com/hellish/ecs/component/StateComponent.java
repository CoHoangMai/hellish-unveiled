package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ai.StateEntity;
import com.hellish.ai.DefaultState;
import com.hellish.ai.EntityState;

public class StateComponent implements Component, Poolable{
	public EntityState nextState = DefaultState.IDLE;
	public DefaultStateMachine<StateEntity, EntityState> stateMachine;
	
	public StateComponent() {
		nextState = DefaultState.IDLE;
		stateMachine = new DefaultStateMachine<StateEntity, EntityState>();
	}
	
	@Override
	public void reset() {
		nextState = DefaultState.IDLE;
		stateMachine.changeState(nextState);
	}
	
	public static class StateComponentListener implements ComponentListener<StateComponent> {
		@Override
		public void onComponentAdded(Entity entity, StateComponent component, Stage stage, World world) {
			component.stateMachine.setOwner(new StateEntity(entity));
		}

		@Override
		public void onComponentRemoved(Entity entity, StateComponent component) {
			
		}	
	}
}
