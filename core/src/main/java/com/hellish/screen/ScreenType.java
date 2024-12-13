package com.hellish.screen;

import com.badlogic.gdx.Screen;

public enum ScreenType {
	GAME(GameScreen.class),
	LOADING(LoadingScreen.class),
	MAIN_MENU(MainMenuScreen.class),
	GUIDE(GuideScreen.class), 
<<<<<<< HEAD
	DAILOG(DialogScreen.class),
	SETTING(SettingScreen.class);
=======
	DIALOG(DialogScreen.class);
>>>>>>> 47b1a12c6cc23032e2e455efb0bc7314c352d072
	
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
