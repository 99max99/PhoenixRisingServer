package net.kagani.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class LoginExecutorManager {

	public static boolean workerShutdown;
	public static LoginThread thread;
	public static ScheduledExecutorService authsExecutor;

	public static void init() {
		thread = new LoginThread();
		thread.start();
		authsExecutor = Executors
				.newSingleThreadScheduledExecutor(new SlowThreadFactory());
	}

	public static void shutdown(boolean await) {
		workerShutdown = true;
		authsExecutor.shutdownNow();
		if (await) {
			while (true) {
				try {
					thread.join();
					break;
				} catch (InterruptedException e) {
				}
			}
		}
	}
}