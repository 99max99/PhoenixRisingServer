package net.kagani.game.player.cutscenes.actions;

import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

/**
 * Handles an interface showing up cutscene action.
 * 
 * @author Emperor
 * 
 */
public final class InterfaceAction extends CutsceneAction {

	/**
	 * The interface id.
	 */
	private final int interfaceId;

	/**
	 * The delay.
	 */
	private final int delay;

	/**
	 * Constructs a new {@code InterfaceAction} {@code Object}.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param actionDelay
	 *            The action delay.
	 */
	public InterfaceAction(int interfaceId, int actionDelay) {
		super(-1, actionDelay);
		this.interfaceId = interfaceId;
		this.delay = actionDelay;
	}

	@Override
	public void process(final Player player, Object[] cache) {
		player.getInterfaceManager().sendCentralInterface(interfaceId);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getInterfaceManager().removeCentralInterface();
			}
		}, delay);
	}

}