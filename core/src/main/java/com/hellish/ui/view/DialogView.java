package com.hellish.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.event.EventUtils;
import com.hellish.event.SelectEvent;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.Scene2DSkin.ImageDrawables;

public class DialogView extends Table {
	
    private final Stage gameStage;

    private final List<Texture> imagesBefore;
    private final List<Texture> imagesAfter;
    private final List<Texture> mail;
    private final ImageButton mailButton;
    private final ImageButton startButton;
    private Image currentImage;
    private int currentImageIndex;
    private final Stack stack; // Sử dụng Stack

    public DialogView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        gameStage = context.getGameStage();

        setBackground(Drawables.DIALOG_BACKGROUND.getAtlasKey());

        stack = new Stack();
        this.add(stack).center().padTop(50).row();

        imagesBefore = new ArrayList<>();
        imagesBefore.add(new Texture("dialog/01_before.png"));
        imagesBefore.add(new Texture("dialog/02_before.png"));
        imagesBefore.add(new Texture("dialog/03_before.png"));
        imagesBefore.add(new Texture("dialog/04_before.png"));
        imagesBefore.add(new Texture("dialog/05_noti.png"));

        mailButton = Scene2DSkin.createButton(ImageDrawables.MAIL_BUTTON, 70, 60);

        mail = new ArrayList<>();
        mail.add(new Texture("dialog/07_hust_mail.png"));
        mail.add(new Texture("dialog/08_monster_mail.png"));

        imagesAfter = new ArrayList<>();
        imagesAfter.add(new Texture("dialog/09_after.png"));
        imagesAfter.add(new Texture("dialog/10_after.png"));
        imagesAfter.add(new Texture("dialog/11_after.png"));
        imagesAfter.add(new Texture("dialog/12_after.png"));
        imagesAfter.add(new Texture("dialog/13_after.png"));

        startButton = new ImageButton(new TextureRegionDrawable(new Texture(ImageDrawables.BIG_BUTTON_PLAY.getFileName())));

        currentImageIndex = 0;

        currentImage = new Image(imagesBefore.get(currentImageIndex));
        stack.add(currentImage);

        // Thêm sự kiện click để thay đổi hình ảnh
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeImage();
                EventUtils.fireEvent(gameStage, SelectEvent.pool, e -> {});
            }

        });

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.GAME);
            }

        });
    }

    public void changeImage() {
        currentImageIndex++;

        if (currentImageIndex < imagesBefore.size()) {
        	if(currentImage.getDrawable() instanceof TextureRegionDrawable) {
        		Texture texture = ((TextureRegionDrawable) currentImage.getDrawable()).getRegion().getTexture();
        		texture.dispose();
        	}
            currentImage.remove();
            currentImage = new Image(imagesBefore.get(currentImageIndex));
            stack.add(currentImage);
        } else if (currentImageIndex == imagesBefore.size()) {
            stack.add(mailButton);
        } else if (currentImageIndex - imagesBefore.size() - 1< mail.size()) {
        	if(currentImage.getDrawable() instanceof TextureRegionDrawable) {
        		Texture texture = ((TextureRegionDrawable) currentImage.getDrawable()).getRegion().getTexture();
        		texture.dispose();
        	}
            currentImage.remove();
            mailButton.remove();
            currentImage = new Image(mail.get(currentImageIndex - imagesBefore.size() - 1));
            stack.add(currentImage);
        } else if (currentImageIndex - imagesBefore.size() - mail.size() - 1 < imagesAfter.size()) {
        	if(currentImage.getDrawable() instanceof TextureRegionDrawable) {
        		Texture texture = ((TextureRegionDrawable) currentImage.getDrawable()).getRegion().getTexture();
        		texture.dispose();
        	}
            currentImage.remove();
            currentImage = new Image(imagesAfter.get(currentImageIndex - imagesBefore.size() - mail.size() - 1));
            stack.add(currentImage);
        } else {
        	if(currentImage.getDrawable() instanceof TextureRegionDrawable) {
        		Texture texture = ((TextureRegionDrawable) currentImage.getDrawable()).getRegion().getTexture();
        		texture.dispose();
        	}
            currentImage.remove();
            stack.add(startButton);
        }
    }
}
