package net.kagani.game.player.actions.mining;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.skillertasks.SkillTasks;
import net.kagani.utils.Utils;

public final class Mining extends MiningBase {

	public static enum RockDefinitions {

		Soft_Clay(1, 5, 1761, 15, 1, 11552, 5, 0),

		Clay_Ore(1, 5, 434, 10, 1, 11552, 5, 0),

		Copper_Ore(1, 17.5, 436, 10, 1, 11552, 5, 0),

		Tin_Ore(1, 17.5, 438, 15, 1, 11552, 5, 0),

		Blurite_Ore(10, 17.5, 668, 15, 1, 11552, 7, 0),

		Iron_Ore(15, 35, 440, 15, 1, 11552, 10, 0),

		Sandstone_Ore(35, 30, 6971, 30, 1, 11552, 10, 0),

		Silver_Ore(20, 40, 442, 25, 1, 11552, 20, 0),

		Coal_Ore(30, 50, 453, 50, 10, 11552, 30, 0),

		Granite_Ore(45, 50, 6979, 50, 10, 11552, 20, 0),

		Gold_Ore(40, 60, 444, 80, 20, 11554, 40, 0),

		Mithril_Ore(55, 180, 447, 100, 20, 11552, 60, 0),

		Adamant_Ore(70, 230, 449, 130, 25, 11552, 180, 0),

		Runite_Ore(85, 300, 451, 150, 30, 11552, 360, 0),

		Seren_Stone(89, 380.7, 32262, 160, 30, -1, -1, -1),

		LRC_Coal_Ore(77, 50, 453, 50, 10, -1, -1, -1),

		LRC_Gold_Ore(80, 60, 444, 40, 10, -1, -1, -1),

		RedSandStone(81, 70, 23194, 50, 10, -1, -1, -1),
		
		CrystalSandStone(81, 80, 32847, 50, 25, -1, -1, -1),

		GEM_ROCK(40, 65, -1, 20, 20, 11152, 60, 5);

		private int level;
		private double xp;
		private int oreId;
		private int oreBaseTime;
		private int oreRandomTime;
		private int emptySpot;
		private int respawnDelay;
		private int randomLifeProbability;

		private RockDefinitions(int level, double xp, int oreId,
				int oreBaseTime, int oreRandomTime, int emptySpot,
				int respawnDelay, int randomLifeProbability) {
			this.level = level;
			this.xp = xp;
			this.oreId = oreId;
			this.oreBaseTime = oreBaseTime;
			this.oreRandomTime = oreRandomTime;
			this.emptySpot = emptySpot;
			this.respawnDelay = respawnDelay;
			this.randomLifeProbability = randomLifeProbability;
		}

		public int getLevel() {
			return level;
		}

		public double getXp() {
			return xp;
		}

		public int getOreId() {
			return oreId;
		}

		public int getOreBaseTime() {
			return oreBaseTime;
		}

		public int getOreRandomTime() {
			return oreRandomTime;
		}

		public int getEmptyId() {
			return emptySpot;
		}

		public int getRespawnDelay() {
			return respawnDelay;
		}

		public int randomLifeProbability() {
			return randomLifeProbability;
		}
	}

	private WorldObject rock;
	private RockDefinitions definitions;
	private PickAxeDefinitions axeDefinitions;

	public Mining(WorldObject rock, RockDefinitions definitions) {
		this.rock = rock;
		this.definitions = definitions;
	}

	@Override
	public boolean start(Player player) {
		axeDefinitions = getPickAxeDefinitions(player, false);
		if (!checkAll(player))
			return false;
		player.getPackets().sendGameMessage(
				"You swing your pickaxe at the rock.", true);
		setActionDelay(player, getMiningDelay(player));
		return true;
	}

	private int getMiningDelay(Player player) {
		int summoningBonus = 0;
		if (player.getFamiliar() != null) {
			if (player.getFamiliar().getId() == 7342
					|| player.getFamiliar().getId() == 7342)
				summoningBonus += 10;
			else if (player.getFamiliar().getId() == 6832
					|| player.getFamiliar().getId() == 6831)
				summoningBonus += 1;
		}
		int mineTimer = definitions.getOreBaseTime()
				- (player.getSkills().getLevel(Skills.MINING) + summoningBonus)
				- Utils.random(axeDefinitions.getPickAxeTime());
		if (mineTimer < 1 + definitions.getOreRandomTime())
			mineTimer = 1 + Utils.random(definitions.getOreRandomTime());
		mineTimer /= player.getAuraManager().getMininingAccurayMultiplier();
		return mineTimer;
	}

	private boolean checkAll(Player player) {
		if (axeDefinitions == null) {
			player.getPackets()
					.sendGameMessage(
							"You do not have a pickaxe or do not have the required level to use the pickaxe.");
			return false;
		}
		if (!hasMiningLevel(player))
			return false;
		if (!player.getInventory().hasFreeSlots()) {
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return false;
		}
		return true;
	}

	private boolean hasMiningLevel(Player player) {
		if (definitions.getLevel() > player.getSkills().getLevel(Skills.MINING)) {
			player.getPackets().sendGameMessage(
					"You need a mining level of " + definitions.getLevel()
							+ " to mine this rock.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(axeDefinitions.getAnimationId()));
		return checkRock(player);
	}

	private boolean usedDeplateAurora;

	@Override
	public int processWithDelay(Player player) {
		addOre(player);
		if (definitions.getEmptyId() != -1) {
			if (!usedDeplateAurora
					&& (1 + Math.random()) < player.getAuraManager()
							.getChanceNotDepleteMN_WC()) {
				usedDeplateAurora = true;
			} else if (Utils.random(definitions.randomLifeProbability()) == 0) {
				World.spawnObjectTemporary(
						new WorldObject(definitions.getEmptyId(), rock
								.getType(), rock.getRotation(), rock.getX(),
								rock.getY(), rock.getPlane()),
						definitions.respawnDelay * 600, false, true);
				player.setNextAnimation(new Animation(-1));
				return -1;
			}
		}
		if (definitions.getOreId() == 32262) {
			if (Utils.random(25) == 0)
				player.stopAll();
		}
		if (!player.getInventory().hasFreeSlots()
				&& definitions.getOreId() != -1) {
			player.setNextAnimation(new Animation(-1));
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return -1;
		}
		return getMiningDelay(player);
	}

	private void addOre(Player player) {
		double xpBoost = 0;
		int idSome = 0;
		if (definitions == RockDefinitions.Granite_Ore) {
			idSome = Utils.random(2) * 2;
			if (idSome == 2)
				xpBoost += 10;
			else if (idSome == 4)
				xpBoost += 25;
		} else if (definitions == RockDefinitions.Sandstone_Ore) {
			idSome = Utils.random(3) * 2;
			xpBoost += idSome / 2 * 10;
		} else if (definitions == RockDefinitions.RedSandStone || definitions == RockDefinitions.CrystalSandStone) {
			if (player.getRedStoneDelay() >= Utils.currentTimeMillis()) {
				player.getPackets()
						.sendGameMessage(
								"It seems that there is no remaining ore, check again in twelve hours.");
				stop(player);
				return;
			}
			player.increaseRedStoneCount();
			if (player.getRedStoneCount() >= (player.isGoldMember()
					|| player.isPlatinumMember() || player.isDiamondMember() ? 225
						: player.isSilverMember() ? 150 : 75)) {
				player.resetRedStoneCount();
				player.setStoneDelay(3600000 * 24); // 12 hours
				player.getVarsManager().sendVarBit(10133, 26);
			} else if (player.getRedStoneCount() == 125)
				player.getVarsManager().sendVarBit(10133, 25);
		}
		final int gem = Utils.random(55);
		if (gem == 1)
			RockDefinitions.GEM_ROCK.oreId = 1617;
		else if (gem >= 2 && gem <= 5)
			RockDefinitions.GEM_ROCK.oreId = 1619;
		else if (gem >= 6 && gem <= 10)
			RockDefinitions.GEM_ROCK.oreId = 1621;
		else if (gem >= 11 && gem <= 18)
			RockDefinitions.GEM_ROCK.oreId = 1623;
		else if (gem >= 19 && gem <= 28)
			RockDefinitions.GEM_ROCK.oreId = 1629;
		else if (gem >= 29 && gem <= 40)
			RockDefinitions.GEM_ROCK.oreId = 1627;
		else if (gem >= 41 && gem <= 55)
			RockDefinitions.GEM_ROCK.oreId = 1625;

		RockDefinitions.GEM_ROCK.emptySpot = 11552;
		double totalXp = definitions.getXp() * 1.45 + xpBoost;
		if (hasMiningSuit(player))
			totalXp *= 1.025;
		player.getSkills().addXp(Skills.MINING, totalXp / 2);
		if (definitions.getOreId() != -1) {
			player.getInventory().addItem(definitions.getOreId() + idSome, 1);
			if ((Utils.currentTimeMillis() - player.getLastStarSprite()) <= 15 * 60 * 1000)
				player.getInventory().addItem(definitions.getOreId() + idSome,
						1);
			String oreName = ItemDefinitions
					.getItemDefinitions(definitions.getOreId() + idSome)
					.getName().toLowerCase();
			player.getPackets().sendGameMessage(
					"You mine some " + oreName + ".", true);
			if (definitions == RockDefinitions.Copper_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MCOPPER1);
				player.getSkillTasks().decreaseTask(SkillTasks.MCOPPER2);
			} else if (definitions == RockDefinitions.Tin_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MTIN1);
				player.getSkillTasks().decreaseTask(SkillTasks.MTIN2);
			} else if (definitions == RockDefinitions.Silver_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MSILVER1);
				player.getSkillTasks().decreaseTask(SkillTasks.MSILVER2);
				player.getSkillTasks().decreaseTask(SkillTasks.MSILVER3);
			} else if (definitions == RockDefinitions.Iron_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MIRON1);
				player.getSkillTasks().decreaseTask(SkillTasks.MIRON2);
				player.getSkillTasks().decreaseTask(SkillTasks.MIRON3);
			} else if (definitions == RockDefinitions.Coal_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MCOAL1);
				player.getSkillTasks().decreaseTask(SkillTasks.MCOAL2);
				player.getSkillTasks().decreaseTask(SkillTasks.MCOAL3);
			} else if (definitions == RockDefinitions.Gold_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MGOLD1);
				player.getSkillTasks().decreaseTask(SkillTasks.MGOLD2);
				player.getSkillTasks().decreaseTask(SkillTasks.MGOLD3);
			} else if (definitions == RockDefinitions.Mithril_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MMITHRIL1);
				player.getSkillTasks().decreaseTask(SkillTasks.MMITHRIL2);
			} else if (definitions == RockDefinitions.Adamant_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MADAMANT1);
				player.getSkillTasks().decreaseTask(SkillTasks.MADAMANT2);
			} else if (definitions == RockDefinitions.Runite_Ore) {
				player.getSkillTasks().decreaseTask(SkillTasks.MRUNE1);
				player.getSkillTasks().decreaseTask(SkillTasks.MRUNE2);
			} else if (definitions == RockDefinitions.Seren_Stone) {
				player.getSkillTasks().decreaseTask(SkillTasks.SERENSTONES1);
				player.getSkillTasks().decreaseTask(SkillTasks.SERENSTONES2);
				player.getSkillTasks().decreaseTask(SkillTasks.SERENSTONES3);
				player.getSkillTasks().decreaseTask(SkillTasks.SERENSTONES4);
			} else if (definitions == RockDefinitions.GEM_ROCK) {
				player.getSkillTasks().decreaseTask(SkillTasks.MGEM1);
				player.getSkillTasks().decreaseTask(SkillTasks.MGEM2);
				player.getSkillTasks().decreaseTask(SkillTasks.MGEM3);
				player.getSkillTasks().decreaseTask(SkillTasks.MGEM4);
			}
			if (player.getDailyTask() != null)
				player.getDailyTask().incrementTask(player, 3,
						definitions.getOreId(), Skills.MINING);
		}
	}

	private boolean hasMiningSuit(Player player) {
		if (player.getEquipment().getHatId() == 20789
				&& player.getEquipment().getChestId() == 20791
				&& player.getEquipment().getLegsId() == 20790
				&& player.getEquipment().getBootsId() == 20788)
			return true;
		return false;
	}

	private boolean checkRock(Player player) {
		return World.containsObjectWithId(rock, rock.getId());
	}
}