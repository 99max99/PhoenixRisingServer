package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Logger;

public class FremennikShipMaster extends Dialogue {

	private int npcId;
	private NPC npc;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (npc.getX() == 3254 && npc.getY() == 3170) {
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You want a passage to Daemonheim?");
			stage = 1;
		} else if (npc.getX() == 3867 && npc.getY() == 3406) {
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You want a passage to Daemonheim?");
			stage = 1;
		} else if (npc.getX() == 3513 && npc.getY() == 3694) {
			sendNPCDialogue(npcId, HAPPY,
					"Do you want a lift back to the south?");
			stage = 13;
		}

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			sendOptionsDialogue(DEFAULT, "Yes, please.",
					"Not right now, thanks.", "Daemonheim?",
					"Why are you so grumpy?");
			stage = 2;
			break;

		case 2:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"Well, don't stand around. Get on board.");
				stage = -2;
				toDaemonheim();
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, ANGRY,
						"Well, be on your way then. Leave me in peace!");
				stage = -2;
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"Yes, the icy peninsula far to the north of here.");
				stage = 3;
				break;
			case OPTION_4:
				sendNPCDialogue(npcId, MAD,
						"Grumpy? I should kill you where you stand!");
				stage = 8;
				break;
			}
			break;

		case 3:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Ice, snow, harsh winds...");
			stage = 4;
			break;

		case 4:
			sendNPCDialogue(npcId, ANGRY,
					"...and no sand or swamp sludge, clogging up every orifice.");
			stage = 5;
			break;

		case 5:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Are you done with questions? Can we go now?");
			stage = 6;
			break;

		case 6:
			sendOptionsDialogue(DEFAULT, "Yes, please.",
					"Not right now, thanks.", "Why are you so grumpy?");
			stage = 7;
			break;

		case 7:
			sendNPCDialogue(npcId, MAD,
					"Grumpy? I should kill you where you stand!");
			stage = 8;
			break;

		case 8:
			sendNPCDialogue(npcId, ANGRY,
					"But that wouldn't help with this damned humidity.");
			stage = 9;
			break;

		case 9:
			sendNPCDialogue(npcId, UPSET,
					"I need the snow in my boots, the sea wind stinging in my face...");
			stage = 10;
			break;

		case 10:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"That's why i want to leave. Are you ready to go to Daemonheim?");
			stage = 11;
			break;

		case 11:
			sendOptionsDialogue(DEFAULT, "Yes, please.",
					"Not right now, thanks.", "Daemonheim?");
			stage = 12;
			break;

		case 12:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"Well, don't stand around. Get on board.");
				stage = -2;
				toDaemonheim();
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, ANGRY,
						"Well, be on your way then. Leave me in peace!");
				stage = -2;
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"Yes, the icy peninsula far to the north of here.");
				stage = 3;
				break;
			}
			break;

		case 13:
			sendOptionsDialogue(DEFAULT, "Yes, please (Al Kharid).",
					"Not right now, thanks.", "You look happy.");
			stage = 15;
			break;

		case 14:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, HAPPY, "All aboard, then.");
				toAlKharid();
				stage = -2;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, HAPPY, "All aboard, then.");
				toTaverley();
				stage = -2;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Suit yourself. I'll be here soaking up the atmosphere if you change your mind.");
				stage = -2;
				break;
			case OPTION_4:
				sendNPCDialogue(npcId, HAPPY,
						"Indeed, brother! I simply can't get enough of this place.");
				stage = 16;
				break;
			}
			break;

		case 15:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, HAPPY, "All aboard, then.");
				toAlKharid();
				stage = -2;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Suit yourself. I'll be here soaking up the atmosphere if you change your mind.");
				stage = -2;
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, HAPPY,
						"Indeed, brother! I simply can't get enough of this place.");
				stage = 16;
				break;
			}
			break;

		case 16:
			sendNPCDialogue(npcId, HAPPY,
					"The brisk sea air, the refreshing chill. It really gets the blood pumping.");
			stage = 17;
			break;

		case 17:
			sendNPCDialogue(npcId, HAPPY,
					"Anyway, listen to me ramble. Are we set to sail south?");
			stage = 18;
			break;

		case 18:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Suit yourself. I'll be here soaking up the atmosphere if you change your mind.");
			stage = -2;
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

	}

	void toDaemonheim() {
		final long time = FadingScreen.fade(player);
		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					FadingScreen.unfade(player, time, new Runnable() {
						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(3512, 3693, 0));
							player.lock(3);
						}
					});
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		});
	}

	void toAlKharid() {
		final long time = FadingScreen.fade(player);
		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					FadingScreen.unfade(player, time, new Runnable() {
						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(3254, 3169, 0));
							player.lock(3);
						}
					});
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		});
	}

	void toTaverley() {
		final long time = FadingScreen.fade(player);
		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					FadingScreen.unfade(player, time, new Runnable() {
						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(2867, 3405, 0));
							player.lock(3);
						}
					});
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		});
	}
}
