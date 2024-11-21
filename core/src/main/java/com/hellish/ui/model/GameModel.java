package com.hellish.ui.model;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.event.EntityAggroEvent;
import com.hellish.event.EntityLootEvent;
import com.hellish.event.EntityReviveEvent;
import com.hellish.event.EntityTakeDamageEvent;

public class GameModel extends PropertyChangeSource implements EventListener{
	
	private float playerLife;
	private String enemyType;
	private float enemyLife;
	private String popUpText;
	private Entity lastEnemy;
	
	public GameModel(Stage stage) {
		playerLife = 1;
		enemyType = "";
		enemyLife = 1;
		popUpText = "";
		lastEnemy = new Entity();
		
		stage.addListener(this);
	}
	
	public float getPlayerLife() {
		return playerLife;
	}
	
	private void setPlayerLife(float playerLife) {
		this.playerLife = playerLife;
		notify("playerLife", playerLife);
	}
	
	public String getEnemyType() {
		return enemyType;
	}
	
	private void setEnemyType(String enemyType) {
		this.enemyType = enemyType;
		notify("enemyType", enemyType);
	}
	
	public float getEnemyLife() {
		return enemyLife;
	}
	
	private void setEnemyLife(float enemyLife) {
		this.enemyLife = enemyLife;
		notify("enemyLife", enemyLife);
	}
	
	public String getPopUpText() {
		return popUpText;
	}
	
	private void setPopUpText(String popUpText) {
		this.popUpText = popUpText;
		notify("popUpText", popUpText);
	}
	
	private void updateEnemy(Entity enemy) {
		LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(enemy);
		setEnemyLife(lifeCmp.life / lifeCmp.max);
		if(!lastEnemy.equals(enemy)) {
			lastEnemy = enemy;
			AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(enemy);
			if(aniCmp != null && aniCmp.currentModel != null) {
				setEnemyType(aniCmp.currentModel.getModel());
			}
		}
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof EntityTakeDamageEvent) {
			EntityTakeDamageEvent damageEvent = (EntityTakeDamageEvent) event;
			boolean isPlayer = ECSEngine.playerCmpMapper.has(damageEvent.getEntity());
			
			if(isPlayer) {
				LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(damageEvent.getEntity());
				setPlayerLife(lifeCmp.life / lifeCmp.max);
			} else {
				updateEnemy(damageEvent.getEntity());
			}
		} else if(event instanceof EntityReviveEvent) { 
			EntityReviveEvent reviveEvent = (EntityReviveEvent) event;
			boolean isPlayer = ECSEngine.playerCmpMapper.has(reviveEvent.getEntity());
			
			if(isPlayer) {
				LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(reviveEvent.getEntity());
				setPlayerLife(lifeCmp.life / lifeCmp.max);
			}
			
		} else if(event instanceof EntityLootEvent){
			setPopUpText("Ta vừa lụm được [RED]thứ gì[] đó");
		} else if(event instanceof EntityAggroEvent){
				EntityAggroEvent aggroEvent = (EntityAggroEvent) event;
				updateEnemy(aggroEvent.getAiEntity());
		} else {
			return false;
		}
		return true;
	}
}
