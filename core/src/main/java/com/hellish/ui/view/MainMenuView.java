package com.hellish.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

public class MainMenuView extends Table {
    
    private final ImageButton startButton;
    private final ImageButton settingButton;
    private final ImageButton guideButton;
    private final ImageButton quitButton;
    public final Sprite backgroundSprite;
    private final AudioManager audioManager;
    // Hình nền sẽ được vẽ vào đây
    
    public MainMenuView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        audioManager = context.getAudioManager();
        // Tải hình nền
        Texture backgroundTexture = new Texture("background.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundSprite.setPosition(-(Gdx.graphics.getWidth()/2), -(Gdx.graphics.getHeight()/2));
        
        // Tạo các nút
        startButton = createButton("start_button.png");
        settingButton = createButton("setting_button.png");
        guideButton = createButton("guide_button.png");
        quitButton = createButton("quit_button.png");

        // Bố trí các nút
        this.bottom().padBottom(10); // Đặt các nút ở dưới cùng
        this.add(startButton).size(60, 60).pad(10); // Nút đầu tiên
        this.add(settingButton).size(60, 60).pad(10); // Nút thứ hai
        this.add(guideButton).size(60, 60).pad(10); // Nút thứ ba
        this.add(quitButton).size(60, 60).pad(10); // Nút thứ tư
        
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
    }

    private ImageButton createButton(String texturePath) {
        Texture texture = new Texture(texturePath);
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        ImageButton button = new ImageButton(drawable);
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setSize(70,70);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setSize(60,60);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.playAudio(AudioType.SELECT);
            }
        });
        return button;

    }
}

