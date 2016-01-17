package com.dcoppetti.lordcream;

public class LevelData {

	private String levelName;
	private float playerTime;
	private float bestTime;
	private boolean complete = false;
	private boolean completeWithBestTime = false;
	
	public LevelData(String levelName) {
		this.levelName = levelName;
	}
	
	public String getLevelName() {
		return this.levelName;
	}
	
	public boolean isCompleteWithBestTime() {
		return this.completeWithBestTime;
	}

	public boolean isComplete() {
		return this.complete;
	}
	
	public void finishLevel(float time) {
		setPlayerTime(time);
		complete = true;
	}
	
	private void setPlayerTime(float time) {
		this.playerTime = time;
		if(time < bestTime) {
			bestTime = time;
			completeWithBestTime = true;
		}
	}
	
	public float getPlayerTime() {
		return playerTime;
	}

	public float getBestTime() {
		return bestTime;
	}

}
