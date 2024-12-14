	package com.hellish.ecs.system;
	
	import com.badlogic.ashley.core.Entity;
	import com.badlogic.ashley.core.Family;
	import com.badlogic.ashley.systems.IteratingSystem;
	import com.badlogic.gdx.math.MathUtils;
	import com.badlogic.gdx.math.Rectangle;
	import com.badlogic.gdx.physics.box2d.World;
	import com.badlogic.gdx.scenes.scene2d.Stage;
	import com.hellish.Main;
	import com.hellish.ecs.ECSEngine;
	import com.hellish.ecs.component.AnimationComponent;
	import com.hellish.ecs.component.AttackComponent;
	import com.hellish.ecs.component.AttackComponent.AttackState;
	import com.hellish.ecs.component.ImageComponent;
	import com.hellish.ecs.component.LifeComponent;
	import com.hellish.ecs.component.LootComponent;
	import com.hellish.ecs.component.PhysicsComponent;
	import com.hellish.ecs.component.PhysicsComponent.Direction;
	import static com.hellish.ecs.system.EntitySpawnSystem.HIT_BOX_SENSOR;
	import com.hellish.event.EntityAttackEvent;
	
	public class AttackSystem extends IteratingSystem{
		public static final Rectangle AABB_RECT = new Rectangle();
		private final Stage stage;
		private World world;
		
		public AttackSystem(final Main context) {
			super(Family.all(AttackComponent.class, PhysicsComponent.class, ImageComponent.class).get());
			
			world = context.getWorld();
			stage = context.getGameStage();
		}
	
		@Override
		protected void processEntity(Entity entity, float deltaTime) {
			final AttackComponent attackCmp = ECSEngine.attackCmpMapper.get(entity);
			
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
				EntityAttackEvent event = EntityAttackEvent.pool.obtain().set(entity);
				stage.getRoot().fire(event);
				EntityAttackEvent.pool.free(event);

				final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
				Direction attackDirection = physicsCmp.direction;
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
						} else {
							System.out.println("Phệt trúng con chó!");
						}
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
			if(aniCmp != null && aniCmp.isAnimationFinished() && attackCmp.getState() == AttackState.DEAL_DAMAGE) {
				attackCmp.setState(AttackState.READY);
			}
		}
	}
