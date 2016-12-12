package net.kagani.game.player.dialogues.impl;

import net.kagani.game.Animation;
import net.kagani.game.ForceMovement;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class CompletionistCape extends Dialogue {

	private static final int[] NPCS = { 6935, 3283, 4344, 6966 };

	@Override
	public void start() {
		if (player.containsItem(20771) || player.containsItem(20769)) {
			sendDialogue("You seem to already own this cape.");
			stage = -2;
			return;
		}
		sendNPCDialogue(
				4405,
				9827,
				"Hey! Get your hands off that! Don't you know not to touch items in the a museum.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			player.setNextFaceWorldTile(new WorldTile(3262, 3455, 2));
			sendPlayerDialogue(9827, "Sorry, I just wondered what it was.");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(4405, 9827,
					"A good question. It's perhaps one of more mysterious exibits in this museum.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(
					4405,
					9827,
					"It was only discovered recently, but our curators have"
							+ " dated it back almost as far as we can track. It turns out"
							+ " this cape might be the product, or at least involved with a"
							+ " Second Age mage by the name of Dahmaroc.");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(9827, "Dahmaroc?");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(
					4405,
					9827,
					"Indeed, you might have learned about him downstairs. He"
							+ " was a powerful mage back in the Second Age. Very skill-"
							+ " focused too, so this cape was a particular find.");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(9827, "What do you mean by that?");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(
					4405,
					9827,
					"Well, generally, his magical abilities were focused away"
							+ " from combat - it seems this cape is under the mose"
							+ " powerful enchantment we've ever seen.");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(9827, "This cape is enchanted?");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(
					4405,
					9827,
					"Yes, and more than we can grasp. It physiclly repels anyone who tries to touch"
							+ " it. We had quite a hassle getting it up here.");
			stage = 9;
			break;
		case 9:
			sendPlayerDialogue(9827, "So no one has worn this cape?");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(
					4405,
					9827,
					"No one can! It's like it has a mind of it's own juding those who try as unworthy.");
			stage = 11;
			break;
		case 11:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Can I try?",
					"How interesting.");
			stage = 12;
			break;
		case 12:
			switch (componentId) {
			case OPTION_1:
				stage = 13;
				sendPlayerDialogue(9827, "Can I try?");
				if (!ItemConstants.canWear(new Item(20769), player)) {
					end();
				}
				break;
			case OPTION_2:
			default:
				stage = 30;
				sendPlayerDialogue(9827, "How interesting.");
				break;
			}
			break;

		case 13:
			sendNPCDialogue(
					4405,
					9827,
					"I said no touching... but, if you get close enough, I'm sure you'll the enchantment affects"
							+ " ... Good luck!");
			stage = 14;
			break;
		case 14:
			if (!ItemConstants.canWear(new Item(20769), player)) {
				player.getInterfaceManager().removeDialogueInterface();
				player.lock();
				player.setNextFaceWorldTile(new WorldTile(3263, 3455, 2));
				WorldTasksManager.schedule(new WorldTask() {
					int phase = 0;

					@Override
					public void run() {
						switch (phase) {
						case 0:
							player.setNextAnimation(new Animation(857));
							break;
						case 1:
							player.setNextAnimation(new Animation(915));
							break;
						case 2:
							player.setNextAnimation(new Animation(857));
							break;
						case 3:
							player.setNextGraphics(new Graphics(86));
							player.getAppearence().transformIntoNPC(
									NPCS[Utils.random(NPCS.length - 1)]);
							break;
						case 4:
							player.setNextForceTalk(new ForceTalk("..What."));
							break;
						case 5:
							player.setNextGraphics(new Graphics(86));
							player.getAppearence().transformIntoNPC(-1);
							player.setNextAnimation(new Animation(10070));
							player.setNextForceMovement(new ForceMovement(
									new WorldTile(3263, 3451, 2), 1, 0));
							break;
						case 6:
							player.setNextWorldTile(new WorldTile(3263, 3451, 2));
							player.setNextFaceWorldTile(new WorldTile(3262,
									3455, 2));
							sendNPCDialogue(4405, 9827,
									"Looks like Dahmaroc had a sense of humour!");
							player.unlock();
							break;
						}
						phase++;
					}
				}, 2, 2);
				stage = 15;
			} else {// TODO: Animation & graphics
				sendNPCDialogue(4405, 9827,
						"I've not seen a reaction like that! I think this cape is"
								+ " identifying it's true owner.");
				stage = 16;
			}
			break;
		case 15:
			player.setNextFaceEntity(null);
			player.unlock();
			sendNPCDialogue(4405, 9827,
					"Sorry, it doesn't look like you are worthy of this cape. At"
							+ " least not yet...");
			stage = -2;
			break;
		case 16:
			sendPlayerDialogue(9827, "You mean I can have it?");
			stage = 17;
			break;
		case 17:
			sendNPCDialogue(
					4405,
					9827,
					"Well, yes, but... I can't just let you take the exhibit. You"
							+ " may be the true owner, but it is one of the most"
							+ " treasured items we have here.");
			stage = 18;
			break;
		case 18:
			sendNPCDialogue(
					4405,
					9827,
					"I suppose if the museum were compensated, perhaps I"
							+ " could let you take it... How does 5,000,000 coins"
							+ " sound?");
			stage = 19;
			break;
		case 19:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "That sounds fair.",
					"That sounds like a joke!");
			stage = 20;
			break;
		case 20:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(9827, "That sounds fair.");
				stage = 21;
				break;
			case OPTION_2:
				sendPlayerDialogue(9827, "That sounds like a joke!");
				stage = -2;
				break;
			}
			break;
		case 21:
			if (player.getInventory().getCoinsAmount() < 5000000) {
				sendNPCDialogue(
						4405,
						9827,
						"Alright, well come talk to me again when you have enough gold and I'll let you have it.");
				stage = -2;
			} else {
				if (player.getInventory().getFreeSlots() < 2) {
					sendNPCDialogue(4405, 9827,
							"Sorry, but your inventory seems to be full, please come back "
									+ "with more space.");
					stage = -2;
				} else {
					player.getInventory().removeItemMoneyPouch(
							new Item(995, 5000000));
					sendNPCDialogue(4405, 9827, "Thanks, enjoy!");
					int type = ItemConstants.hasCompletionistCapeTrimmedReqs(
							player, false) ? 2 : 1;
					if (type == 2) {
						player.getInventory().addItem(20771, 1);
						player.getInventory().addItem(20772, 1);
					} else {
						player.getInventory().addItem(20769, 1);
						player.getInventory().addItem(20770, 1);
					}
					if (player.getReceivedCompletionistCape() < type) {
						if (player.isAnIronMan())
							World.sendNews(
									player,
									player.getIronmanTitle(false)
											+ " <col=D80000>"
											+ player.getDisplayName()
											+ " has been awarded the "
											+ (type == 2 ? "trimmed " : "")
											+ "Completionist Cape!", 0);
						else
							World.sendNews(player, player.getDisplayName()
									+ " has been awarded the "
									+ (type == 2 ? "trimmed " : "")
									+ "Completionist Cape!", 0);
						player.setReceivedCompletionistCape(type);
						player.setCompletionistCapeCustomized(player
								.getMaxedCapeCustomized());
					}
					stage = -2;
				}
			}
			break;
		case 30:
			sendNPCDialogue(4405, 9827, "Thanks for wasting my time.");
			stage = -2;
			break;
		case -2:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}