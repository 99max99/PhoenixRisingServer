package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;

public class Ahab extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, DRUNK, "Arrr, matey!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "I'm looking for Redbeard Frank.",
					"Arrr!", "Are you going to sit there all day?",
					"Do you want to trade?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "I'm looking for Redbeard Frank.");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "Arrr!");
				stage = 3;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL,
						"Are you going to sit there all day?");
				stage = 4;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "Do you have anything for trade?");
				stage = 18;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, HAPPY,
					"Redbeard Frank ye say? He be outside. Says he likes the",
					"feel of the wind on his cheeks.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(HAPPY, "Thanks.");
			stage = 50;
			break;
		case 3:
			sendNPCDialogue(npcId, DRUNK, "Arrr, matey!");
			stage = -1;
			break;
		case 4:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"Aye, I am. I canna walk, ye see.");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(NORMAL, "What's stopping you from walking?");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(
					npcId,
					SAD,
					"Arrr, I'ave only the one leg! I lost its twin when my last",
					"ship went down.");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(NORMAL, "But I can see both your legs!");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(
					npcId,
					SAD,
					"Nay, young laddie, this be a false leg. For years I had me a",
					"sturdy wooden peg-leg, but now I wear this dainty little",
					"feller.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yon peg-leg kept getting stuck in the floorboards.");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(NORMAL, "Right...");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Perhaps a bright young laddie like yerself would like to",
					"help me? I be needing another ship to go a-hunting my",
					"enemy.");
			stage = 12;
			break;
		case 12:
			sendPlayerDialogue(NORMAL, "Hmmm. And you can afford another ship?");
			stage = 13;
			break;
		case 13:
			sendNPCDialogue(npcId, SAD,
					"Nay, I have nary a penny to my name. All my wordly goods",
					"went down with me old ship.");
			stage = 14;
			break;
		case 14:
			sendPlayerDialogue(NORMAL,
					"So you're actually asking me to give you a free ship.");
			stage = 15;
			break;
		case 15:
			sendNPCDialogue(npcId, HAPPY, "Arrr! Would ye be so kind?");
			stage = 16;
			break;
		case 16:
			sendPlayerDialogue(MILDLY_ANGRY, "No I jolly well wouldn't!");
			stage = 17;
			break;
		case 17:
			sendNPCDialogue(npcId, SAD, "Arrr.");
			stage = 50;
			break;
		case 18:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Nothin' at the moment, but then again the Customs",
					"Agents are on the warpath right now.");
			stage = 50;
			break;
		case 50:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
