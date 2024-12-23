package com.hellish.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hellish.Main;
import com.hellish.ecs.system.AudioSystem;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.Scene2DSkin.CheckBoxes;
import com.hellish.ui.Scene2DSkin.ImageDrawables;
import com.hellish.ui.Scene2DSkin.Labels;
import com.hellish.ui.Scene2DSkin.Sliders;

public class SettingView extends Table{
   
    private final ImageButton goBackButton;
    public final Sprite backgroundSprite;
    private final Image dimOverlay;
    private final AudioSystem audioSystem = new AudioSystem();
    //private CheckBox fullscreenCheckBox;
    //private SelectBox<String> resolutionSelectBox;

    public SettingView(Skin skin, final Main context) {
        super(skin);
        setFillParent(true);

        Texture backgroundTexture = new Texture("ui/background/background_guide.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundSprite.setPosition(-(Gdx.graphics.getWidth()/2), -(Gdx.graphics.getHeight()/2));
        //Thêm lớp phủ
        dimOverlay = createDimOverlay();
        this.addActor(dimOverlay);
        // Tạo nút
        goBackButton = createButton(ImageDrawables.SMALL_BUTTON_QUIT);
        this.top(); // Đặt bảng chính để định vị từ trên xuống

        Label volumeMusicLabel = new Label("Music Volume", Scene2DSkin.defaultSkin.get(Labels.NORMAL.getSkinKey(), Label.LabelStyle.class));
        Label volumeMusicValueLabel = new Label(String.format("%d%%", (int) (audioSystem.getMusicVolume() * 100)), Scene2DSkin.defaultSkin.get(Labels.NORMAL.getSkinKey(), Label.LabelStyle.class));
        Slider volumeMusicSlider = new Slider(0, 1, 0.01f, false, Scene2DSkin.defaultSkin.get(Sliders.SLIDER.getSkinKey(), SliderStyle.class));
        volumeMusicSlider.setValue(audioSystem.getMusicVolume());

        Label volumeSFXLabel = new Label("SFX Volume", Scene2DSkin.defaultSkin.get(Labels.NORMAL.getSkinKey(), Label.LabelStyle.class));
        Label volumeSFXValueLabel = new Label(String.format("%d%%", (int) (audioSystem.getSoundVolume() * 100)), Scene2DSkin.defaultSkin.get(Labels.NORMAL.getSkinKey(), Label.LabelStyle.class));
        Slider volumeSFXSlider = new Slider(0, 1, 0.01f, false, Scene2DSkin.defaultSkin.get(Sliders.SLIDER.getSkinKey(), SliderStyle.class));
        volumeSFXSlider.setValue(audioSystem.getSoundVolume());

        Table topTable = new Table();
        topTable.add(goBackButton).size(20, 20).pad(4).top().right();

        Table musicTable = new Table();
        musicTable.add(volumeMusicLabel).minWidth(80).padRight(10).padLeft(5).left();
        musicTable.add(volumeMusicSlider).expandX().fill().padRight(10);
        musicTable.add(volumeMusicValueLabel).width(50).row();

        Table SFXTable = new Table();
        SFXTable.add(volumeSFXLabel).minWidth(80).padRight(10).padLeft(5).left();
        SFXTable.add(volumeSFXSlider).expandX().fill().padRight(10);
        SFXTable.add(volumeSFXValueLabel).width(50).row();

        this.add(topTable).expandX().top().right().padTop(10).padRight(10).row();
        this.add(musicTable).expandX().fillX().padTop(10).row();
        this.add(SFXTable).expandX().fillX().padTop(10).row();

        goBackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.setScreen(ScreenType.MAIN_MENU);
            }
        });

        volumeMusicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeMusicSlider.getValue();
                audioSystem.setMusicVolume(volume);  // Thay đổi volume cho nhạc nền
                volumeMusicValueLabel.setText(String.format("%d%%", (int) (volume * 100)));
                System.out.println("Music volume after slider change: " + volume);
            }
        });

        volumeSFXSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSFXSlider.getValue();
                audioSystem.setSoundVolume(volume);
                volumeSFXValueLabel.setText(String.format("%d%%", (int) (audioSystem.getSoundVolume() * 100))); 
            }
        });

        CheckBox x540screenCheckBox = new CheckBox(" 960x540", Scene2DSkin.defaultSkin.get(CheckBoxes.CHECKBOX.getSkinKey(), CheckBox.CheckBoxStyle.class));
        CheckBox x720screenCheckBox = new CheckBox(" 1280x720", Scene2DSkin.defaultSkin.get(CheckBoxes.CHECKBOX.getSkinKey(), CheckBox.CheckBoxStyle.class));
        CheckBox fullscreenCheckBox = new CheckBox(" Full screen", Scene2DSkin.defaultSkin.get(CheckBoxes.CHECKBOX.getSkinKey(), CheckBox.CheckBoxStyle.class));
        x540screenCheckBox.setChecked(true);

        // Thêm listener cho CheckBox để chuyển đổi chế độ toàn màn hình
        fullscreenCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fullscreenCheckBox.isChecked()) {
                    // Lấy độ phân giải hiện tại và chuyển sang chế độ toàn màn hình
                    x540screenCheckBox.setChecked(false);
                    x720screenCheckBox.setChecked(false);
                    DisplayMode currentMode = Gdx.graphics.getDisplayMode();
                    Gdx.graphics.setFullscreenMode(currentMode);
                    //audioSystem.queueSound("audio/sounds/button_click.mp3");
                }
            }
        });

        x540screenCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (x540screenCheckBox.isChecked()) {
                    fullscreenCheckBox.setChecked(false);
                    x720screenCheckBox.setChecked(false);
                    Gdx.graphics.setWindowedMode(960, 540);
                    //audioSystem.queueSound("audio/sounds/button_click.mp3");
                }
            }
        });

        x720screenCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (x720screenCheckBox.isChecked()) {
                    fullscreenCheckBox.setChecked(false);
                    x540screenCheckBox.setChecked(false);
                    Gdx.graphics.setWindowedMode(1280, 720);
                    //audioSystem.queueSound("audio/sounds/button_click.mp3");
                }
            }
        });

        // // Thêm các phần tử vào bảng
        this.add(x540screenCheckBox).padLeft(5).padTop(5).left().row();
        this.add(x720screenCheckBox).padLeft(5).padTop(5).left().row();
        this.add(fullscreenCheckBox).padLeft(5).padTop(5).left().row();
        //this.add(resolutionSelectBox).padBottom(100).left().expand().fill().row();
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
                //audioSystem.queueSound("audio/sounds/button_click.mp3");
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
