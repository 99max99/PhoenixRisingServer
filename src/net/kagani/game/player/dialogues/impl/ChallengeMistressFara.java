package net.kagani.game.player.dialogues.impl;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.player.content.DailyTasksInterface;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class ChallengeMistressFara extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private int option;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		option = (int) parameters[1];
		switch (option) {
		case 1:
			if (player.getDailyTask().getTaskAmount() < 1 && player.claimedDailyReward == false
					&& player.completedDaily) {
				stage = 19;
				sendNPCDialogue(npcId, HAPPY,
						"You completed: " + player.getDailyTask().reformatTaskName(player.getDailyTask().getName())
								+ " (" + player.getDailyTask().getAmountCompleted() + "/"
								+ player.getDailyTask().getTotalAmount() + ").");
			} else {
				stage = 1;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Toggle max Challenges.", "Talk.");
			}
			break;
		case 2:
			if (player.getDailyTask().getTaskAmount() < 1 && player.claimedDailyReward == false
					&& player.completedDaily) {
				stage = 19;
				sendNPCDialogue(npcId, HAPPY,
						"You completed: " + player.getDailyTask().reformatTaskName(player.getDailyTask().getName())
								+ " (" + player.getDailyTask().getAmountCompleted() + "/"
								+ player.getDailyTask().getTotalAmount() + ").");
			} else {
				stage = -1;
				sendNPCDialogue(npcId, NORMAL,
						"It doesn't look like you've completed any Daily Challenges. Come back when you've completed one.");
			}
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				DailyTasksInterface.openTaskDialogue(player);
				end();
				break;
			case OPTION_2:
				stage = 2;
				sendNPCDialogue(npcId, NORMAL, getText());
				break;
			}
			break;
		case 2:
			stage = 3;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "I've completed a challenge.",
					player.getInventory().containsItem(23030, 1) || player.getBank().containsItem(23030)
							? "Who are you?" : "Can I have my baby troll?",
					"Tell me about challenges.", "Tell me about Daily Challenges.", "Goodbye.");
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				if (player.getDailyTask().getTaskAmount() < 1 && player.claimedDailyReward == false
						&& player.completedDaily) {
					stage = 19;
					sendNPCDialogue(npcId, HAPPY,
							"You completed: " + player.getDailyTask().reformatTaskName(player.getDailyTask().getName())
									+ " (" + player.getDailyTask().getAmountCompleted() + "/"
									+ player.getDailyTask().getTotalAmount() + ").");
				} else {
					stage = -1;
					sendNPCDialogue(npcId, NORMAL,
							"It doesn't look like you've completed any Daily Challenges. Come back when you've completed one.");
				}
				break;
			case OPTION_2:
				if (player.getInventory().containsItem(23030, 1) || player.getBank().containsItem(23030)) {
					stage = 18;
					sendNPCDialogue(npcId, HAPPY, "The name's Fara, proud gnome and collector extraordinaire.");
				} else {
					stage = 4;
					sendNPCDialogue(npcId, NORMAL,
							"You seem to have lost your baby troll, I found it trying to chomp its way through my things! You going to take it back?");
				}
				break;
			case OPTION_3:
				stage = 7;
				sendNPCDialogue(npcId, HAPPY,
						"Well, a challenge is a job to complete: anything from forging swords to baking potatoes.");
				break;
			case OPTION_4:
				stage = 10;
				sendNPCDialogue(npcId, NORMAL,
						"Daily Challenges are pretty special. I only assign you one per day, but you get a big reward when you complete it.");
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		case 4:
			stage = 5;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "I could look after him.", "Bad luck!",
					"You could drop him in a well.");
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				stage = 6;
				sendItemDialogue(23030,
						"You have adopted a baby troll pet! To have him follow you, open your inventory and click on him.");
				player.getInventory().addItemDrop(23030, 1);
				break;
			case OPTION_2:
				stage = -1;
				sendNPCDialogue(npcId, SAD, "Well, thanks a lot, beanpole!");
				break;
			case OPTION_3:
				stage = -1;
				sendNPCDialogue(npcId, SAD, "I couldn't do that!");
				break;
			}
			break;
		case 6:
			stage = -1;
			sendItemDialogue(23030,
					"You can feed the baby troll by using an object on him from your inventory. Watch out! If he likes how it tastes, you won't get it back!");
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(npcId, HAPPY,
					"Those are just for your own development. You don't have to hand anything in and I won't give you a reward. They're just a good way of finding out what to do next!");
			break;
		case 8:
			stage = 9;
			sendDialogue("You can view all available challenges on the Challenges tab of the Adventures interface.");
			break;
		case 9:
			stage = 2;
			sendNPCDialogue(npcId, NORMAL,
					"If you're interested in rewards - which it looks like you are - you'll need a Daily Challenge.");
			break;
		case 10:
			stage = 11;
			sendDialogue("You can view all Daily Challenges on the Challenges tab of the Adventures interface.");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(npcId, NORMAL,
					"You can have up to five Daily Challenges at a time. After that, new ones knock old ones off your list.");
			break;
		case 12:
			stage = 13;
			sendNPCDialogue(npcId, NORMAL,
					"These dailies work the same as regular challenges, but you'll have to give me whatever you gather or produce at the end so I can add it to my collection!");
			break;
		case 13:
			stage = 14;
			sendNPCDialogue(npcId, NORMAL,
					"Don't worry. I'll compenstate you with a reward. I've got all sorts of useful stuff up here, so I'll do my best to make it a fair trade that covers your costs.");
			break;
		case 14:
			stage = 15;
			sendNPCDialogue(npcId, NORMAL,
					"I've also got a big haul of old lamps sitting around. You adventurer type seem to go crazy for those old things!");
			break;
		case 15:
			stage = 16;
			sendNPCDialogue(npcId, HAPPY, "I'll give you one of these lamps for every Daily Challenge you complete.");
			break;
		case 16:
			stage = 17;
			sendDialogue(
					"If you complete a Combat Challenge you'll get an XP lamp that can be used in any combat skill. If you complete any other challenge, you'll get XP in that skill directly.");
			break;
		case 17:
			stage = 2;
			sendDialogue(
					"Every day you will receive a new Daily Challenge. <col=FF0000>If you already have five Daily Challenges, they oldest will disappear!");
			break;
		case 18:
			stage = 2;
			sendNPCDialogue(npcId, HAPPY,
					"I'm the challenge mistress in charge of assigning challenges and handing out rewards. They're all here on my list...");
			break;
		case 19:
			int reward = player.getDailyTask().getReward();
			int amount = player.getDailyTask().getRewardAmount();
			int exp = player.getDailyTask().getExp();
			int skill = player.getDailyTask().getSkill();
			int coins = player.getSkills().getLevel(skill) * 3000;
			stage = -1;
			sendNPCDialogue(npcId, HAPPY,
					"I shall reward you " + Utils.format(exp * Settings.XP_RATE) + " xp in "
							+ player.getSkills().getSkillName(skill) + " and x" + Utils.format(amount) + " "
							+ ItemDefinitions.getItemDefinitions(reward).getName() + ".");
			handleReward(reward, amount, exp, skill, coins);
			break;
		}
	}

	private void handleReward(int reward, int amount, int exp, int skill, int coins) {
		player.getInventory().addItemDrop(reward, amount);
		player.getSkills().addXp(skill, exp);
		player.getMoneyPouch().setAmount(coins, false);
		player.claimedDailyReward = true;
	}

	private String getText() {
		switch (Utils.random(3)) {
		case 0:
			return "Hey, how's the weather up there";
		case 1:
			return "Got something for me, beanpole?";
		case 2:
			return "Hey skretch! Got anything for me?";
		}
		return null;
	}

	@Override
	public void finish() {

	}
}