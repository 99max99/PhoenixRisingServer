package net.kagani.game.player.actions.divination.actions;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.mining.MiningBase;
import net.kagani.utils.Utils;

public final class DivineMining extends MiningBase {

	public enum RockDefinitions {

		DIVINE_SEREN_ORE(89, 296.7, 32262, 10, 10, 87290, 360, -1), DIVINE_RUNE_ORE(
				85, 125, 451, 10, 10, 87290, 360, -1), DIVINE_ADAMANTITE_ORE(
				70, 125, 449, 10, 10, 87289, 360, -1), DIVINE_MITHRIL_ORE(55,
				125, 447, 10, 10, 87288, 360, -1), DIVINE_COAL_ORE(30, 125,
				453, 10, 10, 87287, 360, -1), DIVINE_IRON_ORE(15, 125, 440, 10,
				10, 87286, 360, -1), DIVINE_BRONZE_ORE(1, 125, 436, 10, 10,
				87285, 360, -1);

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

		public int getRandomLifeProbability() {
			return randomLifeProbability;
		}
	}

	private WorldObject rock;
	private RockDefinitions definitions;

	public DivineMining(WorldObject rock, RockDefinitions definitions) {
		this.rock = rock;
		this.definitions = definitions;
	}

	@Override
	public boolean start(Player player) {
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
				- Utils.random(pickaxeTime);
		if (mineTimer < 1 + definitions.getOreRandomTime())
			mineTimer = 1 + Utils.random(definitions.getOreRandomTime());
		mineTimer /= player.getAuraManager().getMininingAccurayMultiplier();
		return mineTimer;
	}

	private boolean checkAll(Player player) {
		if (!hasPickaxe(player)) {
			player.getPackets().sendGameMessage(
					"You need a pickaxe to mine this rock.");
			return false;
		}
		if (!setPickaxe(player)) {
			player.getPackets().sendGameMessage(
					"You dont have the required level to use this pickaxe.");
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
		player.setNextAnimation(new Animation(17310));
		player.setNextGraphics(new Graphics(3304));
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
			} else if (Utils.random(definitions.getRandomLifeProbability()) == 0
					&& definitions != RockDefinitions.DIVINE_RUNE_ORE
					&& definitions != RockDefinitions.DIVINE_ADAMANTITE_ORE
					&& definitions != RockDefinitions.DIVINE_MITHRIL_ORE
					&& definitions != RockDefinitions.DIVINE_COAL_ORE
					&& definitions != RockDefinitions.DIVINE_IRON_ORE
					&& definitions != RockDefinitions.DIVINE_BRONZE_ORE) {
				World.spawnTemporaryObject(
						new WorldObject(definitions.getEmptyId(), rock
								.getType(), rock.getRotation(), rock.getX(),
								rock.getY(), rock.getPlane()),
						definitions.respawnDelay * 600, false);
				player.setNextAnimation(new Animation(-1));
				return -1;
			}
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

	public void addOre(Player player) {
		double xpBoost = 0;
		int idSome = 0;
		double totalXp = definitions.getXp() + xpBoost;
		if (hasMiningSuit(player))
			totalXp *= 1.025;
		player.getSkills().addXp(Skills.MINING, totalXp);
		if (definitions.getOreId() != -1) {
			player.Oreid = definitions.getOreId();
			int roll = Utils.random(100);
			Player owner = rock.getOwner();
			if (player == owner && roll >= 60) {
				player.getInventory().addItem(definitions.getOreId() + idSome,
						1);
				String oreName = ItemDefinitions
						.getItemDefinitions(definitions.getOreId() + idSome)
						.getName().toLowerCase();
				player.getPackets().sendGameMessage(
						"You mine some " + oreName + ".", true);
			} else {
				String oreName = ItemDefinitions
						.getItemDefinitions(definitions.getOreId() + idSome)
						.getName().toLowerCase();
				owner.getInventory().addItem(definitions.getOreId() + 1, 1);
				owner.getPackets().sendGameMessage(
						"" + player.getUsername() + " mined some " + oreName
								+ " for you.");
				player.getInventory().addItem(definitions.getOreId() + idSome,
						1);
				player.getPackets().sendGameMessage(
						"You mine some " + oreName + ".", true);
			}
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