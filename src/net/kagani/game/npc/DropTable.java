package net.kagani.game.npc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.player.Player;
import net.kagani.utils.NPCDrops;
import net.kagani.utils.Utils;

public class DropTable {

	/**
	 * @author: Dylan Page
	 */

	public static void getDropTable(Player player, NPC npc) {
		String list = "";
		long currentTime = Utils.currentTimeMillis();
		Drops drops = NPCDrops.getDrops(npc.getId());
		if (npc == null || npc.isDead() || !npc.getDefinitions().hasAttackOption() || drops == null)
			return;
		if (player.isLocked() || player == null || player.isDead())
			return;
		if (player.getRights() < 2) {
			if (player.getAttackedByDelay() + 10000 > currentTime) {
				player.getPackets().sendGameMessage("You can't do that while in combat.");
				return;
			}
		}
		if (player.getInterfaceManager().containsScreenInterface()
				|| player.getInterfaceManager().containsBankInterface()) {
			player.getPackets().sendGameMessage(
					"Please finish what you're doing before opening " + npc.getName() + "'s drop table.");
			return;
		}
		List<Drop> dropL = drops.getAllDrops(player);
		player.getInterfaceManager().sendCentralInterface(1166);
		for (Drop drop : dropL) {
			for (int i = 0; i < 1; i++) {
				list += "<col=FF0000>" + ItemDefinitions.getItemDefinitions(drop.getItemId()).getName()
						+ "</col><br>Amount: " + Utils.format(drop.getMinAmount()) + "-"
						+ Utils.format(drop.getMaxAmount()) + "<br><br>";
			}
		}
		player.getPackets().sendIComponentText(1166, 2, "Total <col=00FF00>" + dropL.size() + "</col> drops");
		player.getPackets().sendIComponentText(1166, 23, npc.getName() + "'s Drop Table");
		player.getPackets().sendIComponentText(1166, 1, list);
	}

	private static String read(int npcId) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("data/npcs/drops/" + npcId + ".txt"));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}
}