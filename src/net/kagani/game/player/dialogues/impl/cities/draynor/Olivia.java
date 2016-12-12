package net.kagani.game.player.dialogues.impl.cities.draynor;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

/**
 * 
 * @author Frostbite
 *
 *         <contact@frostbitersps@gmail.com><skype@frostbitersps>
 */

public class Olivia extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		npc(HAPPY, "Would you like to trade in seeds?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			options(DEFAULT, "Yes", "No.",
					"Where do I get higher-level seeds from?");
			stage = 1;
			break;

		case 1:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 99);
				end();
				break;
			case OPTION_2:
				end();
				break;
			case OPTION_3:
				npc(NORMAL,
						"Master farmers usually carry a few higher-level seeds",
						"around with them, although I don't know if they'd want",
						"to part with them for any price, to be honest.");
				stage = -2;
				break;
			}
			break;

		case -2:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
