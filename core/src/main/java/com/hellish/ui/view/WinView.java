package com.hellish.ui.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.hellish.Main;
import com.hellish.ui.Scene2DSkin.Drawables;

public class WinView extends Table{
	private final Image congratulation;
	
	public WinView(Skin skin, final Main context) {
		super(skin);
		setFillParent(true);
	    
		setBackground(Drawables.GUIDE_BACKGROUND.getAtlasKey());
		
		congratulation = new Image(new Texture("ui/CONGRATULATION.png"));

	    add(congratulation);
	}
}
