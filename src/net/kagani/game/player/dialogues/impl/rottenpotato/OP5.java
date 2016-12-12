package net.kagani.game.player.dialogues.impl.rottenpotato;

import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.content.DeveloperConsole;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.player.controllers.HouseControler;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class OP5 extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("Rotten Potato - Option: 5", "Wipe Inventory.",
				"Wipe Equipment.", "Wipe Bank.", "Set Visbility.", "More.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				stage = 3;
				sendOptionsDialogue("Rotten Potato - Option: Wipe Inventory",
						"Yes, wipe my inventory.", "No, I'm fine.");
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				stage = 4;
				sendOptionsDialogue("Rotten Potato - Option: Wipe Equipment",
						"Yes, wipe my equipment.", "No, I'm fine.");
				break;
			case OPTION_3:
				if (player.getRights() < 2) {
					end();
					return;
				}
				stage = 5;
				sendOptionsDialogue("Rotten Potato - Option: Wipe Bank",
						"Yes, wipe my bank.", "Set up bank.", "No, I'm fine.");
				break;
			case OPTION_4:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getAppearence().switchHidden();
				player.getPackets()
						.sendGameMessage(
								player.getAppearence().isHidden() ? "You are invisible."
										: "You have turned visible.");
				end();
				break;
			case OPTION_5:
				if (player.getRights() < 2) {
					end();
					return;
				}
				stage = 2;
				sendOptionsDialogue("Rotten Potato - Option: 5",
						"Max Account.", "Reset Account.", "God Mode.",
						"Kill Me.", "More.");
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
				DeveloperConsole.max(player, 13034431);
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				DeveloperConsole.reset(player);
				end();
				break;
			case OPTION_3:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.setHitpoints(Integer.MAX_VALUE);
				player.getEquipment().setEquipmentHpIncrease(
						player.getMaxHitpoints() - 9900);
				for (int i = 0; i < 8; i++) {
					player.getCombatDefinitions().getStats()[i] = 50000;
				}
				player.getPackets().sendGameMessage(
						"hitpoints to " + Utils.format(Integer.MAX_VALUE)
								+ " and 0-6 stats: 50000.");
				end();
				break;
			case OPTION_4:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.applyHit(new Hit(player, player.getHitpoints(),
						HitLook.POISON_DAMAGE));
				end();
				break;
			case OPTION_5:
				if (player.getRights() < 2) {
					end();
					return;
				}
				stage = 6;
				sendOptionsDialogue("Rotten Potato - Option: 5",
						"Setup house.", "Nevermind.");
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
				player.getInventory().reset();
				player.getInventory().addItem(5733, 1);
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
				player.getEquipment().getItems().clear();
				player.getEquipment().reset();
				for (int i = 0; i < 18; i++) {
					player.getEquipment().refresh(i);
				}
				player.getAppearence().generateAppearenceData();
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
				player.getBank().potatoBank();
				player.getPackets().sendGameMessage(
						"You are required to relog to see affects.");
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.setupBank();
				end();
				break;
			case OPTION_3:
				if (player.getRights() < 2) {
					end();
					return;
				}
				end();
				break;
			}
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				if (player.getRights() < 2) {
					end();
					return;
				}
				player.getHouse().leaveHouse(player, 1);
				player.getHouse().destroyHouse();
				player.getHouse().createHouse(true);
				end();
				break;
			case OPTION_2:
				if (player.getRights() < 2) {
					end();
					return;
				}
				end();
			}
			break;
		}
	}

	@Override
	public void finish() {
	}
}