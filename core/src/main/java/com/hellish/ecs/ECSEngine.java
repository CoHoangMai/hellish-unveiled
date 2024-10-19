package com.hellish.ecs;

import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.Main.BIT_GROUND;
import static com.hellish.Main.BIT_PLAYER;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.Main;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.Box2DComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.system.AnimationSystem;
import com.hellish.ecs.system.PlayerAnimationSystem;
import com.hellish.ecs.system.PlayerCameraSystem;
import com.hellish.ecs.system.PlayerMovementSystem;
import com.hellish.view.AnimationType;

public class ECSEngine extends PooledEngine{
	public static final ComponentMapper<PlayerComponent> playerCmpMapper = ComponentMapper.getFor(PlayerComponent.class);
	public static final ComponentMapper<Box2DComponent> b2dCmpMapper = ComponentMapper.getFor(Box2DComponent.class);
	public static final ComponentMapper<AnimationComponent> aniCmpMapper = ComponentMapper.getFor(AnimationComponent.class);
	
	private final World world;
	private final BodyDef bodyDef;
	private final FixtureDef fixtureDef;
	
	public ECSEngine(final Main context) {
		super();
		
		world = context.getWorld();
		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		
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
		Main.resetBodiesAndFixtureDefinition();
		final Box2DComponent b2dComponent = this.createComponent(Box2DComponent.class);
		bodyDef.position.set(playerSpawnLocation.x, playerSpawnLocation.y + 0.5f);
		bodyDef.fixedRotation = true;
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		b2dComponent.body = world.createBody(bodyDef);
		b2dComponent.body.setUserData("PLAYER");
		b2dComponent.width = width;
		b2dComponent.height = height;
		b2dComponent.renderPosition.set(b2dComponent.body.getPosition());
				
		fixtureDef.filter.categoryBits = BIT_PLAYER;
		fixtureDef.filter.maskBits = BIT_GROUND;
		final PolygonShape pShape = new PolygonShape();
		pShape.setAsBox(0.5f, 0.5f);
		fixtureDef.shape = pShape;
		b2dComponent.body.createFixture(fixtureDef);
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
}
