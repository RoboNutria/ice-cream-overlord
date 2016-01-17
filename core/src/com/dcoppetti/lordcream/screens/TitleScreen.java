package com.dcoppetti.lordcream.screens;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_WIDTH;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_HEIGHT;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dcoppetti.lordcream.IceCreamOverlordGame;
import com.dcoppetti.lordcream.utils.Assets;
import com.dcoppetti.lordcream.utils.GuiCursor;

/**
 * @author Diego Coppetti
 *
 */
public class TitleScreen implements Screen {
	
	private SpriteBatch batch;
	private Viewport viewport;
	private BitmapFont font;
	private BitmapFont font2;
	private Stage stage;
	private Color backColor = Color.BLACK;
	private Color fontColor = Color.WHITE;
	private float scale = 3.5f;
	
	private GuiCursor cursor;
	
	// Main menu stuff
	private Table table;
	private Label titleLabel;
	private String titleLabelText = "Ice Cream Overlord\n Space Conquest";

	private Label playLabel;
	private String playLabelText = "PLAY";

	private Label quitLabel;
	private String quitLabelText = "QUIT";

	private Label creditsLabel;
	private String creditsLabelText = "By Diego Coppetti & Marccio Silva";

	private Label additionalLabel;
	private String additionalLabelText = "Keys: WASD, Space and Esc. Take it easy!";
	
	private IceCreamOverlordGame game;
	
	public TitleScreen(IceCreamOverlordGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		font = Assets.loadBitmapFont("fonts/minimal-4.fnt");
		font.getData().setScale(1f);
		font2 = Assets.loadBitmapFont("fonts/megaman-style.fnt");
		font2.getData().setScale(0.25f);
		viewport = new FitViewport(V_WIDTH * scale, V_HEIGHT * scale);
		batch = new SpriteBatch();
		stage = new Stage(viewport, batch);

		Array<TextureRegion> regions = Assets.getAtlasRegions(IceCreamOverlordGame.SPRITES_PACK_FILE, "cone", "-", 1);

		table = new Table();
		if(IceCreamOverlordGame.DEBUG_MODE) table.setDebug(true);
		table.setFillParent(true);
		
		titleLabel = new Label(titleLabelText, new Label.LabelStyle(font, fontColor));
		playLabel = new Label(playLabelText, new Label.LabelStyle(font, fontColor));
		quitLabel = new Label(quitLabelText, new Label.LabelStyle(font, fontColor));
		creditsLabel = new Label(creditsLabelText, new Label.LabelStyle(font2, fontColor));
		additionalLabel = new Label(additionalLabelText, new Label.LabelStyle(font2, fontColor));
		

		table.add(titleLabel).expandX().padTop(20);
		table.row().padTop(160);
		table.add(playLabel).expandX().padTop(150);
		table.row();
		table.add(quitLabel).expandX();
		table.row().padTop(60);
		table.add(additionalLabel).expandX();
		table.row();
		table.add(creditsLabel).padTop(40);

		float playButtonY = ((V_HEIGHT*scale)/2f)-60;
		float playButtonX = ((V_WIDTH*scale)/2f)-60;
		float cursorMoveBy = 30;

		cursor = new GuiCursor(regions.first(), 2, cursorMoveBy, playButtonX, playButtonY);
		cursor.getSprite().setScale(2f);
		
		stage.addActor(table);
		
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Keys.W) {
					cursor.moveUp();
				} else if(keycode == Keys.S) {
					cursor.moveDown();
				} else if(keycode == Keys.SPACE || keycode == Keys.ENTER) {
					if(cursor.getCurrentRow() == 1) {
						game.setScreen(new LevelSelectScreen(game));
					} else {
						Gdx.app.exit();
					}
				} else if(keycode == Keys.ESCAPE) {
						Gdx.app.exit();
				}
				return true;
			}
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		cursor.render(batch);
		batch.end();
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
		Gdx.input.setInputProcessor(null);
	}

}
