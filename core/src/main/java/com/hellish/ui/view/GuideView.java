package com.hellish.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import com.hellish.ui.Scene2DSkin.ImageDrawables;

public class GuideView extends Table{
    
    public final Sprite backgroundSprite;
    private final ImageButton goBackButton;
    private final AudioManager audioManager;
    private final Image dimOverlay;
    private final Image guideImage;

    public GuideView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        audioManager = context.getAudioManager();
        // Tải hình nền
        Texture backgroundTexture = new Texture("ui/background/background_guide.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundSprite.setPosition(-(Gdx.graphics.getWidth()/2), -(Gdx.graphics.getHeight()/2));
        //Thêm lớp phủ
        dimOverlay = createDimOverlay();
        this.addActor(dimOverlay);
        // Tạo nút
        goBackButton = createButton(ImageDrawables.SMALL_BUTTON_QUIT);
        // Thêm guide
        guideImage = new Image(new Texture("dialog/guide.png"));

        this.top(); // Đặt bảng chính để định vị từ trên xuống

        // Tạo bảng con cho nút quay lại
        Table topTable = new Table();
        topTable.add(goBackButton).size(20, 20).pad(4).top().right();

        // Tạo bảng con cho ảnh hướng dẫn
        Table centerTable = new Table();
        centerTable.add(guideImage).size(200, 150).padBottom(50);

        // Thêm các bảng con vào bảng chính
        this.add(topTable).expandX().top().right().padTop(10).padRight(10).row(); // Hàng đầu tiên với nút ở góc trên cùng bên phải
        this.add(centerTable).expand().padBottom(100); // Hàng thứ hai với ảnh ở giữa
        
        //Tạo event cho nút
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.setScreen(ScreenType.MAIN_MENU); // Chuyển đến màn hình Game
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

    private Image createDimOverlay() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f); // Màu đen với độ trong suốt 50%
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        Image overlay = new Image(new TextureRegionDrawable(texture));
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlay.setPosition(0, 0);
        return overlay;
    }
}
