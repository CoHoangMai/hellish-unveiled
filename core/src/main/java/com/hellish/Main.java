package com.hellish;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
import com.hellish.input.InputManager;
import com.hellish.map.MapManager;
import com.hellish.screen.ScreenType;
import com.hellish.ui.Scene2DSkin;

import box2dLight.RayHandler;

import java.util.EnumMap;

public class Main extends Game {
	private static final String TAG = Main.class.getSimpleName();
	public static final String VIETNAMESE_CHARS = "ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝàáâãèéêìíòóôõùúýĂăĐđĨĩŨũƠơƯưẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹ";
	
	private SpriteBatch spriteBatch;
	private EnumMap<ScreenType, Screen> screenCache;
	private FitViewport screenViewport;
	
	public static final BodyDef BODY_DEF = new BodyDef();
	public static final FixtureDef FIXTURE_DEF = new FixtureDef();
	public static final float UNIT_SCALE = 1 / 32f;
	
	private World world;
	private RayHandler rayHandler;
	
	private AssetManager assetManager;
	
	private AudioManager audioManager;
	
	private Stage gameStage;
	private Stage uiStage;
	private Skin skin;
	
	private InputManager inputManager;
	
	private MapManager mapManager;
	
	private ComponentManager componentManager;
	
	private ECSEngine ecsEngine;
	
	private PreferenceManager preferenceManager;
	
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		spriteBatch = new SpriteBatch();
		
		//box2d
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.05f, 0.05f, 0.4f, 0.2f);
		
		//assetManager
		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));
		//initializeSkin();
		Scene2DSkin.loadSkin();
		gameStage = new Stage(new FitViewport(16, 9));
		uiStage = new Stage(new FitViewport(320, 180));
		
		//audio
		audioManager = new AudioManager(this);
		
		//componentManager
		componentManager = new ComponentManager();
		
		//ECS
		ecsEngine = new ECSEngine(this);
		
		//input
		inputManager = new InputManager(this);
		Gdx.input.setInputProcessor(new InputMultiplexer(inputManager, gameStage));
		
		//mapManager
		mapManager = new MapManager(this);
		
		//Preference manager
		preferenceManager = new PreferenceManager();
		
		//Screen đầu
		screenCache = new EnumMap<ScreenType, Screen>(ScreenType.class);
		setScreen(ScreenType.LOADING);	
	}
	
	public static void resetBodyAndFixtureDefinition() {
		BODY_DEF.position.set(0, 0);
		BODY_DEF.gravityScale = 1;
		BODY_DEF.type = BodyDef.BodyType.StaticBody;
		BODY_DEF.fixedRotation = true;
		
		FIXTURE_DEF.density = 0;
		FIXTURE_DEF.isSensor = false;
		FIXTURE_DEF.restitution = 0;
		FIXTURE_DEF.friction = 0.2f;
		FIXTURE_DEF.filter.categoryBits = 0x0001;
		FIXTURE_DEF.filter.maskBits = -1;
		FIXTURE_DEF.shape = null;
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
	
	public Skin getSkin() {
		return skin;
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
			} catch (ReflectionException e) {
				throw new GdxRuntimeException("Screen "+ screenType + " không thể khởi tạo", e);
			}
		} else {
			Gdx.app.debug(TAG, "Đổi sang screen: " + screenType);
			setScreen(screen);
		}
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
		
		@SuppressWarnings("deprecation")
		final float deltaTime = Math.min(0.25f, Gdx.graphics.getRawDeltaTime());
		GdxAI.getTimepiece().update(deltaTime);
		ecsEngine.update(deltaTime);
		
		gameStage.getViewport().apply();
		gameStage.act(deltaTime);
		gameStage.draw();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		rayHandler.dispose();
		world.dispose();
		ecsEngine.dispose();
		assetManager.dispose();
		spriteBatch.dispose();	
		gameStage.dispose();
		uiStage.dispose();
		Scene2DSkin.disposeSkin();
	}
}