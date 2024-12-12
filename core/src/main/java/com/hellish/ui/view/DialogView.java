package com.hellish.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.audio.AudioManager;
import com.hellish.audio.AudioType;
import com.hellish.ui.Scene2DSkin.Drawables;

public class DialogView extends Table {
    
    private List<Texture> imagesBefore;
    private List<Texture> imagesAfter;
    private List<Texture> mail;
    private ImageButton mailButton;
    private Image currentImage;
    private int currentImageIndex;
    private final AudioManager audioManager;

    public DialogView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        audioManager = context.getAudioManager();

        setBackground(Drawables.DIALOG_BACKGROUND.getAtlasKey());

        imagesBefore = new ArrayList<>();
        imagesBefore.add(new Texture("dialog/01_before.png"));  // Thêm các hình ảnh vào đây
        imagesBefore.add(new Texture("dialog/02_before.png"));
        imagesBefore.add(new Texture("dialog/03_before.png"));
        imagesBefore.add(new Texture("dialog/04_before.png"));
        imagesBefore.add(new Texture("dialog/05_noti.png"));

        mailButton = createButton("dialog/06_noti_mail.png");

        mail = new ArrayList<>();
        mail.add(new Texture("dialog/07_hust_mail.png"));
        mail.add(new Texture("dialog/08_monster_mail.png"));

        imagesAfter = new ArrayList<>();
        imagesAfter.add(new Texture("dialog/09_after.png"));
        imagesAfter.add(new Texture("dialog/10_after.png"));
        imagesAfter.add(new Texture("dialog/11_after.png"));
        imagesAfter.add(new Texture("dialog/12_after.png"));
        imagesAfter.add(new Texture("dialog/13_after.png"));


        currentImageIndex = 0; // Đặt hình ảnh đầu tiên là hình đang hiển thị
        currentImage = new Image(imagesBefore.get(currentImageIndex));

        // Thêm một sự kiện click chuột
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Thay đổi hình ảnh khi click chuột
                //changeImage();
                audioManager.playAudio(AudioType.SELECT);
            }
        });

        // Thêm hình ảnh đầu tiên vào UI Stage
        this.add(currentImage).size(200,50).padTop(10);
    }
    
    /*private void changeImage() {
        currentImageIndex++;
        if (currentImageIndex < imagesBefore.size()) currentImage.setDrawable(new TextureRegionDrawable(imagesBefore.get(currentImageIndex)));
        // Cập nhật chỉ số hình ảnh hiện tại
        
        currentImageIndex = (currentImageIndex + 1) % images.size(); // Chuyển đến hình tiếp theo, nếu hết lại quay lại hình đầu tiên

        // Cập nhật Image trong UI
        currentImage.setDrawable(new TextureRegionDrawable(images.get(currentImageIndex)));
    }*/

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
