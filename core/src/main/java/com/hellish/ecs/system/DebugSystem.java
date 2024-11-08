package com.hellish.ecs.system;

import static com.hellish.ecs.system.AttackSystem.AABB_RECT;
import static com.hellish.ai.AiEntity.TMP_RECT1;
import static com.hellish.ai.AiEntity.TMP_RECT2;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.hellish.Main;

public class DebugSystem extends EntitySystem{
	private final World world;
	private final Stage gameStage;
	private Box2DDebugRenderer physicsRenderer;
	private ShapeRenderer shapeRenderer;
	private final GLProfiler profiler;
	
	public DebugSystem(final Main context) {
		gameStage = context.getGameStage();
		world = context.getWorld();
		profiler = new GLProfiler(Gdx.graphics);
		profiler.enable();
		if(profiler.isEnabled()) {
			physicsRenderer = new Box2DDebugRenderer();
			shapeRenderer = new ShapeRenderer();
		}
	}
	
	@Override
	public void update(float deltaTime) {
		physicsRenderer.render(world, gameStage.getCamera().combined);
		
		shapeRenderer.setProjectionMatrix(gameStage.getCamera().combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		
		shapeRenderer.setColor(0, 1, 0, 0);
		shapeRenderer.rect(AABB_RECT.x, AABB_RECT.y, AABB_RECT.width - AABB_RECT.x, AABB_RECT.height - AABB_RECT.y);
		shapeRenderer.setColor(1, 0, 0, 0);
		shapeRenderer.rect(TMP_RECT1.x, TMP_RECT1.y, TMP_RECT1.width, TMP_RECT1.height);
		shapeRenderer.setColor(0, 0, 1, 0);
		shapeRenderer.rect(TMP_RECT2.x, TMP_RECT2.y, TMP_RECT2.width, TMP_RECT2.height);
		
		shapeRenderer.end();
	}
	
	public void dispose() {
		if(profiler.isEnabled()) {
			physicsRenderer.dispose();
			shapeRenderer.dispose();
		}
	}
}
