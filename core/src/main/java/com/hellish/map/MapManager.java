package com.hellish.map;

import static com.hellish.Main.BIT_GROUND;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;

public class MapManager {
	public static final String TAG = MapManager.class.getSimpleName();
	
	private final World world;
	private final Array<Body> bodies;
	
	private final AssetManager assetManager;
	
	private MapType currentMapType;
	private Map currentMap;
	private final EnumMap<MapType, Map> mapCache;
	private final Array<MapListener> listeners;

	public MapManager(final Main context) {
		currentMapType = null;
		currentMap = null;
		world = context.getWorld();
		assetManager = context.getAssetManager();
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
		
		for (final MapListener listener : listeners) {
			listener.mapChange(currentMap);
		}
	}
	
	private void destroyCollisionAreas() {
		for(final Body body: bodies) {
			if(body.getUserData().equals("GROUND")) {
				world.destroyBody(body);
			}
		}
	}
	
	private void spawnCollisionAreas() {
		Main.resetBodiesAndFixtureDefinition();
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
