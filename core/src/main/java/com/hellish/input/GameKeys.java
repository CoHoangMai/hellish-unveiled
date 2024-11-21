package com.hellish.input;

import com.badlogic.gdx.Input;

public enum GameKeys {
	UP(Input.Keys.UP), 
	DOWN(Input.Keys.DOWN), 
	LEFT(Input.Keys.LEFT),
	RIGHT(Input.Keys.RIGHT),
	ATTACK(Input.Keys.SPACE),
	SELECT(Input.Keys.ENTER, Input.Keys.Z),
	BACK(Input.Keys.BACKSPACE, Input.Keys.X),
	INVENTORY(Input.Keys.C),
	PAUSE(Input.Keys.ESCAPE);
	
	final int[] keyCode;
	
	GameKeys(final int... keyCode){
		this.keyCode = keyCode;
	}
	
	public int[] getKeyCode() {
		return keyCode;
	}
}
