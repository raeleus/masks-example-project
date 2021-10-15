package com.ray3k.liftoff.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.ray3k.liftoff.Core;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
		@Override
		public GwtApplicationConfiguration getConfig () {
			GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);
			config.padHorizontal = 0;
			config.padVertical = 0;
			return config;
		}

		@Override
		public ApplicationListener createApplicationListener () { 
			return new Core();
		}
}
