package com.hellish.ui.widget;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.hellish.ecs.component.ItemComponent.ItemCategory;
import com.hellish.ui.model.ItemModel;

public class InventoryDragSource extends Source{
	public static final float DRAG_ACTOR_SIZE = 40;
	public final InventorySlot inventorySlot;

	public InventoryDragSource(InventorySlot inventorySlot) {
		super(inventorySlot);
		this.inventorySlot = inventorySlot;
	}
	
	public boolean isGear() {
		return inventorySlot.isGear();
	}
	
	public ItemCategory getSupportedCategory() {
        return inventorySlot.getSupportedCategory();
    }

	@Override
	public Payload dragStart(InputEvent event, float x, float y, int pointer) {
		if(inventorySlot.getItemModel() == null) {
			return null;
		}
		
		Payload payload = new Payload();
		payload.setObject(inventorySlot.getItemModel());
		payload.setDragActor(new Image(inventorySlot.getItemDrawable()));
		payload.getDragActor().setSize(DRAG_ACTOR_SIZE, DRAG_ACTOR_SIZE);
		
		inventorySlot.setItem(null);
		
		return payload;
	}
	
	@Override
	public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
		if(target == null) {
			inventorySlot.setItem((ItemModel) payload.getObject());
		} else if(target != null && target.getActor() == inventorySlot) {
			inventorySlot.setItem((ItemModel) payload.getObject());
		}
	}
}
