package com.hellish.ui.widget;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.hellish.ecs.component.ItemComponent.ItemCategory;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.model.ItemModel;

public class InventorySlot extends WidgetGroup{
	private final Skin skin;
	private final Drawable slotItemBgd;
	private final Image background;
	private final Image slotItemInfo;
	private final Image itemImage;
	private ItemModel itemModel;
	
	public InventorySlot(Drawables slotItemBgd, Skin skin) {
		this.skin = skin;
		background = new Image(skin.getDrawable(Drawables.INVENTORY_SLOT.getAtlasKey()));
		this.addActor(background);
		
		if(slotItemBgd != null) {
			this.slotItemBgd = skin.getDrawable(slotItemBgd.getAtlasKey());
			slotItemInfo = new Image(skin.getDrawable(slotItemBgd.getAtlasKey()));
		} else {
			this.slotItemBgd = null;			
			slotItemInfo = null;
		}

		if(slotItemInfo != null) {
			slotItemInfo.setColor(1, 1, 1, 0.33f);
			slotItemInfo.setPosition(2, 2);
			slotItemInfo.setSize(12, 12);
			slotItemInfo.setScaling(Scaling.contain);
			this.addActor(slotItemInfo);
		}
		
		itemImage = new Image();
		itemImage.setPosition(2, 2);
		itemImage.setSize(12, 12);
		itemImage.setScaling(Scaling.contain);
		this.addActor(itemImage);
	}
	
	public Drawable getItemDrawable() {
		return itemImage.getDrawable();
	}
	
	public ItemCategory getSupportedCategory() {
		if(slotItemBgd == null) {
			return ItemCategory.UNDEFINED;
		}
		
		if (slotItemBgd.equals(skin.getDrawable(Drawables.INVENTORY_SLOT_HELMET.getAtlasKey()))) {
            return ItemCategory.HELMET;
        } else if (slotItemBgd.equals(skin.getDrawable(Drawables.INVENTORY_SLOT_WEAPON.getAtlasKey()))) {
            return ItemCategory.WEAPON;
        } else if (slotItemBgd.equals(skin.getDrawable(Drawables.INVENTORY_SLOT_ARMOR.getAtlasKey()))) {
            return ItemCategory.ARMOR;
        } else if (slotItemBgd.equals(skin.getDrawable(Drawables.INVENTORY_SLOT_BOOTS.getAtlasKey()))) {
            return ItemCategory.BOOTS;
        }
        return ItemCategory.UNDEFINED;
	}
	
	public boolean isEmpty() {
		return itemModel == null;
	}
	
	public ItemCategory getItemCategory() {
		return (itemModel != null) ? itemModel.itemCatergory : ItemCategory.UNDEFINED;
	}
	
	public boolean isGear() {
		return getSupportedCategory() != ItemCategory.UNDEFINED;
	}

	public void setItem(ItemModel model) {
		itemModel = model;
		if(model == null) {
			itemImage.setDrawable(null);
		} else {
			itemImage.setDrawable(skin.getDrawable(model.atlasKey));
		}
	}
	
	public ItemModel getItemModel() {
		return itemModel;
	}
	
	@Override
    public float getPrefWidth() {
        return background.getDrawable().getMinWidth();
    }

    @Override
    public float getPrefHeight() {
        return background.getDrawable().getMinHeight();
    }
}
