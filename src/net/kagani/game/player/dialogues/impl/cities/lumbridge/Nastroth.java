package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class Nastroth extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendNPCDialogue(6539, 9827,
					"Greetings, brave warrior. What can I do for you?");
			stage = 1;
		} else if (stage == 1) {
			if (player.neverresetskills == false) {
				sendOptionsDialogue("Select an Option", "Who are you?",
						"What do you do here?",
						"Let me talk about skill resets.", "Nothing, actually.");
			} else {
				sendOptionsDialogue("Select an Option", "Who are you?",
						"What do you do here?", "Nothing, actually.");
			}
			stage = 2;
		} else if (stage == 2) {
			if (player.neverresetskills == false) {
				if (componentId == OPTION_1) {
					player(9827, "Who are you?");
					stage = 3;
				} else if (componentId == OPTION_2) {
					player(9827, "What do you do here?");
					stage = 21;
				} else if (componentId == OPTION_3) {
					sendNPCDialogue(6539, 9827,
							"Okay, what would you like to reset?");
					stage = 68;
				} else if (componentId == OPTION_4) {
					player(9827, "Nothing, actually.");
					stage = 99;
				}
			} else {
				if (componentId == OPTION_1) {
					player(9827, "Who are you?");
					stage = 3;
				} else if (componentId == OPTION_2) {
					player(9827, "What do you do here?");
					stage = 21;
				} else if (componentId == OPTION_3) {
					player(9827, "Nothing, actually.");
					stage = 99;
				}
			}
		} else if (stage == 3) {
			sendNPCDialogue(
					6539,
					9827,
					"I am Nastroth. Like my brother, Mandrith, I'm a collector of ancient artefacts. I'm just not as excited about it as he is.");
			stage = 4;
		} else if (stage == 4) {
			sendOptionsDialogue("Select an Option",
					"Why aren't you excited about it?",
					"What are these ancient artefacts?", "Who is Mandrith?",
					"Okay. Goodbye, then.");
			stage = 5;
		} else if (stage == 5) {
			if (componentId == OPTION_1) {
				player(9827,
						"Why aren't you excited about it? It doesn't sound that boring.");
				stage = 6;
			} else if (componentId == OPTION_2) {
				player(9827, "What are these ancient artefacts?");
				stage = 12;
			} else if (componentId == OPTION_3) {
				player(9827, "Who is Mandrith?");
				stage = 20;
			} else if (componentId == OPTION_4) {
				player(9827, "Okay. Goodbye, then.");
				stage = 20;
			}
		} else if (stage == 6) {
			sendNPCDialogue(
					6539,
					9827,
					"Truth be told, I'd much rather be out there with the rest of you, breaking bones and cracking skulls.");
			stage = 7;
		} else if (stage == 7) {
			sendOptionsDialogue("Select an Option", "Then why aren't you?",
					"That's not what I do.", "Oh, okay.");
			stage = 8;
		} else if (stage == 8) {
			if (componentId == OPTION_1) {
				player(9827, "Then why aren't you?");
				stage = 9;
			} else if (componentId == OPTION_2) {
				player(9827, "That's not what I do.");
				stage = 19;
			} else if (componentId == OPTION_3) {
				player(9827, "Oh, okay.");
				stage = 99;
			}
		} else if (stage == 9) {
			sendNPCDialogue(
					6539,
					9827,
					"My days of battle are over. Now I spend my time here, collecting ancient artefacts.");
			stage = 10;
		} else if (stage == 10) {
			sendOptionsDialogue("Select an Option",
					"What are these ancient artefacts?", "Have fun with that.");
			stage = 11;
		} else if (stage == 11) {
			if (componentId == OPTION_1) {
				player(9827, "What are these ancient artefacts?");
				stage = 12;
			} else if (componentId == OPTION_2) {
				player(9827, "Have fun with that.");
				stage = 99;
			}
		} else if (stage == 12) {
			sendNPCDialogue(
					6539,
					9827,
					"As the blood and sweat of warriors is spilled on the ground, relics of the God Wars are drawn out from the dity where they were once left forgotten. If you happen to come across of these ancient items, bring them to");
			stage = 13;
		} else if (stage == 13) {
			sendNPCDialogue(
					6539,
					9827,
					"me or my brother Mandrith in Edgeville, and we will pay you a fair price for them, We don't accept them in noted form, though, remember that. Also, we don't want to buy any weapons or armour.");
			stage = 14;
		} else if (stage == 14) {
			sendOptionsDialogue("Select an Option", "Who is Mandrith?",
					"Why won't you buy weapons or armour",
					"That sounds great. Goodbye.");
			stage = 15;
		} else if (stage == 15) {
			if (componentId == OPTION_1) {
				player(9827, "Who is Mandrith?");
				stage = 20;
			} else if (componentId == OPTION_2) {
				player(9827, "Why won't you buy weapons or armour?");
				stage = 16;
			} else if (componentId == OPTION_3) {
				player(9827, "That sounds great. Goodbye.");
				stage = 99;
			}
		} else if (stage == 16) {
			sendNPCDialogue(
					6539,
					9827,
					"They should be used as they were meant to be used, not traded for money. Mandrith and I only collect ancient artefacts.");
			stage = 17;
		} else if (stage == 17) {
			sendOptionsDialogue("Select an Option",
					"What are these ancient artefacts?", "Oh, okay.");
			stage = 18;
		} else if (stage == 18) {
			player(9827, "What are these ancient artefacts?");
			stage = 12;
		} else if (stage == 19) {
			sendNPCDialogue(6539, 9827, "Oh. My apologies.");
			stage = 99;
		} else if (stage == 20) {
			sendNPCDialogue(
					6539,
					9827,
					"Mandrith is my overly excited brother and the one who made me wear this outfit. We share the same purpose, collecting ancient artefacts, but he is located closer to the Wilderness, in a small village called Edgeville.");
			stage = 17;
		} else if (stage == 21) {
			sendNPCDialogue(6539, 9827,
					"I collect ancient artefacts acquired by warriors in return for money.");
			stage = 18;
		} else if (stage == 68) {
			sendOptionsDialogue("What do you want to reset?",
					"Hitpoints & Prayer.", "Defence.",
					"Never reset my skills.", "Nothing");
			stage = 69;
		} else if (stage == 69) {
			if (componentId == OPTION_1) {
				sendNPCDialogue(6539, 9827,
						"Are you sure you want to reset? The process can be quite... rough...");
				stage = 70;
			} else if (componentId == OPTION_2) {
				sendNPCDialogue(6539, 9827,
						"Are you sure you want to reset? The process can be quite... rough...");
				stage = 72;
			} else if (componentId == OPTION_3) {
				sendNPCDialogue(
						6539,
						9827,
						"Are you sure you want me to stop offering to reset your skills? If you do this, I will NEVER ask about resetting your skills again.");
				stage = 75;
			} else if (componentId == OPTION_4) {
				end();
			}
		} else if (stage == 70) {
			sendOptionsDialogue("Are you sure you wish to reset?",
					"Yes, reset my Consitution & Prayer.", "No, I'm fine.");
			stage = 71;
		} else if (stage == 71) {
			if (componentId == OPTION_1) {
				sendNPCDialogue(6539, 9827, "There you go.");
				player.getSkills().setXp(3, 1184);
				player.getSkills().set(3, 10);
				player.getSkills().setXp(5, 0);
				player.getSkills().set(5, 1);
				stage = 99;
			} else if (componentId == OPTION_2) {
				end();
			}
		} else if (stage == 72) {
			sendOptionsDialogue("Are you sure you wish to reset?",
					"Yes, reset my Defence", "No, I'm fine.");
			stage = 73;
		} else if (stage == 73) {
			if (componentId == OPTION_1) {
				sendNPCDialogue(6539, 9827, "There you go.");
				player.getSkills().setXp(1, 0);
				player.getSkills().set(1, 1);
				stage = 99;
			} else if (componentId == OPTION_2) {
				end();
			}
		} else if (stage == 75) {
			sendOptionsDialogue("Never reset your skills?",
					"Yes, never reset my skills.",
					"No, let me reset my skills.");
			stage = 77;
		} else if (stage == 77) {
			if (componentId == OPTION_1) {
				sendNPCDialogue(6539, 9827, "There you go.");
				player.neverresetskills = true;
				stage = 99;
			} else if (componentId == OPTION_2) {
				end();
			}
		} else if (stage == 99) {
			end();
		}
	}

	@Override
	public void start() {
		player(9827, "Hello.");
	}
}