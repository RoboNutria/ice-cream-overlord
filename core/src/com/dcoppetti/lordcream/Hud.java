package com.dcoppetti.lordcream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dcoppetti.lordcream.entities.Overlord;
import com.dcoppetti.lordcream.screens.PlayScreen;
import com.dcoppetti.lordcream.utils.Assets;

/**
 * @author Marccio Silva, Diego Coppetti
 *
 */
public class Hud {

	private Stage stage;
	private Viewport viewport;
	private BitmapFont font;
	private short livesHud = 3;
	private short rescueAmount = 0;
	private Table table;
	private short chibiToRescue;
	private Color fontColor;
	
	private Array<Label> messages;

	// debug stuff
	private Label playerStateLabel;
	private Label playerXLabel;
	private Label playerYLabel;
	private Label rescueAmountLabel;
	
	private float scale = 2;
	private boolean deleteMessages = false;
	
	public Hud(SpriteBatch batch) {
		
		this.chibiToRescue = PlayScreen.chibiAmount;
		viewport = new FitViewport(IceCreamOverlordGame.V_WIDTH * scale, IceCreamOverlordGame.V_HEIGHT * scale, new OrthographicCamera());
		stage = new Stage(viewport, batch);
		font = Assets.getBitmapFont("fonts/minimal-4.fnt");
		font.getData().setScale(0.5f);
		table = new Table();
		table.top();
		table.setFillParent(true);
		messages = new Array<Label>();

		fontColor = Color.WHITE;
		if(IceCreamOverlordGame.DEBUG_MODE) {
			playerStateLabel = new Label("P_State: ", new Label.LabelStyle(font, fontColor));
			playerXLabel = new Label("P_X: ", new Label.LabelStyle(font, fontColor));
			playerYLabel = new Label("P_Y: ", new Label.LabelStyle(font, fontColor));
		}
		rescueAmountLabel = new Label(Short.toString(rescueAmount) + "/" + Short.toString(chibiToRescue), new Label.LabelStyle(font, fontColor));
		//hearts
		table.row();
		for (int i = 0 ; i < 3 ; i++) table.add(new Image(Assets.loadTexture("textures/heart-full.png"))).padTop(10);
		//chibi icecreams rescued
		table.add(new Image(Assets.loadTexture("textures/chibi-icon.png"))).padTop(10).padLeft(viewport.getWorldWidth() - 100);
		table.add(rescueAmountLabel).padLeft(10).padTop(10);
		
		stage.addActor(table);
	}
	
	public void update(Overlord overlord) {
		stage.act();
		if(IceCreamOverlordGame.DEBUG_MODE) {
			playerStateLabel.setText("P_State: " + overlord.getState().toString());
			playerXLabel.setText("P_X: " + overlord.getBody().getPosition().x);
			playerYLabel.setText("P_Y: " + overlord.getBody().getPosition().y);
		}
		short tmpLives = overlord.getLives();
		short tmpRescueAmount = PlayScreen.rescueAmount;
		if (tmpLives != livesHud || tmpRescueAmount != rescueAmount) {
			table.clear();
			livesHud = tmpLives;
			rescueAmount = tmpRescueAmount;
			for (int i = 0 ; i < livesHud ; i++) table.add(new Image(Assets.loadTexture("textures/heart-full.png"))).padTop(10);
			for (int i = livesHud ; i < 3 ; i++) table.add(new Image(Assets.loadTexture("textures/heart-empty.png"))).padTop(10);
			table.add(new Image(Assets.loadTexture("textures/chibi-icon.png"))).padTop(10).padLeft(viewport.getWorldWidth() - 100);
			rescueAmountLabel.setText(Short.toString(rescueAmount) + "/" + Short.toString(chibiToRescue));
			table.add(rescueAmountLabel).padLeft(10).padTop(10);
		}
		if(deleteMessages) {
			deleteMessages = false;
			for(Label l : messages) {
				l.remove();
			}
			messages.clear();
		}
	}
	
	public void render() {
		stage.draw();
	}
	
	public void dispose() {
		stage.dispose();
	}

	public Stage getStage() {
		return stage;
	}
	
	public short getRescueAmount() {
		return rescueAmount;
	}
	
	public void displayMessage(String text, Vector2 position) {
		Label message = new Label(text, new Label.LabelStyle(font, fontColor));
		message.setPosition(position.x, position.y);
		messages.add(message);
		stage.addActor(message);
	}

	public void displayMessage(String text) {
		Label message = new Label(text, new Label.LabelStyle(font, fontColor));
		message.setPosition(stage.getWidth()/4, stage.getHeight()*0.9f);
		messages.add(message);
		stage.addActor(message);
	}
	
	public void clearMessages() {
		this.deleteMessages = true;
	}

}
