package net.kagani.game.player.dialogues.impl.rottenpotato;

import java.util.ArrayList;
import java.util.List;

import net.kagani.Settings;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;

public class OPuOP extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	/** The target. */
	private Player target;

	private static List<String> items = new ArrayList<String>();

	@Override
	public void finish() {
		
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 0:
			stage = 1;
			items.clear();
			sendOptionsDialogue("Rotten Potato - Option: Player", "Check "
					+ target.getDisplayName() + "'s bank.",
					"Send " + target.getDisplayName() + " to jail.", "Un-Null "
							+ target.getDisplayName() + ".",
					"Punish " + target.getDisplayName() + ".", "More.");
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getPackets().sendItems(95,
						target.getBank().getContainerCopy());
				player.getBank().openPlayerBank(target);
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				end();
				break;
			case OPTION_3:
				if (player.getRights() < 2) {
					end();
					return;
				}
				if (target == null)
					player.getPackets().sendGameMessage(
							target.getDisplayName() + " is offline.");
				else {
					target.unlock();
					target.getControlerManager().forceStop();
					if (target.getNextWorldTile() == null)
						target.setNextWorldTile(Settings.HOME_LOCATION);
					player.getPackets().sendGameMessage(
							"You have unnulled: " + target.getDisplayName()
									+ ".");
				}
				end();
				break;
			case OPTION_4:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getDialogueManager().startDialogue("AddOffenceD",
						target.getUsername());
				break;
			case OPTION_5:
				if (player.getRights() < 2) {
					end();
					return;
				}
				stage = 2;
				sendOptionsDialogue("Rotten Potato - Option: Player",
						"Wipe Inventory.", "Wipe Equipment.",
						"Copy Inventory.", "Copy Equipment.", "More.");
				break;
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				if (target == null)
					player.getPackets().sendGameMessage(
							target.getDisplayName() + " is offline.");
				else {
					stage = 3;
					sendOptionsDialogue("Are you sure?",
							"Yes, wipe inventory.", "Nevermind.");
				}
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				if (target == null)
					player.getPackets().sendGameMessage(
							target.getDisplayName() + " is offline.");
				else {
					stage = 4;
					sendOptionsDialogue("Are you sure?",
							"Yes, wipe equipment.", "Nevermind.");
				}
				break;
			case OPTION_3:
				if (player.getRights() < 2) {
					end();
					return;
				}
				if (target == null)
					player.getPackets().sendGameMessage(
							target.getDisplayName() + " is offline.");
				else {
					end();
					player.getPackets().sendGameMessage(
							"Failed while copying " + target.getDisplayName()
									+ "'s inventory.");
				}
				break;
			case OPTION_4:
				if (player.getRights() < 2) {
					end();
					return;
				}
				Item[] items = new Item[13];
				items = target.getEquipment().getItems().getItemsCopy();
				for (int i = 0; i < items.length; i++) {
					if (items[i] == null)
						continue;
					player.getEquipment().getItems().set(i, items[i]);
					player.getEquipment().refresh(i);
				}
				player.getAppearence().generateAppearenceData();
				player.getPackets().sendGameMessage(
						"Successfully copied " + target.getDisplayName()
								+ "'s equipment.");
				end();
				break;
			case OPTION_5:
				if (player.getRights() < 2) {
					end();
					return;
				}
				stage = 5;
				sendOptionsDialogue("Rotten Potato - Option: Player",
						"Forcetalk.", "Send game message.", "Nevermind.");
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				target.getInventory().reset();
				player.getPackets().sendGameMessage(
						"Successfully wiped " + target.getDisplayName()
								+ "'s inventory.");
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				end();
				break;
			}
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				target.getEquipment().getItems().clear();
				target.getEquipment().reset();
				for (int i = 0; i < 18; i++) {
					target.getEquipment().refresh(i);
				}
				target.getAppearence().generateAppearenceData();
				player.getPackets().sendGameMessage(
						"Successfully wiped " + target.getDisplayName()
								+ "'s inventory.");
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				end();
				break;
			}
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getPackets().sendInputLongTextScript(
						"Please enter the message:");
				player.getTemporaryAttributtes().put("nogood", Boolean.TRUE);
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getPackets().sendInputLongTextScript(
						"Please enter the message:");
				player.getTemporaryAttributtes().put("nogood", Boolean.TRUE);
				end();
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void start() {
		target = (Player) parameters[0];
		stage = 0;
		for (int i = 0; i < 28; i++) {
			Item item = target.getInventory().getItem(i);
			if (item == null) {
				continue;
			}
			items.add("x" + item.getAmount() + " " + item.getName());
		}
		if (items.isEmpty()) {
			sendDialogue(target.getDisplayName()
					+ " does not seem to have anything in his inventory.");
			return;
		}
		sendDialogue(target.getDisplayName() + "'s inventory contains " + items
				+ ".");
		items.clear();
	}
}