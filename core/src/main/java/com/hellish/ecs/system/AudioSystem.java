package com.hellish.ecs.system;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Logger;
import com.hellish.ecs.component.ImageComponent;
import com.hellish.ecs.component.PlayerComponent;
import com.hellish.event.EntityAggroEvent;
import com.hellish.event.EntityAttackEvent;
import com.hellish.event.MapChangeEvent;

public class AudioSystem extends IteratingSystem implements EventListener{

    private final Map<String, Sound> soundCache = new HashMap<>();
    private final Map<String, Music> musicCache = new HashMap<>();
    private final Map<String, Sound> soundRequests = new HashMap<>();
    private Music music = null;
    private static final Logger log = new Logger(AudioSystem.class.getSimpleName(), Logger.DEBUG);

    public AudioSystem() {
        super(Family.all(PlayerComponent.class, ImageComponent.class).get());
    }

 

    @Override
    public boolean handle(Event event) {
        switch(event){
            case MapChangeEvent mapEvent -> {
                String path = mapEvent.getTiledMap().getProperties().get("music", String.class);
                if (path != null) {
                    log.info("Changing music to '" + path + "'");
                    Music newMusic = musicCache.computeIfAbsent(path, key -> {
                        Music loadedMusic = Gdx.audio.newMusic(Gdx.files.internal(key));
                        loadedMusic.setLooping(true);
                        return loadedMusic;
                        });

                    if (music != null && newMusic != music) {
                        music.stop();
                    }
                    music = newMusic;
                    newMusic.play();
                }
            }
            case EntityAttackEvent attackEvent -> {
                queueSound("audio/attack/" + attackEvent.getModel() + "_attack.mp3");
            }
            case EntityAggroEvent aggroEvent -> {
                queueSound("audio/aggro/" + aggroEvent.getModel() + "_aggro.mp3");
            }
            default -> {
                return false;
            }
            
        }
        return true;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(soundRequests.isEmpty()) {
            return;
        }
        for (Sound sound : soundRequests.values()) {
            sound.play();
            System.out.println("Sound: " + sound.path);
        }
    }
    
    private void queueSound(String path) {
        log.info("Queueing new sound '" + path + "'");
        if (soundRequests.containsKey(path)) {
            return;
        }
        Sound sound = soundCache.computeIfAbsent(path, key -> Gdx.audio.newSound(Gdx.files.internal(key)));
        soundRequests.put(path, sound);
    }
}
