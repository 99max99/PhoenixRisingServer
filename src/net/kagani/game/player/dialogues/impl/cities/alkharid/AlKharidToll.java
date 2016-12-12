package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.network.decoders.handlers.ObjectHandler;

public class AlKharidToll extends Dialogue {

	private WorldObject object;

	@Override
	public void start() {
		object = (WorldObject) parameters[0];
		sendPlayerDialogue(CONFUSED, "Can I come through this gate?");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendNPCDialogue(926, NORMAL,
					"You must pay a toll of 10 gold coins to pass.");
			break;
		case 0:
			if (player.getMoneyPouch().getCoinsAmount() < 10) {
				sendOptionsDialogue(DEFAULT, "Okay, I'll pay.",
						"Who does my money go to?",
						"No thanks, I'll walk around.");
				stage = 1;
			} else {
				sendOptionsDialogue(DEFAULT, "Who does my money go to?",
						"I haven't got that much.");
				stage = 31;
			}
			break;
		case 1:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "Okay, I'll pay.");
				stage = 2;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(CONFUSED, "Who does my money go to?");
				stage = 21;
			} else {
				end();
			}
			break;
		case 2:
			passGate();
			end();
			break;
		case 21:
			sendNPCDialogue(926, NORMAL,
					"The money goes to the city of Al-Kharid.");
			stage = -2;
			break;
		case 31:
			if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL, "I haven't got that much.");
				stage = -2;
			} else if (componentId == OPTION_1) {
				sendPlayerDialogue(CONFUSED, "Who does my money go to?");
				stage = 21;
			} else {
				end();
			}
			break;
		default:
			end();
			break;
		}

	}

	public void passGate() {
		WorldObject gate = object;
		if (object.getId() == 35551) {
			gate = World.getObjectWithId(new WorldTile(3268, 3227, 0), 35549);
		}
		ObjectHandler.handleGate(player, gate, 1200);
		player.lock(2);
		player.getMoneyPouch().sendDynamicInteraction(10, true);
		player.addWalkSteps(player.getX() == object.getX() ? object.getX() - 1
				: object.getX(), object.getY(), 1, false);
	}

	@Override
	public void finish() {
	}

}