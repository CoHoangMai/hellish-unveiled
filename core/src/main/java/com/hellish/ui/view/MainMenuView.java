package com.hellish.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.audio.AudioManager;
import com.hellish.audio.AudioType;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.Scene2DSkin.ImageDrawables;

public class MainMenuView extends Table {
    
    private final ImageButton startButton;
    private final ImageButton settingButton;
    private final ImageButton guideButton;
    private final ImageButton quitButton;
    private final AudioManager audioManager;
    private final Stage gameStage;
    // Hình nền sẽ được vẽ vào đây
    
    public MainMenuView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        audioManager = context.getAudioManager();
        gameStage = context.getGameStage();
        
        setBackground(Drawables.LOADING_BACKGROUND.getAtlasKey());
       
        // Tạo các nút
        startButton = createButton(ImageDrawables.BIG_BUTTON_PLAY);
        settingButton = createButton(ImageDrawables.BIG_BUTTON_SETTING);
        guideButton = createButton(ImageDrawables.BIG_BUTTON_GUIDE);
        quitButton = createButton(ImageDrawables.BIG_BUTTON_QUIT);

        // Bố trí các nút
        this.bottom().padBottom(20); // Đặt các nút ở dưới cùng
        this.add(startButton).size(120, 120).pad(10); // Nút đầu tiên
        this.add(settingButton).size(120, 120).pad(10); // Nút thứ hai
        this.add(guideButton).size(120, 120).pad(10); // Nút thứ ba
        this.add(quitButton).size(120, 120).pad(10); // Nút thứ tư
        
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.setScreen(ScreenType.DAILOG); // Chuyển đến màn hình Game
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
                context.setScreen(ScreenType.GUIDE); // Chuyển đến màn hình Game
            }
        });

        settingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.setScreen(ScreenType.SETTING); // Chuyển đến màn hình Game
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
                button.setSize(140,140);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setSize(120,120);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.playAudio(AudioType.SELECT);
            }
        });
        return button;
    }
}

