package net.kagani.game.minigames;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.kagani.Settings;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.others.StarSprite;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.mining.ShootingStarMining;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class ShootingStars {

	public static final int SHADOW = 8092, SPRITE = 8091, INVISIBLE = 1957,
			STARDUST = 13727, STAR_FALL_TIME = 12000;

	private static StarSprite star;
	private static Queue<BoardEntry> noticeboard;
	private static StarLocations nextLocation;

	public static final int[] STAR_DUST_Q = { 1200, 700, 439, 250, 175, 80, 40,
			25, 15 };
	public static final int[] STAR_DUST_XP = { 14, 25, 29, 32, 47, 71, 114,
			145, 210 };

	public static final void init() {
		noticeboard = new ConcurrentLinkedQueue<BoardEntry>();
		generateNextLocation();
		star = new StarSprite();
	}

	private static enum StarLocations {

		CRAFTING_GUILD_MINING_SITE("Asgarnia", new WorldTile(2940, 3278, 0)), MINING_GUILD(
				"Asgarnia", new WorldTile(3027, 3349, 0)), RIMMINGTON_MINING_SITE(
				"Asgarnia", new WorldTile(2975, 3234, 0)), FALADOR_MINING_SITE(
				"Asgarnia", new WorldTile(2926, 3340, 0))

		, KARAMJA_NORTH_WEST_MINING_SITE("Crandor or Karamja", new WorldTile(
				2732, 3219, 0)), BRIMHAVEN_MINING_SITE("Crandor or Karamja",
				new WorldTile(2743, 3143, 0)), SOUTH_CRANDOR_MINING_SITE(
				"Crandor or Karamja", new WorldTile(2822, 3237, 0)), KARAMJA_MINING_SITE(
				"Crandor or Karamja", new WorldTile(2847, 3028, 0)), SHILO_VILLAGE_SITE(
				"Crandor or Karamja", new WorldTile(2826, 2996, 0))

		, KELDAGRIM_ENTRANCE_MINING_SITE("Fremennik lands or Lunar Isle",
				new WorldTile(2724, 3698, 0)), JATIZSO_MINE(
				"Fremennik lands or Lunar Isle", new WorldTile(2397, 3815, 0)), LUNAR_ISLE_MINE(
				"Fremennik lands or Lunar Isle", new WorldTile(2139, 3938, 0)), MISCELLANIA_MINING_SITE(
				"Fremennik lands or Lunar Isle", new WorldTile(2530, 3887, 0)), FREMENNIK_ISLES_MINING_SITE(
				"Fremennik lands or Lunar Isle", new WorldTile(2375, 3834, 0)), RELLEKKA_MINNING_SITE(
				"Fremennik lands or Lunar Isle", new WorldTile(2683, 3699, 0))

		, LEGENDS_GUILD_MINNING_SITE("Kandarin", new WorldTile(2702, 3331, 0)), SOUTH_EAST_ARDOUGNE_MINNING_SITE(
				"Kandarin", new WorldTile(2610, 3220, 0)), COAL_TRUCKS(
				"Kandarin", new WorldTile(2585, 3477, 0)), YANNILLE_BANK(
				"Kandarin", new WorldTile(2603, 3086, 0)), FIGHT_ARENA_MINING_SITE(
				"Kandarin", new WorldTile(2628, 3134, 0))

		, ALKHARID_BANK("Kharidian Desert", new WorldTile(3285, 3181, 0)), ALKHARID_MINING_SITE(
				"Kharidian Desert", new WorldTile(3297, 3297, 0)), DUEL_ARENA_BANK_CHEST(
				"Kharidian Desert", new WorldTile(3388, 3268, 0)), UZER_MINING_SITE(
				"Kharidian Desert", new WorldTile(3456, 3136, 0)), NARDAH_MINING_SITE(
				"Kharidian Desert", new WorldTile(3456, 3136, 0)), NARDAH_BANK(
				"Kharidian Desert", new WorldTile(3434, 2888, 0)), WESTERN_DESERT_MINING_SITE(
				"Kharidian Desert", new WorldTile(3169, 2911, 0))

		, SOUTH_EAST_VARROCK_MINING_SITE("Misthalin", new WorldTile(3293, 3352,
				0)), LUMBRIDGE_SWAMP_TRAINING_MINING_SITE("Misthalin",
				new WorldTile(3231, 3155, 0)), SOUTH_WEST_VARROCK_MINING_SITE(
				"Misthalin", new WorldTile(3174, 3361, 0)), VARROCK_EAST_BANK(
				"Misthalin", new WorldTile(3257, 3408, 0))

		, BURGH_DE_ROTT_BANK("Morytania or Mos Le'Harmless", new WorldTile(
				3501, 3215, 0)), CANIFIS_BANK("Morytania or Mos Le'Harmless",
				new WorldTile(3505, 3485, 0)), MOS_LE_HARMLESS_BANK(
				"Morytania or Mos Le'Harmless", new WorldTile(3685, 2967, 0))

		, GNOME_STRONGHOLD_BANK("Piscatoris, Gnome Stronghold or Tirannwn",
				new WorldTile(2449, 3436, 0)), LTETYA_BANK(
				"Piscatoris, Gnome Stronghold or Tirannwn", new WorldTile(2334,
						3169, 0)), PISCATORIS_MINING_SITE(
				"Piscatoris, Gnome Stronghold or Tirannwn", new WorldTile(2336,
						3636, 0))

		, NORTH_EDGEVILLE_MINING_SITE("Wilderness",
				new WorldTile(3108, 3569, 0)), SOUTHERN_WILDERNESS_MINING_SITE(
				"Wilderness", new WorldTile(3019, 3594, 0)), WILDERNESS_VOLCANO(
				"Wilderness", new WorldTile(3188, 3690, 0)), BANDIT_CAMP_MINING_SITE(
				"Wilderness", new WorldTile(3031, 3795, 0)), LAVA_MAZE_RUNITE_MINING_SITE(
				"Wilderness", new WorldTile(3059, 3888, 0)), PIRATES_HIDEOUT_MINING_SITE(
				"Wilderness", new WorldTile(3048, 3944, 0)), MAGE_ARENA_BANK(
				"Wilderness", new WorldTile(3091, 3962, 0));

		private String name;
		private WorldTile location;

		private StarLocations(String name, WorldTile location) {
			this.name = name;
			this.location = location;
		}

	}

	public static void generateNextLocation() {
		nextLocation = StarLocations.values()[Utils.random(StarLocations
				.values().length)];
		if (Settings.DEBUG)
			Logger.log(
					"ShootingStars",
					"Location: "
							+ Utils.fixChatMessage(String.valueOf(nextLocation)
									.replace("_", " ")));
	}

	private static class BoardEntry {
		String name;
		long time;

		public BoardEntry(String name, long time) {
			this.name = name;
			this.time = time;
		}

	}

	public static WorldTile getNewLocation() {
		return nextLocation.location;
	}

	public static void mine(Player player, WorldObject object) {
		if (!star.isReady())
			return;
		if (!star.isFirstClick()) {
			star.setFirstClick();
			player.setFoundShootingStar();
			int xp = player.getSkills().getLevelForXp(Skills.MINING) * 10;
			player.getSkills().addXp(Skills.MINING, xp, true);
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You were the first person to find this star and so you receive "
							+ xp + " mining experience.");
			if (noticeboard.size() >= 5)
				noticeboard.poll();
			noticeboard.add(new BoardEntry(player.getDisplayName(), Utils
					.currentTimeMillis()));
			return;
		}
		player.getActionManager().setAction(new ShootingStarMining(object));
	}

	public static int getLevel() {
		return !star.isReady() ? 1 : star.getMiningLevel();
	}

	public static int getStarSize() {
		return !star.isReady() ? Integer.MAX_VALUE : star.getStarSize();
	}

	public static int getXP() {
		return !star.isReady() ? 1 : STAR_DUST_XP[star.getStarSize() - 1];
	}

	public static void reduceStarLife() {
		star.reduceStarLife();
	}

	public static void prospect(Player player) {
		if (!star.isReady())
			return;
		player.getDialogueManager()
				.startDialogue(
						"SimpleMessage",
						"This is a size "
								+ star.getStarSize()
								+ " star. A Mining level of at least "
								+ star.getMiningLevel()
								+ " is required to mine this layer. It has been mined about "
								+ star.getMinedPerc()
								+ " percent of the way to the "
								+ (star.getStarSize() == 1 ? "core"
										: "next layer") + ".");
	}

	public static void openNoticeboard(Player player) {
		int c = 0;
		long time = Utils.currentTimeMillis();
		player.getInterfaceManager().sendCentralInterface(787);
		for (BoardEntry entry : noticeboard.toArray(new BoardEntry[noticeboard
				.size()])) {
			player.getPackets().sendIComponentText(787, 6 + c,
					((time - entry.time) / 60000) + " minutes ago");
			player.getPackets().sendIComponentText(787, 11 + c, entry.name);
			c++;
		}
	}

	private static final String[] NO_STAR_MESSAGES = {
			"Hmm... are the stars really small, or are they just very far away?",
			"One of these stars has... little stars moving around it. Interesting...",
			"Oh no! A giant space spider is eating the moon! Oh, it was just a spider crawling across the lens.",
			"It's overcast; I can't see anything.",
			"My goodness... it's full of stars!" };

	public static void openTelescope(Player player) {
		String message = "";
		if (star.getStarCycle() > 1000) { // 10min after one spawned
			int time = (int) ((STAR_FALL_TIME - star.getStarCycle()) * 0.6 / 60);
			message = "You see a shooting star! The star looks like it will land in "
					+ nextLocation.name
					+ " in the next "
					+ (time - 2)
					+ " minutes to " + (time + 2) + " minutes.";
		} else
			message = NO_STAR_MESSAGES[Utils.random(NO_STAR_MESSAGES.length)];

		player.getDialogueManager().startDialogue("TelescopeD", message);
	}
}