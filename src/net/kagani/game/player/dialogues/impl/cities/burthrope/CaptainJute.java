package net.kagani.game.player.dialogues.impl.cities.burthrope;

import net.kagani.game.player.dialogues.Dialogue;

public class CaptainJute extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		npcId = (int) parameters[0];
		stage = (int) parameters[1];
		if (stage == 18)
			sendPlayerDialogue(ASKING,
					"I want to know about the rewards. What's in it for me?");
		else if (stage == 50)
			sendPlayerDialogue(HAPPY, "Defend the gatehouse!");
		else if (stage == 51)
			sendPlayerDialogue(HAPPY, "Fight the trolls!");
		else
			sendNPCDialogue(npcId, HAPPY, "Ah, " + player.getDisplayName()
					+ "! Back for more, are you? What can I do for you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendOptionsDialogue("What do you want to say?",
					"I want to start one of the activities.",
					"I want to talk to you about rewards.",
					"Can you go over the activities again?");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				stage = 47;
				sendPlayerDialogue(HAPPY,
						"I want to start one of the activities.");
				break;
			case OPTION_2:
				stage = 16;
				sendPlayerDialogue(ASKING,
						"I want to talk to you about rewards.");
				break;
			case OPTION_3:
				stage = 3;
				sendPlayerDialogue(ASKING,
						"Can you go over the activities again?");
				break;
			}
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(npcId, ASKING,
					"Do you want the quick version or the more detailed version?");
			break;
		case 4:
			stage = 5;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Summary.", "Detailed.");
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
			case OPTION_2:
				stage = 6;
				sendNPCDialogue(npcId, HAPPY,
						"Every month, you can come back here and help defend the gatehouse.");
				break;
			}
			break;
		case 6:
			stage = 7;
			sendPlayerDialogue(ASKING,
					"Why every month? Aren't trolls invading all the time?");
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Yes, but due to union regulations we can only have each shift work once a month. There are two things you can do to help the gatehouse. You may only choose to do one to gain a reward. You can still play the other, for practive,");
			break;
		case 8:
			stage = 9;
			sendNPCDialogue(npcId, NORMAL, "any time you like.");
			break;
		case 9:
			stage = 10;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"So, things you can do to help defend the gatehouse... First, you can fight trolls. You stand alone in front of the gate and fight off as many trolls as you can until you die.");
			break;
		case 10:
			stage = 11;
			sendPlayerDialogue(NONONO, "What? Until I die?");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Well... maybe not die. Basically, we leave you out there until you 'almost' die, then drag you back in. That way you don't lose any of your stuff.");
			break;
		case 12:
			stage = 13;
			sendPlayerDialogue(SCARED, "Okay, what's the other thing I can do?");
			break;
		case 13:
			stage = 14;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Oh, that's much safer. Well, you won't die, but Norris did have a nervious breakdown a few weeks ago. I guess thestrain of it got to him.");
			break;
		case 14:
			stage = 15;
			sendPlayerDialogue(SAD, "Oh no.");
			break;
		case 15:
			stage = -1;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Basically, you run around repairing the gatehouse defences. Keep the oil drums full, repair barricade lines and gatehouse walls...");
			break;
		case 16:
			stage = 17;
			sendNPCDialogue(npcId, HAPPY,
					"Ah, the payoff. Incentive schemes. Points make prizes. Let's talk!");
			break;
		case 17:
			stage = 18;
			sendPlayerDialogue(ASKING,
					"I want to know about the rewards. What's in it for me?");
			break;
		case 18:
			stage = 19;
			sendNPCDialogue(
					npcId,
					CONFUSED,
					"Isn't the glory of the battle enough? The adrenaline rush? The smell of troll sweat in the morning.");
			break;
		case 19:
			stage = 20;
			sendPlayerDialogue(SCARED, "Definitely not!");
			break;
		case 20:
			stage = 21;
			sendNPCDialogue(npcId, BLANK, "People these days...");
			break;
		case 21:
			stage = 22;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"I suppose I would be willing to impart some expertise to you in exchange for your help. The more you help, the more experience I'll give you. Just speak to me after you complete the event and I'll give you your reward.");
			break;
		case 22:
			stage = 23;
			sendOptionsDialogue("What do you want to ask about?",
					"Fighting trolls.", "Defending the gatehouse.",
					"Once a month?", "Rewards.");
			break;
		case 23:
			switch (componentId) {
			case OPTION_1:
				stage = 34;
				sendPlayerDialogue(ASKING,
						"I want to know more about fighting off the troll hordes.");
				break;
			case OPTION_2:
				stage = 27;
				sendPlayerDialogue(ASKING,
						"I want to know more about defending and repairing the gatehouse.");
				break;
			case OPTION_3:
				stage = 25;
				sendPlayerDialogue(
						ASKING,
						"I want to know more about the only doing the event once a month. Does that mean I can only do the fighting or the defendin? I can't do both?");
				break;
			case OPTION_4:
				stage = 18;
				sendPlayerDialogue(ASKING,
						"I want to know about the rewards. What's in it for me?");
				break;
			}
			break;
		case 25:
			stage = 26;
			sendNPCDialogue(
					npcId,
					LAUGHING,
					"You can do one a month for reward, after that you can keep going if you wish for training for the following months attempt thought.");
			break;
		case 26:
			stage = 22;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"We in the Imperial Guard recognise that not all guards are cut out for both types of work. We like to be flexible in your schedules and workload, so you can do it whenever you like the month thought.");
			break;
		case 27:
			stage = 28;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Ah, deliberating over the defence? Strategising your protective methods? Incentivising your grey cells to safeguard the gatehouse? Wonderful!");
			break;
		case 28:
			stage = 29;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Defending the gatehouse is all about speed and multi-tasking. It also helps if you're handy in Strength, Agility, Defence, Crafting, Construction and Firemaking. You don't have to have ay levels in any of those to help you,");
			break;
		case 29:
			stage = 30;
			sendNPCDialogue(npcId, NORMAL, "but it will make your life easier!");
			break;
		case 30:
			stage = 31;
			sendPlayerDialogue(
					ANGRY,
					"Oh, gee, is that all? Why not throw in a few more skills to 'make my life easier'?");
			break;
		case 31:
			stage = 32;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Hmm, I suppose we could, but let's see how you get on with these. It'll be easiest if I show you how it all works. Do you want to see?");
			break;
		case 32:
			stage = 33;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes.", "No.");
			break;
		case 33:
			stage = 22;
			sendPlayerDialogue(MILDLY_ANGRY, "No, not right now.");
			break;
		case 34:
			stage = 35;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Ah, after a fight, are you? Think of the sweat of battle? Pondering the smell of torll blood in the morning? Good for you!");
			break;
		case 35:
			stage = 36;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Figthing troll hordes is all about endurance. It's about showing your moxie. It's about perserverance in the face of unbeatable odds.");
			break;
		case 36:
			stage = 37;
			sendPlayerDialogue(CONFUSED, "Unbeatable?");
			break;
		case 37:
			stage = 38;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Well, maybe not unbeatable. More like very, very likely not beatable. The hordes get stronger and more difficult the longer you are out there. For the most part, the trolls are pretty run of the mill, but there are some that are a");
			break;
		case 38:
			stage = 39;
			sendNPCDialogue(npcId, NORMAL, "bit... unusual.");
			break;
		case 39:
			stage = 40;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Some trolls have eaten a few too many wizards and have gained magical powers. They'll drag you out from your hiding place and beat you to a pulp... like they did to ol'Ralph. Oh, and there are trolls that ate some druids and");
			break;
		case 40:
			stage = 41;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"now they can summon other trolls - gave Derek a real shock when a troll popped out of the ground between his feet.");
			break;
		case 41:
			stage = 42;
			sendPlayerDialogue(SCARED, "That's what you call unusual?");
			break;
		case 42:
			stage = 43;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Oh, I haven't finished! There's some sort of flu or plague the trolls have caught and you'll want to avoid touching those ones. And it might be worth me mentioning that some of the trolls stole some explosives from us and like");
			break;
		case 43:
			stage = 44;
			sendNPCDialogue(npcId, HAPPY,
					"to use it against the castle. No big deal, just an explosion here or there.");
			break;
		case 44:
			stage = 45;
			sendPlayerDialogue(ANGRY, "Explosives!");
			break;
		case 45:
			stage = 46;
			sendNPCDialogue(npcId, NORMAL,
					"and I nearly forgot about Cliff! He's a real nasty customer.");
			break;
		case 46:
			stage = 22;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Other than them, it's a piece of cake. Just make sure to go in prepared for long combat. You won't lose anything you take in unless you custome it.");
			break;
		case 47:
			stage = 48;
			sendNPCDialogue(npcId, HAPPY,
					"That's the spirit! Which do you want to do?");
			break;
		case 48:
			stage = 49;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Defend the gatehouse!",
					"Fight the trolls!");
			break;
		case 49:
			switch (componentId) {
			case OPTION_1:
				stage = 50;
				sendPlayerDialogue(HAPPY, "Defend the gatehouse!");
				break;
			case OPTION_2:
				stage = 51;
				sendPlayerDialogue(HAPPY, "Fight the trolls!");
				break;
			}
			break;
		case 50:
			stage = -1;
			sendNPCDialogue(npcId, SAD,
					"Sadly this piece of content is still under development.");
			break;
		case 51:
			stage = 52;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Right on!<br>Just to make you aware, this attempt will be reward, so do your best!");
			break;
		case 52:
			stage = 53;
			sendOptionsDialogue("Please select difficulty:",
					"Easy (20 waves).", "Hard (7 waves).");
			break;
		case 53:
			switch (componentId) {
			case OPTION_1:
				player.getControlerManager().startControler("TrollInvasion",
						false);
				end();
				break;
			case OPTION_2:
				player.getControlerManager().startControler("TrollInvasion",
						true);
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}