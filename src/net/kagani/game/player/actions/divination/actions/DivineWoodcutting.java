package net.kagani.game.player.actions.divination.actions;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.Action;
import net.kagani.utils.Utils;

public final class DivineWoodcutting extends Action {

	public static enum DivineTreeDefinitions {

		DIVINE_MAGIC(75, 250, 1513, 10, 5, 37824, 121, 0),

		DIVINE_YEW(60, 175, 1515, 10, 5, 1341, 94, 10),

		DIVINE_MAPLE(45, 100, 1517, 10, 5, 31057, 72, 10),

		DIVINE_WILLOW(30, 67.5, 1519, 10, 5, 1341, 51, 15),

		DIVINE_OAK(15, 37.5, 1521, 10, 5, 1341, 15, 15),

		DIVINE_NORMAL(1, 25, 1511, 10, 5, 1341, 8, 0);

		private int level;
		private double xp;
		private int logsId;
		private int logBaseTime;
		private int logRandomTime;
		private int stumpId;
		private int respawnDelay;
		private int randomLifeProbability;

		private DivineTreeDefinitions(int level, double xp, int logsId,
				int logBaseTime, int logRandomTime, int stumpId,
				int respawnDelay, int randomLifeProbability) {
			this.level = level;
			this.xp = xp;
			this.logsId = logsId;
			this.logBaseTime = logBaseTime;
			this.logRandomTime = logRandomTime;
			this.stumpId = stumpId;
			this.respawnDelay = respawnDelay;
			this.randomLifeProbability = randomLifeProbability;
		}

		public int getLevel() {
			return level;
		}

		public double getXp() {
			return xp;
		}

		public int getLogsId() {
			return logsId;
		}

		public int getLogBaseTime() {
			return logBaseTime;
		}

		public int getLogRandomTime() {
			return logRandomTime;
		}

		public int getStumpId() {
			return stumpId;
		}

		public int getRespawnDelay() {
			return respawnDelay;
		}

		public int getRandomLifeProbability() {
			return randomLifeProbability;
		}
	}

	private WorldObject tree;
	private DivineTreeDefinitions definitions;

	private int emoteId = 879;
	private boolean usingBeaver = false;
	private int axeTime;

	public DivineWoodcutting(WorldObject tree, DivineTreeDefinitions definitions) {
		this.tree = tree;
		this.definitions = definitions;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;

		setActionDelay(player, getWoodcuttingDelay(player));
		return true;
	}

	private int getWoodcuttingDelay(Player player) {
		int summoningBonus = player.getFamiliar() != null ? (player
				.getFamiliar().getId() == 6808 || player.getFamiliar().getId() == 6807) ? 10
				: 0
				: 0;
		int wcTimer = definitions.getLogBaseTime()
				- (player.getSkills().getLevel(8) + summoningBonus)
				- Utils.random(axeTime);
		if (wcTimer < 1 + definitions.getLogRandomTime())
			wcTimer = 1 + Utils.random(definitions.getLogRandomTime());
		wcTimer /= player.getAuraManager().getWoodcuttingAccurayMultiplier();
		return wcTimer;
	}

	private boolean checkAll(Player player) {
		if (!hasAxe(player)) {
			player.getPackets().sendGameMessage(
					"You need a hatchet to chop down this tree.");
			return false;
		}
		if (!hasWoodcuttingLevel(player)) {
			player.getPackets().sendGameMessage(
					"You need a level of: " + definitions.getLevel()
							+ " to chop this tree.");
			return false;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.getPackets().sendGameMessage(
					"You don't have enough space in your inventory.");
			return false;
		}

		return true;
	}

	private boolean hasWoodcuttingLevel(Player player) {
		if (definitions.getLevel() > player.getSkills().getLevel(8)) {
			player.getPackets().sendGameMessage(
					"You need a woodcutting level of " + definitions.getLevel()
							+ " to chop down this tree.");
			return false;
		}
		return true;
	}

	public static int getAxeAnim(Player player) {
		int level = player.getSkills().getLevel(8);
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId != -1) {
			switch (weaponId) {
			case 6739: // dragon axe
				if (level >= 61) {
					return 21279;
				}
				break;
			case 1359: // rune axe
				if (level >= 41) {
					return 867;
				}
				break;
			case 1357: // adam axe
				if (level >= 31) {
					return 21272;
				}
				break;
			case 1355: // mit axe
				if (level >= 21) {
					return 21273;
				}
				break;
			case 1361: // black axe
				if (level >= 11) {
					return 21275;
				}
				break;
			case 1353: // steel axe
				if (level >= 6) {
					return 21276;
				}
				break;
			case 1349: // iron axe
				return 21277;

			case 1351: // bronze axe
				return 21278;

			case 13661: // Inferno adze
				if (level >= 61) {
					return 10251;
				}
				break;
			}
		}
		if (player.getInventory().containsOneItem(6739)) {
			if (level >= 61) {
				return 21279;
			}
		}
		if (player.getInventory().containsOneItem(1359)) {
			if (level >= 41) {
				return 21272;
			}
		}
		if (player.getInventory().containsOneItem(1357)) {
			if (level >= 31) {
				return 21273;
			}
		}
		if (player.getInventory().containsOneItem(1355)) {
			if (level >= 21) {
				return 21274;
			}
		}
		if (player.getInventory().containsOneItem(1361)) {
			if (level >= 11) {
				return 21275;
			}
		}
		if (player.getInventory().containsOneItem(1353)) {
			if (level >= 6) {
				return 21276;
			}
		}
		if (player.getInventory().containsOneItem(1349)) {
			return 21277;
		}
		if (player.getInventory().containsOneItem(1351)) {
			return 21278;
		}
		if (player.getInventory().containsOneItem(13661)) {
			if (level >= 61) {
				return 10251;
			}
		}
		return -1;
	}

	@SuppressWarnings("unused")
	private boolean setAxe(Player player) {
		int level = player.getSkills().getLevel(8);
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId != -1) {
			switch (weaponId) {
			case 6739: // dragon axe
				if (level >= 61) {
					emoteId = 879;
					axeTime = 13;
					return true;
				}
				break;
			case 1359: // rune axe
				if (level >= 41) {
					emoteId = 879;
					axeTime = 10;
					return true;
				}
				break;
			case 1357: // adam axe
				if (level >= 31) {
					emoteId = 879;
					axeTime = 7;
					return true;
				}
				break;
			case 1355: // mit axe
				if (level >= 21) {
					emoteId = 879;
					axeTime = 5;
					return true;
				}
				break;
			case 1361: // black axe
				if (level >= 11) {
					emoteId = 879;
					axeTime = 4;
					return true;
				}
				break;
			case 1353: // steel axe
				if (level >= 6) {
					emoteId = 879;
					axeTime = 3;
					return true;
				}
				break;
			case 1349: // iron axe
				emoteId = 879;
				axeTime = 2;
				return true;
			case 1351: // bronze axe
				emoteId = 879;
				axeTime = 1;
				return true;
			case 13661: // Inferno adze
				if (level >= 61) {
					emoteId = 879;
					axeTime = 13;
					return true;
				}
				break;
			}
		}
		if (player.getInventory().containsOneItem(6739)) {
			if (level >= 61) {
				emoteId = 879;
				axeTime = 13;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1359)) {
			if (level >= 41) {
				emoteId = 879;
				axeTime = 10;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1357)) {
			if (level >= 31) {
				emoteId = 879;
				axeTime = 7;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1355)) {
			if (level >= 21) {
				emoteId = 879;
				axeTime = 5;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1361)) {
			if (level >= 11) {
				emoteId = 879;
				axeTime = 4;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1353)) {
			if (level >= 6) {
				emoteId = 879;
				axeTime = 3;
				return true;
			}
		}
		if (player.getInventory().containsOneItem(1349)) {
			emoteId = 879;
			axeTime = 2;
			return true;
		}
		if (player.getInventory().containsOneItem(1351)) {
			emoteId = 879;
			axeTime = 1;
			return true;
		}
		if (player.getInventory().containsOneItem(13661)) {
			if (level >= 61) {
				emoteId = 879;
				axeTime = 13;
				return true;
			}
		}
		return false;

	}

	private boolean hasAxe(Player player) {
		if (player.getInventory().containsItemToolBelt(1351)
				|| player.getInventory().containsItemToolBelt(1349)
				|| player.getInventory().containsItemToolBelt(1353)
				|| player.getInventory().containsItemToolBelt(1355)
				|| player.getInventory().containsItemToolBelt(1357)
				|| player.getInventory().containsItemToolBelt(1361)
				|| player.getInventory().containsItemToolBelt(1359)
				|| player.getInventory().containsItemToolBelt(6739)
				|| player.getInventory().containsItemToolBelt(13661)) {
			return true;
		}
		if (player.getInventory().containsOneItem(1351, 1349, 1353, 1355, 1357,
				1361, 1359, 6739, 13661))
			return true;
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId == -1)
			return false;
		switch (weaponId) {
		case 1351:// Bronze Axe
		case 1349:// Iron Axe
		case 1353:// Steel Axe
		case 1361:// Black Axe
		case 1355:// Mithril Axe
		case 1357:// Adamant Axe
		case 1359:// Rune Axe
		case 6739:// Dragon Axe
		case 13661: // Inferno adze
			return true;
		default:
			return false;
		}

	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(usingBeaver ? 1 : emoteId));
		return checkTree(player);
	}

	private boolean usedDeplateAurora;

	@Override
	public int processWithDelay(Player player) {
		addLog(player);
		if (!usedDeplateAurora
				&& (1 + Math.random()) < player.getAuraManager()
						.getChanceNotDepleteMN_WC()) {
			usedDeplateAurora = true;
		} else if (Utils.random(definitions.getRandomLifeProbability()) == 0
				&& definitions != DivineTreeDefinitions.DIVINE_MAGIC
				&& definitions != DivineTreeDefinitions.DIVINE_YEW
				&& definitions != DivineTreeDefinitions.DIVINE_MAPLE
				&& definitions != DivineTreeDefinitions.DIVINE_WILLOW
				&& definitions != DivineTreeDefinitions.DIVINE_OAK
				&& definitions != DivineTreeDefinitions.DIVINE_NORMAL) {
			long time = definitions.respawnDelay * 600;
			World.spawnTemporaryObject(
					new WorldObject(definitions.getStumpId(), tree.getType(),
							tree.getRotation(), tree.getX(), tree.getY(), tree
									.getPlane()), time);
			if (tree.getPlane() < 3) {
				WorldObject object = World.getObject(new WorldTile(
						tree.getX() - 1, tree.getY() - 1, tree.getPlane() + 1));

				if (object == null) {
					object = World.getObject(new WorldTile(tree.getX(), tree
							.getY() - 1, tree.getPlane() + 1));
					if (object == null) {
						object = World.getObject(new WorldTile(tree.getX() - 1,
								tree.getY(), tree.getPlane() + 1));
						if (object == null) {
							object = World.getObject(new WorldTile(tree.getX(),
									tree.getY(), tree.getPlane() + 1));
						}
					}
				}

				if (object != null)
					World.removeTemporaryObject(object, time, false);
			}
			player.setNextAnimation(new Animation(-1));
			return -1;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.setNextAnimation(new Animation(-1));
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return -1;
		}
		return getWoodcuttingDelay(player);
	}

	private void addLog(Player player) {
		int log = definitions.getLogsId();
		int notedlog = definitions.getLogsId() + 1;
		double xpBoost = 1.00;
		if (player.getEquipment().getChestId() == 10939)
			xpBoost += 0.008;
		if (player.getEquipment().getLegsId() == 10940)
			xpBoost += 0.006;
		if (player.getEquipment().getHatId() == 10941)
			xpBoost += 0.004;
		if (player.getEquipment().getBootsId() == 10933)
			xpBoost += 0.002;
		if (player.getEquipment().getChestId() == 10939
				&& player.getEquipment().getLegsId() == 10940
				&& player.getEquipment().getHatId() == 10941
				&& player.getEquipment().getBootsId() == 10933)
			xpBoost += 0.005;
		player.getSkills().addXp(8, definitions.getXp() * xpBoost);
		player.getInventory().addItem(definitions.getLogsId(), 1);
		int roll = Utils.random(100);
		Player owner = tree.getOwner();
		if (player == owner && roll >= 60) {
			String logName = ItemDefinitions
					.getItemDefinitions(definitions.getLogsId()).getName()
					.toLowerCase();
			player.getInventory().addItem(log, 1);
			player.getPackets().sendGameMessage("You cut a " + logName + ".",
					true);
		} else {
			String logName = ItemDefinitions
					.getItemDefinitions(definitions.getLogsId()).getName()
					.toLowerCase();
			owner.getInventory().addItem(notedlog, 1);
			owner.getPackets().sendGameMessage(
					"" + player.getUsername() + " cut some " + logName
							+ " for you.");
			player.getInventory().addItem(log, 1);
			player.getPackets().sendGameMessage(
					"You cut some " + logName + ".", true);
		}
	}

	private boolean checkTree(Player player) {
		return World.containsObjectWithId(tree, tree.getId());
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

}
