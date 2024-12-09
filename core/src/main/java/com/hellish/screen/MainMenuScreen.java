package com.hellish.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.hellish.Main;
import com.hellish.input.GameKeys;
import com.hellish.input.InputManager;
import com.hellish.ui.view.MainMenuView;

public class MainMenuScreen extends AbstractScreen<MainMenuView> {

    private final Skin skin;
    private MainMenuView mainMenuView;
    OrthographicCamera camera;
    SpriteBatch batch; // Skin để sử dụng cho MainMenuView

    public MainMenuScreen(final Main context) {
        super(context);
        this.skin = context.getSkin(); // Lấy skin từ ngữ cảnh
        mainMenuView = new MainMenuView(context.getSkin(), context);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
    }

    @Override
    protected Array<MainMenuView> getScreenViews(final Main context) {
        Array<MainMenuView> views = new Array<>();
        MainMenuView mainMenuView = new MainMenuView(skin, context);
        views.add(mainMenuView);
        return views;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mainMenuView.backgroundSprite.draw(batch);
        batch.end();

        // Vẽ UI hoặc các thành phần khác
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
	public void keyPressed(InputManager manager, GameKeys key) {
	}

    @Override
    public void keyUp(InputManager manager, GameKeys key) {
      }
}