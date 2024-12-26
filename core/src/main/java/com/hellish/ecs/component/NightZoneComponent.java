package com.hellish.ecs.component;

import java.util.HashSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class NightZoneComponent implements Component, Poolable{
	public final HashSet<Entity> triggerEntities;
	
	public NightZoneComponent() {
		triggerEntities = new HashSet<>();
	}

	@Override
	public void reset() {
		triggerEntities.clear();
	}

}
