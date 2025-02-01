package com.hellish.ui.widget;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.Scene2DSkin.Labels;

public class CharacterInfo extends WidgetGroup{
	private final Image background;
    private final Image lifeBar;
    private final Image cooldownBar;
    private final Label lifePoint;
    
    public CharacterInfo(Skin skin) {
    	
    	background = new Image(skin.getDrawable(Drawables.CHAR_INFO_BGD.getAtlasKey()));
    	
    	lifeBar = new Image(skin.getDrawable(Drawables.LIFE_BAR.getAtlasKey()));
    	lifeBar.setPosition(46, 22);
    	
    	cooldownBar = new Image(skin.getDrawable(Drawables.COOLDOWN_BAR.getAtlasKey()));
    	cooldownBar.setPosition(46, 11);
    	
    	lifePoint = new Label("", skin.get(Labels.SMOL.getSkinKey(), LabelStyle.class));
    	lifePoint.setAlignment(Align.topLeft);
    	lifePoint.setPosition(185, 30);
    	
    	addActor(background);
    	addActor(lifeBar);
    	addActor(cooldownBar);
    	addActor(lifePoint);
    }
    
    @Override
    public float getPrefWidth() {
    	return background.getDrawable().getMinWidth();
    }

    @Override
    public float getPrefHeight() {
    	return background.getDrawable().getMinHeight();
    }
    
    public void life(float current, float max, float duration) {
    	float percentage = current / max;
        lifeBar.clearActions();
        lifeBar.addAction(Actions.scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, duration));
		
		final StringBuilder stringBuilder = lifePoint.getText();
		stringBuilder.setLength(0);
		stringBuilder.append(String.format("%.0f", current));
		stringBuilder.append("/");
		stringBuilder.append(String.format("%.0f", max));
		lifePoint.invalidateHierarchy();
    }
    public void life(float current, float max) {
    	life(current, max, 0.75f);
    }
    
    public void cooldown(float percentage) {
    	cooldownBar.clearActions();
        cooldownBar.addAction(Actions.scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, 0.1f));
    }
}
