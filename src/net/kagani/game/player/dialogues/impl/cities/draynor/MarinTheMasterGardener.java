package net.kagani.game.player.dialogues.impl.cities.draynor;

import net.kagani.game.player.dialogues.Dialogue;

public class MarinTheMasterGardener extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		options(DEFAULT, "Skill cape of Farming", "General Chat");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				npc(HAPPY,
						"This is a Skillcape of Farming, isn't it incredible?",
						"It's a symbol of my ability as the finest farmer in the land!");
				stage = -2;// TODO add buying cape
				break;

			case OPTION_2:
				npc(SAD, "I can't chat now, I have too many things to worry");
				stage = -2;
				break;
			}
			break;

		case -2:
			end();
			break;

		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}
}