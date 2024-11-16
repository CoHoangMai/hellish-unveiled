package com.hellish.ui.view;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
	
	public LoadingView(Skin skin) {
		super(skin);
		setFillParent(true);
		
		progressBar = new ProgressBar(0, 1, 0.01f, false,
				getSkin().get(ProgressBars.LOADING.getSkinKey(), ProgressBarStyle.class));
		
		
		txtButton = new TextButton("", skin.get(Buttons.TEXT_BUTTON.getSkinKey(), TextButtonStyle.class));
		txtButton.getLabel().setWrap(true);
		
		pressAnyKeyButton = new TextButton("[RED]Nhấn phím bất kì test nhân phẩm để tiếp tục", 
				skin.get(Buttons.TEXT_BUTTON.getSkinKey(), TextButtonStyle.class));
		pressAnyKeyButton.getLabel().setWrap(true);
		pressAnyKeyButton.setVisible(false);
		
		add(pressAnyKeyButton).expand().fill().center().row();
		add(txtButton).expandX().fillX().bottom().row();
		add(progressBar).expandX().fillX().bottom().pad(10, 15, 10, 15);
	}
	
	public void setProgress(final float progress) {
		progressBar.setValue(progress);
		
		final StringBuilder stringBuilder = txtButton.getLabel().getText();
		stringBuilder.setLength(0);
		stringBuilder.append("[Red]Loading đến ");
		stringBuilder.append(progress*100);
		stringBuilder.append("%");
		txtButton.getLabel().invalidateHierarchy();
		
		if(progress >= 1 && !pressAnyKeyButton.isVisible()) {
			pressAnyKeyButton.setVisible(true);
			pressAnyKeyButton.setColor(1, 1, 1, 0);
			pressAnyKeyButton.addAction(Actions.forever(Actions.sequence(Actions.alpha(1, 1), Actions.alpha(0, 1))));
		}
	}
}
