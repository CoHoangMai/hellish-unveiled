package com.hellish.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.ai.steer.steerer.PursueSteerer;
import com.hellish.ai.steer.steerer.WanderSteerer;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AiComponent;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PhysicsComponent;

//Loại dành cho các nhân vật khác ngoài con HUST
public class AiEntity {
	public static final Rectangle TMP_RECT1 = new Rectangle();
	public static final Rectangle TMP_RECT2 = new Rectangle();

	public final Entity entity;
	
	private final Stage stage;
	
	private final AnimationComponent aniCmp;
	private final ImageComponent imageCmp;
	private final PhysicsComponent physicsCmp;
	private final MoveComponent moveCmp;
	private final AttackComponent attackCmp;

	private final AiComponent aiCmp;
	
	public final WanderSteerer wanderSteerer;
	public final PursueSteerer seekSteerer;
	
	public AiEntity(Entity entity, Stage stage, World world) {
		this.entity = entity;
		this.stage = stage;
		
		physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
		moveCmp = ECSEngine.moveCmpMapper.get(entity);
		attackCmp = ECSEngine.attackCmpMapper.get(entity);
		aniCmp = ECSEngine.aniCmpMapper.get(entity);
		imageCmp = ECSEngine.imageCmpMapper.get(entity);
		aiCmp = ECSEngine.aiCmpMapper.get(entity);
		
		wanderSteerer = new WanderSteerer(physicsCmp, world);
		seekSteerer = new PursueSteerer(physicsCmp, world);
	}
	
	public boolean isAnimationFinished() {
		return aniCmp != null && aniCmp.isAnimationFinished();
	}
	
	public void animation(AnimationType type, PlayMode mode, boolean resetAnimation) {
		if(aniCmp != null) {
			aniCmp.nextAnimation(type);
			aniCmp.mode = mode;
			if(resetAnimation) {
				aniCmp.aniTime = 0;
			}
		}
	}
	public void animation(AnimationType type) {
		animation(type, PlayMode.LOOP, false);
	}
	public void animation(AnimationType type, PlayMode mode) {
		animation(type, mode, false);
	}
	
	
	public void startAttack() {
		attackCmp.doAttack = true;
		attackCmp.startAttack();
	}
	
	public Vector2 getPosition() {
		return physicsCmp.body.getPosition();
	}

	public boolean inRange(float range, Vector2 target) {
		float sourceX = getPosition().x, sourceY = getPosition().y;
		float sizeX = physicsCmp.size.x, sizeY = physicsCmp.size.y;
		sizeX += range;
		sizeY += range;
		
		TMP_RECT1.set(
			sourceX - sizeX * 0.5f,
			sourceY - sizeY * 0.5f,
			sizeX,
			sizeY
		);
	
		return TMP_RECT1.contains(target);
	}
	
	public boolean inTargetRange(float range) {
		if(aiCmp.target == null) {
			return true;
		}
		float sourceX = getPosition().x, sourceY = getPosition().y;
		float sizeX = physicsCmp.size.x, sizeY = physicsCmp.size.y;
		sizeX += range;
		sizeY += range;
		
		boolean facingRight = imageCmp.image.isFlipX();
		if(facingRight) {
			TMP_RECT1.set(
				sourceX,
				sourceY - sizeY * 0.5f,
				sizeX * 0.5f + attackCmp.extraRange,
				sizeY
			);
		} else {
			TMP_RECT1.set(
					sourceX - sizeX * 0.5f  - attackCmp.extraRange,
				sourceY - sizeY * 0.5f,
				sizeX * 0.5f + attackCmp.extraRange,
				sizeY	
			);
		}
		
		PhysicsComponent targetPhysicsCmp = ECSEngine.physicsCmpMapper.get(aiCmp.target);
		float targetX = targetPhysicsCmp.body.getPosition().x, targetY = targetPhysicsCmp.body.getPosition().y;
		float targetSizeX = targetPhysicsCmp.size.x, targetSizeY = targetPhysicsCmp.size.y;
		TMP_RECT2.set(
			targetX - targetSizeX * 0.5f,
			targetY - targetSizeY * 0.5f,
			targetSizeX,
			targetSizeY
		);
	
		return TMP_RECT1.overlaps(TMP_RECT2);
	}
	
	public void moveToTarget() {
		if(aiCmp.target == null) {
			moveCmp.cosine = 0;
			moveCmp.sine = 0;
			return;
		}
		seekSteerer.setTarget(ECSEngine.physicsCmpMapper.get(aiCmp.target));
		seekSteerer.startPursuing();
	}

	public void stopMovement() {
		moveCmp.cosine = 0;
		moveCmp.sine = 0;
	}

	public void moveSlowly(boolean slowed) {
		moveCmp.slow = slowed;
	}

	public boolean hasNearbyEnemy() {
		aiCmp.target = aiCmp.nearbyEntities.stream()
			.filter(nearbyEntity -> ECSEngine.playerCmpMapper.has(nearbyEntity) 
				&& !ECSEngine.lifeCmpMapper.get(nearbyEntity).isDead())
			.findFirst()
			.orElse(null);
		return aiCmp.target != null;
	}
	
	public void checkTargetStillNearby() {
		if(!aiCmp.nearbyEntities.contains(aiCmp.target)) {
			aiCmp.target = null;
			seekSteerer.stopPursuing();
		}
	}

	public boolean canAttack(float range) {
		if(aiCmp.target == null) {
			return false;
		}
		return attackCmp.isReady() && inTargetRange(range);
	}

	public void fireEvent(Event event) {
		stage.getRoot().fire(event);
	}
}