package com.hellish.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

public class FlipImage extends Image{
	private boolean flipX = false;
	
	public FlipImage() {
		super();
	}
	
	public FlipImage(TextureRegion region) {
		super(region);
	}
	
	public boolean isFlipX() {
		return flipX;
	}
	
	public void setFlipX(boolean flipX) {
		this.flipX = flipX;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
		batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
		
		TransformDrawable toDraw = (TransformDrawable) getDrawable();
		if(toDraw != null && (getScaleX() != 1f || getScaleY() != 1f || getRotation() != 0f)) {
			toDraw.draw(
				batch, 
				(flipX ? getX() + getImageX() * getScaleX() : getX() + getImageX()),
				getY() + getImageY(), 
				getOriginX() - getImageX(), 
				getOriginY() - getImageY(), 
				getImageWidth(), 
				getImageHeight(), 
				(flipX ? -getScaleX() : getScaleX()), 
				getScaleY(), 
				getRotation()
			);
		} else {
			if (toDraw != null) {
				toDraw.draw(
					batch, 
					(flipX ? getX() + getImageX() + getImageWidth() * getScaleX() : getX() + getImageX()), 
					getY() + getImageY(),
					(flipX ? -getImageWidth() * getScaleX() : getImageWidth() * getScaleX()), 
					getImageHeight() * getScaleY()
				);
			}
		}
	}
}
