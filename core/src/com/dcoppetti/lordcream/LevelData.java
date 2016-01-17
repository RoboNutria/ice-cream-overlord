package com.dcoppetti.lordcream;


public class LevelData {

	private boolean unlocked = true;
	private String levelName;
	private String bestTime;
	private String parTime;
	private boolean completeWithBestTime = false;
	
	public LevelData() {}
	
	public LevelData(String levelName) {
		this.levelName = levelName;
	}
	
	public String getLevelName() {
		return this.levelName;
	}
	
	public boolean isCompleteWithBestTime() {
		return this.completeWithBestTime;
	}
	
	public void setBestTime(String currentTime) {
		float currentTimeMillis = getMillis(currentTime);
		float bestTimeMillis = getMillis(bestTime);
		float parTimeMillis = getMillis(parTime);
		if(bestTimeMillis == 0) {
			bestTime = currentTime;
		} else if(currentTimeMillis < bestTimeMillis) {
			bestTime = currentTime;
		}
		if(currentTimeMillis < parTimeMillis) {
			parTime = bestTime;
			completeWithBestTime = true;
		}
	}
	
	public float getMillis(String time) {
		String[] s = time.split(":");
		float minsMillis = (Integer.valueOf(s[0]) * 60000);
		float secsMillis = (Integer.valueOf(s[1]) * 1000);
		float millis = (Integer.valueOf(s[2]));
		return minsMillis + secsMillis + millis;
	}
	
	public void setParTime(String time) {
		this.parTime = time;
	}

	public boolean isUnlocked() {
		return unlocked;
	}
	
	public void setUnlocked(boolean b) {
		this.unlocked = b;
	}
	
	public String getBestTime() {
		if(bestTime == null) {
			bestTime = "00:00:00";
		}
		return bestTime;
	}

	public String getParTime() {
		if(parTime == null) {
			parTime = "00:00:00";
		}
		return parTime;
	}

}
