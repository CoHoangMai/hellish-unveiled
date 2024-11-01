package com.hellish.ecs.system;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.event.MapChangeEvent;

import static com.hellish.Main.UNIT_SCALE;
	
public class RenderSystem extends IteratingSystem implements Disposable, EventListener{
	private final Stage stage;
	private final ArrayList<Entity> entitiesArray;
	private final ArrayList<TiledMapTileLayer> backgroundLayers;
	private final ArrayList<TiledMapTileLayer> foregroundLayers;
	private final OrthogonalTiledMapRenderer mapRenderer;
	private final OrthographicCamera orthoCam;

	public RenderSystem(final Main context) {
		super(Family.all(ImageComponent.class).get());
		stage = context.getStage();
		entitiesArray = new ArrayList<>();
		backgroundLayers = new ArrayList<>();
		foregroundLayers = new ArrayList<>();
		orthoCam = (OrthographicCamera)stage.getCamera();
		mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, stage.getBatch());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		entitiesArray.add(entity);
		
	}
	
	@Override
	public void update(float deltaTime) {
		entitiesArray.clear();
		super.update(deltaTime);
		
		final ComponentMapper<ImageComponent> imageCmpMapper = ECSEngine.imageCmpMapper;
		Collections.sort(entitiesArray, (e1, e2) -> imageCmpMapper.get(e1).compareTo(imageCmpMapper.get(e2)));

		stage.getViewport().apply();
		AnimatedTiledMapTile.updateAnimationBaseTime();
		mapRenderer.setView(orthoCam);
		
		if (!backgroundLayers.isEmpty()) {
			stage.getBatch().setColor(Color.WHITE);
			stage.getBatch().setProjectionMatrix(orthoCam.combined);
			stage.getBatch().begin();
			for (TiledMapTileLayer layer : backgroundLayers) {
				mapRenderer.renderTileLayer(layer);
			}
			stage.getBatch().end();
		}
	    for (Entity entity : entitiesArray) {
	    	if(imageCmpMapper.get(entity).image.getParent() == null) {
				stage.addActor(imageCmpMapper.get(entity).image);
			}
	    	imageCmpMapper.get(entity).image.toFront();
	    }

		stage.act(deltaTime);
		stage.draw();
		
		//Nếu không remove thì image luôn hiện lên phía trước mọi layer
		for (Entity entity : entitiesArray) {
			stage.getRoot().removeActor(imageCmpMapper.get(entity).image);
		}
		
		if(!foregroundLayers.isEmpty()) {
			stage.getBatch().setColor(Color.WHITE);
			stage.getBatch().setProjectionMatrix(orthoCam.combined);
			stage.getBatch().begin();
			for (TiledMapTileLayer layer : foregroundLayers) {
				mapRenderer.renderTileLayer(layer);
			}
			stage.getBatch().end();
		}
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof MapChangeEvent) {
			final MapChangeEvent mapChangeEvent = (MapChangeEvent) event;
			
			backgroundLayers.clear();
			foregroundLayers.clear();
			
			mapChangeEvent.getMap().getTiledMap().getLayers().forEach(layer -> {
				if (layer instanceof TiledMapTileLayer) {
					TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
					if(tiledLayer.getName().startsWith("fgd_")) {
						foregroundLayers.add(tiledLayer);
					} else {
						backgroundLayers.add(tiledLayer);
					}
				}
			});
			return true;	
		}
		return false;
	}
	
	@Override
	public void dispose() {
		mapRenderer.dispose();
	}
}
