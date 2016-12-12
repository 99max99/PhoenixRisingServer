package net.kagani.game.player.dialogues.impl;

import net.kagani.game.item.Item;
import net.kagani.game.player.content.ItemTransportation;
import net.kagani.game.player.dialogues.Dialogue;

public class Transportation extends Dialogue {

	@Override
	public void start() {
		String[] locations = (String[]) parameters[0];
		// sendOptionsDialogue("Where would you like to teleport to",
		// locations);

		player.getInterfaceManager().sendCentralInterface(1578);
		player.getPackets().sendExecuteScript(10890, locations.length, 0,
				"Choose teleport destination.",
				locations.length > 0 ? locations[0] : "",
				locations.length > 1 ? locations[1] : "",
				locations.length > 2 ? locations[2] : "",
				locations.length > 3 ? locations[3] : "",
				locations.length > 4 ? locations[4] : "",
				locations.length > 5 ? locations[5] : "",
				locations.length > 6 ? locations[6] : "",
				locations.length > 7 ? locations[7] : "",
				locations.length > 8 ? locations[8] : "",
				locations.length > 9 ? locations[9] : "");

		player.getPackets().sendIComponentSettings(1578, 1, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 20, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 23, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 26, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 29, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 32, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 35, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 38, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 41, -1, -1, 1);
		player.getPackets().sendIComponentSettings(1578, 44, -1, -1, 1);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		String[] locations = (String[]) parameters[0];
		int option = interfaceId == 1578 ? (componentId == 1 ? 0
				: componentId == 20 ? 1 : componentId == 23 ? 2
						: componentId == 26 ? 3 : componentId == 29 ? 4
								: componentId == 32 ? 5 : componentId == 35 ? 6
										: componentId == 38 ? 7
												: componentId == 41 ? 8 : 9)
				: componentId == OPTION_1 ? 0 : ((componentId - OPTION_1) + 1);
		ItemTransportation
				.sendTeleport(
						player,
						(Item) parameters[1],
						option,
						false,
						(locations[locations.length - 1].equals("Nowhere") ? locations.length - 1
								: locations.length), (boolean) parameters[2]);
		end();
	}

	@Override
	public void finish() {
	}
}
