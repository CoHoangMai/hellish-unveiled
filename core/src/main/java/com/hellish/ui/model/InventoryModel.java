package com.hellish.ui.model;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.InventoryComponent;
import com.hellish.ecs.component.ItemComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.event.EntityAddItemEvent;

public class InventoryModel extends PropertyChangeSource implements EventListener{
	private Array<ItemModel> playerItems;
	private final ECSEngine ecsEngine;

	public InventoryModel(final Main context) {
		playerItems = new Array<>();
		
		ecsEngine = context.getECSEngine();
		context.getGameStage().addListener(this);
	}
	
	private InventoryComponent getPlayerInventoryCmp() {
	    for (Entity entity : ecsEngine.getEntitiesFor(Family.all(PlayerComponent.class).get())) {
	        InventoryComponent inventoryCmp = ECSEngine.invCmpMapper.get(entity);
	        if (inventoryCmp != null) {
	            return inventoryCmp;
	        }
	    }
	    return null;
	}

	private void setPlayerItems(Array<ItemModel> updatedItems) {
		playerItems = updatedItems;
		notify("playerItems", playerItems);
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof EntityAddItemEvent) {
			EntityAddItemEvent addItemEvent = (EntityAddItemEvent) event;
			if(addItemEvent.getEntity() != null && ECSEngine.playerCmpMapper.has(addItemEvent.getEntity())) {
				InventoryComponent invCmp = ECSEngine.invCmpMapper.get(addItemEvent.getEntity());
				Array<ItemModel> updatedItems = new Array<>();
				for(Entity itemEntity : invCmp.items) {
					ItemComponent itemCmp = ECSEngine.itemCmpMapper.get(itemEntity);
					if(itemCmp != null) {
						updatedItems.add(new ItemModel(
								itemEntity,
								itemCmp.itemType.category,
								itemCmp.itemType.uiAtlasKey,
								itemCmp.slotIdx,
								itemCmp.equipped
						));
					}
				}
				setPlayerItems(updatedItems);
			}
			return true;
		}
		return false;
	}

	private ItemComponent getPlayerItemByModel(ItemModel itemModel) {
		for(Entity itemEntity : getPlayerInventoryCmp().items) {
			if(itemEntity == itemModel.itemEntity) {
				return ECSEngine.itemCmpMapper.get(itemEntity);
			}
		}
		return null;
	}
	
	public void equip(ItemModel itemModel, boolean equip) {
		ItemComponent itemCmp = getPlayerItemByModel(itemModel);
		if(itemCmp != null) {
			itemCmp.equipped = equip;
			itemModel.equipped = equip;
		}
	}

	public void inventoryItem(int slotIdx, ItemModel sourceItem) {
		ItemComponent itemCmp = getPlayerItemByModel(sourceItem);
		itemCmp.slotIdx = slotIdx;
		sourceItem.slotIdx = slotIdx;
	}
}
