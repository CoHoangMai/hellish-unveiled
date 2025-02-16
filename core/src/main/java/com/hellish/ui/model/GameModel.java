package com.hellish.ui.model;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.CooldownComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.event.EntityLootEvent;
import com.hellish.event.EntityReviveEvent;
import com.hellish.event.CooldownChangeEvent;
import com.hellish.event.EntityLifeChangeEvent;

public class GameModel extends PropertyChangeSource implements EventListener{
	private String popUpText;
	
	public GameModel(Stage stage) {
		popUpText = "";
		
		stage.addListener(this);
	}
	
	private void setPlayerLife(float currentLife, float maxLife, Float duration) {
		notify("playerLife", currentLife, maxLife, duration);
	}
	
	private void setPlayerCooldown(float percentage, Float duration) {
		notify("playerCooldown", percentage, duration);
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
		if(event instanceof EntityLifeChangeEvent) {
			EntityLifeChangeEvent lifeChangeEvent = (EntityLifeChangeEvent) event;
			
			if(ECSEngine.playerCmpMapper.has(lifeChangeEvent.getEntity())) {
				LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(lifeChangeEvent.getEntity());
				setPlayerLife(lifeCmp.life, lifeCmp.max, lifeChangeEvent.getDuration());
			}
		} else if(event instanceof CooldownChangeEvent) {
			CooldownChangeEvent cdEvent = (CooldownChangeEvent) event;
			
			if(ECSEngine.playerCmpMapper.has(cdEvent.getEntity())) {
				CooldownComponent cdCmp = ECSEngine.cooldownCmpMapper.get(cdEvent.getEntity());
				setPlayerCooldown(cdCmp.current / cdCmp.max, cdEvent.getDuration());
			}
		} else if(event instanceof EntityReviveEvent) { 
			EntityReviveEvent reviveEvent = (EntityReviveEvent) event;
			boolean isPlayer = ECSEngine.playerCmpMapper.has(reviveEvent.getEntity());
			
			if(isPlayer) {
				LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(reviveEvent.getEntity());
				setPlayerLife(lifeCmp.life, lifeCmp.max, 0.5f);
				setPlayerCooldown(1f, 0.5f);
			}
			
		} else if(event instanceof EntityLootEvent){
			setPopUpText("[BLACK]Bạn đã mở ra [RED]một cái rương trống không...");
		} else {
			return false;
		}
		return true;
	}
}
