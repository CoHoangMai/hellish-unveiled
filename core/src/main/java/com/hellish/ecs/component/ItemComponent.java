package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ItemComponent implements Component, Poolable{
	public ItemType itemType;
	public int slotIdx;
	public boolean equipped;
	
	public ItemComponent() {
		itemType = ItemType.UNDEFINED;
		slotIdx = -1;
		equipped = false; 
    }

	@Override
	public void reset() {
		itemType = ItemType.UNDEFINED;
		slotIdx = -1;
		equipped = false;  
	}
	
	public enum ItemCategory{
		UNDEFINED,
		HELMET,
		ARMOR,
		WEAPON,
		BOOTS,
		CONSUMABLE,
		POWER_UP
	}
	
	public enum ItemType{
		UNDEFINED(ItemCategory.UNDEFINED, ""),
		HELMET(ItemCategory.HELMET, "helmet"),
	    SWORD(ItemCategory.WEAPON, "sword1"),
	    BIG_SWORD(ItemCategory.WEAPON, "sword2"),
	    BOOTS(ItemCategory.BOOTS, "boots"),
	    ARMOR(ItemCategory.ARMOR, "armor"),
	    ALGEBRA(ItemCategory.POWER_UP, "linear_algebra"),
	    CALCULUS_1(ItemCategory.POWER_UP, "calculus_1"),
	    CALCULUS_2(ItemCategory.POWER_UP, "calculus_2"),
	    CALCULUS_3(ItemCategory.POWER_UP, "calculus_3");
		
		public final ItemCategory category;
		public final String uiAtlasKey;
		
		ItemType(ItemCategory category, String uiAtlasKey){
			this.category = category;
			this.uiAtlasKey = uiAtlasKey;
		}
	}
}
