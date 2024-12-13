package com.hellish.ui.model;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.event.EntityAggroEvent;
import com.hellish.event.EntityLootEvent;
import com.hellish.event.EntityReviveEvent;
import com.hellish.event.EntityTakeDamageEvent;
import com.hellish.event.GameRestartEvent;

public class GameModel extends PropertyChangeSource implements EventListener{
	
	private float playerLife;
	private String popUpText;
	
	public GameModel(Stage stage) {
		playerLife = 1;
		popUpText = "";
		
		stage.addListener(this);
	}
	
	public float getPlayerLife() {
		return playerLife;
	}
	
	private void setPlayerLife(float playerLife) {
		this.playerLife = playerLife;
		notify("playerLife", playerLife);
	}
	
	public String getPopUpText() {
		return popUpText;
	}
	
	private void setPopUpText(String popUpText) {
		this.popUpText = popUpText;
		notify("popUpText", popUpText);
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof EntityTakeDamageEvent) {
			EntityTakeDamageEvent damageEvent = (EntityTakeDamageEvent) event;
			boolean isPlayer = ECSEngine.playerCmpMapper.has(damageEvent.getEntity());
			
			if(isPlayer) {
				LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(damageEvent.getEntity());
				setPlayerLife(lifeCmp.life / lifeCmp.max);
			}
		} else if(event instanceof EntityReviveEvent) { 
			EntityReviveEvent reviveEvent = (EntityReviveEvent) event;
			boolean isPlayer = ECSEngine.playerCmpMapper.has(reviveEvent.getEntity());
			
			if(isPlayer) {
				LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(reviveEvent.getEntity());
				setPlayerLife(lifeCmp.life / lifeCmp.max);
			}
			
		} else if(event instanceof EntityLootEvent){
			setPopUpText("[BLACK]Ta vừa lụm được [RED]thứ gì đó");
		} else if(event instanceof EntityAggroEvent){
			//Tạm không làm gì
		} else if(event instanceof GameRestartEvent){
			setPlayerLife(1);
		} else {
			return false;
		}
		return true;
	}
}
