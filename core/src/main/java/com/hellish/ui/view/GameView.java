package com.hellish.ui.view;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.hellish.ui.Scene2DSkin.Drawables;
import com.hellish.ui.Scene2DSkin.Labels;
import com.hellish.ui.model.GameModel;
import com.hellish.ui.model.PropertyChangeSource.MultiConsumer;
import com.hellish.ui.widget.CharacterInfo;

public class GameView extends Table{
	private CharacterInfo playerInfo;
	private Label popUpLabel;
	
	public GameView(GameModel model, Skin skin) {
		super(skin);
		setFillParent(true);
        
		playerInfo = new CharacterInfo(skin);
        add(playerInfo).left().top().pad(10).row();
        
        Table table = new Table();
        table.background(skin.getDrawable(Drawables.FRAME_BGD.getAtlasKey()));
        popUpLabel = new Label("", skin.get(Labels.NORMAL.getSkinKey(), LabelStyle.class));
        popUpLabel.setAlignment(Align.topLeft);
        popUpLabel.setWrap(true);
        table.add(popUpLabel).expand().fill().pad(14).top().row();
        table.getColor().a = 0;
        add(table).expand().width(400).height(120).pad(20).bottom();
        
        //Data binding
        model.onPropertyChange("playerLife", (MultiConsumer<Object>) (values) -> {
            // Lấy từng giá trị từ mảng values
            float current = (float) values[0];
            float max = (float) values[1];
            Float duration = (Float) values[2];
            
            // Thực hiện hành động
            playerLife(current, max, duration);
        });
        model.onPropertyChange("playerCooldown", 
        		(Consumer<Object>) (percentage -> playerCooldown((float) percentage)));
        model.onPropertyChange("popUpText", (Consumer<Object>) (popUpInfo -> popUp((String) popUpInfo)));
	}
	
	 private void playerLife(float current, float max, Float duration) {
		 if(duration == null) {
			 playerInfo.life(current, max);       
		 }
		 else {
			 playerInfo.life(current, max, duration);
		 }
	 }
	 
	 public void playerCooldown(float percentage) {
		 playerInfo.cooldown(percentage);
	 }

	private void resetFadeOutDelay(Actor actor) {
		SequenceAction sequence = null;
		for(Action action : actor.getActions()) {
			if(action instanceof SequenceAction) {
				sequence = (SequenceAction) action;
			}
		}
		
		if(sequence != null) {
			DelayAction delay = (DelayAction) sequence.getActions().get(sequence.getActions().size - 1);
			delay.setTime(0);
		}
	}
    
    public void popUp(String infoText) {
        popUpLabel.setText(infoText);
        if (popUpLabel.getParent().getColor().a == 0f) {
            popUpLabel.getParent().clearActions();
            popUpLabel.getParent().addAction(Actions.sequence(Actions.fadeIn(0.2f), Actions.delay(1, Actions.fadeOut(0.75f))));
        } else {
            resetFadeOutDelay(popUpLabel.getParent());
        }
    }
}
