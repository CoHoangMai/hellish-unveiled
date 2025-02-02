package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.actor.FlipImage;

public class ImageComponent implements Component, Poolable, Comparable<ImageComponent>{
	public FlipImage image;
	public int layer;
	
	public ImageComponent() {
		image = null;
		layer = 0;
	}

	@Override
	public void reset() {
		image = null;
		layer = 0;
	}

	@Override
	public int compareTo(ImageComponent other) {
		int layerDiff = Integer.compare(this.layer, other.layer);
		if (layerDiff != 0) {
			return layerDiff;
		} else {
			int yDiff = Float.compare(other.image.getY(), this.image.getY());
			if (yDiff != 0) {
				return yDiff;
			} else {
				return Float.compare(other.image.getX(), this.image.getX());
			}
		}
	}
	
	public static class ImageComponentListener implements ComponentListener<ImageComponent>{
        private final Stage stage;

        public ImageComponentListener(Stage stage) {
            this.stage = stage;
        }

		@Override
		public void onComponentAdded(Entity entity, ImageComponent component, Stage stage, World world) {
			stage.addActor(component.image);
		}

		@Override
		public void onComponentRemoved(Entity entity, ImageComponent component) {
			stage.getRoot().removeActor(component.image);
		}
    }
}
