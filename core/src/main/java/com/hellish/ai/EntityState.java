package com.hellish.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public interface EntityState extends State<AiEntity>{
	@Override
	public void enter (AiEntity aiEntity);
	
	@Override
	public void update (AiEntity aiEntity);
	
	@Override
	public void exit (AiEntity aiEntity);
	
	@Override
	default public boolean onMessage (AiEntity aiEntity, Telegram telegram) {
		return false;
	}
}
