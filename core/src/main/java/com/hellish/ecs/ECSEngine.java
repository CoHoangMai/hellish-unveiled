package com.hellish.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hellish.Main;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.CollisionComponent;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.DeadComponent;
import com.hellish.ecs.component.FloatingTextComponent;
import com.hellish.ecs.component.ParticleEffectComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.component.SpawnComponent;
import com.hellish.ecs.component.StateComponent;
import com.hellish.ecs.component.TiledComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.system.AiSystem;
import com.hellish.ecs.system.AnimationSystem;
import com.hellish.ecs.system.AttackSystem;
import com.hellish.ecs.system.CameraSystem;
import com.hellish.ecs.system.CollisionDespawnSystem;
import com.hellish.ecs.system.CollisionSpawnSystem;
import com.hellish.ecs.system.DeadSystem;
import com.hellish.ecs.system.DebugSystem;
import com.hellish.ecs.system.EntitySpawnSystem;
import com.hellish.ecs.system.FloatingTextSystem;
import com.hellish.ecs.system.InventorySystem;
import com.hellish.ecs.system.LifeSystem;
import com.hellish.ecs.system.LootSystem;
import com.hellish.ecs.system.MoveSystem;
import com.hellish.ecs.system.ParticleEffectSystem;
import com.hellish.ecs.system.PhysicsSystem;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.ecs.system.StateSystem;
import com.hellish.ecs.component.AiComponent;
import com.hellish.ecs.component.AiComponent.AiComponentListener;
import com.hellish.ecs.component.FloatingTextComponent.FloatingTextComponentListener;
import com.hellish.ecs.component.ImageComponent.ImageComponentListener;
import com.hellish.ecs.component.InventoryComponent;
import com.hellish.ecs.component.ItemComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.LootComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PhysicsComponent.PhysicsComponentListener;
import com.hellish.ecs.component.StateComponent.StateComponentListener;

public class ECSEngine extends PooledEngine implements Disposable{
	public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
	public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
	public static final ComponentMapper<ParticleEffectComponent> peCmpMapper = ComponentMapper.getFor(ParticleEffectComponent.class);
	public static final ComponentMapper<ImageComponent> imageCmpMapper = ComponentMapper.getFor(ImageComponent.class);
	public static final ComponentMapper<SpawnComponent> spawnCmpMapper = ComponentMapper.getFor(SpawnComponent.class); 
	public static final ComponentMapper<PhysicsComponent> physicsCmpMapper = ComponentMapper.getFor(PhysicsComponent.class); 
	public static final ComponentMapper<MoveComponent> moveCmpMapper = ComponentMapper.getFor(MoveComponent.class);
	public static final ComponentMapper<TiledComponent> tiledCmpMapper = ComponentMapper.getFor(TiledComponent.class);
	public static final ComponentMapper<CollisionComponent> collisionCmpMapper = ComponentMapper.getFor(CollisionComponent.class);
	public static final ComponentMapper<LifeComponent> lifeCmpMapper = ComponentMapper.getFor(LifeComponent.class);
	public static final ComponentMapper<DeadComponent> deadCmpMapper = ComponentMapper.getFor(DeadComponent.class);
	public static final ComponentMapper<AttackComponent> attackCmpMapper = ComponentMapper.getFor(AttackComponent.class);
	public static final ComponentMapper<FloatingTextComponent> floatTxtCmpMapper = ComponentMapper.getFor(FloatingTextComponent.class);
	public static final ComponentMapper<LootComponent> lootCmpMapper = ComponentMapper.getFor(LootComponent.class);
	public static final ComponentMapper<StateComponent> stateCmpMapper = ComponentMapper.getFor(StateComponent.class);
	public static final ComponentMapper<AiComponent> aiCmpMapper = ComponentMapper.getFor(AiComponent.class);
	public static final ComponentMapper<ItemComponent> itemCmpMapper = ComponentMapper.getFor(ItemComponent.class);
	public static final ComponentMapper<InventoryComponent> invCmpMapper = ComponentMapper.getFor(InventoryComponent.class);
	
	private final Stage gameStage;
	private final Stage uiStage;
	private final ComponentManager componentManager;
	private final Array<EntitySystem> addedSystems;
	
	public ECSEngine(final Main context) {
		super();
		
		gameStage = context.getGameStage();	
		uiStage = context.getUIStage();
		addedSystems = new Array<EntitySystem>();
		
		componentManager = context.getComponentManager();
		componentManager.addComponentListener(new ImageComponentListener(gameStage));
		componentManager.addComponentListener(new PhysicsComponentListener());
		componentManager.addComponentListener(new FloatingTextComponentListener(uiStage));
		componentManager.addComponentListener(new StateComponentListener());
		componentManager.addComponentListener(new AiComponentListener());
		
		addAndTrackSystem(new EntitySpawnSystem(context));
		addAndTrackSystem(new CollisionSpawnSystem(context));
		addAndTrackSystem(new CollisionDespawnSystem(context));
		addAndTrackSystem(new MoveSystem());
		addAndTrackSystem(new LootSystem(context));
		addAndTrackSystem(new InventorySystem(context));
		addAndTrackSystem(new DeadSystem(context));
		addAndTrackSystem(new LifeSystem(context));
		addAndTrackSystem(new PhysicsSystem(context));
		addAndTrackSystem(new AnimationSystem(context));
		addAndTrackSystem(new AttackSystem(context));
		addAndTrackSystem(new StateSystem());
		addAndTrackSystem(new AiSystem());
		addAndTrackSystem(new CameraSystem(context));
		addAndTrackSystem(new FloatingTextSystem(context));
		addAndTrackSystem(new RenderSystem(context));
		addAndTrackSystem(new DebugSystem(context));
		addAndTrackSystem(new ParticleEffectSystem(context));
	}
	
	private void addAndTrackSystem(EntitySystem system) {
		addedSystems.add(system);
		addSystem(system);
	}
	
	@Override
	public void addEntity(Entity entity) {
		super.addEntity(entity);
		
		entity.getComponents().forEach(component -> {
			componentManager.notifyComponentAdded(entity, component, gameStage);
		});
	}
	
	@Override
    public void removeEntity(Entity entity) {
		entity.getComponents().forEach(component -> {
			componentManager.notifyComponentRemoved(entity, component);
		});
        super.removeEntity(entity);
    }

	@Override
	public void dispose() {
		for(EntitySystem system : addedSystems) {
			if (system instanceof Disposable) {
				((Disposable) system).dispose();
			}
		}
	}
}
