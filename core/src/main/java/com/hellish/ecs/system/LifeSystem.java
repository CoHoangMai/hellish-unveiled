package com.hellish.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AnimationComponent;
import com.hellish.ecs.component.AnimationComponent.AnimationType;
import com.hellish.ecs.component.DeadComponent;
import com.hellish.ecs.component.FloatingTextComponent;
import com.hellish.ecs.component.LifeComponent;
import com.hellish.ecs.component.PhysicsComponent;

public class LifeSystem extends IteratingSystem implements Disposable{
	private final BitmapFont damageFont;
	private final Label.LabelStyle floatTxtFntStyle;
	
	public LifeSystem() {
		super(Family.all(LifeComponent.class, PhysicsComponent.class).exclude(DeadComponent.class).get());
		
		this.damageFont = new BitmapFont(Gdx.files.internal("ui/damage.fnt"));
		damageFont.getData().setScale(0.33f);
		this.floatTxtFntStyle = new Label.LabelStyle(damageFont, Color.WHITE);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		final LifeComponent lifeCmp = ECSEngine.lifeCmpMapper.get(entity);
		lifeCmp.life = Math.min(lifeCmp.life + lifeCmp.regeneration * deltaTime, lifeCmp.max);
		
		if (lifeCmp.takeDamage > 0) {
			final PhysicsComponent physicsCmp = ECSEngine.physicsCmpMapper.get(entity);
			lifeCmp.life -= (int)lifeCmp.takeDamage;
			floatingText(Integer.toString((int) lifeCmp.takeDamage), physicsCmp.body.getPosition(), physicsCmp.size);
			lifeCmp.takeDamage = 0;
		}
		if (lifeCmp.isDead()) {
			final AnimationComponent aniCmp = ECSEngine.aniCmpMapper.get(entity);
			if(aniCmp != null) {
				aniCmp.nextAnimation(AnimationType.DIE);
				aniCmp.mode = PlayMode.NORMAL;
			}
			final DeadComponent deadCmp = new DeadComponent();
			entity.add(deadCmp);
			if (ECSEngine.playerCmpMapper.has(entity)) {
				deadCmp.reviveTime = 7f;
				System.out.println("Gục ngã vì chó cắn quá đau.....");
			} else {
				System.out.println("Bay màu con chó");
			}
		}
	}
	
	private void floatingText(String text, Vector2 entityPosition, Vector2 entitySize) {
		final FloatingTextComponent floatTxtCmp = new FloatingTextComponent();
		floatTxtCmp.txtLocation.set(entityPosition.x, entityPosition.y - entitySize.y * 0.5f);
		floatTxtCmp.lifeSpan = 0.75f;
		floatTxtCmp.label = new Label(text, floatTxtFntStyle);
		
		getEngine().addEntity(new Entity().add(floatTxtCmp));
	}
	
	@Override
	public void dispose() {
		damageFont.dispose();
	}
}
