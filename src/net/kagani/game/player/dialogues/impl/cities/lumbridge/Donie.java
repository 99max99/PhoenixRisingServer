package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class Donie extends Dialogue {

	int npcId;

	int random = 0;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT,
				new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
						"Hello there, can I help you?" }, IS_NPC, npcId, 9850);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			random = Utils.random(2);
			stage = 1;
			if (random == 0) {
				sendOptionsDialogue(DEFAULT,
						"Do you have anything of value which I can have?",
						"Are there any quests I can do here?",
						"Can I buy your stick?");
			} else if (random == 1) {
				sendOptionsDialogue(DEFAULT, "Where am I?",
						"How are you today?",
						"Are there any quests I can do here?",
						"Where can I get a haircut like yours?");
			} else {
				sendOptionsDialogue(DEFAULT,
						"Are there any quests I can do here?",
						"Can I buy your stick?");
			}
			break;
		case 1:
			if (random == 0) {
				if (componentId == OPTION_1) {
					sendPlayerDialogue(9827,
							"Do you have anything of value which I can have?");
					stage = 5;
				} else if (componentId == OPTION_2) {
					sendPlayerDialogue(9827,
							"Are there any quests I can do here?");
					stage = 14;
				} else if (componentId == OPTION_3) {
					sendPlayerDialogue(9827, "Can I buy your stick?");
					stage = 51;
				}
			} else if (random == 1) {
				if (componentId == OPTION_1) {
					sendPlayerDialogue(9827, "Where am I?");
					stage = 40;
				} else if (componentId == OPTION_2) {
					sendPlayerDialogue(9827, "How are you today?");
					stage = 41;
				} else if (componentId == OPTION_3) {
					sendPlayerDialogue(9827,
							"Are there any quests I can do here?");
					stage = 14;
				} else {
					sendPlayerDialogue(9827,
							"Where can I get a haircut like yours?");
					stage = 47;
				}
			} else if (random == 2) {
				if (componentId == OPTION_1) {
					sendPlayerDialogue(9827,
							"Are there any quests I can do here?");
					stage = 14;
				} else if (componentId == OPTION_2) {
					sendPlayerDialogue(9827, "Can I buy your stick?");
					stage = 14;
				}
			}
			break;
		// op1
		case 5:
			stage = 6;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Are you asking for free stuff?" }, IS_NPC, npcId,
					9827);
			break;
		case 6:
			sendPlayerDialogue(9827, "Well... er... yes.");
			break;
		case 7:
			stage = 11;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"No I do not have anything I can give you. If I did have",
							"anything of value I wouldn't want to give it away." },
					IS_NPC, npcId, 9785);
			break;
		case 11:
			end();
			break;
		// op2
		case 14:
			stage = 15;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"What kind of quest are you looking for?" },
					IS_NPC, npcId, 9827);
			break;
		case 15:
			stage = 16;
			sendOptionsDialogue(
					DEFAULT,
					"I fancy a bit of a fight, anything dangerous?",
					"Something easy please, I'm new here.",
					"I'm a thinker rather than fighter, anything skill oriented?",
					"I want to do all kinds of things, do you know of anything like that?",
					"Maybe another time.");
			break;
		case 16:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827,
						"I fancy a bit of a fight, anything dangerous?");
				stage = 17;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827, "Something easy please, I'm new here.");
				stage = 60;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(9827,
						"I'm a thinker rather than fighter, anything skill oriented?");
				stage = 67;
			} else if (componentId == OPTION_4) {
				sendPlayerDialogue(9827,
						"I want to do all kinds of things, do you know of anything like that?");
				stage = 74;
			} else {
				sendPlayerDialogue(9827, "Maybe another time.");
				stage = 24;
			}
			break;
		case 17:
			stage = 18;
			sendEntityDialogue(
					SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Hmm.. dangerous you say? What sort of creatures are you",
							"looking to fight?" }, IS_NPC, npcId, 9827);
			break;
		case 18:
			stage = 19;
			sendOptionsDialogue(DEFAULT, "Big scary demons!", "Vampyres!",
					"Small.. something small would be good.",
					"Maybe another time.");
			break;
		case 19:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827, "Big scary demons!");
				stage = 25;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827, "Vampyres!");
				stage = 21;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(9827,
						"Small.. something small would be good.");
				stage = 28;
			} else if (componentId == OPTION_4) {
				sendPlayerDialogue(9827, "Maybe another time.");
				stage = 24;
			}
			break;
		case 20:
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"What kind of quest are you looking for?" },
					IS_NPC, npcId, 9827);
			break;
		case 21:
			stage = 22;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Ha ha. I personally don't believe in such things. However,",
							"there is a man in Draynor Village who has been scaring the",
							"village folk with stories of vampyres." }, IS_NPC,
					npcId, 9841);
			break;
		case 22:
			stage = 23;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"He's named Morgan and can be found in one of the village",
							"houses. Perhaps you could see what the matter is?" },
					IS_NPC, npcId, 9850);
			break;
		case 23:
			stage = 24;
			sendPlayerDialogue(9827, "Thanks a lot!");
			break;
		case 24:
			end();
			break;
		case 25:
			stage = 26;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"You are a brave soul indeed." }, IS_NPC, npcId,
					9840);
			break;
		case 26:
			stage = 27;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Now that you mention it, I heard a rumour about a",
							"Saradominist in the Varrock church who is rambling about",
							"some kind of greater evil.. sounds demon-like if you ask",
							"me." }, IS_NPC, npcId, 9827);
			break;
		case 27:
			stage = 23;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Perhaps you could check it out if you are as brave as you",
							"say?" }, IS_NPC, npcId, 9827);
			break;
		case 28:
			stage = 29;
			sendEntityDialogue(
					SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Small? Small isn't really that dangerous though is it?" },
					IS_NPC, npcId, 9827);
			break;
		case 29:
			stage = 30;
			sendPlayerDialogue(
					9785,
					"Yes it can be! There could be anything from an evil",
					"chicken to a poisonous spider. They attack in numbers you",
					"know!");
			break;
		case 30:
			stage = 31;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Yes ok, point taken. Speaking of small monsters, I hear",
							"old Wizard Mizgog in the wizards' tower has just had all his",
							"beads taken by a gang of mischievous imps." },
					IS_NPC, npcId, 9850);
			break;
		case 31:
			stage = 23;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Sounds like it could be a quest for you?" },
					IS_NPC, npcId, 9850);
			break;
		case 40:
			stage = 24;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"This is the town of Lumbridge my friend." },
					IS_NPC, npcId, 9840);
			break;
		case 41:
			stage = 42;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Aye, not too bad thank you. Lovely weather in RuneScape",
							"this fine day." }, IS_NPC, npcId, 9827);
			break;
		case 42:
			stage = 43;
			sendPlayerDialogue(9740, "Weather?");
			break;
		case 43:
			stage = 44;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Yes weather, you know." }, IS_NPC, npcId, 9827);
			break;
		case 44:
			stage = 45;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"The state of condition of the atmosphere at a time and",
							"place, with respect to variables such as temperature,",
							"moisture, wind velocity, and barometric pressure." },
					IS_NPC, npcId, 9827);
			break;
		case 45:
			stage = 46;
			sendPlayerDialogue(9836, "...");
			break;
		case 46:
			stage = 24;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Not just a pretty face eh? Ha ha ha." }, IS_NPC,
					npcId, 9840);
			break;
		case 47:
			stage = 48;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Yes, it does look like you need a hairdresser." },
					IS_NPC, npcId, 9840);
			break;
		case 48:
			stage = 49;
			sendPlayerDialogue(9785, "Oh thanks!");
			break;
		case 49:
			stage = 50;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"No problem. The hairdresser in Falador will probably be",
							"able to sort you out." }, IS_NPC, npcId, 9840);
			break;
		case 50:
			stage = 24;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"The Lumbridge general store sells useful maps if you don't",
							"know the way." }, IS_NPC, npcId, 9850);
			break;
		case 51:
			stage = 52;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"It's not a stick! I'll have you know it's a very powerful",
							"staff!" }, IS_NPC, npcId, 9785);
			break;
		case 52:
			stage = 53;
			sendPlayerDialogue(9827, "Really? Show me what it can do!");
			break;
		case 53:
			stage = 54;
			sendEntityDialogue(SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Um.. It's a bit low on power at the moment.." },
					IS_NPC, npcId, 9770);
			break;
		case 54:
			stage = 55;
			sendPlayerDialogue(9840, "It's a stick isn't it?");
			break;
		case 55:
			stage = 56;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"..Ok it's a stick.. But only while I save up for a staff. Zaff",
							"in Varrock square sells them in his shop." },
					IS_NPC, npcId, 9770);
			break;
		case 56:
			stage = 24;
			sendPlayerDialogue(9840, "Well good luck with that.");
			break;
		case 60:
			stage = 61;
			sendEntityDialogue(
					SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"I can tell you about plenty of small easy tasks." },
					IS_NPC, npcId, 9850);
			break;
		case 61:
			stage = 62;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"The Lumbridge cook has been having problems and the",
							"Duke is confused over some strange rocks." },
					IS_NPC, npcId, 9827);
			break;
		case 62:
			stage = 63;
			sendOptionsDialogue("Tell me about...", "The Lumbridge cook.",
					"The Duke's strange stones.", "Maybe another time.");
			break;
		case 63:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827,
						"Tell me about The Lumbridge cook, please.");
				stage = 64;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827,
						"Tell me about The Duke's strange stones, please.");
				stage = 66;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(9827, "Maybe another time.");
				stage = 24;
			}
			break;
		case 64:
			stage = 65;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"It's funny really, the cook would forget his head if it",
							"wasn't screwed on. This time he forgot to get ingredients for the Duke's birthday cake." },
					IS_NPC, npcId, 9840);
			break;
		case 65:
			stage = 23;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Perhaps you could help him? You will probably find him in",
							"the Lumbridge Castle kitchen." }, IS_NPC, npcId,
					9827);
			break;
		case 66:
			stage = 23;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Well the Duke of Lumbridge has found a strange stone",
							"that no one seems to understand. Perhaps you could help",
							"him? You can probably find him upstairs in Lumbridge",
							"Castle." }, IS_NPC, npcId, 9850);
			break;
		case 67:
			stage = 68;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Skills play a big part when you want to progress in",
							"knowledge throughout RuneRetro. I know of a few skill-",
							"related quests that can get you started." },
					IS_NPC, npcId, 9827);
			break;
		case 68:
			stage = 69;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"You may be able to help out Fred the farmer who is in",
							"need of someones crafting expertise." }, IS_NPC,
					npcId, 9850);
			break;
		case 69:
			stage = 70;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Or, there's always Doric the dwarf who needs an errand",
							"running for him?" }, IS_NPC, npcId, 9850);
			break;
		case 70:
			stage = 71;
			sendOptionsDialogue("Tell me about...", "Fred the farmer.",
					"Doric the dwarf.", "Maybe another time.");
			break;
		case 71:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827,
						"Tell me about Fred the farmer, please.");
				stage = 72;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827,
						"Tell me about Doric the dwarf, please.");
				stage = 73;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(9827, "Maybe another time.");
				stage = 24;
			}
			break;
		case 72:
			stage = 23;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"You can find Fred next to the field of sheep in Lumbridge.",
							"Perhaps you should go and speak with him." },
					IS_NPC, npcId, 9827);
			break;
		case 73:
			stage = 23;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Doric the dwarf is located north of Falador. He might be",
							"able to help you with smithing. You should speak to him. He",
							"may let you use his anvils." }, IS_NPC, npcId,
					9850);
			break;
		case 74:
			stage = 75;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Of course I do. RuneRetro is a huge place you know, now",
							"let me think..." }, IS_NPC, npcId, 9850);
			break;
		case 75:
			stage = 76;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Hetty the witch in Rimmington might be able to offer help",
							"in the ways of magical abilities.." }, IS_NPC,
					npcId, 9850);
			break;
		case 76:
			stage = 77;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Also, pirates are currently docked in Port Sarim, Where",
							"pirates are, treasure is never far away..." },
					IS_NPC, npcId, 9850);
			break;
		case 77:
			stage = 78;
			sendEntityDialogue(
					SEND_2_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Or you could go help out Ernest who got lost in Draynor",
							"Manor, spooky place that." }, IS_NPC, npcId, 9850);
			break;
		case 78:
			stage = 79;
			sendOptionsDialogue("Tell me about...", "Hetty the Witch.",
					"Pirate's treasure.", "Ernest and Draynor Manor.",
					"Maybe another time.");
			break;
		case 79:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827,
						"Tell me about Hetty the Witch, please.");
				stage = 80;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827,
						"Tell me about Pirate's treasure, please.");
				stage = 81;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(9827,
						"Tell me about Ernest and Draynor Manor, please.");
				stage = 82;
			} else if (componentId == OPTION_4) {
				sendPlayerDialogue(9827, "Maybe another time.");
				stage = 24;
			}
			break;
		case 80:
			stage = 23;
			sendEntityDialogue(
					SEND_4_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"Hetty the witch can be found in Rimmington, south of",
							"Falador. She's currently working on some new potions.",
							"Perhaps you could give her a hand? She might be able to",
							"offer help with your magical abilities." },
					IS_NPC, npcId, 9850);
			break;
		case 81:
			stage = 23;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"RedBeard Frank in Port Sarim's bar, the Rusty Anchor",
							"might be able to tell you about the rumored treasure that",
							"is buried somewhere in RuneRetro." }, IS_NPC,
					npcId, 9850);
			break;
		case 82:
			stage = 83;
			sendEntityDialogue(
					SEND_3_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"The best place to start would be at the gate to Draynor",
							"Manor. There you will find Veronica who will be able to tell",
							"you more." }, IS_NPC, npcId, 9850);
			break;
		case 83:
			stage = 23;
			sendEntityDialogue(
					SEND_1_TEXT_CHAT,
					new String[] {
							NPCDefinitions.getNPCDefinitions(npcId).name,
							"I suggest you tread carefully in that place; it's haunted." },
					IS_NPC, npcId, 9850);
			break;
		}
	}

	@Override
	public void finish() {

	}
}