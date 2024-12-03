package com.hellish.ecs.system;

import static com.hellish.ai.AiEntity.TMP_RECT1;
import static com.hellish.ai.AiEntity.TMP_RECT2;
import static com.hellish.ecs.system.AttackSystem.AABB_RECT;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.ai.AiEntity;
import com.hellish.ai.steer.steerer.RaycastObstacleAvoidanceSteererBase;

public class DebugSystem extends EntitySystem{
	private final World world;
	private final Stage gameStage;
	private Box2DDebugRenderer physicsRenderer;
	private ShapeRenderer shapeRenderer;
	private final GLProfiler profiler;
	
	public static final Array<AiEntity> aiEntities = new Array<AiEntity>();
	
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
		
		 for (AiEntity aiEntity : aiEntities) {
	            // Vẽ raycast từ wanderSteerer và pursueSteerer
	            drawRay(aiEntity.wanderSteerer);
	            drawRay(aiEntity.pursueSteerer);
	        }
		
		shapeRenderer.end();
	}
	
	 private void drawRay(RaycastObstacleAvoidanceSteererBase steerer) {
	        CentralRayWithWhiskersConfiguration<Vector2> rayConfig = steerer.getRayConfiguration();
	        Ray<Vector2>[] rays = rayConfig.getRays(); // Giả sử phương thức này trả về các ray
	        
	        for (Ray<Vector2> ray : rays) {
	            Vector2 start = ray.start;
	            Vector2 end = ray.end;

	            shapeRenderer.setColor(1, 0, 0, 1);  // Màu đỏ cho raycast
	            shapeRenderer.line(start, end);  // Vẽ đường ray
	        }
	    }
	
	public void dispose() {
		if(profiler.isEnabled()) {
			physicsRenderer.dispose();
			shapeRenderer.dispose();
		}
	}
}
