package net.kagani.game.player.controllers;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.ForceMovement;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.map.bossInstance.BossInstanceHandler;
import net.kagani.game.map.bossInstance.BossInstanceHandler.Boss;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.agility.Agility;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.network.decoders.handlers.ObjectHandler;
import net.kagani.utils.Utils;

public class GodWars extends Controller {

	public static final int EMPTY_SECTOR = -1, BANDOS = 0, ARMADYL = 1,
			SARADOMIN = 2, ZAMORAK = 3, ZAROS = 4;
	private static final int BANDOS_SECTOR = 4, ARMADYL_SECTOR = 5,
			SARADOMIN_SECTOR = 6, ZAMORAK_SECTOR = 7, ZAROS_PRE_CHAMBER = 8,
			ZAROS_SECTOR = 9;
	public static final WorldTile[] CHAMBER_TELEPORTS = {
			new WorldTile(2863, 5357, 0), new WorldTile(2857, 5357, 0), // bandos
			new WorldTile(2835, 5295, 0), new WorldTile(2835, 5294, 0), // armadyl
			new WorldTile(2923, 5256, 0), new WorldTile(2923, 5257, 0), // saradomin
			new WorldTile(2925, 5332, 0), new WorldTile(2925, 5333, 0), // zamorak
	};

	public int[] killCount = new int[5];
	private long lastPrayerRecharge;
	private int sector;

	@Override
	public void start() {
		sector = EMPTY_SECTOR;// We always start with this :)
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendMinigameInterface(601);
		if (sector == ZAROS_PRE_CHAMBER)
			player.getPackets().sendExecuteScriptReverse(1171);
		refresh();
	}

	@Override
	public boolean logout() {
		setArguments(new Object[] { killCount, lastPrayerRecharge, sector });
		return false; // so doesnt remove script
	}

	@Override
	public boolean login() {
		/*
		 * if (this.getArguments().length != 3) return true;
		 */
		killCount = (int[]) this.getArguments()[0];
		lastPrayerRecharge = (long) this.getArguments()[1];
		sector = (int) this.getArguments()[2];
		sendInterfaces();
		return false; // so doesnt remove script
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 57225) {
			player.getDialogueManager().startDialogue("NexEntrance");
			return false;
		} else if (object.getId() == 26287 || object.getId() == 26286
				|| object.getId() == 26288 || object.getId() == 26289) {
			if (lastPrayerRecharge >= Utils.currentTimeMillis()) {
				player.getPackets()
						.sendGameMessage(
								"You must wait a total of 10 minutes before being able to recharge your prayer points.");
				return false;
			} else if (player.getAttackedByDelay() >= Utils.currentTimeMillis()) {
				player.getPackets()
						.sendGameMessage(
								"You cannot recharge your prayer while engaged in combat.");
				return false;
			}
			player.getPrayer().restorePrayer(
					player.getSkills().getLevelForXp(Skills.PRAYER) * 10);
			player.setNextAnimation(new Animation(645));
			player.getPackets().sendGameMessage(
					"Your prayer points feel rejuvinated.");
			lastPrayerRecharge = 600000 + Utils.currentTimeMillis();
			return false;
		} else if (object.getId() == 57258) {
			if (sector == ZAROS) {
				player.getPackets().sendGameMessage(
						"The door will not open in this direction.");
				return false;
			}

			boolean hasCerimonial = hasFullCerimonial(player);
			int requiredKc = getRequiredKillcount();
			if (killCount[4] >= requiredKc || hasCerimonial) {
				if (hasCerimonial)
					player.getPackets()
							.sendGameMessage(
									"The door recognises your familiarity with the area and allows you to pass through.");
				if (killCount[4] >= requiredKc)
					killCount[4] -= requiredKc;
				sector = ZAROS;
				player.addWalkSteps(2900, 5203, -1, false);
				refresh();
			} else
				player.getPackets()
						.sendGameMessage(
								"You don't have enough kills to enter the lair of Zaros.");
			return false;
		} else if (object.getId() == 57234) {
			if (!Agility.hasLevel(player, 70))
				return false;
			final boolean travelingEast = sector == ZAROS_PRE_CHAMBER;
			player.setNextAnimation(new Animation(1133));
			final WorldTile tile = new WorldTile(
					2863 + (travelingEast ? 0 : -3), 5219, 0);
			player.setNextForceMovement(new ForceMovement(tile, 1,
					travelingEast ? ForceMovement.EAST : ForceMovement.WEST));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					sector = travelingEast ? ZAROS_SECTOR : ZAROS_PRE_CHAMBER;
					player.setNextWorldTile(tile);
					sendInterfaces();
				}
			}, 1);
			return false;
		} else if (object.getId() == 75089) {
			if (sector == ZAROS_PRE_CHAMBER) {
				player.getDialogueManager()
						.startDialogue("SimpleMessage",
								"You pull out your key once more but the door doesn't respond.");
				return false;
			}
			if (player.getInventory().containsItem(20120, 1)) {
				player.getPackets().sendGameMessage(
						"You flash the key in front of the door");
				player.useStairs(1133, new WorldTile(2887, 5278, 0), 1, 2,
						"...and a strange force flings you in.");
				sector = ZAROS_PRE_CHAMBER;
			} else
				player.getDialogueManager()
						.startDialogue(
								"SimpleMessage",
								"You try to push the door open, but it wont budge.... It looks like there is some kind of key hole.");
			return false;
		} else if (object.getId() == 57256) {
			player.useStairs(-1, new WorldTile(2855, 5222, 0), 1, 2,
					"You climb down the stairs.");
			return false;
		} else if (object.getId() == 57260) {
			player.useStairs(-1, new WorldTile(2887, 5276, 0), 1, 2,
					"You climb up the stairs.");
			return false;
		} else if (object.getId() == 26293) {
			player.useStairs(828, new WorldTile(2913, 3741, 0), 1, 2);
			player.getControlerManager().forceStop();
			return false;
		} else if (object.getId() == 26384) { // bandos
			if (player.getSkills().getLevel(Skills.STRENGTH) < 70) {
				player.getPackets()
						.sendGameMessage(
								"You attempt to hit the door, but realize that you are not yet experienced enough.");
				return false;
			}
			final boolean withinBandos = sector == BANDOS_SECTOR;
			if (!withinBandos)
				player.setNextAnimation(new Animation(7002));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					ObjectHandler.handleDoor(player, object, 1000);
					player.addWalkSteps(withinBandos ? 2851 : 2850, 5334, -1,
							false);
					sector = withinBandos ? EMPTY_SECTOR : BANDOS_SECTOR;
				}
			}, withinBandos ? 0 : 1);
			return false;
		} else if (object.getId() == 75463) {
			if (object.withinDistance(player, 7)) {
				final boolean withinArmadyl = sector == ARMADYL_SECTOR;
				final WorldTile tile = new WorldTile(2872, withinArmadyl ? 5280
						: 5272, 0);
				WorldTasksManager.schedule(new WorldTask() {

					int ticks = 0, projectileTicks = 0;

					@Override
					public void run() {
						ticks++;
						if (ticks == 1) {
							player.setNextAnimation(new Animation(827));
							player.setNextFaceWorldTile(tile);
							player.lock();
						} else if (ticks == 3)
							player.setNextAnimation(new Animation(385));
						else if (ticks == 5) {
							player.setNextAnimation(new Animation(16635));
						} else if (ticks == 6) {
							player.getAppearence().setHidden(true);
							projectileTicks = ticks
									+ Utils.projectileTimeToCycles(World
											.sendProjectileNew(player, tile,
													2699, 18, 18, 20, 3, 175, 0)
											.getEndTime());
							player.setNextForceMovement(new ForceMovement(
									player, 1, tile, 6,
									withinArmadyl ? ForceMovement.NORTH
											: ForceMovement.SOUTH));
						} else if (ticks == projectileTicks) {
							player.getAppearence().setHidden(false);
							player.setNextAnimation(new Animation(16672));
							player.setNextWorldTile(tile);
							player.unlock();
							player.resetReceivedHits();
							sector = withinArmadyl ? EMPTY_SECTOR
									: ARMADYL_SECTOR;
							stop();
							return;
						}
					}
				}, 0, 1);
			}
			return false;
		} else if (object.getId() == 26439) {
			if (!Agility.hasLevel(player, 70))
				return false;
			final boolean withinZamorak = sector == ZAMORAK_SECTOR;
			final WorldTile tile = new WorldTile(2887, withinZamorak ? 5336
					: 5346, 0);
			player.lock();
			player.setNextWorldTile(object);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					player.setNextAnimation(new Animation(17454));
					player.setNextFaceWorldTile(tile);
					if (!withinZamorak) {
						player.getPrayer().drainPrayer();
						sector = ZAMORAK_SECTOR;
					} else
						sector = EMPTY_SECTOR;
					sendInterfaces();
				}
			}, 1);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					player.unlock();
					player.resetReceivedHits();
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(tile);
				}
			}, 5);
			return false;
		} else if (object.getId() == 75462) {
			if (object.getX() == 2912
					&& (object.getY() == 5298 || object.getY() == 5299)) {
				sector = SARADOMIN_SECTOR;
				useAgilityStones(player, object,
						new WorldTile(2915, object.getY(), 0), 70, 15239, 7);
			} else if (object.getX() == 2914
					&& (object.getY() == 5298 || object.getY() == 5299)) {
				sector = EMPTY_SECTOR;
				useAgilityStones(player, object,
						new WorldTile(2911, object.getY(), 0), 70, 3378, 7);
			} else if ((object.getX() == 2919 || object.getX() == 2920)
					&& object.getY() == 5278)
				useAgilityStones(player, object, new WorldTile(object.getX(),
						5275, 0), 70, 15239, 7);
			else if ((object.getX() == 2920 || object.getX() == 2919)
					&& object.getY() == 5276)
				useAgilityStones(player, object, new WorldTile(object.getX(),
						5279, 0), 70, 3378, 7);
			return false;
			/* Bandos */
		} else if (object.getId() == 84022) {
			int index = BANDOS;
			int requiredKc = getRequiredKillcount();
			if (player.getRights() == 2 || killCount[index] >= requiredKc) {
				if (player.getX() >= 2857 && player.getX() <= 2861) {
					WorldTile tile = new WorldTile(2856, 5357, 0);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
				} else {
					WorldTile tile = new WorldTile(2857, 5357, 0);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
					killCount[index] -= requiredKc;
				}
				sector = index;
				refresh();
			} else
				player.getPackets().sendGameMessage(
						"You need to have a killcount of " + requiredKc
								+ " kills to enter.");
			return false;
		} else if (object.getId() == 26425) {
			int index = BANDOS;
			if (player.getX() >= 2857 && player.getX() <= 2862)
				BossInstanceHandler.enterInstance(player, Boss.General_Graador);
			sector = index;
			refresh();
			return false;
			/* Armadyl */
		} else if (object.getId() == 84024) {
			int index = ARMADYL;
			int requiredKc = getRequiredKillcount();
			if (player.getRights() == 2 || killCount[index] >= requiredKc) {
				if (player.getX() >= 2831 && player.getX() <= 2835) {
					WorldTile tile = new WorldTile(2830, 5284, 0);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
					killCount[index] -= requiredKc;
				} else {
					WorldTile tile = new WorldTile(2831, 5284, 0);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
				}
				sector = index;
				refresh();
			} else
				player.getPackets().sendGameMessage(
						"You need to have a killcount of " + requiredKc
								+ " kills to enter.");
			return false;
		} else if (object.getId() == 26426) {
			int index = ARMADYL;
			if (player.getY() >= 5291 && player.getY() <= 5294)
				BossInstanceHandler.enterInstance(player, Boss.Kree_Arra);
			sector = index;
			refresh();
			return false;
			/* ZAMORAK */
		} else if (object.getId() == 84028) {
			int index = ZAMORAK;
			int requiredKc = getRequiredKillcount();
			if (player.getRights() == 2 || killCount[index] >= requiredKc) {
				if (player.getY() >= 5333 && player.getY() <= 5339) {
					WorldTile tile = new WorldTile(2925, 5340, 0);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
				} else {
					WorldTile tile = new WorldTile(2925, 5339, 0);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
					killCount[index] -= requiredKc;
				}
				sector = index;
				refresh();
			} else
				player.getPackets().sendGameMessage(
						"You need to have a killcount of " + requiredKc
								+ " kills to enter.");
			return false;
		} else if (object.getId() == 26428) {
			int index = ZAMORAK;
			if (player.getY() >= 5333 && player.getY() <= 5339)
				BossInstanceHandler.enterInstance(player, Boss.Kril_Tsutsaroth);
			sector = index;
			refresh();
			return false;
			/* SARADOMIN */
		} else if (object.getId() == 84026) {
			int index = SARADOMIN;
			int requiredKc = getRequiredKillcount();
			if (player.getRights() == 2 || killCount[index] >= requiredKc) {
				if (player.getY() >= 5265 && player.getY() <= 5268) {
					WorldTile tile = new WorldTile(2919, 5264, 0);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
					killCount[index] -= requiredKc;
				} else {
					WorldTile tile = new WorldTile(2919, 5265, 0);
					player.addWalkSteps(tile.getX(), tile.getY(), -1, false);
				}
				sector = index;
				refresh();
			} else
				player.getPackets().sendGameMessage(
						"You need to have a killcount of " + requiredKc
								+ " kills to enter.");
			return false;
		} else if (object.getId() == 26427) {
			int index = SARADOMIN;
			if (player.getY() >= 5257 && player.getY() <= 5263)
				BossInstanceHandler.enterInstance(player,
						Boss.Commander_Zilyana);
			sector = index;
			refresh();
			return false;
		}
		return true;
	}

	private int getRequiredKillcount() {
		if (player.isBronzeMember())
			return 30;
		else if (player.isSilverMember())
			return 20;
		else if (player.isGoldMember())
			return 10;
		else if (player.isPlatinumMember())
			return 0;
		else if (player.isDiamondMember())
			return 0;
		return 40;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		if (object.getId() == 26287 || object.getId() == 26286
				|| object.getId() == 26288 || object.getId() == 26289) {
			player.getPackets()
					.sendGameMessage(
							"The god's pitty you and allow you to leave the encampment.");
			player.setNextWorldTile(new WorldTile(Settings.HOME_LOCATION));
			player.getControlerManager().forceStop();
			sector += 4;
			player.resetReceivedHits();
			return false;
		}
		return true;
	}

	public void incrementKillCount(int index) {
		killCount[index]++;
		refresh();
	}

	public void resetKillCount(int index) {
		killCount[index] = 0;
		refresh();
		player.getPackets()
				.sendGameMessage(
						"The power of all those you slew in the dungeon drains from your body.");
	}

	public void refresh() {
		player.getPackets()
				.sendIComponentText(601, 18, "" + killCount[ARMADYL]);// arma
		player.getPackets().sendIComponentText(601, 19, "" + killCount[BANDOS]);// bando
		player.getPackets().sendIComponentText(601, 20,
				"" + killCount[SARADOMIN]);// sara
		player.getPackets()
				.sendIComponentText(601, 21, "" + killCount[ZAMORAK]);// zamy
		player.getVarsManager().sendVarBit(15163, killCount[ARMADYL]);// arma
		player.getVarsManager().sendVarBit(15165, killCount[BANDOS]);// bando
		player.getVarsManager().sendVarBit(15162, killCount[SARADOMIN]);// sara
		player.getVarsManager().sendVarBit(15164, killCount[ZAMORAK]);// zamy
		player.getVarsManager().sendVarBit(15166, killCount[ZAROS]);// zaros
	}

	public static void useAgilityStones(final Player player,
			final WorldObject object, final WorldTile tile, int levelRequired,
			final int emote, final int delay) {
		if (!Agility.hasLevel(player, levelRequired))
			return;
		player.faceObject(object);
		player.addWalkSteps(object.getX(), object.getY());
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.resetReceivedHits();// Kinda unfair if not :)
				player.useStairs(emote, tile, delay, delay + 1);
			}
		}, 1);
	}

	@Override
	public boolean sendDeath() {
		if (sector > EMPTY_SECTOR && sector < ZAMORAK) {
			player.lock(8);
			player.stopAll();

			WorldTasksManager.schedule(new WorldTask() {
				int loop;

				@Override
				public void run() {
					if (loop == 0) {
						player.setNextAnimation(player.getDeathAnimation());
					} else if (loop == 1) {
						player.getPackets().sendGameMessage(
								"Oh dear, you have died.");
					} else if (loop == 3) {
						player.getControlerManager().forceStop();
						player.getControlerManager().startControler(
								"DeathEvent",
								CHAMBER_TELEPORTS[(sector * 2) + 1],
								player.hasSkull());
					} else if (loop == 4) {
						player.getMusicsManager().playMusicEffect(
								MusicsManager.DEATH_MUSIC_EFFECT);
						stop();
					}
					loop++;
				}
			}, 0, 1);
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControlerManager().forceStop();
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeMinigameInterface();
	}

	public static void passGiantBoulder(Player player, WorldObject object,
			boolean liftBoulder) {
		if (player.getSkills().getLevel(
				liftBoulder ? Skills.STRENGTH : Skills.AGILITY) < 60) {
			player.getPackets().sendGameMessage(
					"You need a " + (liftBoulder ? "Agility" : "Strength")
							+ " of 60 in order to "
							+ (liftBoulder ? "lift" : "squeeze past")
							+ "this boulder.");
			return;
		}
		if (liftBoulder)
			World.sendObjectAnimation(object, new Animation(318));
		boolean isReturning = player.getY() >= 3709;
		int baseAnimation = liftBoulder ? 3725 : 3466;
		player.setRunEnergy(0);
		player.useStairs(isReturning ? baseAnimation-- : baseAnimation,
				new WorldTile(player.getX(), player.getY()
						+ (isReturning ? -4 : 4), 0), liftBoulder ? 10 : 5,
				liftBoulder ? 11 : 6, null, true);
	}

	private static boolean hasFullCerimonial(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int bootsId = player.getEquipment().getBootsId();
		int glovesId = player.getEquipment().getGlovesId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || bootsId == -1
				|| glovesId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName()
				.contains("Ancient ceremonial")
				&& ItemDefinitions.getItemDefinitions(chestId).getName()
						.contains("Ancient ceremonial")
				&& ItemDefinitions.getItemDefinitions(legsId).getName()
						.contains("Ancient ceremonial")
				&& ItemDefinitions.getItemDefinitions(bootsId).getName()
						.contains("Ancient ceremonial")
				&& ItemDefinitions.getItemDefinitions(glovesId).getName()
						.contains("Ancient ceremonial");
	}
}