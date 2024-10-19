package com.hellish.input;

import com.badlogic.gdx.Input;

public enum GameKeys {
	UP(Input.Keys.UP, Input.Keys.W), 
	DOWN(Input.Keys.DOWN, Input.Keys.S), 
	LEFT(Input.Keys.LEFT, Input.Keys.A),
	RIGHT(Input.Keys.RIGHT, Input.Keys.D),
	SELECT(Input.Keys.ENTER, Input.Keys.SPACE),
	BACK(Input.Keys.ESCAPE, Input.Keys.BACKSPACE);
	
	final int[] keyCode;
	
	GameKeys(final int... keyCode){
		this.keyCode = keyCode;
	}
	
	public int[] getKeyCode() {
		return keyCode;
	}
}
