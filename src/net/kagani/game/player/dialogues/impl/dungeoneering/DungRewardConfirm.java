package net.kagani.game.player.dialogues.impl.dungeoneering;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.dungeoneering.DungeonRewardShop;
import net.kagani.game.player.content.dungeoneering.DungeonRewardShop.DungeonReward;
import net.kagani.game.player.dialogues.Dialogue;

public class DungRewardConfirm extends Dialogue {

	DungeonReward item;

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 6) {
			if (player.getInventory().getFreeSlots() >= 1) {
				if (player.getDungManager().getTokens() < item.getCost())
					return;
				if (player.getSkills().getLevel(Skills.DUNGEONEERING) < item
						.getRequirement())
					return;
				player.getDungManager().setTokens(
						player.getDungManager().getTokens() - item.getCost());
				player.getInventory().addItem(item.getId(), 1);
				DungeonRewardShop.refreshPoints(player);
			} else {
				player.getPackets().sendGameMessage(
						"You need more inventory space.");
			}
		}
		end();
	}

	@Override
	public void start() {
		item = (DungeonReward) parameters[0];
		player.getInterfaceManager().sendDialogueInterface(1183);
		player.getPackets().sendItemOnIComponent(1183, 10, item.getId(), 1);
		player.getPackets().sendIComponentText(1183, 12,
				"Are you sure you want to buy this?");
		player.getPackets().sendIComponentText(1183, 16, "Confirm");
		player.getPackets().sendIComponentText(1183, 20, "Cancel");
		player.getPackets().sendIComponentText(1183, 5,
				ItemDefinitions.getItemDefinitions(item.getId()).getName());
	}
}