package com.dcoppetti.lordcream.utils;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * @author Diego Coppetti
 *
 */
public class Assets {
	
	private static HashMap<String, TextureAtlas> atlasMap = new HashMap<String, TextureAtlas>();
	private static HashMap<String, Texture> texturesMap = new HashMap<String, Texture>();
	private static HashMap<String, Sound> soundMap = new HashMap<String, Sound>();
	private static HashMap<String, Music> musicMap = new HashMap<String, Music>();
	private static HashMap<String, BitmapFont> fontsMap = new HashMap<String, BitmapFont>();

	private static float musicVolume = 1f;
	private static float soundVolume = 0.8f;
	private static boolean musicEnabled = true;
	private static boolean soundEnabled = true;
	private static boolean musicLooping = true;

	private Assets() {}
	
	public static void loadAllAssets() {
		// TODO: all the atlas textures
		// TODO: all textures
		// TODO: all the music
		// TODO: all the sounds
	}
	
	public static TextureAtlas loadAtlas(String filename) {
		return loadAtlas(filename, false);
	}

	public static TextureAtlas loadAtlas(String filename, boolean sort) {
		if(atlasMap.containsKey(filename)) {
			return atlasMap.get(filename);
		}
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(filename));
		if(sort) {
			atlas.getRegions().sort(new RegionComparator());
		}
		atlasMap.put(filename, atlas);
		return atlas;
	}

	public static Array<TextureRegion> getAtlasRegions(String atlasFilename, String namePrefix, String separator, int startIndex) {
		TextureAtlas atlas = atlasMap.get(atlasFilename);
		return getAtlasRegions(atlas, namePrefix, separator, startIndex);
	}

	private static Array<TextureRegion> getAtlasRegions(TextureAtlas atlas, String namePrefix, String separator, int startIndex) {
		Array<TextureRegion> regions = new Array<TextureRegion>();
		String name = namePrefix + separator + startIndex;
		AtlasRegion aRegion = atlas.findRegion(name);
		while(aRegion != null) {
			regions.add(aRegion); 
			startIndex++;
			name = namePrefix + separator + startIndex;
			aRegion = atlas.findRegion(name);
		}
		return regions;
	}

	public static BitmapFont loadBitmapFont(String filename) {
		if(fontsMap.containsKey(filename)) {
			return fontsMap.get(filename);
		}
		BitmapFont font = new BitmapFont(Gdx.files.internal(filename));
		fontsMap.put(filename, font);
		return font;
	}

	public static Texture loadTexture(String filename) {
		if(texturesMap.containsKey(filename)) {
			return texturesMap.get(filename);
		}
		Texture texture = new Texture(Gdx.files.internal(filename));
		texturesMap.put(filename, texture);
		return texture;
	}
	
	public static Music loadMusic(String filename) {
		if(musicMap.containsKey(filename)) return musicMap.get(filename);
		FileHandle musicFile = Gdx.files.internal(filename);
		Music music = Gdx.audio.newMusic(musicFile);
		music.setVolume(musicVolume);
		music.setLooping(musicLooping);
		musicMap.put(filename, music);
		return music;
	}

	public static void loadSound(String filename) {
		FileHandle soundFile = Gdx.files.internal(filename);
		Sound sound = Gdx.audio.newSound(soundFile);
		soundMap.put(filename, sound);
	}

	public static void playSound(String filename) {
		if(!soundEnabled) return;
		Sound sound = soundMap.get(filename);
		if(sound == null) {
			throw new IllegalArgumentException("Sound file: " + filename + " has not been loaded!");
		}
		sound.play(soundVolume);
	}

	public static void playMusic(String filename) {
		if(!musicEnabled) return;
		Music music = musicMap.get(filename); 
		if(music == null) {
			throw new IllegalArgumentException("Music file: " + filename + " has not been loaded!");
		}
		stopMusic(music);
		music.play();
	}
	
	public static void stopAllMusic() {
		if(musicMap.size() > 0) {
			for(Music m : musicMap.values()) {
				if(!m.isPlaying()) continue;
				m.stop();
			}
		}
	}

	public static void pauseAllMusic() {
		if(musicMap.size() > 0) {
			for(Music m : musicMap.values()) {
				if(!m.isPlaying()) continue;
				m.pause();
			}
		}
	}

	public static void resumeAllMusic() {
		if(musicMap.size() > 0) {
			for(Music m : musicMap.values()) {
				if(m.getPosition() < 0.2f) continue;
				m.play();
			}
		}
	}

	public static void resumeMusic(String musicFile) {
		Music music = musicMap.get(musicFile);
		if(music == null) return;
		if(music.isPlaying()) return;
		music.play();
	}

	private static void stopMusic(Music music) {
		if(music.isPlaying()) music.stop();
	}

	public static void stopMusic(String musicFile) {
		Music music = musicMap.get(musicFile);
		if(music == null) return;
		if(!music.isPlaying()) return;
		music.stop();
	}

	public static void pauseMusic(String musicFile) {
		Music music = musicMap.get(musicFile);
		if(music == null) return;
		if(!music.isPlaying()) return;
		music.pause();
	}

	private static void stopMusic() {
		if(musicMap.size() > 0) {
			for(Music m : musicMap.values()) {
				if(m.isPlaying()) {
					m.stop();
					break;
				}
			}
		}
	}

	public static void setMusicVolume(float volume) {
		if(volume < 0 || volume > 1f) {
			throw new IllegalArgumentException("The volume must be inside the range: [0,1]");
		}
		musicVolume = volume;
		if(musicMap.size() > 0) {
			for(Music m : musicMap.values()) {
				if(m.isPlaying()) {
					m.setVolume(musicVolume);
					break;
				}
			}
		}

	}
	
	public static void setSoundVolume(float volume) {
		if(volume < 0 || volume > 1f) {
			throw new IllegalArgumentException("The volume must be inside the range: [0,1]");
		}
		soundVolume = volume;
	}

	public static void setMusicEnabled(boolean enabled) {
		musicEnabled = enabled;
		
		if(!musicEnabled) {
			stopMusic();
		}
	}

	public static void setSoundEnabled(boolean enabled) {
		soundEnabled = enabled;
	}

	public static TextureAtlas getAtlas(String filename) {
		return atlasMap.get(filename);
	}

	public static Texture getTexture(String filename) {
		return texturesMap.get(filename);
	}

	public static BitmapFont getBitmapFont(String filename) {
		return fontsMap.get(filename);
	}
	
	public static Music getMusic(String filename) {
		return musicMap.get(filename);
	}

	public static void dispose() {
		disposeAtlasMap();
		disposeTexturesMap();
		disposeSoundMap();
		disposeMusicMap();
		disposeFontsMap();
	}

	private static void disposeAtlasMap() {
		if(atlasMap.size() > 0) {
			for(TextureAtlas ta : atlasMap.values()) {
				ta.dispose();
			}
		}
		atlasMap.clear();
		atlasMap = null;
	}

	public static void disposeFontsMap() {
		if(fontsMap.size() > 0) {
			for(BitmapFont f : fontsMap.values()) {
				f.dispose();
			}
		}
		fontsMap.clear();
		fontsMap = null;
	}

	public static void disposeTexturesMap() {
		if(texturesMap.size() > 0) {
			for(Texture t : texturesMap.values()) {
				t.dispose();
			}
		}
		texturesMap.clear();
		texturesMap = null;
	}

	public static void disposeSoundMap() {
		if(soundMap.size() > 0) {
			for(Sound s : soundMap.values()) {
				s.dispose();
			}
		}
		soundMap.clear();
		soundMap = null;
	}

	public static void disposeMusicMap() {
		if(musicMap.size() > 0) {
			for(Music m : musicMap.values()) {
				m.dispose();
			}
		}
		musicMap.clear();
		musicMap = null;
	}
	
	public static void disposeAtlas(String filename) {
		TextureAtlas atlas = atlasMap.get(filename);
		if(atlas == null) return;
		atlas.dispose();
	}

	public static void disposeMusic(String filename) {
		Music music = musicMap.get(filename);
		if(music == null) return;
		music.stop();
		music.dispose();
	}

	public static void disposeSound(String filename) {
		Sound sound = soundMap.get(filename);
		if(sound == null) return;
		sound.stop();
		sound.dispose();
	}

}
