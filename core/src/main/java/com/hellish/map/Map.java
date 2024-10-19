package com.hellish.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import static com.hellish.Main.UNIT_SCALE;;

public class Map {
	public static final String TAG = Map.class.getSimpleName();
	
	private final TiledMap tiledMap;
	private final Array<CollisionArea> collisionAreas;
	private final Vector2 startLocation;
	
	public Map(final TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		collisionAreas = new Array<CollisionArea>();
		
		parseCollisionLayer();
		startLocation = new Vector2();
		parsePlayerStartLocation();
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
	
	public Array<CollisionArea> getCollisionAreas(){
		return collisionAreas;
	}
	
	public Vector2 getStartLocation() {
		return startLocation;
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}
}
