package com.hellish.map;

import java.util.EnumMap;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.event.MapChangeEvent;

public class MapManager {
	public static final String TAG = MapManager.class.getSimpleName();
	
	private final AssetManager assetManager;
	private final ECSEngine ecsEngine;
	private final Stage stage;
	
	private MapType currentMapType;
	private Map currentMap;
	private final EnumMap<MapType, Map> mapCache;

	public MapManager(final Main context) {
		currentMapType = null;
		currentMap = null;
		stage = context.getStage();
		ecsEngine = context.getECSEngine();
		assetManager = context.getAssetManager();
		mapCache = new EnumMap<MapType, Map>(MapType.class);
	}
	
	public void setMap(final MapType type) {
		if(currentMapType == type) {
			return;
		}
		if(currentMap != null) {
			//world.getBodies(bodies);
			//destroyCollisionAreas();
			//destroyGameObjects();
		}
		
		currentMap = mapCache.get(type);
		if(currentMap == null) {
			Gdx.app.debug(TAG, "Tạo map mới " + type);
			final TiledMap tiledMap = assetManager.get(type.getFilePath(), TiledMap.class);
			currentMap = new Map(tiledMap);
			mapCache.put(type, currentMap);
		}
		Gdx.app.debug(TAG, "Map hiện tại: " + type);
		
		for (EntitySystem system : ecsEngine.getSystems()) {
			if(system instanceof EventListener) {
				stage.addListener((EventListener) system);
			}
		}
		//NOTE: không dùng Pooling nên phần này đang không tối ưu
		MapChangeEvent mapChangeEvent = new MapChangeEvent(currentMap);
		stage.getRoot().fire(mapChangeEvent);
	}
	
	public Map getCurrentMap() {
		return currentMap;
	}	
}
