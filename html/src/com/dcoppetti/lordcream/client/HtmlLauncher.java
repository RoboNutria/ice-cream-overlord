package com.dcoppetti.lordcream.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.dcoppetti.lordcream.IceCreamOverlordGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
        		int width = IceCreamOverlordGame.W_WIDTH;
        		int height = IceCreamOverlordGame.W_HEIGHT;
                return new GwtApplicationConfiguration(width, height);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new IceCreamOverlordGame();
        }
}