package com.hellish.ui.widget;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Scaling;
import com.hellish.ui.Scene2DSkin.Drawables;

public class CharacterInfo extends WidgetGroup{
	private final Skin skin;
	private final Image background;
    private final Image charBgd;
    private final Image lifeBar;
    private final Image manaBar;
    
    public CharacterInfo(Drawables charDrawable, Skin skin) {
    	this.skin = skin;
    	
    	background = new Image(skin.getDrawable(Drawables.CHAR_INFO_BGD.getAtlasKey()));
    	
    	lifeBar = new Image(skin.getDrawable(Drawables.LIFE_BAR.getAtlasKey()));
    	lifeBar.setPosition(39, 19);
    	
    	manaBar = new Image(skin.getDrawable(Drawables.MANA_BAR.getAtlasKey()));
    	manaBar.setPosition(39, 8);
    	
    	charBgd = new Image(charDrawable == null ? null : skin.getDrawable(charDrawable.getAtlasKey()));
    	charBgd.setPosition(7.5f, 10);
    	charBgd.setSize(22, 20);
        charBgd.setScaling(Scaling.contain);
    	
    	addActor(background);
    	addActor(charBgd);
    	addActor(lifeBar);
    	addActor(manaBar);
    }
    
    @Override
    public float getPrefWidth() {
    	return background.getDrawable().getMinWidth();
    }

    @Override
    public float getPrefHeight() {
    	return background.getDrawable().getMinHeight();
    }
    
    public void character(Drawables charDrawable) {
        if (charDrawable == null) {
            charBgd.setDrawable(null);
        } else {
            charBgd.setDrawable(skin.getDrawable(charDrawable.getAtlasKey()));
        }
    }
    
    public void life(float percentage, float duration) {
        lifeBar.clearActions();
        lifeBar.addAction(Actions.scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, duration));
    }
    public void life(float percentage) {
        lifeBar.clearActions();
        lifeBar.addAction(Actions.scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, 0.75f));
    }
    
    public void mana(float percentage, float duration) {
        manaBar.clearActions();
        manaBar.addAction(Actions.scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, duration));
    }
    public void mana(float percentage) {
        manaBar.clearActions();
        manaBar.addAction(Actions.scaleTo(MathUtils.clamp(percentage, 0f, 1f), 1f, 0.75f));
    }
}
