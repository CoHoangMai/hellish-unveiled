package com.hellish.map;

import static com.hellish.Main.BIT_GROUND;

import java.util.EnumMap;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.event.MapChangeEvent;

public class MapManager {
	public static final String TAG = MapManager.class.getSimpleName();
	
	private final World world;
	private final Array<Body> bodies;
	
	private final AssetManager assetManager;
	private final ECSEngine ecsEngine;
	private final Stage stage;
	private final Array<Entity> gameObjectsToRemove;
	
	private MapType currentMapType;
	private Map currentMap;
	private final EnumMap<MapType, Map> mapCache;
	private final Array<MapListener> listeners;

	public MapManager(final Main context) {
		currentMapType = null;
		currentMap = null;
		world = context.getWorld();
		stage = context.getStage();
		ecsEngine = context.getECSEngine();
		assetManager = context.getAssetManager();
		gameObjectsToRemove = new Array<Entity>();
		bodies = new Array<Body>();
		mapCache = new EnumMap<MapType, Map>(MapType.class);
		listeners = new Array<MapListener>();
	}
	
	public void addMapListener(final MapListener listener) { 
		listeners.add(listener);
	}
	
	public void setMap(final MapType type) {
		if(currentMapType == type) {
			return;
		}
		if(currentMap != null) {
			world.getBodies(bodies);
			destroyCollisionAreas();
			destroyGameObjects();
		}
		
		currentMap = mapCache.get(type);
		if(currentMap == null) {
			Gdx.app.debug(TAG, "Tạo map mới " + type);
			final TiledMap tiledMap = assetManager.get(type.getFilePath(), TiledMap.class);
			currentMap = new Map(tiledMap);
			mapCache.put(type, currentMap);
		}
		Gdx.app.debug(TAG, "Map hiện tại: " + type);
		
		spawnCollisionAreas();
		spawnGameObjects();
		
		//NOTE: không dùng Pooling nên phần này đang không tối ưu
		for (EntitySystem system : ecsEngine.getSystems()) {
			if(system instanceof EventListener) {
				stage.addListener((EventListener) system);
			}
		}	
		MapChangeEvent mapChangeEvent = new MapChangeEvent(currentMap);
		stage.getRoot().fire(mapChangeEvent);
		
		
		for (final MapListener listener : listeners) {
			listener.mapChange(currentMap);
		}
	}
	
	private void spawnGameObjects() {
		for (final GameObject gameObj : currentMap.getGameObjects()) {
			ecsEngine.createGameObject(gameObj);
		}
	}

	private void destroyGameObjects() {
		for (final Entity entity : ecsEngine.getEntities()) {
			if(ECSEngine.gameObjCmpMapper.get(entity) != null) {
				gameObjectsToRemove.add(entity);
			}
		}
		for (final Entity entity : gameObjectsToRemove) {
			ecsEngine.removeEntity(entity);
		}
		gameObjectsToRemove.clear();
	}

	private void destroyCollisionAreas() {
		for(final Body body: bodies) {
			if(body.getUserData().equals("GROUND")) {
				world.destroyBody(body);
			}
		}
	}
	
	private void spawnCollisionAreas() {
		Main.resetBodyAndFixtureDefinition();
		for(final CollisionArea collisionArea : currentMap.getCollisionAreas()) {
			Main.BODY_DEF.position.set(collisionArea.getX(), collisionArea.getY());
			Main.BODY_DEF.fixedRotation = true;
			final Body body = world.createBody(Main.BODY_DEF);
			body.setUserData("GROUND");
			
			Main.FIXTURE_DEF.filter.categoryBits = BIT_GROUND;
			Main.FIXTURE_DEF.filter.maskBits = -1;
			final ChainShape chainShape = new ChainShape();
			chainShape.createChain(collisionArea.getVertices());
			Main.FIXTURE_DEF.shape = chainShape;
			body.createFixture(Main.FIXTURE_DEF);
			chainShape.dispose();
		}
	}
	
	public Map getCurrentMap() {
		return currentMap;
	}
	
}
