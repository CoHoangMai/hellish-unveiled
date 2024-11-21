package com.hellish.ui.model;

import com.badlogic.ashley.core.Entity;
import com.hellish.ecs.component.ItemComponent.ItemCategory;

public class ItemModel {
	public Entity itemEntity;
	public ItemCategory itemCatergory;
	public String atlasKey;
	public int slotIdx;
	public boolean equipped;

	public ItemModel(Entity itemEntity, ItemCategory category, String uiAtlasKey, int slotIdx, boolean equipped) {
		this.itemEntity = itemEntity;
		this.itemCatergory = category;
		this.atlasKey = uiAtlasKey;
		this.slotIdx = slotIdx;
		this.equipped = equipped;
	}
}
