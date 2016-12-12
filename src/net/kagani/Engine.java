package net.kagani;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import net.kagani.cache.Cache;
import net.kagani.cache.filestore.store.Index;
import net.kagani.cache.loaders.BodyDefinitions;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.executor.GameExecutorManager;
import net.kagani.executor.PlayerHandlerThread;
import net.kagani.executor.ShutDownHook;
import net.kagani.game.World;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.map.bossInstance.BossInstanceHandler;
import net.kagani.game.npc.combat.CombatScriptsHandler;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.FishingSpotsHandler;
import net.kagani.game.player.content.FriendsChat;
import net.kagani.game.player.content.clans.ClansManager;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.controllers.ControlerHandler;
import net.kagani.game.player.cutscenes.CutscenesHandler;
import net.kagani.game.player.dialogues.DialogueHandler;
import net.kagani.network.GameChannelsManager;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.utils.Censor;
import net.kagani.utils.CharBackup;
import net.kagani.utils.ItemDestroys;
import net.kagani.utils.ItemExamines;
import net.kagani.utils.ItemSpawns;
import net.kagani.utils.ItemWeights;
import net.kagani.utils.Logger;
import net.kagani.utils.MapArchiveKeys;
import net.kagani.utils.MapAreas;
import net.kagani.utils.MusicEffects;
import net.kagani.utils.MusicHints;
import net.kagani.utils.NPCCombatDefinitionsL;
import net.kagani.utils.NPCDrops;
import net.kagani.utils.NPCExamines;
import net.kagani.utils.NPCSpawns;
import net.kagani.utils.ObjectExamines;
import net.kagani.utils.ObjectSpawns;
import net.kagani.utils.SRCBackup;
import net.kagani.utils.SerializationUtilities;
import net.kagani.utils.ShopsHandler;
import net.kagani.utils.Utils;
import net.kagani.utils.huffman.Huffman;
import net.kagani.utils.sql.Gero;
import net.kagani.utils.sql.Highscores;

public class Engine {

	/**
	 * @author: Dylan Page
	 * @author: 
	 */

	/**
	 * Whether shutdown has been started
	 */
	public static volatile boolean shutdown;
	/**
	 * Time when delayed shutdown started.
	 */
	public static volatile long delayedShutdownStart;
	/**
	 * Delay in seconds when delayed shutdown will start.
	 */
	public static volatile int delayedShutdownDelay;

	public static int staffOnline;

	public static long currentTime;

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("USE: worldid(int) debug(boolean) hosted(boolean)");
			return;
		}
		currentTime = Utils.currentTimeMillis();
		Settings.WORLD_ID = Integer.parseInt(args[0]);
		Settings.DEBUG = Boolean.parseBoolean(args[1]);
		Settings.HOSTED = Boolean.parseBoolean(args[2]);
		Logger.log("Engine", "Initiating Settings...");
		Settings.init();
		Logger.log("Engine", "Initiating Cache...");
		Cache.init();
		Logger.log("Engine", "Initiating Shops...");
		ShopsHandler.init();
		Huffman.init();
		BodyDefinitions.init();
		Logger.log("Engine", "Initiating Backups...");
		SRCBackup.init();
		CharBackup.init();
		Logger.log("Engine", "Initiating Data Files...");
		Censor.init();
		Logger.log("Engine", "Initiating Maps...");
		MapArchiveKeys.init();
		MapAreas.init();
		Logger.log("Engine", "Initiating Objects...");
		ObjectSpawns.init();
		ObjectExamines.init();
		Logger.log("Engine", "Initiating NPCs...");
		NPCSpawns.init();
		NPCCombatDefinitionsL.init();
		NPCDrops.init();
		NPCExamines.init();
		Logger.log("Engine", "Initiating Items...");
		ItemExamines.init();
		ItemWeights.init();
		ItemDestroys.init();
		ItemSpawns.init();
		Logger.log("Engine", "Initiating Music Hints...");
		MusicHints.init();
		Logger.log("Engine", "Initiating Music Effects...");
		MusicEffects.init();
		Logger.log("Engine", "Initiating Shops...");
		ShopsHandler.init();
		Logger.log("Engine", "Initiating Grand Exchange...");
		GrandExchange.init();
		Logger.log("Engine", "Initiating Controlers...");
		ControlerHandler.init();
		Logger.log("Engine", "Initiating Boss Instances...");
		BossInstanceHandler.init();
		Logger.log("Engine", "Initiating Fishing Spots...");
		FishingSpotsHandler.init();
		Logger.log("Engine", "Initiating NPC Combat Scripts...");
		CombatScriptsHandler.init();
		Logger.log("Engine", "Initiating Dialogues...");
		DialogueHandler.init();
		Logger.log("Engine", "Initiating Cutscenes...");
		CutscenesHandler.init();
		Logger.log("Engine", "Initiating Friend Chats...");
		FriendsChat.init();
		Logger.log("Engine", "Initiating Clans Manager...");
		ClansManager.init();
		Logger.log("Engine", "Initiating Executor Manager...");
		GameExecutorManager.init();
		Logger.log("Engine", "Initiating World...");
		World.init();
		Logger.log("Engine", "Initiating Region Builder...");
		MapBuilder.init();
		Logger.log("Engine", "Initiating Game Channels Manager...");
		if (Settings.HOSTED) {
			Highscores.init();
			Logger.log("Engine", "Initiating Connection to Highscores...");
		}
		Gero.init();
		ShutDownHook.get().join();
		// Runtime.getRuntime().addShutdownHook(ShutDownHook.get());
		try {
			GameChannelsManager.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("Engine", "Failed Initiating Game Channels Manager. Shutting down...");
			System.exit(1);
			return;
		}
		Logger.log("Engine", "Initiating Login Client Channel Manager...");
		try {
			LoginClientChannelManager.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("Engine", "Failed Initiating Login Client Manager. Shutting down...");
			System.exit(1);
			return;
		}
		if (Settings.HOSTED)
			System.err.println(
					Settings.SERVER_NAME + ":hosted (host: " + docoumentHoster() + ") world " + Settings.WORLD_ID
							+ " - took " + (Utils.currentTimeMillis() - currentTime) + " milli seconds to launch.");
		else
			System.err.println(
					Settings.SERVER_NAME + ":localhost (host: " + docoumentHoster() + ") world " + Settings.WORLD_ID
							+ " - took " + (Utils.currentTimeMillis() - currentTime) + " milli seconds to launch.");
		addAutoSavingTask();
		addCleanMemoryTask();
		addRecalculatePricesTask();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				savePlayers();
			}
		});
		Thread console = new Thread("console thread") {
			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				while (!shutdown) {
					try {
						String cmd = scanner.nextLine();
						if (cmd.startsWith("kick ")) {
							String[] spl = cmd.substring(5).split("\\s\\|\\=\\|\\s");
							Player player = World.getPlayerByDisplayNameAll(spl[0]);
							if (player != null) {
								player.disconnect(true, false);
								System.err.println("Kicked: " + player.getDisplayName());
							} else {
								System.err.println("Player is offline");
							}
						} else if (cmd.equals("players") || cmd.equals("ppl")) {
							System.err.println("There are " + World.getPlayers().size() + " players online");
						} else if (cmd.equals("staff")) {
							for (Player staff : World.getPlayers()) {
								if (staff.getRights() == 0)
									continue;
								staffOnline += 1;
							}
							System.err.println("There are " + staffOnline + " staff online");
							staffOnline = 0;
						} else if (cmd.equals("uptime")) {
							long ticks = currentTime - Utils.currentTimeMillis();
							int seconds = Math.abs((int) (ticks / 1000) % 60);
							int minutes = Math.abs((int) ((ticks / (1000 * 60)) % 60));
							int hours = Math.abs((int) ((ticks / (1000 * 60 * 60)) % 24));
							System.err.println("Uptime: " + hours + (hours != 1 ? " hours" : "hour") + ", " + minutes
									+ (minutes != 1 ? " minutes" : " minute") + " and " + seconds
									+ (seconds != 1 ? " seconds." : " second."));
						} else if (cmd.startsWith("/"))
							World.sendEngineMessage(cmd.substring(1));
						else if (cmd.startsWith("shutdown "))
							shutdown(Integer.parseInt(cmd.substring(9)), true, true);
						else
							Logger.log("Console", "The command '" + cmd + "' does not exist.");
					} catch (Throwable t) {
						Logger.handle(t);
					}
				}
				scanner.close();
			}
		};
		console.setDaemon(true);
		console.start();
		while (!shutdown) {
			try {
				Thread.sleep(1000);
			} catch (Throwable t) {

			}
		}
		processShutdown(true);
	}

	private static InetAddress docoumentHoster() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected static boolean getStatic() {
		if (shutdown)
			return true;
		return false;
	}

	public static boolean shutdown(int delay, boolean force, boolean reboot) {
		if (getStatic()){
			Logger.log("Engine", "Denying access to cheat clients.");
		}
		if (!force) {
			if (shutdown || delayedShutdownStart != 0)
				return false;
		}
		delayedShutdownStart = Utils.currentTimeMillis();
		delayedShutdownDelay = delay;
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay, false);
		}
		for (Player player : World.getLobbyPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay, false);
		}
		Logger.log("Engine", delay + " seconds remaining.");
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				initShutdown(reboot);
			}
		}, delay, TimeUnit.SECONDS);
		return true;
	}

	public static boolean initShutdown(boolean reboot) {
		if (shutdown)
			return false;
		shutdown = true;
		processShutdown(reboot);
		return true;
	}

	private static void processShutdown(boolean reboot) {
		Logger.log("Engine", "Shutdown process started...");
		Logger.log("Engine", "Shutting down game network channels...");
		GameChannelsManager.shutdown();
		for (int cycle = 0;; cycle++) {
			for (Player p : World.getPlayers()) {
				Logger.log("Engine", "Force-logging out: "
						+ (World.getPlayers().size() + World.getLobbyPlayers().size()) + " -> cycle: " + cycle + ".");
				try {
					if (p == null || !p.hasStarted() || p.hasFinished())
						continue;
					byte[] data = SerializationUtilities.tryStoreObject(p);
					if (data == null || data.length <= 0)
						continue;
					PlayerHandlerThread.addSave(p.getUsername(), data);
				} catch (Exception e) {
					Logger.log("Engine", "An error has occured: " + e);
				}
			}
			if (World.getPlayers().size() == 0 && World.getLobbyPlayers().size() == 0)
				break;
			for (Player player : World.getPlayers())
				player.disconnect(true, false);

			for (Player player : World.getLobbyPlayers())
				player.disconnect(true, false);

			Logger.log("Engine", "Saving data -> cycle: " + cycle + ".");
			GrandExchange.save();
			GrandExchange.savePrices();

			try {
				Thread.sleep(2000);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		Logger.log("Engine", "Sent request to login engine for shutdown...");
		LoginEngine.shutdown(1, true);

		Logger.log("Engine", "Completed, ready for reboot");
		restart();
	}

	public static void restart() {
		try {
			/*
			 * Runtime.getRuntime().exec(
			 * "java -d64 -Xss50m -cp bin;library/*; net.kagani.Engine " +
			 * Settings.WORLD_ID + " " + Settings.DEBUG + " " +
			 * Settings.HOSTED);
			 */
			System.err.println(Settings.WORLD_ID + " " + Settings.DEBUG + " " + Settings.HOSTED);
			System.exit(0);
		} catch (final Throwable e) {
			Logger.handle(e);
		}
	}

	private static void addCleanMemoryTask() {
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cleanMemory(Runtime.getRuntime().freeMemory() < Settings.MIN_FREE_MEM_ALLOWED);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 10, TimeUnit.MINUTES);
	}

	private static void addAutoSavingTask() {
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					savePlayers();
					// saveFiles();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, 0, 1, TimeUnit.MINUTES);
	}

	private static void addRecalculatePricesTask() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		int minutes = (int) ((c.getTimeInMillis() - Utils.currentTimeMillis()) / 1000 / 60);
		int halfDay = 12 * 60;
		if (minutes > halfDay)
			minutes -= halfDay;
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					GrandExchange.recalcPrices();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, minutes, halfDay, TimeUnit.MINUTES);
	}

	private static void savePlayers() {
		for (Player player : World.getPlayers()) {
			try {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				byte[] data = SerializationUtilities.tryStoreObject(player);
				if (data == null || data.length <= 0)
					continue;
				if (player.getClanManager() != null)
					ClansManager.saveClanmateDetails(player);
				PlayerHandlerThread.addSave(player.getUsername(), data);
			} catch (Exception e) {
				Logger.logErr("Engine", "An error has occured: " + e);
			}
		}
	}

	public static void cleanMemory(boolean force) throws IOException {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
		}
		for (Index index : Cache.STORE.getIndexes())
			if (index != null) {
				index.resetCachedFiles();
				index.getMainFile().resetCachedArchives();
			}
		GameExecutorManager.fastExecutor.purge();
		System.gc();
	}
}
