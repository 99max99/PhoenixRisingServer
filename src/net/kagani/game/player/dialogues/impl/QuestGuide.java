package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.controllers.NewSpawnPlayerController;
import net.kagani.game.player.dialogues.Dialogue;

public class QuestGuide extends Dialogue {

	int npcId;
	NewSpawnPlayerController controler;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		controler = (NewSpawnPlayerController) parameters[1];
		if (controler == null) {
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] {
					"Oracle of Dawn",
					"You now know everything to succeed in this",
					"dangerous world. Go prove what you are worth now!" },
					IS_NPC, npcId, 9827);
			stage = 7;
		} else {
			int s = controler.getStage();
			if (s == 2) {
				sendEntityDialogue(
						SEND_2_TEXT_CHAT,
						new String[] {
								"Oracle of Dawn",
								"Greetings! I see you are a new arrival in this land. My",
								"job is welcome all new visitors. So Welcome!" },
						IS_NPC, npcId, 9827);
			} else if (s == 4) {
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] {
						"Oracle of Dawn", "I'm glad you're making progress!" },
						IS_NPC, npcId, 9827);
				stage = 5;
			}
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] {
					"Oracle of Dawn",
					"You have already learned the first thing needed to",
					"succeed in this world talking to other people!" }, IS_NPC,
					npcId, 9827);
		} else if (stage == 0) {
			stage = 1;
			sendEntityDialogue(SEND_4_TEXT_CHAT, new String[] {
					"Oracle of Dawn",
					"This is a dangerous world where people kill themselves",
					"to increase their honor. Those people get their",
					"dangerous artefacts by spawning taking use of",
					"commands as ::item ITEMID AMMOUNT." }, IS_NPC, npcId, 9827);
		} else if (stage == 1) {
			stage = 2;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							"Oracle of Dawn",
							"They also pray their gods on altars to get boosts.",
							"Why don't you try too? Click on the zaros altar there and",
							"try switching your prayers." }, IS_NPC, npcId,
					9827);
			controler.updateProgress();
		} else if (stage == 5) {
			stage = 6;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] {
					"Oracle of Dawn",
					"To continue the tutorial head to the north",
					"and click on the wilderness ditch!" }, IS_NPC, npcId, 9827);
		} else {

			end();
		}

	}

	@Override
	public void finish() {

	}

}
