package net.kagani.executor;

import net.kagani.Engine;
import net.kagani.game.minigames.fistofguthix.MinigameManager;

public class MinigameThread extends Thread {

	private int tick = 0;

	public int tick() {
		return tick;
	}

	protected MinigameThread() {
		setPriority(Thread.MIN_PRIORITY);
		setName("Minigames Thread");
	}

	@Override
	public void run() {
		while (!Engine.shutdown) {
			try {
				tick++;
				if (tick >= 10) {
					if (MinigameManager.INSTANCE().fistOfGuthix() != null)
						MinigameManager.INSTANCE().fistOfGuthix().process();
					tick = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}