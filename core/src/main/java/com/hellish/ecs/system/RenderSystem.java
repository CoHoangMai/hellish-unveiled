package com.hellish.ecs.system;

import java.util.ArrayList;
import java.util.Collections;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
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
	private final Stage gameStage;
	private final Stage uiStage;
	private final ArrayList<Entity> entitiesArray;
	private final ArrayList<TiledMapTileLayer> backgroundLayers;
	private final ArrayList<TiledMapTileLayer> foregroundLayers;
	private final OrthogonalTiledMapRenderer mapRenderer;
	private final OrthographicCamera orthoCam;
	private final RayHandler rayHandler;

	public RenderSystem(final Main context) {
		super(Family.all(ImageComponent.class).get());
		gameStage = context.getGameStage();
		uiStage = context.getUIStage();
		entitiesArray = new ArrayList<>();
		backgroundLayers = new ArrayList<>();
		foregroundLayers = new ArrayList<>();
		orthoCam = (OrthographicCamera)gameStage.getCamera();
		mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, gameStage.getBatch());
		rayHandler = context.getRayHandler();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		entitiesArray.add(entity);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void update(float deltaTime) {
		entitiesArray.clear();
		super.update(deltaTime);
		
		final ComponentMapper<ImageComponent> imageCmpMapper = ECSEngine.imageCmpMapper;
		Collections.sort(entitiesArray, (e1, e2) -> imageCmpMapper.get(e1).compareTo(imageCmpMapper.get(e2)));

		gameStage.getViewport().apply();
		AnimatedTiledMapTile.updateAnimationBaseTime();
		mapRenderer.setView(orthoCam);
		
		if (!backgroundLayers.isEmpty()) {
			gameStage.getBatch().setColor(Color.WHITE);
			gameStage.getBatch().setProjectionMatrix(orthoCam.combined);
			gameStage.getBatch().begin();
			for (TiledMapTileLayer layer : backgroundLayers) {
				mapRenderer.renderTileLayer(layer);
			}
			gameStage.getBatch().end();
		}
	    for (Entity entity : entitiesArray) {
	    	if(imageCmpMapper.get(entity).image.getParent() == null) {
				gameStage.addActor(imageCmpMapper.get(entity).image);
			}
	    	
	    	boolean isPlayer = ECSEngine.playerCmpMapper.has(entity);
	    	if(isPlayer) {
	    		for(Entity otherEntity : entitiesArray) {
	    			if(entity != otherEntity && ECSEngine.terrainCmpMapper.has(otherEntity)) {
	    				ImageComponent thisImageCmp = ECSEngine.imageCmpMapper.get(entity);
	    				ImageComponent otherImageCmp = ECSEngine.imageCmpMapper.get(otherEntity);
	    				if(isEntityObscured(thisImageCmp, otherImageCmp)) {
	    					otherImageCmp.image.setColor(1f, 1f, 1f, 0.5f);
	    				} else {
	    					otherImageCmp.image.setColor(1f, 1f, 1f, 1f); 
	    				}
	    			}
	    		}
	    	}
	    	
	    	imageCmpMapper.get(entity).image.toFront();
	    }

		gameStage.act(deltaTime);
		gameStage.draw();

		
		
		if(!foregroundLayers.isEmpty()) {
			gameStage.getBatch().setColor(Color.WHITE);
			gameStage.getBatch().setProjectionMatrix(orthoCam.combined);
			gameStage.getBatch().begin();
			for (TiledMapTileLayer layer : foregroundLayers) {
				mapRenderer.renderTileLayer(layer);
			}
			gameStage.getBatch().end();
		}
		
		rayHandler.setCombinedMatrix(orthoCam.combined);
		rayHandler.update();
		rayHandler.render();

		uiStage.getViewport().apply();
		uiStage.act(deltaTime);
		uiStage.draw();	
	}


	@Override
	public boolean handle(Event event) {
		if (event instanceof MapChangeEvent) {
			final MapChangeEvent mapChangeEvent = (MapChangeEvent) event;
			
			backgroundLayers.clear();
			foregroundLayers.clear();
			
			if(mapChangeEvent.getTiledMap() == null) {
				return false;
			}
			
			mapChangeEvent.getTiledMap().getLayers().forEach(layer -> {
				if (layer instanceof TiledMapTileLayer) {
					TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
					if(tiledLayer.getName().startsWith("fgd_")) {
						foregroundLayers.add(tiledLayer);
					} else {
						backgroundLayers.add(tiledLayer);
					}
				}
			});
			
			mapChangeEvent.getTiledMap().getLayers().forEach(layer -> {
				if(layer instanceof MapGroupLayer) {
					MapGroupLayer groupLayer = (MapGroupLayer) layer;
					if(groupLayer.getName().equals("bgd")) {
						for(MapLayer mapLayer : groupLayer.getLayers()) {
							if (mapLayer instanceof TiledMapTileLayer) {
			                    TiledMapTileLayer tiledLayer = (TiledMapTileLayer) mapLayer;
			                    backgroundLayers.add(tiledLayer);
			                }
						}
					} else if(groupLayer.getName().equals("fgd")) {
						for(MapLayer mapLayer : groupLayer.getLayers()) {
							if (mapLayer instanceof TiledMapTileLayer) {
			                    TiledMapTileLayer tiledLayer = (TiledMapTileLayer) mapLayer;
			                    foregroundLayers.add(tiledLayer);
			                }
						}
					}
				}
			});
			return true;	
		}
		return false;
	}
	
	 public boolean isEntityObscured(ImageComponent thisImageComponent, ImageComponent otherImageComponent) {
		 int comparison = thisImageComponent.compareTo(otherImageComponent);
		 
		 if(comparison > 0) {
			 return false;
		 }
		 
		 Rectangle thisBoundingBox = new Rectangle(
			thisImageComponent.image.getX(),
			thisImageComponent.image.getY(),
			thisImageComponent.image.getWidth(),
			thisImageComponent.image.getHeight()
		);
		 
		 Rectangle otherBoundingBox = new Rectangle(
			otherImageComponent.image.getX(),
			otherImageComponent.image.getY(),
			otherImageComponent.image.getWidth(),
			otherImageComponent.image.getHeight()
		);
		 
		 return thisBoundingBox.overlaps(otherBoundingBox);
	}
	
	@Override
	public void dispose() {
		mapRenderer.dispose();
	}
}
