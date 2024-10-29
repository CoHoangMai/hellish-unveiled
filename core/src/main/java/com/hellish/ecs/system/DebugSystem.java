package com.hellish.ecs.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;

public class DebugSystem extends EntitySystem{
	private final World world;
	private final Stage stage;
	private Box2DDebugRenderer physicRenderer;
	private final GLProfiler profiler;
	
	public DebugSystem(final Main context) {
		stage = context.getStage();
		world = context.getWorld();
		profiler = new GLProfiler(Gdx.graphics);
		profiler.enable();
		if(profiler.isEnabled()) {
			physicRenderer = new Box2DDebugRenderer();
		}
	}
	
	@Override
	public void update(float deltaTime) {
		physicRenderer.render(world, stage.getCamera().combined);
	}
	
	public void dispose() {
		if(profiler.isEnabled()) {
			physicRenderer.dispose();
		}
	}
}
