package net.kagani.game.player.dialogues.impl;

import net.kagani.Settings;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.QuestManager.Quests;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.dialogues.Dialogue;

public class Lucille extends Dialogue {

	/**
	 * @author: Paty
	 **/
	
	@Override
	public void start() {	
		if (player.getQuestManager().getQuestStage(Quests.SONG_FROM_THE_DEPTHS) == -2) {
			stage = -1;
			sendNPCDialogue(15469, 9761, "Wake up Waylan wake up!");
		} else if (player.getQuestManager().getQuestStage(Quests.SONG_FROM_THE_DEPTHS) > -1) {
			stage = 98;
			sendNPCDialogue(15469, 9827, "Have you managed to find a way to wake Waylan?");
		} else if (player.getQuestManager().getQuestStage(Quests.SONG_FROM_THE_DEPTHS) == -1) {
			stage = 100;
			sendNPCDialogue(15469, 9847, "Thank you so much for helping Waylan I am forever in your debt.");//temp till more quests
		}
	}
		
	@Override
	public void run(int interfaceId, int componentId) {
		
		if (stage == -1) {
			stage = 0;
			sendNPCDialogue(15469, 9761, "Come on, you great drunken oaf. This isn't funny.");
		} else if (stage == 0) {
			stage = 1;
			sendOptionsDialogue("Select an option:",
					"Is everything ok?",
					"Can't you wake him?",
					"[Quietly leave]");
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				stage = 2;
				sendNPCDialogue(15469, 9761, "No. Everything is not ok. I can't wake any of these men up.");
			} else if (componentId == OPTION_2) {
				stage = 2;
				sendNPCDialogue(15469, 9761, "No. Everything is not ok. I can't wake any of these men up.");
			} else if (componentId == OPTION_3) {
				end();
			}
		} else if (stage == 2) {
			stage = 3;
			sendNPCDialogue(15469, 9761, "They keep muttering things - strangely similar things - as though they're all sharing the same dream.");
		} else if (stage == 3) {
			stage = 4;
			sendNPCDialogue(15469, 9761, "Makes no sense.");
		} else if (stage == 4) {
			stage = 5;
			sendNPCDialogue(15469, 9761, "I've heard rumours of men affected like this. Lulled into sleep by a strange force. Never to awaken again.");
		} else if (stage == 5) {
			stage = 6;
			sendNPCDialogue(15469, 9761, "They just lie there, empty, as though their soul just upped and left somewhere.");
		} else if (stage == 6) {
			stage = 7;
			sendNPCDialogue(15469, 9761, "I don't know what to do. That's my husband there; my Waylan. I don't know what I'd do without him.");
		} else if (stage == 7) {
			stage = 8;
			sendOptionsDialogue("Select an option:",
					"Can I help?",
					"This isn't my problem.");
		} else if (stage == 8) {
			if (componentId == OPTION_1) {
				stage = 9;
				sendPlayerDialogue(9827, "Can I help?");
			} else if (componentId == OPTION_2) {
				stage = 100;
				sendPlayerDialogue(9827, "This isn't my problem.");
			}
		} else if (stage == 9) {
			stage = 10;
			sendNPCDialogue(15469, 9761, "Well, I ain't going to turn you down. Anything to get my Waylan back.");
		} else if (stage == 10) {
			stage = 11;
			sendNPCDialogue(15469, 9761, "I should warn you though; a warrior named the Raptor came here before you and offered to help.");
		} else if (stage == 11) {
			stage = 12;
			sendNPCDialogue(15469, 9761, "He terrified me. I'm not ashamed to admit it.");
		} else if (stage == 12) {
			stage = 13;
			sendNPCDialogue(15469, 9761, "I... gave him a Restless Sleep potion; an old recipe I learned from a witch some time ago.");
		} else if (stage == 13) {
			stage = 14;
			sendNPCDialogue(15469, 9761, "She said it would let me 'visit the world of dreams' - whatever that means.");
		} else if (stage == 14) {
			stage = 15;
			sendNPCDialogue(15469, 9761, "He drank it and then seemed to fade away to somewhere else. You'll need to drink it as well if you want to catch up with him.");
		} else if (stage == 15) {
			stage = 16;
			sendNPCDialogue(15469, 9761, "I'm not afraid to say that I fear the Raptor. You hear stories about him. His solution to a problem is to destroy everything linked to it. That's not who I want saving my husband.");
		} else if (stage == 16) {
			stage = 17;
			sendNPCDialogue(15469, 9761, "Protect my Waylan, would you? I know he's a drunkard, but he's MY drunkard.");
		//EOC Quest interface pops up to accept or decline here
		} else if (stage == 17) {
			stage = 18;
			sendOptionsDialogue("Select an option:",
					"Accept the quest",
					"Decline");
		} else if (stage == 18) {
			if (componentId == OPTION_1) {
				//player.lock();
				//player.getAttributes().put("DialogueException", Boolean.TRUE);//locked but can continue dialogue
				stage = 19;
				player.getQuestManager().setQuestStage(Quests.SONG_FROM_THE_DEPTHS, 0);
				//TODO map showing blue square quest area
				sendNPCDialogue(15469, 9761, "You'll need this if you want to follow the Raptor and Waylan. If it has the effect I think it will, you will enter some kind of dream world. Then make sure you get to Waylan before the Raptor does.");
			} else if (componentId == OPTION_2) {
				stage = 100;
				sendPlayerDialogue(9827, "I'm too busy to save Waylan sorry.");
			}
		} else if (stage == 19) {
			stage = 100;
			sendItemDialogue(24313, "Lucille hands you a Restless Sleep potion");
			if (player.getInventory().getFreeSlots() >= 1) {
				player.getInventory().addItem(24313, 1);
			} else {
				World.addGroundItem(new Item(24313, 1), new WorldTile(player), player, true, 60);
			}
		} else if (stage == 100) {
			stage = 101;
			end();
		} else
			end();
		}

	@Override
		public void finish() {
	}
		
}