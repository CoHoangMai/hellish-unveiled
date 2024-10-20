package com.hellish.ecs;

import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.Main.BIT_GROUND;
import static com.hellish.Main.BIT_PLAYER;
import static com.hellish.Main.BIT_GAME_OBJECT;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.Main;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.Box2DComponent;
import com.hellish.ecs.component.GameObjectComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.system.AnimationSystem;
import com.hellish.ecs.system.PlayerAnimationSystem;
import com.hellish.ecs.system.PlayerCameraSystem;
import com.hellish.ecs.system.PlayerMovementSystem;
import com.hellish.map.GameObject;
import com.hellish.view.AnimationType;

public class ECSEngine extends PooledEngine{
	public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
	public static final ComponentMapper<Box2DComponent> b2dCmpMapper = ComponentMapper.getFor(Box2DComponent.class);
	public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
	public static final ComponentMapper<GameObjectComponent> gameObjCmpMapper = ComponentMapper.getFor(GameObjectComponent.class);
	
	private final World world;
	
	private final Vector2 localPosition;
	private final Vector2 posBeforeRotation;
	private final Vector2 posAfterRotation;
	
	public ECSEngine(final Main context) {
		super();
		
		world = context.getWorld();	
		
		localPosition = new Vector2();
		posBeforeRotation = new Vector2();
		posAfterRotation = new Vector2();
		
		this.addSystem(new PlayerMovementSystem(context));
		this.addSystem(new PlayerCameraSystem(context));
		this.addSystem(new AnimationSystem(context));
		this.addSystem(new PlayerAnimationSystem(context));
	}
	
	public void createPlayer(final Vector2 playerSpawnLocation, final float width, final float height) {
		final Entity player = this.createEntity();
		
		//Thành phần player
		final PlayerComponent playerComponent = this.createComponent(PlayerComponent.class);
		playerComponent.speed.set(3,3);
		player.add(playerComponent);
		
		//Thành phần Box2D
		Main.resetBodyAndFixtureDefinition();
		final Box2DComponent b2dComponent = this.createComponent(Box2DComponent.class);
		Main.BODY_DEF.position.set(playerSpawnLocation.x, playerSpawnLocation.y + 0.5f);
		Main.BODY_DEF.fixedRotation = true;
		Main.BODY_DEF.type = BodyDef.BodyType.DynamicBody;
		b2dComponent.body = world.createBody(Main.BODY_DEF);
		b2dComponent.body.setUserData("PLAYER");
		b2dComponent.width = width;
		b2dComponent.height = height;
		b2dComponent.renderPosition.set(b2dComponent.body.getPosition());
				
		Main.FIXTURE_DEF.filter.categoryBits = BIT_PLAYER;
		Main.FIXTURE_DEF.filter.maskBits = BIT_GROUND;
		final PolygonShape pShape = new PolygonShape();
		pShape.setAsBox(0.5f, 0.5f);
		Main.FIXTURE_DEF.shape = pShape;
		b2dComponent.body.createFixture(Main.FIXTURE_DEF);
		pShape.dispose();	
		
		player.add(b2dComponent);
		
		//Thành phần Animation
		final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
		animationComponent.aniType = AnimationType.HERO_DOWN_WALK;
		animationComponent.width = 64 * UNIT_SCALE;
		animationComponent.height = 64 * UNIT_SCALE;
		player.add(animationComponent);
		
		this.addEntity(player);
	}

	public void createGameObject(final GameObject gameObj) {
		final Entity gameObjEntity = this.createEntity();
		
		//Thành phần GameObject
		final GameObjectComponent gameObjComponent = this.createComponent(GameObjectComponent.class);
		gameObjComponent.animationIndex = gameObj.getAnimationIndex();
		gameObjComponent.type = gameObj.getType();
		gameObjEntity.add(gameObjComponent);
		
		//Thành phần Animation
		final AnimationComponent animationComponent = this.createComponent(AnimationComponent.class);
		animationComponent.aniType = null;
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
		b2dComponent.body = world.createBody(Main.BODY_DEF);
		b2dComponent.body.setUserData("GAMEOBJECT");
		b2dComponent.width = gameObj.getWidth();
		b2dComponent.height = gameObj.getHeight();
		
		localPosition.set(-halfWidth, -halfHeight);
		posBeforeRotation.set(b2dComponent.body.getWorldPoint(localPosition));
		b2dComponent.body.setTransform(b2dComponent.body.getPosition(), radianAngle);
		posAfterRotation.set(b2dComponent.body.getWorldPoint(localPosition));
		b2dComponent.body.setTransform(b2dComponent.body.getPosition().add(posBeforeRotation).sub(posAfterRotation), radianAngle);
		b2dComponent.renderPosition.set(b2dComponent.body.getPosition().x - animationComponent.width * 0.5f,
										b2dComponent.body.getPosition().y - animationComponent.height * 0.5f);
		
		Main.FIXTURE_DEF.filter.categoryBits = BIT_GAME_OBJECT;
		Main.FIXTURE_DEF.filter.maskBits = BIT_PLAYER;
		final PolygonShape pShape = new PolygonShape();
		pShape.setAsBox(halfWidth, halfHeight);
		Main.FIXTURE_DEF.shape = pShape;
		b2dComponent.body.createFixture(Main.FIXTURE_DEF);
		pShape.dispose();
		gameObjEntity.add(b2dComponent);
		
		this.addEntity(gameObjEntity);
	}
}
