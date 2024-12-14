package com.hellish.ecs.system;
	
import static com.hellish.Main.UNIT_SCALE;
import static com.hellish.ecs.system.EntitySpawnSystem.HIT_BOX_SENSOR;
	
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.hellish.Main;
import com.hellish.actor.FlipImage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.AttackComponent.AttackState;
import com.hellish.ecs.component.FireComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.LootComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.ecs.component.AnimationComponent.AnimationModel;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.ecs.component.PhysicsComponent.Direction;
	
public class AttackSystem extends IteratingSystem{
	public static final Rectangle AABB_RECT = new Rectangle();
	
	private final World world;
	private final AssetManager assetManager;
	
	public AttackSystem(final Main context) {
		super(Family.all(AttackComponent.class, PhysicsComponent.class, ImageComponent.class).get());
		
		world = context.getWorld();
		assetManager = context.getAssetManager();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final AttackComponent attackCmp = ECSEngine.attackCmpMapper.get(entity);
		final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
		final ImageComponent imageCmp = ECSEngine.imageCmpMapper.get(entity);
	
		Direction attackDirection = aniCmp.realDirection;
		
		if (attackCmp.isReady() && !attackCmp.doAttack) {
			return;
		} 

		if (attackCmp.isPrepared() && attackCmp.doAttack) {
			attackCmp.doAttack = false;
			attackCmp.setState(AttackState.ATTACKING);
			attackCmp.delay = attackCmp.maxDelay;
			return;
		} 
		
		attackCmp.delay -= deltaTime;
		if(attackCmp.delay <= 0 && attackCmp.isAttacking()) {
			attackCmp.setState(AttackState.DEAL_DAMAGE);
			attackDirection = aniCmp.realDirection;
			
			if(attackCmp.canFire) {
				fire(imageCmp.image.isFlipX(), physicsCmp.body.getPosition(), physicsCmp.size);
			}
			
			float x = physicsCmp.body.getPosition().x;
			float y = physicsCmp.body.getPosition().y;
			float w = physicsCmp.size.x;
			float h = physicsCmp.size.y;
			float halfW = w * 0.5f;
			float halfH = h * 0.5f;
			
			//Chiều rộng và chiều cao của AABB_RECT thực chất là tọa độ góc trên phải của
			//hình chữ nhật ta mong muốn. Khi truyền chúng vào QueryAABB thì vẫn chính xác
			//vì QueryAABB lấy 2 thông số cuối đó là tọa độ góc trên phải.
			switch(attackDirection) {
				case RIGHT:
					AABB_RECT.set(
						x,
						y - halfH,
						x + halfW + attackCmp.extraRange,
						y + halfH
					);
					break;
				case LEFT:
					AABB_RECT.set(
						x - halfW  - attackCmp.extraRange,
						y - halfH,
						x,
						y + halfH	
					);
					break;
				case UP:
					AABB_RECT.set(
						x - halfH,
						y,
						x + halfH,
						y + halfW  + attackCmp.extraRange
					);
					break;
				case DOWN:
					AABB_RECT.set(
						x - halfH,
						y - halfW - attackCmp.extraRange,
						x + halfH,
						y	
					);
					break;
			}
			
			world.QueryAABB(fixture -> {
				if(fixture.getUserData() != HIT_BOX_SENSOR) {
					return true;
				}
				
				Entity fixtureEntity = (Entity) fixture.getBody().getUserData();
				if(fixtureEntity == entity) {
					return true;
				}
				
				boolean isAttackerPlayer = ECSEngine.playerCmpMapper.has(entity);
				if (isAttackerPlayer && ECSEngine.playerCmpMapper.has(fixtureEntity)) {
					return true;
				} else if (!isAttackerPlayer && !ECSEngine.playerCmpMapper.has(fixtureEntity)) {
					return true;
				}
				
				final LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(fixtureEntity);
				if (lifeCmp != null) {
					lifeCmp.takeDamage += attackCmp.damage * MathUtils.random(0.9f, 1.1f);
					if(ECSEngine.playerCmpMapper.has(fixtureEntity)) {
						System.out.println("Bị cắn mất " + (int)lifeCmp.takeDamage + " miếng HP!");
					}
				}
				
				if (isAttackerPlayer) {
					final LootComponent lootCmp = ECSEngine.lootCmpMapper.get(fixtureEntity);
					if(lootCmp != null) {
						lootCmp.interactEntity = entity;
					}
					return true;
				}
				return true;
			}, AABB_RECT.x, AABB_RECT.y, AABB_RECT.width, AABB_RECT.height);
		}
		
		if(aniCmp != null && aniCmp.isAnimationFinished() && attackCmp.getState() == AttackState.DEAL_DAMAGE) {
			attackCmp.setState(AttackState.READY);
		}
	}
	
	private void fire(boolean imageFacingRight, Vector2 entityPosition, Vector2 entitySize) {
		final Entity fireEntity = getEngine().createEntity();
		
		//Image
		final ImageComponent imageCmp = getEngine().createComponent(ImageComponent.class);
		imageCmp.image = new FlipImage();
		
		final TextureAtlas atlas = assetManager.get("characters_and_effects/gameObjects.atlas", TextureAtlas.class);
		Array<AtlasRegion> regions = atlas.findRegions("fire/fire");
		if (regions.size == 0) {
			throw new RuntimeException("Không có texture region cho " + AnimationModel.FIRE);
		}
		AtlasRegion firstFrame = regions.first();
		
		float relativeSizeX = firstFrame.originalWidth * UNIT_SCALE;
		float relativeSizeY = firstFrame.originalHeight * UNIT_SCALE;
		
		imageCmp.image.setSize(relativeSizeX, relativeSizeY);
		
		imageCmp.image.setFlipX(imageFacingRight ? false : true);
		
		if(imageFacingRight) {
			imageCmp.image.setPosition(entityPosition.x, entityPosition.y - entitySize.y * 0.75f);
		} else {
			imageCmp.image.setPosition(entityPosition.x - relativeSizeX, entityPosition.y - entitySize.y * 0.75f);
		}
		imageCmp.image.setScaling(Scaling.fill);
		
		fireEntity.add(imageCmp);
		
		//Animation
		final AnimationComponent aniCmp = getEngine().createComponent(AnimationComponent.class);
		aniCmp.mode = Animation.PlayMode.NORMAL;
		aniCmp.nextAnimation(AnimationModel.FIRE, AnimationType.UNSPECIFIED);
		fireEntity.add(aniCmp);
		
		//Fire
		final FireComponent fireCmp = getEngine().createComponent(FireComponent.class);
		fireEntity.add(fireCmp);
		
		getEngine().addEntity(fireEntity);
	}
}
