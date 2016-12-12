package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;

public class MonkOfEntrana extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Do you seek passage to holy Entrana? If so, you must",
				"leaves you weaponry and armour behind - this is",
				"Saradomin's will.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Yes, okay, I'm ready to do.",
					"No, not right now.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Yes, okay, I'm ready to go.");
				stage = 1;
				break;
			case OPTION_2:
				break;
			}
			break;
		case 1:
			sendDialogue("The monk quickly searches you.");
			break;
		}
	}

	@Override
	public void finish() {

	}

	/**
	 * Checks if the player can enter entrana.
	 * 
	 * @param player
	 *            the player.
	 * @return {@code True} if so.
	 */
	/**
	 * public static boolean canEnterEntrana(Player player) { Container[]
	 * container = new Container[] {player.getInventory(),
	 * player.getEquipment()}; for (Container c: container) { for (Item i :
	 * c.toArray()) { if (i == null) { continue; } if
	 * (!i.getDefinition().isAllowedOnEntrana()) { return false; } } } return
	 * true; }
	 * 
	 * private static final String[] allowedNames = new String[] {"cape",
	 * "robe", "hat", "potion"};
	 * 
	 * public boolean isAllowedOnEntrana() { if (getId() == 946) { return true;
	 * } if (getName().equals("Boots")) { return true; } if
	 * (getName().toLowerCase().startsWith("ring") ||
	 * getName().toLowerCase().startsWith("amulet")) { return true; } if
	 * (getName().toLowerCase().contains("spotted") ||
	 * getName().toLowerCase().equals("spottier")) { return true; } if
	 * (getName().equals("Boots of lightness")) { return true; } for (String
	 * name : allowedNames) { if (getName().toLowerCase().contains(name)) {
	 * return true; } } return getConfiguration(ItemConfiguration.BONUS) ==
	 * null; }
	 **/

}
