package net.kagani.game.player.dialogues.impl.cities.keldagrim;

import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class BlastfurnaceForeman extends Dialogue {
	Player player;
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		player = (Player) parameters[1];
		sendNPCDialogue(npcId, 9827,
				"Hello, would you like to browse my store?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Choose an option", "Yes, please.",
					"No, thanks.");
			stage++;
		} else if (stage == 0) {
			switch (componentId) {
			case (OPTION_1):
				end();
				ShopsHandler.openShop(player, 31);
				break;
			case (OPTION_2):
				stage++;
				sendNPCDialogue(npcId, 9827, "Well, perhaps another time.");
				break;
			}
		} else
			end();
	}

	@Override
	public void finish() {
	}
}
