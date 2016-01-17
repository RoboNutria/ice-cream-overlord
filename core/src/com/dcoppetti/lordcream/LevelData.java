package com.dcoppetti.lordcream;

public class LevelData {

	private boolean unlocked = true;
	private String levelName;
	private float playerTime;
	private float bestTime;
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
	
	public void setPlayerTime(float time) {
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

	public boolean isUnlocked() {
		return unlocked;
	}
	
	public void setUnlocked(boolean b) {
		this.unlocked = b;
	}

}
