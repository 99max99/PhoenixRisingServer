package net.kagani.game.player.dialogues.impl.home;

import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class Ariane extends Dialogue {

	/**
	 * @author: Dylan Page
	 * @author: 
	 */

	private NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		npcId = npc.getId();
		sendNPCDialogue(npcId, HAPPY, "Hello, " + player.getDisplayName()
				+ "! Where would you like to teleport?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("<col=FF0000>Locations", "Training Locations",
					"Minigame Locations", "Bossing Locations",
					"Skilling Locations");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendOptionsDialogue(
						"<col=FF0000>Training Locations - Difficulty",
						"Low-level", "Medium-level", "High-level");
				stage = 28;
				break;

			case OPTION_2:
				sendOptionsDialogue("<col=FF0000>Minigames Locations",
						"TzHaar Fight Cave", "TzHaar Fight Kiln",
						"TzHaar Fight Pit", "Pest Control", "More");
				stage = 4;
				break;

			case OPTION_3:
				sendOptionsDialogue("<col=FF0000>Bossing Locations",
						"Godwars Dungeon", "Tormented Demons",
						"King Black Dragon", "Queen Black Dragon", "More");
				stage = 5;
				break;
			case OPTION_4:
				sendOptionsDialogue("<col=FF0000>Skilling Locations",
						"Runespan", "Woodcutting Locations",
						"Agility Locations", "Fishing Locations", "More");
				stage = 10;
				break;
			}
			break;

		case 28:
			switch (componentId) {
			case OPTION_1:
				sendOptionsDialogue(
						"<col=FF0000>Training Locations - Low-level", "Cows",
						"Stronghold of Security", "Rock Crabs", "Black Demons");
				stage = 2;
				break;
			case OPTION_2:
				sendOptionsDialogue(
						"<col=FF0000>Training Locations - Medium-level",
						"Grotworms", "Hellhounds", "Waterfiends",
						"Fire giants", "More");
				stage = 29;
				break;
			case OPTION_3:
				sendOptionsDialogue(
						"<col=FF0000>Training Locations - High-level",
						"Dark Beasts", "Airuts", "Jadinkos", "Glacors", "More");
				stage = 35;
				break;
			}
			break;

		case 29:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2998,
						3236, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1737,
						5313, 1));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1737,
						5313, 1));
				end();
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2633,
						9556, 2));
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue(
						"<col=FF0000>Training Locations - Medium-level",
						"TzHaar creatures", "Living rock creatures", "Ogres",
						"Back");
				stage = 30;
				break;
			}
			break;

		case 30:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(4629,
						5117, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3656,
						5113, 0));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2438,
						2858, 0));
				end();
				break;
			case OPTION_4:
				sendOptionsDialogue(
						"<col=FF0000>Training Locations - Medium-level",
						"Grotworms", "Hellhounds", "Waterfiends",
						"Fire giants", "More");
				stage = 29;
				break;
			}
			break;

		case 35:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1737,
						5313, 1));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2277,
						3615, 0));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2952,
						2954, 0));
				end();
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(4185,
						5734, 0));
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue(
						"<col=FF0000>Training Locations - High-level",
						"Ganodermic Beasts", "Frost Dragons", "Automatons",
						"Celestial Dragons", "Back");
				stage = 36;
				break;
			}
			break;

		case 36:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3403,
						3326, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3034,
						9584, 0));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2700,
						3374, 0));
				/*
				 * if (!player.hasItem(27477) &&
				 * player.getSkills().getTotalLevel() >= 2000) { if
				 * (Utils.random(50) == 2) {
				 * player.getInventory().addItemDrop(27477, 1);
				 * player.getPackets() .sendGameMessage(
				 * "Ariane gave you a Sixth-age circuit for accessing this place easier."
				 * ); } }
				 */
				end();
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3806,
						3531, 0));
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue(
						"<col=FF0000>Training Locations - High-level",
						"Dark Beasts", "Airuts", "Jadinkos", "Glacors", "More");
				stage = 35;
				break;
			}
			break;

		/*
		 * Skilling Teleports
		 */

		case 10:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3098,
						3162, 3));
				end();
				break;
			case OPTION_2:
				sendOptionsDialogue("<col=FF0000>Woodcutting Locations",
						"Seers' Village", "Daemonheim");
				stage = 103;
				break;
			case OPTION_3:
				sendOptionsDialogue("<col=FF0000>Agility Locations",
						"Gnome Agility", "Barbarian Agility",
						"Wilderness Agility <col=FF0000>(Wilderness)<(col>",
						"Hefin Agiity");
				stage = 102;
				break;
			case OPTION_4:
				sendOptionsDialogue("<col=FF0000>Fishing Locations",
						"Fishing Guild", "Barbarian Fishing",
						"Living Rock Caverns");
				stage = 101;
				break;
			case OPTION_5:
				stage = 105;
				sendOptionsDialogue("<col=FF0000>Skilling Locations",
						"Mining Locations", "Divination Locations", "Back");
				break;
			}
			break;

		case 102:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2468,
						3438, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2543,
						3566, 0));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3000,
						3910, 0));
				end();
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2178,
						3397, 1));
				end();
				break;
			}
			break;

		case 103:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2706,
						3483, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3493,
						3611, 0));
				end();
				break;
			}
			break;

		case 105:
			switch (componentId) {
			case OPTION_1:
				sendOptionsDialogue("<col=FF0000>Mining Locations",
						"Varrock Mining Spot", "Dwarven Mines",
						"Living Rock Caverns");
				stage = 106;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, LAUGHING,
						"I'm not that kind to people, hahaha!");
				stage = 50;
				break;
			case OPTION_3:
				sendOptionsDialogue("<col=FF0000>Skilling Locations",
						"Runespan", "Woodcutting Locations",
						"Agility Locations", "Fishing Locations", "More");
				stage = 10;
				break;
			}
			break;

		case 106:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3191,
						3371, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3060,
						3372, 0));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2639,
						3684, 0));
				end();
				break;
			}
			break;

		case 101:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2592,
						3419, 0));
				end();
				break;

			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3107,
						3433, 0));
				end();
				break;

			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2639,
						3684, 0));
				end();
				break;
			}
			break;

		case 104:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3006,
						9550, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2804,
						10000, 0));
				end();
				break;

			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3423,
						3537, 0));
				end();
				break;
			}
			break;
		/*
		 * Minigame Teleports
		 */
		case 4:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(4610,
						5129, 0));
				end();
				stage = 50;
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(4744,
						5172, 0));
				end();
				stage = 50;
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(4602,
						5062, 0));
				end();
				stage = 50;
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2663,
						2652, 0));
				end();
				stage = 50;
				break;
			case OPTION_5:
				sendOptionsDialogue("<col=FF0000>Minigames Locations",
						"Duel Arena", "Dominion Tower", "Stealing Creation",
						"Soul Wars", "More");
				stage = 8;
				break;
			}
			break;

		case 8:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3342,
						3232, 0));
				end();
				stage = 50;
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3373,
						3087, 0));
				end();
				stage = 50;
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2968,
						9699, 0));
				end();
				stage = 50;
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1890,
						3177, 0));
				end();
				stage = 50;
				break;
			case OPTION_5:
				sendOptionsDialogue("<col=FF0000>Minigames Locations",
						"Clan Wars", "Barrows", "Rise of the Six",
						"Fist of Guthix", "More");
				stage = 9;
				break;
			}
			break;

		case 9:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2994,
						9679, 0));
				end();
				stage = 50;
				break;

			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3565,
						3306, 0));
				end();
				stage = 50;
				break;

			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3539,
						3302, 0));
				end();
				stage = 50;
				break;

			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1677,
						5599, 0));
				end();
				break;

			case OPTION_5:
				stage = 15;
				sendOptionsDialogue("<col=FF0000>Minigames Locations",
						"Pyramid Plunder", "Troll Invasion", "Back");
			}
			break;

		case 15:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3293,
						2804, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2878,
						3553, 0));
				end();
				break;
			case OPTION_3:
				stage = 9;
				sendOptionsDialogue("<col=FF0000>Minigames Locations",
						"Clan Wars", "Barrows", "Rise of the Six",
						"Fist of Guthix", "More");
				break;
			}
			break;

		/*
		 * Training Teleports
		 */
		case 2:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3259,
						3274, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3081,
						3421, 0));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2706,
						3730, 0));
				end();
				stage = 50;
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2863,
						9774, 0));
				end();
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1105,
						6509, 0));
				end();
				stage = 50;
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1737,
						5313, 1));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1737,
						5313, 1));
				end();
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2277,
						3615, 0));
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("<col=FF0000>Training Locations",
						"Jadinko Lair", "Glacors", "Waterfiends",
						"Ganodermic Beasts", "Frost Dragons");
				stage = 150;
				break;
			}
			break;

		case 150:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2952,
						2954, 0));
				end();
				stage = 50;
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(4185,
						5734, 0));
				end();
				stage = 50;
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1748,
						5325, 0));
				end();
				stage = 50;
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3403,
						3326, 0));
				end();
				stage = 50;
				break;
			case OPTION_5:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3034,
						9584, 0));
				end();
				stage = 50;
				break;
			}
			break;
		/*
		 * Bossing Teleports
		 */
		case 5:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2916,
						3739, 0));
				end();
				stage = 50;
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2567,
						5738, 0));
				end();
				player.getPackets().sendGameMessage(
						"Head south-east to approach the tormented demons.");
				stage = 50;
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3051,
						3519, 0));
				end();
				player.getPackets()
						.sendGameMessage(
								"Activate the artefact to teleport to the King Black Dragon.");
				stage = 50;
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1199,
						6500, 0));
				end();
				player.getPackets()
						.sendGameMessage(
								"Pass through the summoning portal to approach the Queen Black Dragon.");
				stage = 50;
				break;
			case OPTION_5:
				sendOptionsDialogue("<col=FF0000>Boss Locations",
						"Kalphite Lair", "Araxxor", "Corporeal Beast",
						"Dagannoth Caves", "More");
				stage = 6;
				break;
			}
			break;

		case 6:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2949,
						1659, 0));
				end();
				stage = 50;
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3695,
						3403, 0));
				end();
				break;

			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2969,
						4384, 2));
				break;

			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1918,
						4367, 0));
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("<col=FF0000>Boss Locations",
						"Ascension Dungeon", "Kalphite King", "Vorago",
						"Chaos Elemental <col=FF0000>(Wilderness)", "More");
				stage = 7;
				break;
			}
			break;

		case 7:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2509,
						2886, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2975,
						1647, 0));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2970,
						3422, 0));
				end();
				break;
			case OPTION_4:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3285,
						3909, 0));
				end();
				break;
			case OPTION_5:
				stage = 16;
				sendOptionsDialogue("<col=FF0000>Boss Locations",
						"Reventants <col=FF0000>(Wilderness)", "Bork",
						"Evil Chicken", "Back");
				break;
			}
			break;

		case 16:
			switch (componentId) {
			case OPTION_1:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3091,
						3694, 0));
				end();
				break;
			case OPTION_2:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3143,
						5545, 0));
				end();
				break;
			case OPTION_3:
				if (npc != null) {
					npc.setNextForceTalk(new ForceTalk("Off you go, "
							+ player.getDisplayName() + "!"));
					npc.setNextAnimation(new Animation(2327));
					player.setNextGraphics(new Graphics(1549));
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2643,
						10413, 0));
				end();
				break;
			case OPTION_4:
				sendOptionsDialogue("<col=FF0000>Boss Locations",
						"Ascension Dungeon", "Kalphite King", "Vorago",
						"Chaos Elemental <col=FF0000>(Wilderness)", "More");
				stage = 7;
				break;
			}
			break;

		case 50:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}