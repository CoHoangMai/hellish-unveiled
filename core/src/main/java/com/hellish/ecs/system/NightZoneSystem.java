package com.hellish.ecs.system;

import static com.hellish.ecs.system.EntitySpawnSystem.COLLISION_BOX;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.NightZoneComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.event.EventUtils;
import com.hellish.event.LightChangeEvent;
import com.hellish.event.MapChangeEvent;

public class NightZoneSystem extends IteratingSystem implements EventListener{
	private final World world;
	private final Stage gameStage;

	public NightZoneSystem(final Main context) {
		super(Family.all(NightZoneComponent.class).get());
		world = context.getWorld();
		gameStage = context.getGameStage();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		NightZoneComponent nightCmp = ECSEngine.nightZoneCmpMapper.get(entity);
		PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		
		nightCmp.entitiesInZone.clear();
		
		float x = physicsCmp.getPosition().x;
		float y = physicsCmp.getPosition().y;
		float w = physicsCmp.size.x;
		float h = physicsCmp.size.y;
		
		//kiểm tra xem có con HUST đứng trong night zone không (một cách tương đối)
		world.QueryAABB(fixture -> {
			if(fixture.getUserData() != COLLISION_BOX) {
				return true;
			}
			
			Entity fixtureEntity = (Entity) fixture.getBody().getUserData();
			if(fixtureEntity == entity) {
				return true;
			}
			
			if(ECSEngine.playerCmpMapper.has(fixtureEntity)) {
				if(!nightCmp.entitiesInZone.contains(fixtureEntity)) {
					nightCmp.entitiesInZone.add(fixtureEntity);
				}
			}
			
			return true;
		}, x, y, x + w, y + h);
	
		
		if(!nightCmp.entitiesInZone.isEmpty() && !nightCmp.activated) {
			nightCmp.activated = true;
			EventUtils.fireEvent(gameStage, LightChangeEvent.pool, event -> {});
			return;
		}
		
		if(nightCmp.entitiesInZone.isEmpty() && nightCmp.activated) {
			nightCmp.activated = false;
			EventUtils.fireEvent(gameStage, LightChangeEvent.pool, event -> {});
			return;
		}
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof MapChangeEvent) {
			//Tạo NightZone
			MapLayer nightZoneLayer = ((MapChangeEvent) event).getTiledMap().getLayers().get("nightZone");
			
			if(nightZoneLayer != null) {
				for(MapObject mapObj : nightZoneLayer.getObjects()) {
					Entity nightZoneEntity = getEngine().createEntity();
					
					nightZoneEntity.add(PhysicsComponent.physicsCmpFromShape2D(getEngine(), world, 0, 0, mapObj, false));
					
					nightZoneEntity.add(getEngine().createComponent(NightZoneComponent.class));
					
					getEngine().addEntity(nightZoneEntity);
				}
			}
			return true;
		}
		return false;
	}
}
