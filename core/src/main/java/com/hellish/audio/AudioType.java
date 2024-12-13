package com.hellish.audio;

public enum AudioType {
	INTRO("audio/horror-background-atmosphere-11-240870.mp3", true, 0.3f),
	SELECT("audio/mixkit-hard-horror-hit-drum-565.wav", false, 0.3f),
	GAME("audio/Where-My-Home-Is.mp3", true, 0.3f);
	
	private final String filePath;
	private final boolean isMusic;
	private float volume;
	
	AudioType(String filePath, boolean isMusic, float volume) {
		this.filePath = filePath;
		this.isMusic = isMusic;
		this.volume = volume;
	}
	
	public String getFilePath() {
		return filePath;
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
