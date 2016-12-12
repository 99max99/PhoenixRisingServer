package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;

public class PresetsD extends Dialogue {

	private String name;

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Save as preset 1.",
				"Save as preset 2.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 50:
			end();
			break;
		case 1:
			stage = -1;
			switch (componentId) {
			case OPTION_1:
				name = "preset 1";
				sendDialogue("Save your inventory, equipment and prayers as "
						+ name + "?");
				break;
			case OPTION_2:
				name = "preset 1";
				sendDialogue("Save your inventory, equipment and prayers as "
						+ name + "?");
				break;
			}
			break;
		case -1:
			stage = 0;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, please.",
					"No, nevermind.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				/*
				 * Item inventory[] = player.getInventory().getItems()
				 * .getItemsCopy(); Item equipment[] =
				 * player.getEquipment().getItems() .getItemsCopy();
				 * PresetSetups set = new PresetSetups(name, equipment,
				 * inventory, player.getCombatDefinitions().spellBook, player
				 * .getPrayer().isAncientCurses() ? 1 : 0); if
				 * (player.getPresetSetups().size() >= getMaxAmount(player)) {
				 * player.getPackets().sendGameMessage(
				 * "You are only able to have a maximum of " +
				 * getMaxAmount(player) + "."); return; } if
				 * (player.getPresetSetupByName(name) != null) {
				 * player.getPackets().sendGameMessage(
				 * "You already have a setup for " + name +
				 * ", would you like to override it?"); return; } if
				 * (player.getPresetSetups().add(set))
				 * player.getDialogueManager() .startDialogue( "SimpleMessage",
				 * "Your equipment, inventory, stats and prayers have been saved with the name "
				 * + name + ", you can load it at anytime by typing ::loadgear "
				 * + name + ".");
				 */
				stage = 50;
				sendDialogue("null");
				break;
			default:
				end();
				break;
			}
			break;
		}
	}

	private int getMaxAmount(Player player) {
		return player.isDiamondMember() ? 5 : player.isPlatinumMember() ? 4
				: player.isGoldMember() ? 3 : player.isSilverMember() ? 2 : 1;
	}

	@Override
	public void finish() {
	}

}