package com.hellish.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.hellish.Main;
import com.hellish.screen.MainMenuScreen;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.Scene2DSkin.ImageDrawables;

public class MainMenuView extends Table {
    
    private final ImageButton startButton;
    private final ImageButton settingButton;
    private final ImageButton guideButton;
    private final ImageButton quitButton;
    
    public MainMenuView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        
        setBackground(Drawables.LOADING_BACKGROUND.getAtlasKey());
       
        // Tạo các nút
        startButton = Scene2DSkin.createButton(ImageDrawables.BIG_BUTTON_PLAY, 140, 120);
        settingButton = Scene2DSkin.createButton(ImageDrawables.BIG_BUTTON_SETTING, 140, 120);
        guideButton = Scene2DSkin.createButton(ImageDrawables.BIG_BUTTON_GUIDE, 140, 120);
        quitButton = Scene2DSkin.createButton(ImageDrawables.BIG_BUTTON_QUIT, 140, 120);

        // Bố trí các nút
        this.bottom().padBottom(20); // Đặt các nút ở dưới cùng
        this.add(startButton).size(120, 120).pad(10); // Nút đầu tiên
        this.add(settingButton).size(120, 120).pad(10); // Nút thứ hai
        this.add(guideButton).size(120, 120).pad(10); // Nút thứ ba
        this.add(quitButton).size(120, 120).pad(10); // Nút thứ tư
        
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.setScreen(ScreenType.DIALOG);
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Thoát ứng dụng
            }
        });

        guideButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	((MainMenuScreen) context.getScreen()).showGuideView(true);
            }
        });

        settingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	((MainMenuScreen) context.getScreen()).showSettingView(true);
            }
        });
    }
}
