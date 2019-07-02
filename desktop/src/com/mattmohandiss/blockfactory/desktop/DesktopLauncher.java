package com.mattmohandiss.blockfactory.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mattmohandiss.blockfactory.DeviceType;
import com.mattmohandiss.blockfactory.GameStarter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width *= 2;
		config.height *= 2;
		new LwjglApplication(new GameStarter(DeviceType.desktop), config);
	}
}
