package com.hellish.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.CollisionComponent;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.ParticleEffectComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.component.SpawnComponent;
import com.hellish.ecs.component.TiledComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.system.AnimationSystem;
import com.hellish.ecs.system.CameraSystem;
import com.hellish.ecs.system.CollisionDespawnSystem;
import com.hellish.ecs.system.CollisionSpawnSystem;
import com.hellish.ecs.system.DebugSystem;
import com.hellish.ecs.system.EntitySpawnSystem;
import com.hellish.ecs.system.MoveSystem;
import com.hellish.ecs.system.ParticleEffectSystem;
import com.hellish.ecs.system.PhysicsSystem;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.ecs.component.ImageComponent.ImageComponentListener;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PhysicsComponent.PhysicsComponentListener;

public class ECSEngine extends PooledEngine{
	public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
	public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
	public static final ComponentMapper<ParticleEffectComponent> peCmpMapper = ComponentMapper.getFor(ParticleEffectComponent.class);
	public static final ComponentMapper<ImageComponent> imageCmpMapper = ComponentMapper.getFor(ImageComponent.class);
	public static final ComponentMapper<SpawnComponent> spawnCmpMapper = ComponentMapper.getFor(SpawnComponent.class); 
	public static final ComponentMapper<PhysicsComponent> physicsCmpMapper = ComponentMapper.getFor(PhysicsComponent.class); 
	public static final ComponentMapper<MoveComponent> moveCmpMapper = ComponentMapper.getFor(MoveComponent.class);
	public static final ComponentMapper<TiledComponent> tiledCmpMapper = ComponentMapper.getFor(TiledComponent.class);
	public static final ComponentMapper<CollisionComponent> collisionCmpMapper = ComponentMapper.getFor(CollisionComponent.class);
	
	
	private final Stage stage;
	private final ComponentManager componentManager;
	
	public ECSEngine(final Main context) {
		super();
		
		stage = context.getStage();	
		
		componentManager = context.getComponentManager();
		componentManager.addComponentListener(new ImageComponentListener(stage));
		componentManager.addComponentListener(new PhysicsComponentListener());
		
		this.addSystem(new EntitySpawnSystem(context));
		this.addSystem(new CollisionSpawnSystem(context));
		this.addSystem(new CollisionDespawnSystem(context));
		this.addSystem(new MoveSystem());
		this.addSystem(new PhysicsSystem(context));
		this.addSystem(new AnimationSystem(context));
		this.addSystem(new CameraSystem(context));
		this.addSystem(new RenderSystem(context));
		this.addSystem(new DebugSystem(context));
		this.addSystem(new ParticleEffectSystem(context));
	}
	
	@Override
	public void addEntity(Entity entity) {
		super.addEntity(entity);
		
		entity.getComponents().forEach(component -> {
			componentManager.notifyComponentAdded(entity, component);
		});
	}
	
	@Override
    public void removeEntity(Entity entity) {
        super.removeEntity(entity);
        
        entity.getComponents().forEach(component -> {
            componentManager.notifyComponentRemoved(entity, component);
        });
    }
}
