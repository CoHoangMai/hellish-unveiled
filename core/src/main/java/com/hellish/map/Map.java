package com.hellish.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import static com.hellish.Main.UNIT_SCALE;;

public class Map {
	public static final String TAG = Map.class.getSimpleName();
	
	private final TiledMap tiledMap;
	private final Array<CollisionArea> collisionAreas;
	private final Vector2 startLocation;

	private Array<GameObject> gameObjects;

	private IntMap<Animation<Sprite>> mapAnimations;
	
	public Map(final TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		collisionAreas = new Array<CollisionArea>();
		
		parseCollisionLayer();
		startLocation = new Vector2();
		parsePlayerStartLocation();
		
		gameObjects = new Array<GameObject>();
		mapAnimations = new IntMap<Animation<Sprite>>();
		parseGameObjectLayer();
	}

	private void parsePlayerStartLocation() {
		final MapLayer startLocationLayer = tiledMap.getLayers().get("playerStartLocation");
		if (startLocationLayer == null) {
			Gdx.app.debug(TAG, "Không có layer playerStartLocation.");
			return;
		}
		
		final MapObjects objects = startLocationLayer.getObjects();
		for(final MapObject mapObj: objects) {
			if(mapObj instanceof RectangleMapObject) {
				final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObj;
				final Rectangle rectangle = rectangleMapObject.getRectangle();
				startLocation.set(rectangle.x * UNIT_SCALE, rectangle.y * UNIT_SCALE);
			} else {
				Gdx.app.debug(TAG, "MapObject kiểu " + mapObj + "chưa được code vào parsePlayerStartPostion().");
			}
		}
	}

	private void parseCollisionLayer() {
		final MapLayer collisionLayer = tiledMap.getLayers().get("collision");
		if(collisionLayer == null) {
			Gdx.app.debug(TAG, "Không có collision layer.");
			return;
		}
		
		for(final MapObject mapObj : collisionLayer.getObjects()) {
			if(mapObj instanceof RectangleMapObject) {
				final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObj;
				final Rectangle rectangle = rectangleMapObject.getRectangle();
				final float[] rectVertices = new float[10];
				
				//Đỉnh trái dưới
				rectVertices[0] = 0;
				rectVertices[1] = 0;
				
				//Đỉnh trái trên
				rectVertices[2] = 0;
				rectVertices[3] = rectangle.height;
				
				//Đỉnh phải trên
				rectVertices[4] = rectangle.width;
				rectVertices[5] = rectangle.height;
				
				//Đỉnh phải dưới
				rectVertices[6] = rectangle.width;
				rectVertices[7] = 0;
				
				//Đỉnh trái dưới
				rectVertices[8] = 0;
				rectVertices[9] = 0;
				
				collisionAreas.add(new CollisionArea(rectangle.x, rectangle.y, rectVertices));
				
			} else if (mapObj instanceof PolylineMapObject) {
				final PolylineMapObject polylineMapObject = (PolylineMapObject) mapObj;
				final Polyline polyline = polylineMapObject.getPolyline();
				collisionAreas.add( new CollisionArea(polyline.getX(), polyline.getY(), polyline.getVertices()));
			} else {
				Gdx.app.debug(TAG, "MapObject kiểu " + mapObj + "chưa được code vào parseCollisionLayer().");
			}
		}			
	}
	
	private void parseGameObjectLayer() {
		final MapLayer gameObjectsLayer = tiledMap.getLayers().get("gameObjects");
		if (gameObjectsLayer == null) {
			Gdx.app.debug(TAG, "Không có layer gameObjects.");
			return;
		}
		
		final MapObjects objects = gameObjectsLayer.getObjects();
		for (final MapObject mapObj : objects) {
			if (! (mapObj instanceof TiledMapTileMapObject)) {
				Gdx.app.log(TAG, "GameObject kiểu " + mapObj + " không được hỗ trợ.");
				continue;
			}
			
			final TiledMapTileMapObject tiledMapObj = (TiledMapTileMapObject) mapObj;
			final MapProperties tiledMapObjProperties = tiledMapObj.getProperties();
			final MapProperties tileProperties = tiledMapObj.getTile().getProperties();
			final GameObjectType gameObjType;
			
			if (tiledMapObjProperties.containsKey("type")) {
				gameObjType = GameObjectType.valueOf(tiledMapObjProperties.get("type", String.class));
			} else if (tileProperties.containsKey("type")) {
				gameObjType = GameObjectType.valueOf(tileProperties.get("type", String.class));
			} else {
				Gdx.app.log(TAG, "Không có gameobjecttype được định nghĩa cho tile " + tiledMapObjProperties.get("id", Integer.class));
				continue;
			}
			
			final int animationIndex = tiledMapObj.getTile().getId();
			if (!createAnimation(animationIndex, tiledMapObj.getTile())){
				Gdx.app.log(TAG, "Không tạo được animation cho tile " + tiledMapObjProperties.get("id", Integer.class));
				continue;
			}
			
			final float width = tiledMapObjProperties.get("width", Float.class) * UNIT_SCALE;
			final float height = tiledMapObjProperties.get("height", Float.class) * UNIT_SCALE;
			gameObjects.add(new GameObject(gameObjType, new Vector2(tiledMapObj.getX() * UNIT_SCALE,
							tiledMapObj.getY() * UNIT_SCALE), width, height, tiledMapObj.getRotation(), animationIndex));
		}
	}
	
	private boolean createAnimation(int animationIndex, TiledMapTile tile) {
		Animation<Sprite> animation = mapAnimations.get(animationIndex);
		if (animation == null) {
			Gdx.app.debug(TAG, "Tạo map animation mới cho tile " + tile.getId());
			if (tile instanceof AnimatedTiledMapTile) {
				final AnimatedTiledMapTile aniTile = (AnimatedTiledMapTile) tile;
				final Sprite[] keyFrames = new Sprite[aniTile.getFrameTiles().length];
				int i = 0;
				for (final StaticTiledMapTile staticTile : aniTile.getFrameTiles()) {
					keyFrames[i++] = new Sprite(staticTile.getTextureRegion());
				}
				animation = new Animation<Sprite>(aniTile.getAnimationIntervals()[0] * 0.001f, keyFrames);
				animation.setPlayMode(Animation.PlayMode.LOOP);
				mapAnimations.put(animationIndex, animation);
			} else if (tile instanceof StaticTiledMapTile) {
				animation = new Animation<Sprite>(0, new Sprite(tile.getTextureRegion()));
				mapAnimations.put(animationIndex, animation);
			} else {
				Gdx.app.log(TAG, "Tile kiểu " + tile + " chưa được hỗ trợ cho map animations.");
				return false;
			}
		}
		return true;
	}

	public Array<CollisionArea> getCollisionAreas(){
		return collisionAreas;
	}
	
	public Vector2 getStartLocation() {
		return startLocation;
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}
	
	public Array<GameObject> getGameObjects() {
		return gameObjects;
	}
	
	public IntMap<Animation<Sprite>> getMapAnimations() {
		return mapAnimations;
	}
}
