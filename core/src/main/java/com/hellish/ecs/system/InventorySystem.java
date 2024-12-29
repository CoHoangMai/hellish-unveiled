package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.InventoryComponent;
import com.hellish.ecs.component.ItemComponent;
import com.hellish.ecs.component.ItemComponent.ItemType;
import com.hellish.event.EntityAddItemEvent;
import com.hellish.event.EventUtils;

import static com.hellish.ecs.component.InventoryComponent.INVENTORY_CAPACITY;

public class InventorySystem extends IteratingSystem{
	private final Stage stage;

	public InventorySystem(final Main context) {
		super(Family.all(InventoryComponent.class).get());
		
		this.stage = context.getGameStage();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InventoryComponent inventory = ECSEngine.invCmpMapper.get(entity);
		if(inventory.itemsToAdd.isEmpty()) {
			return;
		}
		
		for(ItemType itemType : inventory.itemsToAdd) {
			int slotIdx = emptySlotIndex(inventory);
			if(slotIdx == -1) {
				return;
			}
			
			Entity newItem = spawnItem(itemType, slotIdx);
			inventory.items.add(newItem);
			EventUtils.fireEvent(stage, EntityAddItemEvent.pool, event -> event.set(entity, newItem));
		}
		inventory.itemsToAdd.clear();
	}

	private Entity spawnItem(ItemType itemType, int slotIdx) {
		Entity newItem = getEngine().createEntity();
		
		ItemComponent itemCmp = getEngine().createComponent(ItemComponent.class);
		itemCmp.itemType = itemType;
		itemCmp.slotIdx = slotIdx;
		itemCmp.equipped = false;
		newItem.add(itemCmp);	

		getEngine().addEntity(newItem);
		
		return newItem;
	}

	private int emptySlotIndex(InventoryComponent inventory) {
		for(int i = 0; i < INVENTORY_CAPACITY; i++) {
			boolean slotOccupied = false;
			for(Entity item : inventory.items) {
				ItemComponent itemCmp = ECSEngine.itemCmpMapper.get(item);
				if(itemCmp.slotIdx == i) {
					slotOccupied = true;
					break;
				}
			}
			if(!slotOccupied) {
				return i;
			}
		}
		return -1;
	}
}
