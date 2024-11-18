package com.hellish.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.hellish.ecs.component.ItemComponent.ItemCategory;
import com.hellish.ui.model.ItemModel;

public class InventoryDragTarget extends Target{
	private final InventorySlot inventorySlot;
	private final OnDropCallback onDropCallback;
	private final ItemCategory supportedItemCategory;
	
	public interface OnDropCallback {
		void onDrop(InventorySlot sourceSlot, InventorySlot targetSlot, ItemModel itemModel);
	}

	public InventoryDragTarget(InventorySlot inventorySlot, OnDropCallback onDrop, ItemCategory supportedItemCategory) {
		super(inventorySlot);
		this.inventorySlot = inventorySlot;
		this.onDropCallback = onDrop;
		this.supportedItemCategory = supportedItemCategory;
	}
	
	private boolean isGearSlot() {
		return supportedItemCategory != null;
	}
	
	private boolean isSupported(ItemCategory category) {
        return supportedItemCategory == category;
    }

	@Override
	public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
		ItemModel itemModel = (ItemModel) payload.getObject();
		InventoryDragSource dragSource = (InventoryDragSource) source;
		ItemCategory srcCategory = dragSource.getSupportedCategory();
		
		if(isGearSlot() && isSupported(itemModel.itemCatergory)) {
			return true;
		} else if(!isGearSlot() && dragSource.isGear() && (inventorySlot.isEmpty() 
				|| inventorySlot.getItemCategory() == srcCategory)) {
			return true;
		} else if (!isGearSlot() && !dragSource.isGear()) {
			return true;
		} else {
			payload.getDragActor().setColor(Color.RED);
			return false;
		}
	}
	
	@Override
    public void reset(Source source, Payload payload) {
        payload.getDragActor().setColor(Color.WHITE);
    }

	@Override
	public void drop(Source source, Payload payload, float x, float y, int pointer) {
		onDropCallback.onDrop(
				((InventoryDragSource)source).inventorySlot, 
				(InventorySlot) getActor(), 
				(ItemModel) payload.getObject());
	}
}
