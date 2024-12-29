package com.hellish.ecs.system;

import static com.hellish.Main.UNIT_SCALE;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Scaling;
import com.hellish.actor.FlipImage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.TerrainComponent;
import com.hellish.ecs.component.TerrainSpawnComponent;
import com.hellish.event.MapChangeEvent;

public class TerrainSpawnSystem extends IteratingSystem implements EventListener{
	public static final String TAG = TerrainSpawnSystem.class.getSimpleName();
	
	public TerrainSpawnSystem() {
		super(Family.all(TerrainSpawnComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final TerrainSpawnComponent spawnCmp = ECSEngine.terrainSpawnCmpMapper.get(entity);
		
		final Entity spawnedEntity = getEngine().createEntity();
		
		//Thành phần Image
		final ImageComponent imageCmp = getEngine().createComponent(ImageComponent.class);
		imageCmp.image = new FlipImage(spawnCmp.terrainObj.getTextureRegion());
		imageCmp.image.setPosition(spawnCmp.terrainObj.getX() * UNIT_SCALE, spawnCmp.terrainObj.getY() * UNIT_SCALE);
		imageCmp.image.setSize(
			spawnCmp.terrainObj.getTextureRegion().getRegionWidth() * UNIT_SCALE, 
			spawnCmp.terrainObj.getTextureRegion().getRegionHeight() * UNIT_SCALE
		);
		imageCmp.image.setScaling(Scaling.fill);
		spawnedEntity.add(imageCmp);
		
		//Thành phần Terrain
		TerrainComponent terrainCmp = getEngine().createComponent(TerrainComponent.class);
		terrainCmp.terrainObj = spawnCmp.terrainObj;
		spawnedEntity.add(terrainCmp);
		
		getEngine().addEntity(spawnedEntity);
		
		getEngine().removeEntity(entity);
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof MapChangeEvent) {
			final MapChangeEvent mapChangeEvent = (MapChangeEvent) event;
			if(mapChangeEvent.getTiledMap() == null) {
				return false;
			}
			MapGroupLayer terrainLayer = (MapGroupLayer) mapChangeEvent.getTiledMap().getLayers().get("terrain");
			if (terrainLayer == null) {
				return false;
			}
			for(MapLayer mapLayer : terrainLayer.getLayers()) {
				for (MapObject mapObj : mapLayer.getObjects()) {
					if (! (mapObj instanceof TiledMapTileMapObject)) {
						Gdx.app.log(TAG, "MapObject kiểu " + mapObj + " trong layer 'terrain' không được hỗ trợ.");
						continue;
					}
					TiledMapTileMapObject tiledMapObj = (TiledMapTileMapObject) mapObj;
					Entity entity = getEngine().createEntity();
					TerrainSpawnComponent spawnCmp = getEngine().createComponent(TerrainSpawnComponent.class);
					spawnCmp.terrainObj = tiledMapObj;
					entity.add(spawnCmp);
					getEngine().addEntity(entity);
				}
			}
			return true;	
		}
		return false;
	}
}
