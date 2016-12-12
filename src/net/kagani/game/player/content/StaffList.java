package net.kagani.game.player.content;

import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class StaffList {

	public final static int INTERFACE = 1158;

	public final static String ONLINE = "<col=02AB2F>Online</col>";
	public final static String OFFLINE = "<col=DB0000>Offline</col>";

	public enum Staff {

		OWNER("<img=1><col=FF0000>99max99", "<col=FF0000>Owner/Developer", "99max99"),

		CO_OWNER("<img=1><col=EDE909>Brandon", "<col=EDE909>Co-Owner/Developer",
				"Brandon"),
		HEADADMIN("<img=1><col=EDE909>RedDragon", "<col=EDE909>Head Admin/Developer",
				"RedDragon"),
		
		DEV("<img=1><col=EDE909>Llama", "<col=EDE909>Co-Developer",
				"Llama"),
		//PMOD("<img=0><col=515756>reddragon", "<col=515756>Player Moderator", "reddragon")

		;

		private final String username, position;
		private final String[] usernames;

		Staff(String username, String position, String... usernames) {
			this.username = username;
			this.position = position;
			this.usernames = usernames;
		}

		public String getUsername() {
			return username;
		}

		public String getPosition() {
			return position;
		}

		public String[] getUsernames() {
			return usernames;
		}

		public String getOnline() {
			for (String name : usernames) {
				if (World.containsPlayer(name))
					return ONLINE;
			}
			return OFFLINE;
		}
	}

	public static void send(Player player) {
		player.getInterfaceManager().sendCentralInterface(INTERFACE);
		for (int i = 0; i < Utils
				.getInterfaceDefinitionsComponentsSize(INTERFACE); i++)
			player.getPackets().sendIComponentText(INTERFACE, i, "");
		player.getPackets().sendIComponentText(INTERFACE, 74, "Staff List");
		int componentId = 8;
		int number = 1;
		for (Staff staff : Staff.values()) {
			if (componentId >= 56) // end of interface
				return;
			player.getPackets().sendIComponentText(INTERFACE, componentId++,
					"" + number++);
			player.getPackets().sendIComponentText(INTERFACE, componentId++,
					staff.getUsername());
			player.getPackets().sendIComponentText(INTERFACE, componentId++,
					staff.getPosition());
			player.getPackets().sendIComponentText(INTERFACE, componentId++,
					staff.getOnline());
			componentId++;
		}
	}
}