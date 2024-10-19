package com.hellish.view;

public enum AnimationType {
	HERO_DOWN_WALK("characters_and_effects/char_and_effect.atlas", "down_walk", 0.1f, 6),
	HERO_UP_WALK("characters_and_effects/char_and_effect.atlas", "up_walk", 0.1f, 6),
	HERO_LEFT_WALK("characters_and_effects/char_and_effect.atlas", "side_walk", 0.1f, 6),
	HERO_RIGHT_WALK("characters_and_effects/char_and_effect.atlas", "side_walk", 0.1f, 6),
	HERO_DOWN_IDLE("characters_and_effects/char_and_effect.atlas", "down_idle", 0.1f, 5),
	HERO_UP_IDLE("characters_and_effects/char_and_effect.atlas", "up_idle", 0.1f, 5),
	HERO_LEFT_IDLE("characters_and_effects/char_and_effect.atlas", "side_idle", 0.1f, 5),
	HERO_RIGHT_IDLE("characters_and_effects/char_and_effect.atlas", "side_idle", 0.1f, 5);
	
	private final String atlasPath;
	private final String atlasKey;
	private final float frameTime;
	private final int frameCount;
	
	AnimationType(String atlasPath, String atlasKey, float frameTime, int frameCount) {
		this.atlasPath = atlasPath;
		this.atlasKey = atlasKey;
		this.frameTime = frameTime;
		this.frameCount = frameCount;
	}

	public String getAtlasPath() {
		return atlasPath;
	}

	public String getAtlasKey() {
		return atlasKey;
	}

	public float getFrameTime() {
		return frameTime;
	}

	int getFrameCount() {
		return frameCount;
	}
	
	
}
