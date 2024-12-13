package com.hellish.ui.view;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.hellish.Main;
import com.hellish.audio.AudioManager;
import com.hellish.audio.AudioType;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin.ImageDrawables;
import com.hellish.ui.Scene2DSkin.Labels;

public class PauseView extends Table{
	private static final String PIXMAP_KEY = "pauseTexture";
	private final ImageButton goBackButton;
    private final ImageButton settingButton;
    private final ImageButton restartButton;
    private final ImageButton continueButton;
	private final AudioManager audioManager;
	public PauseView(Skin skin, final Main context) {
		super(skin);
		setFillParent(true);
		audioManager = context.getAudioManager();
		//GameScreen gameScreen = new GameScreen(context);
		this.top();
		
		if(!skin.has(PIXMAP_KEY, TextureRegionDrawable.class)) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0.1f, 0.1f, 0.1f, 0.7f);
            pixmap.fillRectangle(0, 0, 1, 1);
			Texture texture = new Texture(pixmap);
			skin.add(PIXMAP_KEY, new TextureRegionDrawable(texture));
		}

		setBackground(skin.get(PIXMAP_KEY, TextureRegionDrawable.class));
		
		Label pauseLabel = new Label("[RED]Xì tóp", skin.get(Labels.LARGE.getSkinKey(), LabelStyle.class));
		pauseLabel.setAlignment(Align.center);
		add(pauseLabel).expandX().center().padTop(50).row();
		// Tạo các nút
		goBackButton = createButton(ImageDrawables.SMALL_BUTTON_QUIT);
        settingButton = createButton(ImageDrawables.SMALL_BUTTON_SETTING);
        restartButton = createButton(ImageDrawables.SMALL_BUTTON_RESTART);
        continueButton = createButton(ImageDrawables.SMALL_BUTTON_CONTINUE);
		// Bố trí các nút
		Table bottomTable = new Table();
        bottomTable.add(goBackButton).size(20, 20).pad(10); // Nút đầu tiên
        bottomTable.add(settingButton).size(20, 20).pad(10); // Nút thứ hai
        bottomTable.add(restartButton).size(20, 20).pad(10); // Nút thứ ba
        bottomTable.add(continueButton).size(20, 20).pad(10); // Nút thứ tư
		this.add(bottomTable); // Đặt các nút ở dưới cùng
		//Tạo event cho nút
        goBackButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				context.setScreen(ScreenType.MAIN_MENU);
            }
        });

        restartButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				context.setScreen(ScreenType.GAME);
			}
		});

        settingButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				context.setScreen(ScreenType.SETTING);
			}
		});
	}

	private ImageButton createButton(ImageDrawables image) {
        Texture texture = new Texture(image.getFileName());
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        ImageButton button = new ImageButton(drawable);
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setSize(25, 25);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setSize(20, 20);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.playAudio(AudioType.SELECT);
            }
        });
        return button;
    }
}