package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
	
	public static class TextComponentListener implements ComponentListener<TextComponent> {
		private final Stage uiStage;
		
		public TextComponentListener(Stage uiStage) {
			this.uiStage = uiStage;
		}
		
		@Override
		public void onComponentAdded(Entity entity, TextComponent component, Stage stage, World world) {
			uiStage.addActor(component.label);
		}

		@Override
		public void onComponentRemoved(Entity entity, TextComponent component) {
			uiStage.getRoot().removeActor(component.label);
		}
	}
}
