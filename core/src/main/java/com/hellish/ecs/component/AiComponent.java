package com.hellish.ecs.component;

import java.util.HashSet;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hellish.ai.AiEntity;
import com.hellish.ecs.system.DebugSystem;

public class AiComponent implements Component, Poolable{
	public HashSet<Entity> nearbyEntities;
	public String treePath;
	public BehaviorTree<AiEntity> behaviorTree;
	public Entity target;
	
	public AiComponent() {
		nearbyEntities = new HashSet<>();
		treePath = "";
		behaviorTree = null;
		target = null;
	}

	@Override
	public void reset() {
		nearbyEntities.clear();
		treePath = "";
		behaviorTree = null;
		target = null;
	}
	
	public static class AiComponentListener implements ComponentListener<AiComponent> {
		private BehaviorTreeParser<AiEntity> bTreeParser = new BehaviorTreeParser<AiEntity>();
		@Override
		public void onComponentAdded(Entity entity, AiComponent component, Stage stage, World world) {
			if(component.treePath != null && !component.treePath.isEmpty()) {
				AiEntity aiEntity = new AiEntity(entity, stage, world);
				component.behaviorTree = bTreeParser.parse(
					Gdx.files.internal(component.treePath), 
					aiEntity
				);
				DebugSystem.aiEntities.add(aiEntity);
			}
		}

		@Override
		public void onComponentRemoved(Entity entity, AiComponent component) {
			
		}
	}
}
