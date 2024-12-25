package com.hellish.screen;

import java.util.Arrays;
import java.util.HashSet;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.audio.AudioType;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.ecs.system.CameraSystem;
import com.hellish.ecs.system.DebugSystem;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.event.GamePauseEvent;
import com.hellish.event.GameResumeEvent;
import com.hellish.event.LoseEvent;
import com.hellish.event.WinEvent;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.map.MapManager;
import com.hellish.map.MapType;
import com.hellish.ui.Scene2DSkin;
import com.hellish.ui.model.GameModel;
import com.hellish.ui.model.InventoryModel;
import com.hellish.ui.view.GameView;
import com.hellish.ui.view.InventoryView;
import com.hellish.ui.view.LoseView;
import com.hellish.ui.view.PauseView;
import com.hellish.ui.view.WinView;

public class GameScreen extends AbstractScreen<Table> implements EventListener{
	private final MapManager mapManager;
	private final AssetManager assetManager;
	private final ECSEngine ecsEngine;
	public boolean paused;
	
	private float playerSin;
	private float playerCos;
	private Vector2 directionVector;
	
	private final HashSet<Class<?>> mandatorySystems;
	
	public GameScreen(final Main context) {
		super(context);
		
		assetManager = context.getAssetManager();
		
		ecsEngine = context.getECSEngine();
		for(EntitySystem system : ecsEngine.getSystems()) {
			system.setProcessing(true);
		}
		
		mandatorySystems = new HashSet<>(Arrays.asList(
			CameraSystem.class,
			RenderSystem.class,
			DebugSystem.class
		));
		
		context.getGameStage().addListener(this);
		
		mapManager = context.getMapManager();
		mapManager.setMap(MapType.MAP_2);
		
		playerSin = 0f;
		playerCos = 0f;
		directionVector = new Vector2();

		paused = false;
		
		for (final AudioType audioType : AudioType.values()) {
		    if (audioType.isMusic()) {
		        assetManager.load(audioType.getFilePath(), Music.class);
		    } else {
		        assetManager.load(audioType.getFilePath(), Sound.class);
		    }
		}
	}
	
	private void pauseWorld(boolean pause) {
		for(EntitySystem system : ecsEngine.getSystems()) {
			if(!mandatorySystems.contains(system.getClass())) {
				system.setProcessing(!pause);
			}
		}
	}

	@Override
	public void render(float delta) {
		// if(!isMusicLoaded && assetManager.isLoaded(AudioType.GAME.getFilePath())) {
		// 	isMusicLoaded = true;
		// 	audioManager.playAudio(AudioType.GAME);
		// }
	}

	@Override
	public void pause() {
		pauseWorld(true);
	}

	@Override
	public void resume() {
		pauseWorld(false);
	}
	
	public void showPauseView(boolean show) {
		for(Actor actor : uiStage.getActors()) {
			if(actor instanceof PauseView) {
				actor.setVisible(show);
				break;
			}
		}
	}
	
	public void showWinView(boolean show) {
		for(Actor actor : uiStage.getActors()) {
			if(actor instanceof WinView) {
				actor.setVisible(show);
				if(show) {
					((WinView) actor).playShowAnimation();
				}
				break;
			}
		}
	}
	
	public void showLoseView(boolean show) {
		for(Actor actor : uiStage.getActors()) {
			if(actor instanceof LoseView) {
				actor.setVisible(show);
				if(show) {
					((LoseView) actor).playShowAnimation();
				}
				break;
			}
		}
	}
	
	@Override
	protected Array<Table> getScreenViews(Main context) {
		Array<Table> views = new Array<>();
		
		GameView gameView = new GameView(new GameModel(context.getGameStage()), Scene2DSkin.defaultSkin);
        views.add(gameView);
        
        InventoryView invView = new InventoryView(new InventoryModel(context), Scene2DSkin.defaultSkin);
        invView.setVisible(false);
        views.add(invView);
        
        PauseView pauseView = new PauseView(Scene2DSkin.defaultSkin, context);
        pauseView.setVisible(false);
        views.add(pauseView);
        
        WinView winView = new WinView(Scene2DSkin.defaultSkin, context);
        winView.setVisible(false);
        views.add(winView);
        
        LoseView loseView = new LoseView(Scene2DSkin.defaultSkin, context);
        loseView.setVisible(false);
        views.add(loseView);
        
		return views;
	}
	
	private boolean isMovementKey(GameKeys key) {
		return key == GameKeys.UP || key == GameKeys.DOWN || key == GameKeys.LEFT || key == GameKeys.RIGHT;
	}
	
	private void updatePlayerMovement() {
		directionVector.set(playerCos, playerSin).nor();
		for (Entity player : ecsEngine.getEntitiesFor(Family.all(PlayerComponent.class).get())) {
			final MoveComponent moveCmp = ECSEngine.moveCmpMapper.get(player);
			moveCmp.cosine = directionVector.x;
			moveCmp.sine = directionVector.y;
		}
	}

	@Override
	public void keyPressed(InputManager manager, GameKeys key) {
		if (isMovementKey(key)) {
			switch (key) {
				case UP:
					playerSin = manager.isKeyPressed(GameKeys.DOWN) ? 0 : 1;
			 		break;
			 	case DOWN:
			 		playerSin = manager.isKeyPressed(GameKeys.UP) ? 0 : -1;
			 		break;
			 	case RIGHT:
			 		playerCos = manager.isKeyPressed(GameKeys.LEFT) ? 0 : 1;
			 		break;
			 	case LEFT:
			 		playerCos = manager.isKeyPressed(GameKeys.RIGHT) ? 0 : -1;
			 		break;
			 	default:
			 		return;
	            }
			 updatePlayerMovement();
		 } else if (key == GameKeys.ATTACK) {
			 for (Entity player : ecsEngine.getEntitiesFor(Family.all(PlayerComponent.class).get())) {
				final AttackComponent attackCmp = ECSEngine.attackCmpMapper.get(player);
				attackCmp.doAttack = true;
			}
		 } else if (key == GameKeys.INVENTORY) {
			 for(Actor actor : uiStage.getActors()) {
					if(actor instanceof InventoryView) {
						actor.setVisible(!actor.isVisible());
						break;
					}
				}
		 } else if (key == GameKeys.PAUSE) {
			 paused = !paused;
			 if(paused) {
				 GamePauseEvent event = GamePauseEvent.pool.obtain();
				 gameStage.getRoot().fire(event);
				 GamePauseEvent.pool.free(event);
			 } else {
				 GameResumeEvent event = GameResumeEvent.pool.obtain();
				 gameStage.getRoot().fire(event);
				 GameResumeEvent.pool.free(event);
			 }
		 }
	}

	@Override
	public void keyUp(InputManager manager, GameKeys key) {
        if (isMovementKey(key)) {
        	switch (key) {
        		case UP:
                    playerSin = manager.isKeyPressed(GameKeys.DOWN) ? -1 : 0;
                    break;
                case DOWN:
                    playerSin = manager.isKeyPressed(GameKeys.UP) ? 1 : 0;
                    break;
                case RIGHT:
                    playerCos = manager.isKeyPressed(GameKeys.LEFT) ? -1 : 0;
                    break;
                case LEFT:
                    playerCos = manager.isKeyPressed(GameKeys.RIGHT) ? 1 : 0;
                    break;
                default:
                    return;
            }
        	updatePlayerMovement();
        }
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof GamePauseEvent) {
			paused = true;
			this.pause();
			this.showPauseView(true);
		} else if(event instanceof GameResumeEvent) {
			paused = false;
			this.resume();
			this.showPauseView(false);
			this.showWinView(false);
			this.showLoseView(false);
		} else if(event instanceof WinEvent) {
			paused = true;
			this.pause();
			this.showWinView(true);
		} else if(event instanceof LoseEvent) {
			paused = true;
			this.pause();
			this.showLoseView(true);
		}else {
			return false;
		}
		return true;
	}
	
	@Override
	public void dispose() {
		
	}
}
