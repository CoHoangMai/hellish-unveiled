package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool.Poolable;

public class FloatingTextComponent implements Component, Poolable{
	public Vector2 txtLocation;
	public float lifeSpan;
	public Label label;
	public float time;
	public Vector2 txtTarget;
	
	public FloatingTextComponent() {
		txtLocation = new Vector2(0, 0);
		txtTarget = new Vector2(0, 0);
		lifeSpan = 0;
		time = 0;
		label = null;
	}

	@Override
	public void reset() {
		txtLocation.set(0, 0);
		txtTarget.set(0, 0);
		lifeSpan = 0;
		time = 0;
		label = null;
	}
	
	public static class FloatingTextComponentListener implements ComponentListener<FloatingTextComponent> {
		private final Stage uiStage;
		
		public FloatingTextComponentListener(Stage uiStage) {
			this.uiStage = uiStage;
		}
		
		@Override
		public void onComponentAdded(Entity entity, FloatingTextComponent component, Stage stage) {
			component.label.addAction(Actions.fadeOut(component.lifeSpan, Interpolation.pow3OutInverse));
			uiStage.addActor(component.label);
			
			component.txtTarget.set(
				component.txtLocation.x + MathUtils.random(-0.25f, 0.25f),
				component.txtLocation.y + 0.5f
			);
		}

		@Override
		public void onComponentRemoved(Entity entity, FloatingTextComponent component) {
			uiStage.getRoot().removeActor(component.label);
		}
	}
}
