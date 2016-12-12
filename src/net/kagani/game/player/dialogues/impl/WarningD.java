package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class WarningD extends Dialogue {

	private Runnable run;

	@Override
	public void start() {
		int id = (int) parameters[1];
		run = (Runnable) parameters[4];
		if (player.getDoomsayerManager().isWarningOff(id)) {
			end();
			run.run();
			return;
		}
		player.getDoomsayerManager().openWarning((int) parameters[0], id,
				(String) parameters[2], (String) parameters[3]);
	}

	@Override
	public void run(int interfaceId, int componentId) {

		if (interfaceId == 1262 || interfaceId == 382 || interfaceId == 1292
				|| interfaceId == 793) {
			if ((interfaceId == 1262 && componentId == 18)
					|| (interfaceId == 382 && componentId == 19)
					|| (interfaceId == 1292 && componentId == 12)
					|| (interfaceId == 793 && componentId == 15)) {
				player.stopAll();
				run.run();
			} else if ((interfaceId == 1262 && componentId == 19)
					|| (interfaceId == 1292 && componentId == 13)
					|| (interfaceId == 793 && componentId == 14)) {
				player.stopAll();
				end();
			} else if (interfaceId == 1262 && componentId == 12)
				player.getDoomsayerManager().toogleCurrentWarning();
			else if ((interfaceId == 382 && componentId == 31)
					|| (interfaceId == 793 && componentId == 9)) {
				player.getDoomsayerManager().toogleCurrentWarning();
				player.stopAll();
				run.run();
			}
		}
	}

	@Override
	public void finish() {

	}
}