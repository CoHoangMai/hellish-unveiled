package com.hellish.ecs.component;

import java.util.HashSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.map.MapType;

public class PortalComponent implements Component, Poolable{
	public int id;
	public MapType toMap;
	public int toPortal;
	public HashSet<Entity> triggerEntities;
	
	public PortalComponent() {
		id = -1;
		toMap = MapType.NO_MAP;
		toPortal = -1;
		triggerEntities = new HashSet<>();
	}

	@Override
	public void reset() {
		id = -1;
		toMap = MapType.NO_MAP;
		toPortal = -1;
		triggerEntities.clear();
	}
}
