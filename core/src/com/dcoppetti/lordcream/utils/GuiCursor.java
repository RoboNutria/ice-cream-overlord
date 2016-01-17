package com.dcoppetti.lordcream.utils;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple sprite cursor utility that moves up and down
 * 
 * @author Diego Coppetti
 *
 */
public class GuiCursor {
	
	private Sprite sprite;
	private int currentRow = 1;
	private int rows;
	private float moveBy;
	private float initY;
	
	public GuiCursor(TextureRegion region, int rows, float moveBy, float x, float y) {
		this.sprite = new Sprite(region);
		this.sprite.setPosition(x, y);
		this.moveBy = moveBy;
		this.rows = rows;
		this.initY = y;
	}
	
	public void moveDown() {
		currentRow++;
		if(currentRow > rows) {
			currentRow = 1;
			sprite.setPosition(sprite.getX(), initY);
			return;
		}
		sprite.setPosition(sprite.getX(), sprite.getY() - moveBy);
	}

	public void moveUp() {
		currentRow--;
		if(currentRow < 1) {
			currentRow = rows;
			sprite.setPosition(sprite.getX(), sprite.getY() - (moveBy * (rows-1)));
			return;
		}
		sprite.setPosition(sprite.getX(), sprite.getY() + moveBy);
	}
	
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public Sprite getSprite() {
		return this.sprite;
	}
	
	public int getCurrentRow() {
		return currentRow;
	}

	public float getSpriteX() {
		return this.sprite.getX();
	}

	public float getSpriteY() {
		return this.sprite.getY();
	}

}
