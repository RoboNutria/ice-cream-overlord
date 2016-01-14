package com.dcoppetti.lordcream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dcoppetti.lordcream.entities.Overlord;

/**
 * @author Diego Coppetti
 *
 */
public class Hud {

	private Stage stage;
	private Viewport viewport;
	private BitmapFont font;

	// debug stuff
	private Label playerStateLabel;
	private Label playerXLabel;
	private Label playerYLabel;
	
	private float scale = 2;
	
	public Hud(SpriteBatch batch) {

		viewport = new FitViewport(IceCreamOverlordGame.V_WIDTH * scale, IceCreamOverlordGame.V_HEIGHT * scale, new OrthographicCamera());
		stage = new Stage(viewport, batch);
		font = new BitmapFont();

		Table table = new Table();
		table.top();
		table.setFillParent(true);

		if(IceCreamOverlordGame.DEBUG_MODE) {
			Color debugColor = Color.GOLD;
			playerStateLabel = new Label("P_State: ", new Label.LabelStyle(font, debugColor));
			playerXLabel = new Label("P_X: ", new Label.LabelStyle(font, debugColor));
			playerYLabel = new Label("P_Y: ", new Label.LabelStyle(font, debugColor));
			table.add(playerStateLabel).fillX().expandX().padRight(viewport.getWorldWidth()-150).padTop(10);
			table.row();
			table.add(playerXLabel).fillX().expandX().padRight(viewport.getWorldWidth()-150).padTop(10);
			table.row();
			table.add(playerYLabel).fillX().expandX().padRight(viewport.getWorldWidth()-150).padTop(10);
		}
		
		stage.addActor(table);
	}
	
	public void update(Overlord overlord) {
		stage.act();
		if(IceCreamOverlordGame.DEBUG_MODE) {
			playerStateLabel.setText("P_State: " + overlord.getState().toString());
			playerXLabel.setText("P_X: " + overlord.getBody().getPosition().x);
			playerYLabel.setText("P_Y: " + overlord.getBody().getPosition().y);
		}
	}
	
	public void render() {
		stage.draw();
	}
	
	public void dispose() {
		stage.dispose();
		font.dispose();
	}

}
