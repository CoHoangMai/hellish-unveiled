package com.hellish.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.screen.ScreenType;

public class MainMenuView extends Table {
    
    private final ImageButton startButton;
    private final ImageButton settingButton;
    private final ImageButton guideButton;
    private final ImageButton quitButton;
    public final Sprite backgroundSprite;
    // Hình nền sẽ được vẽ vào đây
    
    public MainMenuView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        // Tải hình nền
        Texture backgroundTexture = new Texture("background.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundSprite.setPosition(-(Gdx.graphics.getWidth()/2), -(Gdx.graphics.getHeight()/2));
        // Tạo nút Start Game
        Texture startTexture = new Texture("start_button.png");  // Đảm bảo đường dẫn đúng
        TextureRegionDrawable startDrawable = new TextureRegionDrawable(startTexture);
        startButton = new ImageButton(startDrawable);

        // Tạo nút setting
        Texture settingTexture = new Texture("setting_button.png");  // Đảm bảo đường dẫn đúng
        TextureRegionDrawable settingDrawable = new TextureRegionDrawable(settingTexture);
        settingButton = new ImageButton(settingDrawable);

        // Tạo nút guide
        Texture guideTexture = new Texture("guide_button.png");  // Đảm bảo đường dẫn đúng
        TextureRegionDrawable guideDrawable = new TextureRegionDrawable(guideTexture);
        guideButton = new ImageButton(guideDrawable);
        
        // Tạo nút quit
        Texture quitTexture = new Texture("quit_button.png");  // Đảm bảo đường dẫn đúng
        TextureRegionDrawable quitDrawable = new TextureRegionDrawable(quitTexture);
        quitButton = new ImageButton(quitDrawable);

        // Bố trí các nút vào Table
        //this.setFillParent(true);  // Để Table chiếm toàn bộ diện tích của màn hình
        
        this.bottom().padBottom(10); // Đặt các nút ở dưới cùng
        this.add(startButton).size(40, 40).pad(10); // Nút đầu tiên
        this.add(settingButton).size(40, 40).pad(10); // Nút thứ hai
        this.add(guideButton).size(40, 40).pad(10); // Nút thứ ba
        this.add(quitButton).size(40, 40).pad(10); // Nút thứ tư

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.setScreen(ScreenType.GAME); // Chuyển đến màn hình Game
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Thoát ứng dụng
            }
        });

        // Thêm hình nền vào Table (background sẽ được vẽ trước các nút)
    }
}
