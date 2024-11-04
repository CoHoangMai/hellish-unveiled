package com.hellish.ecs.system;

import java.util.HashSet;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
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
	public static final int SPAWN_AREA_SIZE = 3;
	final private World world;
	final private Array<TiledMapTileLayer> tiledLayers;
	final private HashSet<Cell> processedCells;
	public CollisionSpawnSystem(final Main context) {
		super(Family.all(PhysicsComponent.class, CollisionComponent.class).get());
		
		world = context.getWorld();
		tiledLayers = new Array<TiledMapTileLayer>();
		processedCells = new HashSet<>();
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

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		float entityX = ECSEngine.physicsCmpMapper.get(entity).body.getPosition().x;
		float entityY = ECSEngine.physicsCmpMapper.get(entity).body.getPosition().y;
		for(TiledMapTileLayer layer : tiledLayers) {
			forEachCell(layer, (int)entityX, (int)entityY, SPAWN_AREA_SIZE, (cell, x, y	) -> {
				if(cell.getTile().getObjects().getCount() == 0) {
		    		return;
		    	}
				if(processedCells.contains(cell)) {
					return;
				}
				processedCells.add(cell);
		    	for(MapObject mapObj : cell.getTile().getObjects()) {
		    		Entity collisionEntity = new Entity();
		    			
		    		collisionEntity.add(PhysicsComponent.physicsCmpFromShape2D(world, x, y, mapObj));
		    		
		    		TiledComponent tiledCmp = new TiledComponent();
		    		tiledCmp.cell = cell;
		    		tiledCmp.nearbyEntities.add(entity);
		    		
		    		collisionEntity.add(tiledCmp);
		    			
		    		getEngine().addEntity(collisionEntity);
		    	}
			});
		}
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof MapChangeEvent) {
			((MapChangeEvent) event).getMap().getTiledMap().getLayers().getByType(TiledMapTileLayer.class, tiledLayers);
			int mapWidth = tiledLayers.get(0).getWidth();
			int mapHeight = tiledLayers.get(0).getHeight();
			
			//Tạo biên cho map
			Entity entity = new Entity();

			PhysicsComponent physicsCmp = new PhysicsComponent();
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
			physicsCmp.body.createFixture(loopShape, 0.0f);
			loopShape.dispose();
			
			entity.add(physicsCmp);

			getEngine().addEntity(entity);
			
			return true;
		} else if(event instanceof CollisionDespawnEvent) {
			processedCells.remove(((CollisionDespawnEvent)event).cell);
			return true;
		}
		return false;
	}
	
	@FunctionalInterface
	private interface CellAction {
		void accept(TiledMapTileLayer.Cell cell, int x, int y);
	}
}
