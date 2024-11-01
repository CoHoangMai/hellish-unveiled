package com.hellish.input;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.MoveComponent;
import com.hellish.ecs.component.PlayerComponent;

public class InputManager implements InputProcessor{
	private final GameKeys[] keyMapping;
	private final boolean[] keyState;
	private final Array<GameKeyInputListener> listeners;
	
	private final ECSEngine ecsEngine;
	
	private float playerSin;
	private float playerCos;
	
	public InputManager(final Main context) {
		this.keyMapping = new GameKeys[256];
		for (final GameKeys gameKey: GameKeys.values()) {
			for(final int code: gameKey.keyCode) {
				keyMapping[code] = gameKey;
			}
		}
		keyState = new boolean[GameKeys.values().length];
		listeners = new Array<GameKeyInputListener>();
		
		ecsEngine = context.getECSEngine();
		Gdx.input.setInputProcessor(this);
		
		playerSin = 0f;
		playerCos = 0f;
	}
	
	public void addInputListener(final GameKeyInputListener listener) {
		listeners.add(listener);
	}
	
	public void removeInputListener(final GameKeyInputListener listener) {
		listeners.removeValue(listener, true);
	}
	
	private boolean isMovementKey(int keycode) {
		return keycode == Input.Keys.UP || keycode == Input.Keys.DOWN || keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT;
	}
	
	private void updatePlayerMovement() {
		for (Entity player : ecsEngine.getEntitiesFor(Family.all(PlayerComponent.class).get())) {
			final MoveComponent moveCmp = ECSEngine.moveCmpMapper.get(player);
			moveCmp.sine = playerSin;
			moveCmp.cosine = playerCos;
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		final GameKeys gameKey = keyMapping[keycode];
		if(gameKey == null) {
			//Không có mapping -> không làm gì cả
			return false;
		}
		 if (isMovementKey(keycode)) {
			 switch (keycode) {
			 	case Input.Keys.UP:
			 		playerSin = 1f;
			 		break;
			 	case Input.Keys.DOWN:
			 		playerSin = -1f;
			 		break;
			 	case Input.Keys.RIGHT:
			 		playerCos = 1f;
			 		break;
			 	case Input.Keys.LEFT:
			 		playerCos = -1f;
			 		break;
	            }
			 updatePlayerMovement();
			 return true;
		 }
		
		notifyKeyDown(gameKey);
		
		return false;
	}

	public void notifyKeyDown(final GameKeys gameKey) {
		keyState[gameKey.ordinal()] = true;
		for(final GameKeyInputListener listener : listeners) {
			listener.keyPressed(this, gameKey);
		}	
	}

	@Override
	public boolean keyUp(int keycode) {
		final GameKeys gameKey = keyMapping[keycode];
		if(gameKey == null) {
			//Không có mapping -> không làm gì cả
			return false;
		}
		
        if (isMovementKey(keycode)) {
        	switch (keycode) {
        		case Input.Keys.UP:
                    playerSin = Gdx.input.isKeyPressed(Input.Keys.DOWN) ? -1f : 0f;
                    break;
                case Input.Keys.DOWN:
                    playerSin = Gdx.input.isKeyPressed(Input.Keys.UP) ? 1f : 0f;
                    break;
                case Input.Keys.RIGHT:
                    playerCos = Gdx.input.isKeyPressed(Input.Keys.LEFT) ? -1f : 0f;
                    break;
                case Input.Keys.LEFT:
                    playerCos = Gdx.input.isKeyPressed(Input.Keys.RIGHT) ? 1f : 0f;
                    break;
            }
        	updatePlayerMovement();
        	return true;
        }
		
		notifyKeyUp(gameKey);
		
		return false;
	}
	
	public void notifyKeyUp(final GameKeys gameKey) {
		keyState[gameKey.ordinal()] = false;
		for(final GameKeyInputListener listener : listeners) {
			listener.keyUp(this, gameKey);
		}	
	}
	
	public boolean isKeyPressed(final GameKeys gameKey) {
		return keyState[gameKey.ordinal()];
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

}
