package com.hellish.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.hellish.Main;
import com.hellish.ecs.component.AiComponent;
import com.hellish.ecs.component.AiComponent.AiComponentListener;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.CollisionComponent;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.CooldownComponent;
import com.hellish.ecs.component.DeadComponent;
import com.hellish.ecs.component.EntitySpawnComponent;
import com.hellish.ecs.component.FloatingTextComponent;
import com.hellish.ecs.component.FloatingTextComponent.FloatingTextComponentListener;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.ImageComponent.ImageComponentListener;
import com.hellish.ecs.component.InventoryComponent;
import com.hellish.ecs.component.ItemComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.NightZoneComponent;
import com.hellish.ecs.component.LightComponent;
import com.hellish.ecs.component.LootComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.PhysicsComponent.PhysicsComponentListener;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.component.PortalComponent;
import com.hellish.ecs.component.StateComponent;
import com.hellish.ecs.component.StateComponent.StateComponentListener;
import com.hellish.ecs.component.TerrainComponent;
import com.hellish.ecs.component.TerrainSpawnComponent;
import com.hellish.ecs.component.TextComponent;
import com.hellish.ecs.component.TextComponent.TextComponentListener;
import com.hellish.ecs.component.TiledComponent;
import com.hellish.ecs.system.AiSystem;
import com.hellish.ecs.system.AnimationSystem;
import com.hellish.ecs.system.AttackSystem;
import com.hellish.ecs.system.AudioSystem;
import com.hellish.ecs.system.CameraSystem;
import com.hellish.ecs.system.CollisionDespawnSystem;
import com.hellish.ecs.system.CollisionSpawnSystem;
import com.hellish.ecs.system.CooldownSystem;
import com.hellish.ecs.system.DeadSystem;
import com.hellish.ecs.system.EntitySpawnSystem;
import com.hellish.ecs.system.FireSystem;
import com.hellish.ecs.system.FloatingTextSystem;
import com.hellish.ecs.system.InventorySystem;
import com.hellish.ecs.system.LifeSystem;
import com.hellish.ecs.system.LightSystem;
import com.hellish.ecs.system.LootSystem;
import com.hellish.ecs.system.MoveSystem;
import com.hellish.ecs.system.NightZoneSystem;
import com.hellish.ecs.system.PhysicsSystem;
import com.hellish.ecs.system.PortalSystem;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.ecs.system.StateSystem;
import com.hellish.ecs.system.SteeringSystem;
import com.hellish.ecs.system.TerrainSpawnSystem;
import com.hellish.ecs.system.TextSystem;


public class ECSEngine extends PooledEngine implements Disposable{
	public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
	public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
	public static final ComponentMapper<ImageComponent> imageCmpMapper = ComponentMapper.getFor(ImageComponent.class);
	public static final ComponentMapper<TerrainComponent> terrainCmpMapper = ComponentMapper.getFor(TerrainComponent.class);
	public static final ComponentMapper<EntitySpawnComponent> entitySpawnCmpMapper = ComponentMapper.getFor(EntitySpawnComponent.class); 
	public static final ComponentMapper<TerrainSpawnComponent> terrainSpawnCmpMapper = ComponentMapper.getFor(TerrainSpawnComponent.class);
	public static final ComponentMapper<PhysicsComponent> physicsCmpMapper = ComponentMapper.getFor(PhysicsComponent.class); 
	public static final ComponentMapper<LightComponent> lightCmpMapper = ComponentMapper.getFor(LightComponent.class);
	public static final ComponentMapper<MoveComponent> moveCmpMapper = ComponentMapper.getFor(MoveComponent.class);
	public static final ComponentMapper<TiledComponent> tiledCmpMapper = ComponentMapper.getFor(TiledComponent.class);
	public static final ComponentMapper<CollisionComponent> collisionCmpMapper = ComponentMapper.getFor(CollisionComponent.class);
	public static final ComponentMapper<LifeComponent> lifeCmpMapper = ComponentMapper.getFor(LifeComponent.class);
	public static final ComponentMapper<DeadComponent> deadCmpMapper = ComponentMapper.getFor(DeadComponent.class);
	public static final ComponentMapper<CooldownComponent> cooldownCmpMapper = ComponentMapper.getFor(CooldownComponent.class);
	public static final ComponentMapper<AttackComponent> attackCmpMapper = ComponentMapper.getFor(AttackComponent.class);
	public static final ComponentMapper<TextComponent> txtCmpMapper = ComponentMapper.getFor(TextComponent.class);
	public static final ComponentMapper<FloatingTextComponent> floatTxtCmpMapper = ComponentMapper.getFor(FloatingTextComponent.class);
	public static final ComponentMapper<LootComponent> lootCmpMapper = ComponentMapper.getFor(LootComponent.class);
	public static final ComponentMapper<StateComponent> stateCmpMapper = ComponentMapper.getFor(StateComponent.class);
	public static final ComponentMapper<AiComponent> aiCmpMapper = ComponentMapper.getFor(AiComponent.class);
	public static final ComponentMapper<ItemComponent> itemCmpMapper = ComponentMapper.getFor(ItemComponent.class);
	public static final ComponentMapper<InventoryComponent> invCmpMapper = ComponentMapper.getFor(InventoryComponent.class);
	public static final ComponentMapper<PortalComponent> portalCmpMapper = ComponentMapper.getFor(PortalComponent.class);
	public static final ComponentMapper<NightZoneComponent> nightZoneCmpMapper = ComponentMapper.getFor(NightZoneComponent.class);
	
	private final Stage gameStage;
	private final Stage uiStage;
	private final World world;
	private final ComponentManager componentManager;
	
	public ECSEngine(final Main context) {
		super();
		
		gameStage = context.getGameStage();	
		uiStage = context.getUIStage();
		world = context.getWorld();
		
		componentManager = context.getComponentManager();
		componentManager.addComponentListener(new ImageComponentListener(gameStage));
		componentManager.addComponentListener(new PhysicsComponentListener());
		componentManager.addComponentListener(new TextComponentListener(uiStage));
		componentManager.addComponentListener(new FloatingTextComponentListener(uiStage));
		componentManager.addComponentListener(new StateComponentListener());
		componentManager.addComponentListener(new AiComponentListener());
		
		addSystem(new AudioSystem());
		addSystem(new TerrainSpawnSystem());
		addSystem(new EntitySpawnSystem(context));
		addSystem(new CollisionSpawnSystem(context));
		addSystem(new CollisionDespawnSystem(context));
		addSystem(new PortalSystem(context));
		addSystem(new MoveSystem());
		addSystem(new LootSystem(context));
		addSystem(new InventorySystem(context));
		addSystem(new DeadSystem(context));
		addSystem(new LifeSystem(context));
		addSystem(new PhysicsSystem(context));
		addSystem(new LightSystem(context));
		addSystem(new NightZoneSystem(context));
		addSystem(new AnimationSystem(context));
		addSystem(new CooldownSystem(context));
		addSystem(new AttackSystem(context));
		addSystem(new FireSystem());
		addSystem(new SteeringSystem());
		addSystem(new StateSystem());
		addSystem(new AiSystem());
		addSystem(new CameraSystem(context));
		addSystem(new TextSystem(context));
		addSystem(new FloatingTextSystem(context));
		addSystem(new RenderSystem(context));
		//addSystem(new DebugSystem(context));	
	}
	
	@Override
	public void addEntity(Entity entity) {
		super.addEntity(entity);
		
		entity.getComponents().forEach(component -> {
			componentManager.notifyComponentAdded(entity, component, gameStage, world);
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
		for(EntitySystem system : getSystems()) {
			if (system instanceof Disposable) {
				((Disposable) system).dispose();
			}
		}
	}
}
