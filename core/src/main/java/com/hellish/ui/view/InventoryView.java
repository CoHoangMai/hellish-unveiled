package com.hellish.ui.view;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.Scene2DSkin.Labels;
import com.hellish.ui.model.InventoryModel;
import com.hellish.ui.model.ItemModel;
import com.hellish.ui.widget.InventoryDragSource;
import com.hellish.ui.widget.InventoryDragTarget;
import com.hellish.ui.widget.InventorySlot;

import static com.hellish.ui.widget.InventoryDragSource.DRAG_ACTOR_SIZE;

public class InventoryView extends Table{
	private final InventoryModel model;
	private final Array<InventorySlot> invSlots;
	private final Array<InventorySlot> gearSlots;

	@SuppressWarnings("unchecked")
	public InventoryView(InventoryModel model, Skin skin) {
		super(skin);
		this.model = model;
		setFillParent(true);
		float titlePadding = 15;
		
		invSlots = new Array<>();
		gearSlots = new Array<>();
		
		//Inventory
		Table outerTable  = new Table();
		outerTable.background(skin.getDrawable(Drawables.FRAME_BGD.getAtlasKey()));
		
		Label invLabel = new Label("Ba lô", skin.get(Labels.TITLE.getSkinKey(), LabelStyle.class));
		invLabel.setAlignment(Align.center);
		invLabel.setWrap(true);
		outerTable.add(invLabel).expandX().fill().pad(8, titlePadding, 0, titlePadding).top().row();
		
		Table invSlotTable = new Table();
		for(int i=1; i<=18; i++) {
			InventorySlot slot = new InventorySlot(null, skin);
			invSlots.add(slot);
			invSlotTable.add(slot).padBottom(2);
			if(i % 6 == 0) {
				invSlotTable.row();
			} else {
				invSlotTable.getCell(slot).padRight(2);
			}
		}
		outerTable.add(invSlotTable).expand().fill();
		
		add(outerTable).expand().width(150).height(140).left().center();
		
		//Gear
		Table gearTable = new Table();
		gearTable.background(skin.getDrawable(Drawables.FRAME_BGD.getAtlasKey()));
		
		Label gearLabel = new Label("Trang bị", skin.get(Labels.TITLE.getSkinKey(), LabelStyle.class));
		gearLabel.setAlignment(Align.center);
		gearLabel.setWrap(true);
		gearTable.add(gearLabel).expandX().fill().pad(8, titlePadding, 0, titlePadding).top().row();
		
		Table gearTableInner = new Table();
		
		InventorySlot helmetSlot = new InventorySlot(Drawables.INVENTORY_SLOT_HELMET, skin);
		gearSlots.add(helmetSlot);
		gearTableInner.add(helmetSlot).padBottom(2).colspan(2).row();
		
		InventorySlot weaponSlot = new InventorySlot(Drawables.INVENTORY_SLOT_WEAPON, skin);
		gearSlots.add(weaponSlot);
		gearTableInner.add(weaponSlot).padBottom(2).padRight(2);
		
		InventorySlot armorSlot = new InventorySlot(Drawables.INVENTORY_SLOT_ARMOR, skin);
		gearSlots.add(armorSlot);
		gearTableInner.add(armorSlot).padBottom(2).row();
		
		InventorySlot bootsSlot = new InventorySlot(Drawables.INVENTORY_SLOT_BOOTS, skin);
		gearSlots.add(bootsSlot);
		gearTableInner.add(bootsSlot).colspan(2).row();
		
		gearTable.add(gearTableInner).expand().fill();
		add(gearTable).expand().width(90).height(140).left().center();
		
		setUpDragAndDrop();
		
		//Data binding
		model.onPropertyChange("playerItems", itemModels -> {
            clearInventoryAndGear();
            for (ItemModel itemModel : (Array<ItemModel>) itemModels) {
                if (itemModel.equipped) {
                    gear(itemModel);
                } else {
                    item(itemModel);
                }
            }
        });
	}
	
	private void setUpDragAndDrop() {
		DragAndDrop dnd = new DragAndDrop();
		dnd.setDragActorPosition(DRAG_ACTOR_SIZE * 0.5f, -DRAG_ACTOR_SIZE * 0.5f);
		
		for (InventorySlot slot : invSlots) {
            dnd.addSource(new InventoryDragSource(slot));
            dnd.addTarget(new InventoryDragTarget(slot, this::onItemDropped, null));
        }
		
		for (InventorySlot slot : gearSlots) {
            dnd.addSource(new InventoryDragSource(slot));
            dnd.addTarget(new InventoryDragTarget(slot, this::onItemDropped, slot.getSupportedCategory()));
        }
	}
	
	private void onItemDropped(InventorySlot sourceSlot, InventorySlot targetSlot, ItemModel itemModel) {
		if(sourceSlot == targetSlot) {
			return;
		}
		
		//Đổi chỗ trang bị
		sourceSlot.setItem(targetSlot.getItemModel());
		targetSlot.setItem(itemModel);
		
		//Cập nhật model
		ItemModel sourceItem = sourceSlot.getItemModel();
		if(sourceSlot.isGear()) {
			model.equip(itemModel, false);
			if(sourceItem != null) {
				model.equip(sourceItem, true);
			}
		} else if(sourceItem != null) {
			model.inventoryItem(invSlots.indexOf(sourceSlot, false), sourceItem);
		}
		
		if(targetSlot.isGear()) {
			if(sourceItem != null) {
				model.equip(sourceItem, false);
			}
			model.equip(itemModel, true);
		} else {
			model.inventoryItem(invSlots.indexOf(targetSlot, false), itemModel);
		}
	}

	public void item(ItemModel model) {
		invSlots.get(model.slotIdx).setItem(model);
	}
	
	public void gear(ItemModel model) {
		for(InventorySlot slot : gearSlots) {
			if(slot.getSupportedCategory() == model.itemCatergory) {
				slot.setItem(model);
				return;
			}
		}
	}

	public void clearInventoryAndGear() {
		for(InventorySlot slot : invSlots) {
			slot.setItem(null);
		}
		for(InventorySlot slot : gearSlots) {
			slot.setItem(null);
		}
	}
}
