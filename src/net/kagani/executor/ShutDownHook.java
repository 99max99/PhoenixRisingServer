package net.kagani.executor;

import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.login.Login;
import net.kagani.utils.Logger;

public class ShutDownHook extends Thread {

	private static final ShutDownHook INSTANCE = new ShutDownHook();

	private ShutDownHook() {

	}

	@Override
	public void run() {
		try {
			Logger.log("ShutDownHook", "Shutting down the server.");
			Logger.log("ShutDownHook", "Preparing players for shutdown.");
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted()
						|| player.hasFinished())
					continue;
				Login.saveFiles();
				player.getPackets().sendLogout(false);
			}
			Login.saveFiles();
		} catch (Exception e) {
			Logger.log("ShutDownHook", "Error while shutting down.");
		}
	}

	public static ShutDownHook get() {
		return INSTANCE;
	}
}