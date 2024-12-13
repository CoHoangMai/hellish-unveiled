package com.hellish.map;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.event.MapChangeEvent;

public class MapManager {
	public static final String TAG = MapManager.class.getSimpleName();
	
	private final AssetManager assetManager;
	private final Stage gameStage;
	
	private MapType currentMapType;
	private TiledMap currentMap;
	private final EnumMap<MapType, TiledMap> mapCache;

	public MapManager(final Main context) {
		currentMapType = MapType.NO_MAP;
		currentMap = null;
		gameStage = context.getGameStage();
		assetManager = context.getAssetManager();
		mapCache = new EnumMap<MapType, TiledMap>(MapType.class);
	}
	
	public void setMap(final MapType type) {
		if(currentMapType == type) {
			return;
		}
		
		currentMap = mapCache.get(type);
		if(currentMap == null) {
			Gdx.app.debug(TAG, "Tạo map mới " + type);
			final TiledMap tiledMap = (type == MapType.NO_MAP) ? null : assetManager.get(type.getFilePath(), TiledMap.class);
			currentMap = tiledMap;
			mapCache.put(type, currentMap);
		}
		currentMapType = type;
		Gdx.app.debug(TAG, "Map hiện tại: " + type);
		
		MapChangeEvent mapChangeEvent = MapChangeEvent.pool.obtain().set(currentMap);
		gameStage.getRoot().fire(mapChangeEvent);
		MapChangeEvent.pool.free(mapChangeEvent);
	}
	
	public TiledMap getCurrentMap() {
		return currentMap;
	}	
}
