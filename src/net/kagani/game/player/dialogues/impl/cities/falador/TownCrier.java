package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Town Crier
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class TownCrier extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Hear ye!Hear ye! Player Moderators massive help to", "Arr-");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Oh, hello citizen. Are you here to find out about Player Moderators? Or perhaps would you like to know about the",
					"laws of the land?");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT, "Tellme about Player Moderators.",
					"Tell me about the Rules of Arrow.",
					"Can you give me a handy tip please?");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "Tell me about Player Moderators.");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "Tell me about the Rules of Arrow.");
				stage = 23;
				break;
			case OPTION_3:
				sendPlayerDialogue(HAPPY, "Can you give me a handy tip please?");
				stage = 100;
				break;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, HAPPY,
					"Of course. What would you like to know?");
			stage = 3;
			break;
		case 3:
			sendOptionsDialogue(DEFAULT, "What is a Player Moderator?",
					"What can Player Moderators do?",
					"How do I become a Player Moderator?",
					"What can Player Moderators not do?", "Something else.");
			stage = 4;
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "What is a Player Moderator?");
				stage = 5;
				break;
			case OPTION_2:
				sendPlayerDialogue(CONFUSED, "What can Player Moderators do?");
				stage = 10;
				break;
			case OPTION_3:
				sendPlayerDialogue(CONFUSED,
						"How do I become a Player Moderator?");
				stage = 12;
				break;
			case OPTION_4:
				sendPlayerDialogue(CONFUSED,
						"What can Player Moderators not do?");
				stage = 17;
				break;
			case OPTION_5:
				sendOptionsDialogue(DEFAULT,
						"Tell me about Player Moderators.",
						"Tell me about the Rules of Arrow",
						"Can you give me a handy tip please?");
				stage = 21;
				break;
			}
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Player Moderators are normal players of the game, just",
					"like you. However, since they have shown themselves to be",
					"trust worthy and active reporters, they have been invited by Arrow to monitor the game and take appropriate");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"action when they see rule breaking. You can spot a Player",
					"Moderator in game by looking at the chat screen - when a",
					"Player Moderator speaks, a silver crown appears to the",
					"left of their name. Remember, if there's no silver crown");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, HAPPY,
					"there, they are not a Player Moderator! You can check",
					"out the website if you'd like more information.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(HAPPY, "Thanks!");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, CONFUSED,
					"Is there anything else you'd like to know?");
			stage = 0;
			break;
		case 10:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Player Moderators, or 'P-Mods', have the ability to mute",
					"rule breakers and Arrow view their reports as a priority so",
					"that action is taken as quickly as possible. P-Mods also",
					"have access to the Player Moderator Centre. Within the");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(npcId, HAPPY,
					"Centre are tools to help them Moderate Arrow.",
					"These tools include dedicated forums, the Player",
					"Moderator Guidelines and the Player Moderator Code of",
					"Conduct.");
			stage = 9;
			break;
		case 12:
			sendNPCDialogue(npcId, HAPPY,
					"Arrow picks players who spend their time and effort to",
					"help better the Arrow community. To increase your",
					"chances of becoming a Player Moderator:");
			stage = 13;
			break;
		case 13:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Keep your account secure! This is very important, as a",
					"player with poor security will never be a P-Mod. Read our",
					"Security Tips for more information.");
			stage = 14;
			break;
		case 14:
			sendNPCDialogue(npcId, HAPPY,
					"Play by the rules! The rules of Arrow are enforced",
					"for a reason, to make the game a fair and enjoyable",
					"environment for all.");
			stage = 15;
			break;
		case 15:
			sendNPCDialogue(npcId, HAPPY,
					"Report accurately! When Arrow consider an account for",
					"review they look for quality, not quantity. Ensure your",
					"reports are of a high quality by following the report",
					"guidelines.");
			stage = 16;
			break;
		case 16:
			sendNPCDialogue(npcId, HAPPY,
					"Be excellent to each other! Treat others as you would",
					"want to be treated yourself. Respect your fellow player.",
					"More information can be found on the website.");
			stage = 8;
			break;
		case 17:
			sendNPCDialogue(npcId, HAPPY,
					"P-Mods cannot ban your account - they can only report",
					"offences. Arrow then take action based on the evidence",
					"received. If you lose your password or get scammed by",
					"another player, P-Mods cannot help you get your account");
			stage = 18;
			break;
		case 18:
			sendNPCDialogue(npcId, HAPPY,
					"back. All they can do is recommend you to go to Player",
					"Support. They cannot retrieve any items you may have",
					"lost and they certainly do not receive any free items",
					"from Arrow for moderating the game. They are players");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(npcId, HAPPY,
					"who give their all to help the community, out of the",
					"goodness of their hearts! P-Mods do not work for Arrow",
					"and so cannot make you a Moderator, or recommend",
					"other accounts to become Moderators. If you wish to");
			stage = 20;
			break;
		case 20:
			sendNPCDialogue(npcId, HAPPY,
					"become a Moderator, feel free to ask me!");
			stage = 8;
			break;
		case 21:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "Tell me about Player Moderators.");
				stage = 22;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "Tell me about the Rules of Arrow.");
				stage = 23;
				break;
			case OPTION_3:
				sendPlayerDialogue(HAPPY, "Can you give me a handy tip please?");
				stage = 24;
				break;
			}
			break;
		case 22:
			sendNPCDialogue(npcId, HAPPY,
					"Of course. What would you like to know?");
			stage = 3;
			break;
		case 23:
			sendNPCDialogue(npcId, HAPPY,
					"At once. Take a look at my book here.");
			stage = 100;
			break;
		case 24:
			sendNPCDialogue(npcId, HAPPY,
					"Take time to check the second trade window carefully.",
					"Don't be scammed!");
			stage = 25;
			break;
		case 25:
			sendNPCDialogue(npcId, CONFUSED,
					"Is there anything else you'd like to know?");
			stage = 26;
			break;
		case 26:
			sendOptionsDialogue(DEFAULT, "Tell me about Player Moderators.",
					"Tell me about something else.", "No thanks.");
			stage = 27;
			break;
		case 27:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Tell me about Player Moderators.");
				stage = 3;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "Tell me about something else.");
				stage = 1;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "No thanks.");
				stage = 100;
				break;
			}
			break;
		case 100:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}