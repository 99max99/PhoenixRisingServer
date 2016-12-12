package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Guardsman Peale
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class GuardsmanPeale extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(HAPPY, generateRandomGreetings());
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, HAPPY, generateRandomResponses());
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Tell me about the Lumbridge Guardsmen.",
					"What is there to do around here?",
					"Tell me about Lumbridge.", "What are you guarding?",
					"Bye.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"I won't pretend that we're an elite fighting force, but we",
						"know how to work with the castle's defences. That means",
						"just a few of us can hold a fairly strong defence, if we",
						"ever need to again.");
				stage = 0;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						NORMAL,
						"If you want to train your creative skills, there are trees",
						"to cut, or you could collect leather from the cow fields to",
						"the east.");
				stage = 0;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						UPSET,
						"Lumbridge used to be a safe haven where you could find",
						"your feet. It is safe again now, but I wonder if we will ever",
						"recover what we've lost.");
				stage = 0;
				break;
			case OPTION_4:
				sendNPCDialogue(
						npcId,
						NORMAL,
						"We work for the safety of the people and the Duke, and",
						"we must be vigilant at all times against potential threats,",
						"be they acts of god or goblin invasions.");
				stage = 0;
				break;
			case OPTION_5:
				sendPlayerDialogue(NORMAL, "Bye.");
				stage = 2;
				break;
			}
			break;
		case 2:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
