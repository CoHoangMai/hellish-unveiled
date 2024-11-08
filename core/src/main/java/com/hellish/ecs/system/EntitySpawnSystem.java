package com.hellish.ecs.system;

import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.ecs.component.SpawnComponent.SpawnConfiguration.DEFAULT_SPEED;
import static com.hellish.ecs.component.SpawnComponent.SpawnConfiguration.DEFAULT_ATTACK_DAMAGE;
import static com.hellish.ecs.component.SpawnComponent.SpawnConfiguration.DEFAULT_LIFE;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;
import com.hellish.Main;
import com.hellish.actor.FlipImage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AiComponent;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.LootComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.component.SpawnComponent;
import com.hellish.event.MapChangeEvent;
import com.hellish.ecs.component.AnimationComponent.AnimationModel;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.CollisionComponent;
import com.hellish.ecs.component.SpawnComponent.SpawnConfiguration;
import com.hellish.ecs.component.StateComponent;

public class EntitySpawnSystem extends IteratingSystem implements EventListener{
	public static final String TAG = EntitySpawnSystem.class.getSimpleName();
	public static final String HIT_BOX_SENSOR = "HitBoxSensor";
	public static final String AI_SENSOR = "AiSensor";
	private final World world;
	private final AssetManager assetManager;
	private final Map<String, SpawnConfiguration> cachedSpawnCfgs;
	private final Map<AnimationModel, Vector2> cachedSizes;

	public EntitySpawnSystem(final Main context) {
		super(Family.all(SpawnComponent.class).get());
		
		world = context.getWorld();
		assetManager = context.getAssetManager();
		cachedSpawnCfgs = new HashMap<>();
		cachedSizes = new HashMap<>();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final SpawnComponent spawnCmp = ECSEngine.spawnCmpMapper.get(entity);
		final SpawnConfiguration cfg = spawnCfg(spawnCmp.type);
		final Vector2 relativeSize = size(cfg.model); 
		
		final Entity spawnedEntity = getEngine().createEntity();
		
		final ImageComponent imageCmp = getEngine().createComponent(ImageComponent.class);
		imageCmp.image = new FlipImage();
		imageCmp.image.setPosition(spawnCmp.location.x, spawnCmp.location.y);
		imageCmp.image.setSize(relativeSize.x, relativeSize.y);
		imageCmp.image.setScaling(Scaling.fill);
		spawnedEntity.add(imageCmp);
		
		final AnimationComponent aniCmp = getEngine().createComponent(AnimationComponent.class);
		aniCmp.mode = Animation.PlayMode.LOOP;
		aniCmp.nextAnimation(cfg.model, AnimationType.IDLE);
		spawnedEntity.add(aniCmp);
		
		final PhysicsComponent physicsCmp = PhysicsComponent.physicsCmpFromImgandCfg(world, imageCmp.image, cfg);
		spawnedEntity.add(physicsCmp);
		
		if (cfg.speedScaling > 0) {
			final MoveComponent moveCmp = new MoveComponent();
			moveCmp.speed = DEFAULT_SPEED * cfg.speedScaling;
			spawnedEntity.add(moveCmp);
		}
		
		if(cfg.canAttack) {
			final AttackComponent attackCmp = new AttackComponent();
			attackCmp.maxDelay = cfg.attackDelay;
			attackCmp.damage = Math.round(DEFAULT_ATTACK_DAMAGE * cfg.attackScaling);
			attackCmp.extraRange = cfg.attackExtraRange;
			spawnedEntity.add(attackCmp);
		}
		
		if(cfg.lifeScaling > 0) {
			final LifeComponent lifeCmp = new LifeComponent();
			lifeCmp.max = DEFAULT_LIFE * cfg.lifeScaling;
			spawnedEntity.add(lifeCmp);
		}
		
		if(spawnCmp.type.equals("Player")) {
			spawnedEntity.add(new PlayerComponent());
			spawnedEntity.add(new StateComponent());
		}
		
		if(cfg.lootable) {
			spawnedEntity.add(new LootComponent());
		}
		
		if (cfg.bodyType != BodyType.StaticBody) {
			spawnedEntity.add(new CollisionComponent());
		}
		
		if(!cfg.aiTreePath.isBlank()) {
			final AiComponent aiCmp = new AiComponent();
			aiCmp.treePath = cfg.aiTreePath;
			spawnedEntity.add(aiCmp);
			
			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(4);
			Fixture fixture = physicsCmp.body.createFixture(circleShape, 0);
			fixture.setUserData(AI_SENSOR);
			fixture.setSensor(true);
		}
		getEngine().addEntity(spawnedEntity);
		
		getEngine().removeEntity(entity);
	}
	
	private SpawnConfiguration spawnCfg(String type) {
		return cachedSpawnCfgs.computeIfAbsent(type, t -> {
			if(t.equals("Player")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.PLAYER);
				builder.speedScaling = 1.5f;
				builder.physicsScaling = new Vector2(0.2f, 0.44f);
				builder.physicsOffset = new Vector2(0, -2 * UNIT_SCALE);
				builder.attackScaling = 1.25f;
				builder.attackExtraRange = 0.75f;
				return new SpawnConfiguration(builder);
			} else if (t.equals("Wolf")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.WOLF);
				builder.physicsScaling = new Vector2(0.4f, 0.4f);
				builder.physicsOffset = new Vector2(0, -5 * UNIT_SCALE);
				builder.lifeScaling = 0.75f;
				builder.attackExtraRange = 0.1f;
				builder.aiTreePath = "ai/wolf.tree";
				return new SpawnConfiguration(builder);
			} else if (t.equals("Chest")) {
				SpawnConfiguration.Builder builder = new SpawnConfiguration.Builder(AnimationModel.CHEST);
				builder.speedScaling = 0;
				builder.physicsScaling = new Vector2(0.3f, 0.2f);
				builder.canAttack = false;
				builder.lifeScaling = 0;
				builder.lootable = true;
				builder.bodyType = BodyType.StaticBody;
				return new SpawnConfiguration(builder);
			} else {
				throw new IllegalArgumentException("Loại spawn " + t + " không có cài đặt SpawnConfiguration");
			}
		});
	}
	
	private Vector2 size(AnimationModel model) {
		return cachedSizes.computeIfAbsent(model, m -> {
			final TextureAtlas atlas = assetManager.get("characters_and_effects/gameObjects.atlas", TextureAtlas.class);
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
			MapLayer entityLayer = mapChangeEvent.getMap().getTiledMap().getLayers().get("entities");
			if (entityLayer == null) {
				return false;
			}
			for (MapObject mapObj : entityLayer.getObjects()) {
				if (! (mapObj instanceof TiledMapTileMapObject)) {
					Gdx.app.log(TAG, "GameObject kiểu " + mapObj + " không được hỗ trợ trong layer 'entities'.");
					continue;
				}
				TiledMapTileMapObject tiledMapObj = (TiledMapTileMapObject) mapObj;
				String type = tiledMapObj.getTile().getProperties().get("type", String.class);
				if (type == null) {
					throw new GdxRuntimeException("MapObject " + mapObj + " trong layer 'entities' không có property Class");
				}
				Entity entity = getEngine().createEntity();
				SpawnComponent spawnComponent = new SpawnComponent();
				spawnComponent.type = type;
				spawnComponent.location.set(tiledMapObj.getX() * UNIT_SCALE, tiledMapObj.getY() * UNIT_SCALE);
				entity.add(spawnComponent);
				getEngine().addEntity(entity);
			}
			return true;	
		}
		return false;
	}
			
}
