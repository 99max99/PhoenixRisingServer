package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class Gee extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING, "Hello there, can I help you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Where am I?",
					"How are you today?",
					"Are there any quests I can do here?",
					"Where can I get a haircut like yours?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "Where am I?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "How are you today?");
				stage = 4;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"Do you know of any quests I can do?");
				stage = 10;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL,
						"Where can I get a haircut like yours?");
				stage = 66;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"This is the town of Lumbridge my friend.");
			stage = 2;
			break;
		case 2:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "How are you today?",
					"Do you know of any quests I can do?",
					"Your shoe lae is untied.");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "How are you today?");
				stage = 4;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"Do you know of any quests I can do?");
				stage = 10;
				break;
			case OPTION_3:
				sendPlayerDialogue(LISTENS_THEN_LAUGHS,
						"Your shoe lace is untied.");
				stage = 63;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Aye, not too bad thank you. Lovely weather in",
					"arrow this fine day.");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(NORMAL, "Weather?");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Yes weather, you know.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"The state or condition of the atmosphere at a time and",
					"place, with respect to variables such as temperature,",
					"moisture, wind velocity, and barometric pressure.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(PLAIN_TALKING, "...");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Not just a pretty face eh? Ha ha ha.");
			stage = -2;
			break;
		case 10:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"What kind of quest are you looking for?");
			stage = 11;
			break;
		case 11:
			sendOptionsDialogue(
					DEFAULT_OPTIONS_TITLE,
					"I fancy a bit of a fight, anything dangerous?",
					"Something easy please, I'm new here.",
					"I'm a thinker rather than fighter, anything skill oriented?",
					"I want to do all kinds of things, do you know of anything like that?",
					"Maybe another time.");
			stage = 12;
			break;
		case 12:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING,
						"I fancy a bit of a fight, anything dangerous?");
				stage = 13;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"Something easy please, I'm new here.");
				stage = 28;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'm a thinker rather than fighter, anything skill",
						"orientated?");
				stage = 41;
				break;
			case OPTION_4:
				sendPlayerDialogue(PLAIN_TALKING,
						"I want to do all kinds of things, do you know of",
						"anything like that?");
				stage = 50;
				break;
			case OPTION_5:
				sendPlayerDialogue(PLAIN_TALKING, "Maybe another time.");
				stage = -2;
				break;
			}
			break;
		case 13:
			sendNPCDialogue(npcId, NORMAL,
					"Hmm.. dangerous you say? What sort of creatures are",
					"you looking to fight?");
			stage = 14;
			break;
		case 14:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Big scary demons!",
					"Vampires!", "Small.. something small would be good.",
					"Maybe another time.");
			stage = 15;
			break;
		case 15:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "Big scary demons!");
				stage = 16;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "Vampires!");
				stage = 20;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"Small.. something small would be good.");
				stage = 23;
				break;
			case OPTION_4:
				sendPlayerDialogue(PLAIN_TALKING, "Maybe another time.");
				stage = -2;
				break;
			}
			break;
		case 16:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You are a brave soul indeed.");
			stage = 17;
			break;
		case 17:
			sendNPCDialogue(npcId, NORMAL,
					"Now that you mention it, I heard a rumour about a",
					"gypsy in Varrock who is rambling about some kind of",
					"greater evil.. sounds demon-like if you ask me.");
			stage = 18;
			break;
		case 18:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Perhaps you could check it out if you are as brave as",
					"you say?");
			stage = 19;
			break;
		case 19:
			sendPlayerDialogue(PLAIN_TALKING,
					"Thanks for the tip, perhaps I will.");
			stage = -2;
			break;
		case 20:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"Ha ha. I personally don't believe in such things.",
					"However, there is a man in Draynor Village who has",
					"been scaring the village folk with stories of vampires.");
			stage = 21;
			break;
		case 21:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"He's named Morgan and can be found in one of the",
					"village houses. Perhaps you could see what the matter",
					"is?");
			stage = 22;
			break;
		case 22:
			sendPlayerDialogue(PLAIN_TALKING, "Thanks for the tip.");
			stage = -2;
			break;
		case 23:
			sendNPCDialogue(npcId, NORMAL,
					"Small? Small isn't really that dangerous though is it?");
			stage = 24;
			break;
		case 24:
			sendPlayerDialogue(MILDLY_ANGRY,
					"Yes it can be! There could be anything from an evil",
					"chicken to a poisonous spider. They attack in numbers",
					"you know!");
			stage = 25;
			break;
		case 25:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes ok, point taken. Speaking of small monsters, I hear",
					"old Wizard Mizgog in the wizards' tower has just had",
					"all his beads taken by a gang of mischievous imps.");
			stage = 26;
			break;
		case 26:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Sounds like it could be a quest for you?");
			stage = 27;
			break;
		case 27:
			sendPlayerDialogue(PLAIN_TALKING, "Thanks for your help.");
			stage = -2;
			break;
		case 28:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I can tell you about plenty of small easy tasks.");
			stage = 29;
			break;
		case 29:
			sendNPCDialogue(npcId, NORMAL,
					"The Lumbridge cook has been having problems, the",
					"Duke is confused over some strange rocks and on top",
					"of all that, poor lad Romeo in Varrock has girlfriend",
					"problems.");
			stage = 30;
			break;
		case 30:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "The Lumbridge cook.",
					"The Duke's strange stones.", "Romeo and his girlfriend.",
					"Maybe another time.");
			stage = 31;
			break;
		case 31:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Tell me about the Lumbridge cook.");
				stage = 32;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"Tell me about the Duke's strange stones.");
				stage = 35;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"Tell me about Romeo and his girlfriend please.");
				stage = 37;
				break;
			case OPTION_4:
				sendPlayerDialogue(PLAIN_TALKING, "Maybe another time.");
				stage = -2;
				break;
			}
			break;
		case 32:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"It's funny really, the cook would forget his head if it",
					"wasn't screwed on. This time he forgot to get",
					"ingredients for the Duke's birthday cake.");
			stage = 33;
			break;
		case 33:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Perhaps you could help him? You will probably find him",
					"in the Lumbridge Castle kitchen.");
			stage = 34;
			break;
		case 34:
			sendPlayerDialogue(PLAIN_TALKING,
					"Thank you. I shall go speak with him.");
			stage = -2;
			break;
		case 35:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well the Duke of Lumbridge has found a strange stone",
					"that no one seems to understand. Perhaps you could",
					"help him? You can probably find him upstairs in",
					"Lumbridge Castle.");
			stage = 36;
			break;
		case 36:
			sendPlayerDialogue(PLAIN_TALKING,
					"Sounds mysterious. I may just do that. Thanks.");
			stage = -2;
			break;
		case 37:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Romeo in Varrock needs help with finding his beloved",
					"Juliet, you may be able to help him out.");
			stage = 38;
			break;
		case 38:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Unless of course you manage to find Juliet first in",
					"which case she has probably lost Romeo.");
			stage = 39;
			break;
		case 39:
			sendPlayerDialogue(PLAIN_TALKING, "Right, ok. Romeo is in Varrock?");
			stage = 40;
			break;
		case 40:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes you can't miss him, he's wandering aimlessly in the",
					"square.");
			stage = -2;
			break;
		case 41:
			sendNPCDialogue(npcId, NORMAL,
					"Skills play a big part when you want to progress in",
					"knowledge throughout arrow. I know of a few skill-",
					"related quests that can get you started.");
			stage = 42;
			break;
		case 42:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You may be able to help out Fred the farmer who is in",
					"need of someones crafting expertise.");
			stage = 43;
			break;
		case 43:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Or, there's always Doric the dwarf who needs an",
					"errand running for him?");
			stage = 44;
			break;
		case 44:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Fred the farmer.",
					"Doric the dwarf.", "Maybe another time.");
			stage = 45;
			break;
		case 45:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL,
						"Tell me about Fred the farmer please.");
				stage = 46;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Tell me about Doric the dwarf.");
				stage = 48;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING, "Maybe another time.");
				stage = -2;
				break;
			}
			break;
		case 46:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You can find Fred next to the field of sheep in",
					"Lumbridge. Perhaps you should go and speak with him.");
			stage = 47;
			break;
		case 47:
			sendPlayerDialogue(NORMAL, "Thanks, maybe I will.");
			stage = -2;
			break;
		case 48:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Doric the dwarf is located north of Falador. He might",
					"be able to help you with smithing. You should speak to",
					"him. He may let you use his anvils.");
			stage = 49;
			break;
		case 49:
			sendPlayerDialogue(PLAIN_TALKING, "Thanks for the tip.");
			stage = -2;
			break;
		case 50:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Of course I do. arrow is a huge place you know,",
					"now let me think...");
			stage = 51;
			break;
		case 51:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Hetty the witch in Rimmington might be able to offer",
					"help in the ways of magical abilities..");
			stage = 52;
			break;
		case 52:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Also, pirates are currently docked in Port Sarim,",
					"Where pirates are, treasure is never far away...");
			stage = 53;
			break;
		case 53:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Or you could go help out Ernest who got lost in",
					"Draynor Manor, spooky place that.");
			stage = 54;
			break;
		case 54:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Hetty the witch.",
					"Pirate's treasure.", "Ernest and Draynor Manor.",
					"Maybe another time.");
			stage = 55;
			break;
		case 55:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING,
						"Tell me about Hetty the witch.");
				stage = 56;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"Tell me about Pirate's Treasure.");
				stage = 58;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"Tell me about Ernets please.");
				stage = 60;
				break;
			case OPTION_4:
				break;
			}
			break;
		case 56:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Hetty the witch can be found in Rimmington, south of",
					"Falador. She's currently working on some new potions.",
					"Perhaps you could give her a hand? She might be able",
					"to offer help with your magical abilities.");
			stage = 57;
			break;
		case 57:
			sendPlayerDialogue(PLAIN_TALKING,
					"Ok thanks, let's hope she doesn't turn me into a potato",
					"or something..");
			stage = -2;
			break;
		case 58:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"RedBeard Frank in Port Sarim's bar, the Rusty",
					"Anchor might be able to tell you about the rumored",
					"treasure that is buried somewhere in arrow.");
			stage = 59;
			break;
		case 59:
			sendPlayerDialogue(PLAIN_TALKING,
					"Sounds adventurous, I may have to check that out.",
					"Thank you.");
			stage = -2;
			break;
		case 60:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"The best place to start would be at the gate to",
					"Draynor Manor. There you will find Veronica who will",
					"be able to tell you more.");
			stage = 61;
			break;
		case 61:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I suggest you tread carefully in that place; it's haunted.");
			stage = 62;
			break;
		case 62:
			sendPlayerDialogue(NORMAL,
					"Sounds like fun. I've never been to a Haunted Manor",
					"before.");
			stage = -2;
			break;
		case 63:
			sendNPCDialogue(npcId, MILDLY_ANGRY, "No it's not!");
			stage = 64;
			break;
		case 64:
			sendPlayerDialogue(LISTENS_THEN_LAUGHS,
					"No you're right. I have nothing to back that up.");
			stage = 65;
			break;
		case 65:
			sendNPCDialogue(npcId, MILDLY_ANGRY, "Fool! Leave me alone!");
			stage = -2;
			break;
		case 66:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes, it does look like you need a hairdresser.");
			stage = 67;
			break;
		case 67:
			sendPlayerDialogue(MILDLY_ANGRY, "Oh thanks!");
			stage = 68;
			break;
		case 68:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"No problem. The hairdresser in Falador will probably be",
					"able to sort you out.");
			stage = 69;
			break;
		case 69:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"The Lumbridge general store sells useful maps if you",
					"don't know the way.");
			stage = -2;
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
