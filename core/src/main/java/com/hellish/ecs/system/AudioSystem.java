package com.hellish.ecs.system;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.hellish.audio.AudioType;
import com.hellish.event.EntityAggroEvent;
import com.hellish.event.EntityAttackEvent;
import com.hellish.event.LoseEvent;
import com.hellish.event.MapChangeEvent;
import com.hellish.event.ScreenChangeEvent;
import com.hellish.event.SelectEvent;
import com.hellish.event.WinEvent;
import com.hellish.screen.ScreenType;

public class AudioSystem extends EntitySystem implements EventListener {
	public static final String TAG = AudioSystem.class.getSimpleName();

    private final Map<String, Sound> soundCache = new HashMap<>();
    private final Map<String, Music> musicCache = new HashMap<>();
    private final Queue<String> soundRequests = new LinkedList<>();

    private Music currentMusic = null;
    private String currentMusicPath = null;

    private static final int MAX_SOUNDS_PER_FRAME = 3;
    private float soundVolume = 1.0f;

    @Override
    public boolean handle(Event event) {
        switch (event) {
            case MapChangeEvent mapEvent -> {
                String path = mapEvent.getTiledMap().getProperties().get("mapMusic", String.class);
                if (path != null) {
                    changeMusic(path);
                }
                return true;
            }
            case ScreenChangeEvent screenEvent -> {
                handleScreenChange(screenEvent);
                return true;
            }
            case EntityAttackEvent attackEvent -> {
                enqueueSound("audio/attack/" + attackEvent.getModel() + "_attack.mp3");
                return true;
            }
            case EntityAggroEvent aggroEvent -> {
                enqueueSound("audio/aggro/" + aggroEvent.getModel() + "_aggro.mp3");
                return true;
            }
            case SelectEvent selectEvent -> {
                enqueueSound(AudioType.SELECT.getFilePath());
                return true;
            }
            case WinEvent winEvent -> {
                changeMusic(AudioType.WIN);
                return true;
            }
            case LoseEvent loseEvent -> {
                changeMusic(AudioType.LOSE);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
    
    @Override
    public void update(float deltaTime) {
    	 if (soundRequests.isEmpty()) {
             return;
         }

         int soundsPlayed = 0;
         while (!soundRequests.isEmpty() && soundsPlayed < MAX_SOUNDS_PER_FRAME) {
             String path = soundRequests.poll();
             Sound sound = soundCache.computeIfAbsent(path, key -> {
                 Gdx.app.debug(TAG, "Loading sound: " + path);
                 return Gdx.audio.newSound(Gdx.files.internal(key));
             });
             sound.play(soundVolume);
             soundsPlayed++;
         }
    }

    private void enqueueSound(String path) {
        if (!soundRequests.contains(path)) {
            Gdx.app.debug(TAG, "Enqueuing sound: " + path);
            soundRequests.offer(path);
        }
    }

    private void changeMusic(AudioType audioType) {
    	String path = audioType.getFilePath();
    	
        if (currentMusicPath != null && currentMusicPath.equals(path)) {
            Gdx.app.debug(TAG, "Music already playing: " + path);
            return;
        }

        currentMusicPath = path;
        Music newMusic = musicCache.computeIfAbsent(path, key -> {
            Music loadedMusic = Gdx.audio.newMusic(Gdx.files.internal(key));
            loadedMusic.setLooping(audioType.doesLoop());
            return loadedMusic;
        });

        if (currentMusic != null && currentMusic != newMusic) {
            currentMusic.stop();
        }

        currentMusic = newMusic;
        currentMusic.setVolume(0.1f);
        currentMusic.play();
        Gdx.app.debug(TAG, "Music changed to: " + path);
    }
    
    private void changeMusic(String path) {
        if (currentMusicPath != null && currentMusicPath.equals(path)) {
            Gdx.app.debug(TAG, "Music already playing: " + path);
            return;
        }

        currentMusicPath = path;
        Music newMusic = musicCache.computeIfAbsent(path, key -> {
            Music loadedMusic = Gdx.audio.newMusic(Gdx.files.internal(key));
            loadedMusic.setLooping(true);
            return loadedMusic;
        });

        if (currentMusic != null && currentMusic != newMusic) {
            currentMusic.stop();
        }

        currentMusic = newMusic;
        currentMusic.setVolume(0.1f);
        currentMusic.play();
        Gdx.app.debug(TAG, "Music changed to: " + path);
    }

    private void handleScreenChange(ScreenChangeEvent screenEvent) {
        ScreenType screenType = screenEvent.getScreenType();
        Gdx.app.debug(TAG, "Changing audio for screen: " + screenType);

        AudioType audioType = switch (screenType) {
        	case ScreenType.LOADING -> AudioType.LOADING;
            case ScreenType.MAIN_MENU, ScreenType.DIALOG, ScreenType.GUIDE -> AudioType.INTRO;
            case ScreenType.GAME -> AudioType.GAME;
            default -> null;
        };

        if (audioType != null) {
            changeMusic(audioType);
        }
    }

    public float getMusicVolume() {
        if (currentMusic == null) {
            // Handle case where currentMusic is null (possibly return a default value or log an error)
            System.out.println("Current music is not loaded");
            return 0f; // Or some default value indicating no music is playing
        }
        return currentMusic.getVolume();
    }
    

    public void setMusicVolume(float volume) {
        soundVolume = Math.max(0, Math.min(volume, 1)); // Clamp volume between 0 and 1
        if (currentMusic != null) {
            currentMusic.setVolume(soundVolume);
            Gdx.app.debug(TAG, "Music volume set to: " + soundVolume);
        }
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float volume) {
        soundVolume = Math.max(0, Math.min(volume, 1)); // Clamp volume between 0 and 1
        Gdx.app.debug(TAG, "Sound effects volume set to: " + soundVolume);
    }

    public Music getCurrentMusic() {
        if (currentMusic == null) {
            Gdx.app.debug(TAG, "No music is currently playing.");
        }
        Gdx.app.debug(TAG, "Current music path: " + currentMusic);
        return currentMusic;
    }
}
