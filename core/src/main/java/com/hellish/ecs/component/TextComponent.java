package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TextComponent implements Component, Poolable{
	public Vector2 txtLocation;
	public Label label;
	
	public TextComponent() {
		txtLocation = new Vector2();
		label = null;
	}

	@Override
	public void reset() {
		txtLocation.set(0, 0);
		label = null;
	}
}
