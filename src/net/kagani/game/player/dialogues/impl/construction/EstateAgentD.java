package net.kagani.game.player.dialogues.impl.construction;

import net.kagani.Settings;
import net.kagani.game.item.Item;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.construction.HouseConstants.POHLocation;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class EstateAgentD extends Dialogue {

	private static final POHLocation[] LOCATIONS = { POHLocation.RIMMINGTON,
			POHLocation.TAVERLY, POHLocation.POLLNIVNEACH,
			POHLocation.RELLEKKE, POHLocation.BRIMHAVEN, POHLocation.YANILLE,
			POHLocation.PRIFDDINAS };
	private static final int[] REDECORATE_PRICE = { 5000, 5000, 7000, 10000,
			15000, 25000, 0 };
	private static final int[] REDECORATE_BUILDS = {/* basic wood */3, 0
	/* basic stone */, 3, 1
	/* whitewash */, 1, 2
	/* fremmy wood */, 2, 3
	/* tropical wood */, 3, 4
	/* fancy stone */, 3, 5
	/* zenviva dark */, 3, 6 };

	private int npcId;

	@Override
	public void start() {
		npcId = (int) this.parameters[0];
		sendNPCDialogue(npcId, NORMAL, "Hello. Welcome to "
				+ Settings.SERVER_NAME
				+ " Housing Agency! What can I do you for?");
	}

	@Override
	public void run(int interfaceId, int componentId) {

		if (stage == -1) {
			sendOptionsDialogue(
					"SELECT AN OPTION",
					"Can you move my house please?",
					"Can you redecorate my house please?",
					"Could I have a Construction guidebook?",
					"Tell me about houses.",
					player.getSkills().getLevel(Skills.CONSTRUCTION) == 99 ? "Can you sell me a Skillcape of Construction?"
							: "Tell me about that skillcape you're wearing.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "Can you move my house please?");
				stage = 1;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL,
						"Can you redecorate my house please?");
				stage = 5;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(NORMAL,
						"Could I have a Construction guidebook?");
				stage = 14;
			} else if (componentId == OPTION_4) {
				sendPlayerDialogue(NORMAL, "Tell me about houses.");
				stage = 9;
			} else if (componentId == OPTION_5) {
				boolean is99Con = player.getSkills().getLevel(
						Skills.CONSTRUCTION) == 99;
				sendPlayerDialogue(
						NORMAL,
						is99Con ? "Can you sell me a Skillcape of Construction?"
								: "Tell me about that cape you're wearing.");
				stage = (byte) (is99Con ? 17 : 15);
			}
		} else if (stage == 1) {
			sendNPCDialogue(npcId, NORMAL,
					"Certainly. Where would you like it moved to?");
			stage = 2;
		} else if (stage == 2) {
			sendOptionsDialogue("SELECT AN OPTION", "Rimmington (5,000)",
					"Taverly (5,000)", "Pollnivneach (7,500)",
					"Relleka (10,000)", "More");
			stage = 3;
		} else if (stage == 3) {

			if (componentId == OPTION_1) {
				moveHouse(LOCATIONS[0]);
			} else if (componentId == OPTION_2) {
				moveHouse(LOCATIONS[1]);
			} else if (componentId == OPTION_3) {
				moveHouse(LOCATIONS[2]);
			} else if (componentId == OPTION_4) {
				moveHouse(LOCATIONS[3]);
			} else if (componentId == OPTION_5) {
				sendOptionsDialogue("SELECT AN OPTION", "Brimhaven (15,000)",
						"Yannile (25,000)", "Prifddinas (50,000)", "Previous");
				stage = 4;
			}

		} else if (stage == 4) {
			if (componentId == OPTION_1) {
				moveHouse(LOCATIONS[4]);
			} else if (componentId == OPTION_2) {
				moveHouse(LOCATIONS[5]);
			} else if (componentId == OPTION_3) {
				moveHouse(LOCATIONS[6]);
			} else if (componentId == OPTION_4) {
				sendOptionsDialogue("SELECT AN OPTION", "Rimmington (5,000)",
						"Taverly (5,000)", "Pollnivneach (7,500)",
						"Relleka (10,000)", "More");
				stage = 3;
			}

		} else if (stage == 5) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Certainly. My magic can rebuild the house in a completely new style! What style would you like?");
			stage = 6;
		} else if (stage == 6) {
			sendOptionsDialogue("SELECT AN OPTION", "Basic wood (5,000)",
					"Basic stone (5,000)", "Whitewashed stone (7,500)",
					"Fremenik-style wood (10,000)", "More");
			stage = 7;
		} else if (stage == 7) {
			if (componentId >= OPTION_1 && componentId <= OPTION_4) {
				redecorateHouse(componentId == 11 ? 0 : componentId - 12);
			} else if (componentId == OPTION_5) {
				sendOptionsDialogue("SELECT AN OPTION",
						"Tropical Wood (15,000)", "Fancy stone (25,000)",
						"Zeneviva's dark stone (Free)", "Previous");
				stage = 8;
			}
		} else if (stage == 8) {
			if (componentId == OPTION_1 || componentId == OPTION_2
					|| componentId == OPTION_3) {
				redecorateHouse(componentId == OPTION_1 ? 4
						: componentId == OPTION_2 ? 5 : 6);
			} else if (componentId == OPTION_4) {
				sendOptionsDialogue("SELECT AN OPTION", "Basic wood (5,000)",
						"Basic stone (5,000)", "Whitewashed stone (7,500)",
						"Fremenik-style wood (10,000)", "More");
				stage = 7;
			}
		} else if (stage == 9) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"It all came out of the wizards' experiments. THey found a way to fold space, so that they could pack many acres of land into an area only a foot across.");
			stage = 10;
		} else if (stage == 10) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"They created serveral folded-space regions across "
							+ Settings.SERVER_NAME
							+ ". Each one contains hundreds of small plots where people can build houses.");
			stage = 11;
		} else if (stage == 11) {
			sendPlayerDialogue(
					NORMAL,
					"Ah, so that's how everyone can have a house without them cluttering up the world!");
			stage = 12;
		} else if (stage == 12) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Quite. The wizards didn't want to get bogged down i nthe business side of things so they hired me to sell the houses.");
			stage = 13;
		} else if (stage == 13) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"There are various other people across "
							+ Settings.SERVER_NAME
							+ " who can help you furnish your house. You should start by buying planks from the sawmill operator in Varrock.");
			stage = 30;
		} else if (stage == 14) {
			sendNPCDialogue(npcId, NORMAL, "Certainly.");
			player.getInventory().addItem(new Item(8463));
			stage = 30;
		} else if (stage == 15) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"As you may know, skillcapes are only avaiable to maters in a skill. I have spent my entire life building houses and now I spend my time selling them! As a sign of my abilities I wear this Skillcape of Construction. If you ever have");
			stage = 16;
		} else if (stage == 16) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"enough skill to build a demonic throne, come and talk to me and i'll sell you a skillcape like mine.");
			stage = 30;
		} else if (stage == 17) {
			sendNPCDialogue(npcId, NORMAL, "Alright, that'll be 99,000 coins.");
			stage = 18;
		} else if (stage == 18) {
			sendOptionsDialogue("Select an option", "I'm not paying that!",
					"Certainly, that sounds fair.");
			stage = 19;
		} else if (stage == 19) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "I'm not paying that!");
				stage = 30;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL, "Certainly, that sounds fair.");
				stage = 20;
			}
		} else if (stage == 20) {
			boolean hasEnoughCoins = player.getInventory().getCoinsAmount() >= 99000;
			if (hasEnoughCoins) {
				sendNPCDialogue(npcId, NORMAL,
						"Excellent, wear that cape with pride my friend.");
				player.getInventory()
						.removeItemMoneyPouch(new Item(995, 99000));
				player.getInventory().addItem(new Item(9791, 1));
				player.getInventory().addItem(
						new Item(player.getSkills().canObtainTrimmed() ? 9790
								: 9789, 1));
			} else {
				sendNPCDialogue(npcId, NORMAL,
						"You don't have enough coins to cover the cost of the cape.");
			}
			stage = 30;
		} else if (stage == 30) {
			end();
		}
	}

	private void redecorateHouse(int index) {
		int cost = REDECORATE_PRICE[index];
		if (player.getInventory().getCoinsAmount() < cost) {
			player.getDialogueManager()
					.startDialogue("SimpleNPCMessage", npcId,
							"You don't have enough capital to cover the costs. Please return when you do!");
			return;
		} else if (player.getHouse().getLook() == index) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage",
					npcId, "Your house is already in that style!");
			return;
		}
		player.getInventory().removeItemMoneyPouch(new Item(995, cost));
		player.getDialogueManager().startDialogue("SimpleNPCMessage", npcId,
				"Your house has been redecorated.");
		player.getHouse().redecorateHouse(REDECORATE_BUILDS[(index * 2) + 1]);
	}

	private void moveHouse(POHLocation location) {
		int cost = location.getCost();
		String regionalName = Utils.formatPlayerNameForDisplay(location
				.toString().toLowerCase());

		if (player.getInventory().getCoinsAmount() < cost) {
			player.getDialogueManager()
					.startDialogue("SimpleNPCMessage", npcId,
							"You don't have enough capital to cover the costs. Please return when you do!");
			return;
		} else if (player.getHouse().getLocation() == location) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage",
					npcId,
					"Your home is already located in " + regionalName + "!");
			return;
		}
		player.getInventory().removeItemMoneyPouch(new Item(995, cost));
		player.getHouse().setLocation(location);
		player.getDialogueManager().startDialogue("SimpleNPCMessage", npcId,
				"Your home has been moved to " + regionalName + "!");
	}

	@Override
	public void finish() {

	}
}
