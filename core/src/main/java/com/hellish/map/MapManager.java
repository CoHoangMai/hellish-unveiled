package com.hellish.map;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.hellish.Main;
import com.hellish.event.EventUtils;
import com.hellish.event.MapChangeEvent;

public class MapManager implements Disposable{
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
		currentMap = mapCache.get(type);
		if(currentMap == null) {
			Gdx.app.debug(TAG, "Tạo map mới " + type);
			final TiledMap tiledMap = (type == MapType.NO_MAP) ? null : assetManager.get(type.getFilePath(), TiledMap.class);
			currentMap = tiledMap;
			mapCache.put(type, currentMap);
		}
		
		EventUtils.fireEvent(gameStage, MapChangeEvent.pool, event -> event.set(currentMap));
		
		currentMapType = type;
		
		Gdx.app.debug(TAG, "Map hiện tại: " + type);
	}
	
	public TiledMap getCurrentMap() {
		return currentMap;
	}
	
	public MapType getCurrentMapType() {
		return currentMapType;
	}

	@Override
	public void dispose() {
		for(TiledMap map : mapCache.values()) {
			if(map != null) {
				map.dispose();
			}
		}
		mapCache.clear();
	}	
}
