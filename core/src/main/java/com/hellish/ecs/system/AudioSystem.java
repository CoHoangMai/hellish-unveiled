package com.hellish.ecs.system;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Logger;
import com.hellish.audio.AudioType;
import com.hellish.ecs.component.AttackComponent;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.PhysicsComponent;
import com.hellish.event.EntityAggroEvent;
import com.hellish.event.EntityAttackEvent;
import com.hellish.event.MapChangeEvent;
import com.hellish.event.ScreenChangeEvent;

public class AudioSystem extends IteratingSystem implements EventListener {

    private final Map<String, Sound> soundCache = new HashMap<>();
    private final Map<String, Music> musicCache = new HashMap<>();
    private final Queue<String> soundRequests = new LinkedList<>();

    private Music currentMusic = null;
    private String currentMusicPath = null;

    private static final int MAX_SOUNDS_PER_FRAME = 3;
    private float soundVolume = 1.0f;
    private static final Logger log = new Logger(AudioSystem.class.getSimpleName(), Logger.DEBUG);

    public AudioSystem() {
        super(Family.all(AttackComponent.class, PhysicsComponent.class, ImageComponent.class).get());
    }

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
            default -> {
            }
        }
        return false;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (soundRequests.isEmpty()) {
            return;
        }

        int soundsPlayed = 0;
        while (!soundRequests.isEmpty() && soundsPlayed < MAX_SOUNDS_PER_FRAME) {
            String path = soundRequests.poll();
            Sound sound = soundCache.computeIfAbsent(path, key -> {
                log.info("Loading sound: " + path);
                return Gdx.audio.newSound(Gdx.files.internal(key));
            });
            sound.play(soundVolume);
            soundsPlayed++;
        }
    }

    private void enqueueSound(String path) {
        if (!soundRequests.contains(path)) {
            log.info("Enqueuing sound: " + path);
            soundRequests.offer(path);
        }
    }

    private void changeMusic(String path) {
        if (currentMusicPath != null && currentMusicPath.equals(path)) {
            log.info("Music already playing: " + path);
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
        log.info("Music changed to: " + path);
    }

    private void handleScreenChange(ScreenChangeEvent screenEvent) {
        String screenName = screenEvent.getScreenName();
        log.info("Changing audio for screen: " + screenName);

        AudioType audioType = switch (screenName) {
            case "MAIN_MENU", "LOADING", "SETTING" -> AudioType.INTRO;
            case "GamePlay" -> AudioType.GAME;
            case "GameOver" -> AudioType.SELECT;
            default -> null;
        };

        if (audioType != null) {
            changeMusic(audioType.getFilePath());
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
            log.info("Music volume set to: " + soundVolume);
        }
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float volume) {
        soundVolume = Math.max(0, Math.min(volume, 1)); // Clamp volume between 0 and 1
        log.info("Sound effects volume set to: " + soundVolume);
    }

    public Music getCurrentMusic() {
        if (currentMusic == null) {
            log.info("No music is currently playing.");
            //return "No music playing";
        }
        log.info("Current music path: " + currentMusic);
        return currentMusic;
    }
}
