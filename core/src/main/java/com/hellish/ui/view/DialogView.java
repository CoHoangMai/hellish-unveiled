package com.hellish.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.audio.AudioManager;
import com.hellish.audio.AudioType;

public class DialogView extends Table {
    
    private List<Texture> images;
    private Image currentImage;
    private int currentImageIndex;
    public final Sprite backgroundSprite;
    private final AudioManager audioManager;

    public DialogView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        audioManager = context.getAudioManager();

        Texture backgroundTexture = new Texture("background_dialog.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundSprite.setPosition(-(Gdx.graphics.getWidth()/2), -(Gdx.graphics.getHeight()/2));

        images = new ArrayList<>();
        images.add(new Texture("dialog/01_before.png"));  // Thêm các hình ảnh vào đây
        images.add(new Texture("dialog/02_before.png"));
        images.add(new Texture("dialog/03_before.png"));
        images.add(new Texture("dialog/04_before.png"));
        images.add(new Texture("dialog/05_noti.png"));
        images.add(new Texture("dialog/06_noti_mail.png"));
        images.add(new Texture("dialog/07_hust_mail.png"));
        images.add(new Texture("dialog/08_monster_mail.png"));
        images.add(new Texture("dialog/09_after.png"));
        images.add(new Texture("dialog/10_after.png"));
        images.add(new Texture("dialog/11_after.png"));
        images.add(new Texture("dialog/12_after.png"));
        images.add(new Texture("dialog/13_after.png"));


        currentImageIndex = 0; // Đặt hình ảnh đầu tiên là hình đang hiển thị
        currentImage = new Image(images.get(currentImageIndex));

        // Thêm một sự kiện click chuột
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Thay đổi hình ảnh khi click chuột
                changeImage();
                audioManager.playAudio(AudioType.SELECT);
            }
        });

        // Thêm hình ảnh đầu tiên vào UI Stage
        this.add(currentImage).size(200,50).padTop(10);
    }
    
    private void changeImage() {
        // Cập nhật chỉ số hình ảnh hiện tại
        currentImageIndex = (currentImageIndex + 1) % images.size(); // Chuyển đến hình tiếp theo, nếu hết lại quay lại hình đầu tiên

        // Cập nhật Image trong UI
        currentImage.setDrawable(new TextureRegionDrawable(images.get(currentImageIndex)));
    }
}
