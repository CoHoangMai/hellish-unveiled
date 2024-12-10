package com.hellish.ui.view;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.hellish.ui.Scene2DSkin.Labels;

public class PauseView extends Table{
	private static final String PIXMAP_KEY = "pauseTexture";
	
	public PauseView(Skin skin) {
		super(skin);
		setFillParent(true);
		
		if(!skin.has(PIXMAP_KEY, TextureRegionDrawable.class)) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0.1f, 0.1f, 0.1f, 0.7f);
            pixmap.fillRectangle(0, 0, 1, 1);
			Texture texture = new Texture(pixmap);
			skin.add(PIXMAP_KEY, new TextureRegionDrawable(texture));
		}

		setBackground(skin.get(PIXMAP_KEY, TextureRegionDrawable.class));
		
		Label pauseLabel = new Label("[RED]Xì tóp", skin.get(Labels.LARGE.getSkinKey(), LabelStyle.class));
		pauseLabel.setAlignment(Align.center);
		add(pauseLabel).expand().fill();
	}
}