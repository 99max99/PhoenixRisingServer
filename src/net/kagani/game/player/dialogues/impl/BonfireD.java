package net.kagani.game.player.dialogues.impl;

import net.kagani.game.WorldObject;
import net.kagani.game.player.actions.Bonfire;
import net.kagani.game.player.actions.Bonfire.Log;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.player.dialogues.impl.skills.ChooseAToolD;

public class BonfireD extends Dialogue {

	private Log[] logs;
	private WorldObject object;

	@Override
	public void start() {
		this.logs = (Log[]) parameters[0];
		this.object = (WorldObject) parameters[1];
		int[] ids = new int[logs.length];
		for (int i = 0; i < ids.length; i++)
			ids[i] = logs[i].getLogId();
		for (int i = 0; i < 15; i++)
			player.getPackets().sendCSVarInteger(1703 + i,
					i >= ids.length ? -1 : ids[i]);
		player.getPackets().sendIComponentText(1179, 0,
				"Which logs do you want to add to the bonfire?");
		player.getInterfaceManager().sendCentralInterface(1179);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
		int slot = ChooseAToolD.getOptionSlot(componentId);
		if (slot >= logs.length)
			return;
		player.getActionManager().setAction(new Bonfire(logs[slot], object));
	}

	@Override
	public void finish() {
		player.getInterfaceManager().removeCentralInterface();
	}

}
