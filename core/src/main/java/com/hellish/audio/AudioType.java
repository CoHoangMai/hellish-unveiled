package com.hellish.audio;

public enum AudioType {
	LOADING("audio/screen/loading.mp3", true, true, 0.3f),
	INTRO("audio/screen/main_menu.mp3", true, true, 0.3f),
	GAME("audio/music/default.mp3", true, true, 0.3f),
	WIN("audio/music/win_music.mp3", false, true, 0.3f),
	LOSE("audio/music/game_over.mp3", false, true, 0.3f),
	SELECT("audio/sound/drum_sound.wav", false, false, 0.3f);
	
	private final String filePath;
	private final boolean doesLoop;
	private final boolean isMusic;
	private float volume;
	
	AudioType(String filePath, boolean doesLoop, boolean isMusic, float volume) {
		this.filePath = filePath;
		this.doesLoop = doesLoop;
		this.isMusic = isMusic;
		this.volume = volume;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public boolean doesLoop() {
		return doesLoop;
	}
	
	public boolean isMusic() {
		return isMusic;
	}
	
	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}
}
