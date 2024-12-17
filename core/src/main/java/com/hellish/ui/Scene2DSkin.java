package com.hellish.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.audio.AudioManager;
import com.hellish.audio.AudioType;

public class Scene2DSkin{
	public static Skin defaultSkin;
	public static final String OVERLAY_KEY = "overlayTexture";
	private static AudioManager audioManager;
	
	public enum Fonts {
		DEFAULT("fnt_white", 0.5f),
		BIG("fnt_white", 1),
		BIGGER("fnt_white", 1.5f);
		
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

	public enum Lists{
		DEFAULT;

		public String getSkinKey() {
			return this.name().toLowerCase();
		}
	}

	public enum Scrolls{
		DEFAULT;

		public String getSkinKey() {
			return this.name().toLowerCase();
		}
	}
	
	public enum Labels{
		TEXT,
		FRAME,
		TITLE,
		LARGE,
		NORMAL;
		
		public String getSkinKey() {
			return this.name().toLowerCase();
		}
	}
	
	public enum Buttons{
		TEXT_BUTTON,
		GO_BACK_BUTTON,
		RESTART_BUTTON,
		SETTING_BUTTON,
		CONTINUE_BUTTON;
		
		public String getSkinKey() {
			return this.name().toLowerCase();
		}
	}

	public enum Sliders{
		SLIDER;

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


    public enum CheckBoxes {
        CHECKBOX;

        public String getSkinKey() {
            return this.name().toLowerCase();
        }
    }

    public enum SelectBoxes {
        SELECT_BOX;

        public String getSkinKey() {
            return this.name().toLowerCase();
        }
    }
	
	public enum Drawables{
		CHAR_INFO_BGD("char_info"),
		PROGRESS_BAR("progress_bar"),
	    LIFE_BAR("life_bar"),
	    MANA_BAR("mana_bar"),
	    FRAME_BGD("frame_bgd"),
	    FRAME_FGD("frame_fgd"),
	    BAR_BGD("bar_bgd"),
	    INVENTORY_SLOT("inv_slot"), 
	    INVENTORY_SLOT_HELMET("inv_slot_helmet"),
	    INVENTORY_SLOT_WEAPON("inv_slot_weapon"),
	    INVENTORY_SLOT_ARMOR("inv_slot_armor"),
	    INVENTORY_SLOT_BOOTS("inv_slot_boots"),
		
		LOADING_BACKGROUND("main_bgd"),
		DIALOG_BACKGROUND("dialog_bgd"),
		GUIDE_BACKGROUND("guide_bgd");

	    private final String atlasKey;

	    Drawables(String atlasKey) {
	        this.atlasKey = atlasKey;
	    }
	    
	    public String getAtlasKey() {
	        return atlasKey;
	    }
	}

	public enum ImageDrawables{
		BIG_BUTTON_PLAY("ui/button/big_button_play.png"),
		BIG_BUTTON_GUIDE("ui/button/big_button_guide.png"),
		BIG_BUTTON_QUIT("ui/button/big_button_quit.png"),
		BIG_BUTTON_SETTING("ui/button/big_button_setting.png"),
		SMALL_BUTTON_CONTINUE("ui/button/small_button_continue.png"),
		SMALL_BUTTON_PAUSE("ui/button/small_button_pause.png"),
		SMALL_BUTTON_QUIT("ui/button/small_button_quit.png"),
		SMALL_BUTTON_RESTART("ui/button/small_button_restart.png"),
		SMALL_BUTTON_SETTING("ui/button/small_button_setting.png"),
		MAIL_BUTTON("dialog/06_noti_mail.png"),
		UNCHECKED_BOX("ui/button/button_no_tick.png"),
		CHECKED_BOX("ui/button/button_tick.png");
	
		private final String fileName;
	
		ImageDrawables(String fileName) {
			this.fileName = fileName;
		}
	
		public String getFileName() {
			return fileName;
		}
	}
	
	public static void loadSkin(final Main context) {
		audioManager = context.getAudioManager();
		
		Scene2DSkin.defaultSkin = new Skin(new TextureAtlas("ui/ui.atlas"));
		
		loadFontSkin(defaultSkin);
		loadListSkin(defaultSkin);
		loadScrollPaneSkin(defaultSkin);
		loadLabelSkin(defaultSkin);
		loadTextButtonSkin(defaultSkin);
		loadProgressBarSkin(defaultSkin);
		loadSliderSkin(defaultSkin);
		loadCheckBoxSkin(defaultSkin);
        loadSelectBoxSkin(defaultSkin);
        
        createOverlayTexture(defaultSkin);
	}

	private static void createOverlayTexture(Skin skin) {
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.7f);
        pixmap.fillRectangle(0, 0, 1, 1);
		Texture texture = new Texture(pixmap);
		skin.add(OVERLAY_KEY, new TextureRegionDrawable(texture));
	}

	private static void loadFontSkin(Skin skin) {
		Colors.put("Red", Color.RED);
		Colors.put("Blue", Color.BLUE);
		Colors.put("Black", Color.BLACK);
		
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

	private static void loadListSkin(Skin skin) {
		List.ListStyle listStyle = new List.ListStyle();
		listStyle.font = skin.get(Fonts.DEFAULT.getSkinKey(), BitmapFont.class);
		listStyle.background = skin.getDrawable(Drawables.FRAME_FGD.getAtlasKey());
		listStyle.selection = skin.getDrawable(Drawables.INVENTORY_SLOT.getAtlasKey()); // Thay đổi với Drawable thích hợp
		skin.add(Lists.DEFAULT.getSkinKey(), listStyle);  // Thêm vào skin
	}

	private static void loadScrollPaneSkin(Skin skin) {
		ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
		scrollPaneStyle.background = skin.getDrawable(Drawables.FRAME_FGD.getAtlasKey()); // Background của ScrollPane
		scrollPaneStyle.vScrollKnob = skin.getDrawable(Drawables.LIFE_BAR.getAtlasKey()); // Con trượt thanh cuộn dọc
		skin.add(Scrolls.DEFAULT.getSkinKey(), scrollPaneStyle);
	}
	
	private static void loadLabelSkin(Skin skin) {
		Label.LabelStyle textLabelStyle = new Label.LabelStyle();
		textLabelStyle.font = skin.get(Fonts.DEFAULT.getSkinKey(), BitmapFont.class);
		skin.add(Labels.TEXT.getSkinKey(), textLabelStyle);
	
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

		Label.LabelStyle normalLabelStyle = new Label.LabelStyle();
		normalLabelStyle.font = skin.get(Fonts.DEFAULT.getSkinKey(), BitmapFont.class);
		skin.add(Labels.NORMAL.getSkinKey(), normalLabelStyle);
	}

	private static void loadTextButtonSkin(Skin skin) {
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.font = skin.get(Fonts.DEFAULT.getSkinKey(), BitmapFont.class);
		skin.add(Buttons.TEXT_BUTTON.getSkinKey(), textButtonStyle);
	}
	
	private static void loadProgressBarSkin(Skin skin) {
		ProgressBar.ProgressBarStyle loadingBarStyle = new ProgressBar.ProgressBarStyle();
		
		loadingBarStyle.background = skin.getDrawable(Drawables.BAR_BGD.getAtlasKey());
		loadingBarStyle.background.setMinHeight(30);
		
		loadingBarStyle.knobBefore = skin.getDrawable(Drawables.PROGRESS_BAR.getAtlasKey());
		loadingBarStyle.knobBefore.setMinHeight(30);
		
		skin.add(ProgressBars.LOADING.getSkinKey(), loadingBarStyle);
	}

	private static void loadSliderSkin(Skin skin) {
		Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
		sliderStyle.background = skin.getDrawable(Drawables.BAR_BGD.getAtlasKey());
		sliderStyle.knob = skin.getDrawable(Drawables.INVENTORY_SLOT.getAtlasKey());
    	sliderStyle.knobBefore = skin.getDrawable(Drawables.LIFE_BAR.getAtlasKey());
		skin.add(Sliders.SLIDER.getSkinKey(), sliderStyle);
	}

	private static void loadCheckBoxSkin(Skin skin) {
		CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
		checkBoxStyle.font = skin.get(Fonts.DEFAULT.getSkinKey(), BitmapFont.class);
	
		// Tạo TextureRegionDrawable và thay đổi kích thước
		Texture uncheckedTexture = new Texture(ImageDrawables.UNCHECKED_BOX.getFileName());
		TextureRegionDrawable checkboxOffDrawable = new TextureRegionDrawable(new TextureRegion(uncheckedTexture));
		checkboxOffDrawable.setMinWidth(20); // Đặt kích thước mong muốn
		checkboxOffDrawable.setMinHeight(20);
	
		Texture checkedTexture = new Texture(ImageDrawables.CHECKED_BOX.getFileName());
		TextureRegionDrawable checkboxOnDrawable = new TextureRegionDrawable(new TextureRegion(checkedTexture));
		checkboxOnDrawable.setMinWidth(20); // Đặt kích thước mong muốn
		checkboxOnDrawable.setMinHeight(20);
	
		// Gán các drawable đã chỉnh kích thước vào CheckBoxStyle
		checkBoxStyle.checkboxOff = checkboxOffDrawable;
		checkBoxStyle.checkboxOn = checkboxOnDrawable;
	
		// Thêm CheckBoxStyle vào Skin
		skin.add(CheckBoxes.CHECKBOX.getSkinKey(), checkBoxStyle);
	}
    private static void loadSelectBoxSkin(Skin skin) {
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = skin.get(Fonts.DEFAULT.getSkinKey(), BitmapFont.class);
        selectBoxStyle.background = skin.getDrawable(Drawables.FRAME_FGD.getAtlasKey());
        selectBoxStyle.scrollStyle = skin.get(Lists.DEFAULT.getSkinKey(), ScrollPane.ScrollPaneStyle.class);
		selectBoxStyle.listStyle = skin.get(Lists.DEFAULT.getSkinKey(), List.ListStyle.class);
        skin.add(SelectBoxes.SELECT_BOX.getSkinKey(), selectBoxStyle);
    }
    
	public static ImageButton createButton(ImageDrawables image) {
        Texture texture = new Texture(image.getFileName());
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        ImageButton button = new ImageButton(drawable);
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setSize(50, 50);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setSize(40, 40);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.playAudio(AudioType.SELECT);
            }
        });
        return button;
    }
	
	public static void disposeSkin() {
		if(Scene2DSkin.defaultSkin != null) {
			Scene2DSkin.defaultSkin.dispose();
		}
	}
}
