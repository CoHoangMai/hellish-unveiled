package com.hellish.view;

import static com.hellish.Main.UNIT_SCALE;

import java.util.EnumMap;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.Box2DComponent;
import com.hellish.ecs.component.GameObjectComponent;
import com.hellish.ecs.component.ParticleEffectComponent;
import com.hellish.map.Map;
import com.hellish.map.MapListener;

import box2dLight.RayHandler;

public class GameRenderer implements Disposable, MapListener{
	public static final String TAG = GameRenderer.class.getSimpleName();
	
	private final OrthographicCamera gameCamera;
	private final FitViewport viewport;
	private final SpriteBatch spriteBatch;
	private final AssetManager assetManager;
	private final EnumMap<AnimationType, Animation<Sprite>> animationCache;
	
	private final ImmutableArray<Entity> animatedEntities;
	private final ImmutableArray<Entity> gameObjectEntities;
	private final ImmutableArray<Entity> effectsToRender;
	private final OrthogonalTiledMapRenderer mapRenderer;
	private final Array<TiledMapTileLayer> tiledMapLayers;
	private IntMap<Animation<Sprite>> mapAnimations;

	private final GLProfiler profiler;
	protected final Box2DDebugRenderer box2DDebugRenderer;
	private final World world;
	private final RayHandler rayHandler;
	
	public GameRenderer(final Main context) {
		assetManager = context.getAssetManager();
		viewport = context.getScreenViewport();
		gameCamera = context.getGameCamera();
		spriteBatch = context.getSpriteBatch();
		
		animationCache = new EnumMap<AnimationType, Animation<Sprite>>(AnimationType.class);
		
		gameObjectEntities = context.getECSEngine().getEntitiesFor(Family.all(GameObjectComponent.class, Box2DComponent.class, AnimationComponent.class).get());
		animatedEntities = context.getECSEngine().getEntitiesFor(Family.all(AnimationComponent.class, Box2DComponent.class).exclude(GameObjectComponent.class).get());
		effectsToRender = context.getECSEngine().getEntitiesFor(Family.all(ParticleEffectComponent.class).get());
		
		mapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, context.getSpriteBatch());
		context.getMapManager().addMapListener(this);
		tiledMapLayers = new Array<TiledMapTileLayer>();
		
		
		
		profiler = new GLProfiler(Gdx.graphics);
		profiler.enable();
		if (profiler.isEnabled()) {
			box2DDebugRenderer = context.getBox2DDebugRenderer();
			world = context.getWorld();
		} else {
			box2DDebugRenderer = null;
			world = null;
		}
		rayHandler = context.getRayHandler();
	}
	
	public void render(final float alpha) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		viewport.apply(false);
		
		mapRenderer.setView(gameCamera);
		spriteBatch.begin();
		if(mapRenderer.getMap() != null) {
			AnimatedTiledMapTile.updateAnimationBaseTime();
			for(final TiledMapTileLayer layer : tiledMapLayers) {
				mapRenderer.renderTileLayer(layer);
			}
		}
		for (final Entity entity : gameObjectEntities) {
			renderGameObject(entity, alpha);
		}
		for (final Entity entity : animatedEntities) {
			renderEntity(entity, alpha);
		}
		for (final Entity entity : effectsToRender) {
			final ParticleEffectComponent peCmp = ECSEngine.peCmpMapper.get(entity);
			if(peCmp.effect != null) {
				peCmp.effect.draw(spriteBatch);
			}
		}
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.end();
		
		rayHandler.setCombinedMatrix(gameCamera);
		rayHandler.updateAndRender();
		
		if (profiler.isEnabled()) {
		//	Gdx.app.debug(TAG, "Bindings: " + profiler.getTextureBindings());
		//	Gdx.app.debug(TAG, "Drawcalls: " + profiler.getDrawCalls());
			profiler.reset();
			
			box2DDebugRenderer.render(world, viewport.getCamera().combined);
		}
	}

	private void renderGameObject(final Entity entity, final float alpha) {
		final Box2DComponent b2dComponent = ECSEngine.b2dCmpMapper.get(entity);
		final AnimationComponent aniComponent = ECSEngine.aniCmpMapper.get(entity);
		final GameObjectComponent gameObjectComponent = ECSEngine.gameObjCmpMapper.get(entity);
		
		if (gameObjectComponent.animationIndex != -1) {
			final Animation<Sprite> animation = mapAnimations.get(gameObjectComponent.animationIndex);
			final Sprite frame = animation.getKeyFrame(aniComponent.aniTime);
			frame.setBounds(b2dComponent.renderPosition.x, b2dComponent.renderPosition.y,
							aniComponent.width, aniComponent.height);
			frame.setOriginCenter();
			frame.setRotation(b2dComponent.body.getAngle() * MathUtils.radDeg);
			frame.draw(spriteBatch);
		}
	}

	private void renderEntity(Entity entity, float alpha) {
		final Box2DComponent b2dComponent = ECSEngine.b2dCmpMapper.get(entity);
		final AnimationComponent aniComponent = ECSEngine.aniCmpMapper.get(entity);
		
		if (aniComponent.aniType != null) {
			final Animation<Sprite> animation = getAnimation(aniComponent.aniType);
			final Sprite frame = animation.getKeyFrame(aniComponent.aniTime);
			
			b2dComponent.renderPosition.lerp(b2dComponent.body.getPosition(), alpha);
			
			boolean isFacingRight = frame.isFlipX();
			if (b2dComponent.body.getLinearVelocity().x < 0 && isFacingRight) {
	            frame.setFlip(false, false); // Lật sang trái
	        } else if (b2dComponent.body.getLinearVelocity().x > 0 && !isFacingRight) {
	            frame.setFlip(true, false); // Lật sang phải
	        }
			
			frame.setBounds(b2dComponent.renderPosition.x - aniComponent.height * 0.5f, 
					b2dComponent.renderPosition.y - aniComponent.width * 0.5f,
					aniComponent.width, aniComponent.height);
			frame.draw(spriteBatch);
		}
	}

	private Animation<Sprite> getAnimation(AnimationType aniType) {
		Animation<Sprite> animation = animationCache.get(aniType);
		if(animation == null) {
			Gdx.app.debug(TAG, "Tạo animation mới loại " + aniType);
			animation = new Animation<Sprite>(aniType.getFrameTime(), getKeyFrames(aniType));
			animation.setPlayMode(Animation.PlayMode.LOOP);
			animationCache.put(aniType, animation);
		}
		return animation;
	}

	private Sprite[] getKeyFrames(AnimationType aniType) {
		final TextureAtlas atlas = assetManager.get(aniType.getAtlasPath(), TextureAtlas.class);
		final Sprite[] keyFrames = new Sprite[aniType.getFrameCount()];
		
		for (int i = 0; i < aniType.getFrameCount(); i++) {
			final Sprite sprite = new Sprite(atlas.findRegion(aniType.getAtlasKey(), i));
			sprite.setOriginCenter();
			keyFrames[i] = sprite;
		}
		return keyFrames;
	}

	@Override
	public void dispose() {
		if (box2DDebugRenderer != null) {
			box2DDebugRenderer.dispose();
		}
		mapRenderer.dispose();
	}

	@Override
	public void mapChange(Map map) {
		mapRenderer.setMap(map.getTiledMap());	
		map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class, tiledMapLayers);
		mapAnimations = map.getMapAnimations();
	}
}
