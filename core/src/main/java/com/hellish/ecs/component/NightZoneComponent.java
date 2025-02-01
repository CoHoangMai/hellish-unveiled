package com.hellish.ecs.component;

import java.util.HashSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class NightZoneComponent implements Component, Poolable{
	public final HashSet<Entity> entitiesInZone;
	public boolean activated;
	
	public NightZoneComponent() {
		entitiesInZone = new HashSet<>();
		activated = false;
	}

	@Override
	public void reset() {
		entitiesInZone.clear();
		activated = false;
	}

}
