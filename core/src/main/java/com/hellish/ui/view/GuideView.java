package com.hellish.ui.view;

import static com.hellish.ui.Scene2DSkin.OVERLAY_KEY;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.screen.MainMenuScreen;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.Scene2DSkin.ImageDrawables;

public class GuideView extends Table{
    private final ImageButton goBackButton;
    private final Image guideImage;

    public GuideView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);
        
        setBackground(Drawables.GUIDE_BACKGROUND.getAtlasKey());
		
		Image overlay = new Image(skin.get(OVERLAY_KEY, TextureRegionDrawable.class));
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlay.setPosition(0, 0);
		this.addActor(overlay);
		
        // Tạo nút
        goBackButton = Scene2DSkin.createButton(ImageDrawables.SMALL_BUTTON_QUIT, 25, 20);
        // Thêm guide
        Texture texture = new Texture("dialog/guide.png");
        guideImage = new Image(texture);

        this.top(); // Đặt bảng chính để định vị từ trên xuống

        // Tạo bảng con cho nút quay lại
        Table topTable = new Table();
        topTable.add(goBackButton).size(20, 20).pad(4).top().right();

        // Tạo bảng con cho ảnh hướng dẫn
        

        // Thêm các bảng con vào bảng chính
        this.add(topTable).expandX().top().right().padTop(10).padRight(10).row();
        this.add(guideImage);

        //Tạo event cho nút
        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	((MainMenuScreen) context.getScreen()).showGuideView(false);
            }
        });
    }
}
