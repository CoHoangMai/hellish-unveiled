package com.hellish.ui.view;

import com.badlogic.gdx.math.Interpolation;
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
import com.hellish.ui.widget.CharacterInfo;

public class GameView extends Table{
	private CharacterInfo playerInfo;
	private CharacterInfo enemyInfo;
	private Label popUpLabel;
	
	public GameView(GameModel model, Skin skin) {
		super(skin);
		setFillParent(true);
        
        enemyInfo = new CharacterInfo(Drawables.PLAYER, skin);
        enemyInfo.getColor().a = 0;
        add(enemyInfo).row();
        
        Table table = new Table();
        table.background(skin.getDrawable(Drawables.FRAME_BGD.getAtlasKey()));
        popUpLabel = new Label("", skin.get(Labels.FRAME.getSkinKey(), LabelStyle.class));
        popUpLabel.setAlignment(Align.topLeft);
        popUpLabel.setWrap(true);
        table.add(popUpLabel).expand().fill().pad(14).top().row();
        table.getColor().a = 0;
        add(table).expand().width(130).height(90).top().row();
        
		playerInfo = new CharacterInfo(Drawables.PLAYER, skin);
        add(playerInfo);
        
        //Data binding
        model.onPropertyChange("playerLife", lifePercentage -> playerLife((float) lifePercentage));
        model.onPropertyChange("enemyLife", lifePercentage -> enemyLife((float) lifePercentage));
        model.onPropertyChange("popUpText", popUpInfo -> popUp((String) popUpInfo));
        model.onPropertyChange("enemyType", type -> {
            if (type.equals("wolf")) {
                showEnemyInfo(Drawables.WOLF, model.getEnemyLife(), 0f);
            }
        });
	}
	
	 public void playerLife(float percentage) {
		 playerInfo.life(percentage);       
	 }
	 
	 public void playerMana(float percentage) {
		 playerInfo.mana(percentage);
	 }

	 public void enemyLife(float percentage) {
		 resetFadeOutDelay(enemyInfo);
		 enemyInfo.life(percentage);
	 }
	 
	 public void enemyMana(float percentage) {
		 resetFadeOutDelay(enemyInfo);
		 enemyInfo.mana(percentage);
	 }
	 
	 public void showEnemyInfo(Drawables charDrawable, float lifePercentage, float manaPercentage) {
		 enemyInfo.character(charDrawable);
		 enemyInfo.life(lifePercentage, 0);
		 enemyInfo.mana(manaPercentage, 0);
		 
		 if(enemyInfo.getColor().a == 0) {
			 enemyInfo.clearActions();
			 enemyInfo.addAction(Actions.sequence(Actions.fadeIn(1, Interpolation.bounceIn),
					 Actions.delay(2, Actions.fadeOut(0.5f))));
		 } else {
			 resetFadeOutDelay(enemyInfo);
		 }
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
            popUpLabel.getParent().addAction(Actions.sequence(Actions.fadeIn(0.2f), Actions.delay(4, Actions.fadeOut(0.75f))));
        } else {
            resetFadeOutDelay(popUpLabel.getParent());
        }
    }
}
