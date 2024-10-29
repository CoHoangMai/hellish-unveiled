package com.hellish.ecs;

import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.Main.BIT_GROUND;
import static com.hellish.Main.BIT_PLAYER;
import static com.hellish.Main.BIT_GAME_OBJECT;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hellish.Main;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.Box2DComponent;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.component.GameObjectComponent;
import com.hellish.ecs.component.ParticleEffectComponent;
import com.hellish.ecs.component.ParticleEffectComponent.ParticleEffectType;
import com.hellish.ecs.component.PhysicComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.component.SpawnComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.system.AnimationSystem;
import com.hellish.ecs.system.DebugSystem;
import com.hellish.ecs.system.EntitySpawnSystem;
import com.hellish.ecs.system.LightSystem;
import com.hellish.ecs.system.ParticleEffectSystem;
import com.hellish.ecs.system.PhysicSystem;
import com.hellish.ecs.system.PlayerAnimationSystem;
import com.hellish.ecs.system.PlayerCameraSystem;
import com.hellish.ecs.system.PlayerCollisionSystem;
import com.hellish.ecs.system.PlayerMovementSystem;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.map.GameObject;
import com.hellish.view.AnimationModel;
import com.hellish.view.AnimationType;
import com.hellish.ecs.component.ImageComponent.ImageComponentListener;
import com.hellish.ecs.component.PhysicComponent.PhysicComponentListener;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class ECSEngine extends PooledEngine{
	public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
	public static final ComponentMapper<Box2DComponent> b2dCmpMapper = ComponentMapper.getFor(Box2DComponent.class);
	public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
	public static final ComponentMapper<GameObjectComponent> gameObjCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);
	public static final ComponentMapper<ParticleEffectComponent> peCmpMapper = ComponentMapper.getFor(ParticleEffectComponent.class);
	public static final ComponentMapper<ImageComponent> imageCmpMapper = ComponentMapper.getFor(ImageComponent.class);
	public static final ComponentMapper<SpawnComponent> spawnCmpMapper = ComponentMapper.getFor(SpawnComponent.class); 
	public static final ComponentMapper<PhysicComponent> physicCmpMapper = ComponentMapper.getFor(PhysicComponent.class); 
	
	private final RayHandler rayHandler;
	private final World world;
	private final Stage stage;
	private final ComponentManager componentManager;
	private final Vector2 localPosition;
	private final Vector2 posBeforeRotation;
	private final Vector2 posAfterRotation;
	
	public ECSEngine(final Main context) {
		super();
		
		stage = context.getStage();
		world = context.getWorld();	
		componentManager = context.getComponentManager();
		rayHandler = context.getRayHandler();
		localPosition = new Vector2();
		posBeforeRotation = new Vector2();
		posAfterRotation = new Vector2();
		
		componentManager.addComponentListener(new ImageComponentListener(stage));
		componentManager.addComponentListener(new PhysicComponentListener());
		
		
		this.addSystem(new PlayerMovementSystem(context));
		this.addSystem(new PlayerCameraSystem(context));
		this.addSystem(new EntitySpawnSystem(context));
		this.addSystem(new PhysicSystem(context));
		this.addSystem(new AnimationSystem(context));
		this.addSystem(new RenderSystem(context));
		this.addSystem(new DebugSystem(context));
		this.addSystem(new PlayerAnimationSystem(context));
		this.addSystem(new LightSystem());
		this.addSystem(new ParticleEffectSystem(context));
		this.addSystem(new PlayerCollisionSystem(context));
	}
	
	public Entity createPlayer(final Vector2 playerSpawnLocation, final float width, final float height) {
		final Entity player = this.createEntity();
		
		//Thành phần player
		final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
		playerComponent.speed.set(3,3);
		player.add(playerComponent);
		
		//Thành phần Box2D
		Main.resetBodyAndFixtureDefinition();
		final Box2DComponent b2dComponent = this.createComponent(Box2DComponent.class);
		Main.BODY_DEF.position.set(playerSpawnLocation.x, playerSpawnLocation.y + 0.5f);
		b2dComponent.prevPosition.set(Main.BODY_DEF.position);
		Main.BODY_DEF.fixedRotation = true;
		Main.BODY_DEF.type = BodyDef.BodyType.DynamicBody;
		b2dComponent.body = world.createBody(Main.BODY_DEF);
		b2dComponent.body.setUserData(player);
		b2dComponent.width = width;
		b2dComponent.height = height;
		b2dComponent.renderPosition.set(b2dComponent.body.getPosition());
		
		Main.FIXTURE_DEF.filter.categoryBits = BIT_PLAYER;
		Main.FIXTURE_DEF.filter.maskBits = BIT_GROUND | BIT_GAME_OBJECT;
		final PolygonShape pShape = new PolygonShape();
		pShape.setAsBox(0.5f, 0.5f);
		Main.FIXTURE_DEF.shape = pShape;
		b2dComponent.body.createFixture(Main.FIXTURE_DEF);
		pShape.dispose();	
		
		b2dComponent.lightDistance = 6;
		b2dComponent.lightFluctuationSpeed = 4;
		b2dComponent.light = new PointLight(rayHandler, 64, new Color(1, 1, 1, 0.7f), b2dComponent.lightDistance,
							b2dComponent.body.getPosition().x, b2dComponent.body.getPosition().y);
		b2dComponent.lightFluctuationDistance = b2dComponent.light.getDistance() * 0.16f;
		b2dComponent.light.attachToBody(b2dComponent.body);
		
		player.add(b2dComponent);
		
		//Thành phần Image
		final ImageComponent imageCmp = this.createComponent(ImageComponent.class);
		//TODO bỏ đoạn test này
		imageCmp.layer = 0;
		imageCmp.image = new Image(new Texture("characters_and_effects/particle.png"));
		imageCmp.image.setScale(UNIT_SCALE);
		imageCmp.image.setPosition(0, 4);
		player.add(imageCmp);
		
		//Thành phần Animation
		final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
		animationComponent.aniType = AnimationType.DOWN_WALK;
		animationComponent.mode = Animation.PlayMode.LOOP;
		animationComponent.nextAnimation(AnimationModel.PLAYER, AnimationType.DOWN_IDLE);
		animationComponent.width = 64 * UNIT_SCALE;
		animationComponent.height = 64 * UNIT_SCALE;
		player.add(animationComponent);
		
		this.addEntity(player);
		return player;
	}

	public void createGameObject(final GameObject gameObj) {
		final Entity gameObjEntity = this.createEntity();
		
		//Thành phần GameObject
		final GameObjectComponent gameObjComponent = this.createComponent(GameObjectComponent.class);
		gameObjComponent.animationIndex = gameObj.getAnimationIndex();
		gameObjComponent.type = gameObj.getType();
		gameObjEntity.add(gameObjComponent);
		
		//Thành phần Image
		final ImageComponent imageCmp = this.createComponent(ImageComponent.class);
		//TODO bỏ đoạn test này
		imageCmp.layer = 0;
		imageCmp.image = new Image(new TextureRegion(new Texture("characters_and_effects/particle.png")));
		imageCmp.image.setScale(UNIT_SCALE);
		gameObjEntity.add(imageCmp);
		
		//Thành phần Animation
		final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
		animationComponent.aniType = AnimationType.DOWN_WALK;
		animationComponent.mode = Animation.PlayMode.LOOP;
		animationComponent.nextAnimation(AnimationModel.WOLF, AnimationType.DOWN_WALK);
		animationComponent.width = gameObj.getWidth();
		animationComponent.height = gameObj.getHeight();
		gameObjEntity.add(animationComponent);
		
		//Thành phần Box2D
		Main.resetBodyAndFixtureDefinition();
		final float halfWidth = gameObj.getWidth() * 0.5f;
		final float halfHeight = gameObj.getHeight() * 0.5f;
		final float radianAngle = -gameObj.getRotationDegree() * MathUtils.degreesToRadians;
		final Box2DComponent b2dComponent = this.createComponent(Box2DComponent.class);
		Main.BODY_DEF.type = BodyDef.BodyType.StaticBody;
		Main.BODY_DEF.position.set(gameObj.getPosition().x + halfWidth, gameObj.getPosition().y + halfHeight);
		b2dComponent.prevPosition.set(Main.BODY_DEF.position);
		b2dComponent.body = world.createBody(Main.BODY_DEF);
		b2dComponent.body.setUserData(gameObjEntity);
		b2dComponent.width = gameObj.getWidth();
		b2dComponent.height = gameObj.getHeight();
		
		localPosition.set(-halfWidth, -halfHeight);
		posBeforeRotation.set(b2dComponent.body.getWorldPoint(localPosition));
		b2dComponent.body.setTransform(b2dComponent.body.getPosition(), radianAngle);
		posAfterRotation.set(b2dComponent.body.getWorldPoint(localPosition));
		b2dComponent.body.setTransform(b2dComponent.body.getPosition().add(posBeforeRotation).sub(posAfterRotation), radianAngle);
		b2dComponent.renderPosition.set(b2dComponent.body.getPosition().x - b2dComponent.width * 0.5f,
										b2dComponent.body.getPosition().y - b2dComponent.height * 0.5f);
		
		Main.FIXTURE_DEF.filter.categoryBits = BIT_GAME_OBJECT;
		Main.FIXTURE_DEF.filter.maskBits = BIT_PLAYER;
		final PolygonShape pShape = new PolygonShape();
		pShape.setAsBox(halfWidth, halfHeight);
		Main.FIXTURE_DEF.shape = pShape;
		b2dComponent.body.createFixture(Main.FIXTURE_DEF);
		pShape.dispose();
		gameObjEntity.add(b2dComponent);
		
		//Test particle effect
		final ParticleEffectComponent peCmp = createComponent(ParticleEffectComponent.class);
		peCmp.effectType = ParticleEffectType.SMOKE;
		peCmp.scaling = 0.05f;
		peCmp.effectPosition.set(b2dComponent.body.getPosition());
		gameObjEntity.add(peCmp);
		
		this.addEntity(gameObjEntity);
	}
}
