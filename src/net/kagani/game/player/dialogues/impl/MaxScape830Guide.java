package net.kagani.game.player.dialogues.impl;

import net.kagani.Settings;
import net.kagani.game.World;
import net.kagani.game.player.dialogues.Dialogue;

public class MaxScape830Guide extends Dialogue {

	/**
	 * @author: 99max99 M
	 */

	private boolean introlong = false;

	@Override
	public void start() {
		stage = 1;
		player.lock();
		sendNPCDialogue(945, HAPPY, "Welcome to <col=55728b>"
				+ Settings.SERVER_NAME + "</col>, " + player.getDisplayName()
				+ ".");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			end();
			player.unlock();
			player.getAppearence().generateAppearenceData();
			if (player.isAnIronMan()) {
				World.sendWorldMessage(
						"<col=43dde5>Everyone, welcome our newest player, "
								+ player.getIronmanTitle(false)
								+ "<col=43dde5>" + player.getDisplayName()
								+ "!", false);
			} else {
				World.sendWorldMessage(
						"<col=43dde5>Everyone, welcome our newest player, "
								+ player.getDisplayName() + "!", false);
			}
			break;
		case 1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Give me a short intrduction of <col=55728b>"
							+ Settings.SERVER_NAME,
					"Give me a tour of <col=55728b>" + Settings.SERVER_NAME);
			stage = 2;
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(945, HAPPY, "Great, let's get started.");
				stage = 3;
				break;
			case OPTION_2:
				sendNPCDialogue(945, HAPPY, "Excellent, let's get started.");
				introlong = true;
				stage = 3;
				break;
			}
			break;
		case 3:
			sendNPCDialogue(
					945,
					NORMAL,
					"We are an economy server, focusing on pvm and skilling. There are some items that can be bought from the shops, located around here. But most useful items need to be obtained.");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(
					945,
					NORMAL,
					"If you are more of a pvp person, we still offer a bunch of content. Speak to <col=FFFF000>Ariane</col> to travel around the world of <col=55728b>"
							+ Settings.SERVER_NAME + "</col>.");
			if (introlong == true) {
				stage = 5; // 6
			} else {
				stage = 5;
			}
			break;
		case 5:
			sendNPCDialogue(
					945,
					NORMAL,
					"Our XP rates are <col=FFFF000>x"
							+ Settings.XP_RATE
							+ "</col>, these were chosen by the community, also remember, we are a community based server! So if you have any suggestions, post them on the forums.");
			stage = 51;
			break;
		case 51:
			sendNPCDialogue(
					945,
					HAPPY,
					"At "
							+ Settings.SERVER_NAME
							+ " we receive <col=FFFF000>daily updates</col>, you can view the latest update by using the command '<col=FFFF000>::patchnotes</col>'.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(
					945,
					HAPPY,
					"Any questions? Don't be shy, just ask in ::yell or '/' for friends chat, our players will be more than happy to assist you.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(945, QUESTIONS,
					"Now, a very serious question, would you like to look into Ironman mode?");
			stage = 8;
			break;
		case 8:
			sendOptionsDialogue("Would you like to be an Ironman?",
					"No, let me start playing.",
					"Can I be an <img=11><col=5F6169>Ironman</col> 1x exp +25% droprate?",
					"Can I be a <img=13><col=A30920>Hardcore Ironman</col> 1x exp +25% droprate?");
			stage = 9;
			break;
		case 9:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(945, HAPPY, "That's fine, have fun!");
				stage = 0;
				break;
			case OPTION_2:
				sendNPCDialogue(
						945,
						HAPPY,
						"Interesting choice, I would like to tell you some more about this mode before we continue any further...");
				stage = 10;
				break;
			case OPTION_3:
				sendNPCDialogue(
						945,
						HAPPY,
						"Interesting choice, I would like to tell you some more about this mode before we continue any further...");
				stage = 16;
				break;
			}
			break;
		case 10:
			sendNPCDialogue(
					945,
					NORMAL,
					"You will not be able to use the grand exchange, trade, dungeoneering with a party, enter other players' house, and some more.");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(
					945,
					NORMAL,
					"Basically you will be playing as a single player. Do you still want to be an <img=11><col=5F6169>Ironman</col>?");
			stage = 12;
			break;
		case 12:
			sendOptionsDialogue("Would you like to be an Ironman?",
					"No, let me start playing.",
					"I want to be an <img=11><col=5F6169>Ironman</col>.");
			stage = 13;
			break;
		case 13:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(945, HAPPY, "That's fine, have fun!");
				stage = 0;
				break;
			case OPTION_2:
				sendNPCDialogue(
						945,
						HAPPY,
						"Your choice has been made, you are now an <img=11><col=5F6169>Ironman</col>. Have fun!");
				player.setIronman(true);
				player.setHardcoreIronMan(false);
				stage = 0;
				break;
			}
			break;
		case 15:
			sendNPCDialogue(
					945,
					NORMAL,
					"You will not be able to use the grand exchange, trade, dungeoneering with a party, enter other players' house, and some more.");
			stage = 16;
			break;
		case 16:
			sendNPCDialogue(
					945,
					NORMAL,
					"Basically you will be playing as a single player. Do you still want to be a <img=13><col=A30920>Hardcore Ironman</col>?");
			stage = 17;
			break;
		case 17:
			sendOptionsDialogue("Would you like to be an Ironman?",
					"No, let me start playing.",
					"I want to be a <img=13><col=A30920>Hardcore Ironman</col>.");
			stage = 18;
			break;
		case 18:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(945, HAPPY, "That's fine, have fun!");
				stage = 0;
			case OPTION_2:
				sendNPCDialogue(
						945,
						HAPPY,
						"Your choice has been made, you are now a <img=13><col=A30920>Hardcore Ironman</col>. Have fun!");
				player.setHardcoreIronMan(true);
				player.setIronman(false);
				stage = 0;
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}