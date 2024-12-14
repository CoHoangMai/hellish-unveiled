package com.hellish.ecs.system;


import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.ecs.system.EntitySpawnSystem.PLAYER_CFG;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.hellish.Main;
import com.hellish.actor.FlipImage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.component.PortalComponent;
import com.hellish.event.MapChangeEvent;
import com.hellish.map.MapManager;
import com.hellish.map.MapType;

public class  PortalSystem extends IteratingSystem implements EventListener{
	private static final String TAG = PortalSystem.class.getSimpleName();
	
	private final World world;
	private final MapManager mapManager;
	
	public PortalSystem(final Main context) {
		super(Family.all(PortalComponent.class).get());
		
		world = context.getWorld();
		mapManager = context.getMapManager();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PortalComponent portalCmp = ECSEngine.portalCmpMapper.get(entity);
		
		if(!portalCmp.triggerEntities.isEmpty()) {
			Entity triggerEntity = portalCmp.triggerEntities.iterator().next();
			portalCmp.triggerEntities.clear();
			
			Gdx.app.debug(TAG, triggerEntity + " đi vào portal " + portalCmp.id);	

			for(Entity eachEntity : getEngine().getEntities()) {
				if(!ECSEngine.playerCmpMapper.has(eachEntity)) {
					getEngine().removeEntity(eachEntity);
				}
			}
			
			mapManager.setMap(portalCmp.toMap);
			
			//cho portal system làm tí việc
			if(portalCmp.toPortal != -1) {
				setMap(portalCmp.toPortal);
			}
		}
	}

	private void setMap(int targetPortalId) {
		for(Entity playerEntity : getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get())) {
			Rectangle targetPortalRect = getTargetPortalbyId(mapManager.getCurrentMap(), targetPortalId).getRectangle();
			
			FlipImage image = ECSEngine.imageCmpMapper.get(playerEntity).image;
			
			image.setPosition(
				targetPortalRect.getX() * UNIT_SCALE + targetPortalRect.getWidth() * 0.5f * UNIT_SCALE, 
				targetPortalRect.getY() * UNIT_SCALE - targetPortalRect.getHeight() * 0.5f * UNIT_SCALE
			);
			
			PhysicsComponent oldPhysicsCmp = ECSEngine.physicsCmpMapper.get(playerEntity);
	        if (oldPhysicsCmp != null) {
	            world.destroyBody(oldPhysicsCmp.body);
	            playerEntity.remove(PhysicsComponent.class);
	        }
	        
			playerEntity.add(PhysicsComponent.physicsCmpFromImgandCfg(getEngine(), world, image, PLAYER_CFG));
			
			ECSEngine.lightCmpMapper.get(playerEntity).light.attachToBody(ECSEngine.physicsCmpMapper.get(playerEntity).body);
		}
	}

	private RectangleMapObject getTargetPortalbyId(TiledMap map, int targetPortalId) {
		MapObjects portalObjects = map.getLayers().get("portals").getObjects();
		
		for(MapObject mapObj : portalObjects) {
			if(mapObj.getProperties().get("id", Integer.class) == targetPortalId) {
				return (RectangleMapObject) mapObj;
			}
		}
		
		 throw new IllegalArgumentException("Không tìm thấy portal với ID " + targetPortalId);
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof MapChangeEvent) {
			MapLayer portalLayer = ((MapChangeEvent) event).getTiledMap().getLayers().get("portals");
			
			for(MapObject mapObj : portalLayer.getObjects()) {
				String toMapString = mapObj.getProperties().get("toMap", "", String.class);
				Integer toPortal = mapObj.getProperties().get("toPortal", -1, Integer.class);
				
				if(toMapString.isBlank()) {
					continue;
				} else if(toPortal == -1) {
					Gdx.app.error(TAG, "Portal " + mapObj.getProperties().get("id") + " không có thuộc tính toPortal");
				}
				
				Entity portalEntity = getEngine().createEntity();
				
				portalEntity.add(PhysicsComponent.physicsCmpFromShape2D(getEngine(), world, 0, 0, mapObj, true));
				
				PortalComponent portalCmp = getEngine().createComponent(PortalComponent.class);
				portalCmp.id = (int) mapObj.getProperties().get("id");
				portalCmp.toPortal = toPortal;
				
				portalCmp.toMap = MapType.valueOf(toMapString);
				
				portalEntity.add(portalCmp);
				
				getEngine().addEntity(portalEntity);
			}
			return true;
		}
		return false;
	}
}
