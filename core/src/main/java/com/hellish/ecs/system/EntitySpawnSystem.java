package com.hellish.ecs.system;

import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.ecs.component.EntitySpawnComponent.SpawnConfiguration.DEFAULT_MAX_SPEED;
import static com.hellish.ecs.component.EntitySpawnComponent.SpawnConfiguration.DEFAULT_MAX_ACCELERATION;
import static com.hellish.ecs.component.EntitySpawnComponent.SpawnConfiguration.DEFAULT_ATTACK_DAMAGE;
import static com.hellish.ecs.component.EntitySpawnComponent.SpawnConfiguration.DEFAULT_LIFE;
import static com.hellish.ecs.component.PhysicsComponent.ENEMY_BIT;
import static com.hellish.ecs.component.PhysicsComponent.ENVIRONMENT_BIT;
import static com.hellish.ecs.component.PhysicsComponent.PLAYER_BIT;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;
import com.hellish.Main;
import com.hellish.actor.FlipImage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AiComponent;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.InventoryComponent;
import com.hellish.ecs.component.ItemComponent.ItemType;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.LightComponent;
import com.hellish.ecs.component.LightComponent.ClosedFloatingPointRange;
import com.hellish.ecs.component.LootComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.component.EntitySpawnComponent;
import com.hellish.event.CooldownChangeEvent;
import com.hellish.event.EntityLifeChangeEvent;
import com.hellish.event.EventUtils;
import com.hellish.event.GameRestartEvent;
import com.hellish.event.MapChangeEvent;
import com.hellish.map.MapManager;
import com.hellish.map.MapType;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.hellish.ecs.component.AnimationComponent.AnimationModel;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.CollisionComponent;
import com.hellish.ecs.component.CooldownComponent;
import com.hellish.ecs.component.EntitySpawnComponent.SpawnConfiguration;
import com.hellish.ecs.component.StateComponent;
import com.hellish.ecs.component.TextComponent;

public class EntitySpawnSystem extends IteratingSystem implements EventListener{
	public static final String TAG = EntitySpawnSystem.class.getSimpleName();
	public static final String COLLISION_BOX = "CollisionBox";
	public static final String HIT_BOX_SENSOR = "HitBoxSensor";
	public static final String AI_SENSOR = "AiSensor";
	private final MapManager mapManager;
	private final World world;
	private final Stage stage;
	private final RayHandler rayHandler;
	private final AssetManager assetManager;
	private final Map<String, SpawnConfiguration> cachedSpawnCfgs;
	private final Map<AnimationModel, Vector2> cachedSizes;
	
	public static final SpawnConfiguration PLAYER_CFG;
	
	static {
		SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.PLAYER);
		builder.speedScaling = 1.75f;
		builder.physicsScaling.set(0.2f, 0.44f);
		builder.physicsOffset.set(0, -18 * UNIT_SCALE);
		builder.lifeScaling = 5;
		builder.attackScaling = 1.25f;
		builder.attackExtraRange = 0.75f;
		builder.hasLight = true;
		builder.physicsCategory = PLAYER_BIT;
		PLAYER_CFG = new SpawnConfiguration(builder);
	}

	public EntitySpawnSystem(final Main context) {
		super(Family.all(EntitySpawnComponent.class).get());
		rayHandler = context.getRayHandler();
		world = context.getWorld();
		stage = context.getGameStage();
		mapManager = context.getMapManager();
		assetManager = context.getAssetManager();
		cachedSpawnCfgs = new HashMap<>();
		cachedSizes = new HashMap<>();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final EntitySpawnComponent spawnCmp = ECSEngine.entitySpawnCmpMapper.get(entity);
		final SpawnConfiguration cfg = spawnCfg(spawnCmp.type);
		final Vector2 relativeSize = size(cfg.model); 
		
		final Entity spawnedEntity = getEngine().createEntity();
		
		//Thành phần Image
		final ImageComponent imageCmp = getEngine().createComponent(ImageComponent.class);
		imageCmp.image = new FlipImage();
		imageCmp.image.setPosition(spawnCmp.location.x, spawnCmp.location.y);
		imageCmp.image.setSize(relativeSize.x, relativeSize.y);
		imageCmp.image.setScaling(Scaling.fill);
		spawnedEntity.add(imageCmp);
		
		//Thành phần Physics
		final PhysicsComponent physicsCmp = PhysicsComponent.physicsCmpFromImgandCfg(
				getEngine(), world, imageCmp.image, cfg);
		
		if(cfg.speedScaling > 0) {
			//Điều chỉnh cho các thông tin liên quan đến steering behavior
			//Thực ra player cũng bị thêm thông tin nhưng vì không phải AiEntity nên steerer luôn null
			//TODO xử lý hợp lý hơn
			physicsCmp.setMaxLinearSpeed(DEFAULT_MAX_SPEED * cfg.speedScaling);
			physicsCmp.setMaxLinearAcceleration(DEFAULT_MAX_ACCELERATION * cfg.accelerationScaling);
		}

		spawnedEntity.add(physicsCmp);
		
		//Thành phần Light
		if (cfg.hasLight) {
    		LightComponent lightComponent = getEngine().createComponent(LightComponent.class);
    		lightComponent.distance = new ClosedFloatingPointRange(5f, 6.5f);
    		lightComponent.light = new PointLight(rayHandler, 64, LightComponent.LIGHT_COLOR, lightComponent.distance.getEnd(), 0f, 0f);
    		lightComponent.light.attachToBody(physicsCmp.body);
    		spawnedEntity.add(lightComponent);
		}

		//Thành phần Animation
		final AnimationComponent aniCmp = getEngine().createComponent(AnimationComponent.class);
		aniCmp.mode = Animation.PlayMode.LOOP;
		aniCmp.nextAnimation(cfg.model, AnimationType.IDLE);
		spawnedEntity.add(aniCmp);
		
		//Thành phần Attack (cho thứ biết tấn công)
		if(cfg.canAttack) {
			final AttackComponent attackCmp = getEngine().createComponent(AttackComponent.class);
			attackCmp.maxDelay = cfg.attackDelay;
			attackCmp.damage = Math.round(DEFAULT_ATTACK_DAMAGE * cfg.attackScaling);
			attackCmp.extraRange = cfg.attackExtraRange;
			attackCmp.canFire = cfg.canFire;
			spawnedEntity.add(attackCmp);
		}
		
		//Thành phần Life (cho thứ có HP)
		if(cfg.lifeScaling > 0) {
			final LifeComponent lifeCmp = getEngine().createComponent(LifeComponent.class);
			lifeCmp.max = (int) (DEFAULT_LIFE * cfg.lifeScaling);
			lifeCmp.life = lifeCmp.max;
			spawnedEntity.add(lifeCmp);
		}
		
		//Thành phần Player, State, Inventory, Move, Cooldown (cho nhân vật người chơi)
		if(spawnCmp.type.equals("Player")) {
			spawnedEntity.add(getEngine().createComponent(PlayerComponent.class));
			spawnedEntity.add(getEngine().createComponent(StateComponent.class));
			spawnedEntity.add(getEngine().createComponent(CooldownComponent.class));
			
			MoveComponent moveCmp = getEngine().createComponent(MoveComponent.class);
			moveCmp.speed = DEFAULT_MAX_SPEED * cfg.speedScaling;
			spawnedEntity.add(moveCmp);
			
			InventoryComponent itemCmp = getEngine().createComponent(InventoryComponent.class);
			itemCmp.itemsToAdd.add(ItemType.SWORD);
			itemCmp.itemsToAdd.add(ItemType.BIG_SWORD);
			itemCmp.itemsToAdd.add(ItemType.ARMOR);
			itemCmp.itemsToAdd.add(ItemType.HELMET);
			itemCmp.itemsToAdd.add(ItemType.BOOTS);
			spawnedEntity.add(itemCmp);
		}
		
		//Thành phần Loot (cho đồ loot được)
		if(cfg.lootable) {
			spawnedEntity.add(getEngine().createComponent(LootComponent.class));
		}
		
		//Thành phần Collision (nếu entity không static thì thêm để spawn những collision entity xung quanh)
		if (cfg.bodyType != BodyType.StaticBody) {
			spawnedEntity.add(getEngine().createComponent(CollisionComponent.class));
		}
		
		//Thành phần AI(cho enemy)
		if(!cfg.aiTreePath.isBlank()) {
			final AiComponent aiCmp = getEngine().createComponent(AiComponent.class);
			aiCmp.treePath = cfg.aiTreePath;
			spawnedEntity.add(aiCmp);
			
			CircleShape circleShape = new CircleShape();
			
			if(spawnCmp.type.equals("Boss")) {
				circleShape.setRadius(8);
			} else {
				circleShape.setRadius(5);
			}
			Fixture fixture = physicsCmp.body.createFixture(circleShape, 0);
			circleShape.dispose();
			fixture.setUserData(AI_SENSOR);
			fixture.setSensor(true);
		}
		
		if(spawnCmp.type.equals("Boss")) {
			final TextComponent txtCmp = getEngine().createComponent(TextComponent.class);
			
			BitmapFont damageFont = new BitmapFont(Gdx.files.internal("ui/damage.fnt"));
			damageFont.getData().setScale(0.75f);
			LabelStyle txtFntStyle = new Label.LabelStyle(damageFont, Color.WHITE);
			txtCmp.label = new Label("OOP", txtFntStyle);
			
			spawnedEntity.add(txtCmp);
		}
		
		getEngine().addEntity(spawnedEntity);
		
		//nếu là Player thì cập nhật thông số UI
		if(spawnCmp.type.equals("Player")) {
			EventUtils.fireEvent(stage, EntityLifeChangeEvent.pool, event -> event.set(spawnedEntity, 0f));
			EventUtils.fireEvent(stage, CooldownChangeEvent.pool, event -> event.set(spawnedEntity, 0f));
		}
		
		getEngine().removeEntity(entity);
	}
	
	private SpawnConfiguration spawnCfg(String type) {
		return cachedSpawnCfgs.computeIfAbsent(type, t -> {
			if(t.equals("Player")) {
				return PLAYER_CFG;
			} else if (t.equals("Wolf")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.WOLF);
				builder.physicsScaling.set(0.4f, 0.4f);
				builder.physicsOffset.set(0, -5 * UNIT_SCALE);
				builder.attackExtraRange = 0.1f;
				builder.aiTreePath = "ai/wolf.tree";
				builder.hasLight = true;
				builder.physicsCategory = ENEMY_BIT;
				return new SpawnConfiguration(builder);
			} else if (t.equals("FlagZombie")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.FLAG_ZOMBIE);
				builder.physicsScaling.set(0.44f, 0.72f);
				builder.physicsOffset.set(0, -9 * UNIT_SCALE);
				builder.attackScaling = 0.75f;
				builder.lifeScaling = 1.5f;
				builder.aiTreePath = "ai/zombie.tree";
				builder.hasLight = true;
				builder.physicsCategory = ENEMY_BIT;
				return new SpawnConfiguration(builder);
			} else if (t.equals("RunningZombie")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.RUNNING_ZOMBIE);
				builder.speedScaling = 2;
				builder.physicsScaling.set(0.48f, 0.75f);
				builder.physicsOffset.set(0, -8 * UNIT_SCALE);
				builder.attackScaling = 1.1f;
				builder.lifeScaling = 1.75f;
				builder.aiTreePath = "ai/zombie.tree";
				builder.hasLight = true;
				builder.physicsCategory = ENEMY_BIT;
				return new SpawnConfiguration(builder);
			} else if (t.equals("TreeZombie")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.TREE_ZOMBIE);
				builder.speedScaling = 0.75f;
				builder.physicsScaling.set(0.4f, 0.78f);
				builder.physicsOffset.set(0, -7 * UNIT_SCALE);
				builder.attackScaling = 1.5f;
				builder.lifeScaling = 2;
				builder.aiTreePath = "ai/zombie.tree";
				builder.hasLight = true;
				builder.physicsCategory = ENEMY_BIT;
				return new SpawnConfiguration(builder);
			} else if (t.equals("Boss")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.BOSS);
				builder.speedScaling = 2;
				builder.physicsScaling.set(0.4f, 0.5f); 
				builder.physicsOffset.set(0, -30 * UNIT_SCALE);
				builder.attackScaling = 4;
				builder.attackExtraRange = 2.8f;
				builder.canFire = true;
				builder.lifeScaling = 10;
				builder.aiTreePath = "ai/boss.tree";
				builder.hasLight = true;
				builder.physicsCategory = ENEMY_BIT;
				return new SpawnConfiguration(builder);
			} else if (t.equals("Chest")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.CHEST);
				builder.physicsScaling.set(0.25f, 0.1f);
				builder.physicsOffset.set(1.5f * UNIT_SCALE, -28 * UNIT_SCALE);
				builder.canAttack = false;
				builder.lifeScaling = 0;
				builder.lootable = true;
				builder.bodyType = BodyType.StaticBody;
				builder.hasLight = true;
				builder.physicsCategory = ENVIRONMENT_BIT;
				return new SpawnConfiguration(builder);
			} else {
				throw new IllegalArgumentException("Loại spawn " + t + " không có cài đặt SpawnConfiguration");
			}
		});
	}
	
	public Vector2 size(AnimationModel model) {
		return cachedSizes.computeIfAbsent(model, m -> {
			final TextureAtlas atlas = assetManager.get("game_objects/gameObjects.atlas", TextureAtlas.class);
			Array<AtlasRegion> regions = atlas.findRegions(model.getModel() + "/" + AnimationType.IDLE.getAtlasKey());
			if (regions.size == 0) {
				throw new RuntimeException("Không có texture region cho " + model);
			}
			AtlasRegion firstFrame = regions.first();	
			return new Vector2(firstFrame.originalWidth * UNIT_SCALE, firstFrame.originalHeight * UNIT_SCALE);
		});
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof MapChangeEvent) {
			final MapChangeEvent mapChangeEvent = (MapChangeEvent) event;
			if(mapChangeEvent.getTiledMap() == null) {
				return false;
			}
			
			MapGroupLayer groupLayer = (MapGroupLayer) mapManager.getCurrentMap().getLayers().get("entities");
			if (groupLayer == null) {
				return false;
			}
			
			for(MapLayer entityLayer : groupLayer.getLayers()) {
				for (MapObject mapObj : entityLayer.getObjects()) {
					if (! (mapObj instanceof TiledMapTileMapObject)) {
						Gdx.app.log(TAG, "GameObject kiểu " + mapObj + " trong layer 'entities' không được hỗ trợ.");
						continue;
					}
					TiledMapTileMapObject tiledMapObj = (TiledMapTileMapObject) mapObj;
					String type = tiledMapObj.getTile().getProperties().get("type", String.class);
					if (type == null) {
						throw new GdxRuntimeException("MapObject " + mapObj + " trong layer 'entities' không có property Class");
					}
					
					if(type.equals("Player") && getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get()).size() > 0) {
						continue;
					}
					
					Entity entity = getEngine().createEntity();
					EntitySpawnComponent spawnComponent = getEngine().createComponent(EntitySpawnComponent.class);
					spawnComponent.type = type;
					spawnComponent.location.set(tiledMapObj.getX() * UNIT_SCALE, tiledMapObj.getY() * UNIT_SCALE);
					entity.add(spawnComponent);
					getEngine().addEntity(entity);
				}
			}
			return true;
		} else if(event instanceof GameRestartEvent) {
			Array<Entity> entities = new Array<Entity>();
			
			//Không dùng getEngine().removeAllEntities() vì sẽ không báo được ComponentListener
			for(Entity entity : getEngine().getEntities()) {
				entities.add(entity);
			}
			
			for (Entity entity : entities) {
				getEngine().removeEntity(entity);
		    }
			
			mapManager.setMap(MapType.MAP_1);
			return true;
		}
		return false;
	}
}
