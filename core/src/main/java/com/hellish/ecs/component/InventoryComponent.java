package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ecs.component.ItemComponent.ItemType;

public class InventoryComponent implements Component, Poolable{
	public static final int INVENTORY_CAPACITY = 18;
	public final Array<Entity> items;
	public final Array<ItemType> itemsToAdd;
	
	public InventoryComponent() {
		items = new Array<Entity>();
		itemsToAdd = new Array<ItemType>();
	}

	@Override
	public void reset() {
		items.clear();
		itemsToAdd.clear();
	}
}
