package com.hellish.audio;

public enum AudioType {
	LOADING("audio/screen/loading.mp3", true),
	INTRO("audio/screen/main_menu.mp3", true),
	GAME("audio/music/default.mp3", true),
	WIN("audio/music/win_music.mp3", false),
	LOSE("audio/music/game_over.mp3", false),
	SELECT("audio/sound/drum_sound.wav", false);
	
	private final String filePath;
	private final boolean doesLoop;
	
	AudioType(String filePath, boolean doesLoop) {
		this.filePath = filePath;
		this.doesLoop = doesLoop;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public boolean doesLoop() {
		return doesLoop;
	}
}
