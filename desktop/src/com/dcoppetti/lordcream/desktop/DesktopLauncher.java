package com.dcoppetti.lordcream.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dcoppetti.lordcream.IceCreamOverlordGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = IceCreamOverlordGame.W_WIDTH;
		config.height = IceCreamOverlordGame.W_HEIGHT;
		config.foregroundFPS = IceCreamOverlordGame.FPS;
		new LwjglApplication(new IceCreamOverlordGame(), config);
	}
}
