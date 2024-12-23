package com.hellish;

import static com.hellish.ecs.component.LightComponent.ENVIRONMENT_BIT;
import static com.hellish.ecs.component.LightComponent.PLAYER_BIT;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.hellish.audio.AudioManager;
import com.hellish.ecs.ECSEngine;
import com.hellish.ecs.component.ComponentManager;
import com.hellish.ecs.system.RenderSystem;
import com.hellish.event.EntityAttackEvent;
import com.hellish.event.ScreenChangeEvent;
import com.hellish.input.InputManager;
import com.hellish.map.MapManager;
import com.hellish.screen.GameScreen;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin;

import box2dLight.Light;
import box2dLight.RayHandler;

import java.util.EnumMap;

public class Main extends Game {
	private static final String TAG = Main.class.getSimpleName();
	public static final String VIETNAMESE_CHARS = "ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝàáâãèéêìíòóôõùúýĂăĐđĨĩŨũƠơƯưẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹ";
	public static final float UNIT_SCALE = 1 / 32f;
	
	private SpriteBatch spriteBatch;
	private EnumMap<ScreenType, Screen> screenCache;
	private FitViewport screenViewport;
	
	private World world;
	private RayHandler rayHandler;
	
	private AssetManager assetManager;
	
	private AudioManager audioManager;
	
	private Stage gameStage;
	private Stage uiStage;
	
	private InputManager inputManager;
	private MapManager mapManager;
	private ComponentManager componentManager;
	private PreferenceManager preferenceManager;
	
	private ECSEngine ecsEngine;
	
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		spriteBatch = new SpriteBatch();
		
		//box2d
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		
		//rayHandler
		rayHandler = new RayHandler(world);
		RayHandler.useDiffuseLight(true);
		Light.setGlobalContactFilter(PLAYER_BIT, (short) 1, ENVIRONMENT_BIT);
		
		//assetManager
		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));
		
		gameStage = new Stage(new FitViewport(16, 9), spriteBatch);
		uiStage = new Stage(new FitViewport(640, 360), spriteBatch);
		
		//audio
		audioManager = new AudioManager(this);
		
		//skin
		Scene2DSkin.loadSkin(this);
		
		//componentManager
		componentManager = new ComponentManager();

		//mapManager
		mapManager = new MapManager(this);
		
		//ECS
		ecsEngine = new ECSEngine(this);
		for (EntitySystem system : ecsEngine.getSystems()) {
			if(system instanceof EventListener) {
				gameStage.addListener((EventListener) system);
			}
		}
		
		//input
		inputManager = new InputManager(this);
		Gdx.input.setInputProcessor(new InputMultiplexer(inputManager, gameStage, uiStage));
		
		//Preference manager
		preferenceManager = new PreferenceManager();
		
		//Screen đầu
		screenCache = new EnumMap<ScreenType, Screen>(ScreenType.class);
		setScreen(ScreenType.LOADING);	
	}
	
	public PreferenceManager getPreferenceManager() {
		return preferenceManager;
	}
	
	public RayHandler getRayHandler() {
		return rayHandler;
	}
	
	public MapManager getMapManager() {
		return mapManager;
	}
	
	public ComponentManager getComponentManager() {
		return componentManager;
	}
	
	public ECSEngine getECSEngine() {
		return ecsEngine;
	}
	
	public AudioManager getAudioManager() {
		return audioManager;
	}
	
	public InputManager getInputManager() {
		return inputManager;
	}
	
	public Stage getGameStage() {
		return gameStage;
	}
	
	public Stage getUIStage() {
		return uiStage;
	}
	
	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}
	
	public AssetManager getAssetManager() {
		return assetManager;
	}
	
	public FitViewport getScreenViewport() {return screenViewport;}
	
	public World getWorld() {
		return world;
	}
	
	public void setScreen(final ScreenType screenType) {
		final Screen screen = screenCache.get(screenType);
		if (screen == null) {
			try {
				Gdx.app.debug(TAG, "Khởi tạo Screen mới: " + screenType);
				final Screen newScreen = (Screen)ClassReflection.getConstructor(screenType.getScreenClass(), Main.class).newInstance(this);
				screenCache.put(screenType, newScreen);
				setScreen(newScreen);
				fireScreenChangeEvent(screenType);
			} catch (ReflectionException e) {
				throw new GdxRuntimeException("Screen "+ screenType + " không thể khởi tạo", e);
			}
		} else {
			Gdx.app.debug(TAG, "Đổi sang screen: " + screenType);
			if(screen instanceof GameScreen) {
				for(EntitySystem system : ecsEngine.getSystems()) {
					system.setProcessing(true);
				}
			} else {
				for(EntitySystem system : ecsEngine.getSystems()) {
					if(!(system instanceof RenderSystem)) {
						system.setProcessing(false);
					}
				}
			}
			setScreen(screen);
			fireScreenChangeEvent(screenType);
		}
	}
	
	private void fireScreenChangeEvent(ScreenType screenType) {
		ScreenChangeEvent event = new ScreenChangeEvent(screenType.name());
		gameStage.getRoot().fire(event);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		@SuppressWarnings("deprecation")
		final float deltaTime = Math.min(0.25f, Gdx.graphics.getRawDeltaTime());
		GdxAI.getTimepiece().update(deltaTime);
		ecsEngine.update(deltaTime);
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		gameStage.getViewport().update(width, height);
		uiStage.getViewport().update(width, height);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		rayHandler.dispose();
		world.dispose();
		ecsEngine.dispose();
		mapManager.dispose();
		assetManager.dispose();
		gameStage.dispose();
		uiStage.dispose();
		spriteBatch.dispose();
		Scene2DSkin.disposeSkin();
	}	
}