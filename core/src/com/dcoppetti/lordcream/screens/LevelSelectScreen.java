package com.dcoppetti.lordcream.screens;

import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_WIDTH;
import static com.dcoppetti.lordcream.IceCreamOverlordGame.V_HEIGHT;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dcoppetti.lordcream.IceCreamOverlordGame;
import com.dcoppetti.lordcream.Level;
import com.dcoppetti.lordcream.handlers.CameraHandler;
import com.dcoppetti.lordcream.utils.Assets;
import com.dcoppetti.lordcream.utils.GuiCursor;

/**
 * This class is a mess :V
 * no time to explain!
 * 
 * @author Diego Coppetti
 *
 */
public class LevelSelectScreen implements Screen {

	private SpriteBatch batch;
	private Viewport viewport;
	private BitmapFont font;
	private BitmapFont font2;
	private Stage stage;
	private Color backColor = Color.BLACK;
	private Color fontColor = Color.WHITE;
	private float scale = 3.5f;
	
	private GuiCursor cursor;
	
	private Label timeLabel;
	private String timeLabelText = "best";
	private Label bestLabel;
	private String bestLabelText = "par";
	private float labelPad = 30;
	private float timeLabelXPos = V_WIDTH*1.8f;
	private float bestLabelXPos = V_WIDTH*2.6f;
	
	private IceCreamOverlordGame game;
	private OrthographicCamera cam;
	private float cursorMoveBy;

	public LevelSelectScreen(IceCreamOverlordGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		font = Assets.loadBitmapFont("fonts/minimal-4.fnt");
		font2 = Assets.loadBitmapFont("fonts/megaman-style.fnt");
		font2.getData().setScale(0.5f);
		font2.setColor(Color.WHITE);
		viewport = new FitViewport(V_WIDTH * scale, V_HEIGHT * scale);
		batch = new SpriteBatch();
		stage = new Stage(viewport, batch);

		Array<TextureRegion> regions = Assets.getAtlasRegions(IceCreamOverlordGame.SPRITES_PACK_FILE, "cone", "-", 1);

		cursorMoveBy = labelPad;
		float cursorInitX = 0;
		float cursorInitY = 0;
		cursor = new GuiCursor(regions.first(), game.levels.size, cursorMoveBy, cursorInitX, cursorInitY);
		cursor.getSprite().setScale(2f);
		font.getData().setScale(1f);

		cam = (OrthographicCamera) stage.getCamera();
		
		createLevelLabels();
		
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Keys.UP) {
					cursor.moveUp();
				}
				if(keycode == Keys.DOWN) {
					cursor.moveDown();
				}
				if(keycode == Keys.W) {
					cursor.moveUp();
				}
				if(keycode == Keys.S) {
					cursor.moveDown();
				}
				if(keycode == Keys.SPACE || keycode == Keys.ENTER) {
					if(game.levels.get(cursor.getCurrentRow()-1).getLevelData().isUnlocked()) {
						int random = MathUtils.random(1);
						if(random == 0) {
							Assets.stopAllMusic();
							Assets.playMusic(IceCreamOverlordGame.levelMusic);
						} else {
							Assets.stopAllMusic();
							Assets.playMusic(IceCreamOverlordGame.levelMusic2);
						}
						game.setPlayScreen(game.levels.get(cursor.getCurrentRow()-1));
					}
				} else if(keycode == Keys.ESCAPE) {
					game.setScreen(new TitleScreen(game));
				}
				return true;
			}
		});
	}

	private void createLevelLabels() {
		Label label;
		Label label2;
		Label label3;
		String labelText;

		float startX = cursor.getSpriteX() + 8;
		float startY = cursor.getSpriteY() + labelPad/6f;

		timeLabel = new Label(timeLabelText, new Label.LabelStyle(font, fontColor));
		timeLabel.setPosition(timeLabelXPos, startY + labelPad);
		bestLabel = new Label(bestLabelText, new Label.LabelStyle(font, fontColor));
		bestLabel.setPosition(bestLabelXPos, startY + labelPad);
		
		stage.addActor(timeLabel);
		stage.addActor(bestLabel);
		
		Color labelColor = fontColor;

		for(Level level : game.levels) {
			if(!level.getLevelData().isUnlocked()) {
				label = new Label("??????", new Label.LabelStyle(font, fontColor));
				label.setPosition(startX, startY);
				stage.addActor(label);
				startY -= labelPad;
				continue;
			}
			if(level.getLevelData().isCompleteWithBestTime()) {
				labelColor = Color.GOLD;
			} else {
				labelColor = fontColor;
			}
			labelText = level.getLevelData().getLevelName();
			label = new Label(labelText, new Label.LabelStyle(font, labelColor));
			label.setPosition(startX, startY);
			stage.addActor(label);
			label2 = new Label(level.getLevelData().getBestTime(), new Label.LabelStyle(font, labelColor));
			label2.setPosition(timeLabelXPos, label.getY());
			stage.addActor(label2);
			label3 = new Label(level.getLevelData().getParTime(), new Label.LabelStyle(font, labelColor));
			label3.setPosition(bestLabelXPos, label2.getY());
			stage.addActor(label3);
			startY -= labelPad;
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		cam.position.set(cursor.getSpriteX() - labelPad/2 + V_WIDTH/2*scale, cursor.getSpriteY(), 0);
		cam.update();
		
		
		stage.act(delta);
		stage.draw();

		batch.begin();
		cursor.render(batch);
		batch.end();

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
