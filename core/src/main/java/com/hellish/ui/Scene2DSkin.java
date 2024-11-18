package com.hellish.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Scene2DSkin{
	public static Skin defaultSkin;
	
	public enum Fonts {
		DEFAULT("fnt_white", 0.25f),
		BIG("fnt_white", 0.5f),
		BIGGER("fnt_white", 0.75f);
		
		private final String atlasRegionKey;
		private final float scaling;
		
		Fonts(String atlasRegionKey, float scaling){
			this.atlasRegionKey = atlasRegionKey;
			this.scaling = scaling;
		}
		
		public String getSkinKey() {
			return "Font_" + this.name().toLowerCase();
		}
		
		public String getFontPath() {
			return "ui/" + this.atlasRegionKey + ".fnt";
		}
		
		public String getAtlasRegionKey() {
			return this.atlasRegionKey;
		}
		
		public float getScaling() {
			return this.scaling;
		}
	}
	
	public enum Labels{
		FRAME,
		TITLE,
		LARGE;
		
		public String getSkinKey() {
			return this.name().toLowerCase();
		}
	}
	
	public enum Buttons{
		TEXT_BUTTON;
		
		public String getSkinKey() {
			return this.name().toLowerCase();
		}
	}
	
	public enum ProgressBars{
		LOADING;
		
		public String getSkinKey() {
			return this.name().toLowerCase();
		}
	}
	
	public enum Drawables{
		CHAR_INFO_BGD("char_info"),
	    PLAYER("player"),
	    WOLF("wolf"),
	    LIFE_BAR("life_bar"),
	    MANA_BAR("mana_bar"),
	    FRAME_BGD("frame_bgd"),
	    FRAME_FGD("frame_fgd"),
	    BAR_BGD("bar_bgd"),
	    INVENTORY_SLOT("inv_slot"), 
	    INVENTORY_SLOT_HELMET("inv_slot_helmet"),
	    INVENTORY_SLOT_WEAPON("inv_slot_weapon"),
	    INVENTORY_SLOT_ARMOR("inv_slot_armor"),
	    INVENTORY_SLOT_BOOTS("inv_slot_boots");

	    private final String atlasKey;

	    Drawables(String atlasKey) {
	        this.atlasKey = atlasKey;
	    }
	    
	    public String getAtlasKey() {
	        return atlasKey;
	    }
	}
	
	public static void loadSkin() {
		Scene2DSkin.defaultSkin = new Skin(new TextureAtlas("ui/ui.atlas"));
		
		loadFontSkin(defaultSkin);
		loadLabelSkin(defaultSkin);
		loadButtonSkin(defaultSkin);
		loadProgressBarSkin(defaultSkin);
	}

	private static void loadFontSkin(Skin skin) {
		Colors.put("Red", Color.RED);
		Colors.put("Blue", Color.BLUE);
		
		for(Fonts font : Fonts.values()) {
			BitmapFont bitmapFont = new BitmapFont(
				Gdx.files.internal(font.getFontPath()), 
				skin.getRegion(font.getAtlasRegionKey())
			);
			bitmapFont.getData().markupEnabled = true;
			bitmapFont.getData().setScale(font.getScaling());
			skin.add(font.getSkinKey(), bitmapFont);
		}
	}
	
	private static void loadLabelSkin(Skin skin) {
		Label.LabelStyle frameLabelStyle = new Label.LabelStyle();
		frameLabelStyle.font = skin.get(Fonts.DEFAULT.getSkinKey(), BitmapFont.class);
		frameLabelStyle.background = skin.getDrawable(Drawables.FRAME_FGD.getAtlasKey());
		frameLabelStyle.background.setLeftWidth(4);
		frameLabelStyle.background.setRightWidth(4);
		frameLabelStyle.background.setTopHeight(6);
		skin.add(Labels.FRAME.getSkinKey(), frameLabelStyle);
		
		Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
		titleLabelStyle.font = skin.get(Fonts.BIG.getSkinKey(), BitmapFont.class);
		titleLabelStyle.fontColor = Color.SLATE;
		titleLabelStyle.background = skin.getDrawable(Drawables.FRAME_FGD.getAtlasKey());
		skin.add(Labels.TITLE.getSkinKey(), titleLabelStyle);
		
		Label.LabelStyle largeLabelStyle = new Label.LabelStyle();
		largeLabelStyle.font = skin.get(Fonts.BIGGER.getSkinKey(), BitmapFont.class);
		skin.add(Labels.LARGE.getSkinKey(), largeLabelStyle);
	}

	private static void loadButtonSkin(Skin skin) {
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.font = skin.get(Fonts.DEFAULT.getSkinKey(), BitmapFont.class);
		skin.add(Buttons.TEXT_BUTTON.getSkinKey(), textButtonStyle);
	}
	
	private static void loadProgressBarSkin(Skin skin) {
		ProgressBar.ProgressBarStyle loadingBarStyle = new ProgressBar.ProgressBarStyle();
		loadingBarStyle.background = skin.getDrawable(Drawables.BAR_BGD.getAtlasKey());
		loadingBarStyle.knobBefore = skin.getDrawable(Drawables.LIFE_BAR.getAtlasKey());
		skin.add(ProgressBars.LOADING.getSkinKey(), loadingBarStyle);
	}
	
	public static void disposeSkin() {
		if(Scene2DSkin.defaultSkin != null) {
			Scene2DSkin.defaultSkin.dispose();
		}
	}
}
