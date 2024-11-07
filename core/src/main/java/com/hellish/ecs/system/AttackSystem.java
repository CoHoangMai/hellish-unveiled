package com.hellish.ecs.system;

import static com.hellish.ecs.system.EntitySpawnSystem.HIT_BOX_SENSOR;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.hellish.Main;
import com.hellish.actor.FlipImage;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.AttackComponent.AttackState;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.LootComponent;
import com.hellish.ecs.component.PhysicsComponent;

public class AttackSystem extends IteratingSystem{
	public static final Rectangle AABB_RECT = new Rectangle();
	
	private World world;
	
	public AttackSystem(final Main context) {
		super(Family.all(AttackComponent.class, PhysicsComponent.class, ImageComponent.class).get());
		
		world = context.getWorld();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final AttackComponent attackCmp = ECSEngine.attackCmpMapper.get(entity);
		
		if (attackCmp.isReady() && !attackCmp.doAttack) {
			return;
		}
		if (attackCmp.isPrepared() && attackCmp.doAttack) {
			attackCmp.doAttack = false;
			attackCmp.state = AttackState.ATTACKING;
			attackCmp.delay = attackCmp.maxDelay;
			return;
		}
		attackCmp.delay -= deltaTime;
		if(attackCmp.delay <= 0 && attackCmp.isAttacking()) {
			System.out.println("PHỆT");
			attackCmp.state = AttackState.DEAL_DAMAGE;
			
			final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
			final FlipImage image = ECSEngine.imageCmpMapper.get(entity).image;
			boolean attackRight = image.isFlipX();
			float x = physicsCmp.body.getPosition().x;
			float y = physicsCmp.body.getPosition().y;
			float offX = physicsCmp.offset.x;
			float offY = physicsCmp.offset.y;
			float w = physicsCmp.size.x;
			float h = physicsCmp.size.y;
			float halfW = w * 0.5f;
			float halfH = h * 0.5f;
			
			if(attackRight) {
				AABB_RECT.set(
					offX + x - halfW,
					offY + y - halfH,
					offX + x + halfW + attackCmp.extraRange,
					offY + y + halfH
				);
			} else {
				AABB_RECT.set(
					offX + x - halfW  - attackCmp.extraRange,
					offY + y - halfH,
					offX + x + halfW,
					offY + y + halfH	
				);
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
					System.out.println("Phệt trúng!");
				}
				
				if (isAttackerPlayer) {
					final LootComponent lootCmp = ECSEngine.lootCmpMapper.get(fixtureEntity);
					if(lootCmp != null) {
						lootCmp.interactEntity = entity;
						System.out.println("Mở rương");
					}
					
					return true;
				}
				return true;
			}, AABB_RECT.x, AABB_RECT.y, AABB_RECT.width, AABB_RECT.height);
		}
		
		final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
		if(aniCmp != null && aniCmp.isAnimationFinished()) {
			attackCmp.state = AttackState.READY;
		}
	}
}
