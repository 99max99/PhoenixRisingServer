package net.kagani.game.tasks;

public abstract class WorldTask implements Runnable {

	protected boolean needRemove;

	public final void stop() {
		needRemove = true;
	}

	public boolean isStopped() {
		return needRemove;
	}
}