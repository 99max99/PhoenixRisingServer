package net.kagani.game.player.content;

import java.util.TimerTask;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public final class FadingScreen {

	private FadingScreen() {

	}

	public static int TICK = 600;

	public static void fade(final Player player, long fadeTime,
			final Runnable event) {
		unfade(player, fade(player, fadeTime), event);
	}

	public static void fade(final Player player, final Runnable event) {
		unfade(player, fade(player), event);
	}

	public static void unfade(final Player player, long startTime,
			final Runnable event) {
		unfade(player, 2500, startTime, event);
	}

	public static void unfade(final Player player, long endTime,
			long startTime, final Runnable event) {
		long leftTime = endTime - (Utils.currentTimeMillis() - startTime);
		if (leftTime > 0) {
			GameExecutorManager.fastExecutor.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						unfade(player, event);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}

			}, leftTime);
		} else
			unfade(player, event);
	}

	public static void unfade(final Player player, Runnable event) {
		event.run();
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.getInterfaceManager().sendFadingInterface(170);
				GameExecutorManager.fastExecutor.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							player.getInterfaceManager()
									.removeFadingInterface();
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, 2000);
			}

		});
	}

	public static long fade(Player player, long fadeTime) {
		player.getInterfaceManager().sendFadingInterface(115);
		return Utils.currentTimeMillis() + fadeTime;
	}

	public static long fade(Player player) {
		return fade(player, 0);
	}
}
