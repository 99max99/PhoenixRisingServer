package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class Kolodion extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 905, "Hey there, " + player.getUsername());
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendPlayerDialogue(npcId, "Hello Kolodion");
			
		} else if (stage == 0)  {
			stage = 1;
			sendNPCDialogue(npcId, 905, "How are you? Are you enjoying the bloodshed?");
		} else if (stage == 1) {
			stage = 2;
			sendPlayerDialogue(905, "I think I've had enough for now");
		} else if (stage == 2) {
			stage = 3;
			sendNPCDialogue(npcId, 905, "A shame. You're a good battle mage.");
		} else if (stage == 3) {
			stage = 4;
			sendNPCDialogue(npcId, 905, "I heard the Chamber Guardian still has God Staves through the sparking water.");
		} else if (stage == 4) {
			stage = 5;
			sendNPCDialogue(npcId, 905, "I hope to see you soon.");
		} else if (stage == 5) {
			end();
		}
	}

	@Override
	public void finish() {
		
	}
	
}
