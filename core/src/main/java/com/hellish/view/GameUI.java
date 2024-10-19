package com.hellish.view;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
//import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.hellish.Main;

public class GameUI extends Table {
	public GameUI(final Main context) {
		super(context.getSkin());
		setFillParent(true);
		
		//add(new TextButton("Test màn hình Game", getSkin(), "huge")).expandX().fillX().bottom();
	}
}
