package net.kagani.game.player.content;

import java.io.Serializable;
import java.util.List;

import net.kagani.Settings;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.slayer.SlayerShop;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.ShopsHandler;
import net.kagani.utils.Utils;

public class Slayer {

	public enum SlayerMaster implements Serializable {

		SPRIA(8462, 85, 1, new int[] { 0, 0, 0 }, new int[] { 15, 50 },
				SlayerTask.BANSHEE, SlayerTask.BAT, SlayerTask.BEAR,
				SlayerTask.COW, SlayerTask.BIRD, SlayerTask.CAVE_BUG,
				SlayerTask.CAVE_SLIME, SlayerTask.DWARF,
				SlayerTask.CRAWLING_HAND, SlayerTask.DESERT_LIZARD,
				SlayerTask.DWARF, SlayerTask.GHOST, SlayerTask.GOBLIN,
				SlayerTask.ICEFIEND, SlayerTask.MINOTAUR, SlayerTask.MONKEY,
				SlayerTask.SCORPION, SlayerTask.SKELETON, SlayerTask.SPIDER,
				SlayerTask.WOLF, SlayerTask.ZOMBIE),

		TURAEL(8480, 3, 1, new int[] { 0, 0, 0 }, new int[] { 15, 50 },
				SlayerTask.BANSHEE, SlayerTask.BAT, SlayerTask.BEAR,
				SlayerTask.COW, SlayerTask.BIRD, SlayerTask.CAVE_BUG,
				SlayerTask.CAVE_SLIME, SlayerTask.DWARF,
				SlayerTask.CRAWLING_HAND, SlayerTask.DESERT_LIZARD,
				SlayerTask.DWARF, SlayerTask.GHOST, SlayerTask.GOBLIN,
				SlayerTask.ICEFIEND, SlayerTask.MINOTAUR, SlayerTask.MONKEY,
				SlayerTask.SCORPION, SlayerTask.SKELETON, SlayerTask.SPIDER,
				SlayerTask.WOLF, SlayerTask.ZOMBIE),

		MAZCHNA(8481, 20, 1, new int[] { 2, 5, 15 }, new int[] { 40, 85 },
				SlayerTask.BANSHEE, SlayerTask.BAT, SlayerTask.CATABLEPON,
				SlayerTask.CAVE_CRAWLER, SlayerTask.COCKATRICE,
				SlayerTask.CRAWLING_HAND, SlayerTask.CYCLOPS,
				SlayerTask.DESERT_LIZARD, SlayerTask.DOG,
				SlayerTask.FLESH_CRAWLER, SlayerTask.GHOUL, SlayerTask.GHOST,
				SlayerTask.GROTWORM, SlayerTask.HILL_GIANT,
				SlayerTask.HOBGOBLIN, SlayerTask.ICE_WARRIOR,
				SlayerTask.KALPHITE, SlayerTask.PYREFIEND, SlayerTask.ROCKSLUG,
				SlayerTask.SKELETON, SlayerTask.VAMPYRE, SlayerTask.WALL_BEAST,
				SlayerTask.WOLF, SlayerTask.ZOMBIE),

		ACHTRYN(8465, 20, 1, new int[] { 2, 5, 15 }, new int[] { 40, 85 },
				SlayerTask.BANSHEE, SlayerTask.BAT, SlayerTask.CATABLEPON,
				SlayerTask.CAVE_CRAWLER, SlayerTask.COCKATRICE,
				SlayerTask.CRAWLING_HAND, SlayerTask.CYCLOPS,
				SlayerTask.DESERT_LIZARD, SlayerTask.DOG,
				SlayerTask.FLESH_CRAWLER, SlayerTask.GHOUL, SlayerTask.GHOST,
				SlayerTask.GROTWORM, SlayerTask.HILL_GIANT,
				SlayerTask.HOBGOBLIN, SlayerTask.ICE_WARRIOR,
				SlayerTask.KALPHITE, SlayerTask.PYREFIEND, SlayerTask.ROCKSLUG,
				SlayerTask.SKELETON, SlayerTask.VAMPYRE, SlayerTask.WALL_BEAST,
				SlayerTask.WOLF, SlayerTask.ZOMBIE),

		VANNAKA(1597, 40, 1, new int[] { 4, 20, 60 }, new int[] { 60, 120 },
				SlayerTask.ABERRANT_SPECTRE, SlayerTask.ANKOU,
				SlayerTask.BANSHEE, SlayerTask.BASILISK, SlayerTask.BLOODVELD,
				SlayerTask.BRINE_RAT, SlayerTask.COCKATRICE,
				SlayerTask.CROCODILE, SlayerTask.CYCLOPS,
				SlayerTask.DUST_DEVIL, SlayerTask.EARTH_WARRIOR,
				SlayerTask.GHOUL, SlayerTask.GREEN_DRAGON, SlayerTask.GROTWORM,
				SlayerTask.HARPIE_BUG_SWARM, SlayerTask.HILL_GIANT,
				SlayerTask.ICE_GIANT, SlayerTask.ICE_WARRIOR,
				SlayerTask.INFERNAL_MAGE, SlayerTask.JELLY,
				SlayerTask.JUNGLE_HORROR, SlayerTask.LESSER_DEMON,
				SlayerTask.MOLANISK, SlayerTask.MOSS_GIANT, SlayerTask.OGRE,
				SlayerTask.OTHERWORLDLY_BEING, SlayerTask.PYREFIEND,
				SlayerTask.SHADE, SlayerTask.SHADOW_WARRIOR, SlayerTask.TUROTH,
				SlayerTask.VAMPYRE, SlayerTask.WEREWOLF),

		CHAELDAR(1598, 70, 1, new int[] { 10, 50, 100 },
				new int[] { 110, 170 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.BANSHEE, SlayerTask.BASILISK, SlayerTask.BLOODVELD,
				SlayerTask.BLUE_DRAGON, SlayerTask.BRINE_RAT,
				SlayerTask.BRONZE_DRAGON, SlayerTask.CAVE_CRAWLER,
				SlayerTask.CAVE_HORROR, SlayerTask.CRAWLING_HAND,
				SlayerTask.DAGANNOTH, SlayerTask.DUST_DEVIL,
				SlayerTask.ELF_WARRIOR, SlayerTask.FEVER_SPIDER,
				SlayerTask.FIRE_GIANT, SlayerTask.FUNGAL_MAGE,
				SlayerTask.GARGOYLE, SlayerTask.GRIFOLAPINE,
				SlayerTask.GRIFOLAROO, SlayerTask.GROTWORM,
				SlayerTask.HARPIE_BUG_SWARM, SlayerTask.JUNGLE_STRYKEWYRM,
				SlayerTask.INFERNAL_MAGE, SlayerTask.JELLY,
				SlayerTask.JUNGLE_HORROR, SlayerTask.KALPHITE,
				SlayerTask.KALPHITE, SlayerTask.KURASK,
				SlayerTask.LESSER_DEMON, SlayerTask.ZYGOMITE,
				SlayerTask.SHADOW_WARRIOR, SlayerTask.TUROTH,
				SlayerTask.VYREWATCH, SlayerTask.WARPED_TORTOISE),

		SUMONA(7780, 85, 35, new int[] { 12, 60, 180 }, new int[] { 120, 185 },
				SlayerTask.ABERRANT_SPECTRE, SlayerTask.ABYSSAL_DEMON,
				SlayerTask.AQUANITE, SlayerTask.BANSHEE, SlayerTask.BASILISK,
				SlayerTask.BLACK_DEMON, SlayerTask.BLOODVELD,
				SlayerTask.BLUE_DRAGON, SlayerTask.CAVE_CRAWLER,
				SlayerTask.CAVE_HORROR, SlayerTask.DAGANNOTH,
				SlayerTask.DESERT_STRYKEWYRM, SlayerTask.DUST_DEVIL,
				SlayerTask.ELF_WARRIOR, SlayerTask.FUNGAL_MAGE,
				SlayerTask.GARGOYLE, SlayerTask.GREATER_DEMON,
				SlayerTask.GRIFOLAPINE, SlayerTask.GRIFOLAROO,
				SlayerTask.GROTWORM, SlayerTask.HELLHOUND,
				SlayerTask.IRON_DRAGON, SlayerTask.JUNGLE_STRYKEWYRM,
				SlayerTask.KALPHITE, SlayerTask.KURASK, SlayerTask.JADINKO, /*
																			 * SlayerTask
																			 * .
																			 * NECHRYAEL
																			 * ,
																			 */
				SlayerTask.RED_DRAGON, SlayerTask.SCABARITE,
				SlayerTask.SPIRITUAL_MAGE, SlayerTask.SPIRITUAL_WARRIOR,
				SlayerTask.TERROR_DOG, SlayerTask.TROLL, SlayerTask.TUROTH,
				SlayerTask.VYREWATCH),

		DURADEL(8466, 100, 50, new int[] { 15, 75, 225 },
				new int[] { 130, 200 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.ABYSSAL_DEMON, SlayerTask.AQUANITE,
				SlayerTask.BLACK_DEMON, SlayerTask.BLACK_DRAGON,
				SlayerTask.BLOODVELD, SlayerTask.DAGANNOTH,
				SlayerTask.DARK_BEAST, SlayerTask.DESERT_STRYKEWYRM,
				SlayerTask.DUST_DEVIL, SlayerTask.FIRE_GIANT,
				SlayerTask.FUNGAL_MAGE, SlayerTask.GANODERMIC,
				SlayerTask.GARGOYLE, SlayerTask.GORAK,
				SlayerTask.GREATER_DEMON, SlayerTask.GRIFALOPINE,
				SlayerTask.GRIFALOROO, SlayerTask.GROTWORM,
				SlayerTask.HELLHOUND, SlayerTask.ICE_STRYKEWYRM,
				SlayerTask.IRON_DRAGON, SlayerTask.JUNGLE_STRYKEWYRM,
				SlayerTask.KALPHITE, SlayerTask.MITHRIL_DRAGON,
				SlayerTask.JADINKO, /* SlayerTask.NECHRYAEL, */
				SlayerTask.SCABARITE, SlayerTask.SKELETAL_WYVERN,
				SlayerTask.SPIRITUAL_MAGE, SlayerTask.STEEL_DRAGON,
				SlayerTask.SUQAH, SlayerTask.VYREWATCH,
				SlayerTask.WARPED_TERRORBIRD, SlayerTask.WATERFIEND),

		LAPALOK(8467, 100, 50, new int[] { 15, 75, 225 },
				new int[] { 130, 200 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.ABYSSAL_DEMON, SlayerTask.AQUANITE,
				SlayerTask.BLACK_DEMON, SlayerTask.BLACK_DRAGON,
				SlayerTask.BLOODVELD, SlayerTask.DAGANNOTH,
				SlayerTask.DARK_BEAST, SlayerTask.DESERT_STRYKEWYRM,
				SlayerTask.DUST_DEVIL, SlayerTask.FIRE_GIANT,
				SlayerTask.FUNGAL_MAGE, SlayerTask.GANODERMIC,
				SlayerTask.GARGOYLE, SlayerTask.GORAK,
				SlayerTask.GREATER_DEMON, SlayerTask.GRIFALOPINE,
				SlayerTask.GRIFALOROO, SlayerTask.GROTWORM,
				SlayerTask.HELLHOUND, SlayerTask.ICE_STRYKEWYRM,
				SlayerTask.IRON_DRAGON, SlayerTask.JUNGLE_STRYKEWYRM,
				SlayerTask.KALPHITE, SlayerTask.MITHRIL_DRAGON,
				SlayerTask.JADINKO, /* SlayerTask.NECHRYAEL, */
				SlayerTask.SCABARITE, SlayerTask.SKELETAL_WYVERN,
				SlayerTask.SPIRITUAL_MAGE, SlayerTask.STEEL_DRAGON,
				SlayerTask.SUQAH, SlayerTask.VYREWATCH,
				SlayerTask.WARPED_TERRORBIRD, SlayerTask.WATERFIEND),

		KURADAL(9085, 110, 75, new int[] { 18, 90, 270 },
				new int[] { 120, 250 }, SlayerTask.ABERRANT_SPECTRE,
				SlayerTask.ABYSSAL_DEMON, SlayerTask.BLACK_DEMON,
				SlayerTask.BLACK_DRAGON, SlayerTask.BLOODVELD,
				SlayerTask.BLUE_DRAGON, SlayerTask.DAGANNOTH,
				SlayerTask.DARK_BEAST, SlayerTask.DESERT_STRYKEWYRM,
				SlayerTask.DUST_DEVIL, SlayerTask.FIRE_GIANT,
				SlayerTask.GANODERMIC, SlayerTask.GARGOYLE,
				SlayerTask.GRIFOLAPINE, SlayerTask.GRIFOLAROO,
				SlayerTask.GROTWORM, SlayerTask.HELLHOUND,
				SlayerTask.IRON_DRAGON, SlayerTask.JUNGLE_STRYKEWYRM,
				SlayerTask.KALPHITE, SlayerTask.LIVING_ROCK,
				SlayerTask.MITHRIL_DRAGON, SlayerTask.JADINKO, /*
																 * SlayerTask.
																 * NECHRYAEL,
																 */
				SlayerTask.SKELETAL_WYVERN, SlayerTask.SPIRITUAL_MAGE,
				SlayerTask.STEEL_DRAGON, SlayerTask.SUQAH,
				SlayerTask.TERROR_DOG, SlayerTask.TZHAAR, SlayerTask.TZHAAR,
				SlayerTask.VYREWATCH, SlayerTask.WARPED_TORTOISE,
				SlayerTask.WATERFIEND),

		MORVAN(20112, 120, 85, new int[] { 18, 90, 270 },
				new int[] { 150, 300 }, SlayerTask.ABYSSAL_DEMON,
				SlayerTask.AVIANSIE, SlayerTask.AQUANITES,
				SlayerTask.BLACK_DEMON, SlayerTask.BLACK_DRAGON,
				SlayerTask.CELESTIAL_DRAGON, SlayerTask.DAGANNOTH,
				SlayerTask.DARK_BEAST, SlayerTask.DESERT_STRYKEWYRM,
				SlayerTask.EDIMMU, SlayerTask.ELVES, SlayerTask.GLACOR,
				SlayerTask.GREATER_DEMON, SlayerTask.GANODERMIC,
				SlayerTask.GARGOYLE, SlayerTask.GRIFOLAPINE,
				SlayerTask.GRIFOLAROO, SlayerTask.GROTWORM,
				SlayerTask.ICE_STRYKEWYRM, SlayerTask.IRON_DRAGON,
				SlayerTask.JUNGLE_STRYKEWYRM, SlayerTask.KALPHITE,
				SlayerTask.LAVA_STRYKEWYRM, SlayerTask.MITHRIL_DRAGON,
				SlayerTask.JADINKO, SlayerTask.NECHRYAEL,
				SlayerTask.STEEL_DRAGON, SlayerTask.TORMENTED_DEMON,
				SlayerTask.SKELETAL_WYVERN, SlayerTask.TZHAAR,
				SlayerTask.VYREWATCH, SlayerTask.WARPED_TORTOISE,
				SlayerTask.WATERFIEND);

		private SlayerTask[] task;
		private int[] tasksRange, pointsRange;
		private int requriedCombatLevel, requiredSlayerLevel, npcId;

		private SlayerMaster(int npcId, int requriedCombatLevel,
				int requiredSlayerLevel, int[] pointsRange, int[] tasksRange,
				SlayerTask... task) {
			this.npcId = npcId;
			this.requriedCombatLevel = requriedCombatLevel;
			this.requiredSlayerLevel = requiredSlayerLevel;
			this.pointsRange = pointsRange;
			this.tasksRange = tasksRange;
			this.task = task;
		}

		public int getNPCId() {
			return npcId;
		}

		public int getRequiredCombatLevel() {
			return requriedCombatLevel;
		}

		public int getRequiredSlayerLevel() {
			return requiredSlayerLevel;
		}

		public SlayerTask[] getTask() {
			return task;
		}

		public int[] getTasksRange() {
			return tasksRange;
		}

		public int[] getPointsRange() {
			return pointsRange;
		}

		public static boolean startInteractionForId(Player player, int npcId,
				int option) {
			for (SlayerMaster master : SlayerMaster.values()) {
				if (master.getNPCId() == npcId) {
					if (option == 1)
						player.getDialogueManager().startDialogue(
								"SlayerMasterD", npcId, master);
					else if (option == 2)
						player.getDialogueManager().startDialogue("QuickTaskD",
								master);
					else if (option == 3)
						ShopsHandler.openShop(player, 29);
					else if (option == 4)
						SlayerShop.sendInterface(player);
					return true;
				}
			}
			return false;
		}
	}

	public enum SlayerTask implements Serializable {// 79 matches out of 117

		MIGHTY_BANSHEE(37, new String[] {
				"Banshees are fearsome creatures with a mighty scream.",
				" You need something to cover your ears",
				"Beware of their scream.",
				"Banshees are found in the Slayer Tower." }),

		// finished
		MIGHTY_BANSEE(37, new String[] {
				"Banshees are fearsome creatures with a mighty scream.",
				" You need something to cover your ears",
				"Beware of their scream.",
				"Banshees are found in the Slayer Tower." }, MIGHTY_BANSHEE),

		// finished
		BANSHEE(15, new String[] {
				"Banshees are fearsome creatures with a mighty scream.",
				" You need something to cover your ears",
				"Beware of their scream.",
				"Banshees are found in the Slayer Tower." }, MIGHTY_BANSEE),

		// finished
		BAT(1, new String[] {}),

		// finished
		AVIANSIE(1, 0.8, new String[] {}),

		// finished
		TORMENTED_DEMON(1, new String[] {}),

		// finished
		CHICKEN(1, new String[] {}),

		// not needed
		CHOMPY_BIRD(1, new String[] {}),

		// finished
		DUCK(1, new String[] {}),

		// finished
		BIRD(
				1,
				new String[] {
						"Birds are a type of species found throughout "
								+ Settings.SERVER_NAME + " in different forms.",
						"It's recomended that you bring range weapons to fight these monsters.",
						"Avansies are the strongest and most widely known type of bird.",
						"Chickens are great for a fast task." }, AVIANSIE,
				CHICKEN, CHOMPY_BIRD, DUCK),

		// finished
		BEAR(1, new String[] {}),

		// finished
		CAVE_BUG(7, new String[] {}),

		// finished
		CAVE_SLIME(17, new String[] {}),

		// finished
		COW(1, new String[] {}),

		// finished
		ZOMBIE_HAND(5, new String[] {}),

		// finished
		SKELETAL_HAND(5, new String[] {}),

		// finished
		CRAWLING_HAND(5, new String[] {}, ZOMBIE_HAND, SKELETAL_HAND),

		// finished
		DWARF(1, new String[] {}),

		// finished
		LIZARD(22, null),

		// finished
		DESERT_LIZARD(22, new String[] {}, LIZARD),

		// finished
		REVENANT(1, 0.65, new String[] {}),

		// finished
		GHOST(1, new String[] {}, REVENANT),

		// finished
		GOBLIN(1, new String[] {}),

		// finished
		ICEFIEND(1, new String[] {}),

		// finished
		MINOTAUR(1, new String[] {}),

		// finished
		MONKEY(1, new String[] {}),

		// finished
		SCORPION(1, new String[] {}),

		// finished
		SKELETON(1, new String[] {}, SKELETAL_HAND),

		// finished
		SPIDER(1, new String[] {}),

		// finished
		WOLF(1, new String[] {}),

		// finished
		ZOMBIE(1, new String[] {}),

		// finished
		CATABLEPON(1, new String[] {}),

		// finished
		CAVE_CRAWLER(10, new String[] {}),

		// finished
		DOG(1, new String[] {}),

		// done
		FLESH_CRAWLER(1, new String[] {}),

		// finished
		HOBGOBLIN(1, new String[] {}),

		// finished
		KALPHITE(1, new String[] {}),

		// finished
		ROCKSLUG(20, new String[] {}),

		// finished
		ROCK_SLUG(20, new String[] {}, ROCKSLUG),

		// finished
		HOLE_IN_THE_WALL(35, new String[] {}),

		// finished
		WALL_BEAST(35, new String[] {}, HOLE_IN_THE_WALL),

		// finished
		ABERRANT_SPECTRE(60, new String[] {}),

		// finished
		ANKOU(1, new String[] {}),

		// finished
		BASILISK(40, new String[] {}),

		// finished
		BLOODVELD(50, new String[] {}),

		// finished
		BRINE_RAT(47, new String[] {}),

		// finished
		COCKATRICE(25, new String[] {}),

		// finished
		CROCODILE(1, 0.5, new String[] {}),

		// finished
		CYCLOPS(1, new String[] {}),

		// finished
		CYCLOPSE(1, new String[] {}, CYCLOPS),

		// finished
		DUST_DEVIL(65, new String[] {}),

		// finished
		EARTH_WARRIOR(1, 0.5, new String[] {}),

		// finished
		GHOUL(1, new String[] {}),

		// finished
		GREEN_DRAGON(1, new String[] {}),

		// finished
		GROTWORM(1, 0.64, new String[] {}),

		HARPIE_BUG_SWARM(33, new String[] {}),

		// finished
		HILL_GIANT(1, new String[] {}),

		// finished
		ICE_GIANT(1, new String[] {}),

		// finished
		ICE_WARRIOR(1, new String[] {}),

		// finished
		INFERNAL_MAGE(45, new String[] {}),

		// finished
		JELLY(52, new String[] {}),

		// finished
		JUNGLE_HORROR(1, 0.64, new String[] {}),

		// finished
		LESSER_DEMON(1, new String[] {}),

		// finished
		MOLANISK(39, new String[] {}),

		// finished
		ELF_WARRIOR(1, new String[] {}),

		// finished
		ELVES(1, new String[] {}, ELF_WARRIOR),

		// finished
		MOSS_GIANT(1, new String[] {}),

		// finished
		OGRE(1, new String[] {}),

		// finished
		OTHERWORLDLY_BEING(1, new String[] {}),

		// finished
		PYREFIEND(30, new String[] {}),

		// finished
		SHADE(1, new String[] {}),

		// finished
		SHADOW_WARRIOR(1, new String[] {}),

		// finished
		NECHRYAEL(80, new String[] {}),

		// finished
		TUROTH(55, new String[] {}),

		// finished
		VAMPYRE(1, new String[] {}),

		// finished
		WEREWOLF(1, new String[] {}),

		// finished
		BLUE_DRAGON(1, 0.8, new String[] {}),

		// finished
		BRONZE_DRAGON(1, 0.35, new String[] {}),

		// finished
		CAVE_HORROR(58, new String[] {}),

		DAGANNOTH(1, 0.96, new String[] {}),

		// completed
		FEVER_SPIDER(42, new String[] {}),

		// completed
		FIRE_GIANT(1, new String[] {}),

		FUNGAL_MAGE(1, new String[] {}),

		// finished
		GARGOYLE(75, new String[] {}),

		// finished
		GRIFOLAPINE(88, 0.316, new String[] {}),

		// finished
		GRIFOLAROO(82, 0.316, new String[] {}),

		GRIFALOPINE(88, 0.316, new String[] {}, GRIFOLAPINE),

		GRIFALOROO(82, 0.316, new String[] {}, GRIFOLAROO),

		// finished
		JUNGLE_STRYKEWYRM(73, new String[] {}),

		// finished
		KURASK(70, new String[] {}),

		// finished
		FUNGI(57, 0.81, new String[] {}),

		// finished
		ZYGOMITE(57, new String[] {}, FUNGI),

		VYRELORD(31, 0.68, new String[] {}), VYRELADY(31, 0.68, new String[] {}), VYREWATCH(
				31, 0.68, new String[] {}, VYRELORD, VYRELADY),

		// finished
		WARPED_TORTOISE(56, new String[] {}),

		// finished
		AQUANITES(78, new String[] {}),

		// finished
		ABYSSAL_DEMON(85, new String[] {}),

		// finished
		RORARI(81, new String[] {}),

		// finished
		AIRUT(92, new String[] {}),

		// finished
		EDIMMU(90, new String[] {}),

		// finished
		GLACOR(1, new String[] {}),

		// finished
		AQUANITE(78, 0.96, new String[] {}),

		// finished
		BLACK_DEMON(1, new String[] {}),

		// finished
		DESERT_STRYKEWYRM(77, 0.64, new String[] {}),

		// finished
		GREATER_DEMON(1, new String[] {}),

		// finished
		HELLHOUND(1, 0.92, new String[] {}),

		// finished
		IRON_DRAGON(1, 0.468, new String[] {}),

		MUTATED_JADINKO_MALE(91, 0.88, new String[] {}), MUTATED_JADINKO_GAURD(
				86, 0.88, new String[] {}), MUTATED_JADINKO_BABY(80, 0.88,
				new String[] {}),

		JADINKO(1, 0.88, new String[] {}, MUTATED_JADINKO_MALE,
				MUTATED_JADINKO_GAURD, MUTATED_JADINKO_BABY),

		// finished
		// NECHRYAEL(80, 0.88, new String[] {}),

		// finished
		RED_DRAGON(1, 0.43, new String[] {}),

		LOCUST(1, 0.4, new String[] {}), SCABARAS(1, 0.4, new String[] {}), SCARAB(
				1, 0.4, new String[] {}), SCABARITE(1, 0.4, new String[] {},
				LOCUST, SCABARAS, SCARAB),

		// finished
		SPIRITUAL_MAGE(83, 0.96, new String[] {}),

		// finished
		SPIRITUAL_WARRIOR(68, new String[] {}),

		// finished
		TERROR_DOG(40, 0.28, new String[] {}),

		// a stupid troll at death plateau
		ROCK(1, 1.033, new String[] {}), TROLL(1, 1.033, new String[] {}, ROCK),

		// finished
		BLACK_DRAGON(1, 0.36, new String[] {}),

		// finished
		CELESTIAL_DRAGON(1, new String[] {}),

		// finished
		DARK_BEAST(90, new String[] {}),

		// finished
		GANODERMIC(95, 0.356, new String[] {}),

		// finished
		GORAK(1, 0.4, new String[] {}),

		ICE_STRYKEWYRM(93, 0.64, new String[] {}),

		// finished
		MITHRIL_DRAGON(1, 0.136, new String[] {}),

		// finished
		SKELETAL_WYVERN(72, 0.36, new String[] {}),

		// finished
		STEEL_DRAGON(1, 0.432, new String[] {}),

		// finished
		SUQAH(1, 0.4, new String[] {}),

		// finished
		WARPED_TERRORBIRD(56, new String[] {}),

		// finished
		WATERFIEND(1, new String[] {}),

		// finished
		LIVING_ROCK(1, 0.74, new String[] {}),

		// finished
		LAVA_STRYKEWYRM(94, new String[] {}),

		// finished
		TZHAAR(1, 0.44, new String[] {});

		private String[] tips;
		private double taskFactor;
		private SlayerTask[] alternatives;
		private int levelRequried;

		private SlayerTask(int levelRequried, double taskFactor, String[] tips,
				SlayerTask... alternatives) {
			this.levelRequried = levelRequried;
			this.taskFactor = taskFactor;
			this.tips = tips;
			this.alternatives = alternatives;
		}

		private SlayerTask(int levelRequried, String[] tips,
				SlayerTask... alternatives) {
			this(levelRequried, 1, tips, alternatives);
		}

		public String[] getTips() {
			return tips;
		}

		public SlayerTask[] getAlternatives() {
			return alternatives;
		}

		public int getLevelRequried() {
			return levelRequried;
		}

		public String getName() {
			return Utils.formatPlayerNameForDisplay(toString());
		}

		public double getTaskFactor() {
			return taskFactor;
		}
	}

	public static boolean canAttackNPC(int slayerLevel, String name) {
		return slayerLevel >= getLevelRequirement(name);
	}

	public static int getLevelRequirement(String name) {
		for (SlayerTask task : SlayerTask.values()) {
			if (name.toLowerCase().contains(
					task.toString().replace("_", " ").toLowerCase()))
				return task.getLevelRequried();
		}
		return 1;
	}

	public static boolean hasNosepeg(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4168 || hasSlayerHelmet(target);
	}

	public static boolean hasEarmuffs(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4166 || hat == 13277 || hasSlayerHelmet(target);
	}

	public static boolean hasMask(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 1506 || hat == 4164 || hat == 13277
				|| hasSlayerHelmet(target);
	}

	public static boolean hasWitchWoodIcon(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getAmuletId();
		return hat == 8923;
	}

	public static boolean hasSlayerHelmet(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 13263 || hat == 14636 || hat == 14637
				|| hasFullSlayerHelmet(target);
	}

	public static boolean hasFullSlayerHelmet(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 15492 || hat == 15496 || hat == 15497
				|| (hat >= 22528 && hat <= 22550);
	}

	public static boolean hasReflectiveEquipment(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int shieldId = targetPlayer.getEquipment().getShieldId();
		return shieldId == 4156;
	}

	public static boolean hasSpinyHelmet(Entity target) {
		if (!(target instanceof Player))
			return true;
		Player targetPlayer = (Player) target;
		int hat = targetPlayer.getEquipment().getHatId();
		return hat == 4551 || hasSlayerHelmet(target);
	}

	public static boolean isUsingBell(final Player player) {
		player.lock(3);
		player.setNextAnimation(new Animation(6083));
		List<WorldObject> objects = World.getRegion(player.getRegionId())
				.getAllObjects();
		if (objects == null)
			return false;
		for (final WorldObject object : objects) {
			if (!object.withinDistance(player, 3) || object.getId() != 22545)
				continue;
			player.getPackets().sendGameMessage(
					"The bell re-sounds loudly throughout the cavern.");
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					NPC npc = World.spawnNPC(5751, player, -1, true);
					npc.getCombat().setTarget(player);
					WorldObject o = new WorldObject(object);
					o.setId(22544);
					World.spawnObjectTemporary(o, 30000);
				}
			}, 1);
			return true;
		}
		return false;
	}

	public static boolean isBlackMask(int requestedId) {
		return requestedId >= 8901 && requestedId <= 8920;
	}

	private static final int[] SLAYER_HELMET_PARTS = { 8921, 4166, 4164, 4551,
			4168 };
	private static final int[] FULL_SLAYER_HELMET_PARTS = { 13263, 15490, 15488 };

	public static boolean createSlayerHelmet(Player player, int itemUsed,
			int itemUsedWith) {
		if (itemUsed == itemUsedWith)
			return false;
		boolean firstCycle = false, secondCycle = false, full = false;
		for (int parts : SLAYER_HELMET_PARTS) {
			if (itemUsed == parts)
				firstCycle = true;
			if (itemUsedWith == parts)
				secondCycle = true;
		}
		if (!firstCycle || !secondCycle) {
			firstCycle = false;
			secondCycle = false;
			for (int parts : FULL_SLAYER_HELMET_PARTS) {
				if (itemUsed == parts)
					firstCycle = true;
				if (itemUsedWith == parts)
					secondCycle = true;
			}
			full = true;
		}
		if (firstCycle && secondCycle) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < 55) {
				player.getPackets()
						.sendGameMessage(
								"You need a Crafting level of 55 in order to assemble a slayer helmet.");
				return false;
			}
			for (int parts : (full ? FULL_SLAYER_HELMET_PARTS
					: SLAYER_HELMET_PARTS))
				if (!player.getInventory().containsItem(parts, 1))
					return false;
			for (int parts : (full ? FULL_SLAYER_HELMET_PARTS
					: SLAYER_HELMET_PARTS))
				player.getInventory().deleteItem(parts, 1);
			player.getInventory().addItem(
					new Item(full ? 15492 : FULL_SLAYER_HELMET_PARTS[0], 1));
			player.getPackets().sendGameMessage(
					full ? "You attach two parts to your slayer helmet."
							: "You combine all parts of the helmet.");
			return true;
		}
		return false;
	}

	public static void dissasembleSlayerHelmet(Player player, boolean full) {
		if (!(player.getInventory().getFreeSlots() >= (full ? 2 : 4))) {
			player.getPackets()
					.sendGameMessage(
							"You don't have enough space in your inventory to dissassemble the helmet.");
			return;
		}
		player.getInventory().deleteItem(full ? 15492 : 13263, 1);
		if (full) {
			for (int parts : FULL_SLAYER_HELMET_PARTS)
				player.getInventory().addItemDrop(parts, 1);
		} else {
			for (int parts : SLAYER_HELMET_PARTS)
				player.getInventory().addItemDrop(parts, 1);
		}
	}

	public static boolean isSlayerHelmet(Item item) {
		return item.getName().toLowerCase().contains("slayer helm");
	}
}