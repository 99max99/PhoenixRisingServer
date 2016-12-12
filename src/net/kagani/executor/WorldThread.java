package net.kagani.executor;

import net.kagani.Settings;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public final class WorldThread extends Thread {

	public static volatile long WORLD_CYCLE;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
	}

	@Override
	public final void run() {
		while (!GameExecutorManager.executorShutdown) {
			WORLD_CYCLE++; // made the cycle update at begin instead of end cuz
			// at end theres 600ms then to next cycle
			long currentTime = Utils.currentTimeMillis();
			// long debug = Utils.currentTimeMillis();
			WorldTasksManager.processTasks();
			try {
				for (Player player : World.getPlayers()) {
					if (!player.hasStarted() || player.hasFinished())
						continue;
					player.processEntity();
				}
				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntity();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
			try {
				for (Player player : World.getPlayers()) {
					if (!player.hasStarted() || player.hasFinished())
						continue;
					player.processEntityUpdate();
				}
				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntityUpdate();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
			try {
				// //
				// System.out.print(" ,NPCS PROCESS: "+(Utils.currentTimeMillis()-debug));
				// debug = Utils.currentTimeMillis();

				for (Player player : World.getPlayers()) {
					if (!player.hasStarted() || player.hasFinished())
						continue;
					player.getPackets().sendLocalPlayersUpdate();
					player.getPackets().sendLocalNPCsUpdate();
					player.processProjectiles();// waits for player to walk and
					// so on
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
			try {
				World.removeProjectiles();
			} catch (Throwable e) {
				Logger.handle(e);
			}
			try {
				// System.out.print(" ,PLAYER UPDATE: "+(Utils.currentTimeMillis()-debug)+", "+World.getPlayers().size()+", "+World.getNPCs().size());
				// debug = Utils.currentTimeMillis();
				for (Player player : World.getPlayers()) {
					if (!player.hasStarted() || player.hasFinished())
						continue;
					player.resetMasks();
				}
				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.resetMasks();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}

			try {
				for (Player player : World.getPlayers()) {
					if (!player.hasStarted() || player.hasFinished())
						continue;
					if (!player.getSession().getChannel().isConnected())
						player.finish(); // requests finish, wont do anything if
					// already requested btw
				}
				for (Player player : World.getLobbyPlayers()) {
					if (!player.hasStarted() || player.hasFinished())
						continue;
					if (!player.getSession().getChannel().isConnected())
						player.finish(); // requests finish, wont do anything if
					// already requested btw
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}

			// //
			// Logger.log(this,
			// "TOTAL: "+(Utils.currentTimeMillis()-currentTime));
			long sleepTime = Settings.WORLD_CYCLE_TIME + currentTime
					- Utils.currentTimeMillis();
			if (sleepTime <= 0)
				continue;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				Logger.handle(e);
			}
		}
	}
}