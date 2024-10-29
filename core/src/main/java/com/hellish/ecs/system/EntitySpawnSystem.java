package com.hellish.ecs.system;

import static com.hellish.Main.UNIT_SCALE;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.PhysicComponent;
import com.hellish.ecs.component.SpawnComponent;
import com.hellish.event.MapChangeEvent;
import com.hellish.view.AnimationModel;
import com.hellish.view.AnimationType;
import com.hellish.view.SpawnConfiguration;

public class EntitySpawnSystem extends IteratingSystem implements EventListener{
	public static final String TAG = EntitySpawnSystem.class.getSimpleName();
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
		imageCmp.image = new Image(new Texture("characters_and_effects/particle.png"));
		imageCmp.image.setPosition(spawnCmp.location.x, spawnCmp.location.y);
		imageCmp.image.setSize(relativeSize.x, relativeSize.y);
		imageCmp.image.setScaling(Scaling.fill);
		spawnedEntity.add(imageCmp);
		
		final AnimationComponent aniCmp = getEngine().createComponent(AnimationComponent.class);
		aniCmp.aniType = AnimationType.UP_WALK;
		aniCmp.mode = Animation.PlayMode.LOOP;
		aniCmp.nextAnimation(cfg.model, AnimationType.UP_WALK);
		aniCmp.width = 64 * UNIT_SCALE;
		aniCmp.height = 64 * UNIT_SCALE;
		spawnedEntity.add(aniCmp);
		
		final PhysicComponent physicCmp = PhysicComponent.physicCmpFromImage(world, imageCmp.image, BodyType.DynamicBody);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = false;
		//fixtureDef.filter.categoryBits = BIT_PLAYER;
		//fixtureDef.filter.maskBits = BIT_GROUND | BIT_GAME_OBJECT;
		final PolygonShape pShape = new PolygonShape();
		pShape.setAsBox(imageCmp.image.getWidth() * 0.5f, imageCmp.image.getHeight() * 0.5f);
		fixtureDef.shape = pShape;
		physicCmp.body.createFixture(fixtureDef);
		pShape.dispose();	 
		spawnedEntity.add(physicCmp);
		
		getEngine().addEntity(spawnedEntity);
		
		getEngine().removeEntity(entity);
	}
	
	private SpawnConfiguration spawnCfg(String type) {
		return cachedSpawnCfgs.computeIfAbsent(type, t -> {
			if(t.equals("Player")) {
				return new SpawnConfiguration(AnimationModel.PLAYER);
			} else if (t.equals("Wolf")) {
				return new SpawnConfiguration(AnimationModel.WOLF);
			} else {
				throw new IllegalArgumentException("Loại spawn " + t + " không có cài đặt SpawnConfiguration");
			}
		});
	}
	
	private Vector2 size(AnimationModel model) {
		return cachedSizes.computeIfAbsent(model, m -> {
			final TextureAtlas atlas = assetManager.get("characters_and_effects/gameObjects.atlas", TextureAtlas.class);
			Array<AtlasRegion> regions = atlas.findRegions(model.getModel() + "/" + AnimationType.UP_WALK.getAtlasKey());
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
