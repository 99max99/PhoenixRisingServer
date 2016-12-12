package net.kagani.game.player.content;

import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

/**
 * Used for when teleporting more than one person.
 * 
 * @author Khaled
 * 
 */
public class ManiFoldTeleport {

	private static final int REQUEST_INTERFACE = 326;
	private static final String[][] TELEPORT_NAMES = { { "MoonClan",
			"WaterBirth Island", "Barbarian Outpost", "Port Khazard",
			"Fishing Guild", "Catherby", "Ice Platue (Dangerous)", "Trollhiem" } };
	private static final WorldTile[][] TELEPORT_LOCATION = {
			{ new WorldTile(2114, 3914, 0), new WorldTile(2546, 3758, 0),
					new WorldTile(2635, 3166, 0), new WorldTile(2635, 3166, 0),
					new WorldTile(2614, 3384, 0), new WorldTile(2795, 3434, 0),
					new WorldTile(2974, 3940, 0), new WorldTile(2814, 3680, 0) },
			{} };

	public static void openInterface(Player player, String name, int index,
			boolean isGroupTeleport) {
		player.getDialogueManager().startDialogue(new Dialogue() {

			private boolean isGroupTeleport;
			private int index;
			private String name;

			@Override
			public void start() {
				name = (String) this.parameters[0];
				index = (int) this.parameters[1];
				isGroupTeleport = (boolean) this.parameters[2];
				if (player.getDisplayName().equals(name)) {
					acceptOffer();
					return;
				}
				player.lock(3);
				player.getInterfaceManager().sendCentralInterface(
						REQUEST_INTERFACE);
				player.getPackets().sendIComponentText(REQUEST_INTERFACE, 1,
						Utils.formatPlayerNameForDisplay(name));
				player.getPackets().sendIComponentText(REQUEST_INTERFACE, 3,
						TELEPORT_NAMES[isGroupTeleport ? 0 : 1][index]);
			}

			@Override
			public void run(int interfaceId, int componentId) {
				if (componentId == 5) {
					acceptOffer();
				}
				end();
			}

			@Override
			public void finish() {

			}

			void acceptOffer() {
				if (isGroupTeleport)
					Magic.sendLunarTeleportSpell(player, 1, 0,
							TELEPORT_LOCATION[0][index]);
				else
					Magic.sendNormalTeleportSpell(player, 1, 0,
							TELEPORT_LOCATION[1][index]);
			}

		}, name, index, isGroupTeleport);
	}
}