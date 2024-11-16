package com.hellish.ecs.component;

import java.util.HashSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ai.AiEntity;

public class AiComponent implements Component, Poolable{
	public HashSet<Entity> nearbyEntities;
	public String treePath;
	public BehaviorTree<AiEntity> behaviorTree;
	public Entity target;
	
	public AiComponent() {
		nearbyEntities = new HashSet<>();
		treePath = "";
		
	}

	@Override
	public void reset() {
		nearbyEntities.clear();
		treePath = "";
		behaviorTree = null;
		
	}
	
	public static class AiComponentListener implements ComponentListener<AiComponent> {
		private BehaviorTreeParser<AiEntity> bTreeParser = new BehaviorTreeParser<AiEntity>();
		@Override
		public void onComponentAdded(Entity entity, AiComponent component, Stage stage) {
			if(component.treePath != null && !component.treePath.isEmpty()) {
				component.behaviorTree = bTreeParser.parse(
					Gdx.files.internal(component.treePath), 
					new AiEntity(entity, stage)
				);
			}
		}

		@Override
		public void onComponentRemoved(Entity entity, AiComponent component) {
			
		}
	}
}
