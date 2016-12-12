package net.kagani;

import java.util.Scanner;

import net.kagani.executor.LoginExecutorManager;
import net.kagani.login.GameWorld;
import net.kagani.login.Login;
import net.kagani.network.LoginServerChannelManager;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class LoginEngine {

	/**
	 * Whether shutdown has been started.
	 */
	public static boolean shutdown;

	public static void main(String[] args) throws Throwable {
		if (args.length < 2) {
			System.out.println("USE: debug(boolean) hosted(boolean)");
			return;
		}
		Settings.DEBUG = Boolean.parseBoolean(args[0]);
		Settings.HOSTED = Boolean.parseBoolean(args[1]);
		Settings.init();
		long currentTime = Utils.currentTimeMillis();
		Logger.log("LoginEngine", "Starting login core...");
		Login.init();
		Logger.log("LoginEngine", "Starting executors...");
		LoginExecutorManager.init();
		Logger.log("LoginEngine", "Initiating Login Server Channel Manager...");
		try {
			LoginServerChannelManager.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("LoginEngine",
					"Failed Initiating Login Server Channel Manager. Shutting down...");
			System.exit(1);
			return;
		}
		Logger.log("LoginEngine", "Initiating Website Channels Manager...");
		Logger.log("LoginEngine",
				"Login server took "
						+ (Utils.currentTimeMillis() - currentTime)
						+ " milli seconds to launch.");

		GameWorld world = Login.getWorld(1);
		LoginServerChannelManager.sendReliablePacket(
				world,
				LoginChannelsPacketEncoder.encodeConsoleMessage(
						"Login Engine connected.").getBuffer());
		Scanner scanner = new Scanner(System.in);
		for (String line = scanner.nextLine(); line != null; line = scanner
				.nextLine()) {
			try {
				handleCommand(line);
			} catch (Throwable t) {
				Logger.handle(t);
			}
		}
		scanner.close();

	}

	private static void handleCommand(String cmd) {
		if (cmd.startsWith("shutdown ") || cmd.startsWith("update ")) {
			String[] args = cmd.split("\\s");
			if (args.length < 2) {
				Logger.log("Console", "Usage " + cmd + " [delay]");
				return;
			}
			shutdown(Integer.parseInt(args[1]), false);
		} else
			Logger.log("Console", "The command '" + cmd + "' does not exist.");
	}

	/**
	 * Perform's safe shutdown of login server.
	 */
	public static void shutdown(int delay, boolean force) {
		if (force) {
			Logger.log("LoginEngine", "Accepted request, starting process...");
			Logger.log("LoginEngine", "Completed, ready for reboot");
		} else {
			if (shutdown)
				return;
			shutdown = true;
			Logger.log("LoginEngine", "Full safe shutdown has been started!");

			Logger.log("LoginEngine", "Sending shutdown requests to worlds...");
			for (int i = 0; i < Login.getWorldsSize(); i++) {
				GameWorld world = Login.getWorld(i);
				if (world == null)
					continue;
				LoginServerChannelManager.sendReliablePacket(
						world,
						LoginChannelsPacketEncoder.encodeWorldShutdownRequest(
								delay).getBuffer());
			}

			for (int cycle = 0;; cycle++) {
				int total = Login.getTotalOnline();
				Logger.log("LoginEngine", "Total: " + total + " -> Cycle: "
						+ cycle);
				if (total <= 0)
					break;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {

				}
			}
			Logger.log("LoginEngine", "Awaiting worker shutdown...");
			LoginExecutorManager.shutdown(true);
			Logger.log("LoginEngine", "Shutting down network...");
			LoginServerChannelManager.shutdown();
			Logger.log("LoginEngine", "Shutting down login core...");
			Login.shutdown();
			Logger.log("LoginEngine", "Done...");
			System.exit(0);
		}
	}
}