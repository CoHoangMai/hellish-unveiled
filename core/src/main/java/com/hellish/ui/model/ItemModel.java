package com.hellish.ui.model;

import com.hellish.ecs.component.ItemComponent.ItemCategory;

public class ItemModel {
	public int itemEntityId;
	public ItemCategory itemCatergory;
	public String atlasKey;
	public int slotIdx;
	public boolean equipped;

	public ItemModel(int id, ItemCategory category, String uiAtlasKey, int slotIdx, boolean equipped) {
		this.itemEntityId = id;
		this.itemCatergory = category;
		this.atlasKey = uiAtlasKey;
		this.slotIdx = slotIdx;
		this.equipped = equipped;
	}
}
