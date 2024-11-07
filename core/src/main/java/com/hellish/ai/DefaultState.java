package com.hellish.ai;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.hellish.ecs.component.AnimationComponent.AnimationType;

public enum DefaultState implements EntityState{
	IDLE{
		@Override
		public void enter(AiEntity aiEntity) {
			aiEntity.enableGlobalState(true);
			aiEntity.animation(AnimationType.IDLE);
		}
		
		@Override
		public void update(AiEntity aiEntity) {
			if(aiEntity.wantsToAttack()) {
				aiEntity.state(ATTACK);
			} else if (aiEntity.wantsToMove()) {
				aiEntity.state(WALK);
			}
		}
	},
	
	WALK{
		@Override
		public void enter(AiEntity aiEntity) {
			aiEntity.animation(AnimationType.SIDE_WALK);
		}
		
		@Override
		public void update(AiEntity aiEntity) {
			if(aiEntity.wantsToAttack()) {
				aiEntity.state(ATTACK);
			} else if (!aiEntity.wantsToMove()) {
				aiEntity.state(IDLE);
			}
		}
	}, 
	
	ATTACK{
		@Override
		public void enter(AiEntity aiEntity) {
			aiEntity.animation(AnimationType.SIDE_ATTACK, Animation.PlayMode.NORMAL);
			aiEntity.root(true);
			aiEntity.startAttack();
		}
		
		@Override
		public void update(AiEntity aiEntity) {
			if(aiEntity.attackCmp().isReady() && !aiEntity.attackCmp().doAttack) {
				aiEntity.changeToPreviousState();
			} else if (aiEntity.attackCmp().isReady()) {
				aiEntity.animation(AnimationType.SIDE_ATTACK, Animation.PlayMode.NORMAL, true);
				aiEntity.startAttack();
			}
		}
		
		@Override
		public void exit(AiEntity aiEntity) {
			aiEntity.root(false);
		}
	}, 
	
	DIE{
		@Override
		public void enter(AiEntity aiEntity) {
			aiEntity.root(true);
			
		}
		
		@Override
		public void update(AiEntity aiEntity) {
			if(!aiEntity.isDead()) {
				aiEntity.state(RESSURECT);
			}
		}
	}, 
	
	RESSURECT{
		@Override
		public void enter(AiEntity aiEntity) {
			aiEntity.enableGlobalState(true);
			aiEntity.animation(AnimationType.DIE, PlayMode.REVERSED, true);
		}
		
		@Override
		public void update(AiEntity aiEntity) {
			if(aiEntity.isAnimationFinished()) {
				aiEntity.state(IDLE);
				aiEntity.root(false);
			}
		}
	};

	@Override
	public void enter(AiEntity aiEntity) {
		
	}

	@Override
	public void update(AiEntity aiEntity) {
		
	}

	@Override
	public void exit(AiEntity aiEntity) {
		
	}
}
