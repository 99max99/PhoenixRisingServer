package net.kagani.game.player.content;

import net.kagani.game.World;
import net.kagani.game.player.Player;

public class SkillCounter {

	public static final int INTER = 1166;

	public static void send(Player player) {
		player.getInterfaceManager().sendCentralInterface(INTER);
		player.getPackets().sendIComponentText(INTER, 23, "Statistics");
		player.getPackets().sendIComponentText(INTER, 2, "");
		int number = 0;
		String list = "";
		for (Player p : World.getPlayers()) {
			number++;
			list += "0 NPC's killed<br>0 Dungeons completed<br>0 Slayer tasks completed<br>0 logs chopped<br>0 logs burnt";
		}
		player.getPackets().sendIComponentText(1166, 1, list);
	}
}