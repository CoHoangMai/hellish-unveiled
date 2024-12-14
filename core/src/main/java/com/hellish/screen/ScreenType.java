package com.hellish.screen;

import com.badlogic.gdx.Screen;

public enum ScreenType {
	GAME(GameScreen.class),
	LOADING(LoadingScreen.class),
	MAIN_MENU(MainMenuScreen.class),
	GUIDE(GuideScreen.class), 
	DAILOG(DialogScreen.class),
	SETTING(SettingScreen.class),
	WIN(WinScreen.class);
	
	@SuppressWarnings("rawtypes")
	private final Class<? extends AbstractScreen> screenClass;
	
	@SuppressWarnings("rawtypes")
	ScreenType(final Class<? extends AbstractScreen> screenClass){
		this.screenClass = screenClass;
	}
	
	public Class<? extends Screen> getScreenClass(){
		return screenClass;
	}
}
