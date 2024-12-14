package com.hellish.event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;
import com.hellish.ecs.component.AnimationComponent;

public class EntityAttackEvent extends Event{
	public static final Pool<EntityAttackEvent> pool = new Pool<EntityAttackEvent>() {
        @Override
        protected EntityAttackEvent newObject() {
            return new EntityAttackEvent();
        }
    };
    
	private Entity entity;
	
	public EntityAttackEvent set(Entity entity) {
        this.entity = entity;
        return this;
    }

    public Entity getEntity() {
		return entity;
	}
	
	public String getModel() {
		AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
		if (animationComponent != null) {
            if (animationComponent.getModel() != null) {
                return animationComponent.getModel();  // Trả về mô hình dưới dạng chuỗi
            }
        }
        return "No model found";  // Trả về thông báo nếu không tìm thấy model
    }
	
	@Override
	public void reset() {
		super.reset();
		entity = null;
	}
}
