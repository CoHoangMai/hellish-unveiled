package com.hellish.ecs.system;

import static com.hellish.ecs.system.EntitySpawnSystem.COLLISION_BOX;
import static com.hellish.Main.UNIT_SCALE;

import java.util.HashSet;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.CollisionComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.TiledComponent;
import com.hellish.event.CollisionDespawnEvent;
import com.hellish.event.MapChangeEvent;


public class CollisionSpawnSystem extends IteratingSystem implements EventListener{
	public static final String TAG = CollisionSpawnSystem.class.getSimpleName();
	public static final int SPAWN_AREA_SIZE = 7;
	final private World world;
	final private Array<TiledMapTileLayer> tiledLayers;	
	final private Array<TiledMapTileMapObject> terrainObjects;
	final private HashSet<Cell> processedCells;
	final private HashSet<TiledMapTileMapObject> processedObjects;
	public CollisionSpawnSystem(final Main context) {
		super(Family.all(PhysicsComponent.class, CollisionComponent.class).get());
		
		world = context.getWorld();
		tiledLayers = new Array<TiledMapTileLayer>();
		terrainObjects = new Array<TiledMapTileMapObject>();
		processedCells = new HashSet<Cell>();
		processedObjects = new HashSet<TiledMapTileMapObject>();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		float entityX = ECSEngine.physicsCmpMapper.get(entity).body.getPosition().x;
		float entityY = ECSEngine.physicsCmpMapper.get(entity).body.getPosition().y;
		
		//Xử lý collision trong các Tile Layer
		for(TiledMapTileLayer layer : tiledLayers) {
			forEachCell(layer, (int)entityX, (int)entityY, SPAWN_AREA_SIZE, (cell, x, y) -> {
				if(cell.getTile().getObjects().getCount() == 0) {
		    		return;
		    	}
				if(processedCells.contains(cell)) {
					return;
				}

				processedCells.add(cell);
		    	for(MapObject mapObj : cell.getTile().getObjects()) {
		    		Entity collisionEntity = getEngine().createEntity();
		    		
		    		collisionEntity.add(PhysicsComponent.physicsCmpFromShape2D(getEngine(), world, x, y, mapObj));
		    		
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
			if(processedObjects.contains(terrainObject)) {
				continue;
			}
			
			float terrainObjX = terrainObject.getX() * UNIT_SCALE;
			float terrainObjY = terrainObject.getY() * UNIT_SCALE;
			
			if(Math.abs(terrainObjX - entityX) <= SPAWN_AREA_SIZE && Math.abs(terrainObjY - entityY) <= SPAWN_AREA_SIZE) {
				processedObjects.add(terrainObject);
				
				for(MapObject mapObj : terrainObject.getTile().getObjects()) {
		    		Entity collisionEntity = getEngine().createEntity();
		    			
		    		collisionEntity.add(PhysicsComponent.physicsCmpFromShape2D(
		    			getEngine(), world,
		    			terrainObjX, terrainObjY, 
		    			mapObj ));
		    		
		    		TiledComponent tiledCmp = getEngine().createComponent(TiledComponent.class);
		    		tiledCmp.nearbyEntities.add(entity);
		    		tiledCmp.object = terrainObject;
		    		collisionEntity.add(tiledCmp);
		    			
		    		getEngine().addEntity(collisionEntity);
		    	}
			} 
		}
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof MapChangeEvent) {
			((MapChangeEvent) event).getTiledMap().getLayers().getByType(TiledMapTileLayer.class, tiledLayers);	
			
			MapLayer terrainLayer = ((MapChangeEvent) event).getTiledMap().getLayers().get("terrain");
			if (terrainLayer != null) {
				for (MapObject mapObj : terrainLayer.getObjects()) {
					if (! (mapObj instanceof TiledMapTileMapObject)) {
						Gdx.app.log(TAG, "MapObject kiểu " + mapObj + " trong layer 'terrain' không được hỗ trợ.");
						continue;
					}
					TiledMapTileMapObject tiledMapObj = (TiledMapTileMapObject) mapObj;
					terrainObjects.add(tiledMapObj);
				}
			}
			
			//Tạo biên cho map
			int mapWidth = tiledLayers.get(0).getWidth();
			int mapHeight = tiledLayers.get(0).getHeight();
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
			if(((CollisionDespawnEvent)event).terrainObject != null) {
				processedObjects.remove(((CollisionDespawnEvent)event).terrainObject);
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
}
