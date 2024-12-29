package com.hellish.ecs.system;

import static com.hellish.ecs.system.EntitySpawnSystem.COLLISION_BOX;
import static com.hellish.Main.UNIT_SCALE;

import java.util.HashSet;
import java.util.Objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.CollisionComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.TiledComponent;
import com.hellish.event.CollisionDespawnEvent;
import com.hellish.event.MapChangeEvent;

public class CollisionSpawnSystem extends IteratingSystem implements EventListener{
	public static final Rectangle SPAWN_RECT = new Rectangle();
	public static final String TAG = CollisionSpawnSystem.class.getSimpleName();
	public static final int EXTRA_SPAWN_SIZE = 5;
	
	final private World world;
	final private Array<TiledMapTileLayer> tiledLayers;	
	final private Array<TiledMapTileMapObject> terrainObjects;
	final private HashSet<Cell> processedCells;
	final private HashSet<MapObjectKey> processedObjects;
	public CollisionSpawnSystem(final Main context) {
		super(Family.all(PhysicsComponent.class, CollisionComponent.class).get());
		
		world = context.getWorld();
		tiledLayers = new Array<TiledMapTileLayer>();
		terrainObjects = new Array<TiledMapTileMapObject>();
		processedCells = new HashSet<Cell>();
		processedObjects = new HashSet<>();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		float entityX = ECSEngine.physicsCmpMapper.get(entity).getPosition().x;
		float entityY = ECSEngine.physicsCmpMapper.get(entity).getPosition().y;
		
		//Xử lý collision trong các Tile Layer
		for(TiledMapTileLayer layer : tiledLayers) {
			forEachCell(layer, (int)entityX, (int)entityY, EXTRA_SPAWN_SIZE, (cell, x, y) -> {
				if(cell.getTile().getObjects().getCount() == 0) {
		    		return;
			    }
				if(processedCells.contains(cell)) {
					return;
				}

				processedCells.add(cell);
		    	for(MapObject mapObj : cell.getTile().getObjects()) {
		    		Entity collisionEntity = getEngine().createEntity();
				    		
		    		collisionEntity.add(PhysicsComponent.physicsCmpFromShape2D(getEngine(), world, x, y, mapObj, true));
			    		
				    TiledComponent tiledCmp = getEngine().createComponent(TiledComponent.class);
				    tiledCmp.cell = cell;
				    tiledCmp.nearbyEntities.add(entity);
				    		
				    collisionEntity.add(tiledCmp);
				    			
				    getEngine().addEntity(collisionEntity);
				 }
			});
		}
		
		//Xử lý collision cho các object trong layer terrain
		for(TiledMapTileMapObject terrainObject : terrainObjects) {
			if(terrainObject.getTile().getObjects().getCount() == 0) {
	    		continue;
	    	}
			
			for(MapObject mapObj : terrainObject.getTile().getObjects()) {
				MapObjectKey key = new MapObjectKey(mapObj, terrainObject);
				
				if(processedObjects.contains(key)) {
					continue;
				}
				
				if (!(mapObj instanceof RectangleMapObject)) {
			        throw new GdxRuntimeException("MapObject shape không được hỗ trợ: " + mapObj);
			    }
				
				final Rectangle rectangle = ((RectangleMapObject) mapObj).getRectangle();
				
				final float bodyX = (terrainObject.getX() + rectangle.x) * UNIT_SCALE - EXTRA_SPAWN_SIZE;
		        final float bodyY = (terrainObject.getY() + rectangle.y) * UNIT_SCALE - EXTRA_SPAWN_SIZE;
		        final float bodyW = rectangle.width * UNIT_SCALE + EXTRA_SPAWN_SIZE * 2;
		        final float bodyH = rectangle.height * UNIT_SCALE + EXTRA_SPAWN_SIZE * 2;
		        
		        SPAWN_RECT.set(bodyX, bodyY, bodyW, bodyH);
		        
		        if (SPAWN_RECT.contains(entityX, entityY)) { 
		        	Entity collisionEntity = getEngine().createEntity();
	    			
		    		collisionEntity.add(PhysicsComponent.physicsCmpFromShape2D(
		    			getEngine(), world,
		    			terrainObject.getX() * UNIT_SCALE, terrainObject.getY() * UNIT_SCALE, 
		    			mapObj, true));
		    		
		    		TiledComponent tiledCmp = getEngine().createComponent(TiledComponent.class);
		    		tiledCmp.nearbyEntities.add(entity);
		    		tiledCmp.objectKey = key;
		    		collisionEntity.add(tiledCmp);
		    			
		    		getEngine().addEntity(collisionEntity);
		    		
		    		processedObjects.add(key);
		        }
			}
		}
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof MapChangeEvent) {
			final MapChangeEvent mapChangeEvent = (MapChangeEvent) event;
			if(mapChangeEvent.getTiledMap() == null) {
				return false;
			}
		
			tiledLayers.clear();
			terrainObjects.clear();
			processedCells.clear();
			processedObjects.clear();
			
			//Lấy bgd layer để xác định biên
			((MapGroupLayer) mapChangeEvent.getTiledMap().getLayers().get("bgd")).getLayers().getByType(TiledMapTileLayer.class, tiledLayers);
			
			//Tạo terrain
			MapGroupLayer terrainLayer = (MapGroupLayer) ((MapChangeEvent) event).getTiledMap().getLayers().get("terrain");
			if (terrainLayer != null) {
				for(MapLayer subLayer : terrainLayer.getLayers()) {
					for (MapObject mapObj : subLayer.getObjects()) {
						if (! (mapObj instanceof TiledMapTileMapObject)) {
							Gdx.app.log(TAG, "MapObject kiểu " + mapObj + " trong layer 'terrain' không được hỗ trợ.");
							continue;
						}
						TiledMapTileMapObject tiledMapObj = (TiledMapTileMapObject) mapObj;
						terrainObjects.add(tiledMapObj);
					}
				}
			}
			
			//Tạo biên cho map
			int mapWidth = tiledLayers.get(0).getWidth() / 2;
			int mapHeight = tiledLayers.get(0).getHeight() / 2;
			Entity entity = getEngine().createEntity();
			PhysicsComponent physicsCmp = getEngine().createComponent(PhysicsComponent.class);
			physicsCmp.body = world.createBody(new BodyDef() {{
			    type = BodyDef.BodyType.StaticBody;
			    position.set(0f, 0f);
			    fixedRotation = true;
			    allowSleep = false;
			}});
			ChainShape loopShape = new ChainShape();
			loopShape.createChain(new Vector2[]{
			    new Vector2(0f, 0f),
			    new Vector2(mapWidth, 0f),
			    new Vector2(mapWidth, mapHeight),
			    new Vector2(0f, mapHeight),
			    new Vector2(0f, 0f)
			});
			Fixture fixture = physicsCmp.body.createFixture(loopShape, 0.0f);
			fixture.setUserData(COLLISION_BOX);
			loopShape.dispose();
			entity.add(physicsCmp);
			getEngine().addEntity(entity);
			
			return true;
		} else if(event instanceof CollisionDespawnEvent) {
			if(((CollisionDespawnEvent)event).cell != null) {
				processedCells.remove(((CollisionDespawnEvent)event).cell);
			}
			if(((CollisionDespawnEvent)event).mapObjectKey != null) {
				processedObjects.remove(((CollisionDespawnEvent)event).mapObjectKey);
			}
			return true;
		}
		return false;
	}
	
	private void forEachCell(TiledMapTileLayer layer, int startX, int startY, int size, 
			CellAction action) {
		for (int x = startX - size; x <= startX + size; x++) {
			for (int y = startY - size; y < startY + size; y++) {
				TiledMapTileLayer.Cell cell = layer.getCell(x, y);
				if (cell != null) {
					action.accept(cell, x, y);
				}
			}
		}
	}
	
	@FunctionalInterface
	private interface CellAction {
		void accept(TiledMapTileLayer.Cell cell, int x, int y);
	}
	
	//Để nhận diện mỗi MapObject của mỗi TerrainObject
	public class MapObjectKey {
	    private final MapObject mapObject;
	    private final TiledMapTileMapObject terrainObject;

	    public MapObjectKey(MapObject mapObject, TiledMapTileMapObject terrainObject) {
	        this.mapObject = mapObject;
	        this.terrainObject = terrainObject;
	    }

	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj) return true;
	        if (obj == null || getClass() != obj.getClass()) return false;
	        MapObjectKey that = (MapObjectKey) obj;
	        return Objects.equals(mapObject, that.mapObject) && 
	                Objects.equals(terrainObject, that.terrainObject);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(mapObject, terrainObject);
	    }
	}
}
