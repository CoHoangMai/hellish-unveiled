package com.hellish;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.hellish.audio.AudioManager;
import com.hellish.ecs.ECSEngine;
import com.hellish.input.InputManager;
import com.hellish.map.MapManager;
import com.hellish.screen.ScreenType;
import com.hellish.view.GameRenderer;

import box2dLight.Light;
import box2dLight.RayHandler;

import java.util.EnumMap;

public class Main extends Game {
	private static final String TAG = Main.class.getSimpleName();
	public static final String VIETNAMESE_CHARS = "ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝàáâãèéêìíòóôõùúýĂăĐđĨĩŨũƠơƯưẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹ";
	
	
	private SpriteBatch spriteBatch;
	private EnumMap<ScreenType, Screen> screenCache;
	private OrthographicCamera gameCamera;
	private FitViewport screenViewport;
	
	public static final BodyDef BODY_DEF = new BodyDef();
	public static final FixtureDef FIXTURE_DEF = new FixtureDef();
	public static final float UNIT_SCALE = 1 / 32f;
	public static final short BIT_PLAYER = 1 << 0; 
	public static final short BIT_GROUND = 1 << 1;	
	public static final short BIT_GAME_OBJECT = 1 << 2;
	
	private World world;
	private WorldContactListener worldContactListener;
	private Box2DDebugRenderer box2DDebugRenderer;
	private RayHandler rayHandler;
	
	private static final float FIXED_TIME_STEP = 1/60f;
	private float accumulator;
	
	private AssetManager assetManager;
	
	private AudioManager audioManager;
	
	private Stage stage;
	private Skin skin;
	
	private InputManager inputManager;
	
	private MapManager mapManager;
	
	private ECSEngine ecsEngine;
	
	private GameRenderer gameRenderer;
	
	private PreferenceManager preferenceManager;
	
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		spriteBatch = new SpriteBatch();
		
		accumulator = 0;
		
		//box2d
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		worldContactListener = new WorldContactListener();
		world.setContactListener(worldContactListener);
		box2DDebugRenderer = new Box2DDebugRenderer();
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.05f, 0.05f, 0.4f, 0.2f);
		Light.setGlobalContactFilter(BIT_PLAYER, (short) 1, BIT_GROUND);
		
		//assetManager
		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(assetManager.getFileHandleResolver()));
		initializeSkin();
		stage = new Stage(new FitViewport(960, 540), spriteBatch);
		
		//audio
		audioManager = new AudioManager(this);
		
		//input
		inputManager = new InputManager();
		Gdx.input.setInputProcessor(new InputMultiplexer(inputManager, stage));
				
		//viewport
		gameCamera = new OrthographicCamera();
		screenViewport = new FitViewport(16, 9, gameCamera);
		
		//ECS
		ecsEngine = new ECSEngine(this);
		
		//mapManager
		mapManager = new MapManager(this);
		
		//Game renderer
		gameRenderer = new GameRenderer(this);
		
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
		BODY_DEF.fixedRotation = false;
		
		FIXTURE_DEF.density = 0;
		FIXTURE_DEF.isSensor = false;
		FIXTURE_DEF.restitution = 0;
		FIXTURE_DEF.friction = 0.2f;
		FIXTURE_DEF.filter.categoryBits = 0x0001;
		FIXTURE_DEF.filter.maskBits = -1;
		FIXTURE_DEF.shape = null;
	}
	
	public void	initializeSkin() {
		//Tạo màu markup
		Colors.put("Red", Color.RED);
		Colors.put("Blue", Color.BLUE);
		
		//Tạo ttf bitmap
		final ObjectMap<String, Object> resources = new ObjectMap<String, Object>();
		final FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/KK7-VCROSDMono.ttf"));
		final FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.minFilter = Texture.TextureFilter.Linear;
		fontParameter.magFilter = Texture.TextureFilter.Linear;
		fontParameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + VIETNAMESE_CHARS;
		final int[] sizesToCreate = {16, 20, 26, 32};
		for(int size: sizesToCreate) {
			fontParameter.size = size;
			final BitmapFont bitmapFont = fontGenerator.generateFont(fontParameter);
			bitmapFont.getData().markupEnabled = true;
			resources.put("font_" + size, bitmapFont);
		}
		fontGenerator.dispose();
		
		//Load skin
		final SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter("ui/gorehud.atlas", resources);
		assetManager.load("ui/hud.json", Skin.class, skinParameter);
		assetManager.finishLoading();
		skin = assetManager.get("ui/hud.json", Skin.class);
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
	
	public ECSEngine getECSEngine() {
		return ecsEngine;
	}
	
	public AudioManager getAudioManager() {
		return audioManager;
	}
	
	public InputManager getInputManager() {
		return inputManager;
	}
	
	public Stage getStage() {
		return stage;
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
	
	public OrthographicCamera getGameCamera() {
		return gameCamera;
	}
	
	public FitViewport getScreenViewport() {return screenViewport;}
	
	public World getWorld() {
		return world;
	}
	
	public Box2DDebugRenderer getBox2DDebugRenderer() {
		return box2DDebugRenderer;
	}
	
	public WorldContactListener getWorldContactListener() {
		return worldContactListener;
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
		super.render();
		
		@SuppressWarnings("deprecation")
		final float deltaTime = Math.min(0.25f, Gdx.graphics.getRawDeltaTime());
		ecsEngine.update(deltaTime);
		
		accumulator += deltaTime;
		while (accumulator >= FIXED_TIME_STEP) {
			//TODO save previous position of the body
			world.step(FIXED_TIME_STEP, 6, 2);
			accumulator -= FIXED_TIME_STEP;
		}
		
		//TODO calculate renderPosition from previous position and real body position
		gameRenderer.render(accumulator / FIXED_TIME_STEP);
		stage.getViewport().apply();
		stage.act(deltaTime);
		stage.draw();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		gameRenderer.dispose();
		rayHandler.dispose();
		world.dispose();
		assetManager.dispose();
		spriteBatch.dispose();	
		stage.dispose();
	}
		
}