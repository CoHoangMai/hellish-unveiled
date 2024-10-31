package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.event.MapChangeEvent;

public class CameraSystem extends IteratingSystem implements EventListener{
	private final Camera camera;
	private float maxW;
	private float maxH;
	
	public CameraSystem(final Main context) {
		super(Family.all(PlayerComponent.class, ImageComponent.class).get());
		
		camera = context.getStage().getCamera();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final ImageComponent imageCmp = ECSEngine.imageCmpMapper.get(entity);
		float viewW = camera.viewportWidth * 0.5f;
		float viewH = camera.viewportHeight * 0.5f;
		float camMinW = Math.min(viewW, maxW - viewW);
		float camMaxW = Math.max(viewW, maxW - viewW);
		float camMinH = Math.min(viewH, maxH - viewH);
		float camMaxH = Math.max(viewH, maxH - viewH);
		
		camera.position.set(MathUtils.clamp(imageCmp.image.getX() + imageCmp.image.getWidth() * 0.5f, camMinW, camMaxW), 
				MathUtils.clamp(imageCmp.image.getY() + imageCmp.image.getHeight() * 0.5f, camMinH, camMaxH), camera.position.z);
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof MapChangeEvent) {
			TiledMap tiledMap = ((MapChangeEvent) event).getMap().getTiledMap();
			TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
			 if (layer != null) {
		            maxW = layer.getWidth();
		            maxH = layer.getHeight();
		        }
			return true;
		}
		return false;
	}

}
