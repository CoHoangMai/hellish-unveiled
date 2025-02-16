package com.hellish.ui.view;

import static com.hellish.ui.Scene2DSkin.OVERLAY_KEY;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.hellish.Main;
import com.hellish.event.EventUtils;
import com.hellish.event.GamePauseEvent;
import com.hellish.event.GameRestartEvent;
import com.hellish.event.GameResumeEvent;
import com.hellish.screen.GameScreen;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.Scene2DSkin.ImageDrawables;
import com.hellish.ui.Scene2DSkin.Labels;

public class PauseView extends Table{
	private final ImageButton goBackButton;
    private final ImageButton settingButton;
    private final ImageButton restartButton;
    private final ImageButton continueButton;
	private final Stage gameStage;
	public PauseView(Skin skin, final Main context) {
		super(skin);

		gameStage = context.getGameStage();
		
		setFillParent(true);
		this.top();

		setBackground(skin.get(OVERLAY_KEY, TextureRegionDrawable.class));
		
		Label pauseLabel = new Label("Xì tóp", skin.get(Labels.LARGE.getSkinKey(), LabelStyle.class));
		pauseLabel.setAlignment(Align.center);
		add(pauseLabel).expandX().center().padTop(120).row();
		// Tạo các nút
		goBackButton = Scene2DSkin.createButton(ImageDrawables.SMALL_BUTTON_QUIT, 50, 40);
        settingButton = Scene2DSkin.createButton(ImageDrawables.SMALL_BUTTON_SETTING, 50, 40);
        restartButton = Scene2DSkin.createButton(ImageDrawables.SMALL_BUTTON_RESTART, 50, 40);
        continueButton = Scene2DSkin.createButton(ImageDrawables.SMALL_BUTTON_CONTINUE, 50, 40);
		// Bố trí các nút
		Table bottomTable = new Table();
        bottomTable.add(goBackButton).size(40, 40).pad(20); // Nút đầu tiên
        bottomTable.add(settingButton).size(40, 40).pad(20); // Nút thứ hai
        bottomTable.add(restartButton).size(40, 40).pad(20); // Nút thứ ba
        bottomTable.add(continueButton).size(40, 40).pad(20); // Nút thứ tư
		this.add(bottomTable); // Đặt các nút ở dưới cùng
		
		//Tạo event cho nút
        goBackButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				EventUtils.fireEvent(gameStage, GameRestartEvent.pool, e -> {});
				EventUtils.fireEvent(gameStage, GameResumeEvent.pool, e -> {});
				context.setScreen(ScreenType.MAIN_MENU); // Khởi động lại màn hình Game
            }
        });

        restartButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				EventUtils.fireEvent(gameStage, GameRestartEvent.pool, e -> {});
				EventUtils.fireEvent(gameStage, GameResumeEvent.pool, e -> {});
			}
		});
        
        continueButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				 GameResumeEvent resumeEvent = GameResumeEvent.pool.obtain();
				 gameStage.getRoot().fire(resumeEvent);
				 GameResumeEvent.pool.free(resumeEvent);
			}
		});

		settingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
        		EventUtils.fireEvent(gameStage, GamePauseEvent.pool, e -> {});
        		((GameScreen) context.getScreen()).showSettingView(true);
		   }
        });
	}
}