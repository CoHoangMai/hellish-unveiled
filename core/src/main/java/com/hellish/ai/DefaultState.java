package com.hellish.ai;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.hellish.ecs.component.AnimationComponent.AnimationType;

public enum DefaultState implements EntityState{
	IDLE{
		@Override
		public void enter(StateEntity stateEntity) {
			stateEntity.enableGlobalState(true);
			stateEntity.animation(AnimationType.IDLE);
		}
		
		@Override
		public void update(StateEntity stateEntity) {
			if(stateEntity.wantsToAttack()) {
				stateEntity.state(ATTACK);
			} else if (stateEntity.wantsToMove()) {
				stateEntity.state(WALK);
			}
		}
	},
	
	WALK{
		@Override
		public void enter(StateEntity stateEntity) {
			stateEntity.animation(AnimationType.WALK);
		}
		
		@Override
		public void update(StateEntity stateEntity) {
			if(stateEntity.wantsToAttack()) {
				stateEntity.state(ATTACK);
			} else if (!stateEntity.wantsToMove()) {
				stateEntity.state(IDLE);
			}
		}
	}, 
	
	ATTACK{
		@Override
		public void enter(StateEntity stateEntity) {
			stateEntity.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL);
			stateEntity.root(true);
			stateEntity.startAttack();
		}
		
		@Override
		public void update(StateEntity stateEntity) {
			if(stateEntity.attackCmp().isReady() && !stateEntity.attackCmp().doAttack) {
				stateEntity.changeToPreviousState();
			} else if (stateEntity.attackCmp().isReady()) {
				stateEntity.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL, true);
				stateEntity.startAttack();
			}
		}
		
		@Override
		public void exit(StateEntity stateEntity) {
			stateEntity.root(false);
		}
	}, 
	
	DIE{
		@Override
		public void enter(StateEntity stateEntity) {
			stateEntity.root(true);
			
		}
		
		@Override
		public void update(StateEntity stateEntity) {
			if(!stateEntity.isDead()) {
				stateEntity.state(RESSURECT);
			}
		}
	}, 
	
	RESSURECT{
		@Override
		public void enter(StateEntity stateEntity) {
			stateEntity.enableGlobalState(true);
			stateEntity.animation(AnimationType.DIE, PlayMode.REVERSED, true);
		}
		
		@Override
		public void update(StateEntity stateEntity) {
			if(stateEntity.isAnimationFinished()) {
				stateEntity.state(IDLE);
				stateEntity.root(false);
			}
		}
	};

	@Override
	public void enter(StateEntity stateEntity) {
		
	}

	@Override
	public void update(StateEntity stateEntity) {
		
	}

	@Override
	public void exit(StateEntity stateEntity) {
		
	}
}
