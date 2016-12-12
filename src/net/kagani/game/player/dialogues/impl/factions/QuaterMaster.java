package net.kagani.game.player.dialogues.impl.factions;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;
import net.kagani.utils.Utils;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class QuaterMaster extends Dialogue {

	public static final String[] GREETINGS = { "Salutations ", "Good day ",
			"What are your needs ",

	};

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		int random = Utils.random(GREETINGS.length);
		npc(HAPPY, GREETINGS[random]
				+ (player.isZamorak ? "Zamorakian" : "Saradominist")
				+ (random == GREETINGS.length ? "?" : "."));

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			options(DEFAULT, "What is this place?", "What are you doing here?",
					"Do you have supplies for sale?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				npc(CONFUSED,
						"Why this is the battle of lumbridge? We are in battle against the Zamorak god.");
				stage = -1;
				break;
			case OPTION_2:
				npc(HAPPY,
						"I am the "
								+ (player.isZamorak ? "Zamorak" : "Saradomin")
								+ " battle instructor. I am in charge of supplying our soliders.");
				stage = 2;
				break;
			case OPTION_3:
				player(ASKING, "Do you have anything for sale?");
				stage = 1;
				break;
			}
			break;

		case 1:
			npc(HAPPY, "Only the finest supplies in all of the "
					+ (player.isZamorak ? "Zamorak" : "Saradomin") + " camp.");
			stage = -3;
			break;

		case 2:
			npc(ASKING, "Are you low in supplies my fellow "
					+ (player.isZamorak ? "Zamorakian" : "Saradominist")
					+ " soldier?");
			stage = 3;
			break;

		case 3:
			options(DEFAULT, "Yes, I am rather low",
					"No, I believe I can handle");
			stage = 4;
			break;

		case 4:
			switch (componentId) {
			case OPTION_1:
				player(HAPPY, "Yes, I am rather low");
				stage = 5;
				break;
			case OPTION_2:
				player(UNSURE, "No, I believe I can handle");
				stage = -2;
				break;
			}
			break;

		case 5:
			npc(HAPPY, "Here you are!");
			stage = -3;
			break;

		case -2:
			end();
			break;

		case -3:
			ShopsHandler.openShop(player, 174);
			end();
			break;

		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}
}