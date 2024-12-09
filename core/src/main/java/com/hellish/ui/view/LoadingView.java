package com.hellish.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.StringBuilder;
import com.hellish.ui.Scene2DSkin.Buttons;
import com.hellish.ui.Scene2DSkin.ProgressBars;

public class LoadingView extends Table{
	private final ProgressBar progressBar;
	private final TextButton pressAnyKeyButton;
	private final TextButton txtButton;
	private final TextButton tempButton;
	public final Sprite backgroundSprite;
	
	public LoadingView(Skin skin) {
		super(skin);
		setFillParent(true);

		Texture backgroundTexture = new Texture("background.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundSprite.setPosition(-(Gdx.graphics.getWidth()/2), -(Gdx.graphics.getHeight()/2));
		
		progressBar = new ProgressBar(0, 1, 0.01f, false,
				getSkin().get(ProgressBars.LOADING.getSkinKey(), ProgressBarStyle.class));
		
		
		txtButton = new TextButton("", skin.get(Buttons.TEXT_BUTTON.getSkinKey(), TextButtonStyle.class));
		txtButton.getLabel().setWrap(true);

		tempButton = new TextButton("", skin.get(Buttons.TEXT_BUTTON.getSkinKey(), TextButtonStyle.class));
		tempButton.getLabel().setWrap(true);
		
		pressAnyKeyButton = new TextButton("[RED]Nhấn phím Enter hoặc Z để tiếp tục", 
				skin.get(Buttons.TEXT_BUTTON.getSkinKey(), TextButtonStyle.class));
		pressAnyKeyButton.getLabel().setWrap(true);
		pressAnyKeyButton.setVisible(false);
		
		add(tempButton).expand().fill().row();
		add(txtButton).expandX().fillX().bottom().pad(10).row();

		Table progressContainer = new Table();
    	progressContainer.add(progressBar).expand().fill(); // Đặt Progress Bar ở dưới cùng
    	Stack stack = new Stack(); // Stack để chồng các thành phần
    	stack.add(progressContainer); // Thêm Progress Bar vào Stack
    	stack.add(pressAnyKeyButton); 

		add(tempButton).expand().fill().row();
		add(txtButton).expandX().fillX().bottom().pad(10).row();
    	add(stack).expand().fill().center().pad(10);

	}
	
	public void setProgress(final float progress) {
		progressBar.setValue(progress);
		
		final StringBuilder stringBuilder = txtButton.getLabel().getText();
		stringBuilder.setLength(0);
		stringBuilder.append("[Red]Loading đến ");
		stringBuilder.append(String.format("%.1f", progress * 100));
		stringBuilder.append("%");
		txtButton.getLabel().invalidateHierarchy();
		
		if(progress >= 1 && !pressAnyKeyButton.isVisible()) {
			pressAnyKeyButton.setVisible(true);
			pressAnyKeyButton.setSize(progressBar.getWidth(), progressBar.getHeight());
			pressAnyKeyButton.setPosition(
				progressBar.getX(), // Vị trí X của progressBar
				progressBar.getY()  // Vị trí Y của progressBar
			);
			pressAnyKeyButton.setColor(1, 1, 1, 0);
			pressAnyKeyButton.addAction(Actions.forever(Actions.sequence(Actions.alpha(1, 1), Actions.alpha(0, 1))));
		}
	}
}
