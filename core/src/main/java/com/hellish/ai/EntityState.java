package com.hellish.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public interface EntityState extends State<StateEntity>{
	@Override
	public void enter (StateEntity stateEntity);
	
	@Override
	public void update (StateEntity stateEntity);
	
	@Override
	public void exit (StateEntity stateEntity);
	
	@Override
	default public boolean onMessage (StateEntity stateEntity, Telegram telegram) {
		return false;
	}
}
