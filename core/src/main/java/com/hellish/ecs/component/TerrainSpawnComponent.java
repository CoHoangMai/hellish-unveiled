package com.hellish.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TerrainSpawnComponent implements Component, Poolable{
	public TextureRegion textureRegion;
	public Vector2 location;
	
	public TerrainSpawnComponent() {
		textureRegion = null;
		location = new Vector2(0, 0);
	}
	
	@Override
	public void reset() {
		textureRegion = null;
		location.set(0, 0);
	}
}
