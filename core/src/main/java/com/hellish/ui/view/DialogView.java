package com.hellish.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.audio.AudioManager;
import com.hellish.audio.AudioType;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.Scene2DSkin.ImageDrawables;

public class DialogView extends Table {

    private final List<Texture> imagesBefore;
    private final List<Texture> imagesAfter;
    private final List<Texture> mail;
    private final ImageButton mailButton;
    private final ImageButton startButton;
    private Image currentImage;
    private int currentImageIndex;
    private final Stack stack; // Sử dụng Stack
    private final AudioManager audioManager;

    public DialogView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        audioManager = context.getAudioManager();

        setBackground(Drawables.DIALOG_BACKGROUND.getAtlasKey());

        // Khởi tạo Stack
        stack = new Stack();
        this.add(stack).size(200,200).top().row(); // Thêm Stack vào Table

        // Tạo danh sách hình ảnh
        imagesBefore = new ArrayList<>();
        imagesBefore.add(new Texture("dialog/01_before.png"));
        imagesBefore.add(new Texture("dialog/02_before.png"));
        imagesBefore.add(new Texture("dialog/03_before.png"));
        imagesBefore.add(new Texture("dialog/04_before.png"));
        imagesBefore.add(new Texture("dialog/05_noti.png"));

        // Tạo mailButton
        mailButton = createButton(ImageDrawables.MAIL_BUTTON);

        mail = new ArrayList<>();
        mail.add(new Texture("dialog/07_hust_mail.png"));
        mail.add(new Texture("dialog/08_monster_mail.png"));

        imagesAfter = new ArrayList<>();
        imagesAfter.add(new Texture("dialog/09_after.png"));
        imagesAfter.add(new Texture("dialog/10_after.png"));
        imagesAfter.add(new Texture("dialog/11_after.png"));
        imagesAfter.add(new Texture("dialog/12_after.png"));
        imagesAfter.add(new Texture("dialog/13_after.png"));

        startButton = createButton(ImageDrawables.BIG_BUTTON_PLAY);

        currentImageIndex = 0;

        // Hiển thị hình ảnh đầu tiên
        currentImage = new Image(imagesBefore.get(currentImageIndex));
        Container<Image> imageContainer = new Container<>(currentImage);
        imageContainer.size(200, 50); // Đặt kích thước cho Container
        stack.add(imageContainer);

        // Thêm sự kiện click để thay đổi hình ảnh
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeImage();
                audioManager.playAudio(AudioType.SELECT);
            }

        });

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.GAME);
            }

        });
    }

    private void changeImage() {
        currentImageIndex++;

        if (currentImageIndex < imagesBefore.size()) {
            // Thay thế hình ảnh hiện tại
            currentImage.remove(); // Loại bỏ hình cũ khỏi Stack
            currentImage = new Image(imagesBefore.get(currentImageIndex));
            Container<Image> imageContainer = new Container<>(currentImage);
            imageContainer.size(200, 50); // Đặt kích thước cho Container
            stack.add(imageContainer);
            System.out.println("Ảnh " + currentImageIndex);
        } else if (currentImageIndex == imagesBefore.size()) {
            // Hiển thị nút mailButton
            Container<ImageButton> buttonContainer = new Container<>(mailButton);
            buttonContainer.size(60, 60);
            buttonContainer.left().bottom().padBottom(20);
            stack.add(buttonContainer);
            System.out.println("Button");
        } else if (currentImageIndex - imagesBefore.size() - 1< mail.size()) {
            currentImage.remove();
            mailButton.remove(); // Loại bỏ hình cũ khỏi Stack
            currentImage = new Image(mail.get(currentImageIndex - imagesBefore.size() - 1));
            Container<Image> imageContainer = new Container<>(currentImage);
            imageContainer.size(150, 100); // Đặt kích thước cho Container
            stack.add(imageContainer);
            System.out.println("Ảnh " + currentImageIndex);
        } else if (currentImageIndex - imagesBefore.size() - mail.size() - 1 < imagesAfter.size()) {
            currentImage.remove(); // Loại bỏ hình cũ khỏi Stack
            currentImage = new Image(imagesAfter.get(currentImageIndex - imagesBefore.size() - mail.size() - 1));
            Container<Image> imageContainer = new Container<>(currentImage);
            imageContainer.size(200, 50); // Đặt kích thước cho Container
            stack.add(imageContainer);
            System.out.println("Ảnh " + currentImageIndex);
        } else {
            Container<ImageButton> buttonContainer = new Container<>(startButton);
            buttonContainer.size(60, 60);
            buttonContainer.center().bottom().padBottom(10);
            stack.add(buttonContainer);
            System.out.println("Button");
        }
    }

    private ImageButton createButton(ImageDrawables image) {
        Texture texture = new Texture(image.getFileName());
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
