package net.kagani.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.kagani.Settings;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.executor.GameExecutorManager;
import net.kagani.executor.PlayerHandlerThread;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.item.FloorItem;
import net.kagani.game.item.Item;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.map.MapUtils;
import net.kagani.game.map.MapUtils.Structure;
import net.kagani.game.minigames.GodWarsBosses;
import net.kagani.game.minigames.PuroPuro;
import net.kagani.game.minigames.ShootingStars;
import net.kagani.game.minigames.WarriorsGuild;
import net.kagani.game.minigames.ZarosGodwars;
import net.kagani.game.minigames.clanwars.ClanWarRequestController;
import net.kagani.game.minigames.duel.DuelControler;
import net.kagani.game.minigames.fistofguthix.MinigameManager;
import net.kagani.game.minigames.soulwars.SoulLobby;
import net.kagani.game.minigames.soulwars.SoulWars;
import net.kagani.game.minigames.warbands.Warbands;
import net.kagani.game.minigames.warbands.Warbands.WarbandEvent;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.araxxi.Araxxi;
import net.kagani.game.npc.corp.CorporealBeast;
import net.kagani.game.npc.dagannoth.DagannothFledeling;
import net.kagani.game.npc.dagannoth.DagannothKing;
import net.kagani.game.npc.dragons.FrostDragon;
import net.kagani.game.npc.dragons.KingBlackDragon;
import net.kagani.game.npc.evilchicken.EvilChicken;
import net.kagani.game.npc.glacior.Glacor;
import net.kagani.game.npc.godwars.GodWarMinion;
import net.kagani.game.npc.godwars.armadyl.GodwarsArmadylFaction;
import net.kagani.game.npc.godwars.armadyl.KreeArra;
import net.kagani.game.npc.godwars.bandos.GeneralGraardor;
import net.kagani.game.npc.godwars.bandos.GodwarsBandosFaction;
import net.kagani.game.npc.godwars.saradomin.CommanderZilyana;
import net.kagani.game.npc.godwars.saradomin.GodwarsSaradominFaction;
import net.kagani.game.npc.godwars.zammorak.GodwarsZammorakFaction;
import net.kagani.game.npc.godwars.zammorak.KrilTstsaroth;
import net.kagani.game.npc.godwars.zaros.Nex;
import net.kagani.game.npc.godwars.zaros.NexMinion;
import net.kagani.game.npc.godwars.zaros.ZarosMinion;
import net.kagani.game.npc.kalphite.KalphiteKing;
import net.kagani.game.npc.kalphite.KalphiteQueen;
import net.kagani.game.npc.nomad.FlameVortex;
import net.kagani.game.npc.nomad.Nomad;
import net.kagani.game.npc.others.AbyssalDemon;
import net.kagani.game.npc.others.BanditCampBandits;
import net.kagani.game.npc.others.BlackDemon;
import net.kagani.game.npc.others.ChaosElemental;
import net.kagani.game.npc.others.ConditionalDeath;
import net.kagani.game.npc.others.Cyclopse;
import net.kagani.game.npc.others.DoorSupport;
import net.kagani.game.npc.others.Ducks;
import net.kagani.game.npc.others.Elemental;
import net.kagani.game.npc.others.Fiara;
import net.kagani.game.npc.others.HarpieBug;
import net.kagani.game.npc.others.HoleInTheWall;
import net.kagani.game.npc.others.HunterTrapNPC;
import net.kagani.game.npc.others.Jadinko;
import net.kagani.game.npc.others.Kurask;
import net.kagani.game.npc.others.LivingRock;
import net.kagani.game.npc.others.Lucien;
import net.kagani.game.npc.others.MasterOfFear;
import net.kagani.game.npc.others.MaxTheMaxed;
import net.kagani.game.npc.others.MercenaryMage;
import net.kagani.game.npc.others.MutatedZygomites;
import net.kagani.game.npc.others.Nechryael;
import net.kagani.game.npc.others.PolyporeCreature;
import net.kagani.game.npc.others.Revenant;
import net.kagani.game.npc.others.RockCrabs;
import net.kagani.game.npc.others.Sheep;
import net.kagani.game.npc.others.SkillAlchemistNPC;
import net.kagani.game.npc.others.Strykewyrm;
import net.kagani.game.npc.others.TheDarkOracle;
import net.kagani.game.npc.others.TormentedDemon;
import net.kagani.game.npc.others.Werewolf;
import net.kagani.game.npc.others.WildyWyrm;
import net.kagani.game.npc.slayer.Airut;
import net.kagani.game.npc.vorago.Vorago;
import net.kagani.game.npc.vorago.VoragoMinion;
import net.kagani.game.player.ActionBar;
import net.kagani.game.player.CombatDefinitions;
import net.kagani.game.player.OwnedObjectManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.PlayerHealing;
import net.kagani.game.player.actions.TrapAction.HunterNPC;
import net.kagani.game.player.actions.divination.Wisp;
import net.kagani.game.player.actions.divination.WispInfo;
import net.kagani.game.player.actions.runecrafting.SiphionActionNodes;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.LivingRockCavern;
import net.kagani.game.player.content.Lottery;
import net.kagani.game.player.content.clans.ClansManager;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.controllers.JadinkoLair;
import net.kagani.game.player.controllers.Wilderness;
import net.kagani.game.route.Flags;
import net.kagani.utils.Logger;
import net.kagani.utils.SerializationUtilities;
import net.kagani.utils.ShopsHandler;
import net.kagani.utils.Utils;
import net.kagani.utils.Utils.EntityDirection;

public final class World {

	private static final EntityList<Player> players = new EntityList<Player>(
			Settings.PLAYERS_LIMIT, true);
	private static final List<Player> lobbyPlayers = new ArrayList<Player>(
			Settings.PLAYERS_LIMIT);
	private static final EntityList<NPC> npcs = new EntityList<NPC>(
			Settings.NPCS_LIMIT, false);
	private static final Map<Integer, Region> regions = Collections
			.synchronizedMap(new HashMap<Integer, Region>());

	public static String ChallengerName;

	public static final void init() {
		addWorldAnnouncementTask();
		addRestoreRunEnergyTask();
		addSoulWarsTimeTask();
		Lottery.INSTANCE.establish();
		addSoulWarsUpdateTask();
		addDrainPrayerTask();
		addRefreshTargetBuffsTask();
		addRestoreHitPointsTask();
		addRestoreNPCBonusesTask();
		addRestoreSkillsTask();
		addRestoreSpecialAttackTask();
		addRestoreShopItemsTask();
		addOwnedObjectsTask();
		addSaveTask();
		if (Settings.DOUBLE_XP)
			addIncreaseElapsedBonusMinutesTak();
		LivingRockCavern.init();
		SiphionActionNodes.init();
		PuroPuro.initPuroImplings();
		WarriorsGuild.init();
		JadinkoLair.init();
		ShootingStars.init();
		ActionBar.addActionBarTask();
		initalizeFactionsRegions();
		addWarbandsEventManager();
		soulWars();
		// sinkHoles();
	}

	public static NPC getNPC(int npcId) {
		for (NPC npc : getNPCs()) {
			if (npc.getId() == npcId) {
				return npc;
			}
		}
		return null;
	}

	public static void initalizeFactionsRegions() {
		MapBuilder.copyAllPlanesMap(248, 792, 392, 392, 8, 24);
	}

	public static void sinkHoles() {
		final int time = Utils.random(3600000, 15000000);
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					// SinkHoles.startEvent();
				} catch (final Throwable e) {
					Logger.handle(e);
				}
			}
		}, 5, time, TimeUnit.SECONDS);
	}

	public static void addWarbandsEventManager() {
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (Warbands.warband == null) {
					int random = Utils.random(WarbandEvent.values().length);
					if (WarbandEvent.getEvent(random) != null)
						Warbands.warband = new Warbands(random);
					return;
				}
				Warbands.warband.finish();
			}
		}, 5, 180, TimeUnit.MINUTES);
	}

	private static void addWorldAnnouncementTask() {
		if (Settings.ANNOUNCEMENT_TEXTS.length > 0) {
			GameExecutorManager.slowExecutor.scheduleWithFixedDelay(
					new Runnable() {
						@Override
						public void run() {
							try {
								World.sendNews(
										Settings.ANNOUNCEMENT_TEXTS[Utils
												.random(Settings.ANNOUNCEMENT_TEXTS.length)],
										World.GAME_NEWS);
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, 0, 5, TimeUnit.MINUTES);
		}
	}

	private static void addRestoreShopItemsTask() {
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ShopsHandler.restoreShops();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30, TimeUnit.SECONDS);
	}

	public static final void addIncreaseElapsedBonusMinutesTak() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					if (!Settings.DOUBLE_XP) {
						this.cancel();
						return;
					}
					for (Player player : getPlayers()) {
						if (player == null || !player.isRunning())
							continue;
						player.getSkills().increaseElapsedBonusMinues();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 60000);
	}

	public static final void addSaveTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || !player.isRunning())
							continue;
						byte[] data = SerializationUtilities
								.tryStoreObject(player);
						if (data == null || data.length <= 0)
							continue;
						PlayerHandlerThread.addSave(player.getUsername(), data);
						GrandExchange.save();
						ClansManager.saveClanmateDetails(player);
						// Highscores.saveHighScore(player);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 60000);
	}

	private static void addOwnedObjectsTask() {
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					OwnedObjectManager.processAll();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 600, TimeUnit.MILLISECONDS);
	}

	private static final void addRestoreSpecialAttackTask() {

		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null
								|| player.isDead()
								|| !player.isRunning()
								|| player.getCombatDefinitions()
										.getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
							continue;
						player.getCombatDefinitions().restoreSpecialAttack();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30000);
	}

	private static final void addRestoreHitPointsTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {

			private long cycle;

			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.isDead()
								|| !player.isRunning()
								|| player.isUnderCombat()
								|| cycle % player.getHealRestoreRate() != 0)
							continue;
						player.restoreHitPoints();
					}
					for (NPC npc : npcs) {
						if (npc == null || npc.isDead() || npc.hasFinished()
								|| npc.isUnderCombat()
								|| cycle % npc.getHealRestoreRate() != 0)
							continue;
						npc.restoreHitPoints();
					}
					cycle++;
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 600);
	}

	private static void addSoulWarsTimeTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					SoulLobby.minutes--;
				} catch (Exception e) {
					Logger.log(this,
							"An error has occured whilst processing Soul Wars: "
									+ e);
				}
			}

		}, 0L, 60000L);
	}

	private static void addSoulWarsUpdateTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {

				for (int index = 0; index < SoulLobby.allWaiting.size(); index++) {
					Player players = SoulLobby.allWaiting.get(index);
					if (SoulLobby.minutes == 0) {
						if (SoulLobby.allWaiting.size() >= 2
								&& SoulWars.startedGame == false)
							SoulWars.passPlayersToGame();
						else if (SoulLobby.minutes == 0
								&& SoulLobby.allWaiting.size() == 1)
							SoulWars.cantStart(players);
					}
				}

				for (Player player : getPlayers()) {

					if (SoulLobby.minutes == 0 || SoulLobby.minutes > 5)
						SoulLobby.minutes = 5;

					if (SoulWars.startedGame) {
						player.getPackets().sendIComponentText(837, 8,
								"Players required");
						player.getPackets().sendIComponentText(837, 3, "");
						player.getPackets().sendIComponentText(837, 5, "");
						player.getPackets().sendIComponentText(837, 9,
								"New game: " + SoulWars.gameTime + " min.");
					} else {
						player.getPackets().sendIComponentText(837, 8,
								"Players required");
						player.getPackets().sendIComponentText(837, 3,
								"" + SoulLobby.blueWait.size());
						player.getPackets().sendIComponentText(837, 5,
								"" + SoulLobby.redWait.size());
						player.getPackets().sendIComponentText(837, 9,
								"New game: " + SoulLobby.minutes + " min.");
					}
				}
			}
		}, 0L, 1000L);

	}

	private static final void addRestoreRunEnergyTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {

			private long cycle;

			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {

						if (player == null || player.isDead()
								|| !player.isRunning())
							continue;
						if (player.getNextRunDirection() == -1) {
							int r = player.isResting() ? 3
									: ((180 - player.getSkills().getLevel(
											Skills.AGILITY)) / 10);
							if (cycle % r != 0)
								continue;
							player.restoreRunEnergy();
						} else {
							double weight = player.getWeight();
							int r = weight >= 270
									|| player.getEffectsManager()
											.hasActiveEffect(
													EffectType.MORRIGAN_AXE) ? 1
									: (int) (10 - (weight / (weight < 0 ? 10
											: 30)));
							if (cycle % r != 0)
								continue;
							player.drainRunEnergy();
						}
					}
					cycle++;
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 100);
	}

	private static final void addDrainPrayerTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {

			private int curseTimer;

			@Override
			public void run() {
				try {
					boolean decreaseLeechs = curseTimer++ % 100 == 0; // every
					// 1min
					for (Player player : getPlayers()) {
						if (player == null || player.isDead()
								|| !player.isRunning())
							continue;
						player.getPrayer().processPrayerDrain();
						if (decreaseLeechs)
							player.getPrayer().decreaseLeechBonus();

					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 600);
	}

	private static final void addRestoreNPCBonusesTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (NPC npc : npcs) {
						if (npc == null || npc.isDead() || npc.hasFinished())
							continue;
						npc.restoreBonuses();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 3000);
	}

	private static final void addRefreshTargetBuffsTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || !player.isRunning())
							continue;
						player.getCombatDefinitions().refreshTargetBuffs();
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 600);

	}

	private static final void addRestoreSkillsTask() {
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || !player.isRunning())
							continue;
						int ammountTimes = player.getPrayer().usingPrayer(0, 8) ? 2
								: 1;
						if (player.isResting())
							ammountTimes += 1;
						boolean berserker = player.getPrayer()
								.usingPrayer(1, 9);
						b: for (int skill = 0; skill < 25; skill++) {
							if (skill == Skills.SUMMONING)
								continue b;
							c: for (int time = 0; time < ammountTimes; time++) {
								int currentLevel = player.getSkills().getLevel(
										skill);
								int normalLevel = player.getSkills()
										.getLevelForXp(skill);
								if (currentLevel > normalLevel && time == 0) {
									if (skill == Skills.ATTACK
											|| skill == Skills.STRENGTH
											|| skill == Skills.DEFENCE
											|| skill == Skills.RANGE
											|| skill == Skills.MAGIC) {
										if (berserker
												&& Utils.random(100) <= 15)
											continue c;
									}
									player.getSkills().set(skill,
											currentLevel - 1);
								} else if (currentLevel < normalLevel)
									player.getSkills().set(skill,
											currentLevel + 1);
								else
									break c;
							}
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 60000);

	}

	public static final Map<Integer, Region> getRegions() {
		return regions;
	}

	public static final Region getRegion(int id) {
		return getRegion(id, false);
	}

	public static final Region getRegion(int id, boolean load) {
		Region region = regions.get(id);
		if (region == null) {
			region = new Region(id);
			regions.put(id, region);
		}
		if (load)
			region.checkLoadMap();
		return region;
	}

	public static void removeProjectiles() {
		synchronized (getRegions()) {
			for (Region region : getRegions().values())
				region.removeProjectiles();
		}
	}

	public static final void addPlayer(Player player) {
		players.add(player);
	}

	public static void addLobbyPlayer(Player player) {
		synchronized (lobbyPlayers) {
			lobbyPlayers.add(player);
		}
	}

	public static void removeLobbyPlayer(Player player) {
		synchronized (lobbyPlayers) {
			lobbyPlayers.remove(player);
		}
	}

	public static void removePlayer(Player player) {
		players.remove(player);
	}

	public static final void addNPC(NPC npc) {
		npcs.add(npc);
	}

	public static final void removeNPC(NPC npc) {
		npcs.remove(npc);
	}

	public static final NPC spawnNPC(int id, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		NPC n = null;
		if (id >= 5533 && id <= 5558)
			n = new Elemental(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 46 || id == 2693 || id == 6133)
			n = new Ducks(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 2880)
			n = new DagannothFledeling(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 2440 || id == 2443 || id == 2446)
			n = new DoorSupport(id, tile);
		else if (WispInfo.forNpcId(id) != null)
			n = new Wisp(id, tile);
		else if (id == 16931)
			n = new Fiara(id, tile);
		/*
		 * else if (id == 17149 || id == 17150 || id == 17151 || id == 17152 ||
		 * id == 17153 || id == 17154) n = new Legio(null, mapAreaNameHash);
		 */
		else if (id == 17182 || id == 17183 || id == 17184)
			n = new Vorago(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 19464)
			n = new Araxxi(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned, null);
		else if (id == 18621)
			n = new Airut(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 17158 || id == 17159 || id == 17160 || id == 17185)
			n = new VoragoMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 15976)
			n = new MaxTheMaxed(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 5079)
			n = new HunterTrapNPC(HunterNPC.GREY_CHINCHOMPA, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5080)
			n = new HunterTrapNPC(HunterNPC.RED_CHINCHOMPA, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 19443)
			n = new HunterTrapNPC(HunterNPC.SKILLCHOMPAS, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5081)
			n = new HunterTrapNPC(HunterNPC.FERRET, id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6916)
			n = new HunterTrapNPC(HunterNPC.GECKO, id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 7272)
			n = new HunterTrapNPC(HunterNPC.MONKEY, id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 7272)
			n = new HunterTrapNPC(HunterNPC.RACCOON, id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 5073)
			n = new HunterTrapNPC(HunterNPC.CRIMSON_SWIFT, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5075)
			n = new HunterTrapNPC(HunterNPC.GOLDEN_WARBLER, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5076)
			n = new HunterTrapNPC(HunterNPC.COPPER_LONGTAIL, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5074)
			n = new HunterTrapNPC(HunterNPC.CERULEAN_TWITCH, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5072)
			n = new HunterTrapNPC(HunterNPC.TROPICAL_WAGTAIL, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 7031)
			n = new HunterTrapNPC(HunterNPC.WIMPY_BIRD, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5088)
			n = new HunterTrapNPC(HunterNPC.BARB_TAILED_KEBBIT, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1926 || id == 1931)
			n = new BanditCampBandits(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 5585)
			n = new SkillAlchemistNPC(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 6078 || id == 6079 || id == 4292 || id == 4291
				|| id == 6080 || id == 6081)
			n = new Cyclopse(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 9441)
			n = new FlameVortex(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 8832 && id <= 8834)
			n = new LivingRock(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 13465 && id <= 13481)
			n = new Revenant(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 1158 || id == 1160 || id == 16707 || id == 16708)
			n = new KalphiteQueen(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 8528 && id <= 8532)
			n = new Nomad(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 13456 || id == 13457 || id == 13458 || id == 13459)
			n = new ZarosMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 84 || id == 677 || id >= 4702 && id <= 4705
				|| id == 10722 || id == 10725)
			n = new BlackDemon(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 1613 || id == 10702)
			n = new Nechryael(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6261 || id == 6263 || id == 6265)
			n = GodWarsBosses.graardorMinions[(id - 6261) / 2] = new GodWarMinion(
					id, 0, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 6260)
			n = new GeneralGraardor(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6222)
			n = new KreeArra(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6223 || id == 6225 || id == 6227 || id == 6081)
			n = GodWarsBosses.armadylMinions[(id - 6223) / 2] = new GodWarMinion(
					id, 1, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 6203)
			n = new KrilTstsaroth(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 6204 || id == 6206 || id == 6208)
			n = GodWarsBosses.zamorakMinions[(id - 6204) / 2] = new GodWarMinion(
					id, 3, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 6248 || id == 6250 || id == 6252)
			n = GodWarsBosses.commanderMinions[(id - 6248) / 2] = new GodWarMinion(
					id, 2, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 6247)
			n = new CommanderZilyana(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 6210 && id <= 6221)
			n = new GodwarsZammorakFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 6254 && id <= 6259)
			n = new GodwarsSaradominFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 6268 && id <= 6283)
			n = new GodwarsBandosFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 6228 && id <= 6246)
			n = new GodwarsArmadylFaction(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 1615)
			n = new AbyssalDemon(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 2058)
			n = new HoleInTheWall(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id >= 9462 && id <= 9467)
			n = new Strykewyrm(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 3200)
			n = new ChaosElemental(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 6026 && id <= 6045)
			n = new Werewolf(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 1266 || id == 1268 || id == 2453 || id == 2886)
			n = new RockCrabs(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 8133)
			n = new CorporealBeast(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 3375)
			n = new EvilChicken(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 13447)
			n = ZarosGodwars.nex = new Nex(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 13451)
			n = new NexMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 13452)
			n = new NexMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 13453)
			n = new NexMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 13454)
			n = new NexMinion(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 14256)
			n = new Lucien(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id >= 2881 && id <= 2883)
			n = new DagannothKing(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 50)
			n = new KingBlackDragon(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 1282) {
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		} else if (id == 43 || (id >= 5156 && id <= 5164) || id == 5156
				|| id == 1765)
			n = new Sheep(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 51)
			n = new FrostDragon(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 8335)
			n = new MercenaryMage(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 8349 || id == 8450 || id == 8451)
			n = new TormentedDemon(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 15149)
			n = new MasterOfFear(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 1631 || id == 1632)
			n = new ConditionalDeath(4161, "The rockslug shrivels and dies.",
					true, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 14301)
			n = new Glacor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1610)
			n = new ConditionalDeath(
					4162,
					"The gargoyle breaks into peices as you slam the hammer onto its head.",
					false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 14849)
			n = new ConditionalDeath(23035, null, false, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1627 || id == 1628 || id == 1629 || id == 1630)
			n = new ConditionalDeath(new int[] { 13290, 4158 }, null, false,
					id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 2803 && id <= 2808)
			n = new ConditionalDeath(6696, null, true, id, tile,
					mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1609 || id == 1610)
			n = new Kurask(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 3153)
			n = new HarpieBug(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 3344 || id == 3345 || id == 3346 || id == 3347)
			n = new MutatedZygomites(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 13820 || id == 13821 || id == 13822)
			n = new Jadinko(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 14688 && id <= 14701)
			n = new PolyporeCreature(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id >= 15184 && id <= 15186)
			n = new TheDarkOracle(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 2417)
			n = new WildyWyrm(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else if (id == 16697 || id == 16698 || id == 16699)
			n = new KalphiteKing(id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea, spawned);
		else {
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
			if (id == 1131 || id == 1132 || id == 1133 || id == 1134)
				n.setForceAgressive(true);
		}
		return n;
	}

	public static final NPC spawnNPC(int id, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
				false);
	}
	
	public static final NPC spawnNPC(int id, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea, EntityDirection faceDirection) {
	                NPC returnValue = spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	                returnValue.setDirection(faceDirection.getValue());
	                return returnValue;
		}

	public static final void updateEntityRegion(Entity entity) {
		if (entity.hasFinished()) {
			if (entity instanceof Player)
				getRegion(entity.getLastRegionId()).removePlayerIndex(
						entity.getIndex());
			else
				getRegion(entity.getLastRegionId()).removeNPCIndex(
						entity.getIndex());
			return;
		}
		int regionId = entity.getRegionId();
		if (entity.getLastRegionId() != regionId) { // map region entity at
			// changed
			if (entity instanceof Player) {
				if (entity.getLastRegionId() > 0)
					getRegion(entity.getLastRegionId()).removePlayerIndex(
							entity.getIndex());
				Region region = getRegion(regionId);
				region.addPlayerIndex(entity.getIndex());
				Player player = (Player) entity;
				int musicId = region.getRandomMusicId();
				if (musicId != -1)
					player.getMusicsManager().checkMusic(musicId);
				player.getControlerManager().moved();
				if (player.hasStarted())
					checkControlersAtMove(player);
			} else {
				if (entity.getLastRegionId() > 0)
					getRegion(entity.getLastRegionId()).removeNPCIndex(
							entity.getIndex());
				getRegion(regionId).addNPCIndex(entity.getIndex());
			}
			entity.setLastRegionId(regionId);
		} else {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.getControlerManager().moved();
				if (player.hasStarted())
					checkControlersAtMove(player);
				if ((MinigameManager.INSTANCE().fistOfGuthix().isAtCave(player)
						|| MinigameManager.INSTANCE().fistOfGuthix()
								.isInLobby(player) || MinigameManager
						.INSTANCE().fistOfGuthix().team(player) != null))
					return;
			}
		}
	}

	private static void checkControlersAtMove(Player player) {
		if (player.getControlerManager().getControler() == null) {
			String control = null;
			PlayerHealing.heal(player);
			if (ClanWarRequestController.inWarRequest(player))
				control = "clan_wars_request";
			else if (DuelControler.isAtDuelArena(player))
				control = "DuelControler";
			if (player.getRegionId() == 13626)
				control = "Kalaboss";
			if (control != null)
				player.getControlerManager().startControler(control);
		}
	}

	/*
	 * checks clip
	 */
	public static boolean isRegionLoaded(int regionId) {
		Region region = getRegion(regionId);
		if (region == null)
			return false;
		return region.getLoadMapStage() == 2;
	}

	public static boolean isTileFree(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (!isFloorFree(plane, tileX, tileY)
						|| !isWallsFree(plane, tileX, tileY))
					return false;
		return true;
	}

	public static boolean isFloorFree(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (!isFloorFree(plane, tileX, tileY))
					return false;
		return true;
	}

	public static boolean isFloorFree(int plane, int x, int y) {
		return (getMask(plane, x, y) & (Flags.FLOOR_BLOCKSWALK
				| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ)) == 0;
	}

	public static boolean isWallsFree(int plane, int x, int y) {
		return (getMask(plane, x, y) & (Flags.CORNEROBJ_NORTHEAST
				| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHEAST
				| Flags.CORNEROBJ_SOUTHWEST | Flags.WALLOBJ_EAST
				| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST)) == 0;
	}

	public static int getMask(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null)
			return -1;
		return region.getMask(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion());
	}

	private static int getClipedOnlyMask(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null)
			return -1;
		return region.getMaskClipedOnly(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion());
	}

	public static final boolean checkProjectileStep(int plane, int x, int y,
			int dir, int size) {
		int xOffset = Utils.DIRECTION_DELTA_X[dir];
		int yOffset = Utils.DIRECTION_DELTA_Y[dir];
		/*
		 * int rotation = getRotation(plane,x+xOffset,y+yOffset); if(rotation !=
		 * 0) { dir += rotation; if(dir >= Utils.DIRECTION_DELTA_X.length) dir =
		 * dir - (Utils.DIRECTION_DELTA_X.length-1); xOffset =
		 * Utils.DIRECTION_DELTA_X[dir]; yOffset = Utils.DIRECTION_DELTA_Y[dir];
		 * }
		 */
		if (size == 1) {
			int mask = getClipedOnlyMask(plane, x
					+ Utils.DIRECTION_DELTA_X[dir], y
					+ Utils.DIRECTION_DELTA_Y[dir]);
			if (xOffset == -1 && yOffset == 0)
				return (mask & 0x42240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask & 0x60240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask & 0x40a40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask & 0x48240000) == 0;
			if (xOffset == -1 && yOffset == -1) {
				return (mask & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == 1 && yOffset == -1) {
				return (mask & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == -1 && yOffset == 1) {
				return (mask & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
			if (xOffset == 1 && yOffset == 1) {
				return (mask & 0x78240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getClipedOnlyMask(plane, x + 2, y) & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y + 1) & 0x78240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y - 1) & 0x60e40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getClipedOnlyMask(plane, x, y + 2) & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y + 2) & 0x78240000) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getClipedOnlyMask(plane, x - 1, y) & 0x4fa40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x63e40000) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getClipedOnlyMask(plane, x + 1, y - 1) & 0x63e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y - 1) & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y) & 0x78e40000) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4fa40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 2) & 0x7e240000) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getClipedOnlyMask(plane, x + 1, y + 2) & 0x7e240000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y + 2) & 0x78240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y + 1) & 0x78e40000) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) != 0
						|| (getClipedOnlyMask(plane, x - 1, -1 + (y + size)) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x + size, y) & 0x60e40000) != 0
						|| (getClipedOnlyMask(plane, x + size, y - (-size + 1)) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) != 0
						|| (getClipedOnlyMask(plane, x + size - 1, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x, y + size) & 0x4e240000) != 0
						|| (getClipedOnlyMask(plane, x + (size - 1), y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + (-1 + sizeOffset)) & 0x4fa40000) != 0
							|| (getClipedOnlyMask(plane, sizeOffset - 1 + x,
									y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x + size, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + size, sizeOffset
							+ (-1 + y)) & 0x78e40000) != 0
							|| (getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x - 1, y + size) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0
							|| (getClipedOnlyMask(plane, -1 + (x + sizeOffset),
									y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x + size, y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0
							|| (getClipedOnlyMask(plane, x + size, y
									+ sizeOffset) & 0x78e40000) != 0)
						return false;
			}
		}
		return true;
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int dir,
			int size) {
		return checkWalkStep(plane, x, y, Utils.DIRECTION_DELTA_X[dir],
				Utils.DIRECTION_DELTA_Y[dir], size);
	}

	public static final boolean checkWalkStep(int plane, int x, int y,
			int xOffset, int yOffset, int size) {
		if (size == 1) {
			int mask = getMask(plane, x + xOffset, y + yOffset);
			if (xOffset == -1 && yOffset == 0)
				return (mask & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0
						&& (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0
						&& (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 2, y + 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
						| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
								| Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
						| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x + 2, y - 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH
								| Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
						| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
								| Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
						| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 2, y + 2) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 1, y + 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_NORTH
								| Flags.WALLOBJ_SOUTH
								| Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0
						|| (getMask(plane, x - 1, -1 + (y + size)) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ
							| Flags.WALLOBJ_NORTH
							| Flags.WALLOBJ_EAST
							| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getMask(plane, x + size, y) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0
						|| (getMask(plane, x + size, y - (-size + 1)) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ
							| Flags.WALLOBJ_NORTH
							| Flags.WALLOBJ_SOUTH
							| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0
						|| (getMask(plane, x + size - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ
							| Flags.WALLOBJ_NORTH
							| Flags.WALLOBJ_EAST
							| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getMask(plane, x, y + size) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0
						|| (getMask(plane, x + (size - 1), y + size) & (Flags.FLOOR_BLOCKSWALK
								| Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ
								| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ
							| Flags.WALLOBJ_EAST
							| Flags.WALLOBJ_SOUTH
							| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + (-1 + sizeOffset)) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ
							| Flags.WALLOBJ_NORTH
							| Flags.WALLOBJ_EAST
							| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
							|| (getMask(plane, sizeOffset - 1 + x, y - 1) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK
									| Flags.OBJ
									| Flags.WALLOBJ_NORTH
									| Flags.WALLOBJ_EAST
									| Flags.WALLOBJ_WEST
									| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getMask(plane, x + size, y - 1) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + size, sizeOffset + (-1 + y)) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ
							| Flags.WALLOBJ_NORTH
							| Flags.WALLOBJ_SOUTH
							| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0
							|| (getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK
									| Flags.OBJ
									| Flags.WALLOBJ_NORTH
									| Flags.WALLOBJ_EAST
									| Flags.WALLOBJ_WEST
									| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getMask(plane, x - 1, y + size) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ
							| Flags.WALLOBJ_NORTH
							| Flags.WALLOBJ_EAST
							| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
							|| (getMask(plane, -1 + (x + sizeOffset), y + size) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK
									| Flags.OBJ
									| Flags.WALLOBJ_EAST
									| Flags.WALLOBJ_SOUTH
									| Flags.WALLOBJ_WEST
									| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getMask(plane, x + size, y + size) & (Flags.FLOOR_BLOCKSWALK
						| Flags.FLOORDECO_BLOCKSWALK
						| Flags.OBJ
						| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ
							| Flags.WALLOBJ_EAST
							| Flags.WALLOBJ_SOUTH
							| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0
							|| (getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK
									| Flags.OBJ
									| Flags.WALLOBJ_NORTH
									| Flags.WALLOBJ_SOUTH
									| Flags.WALLOBJ_WEST
									| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			}
		}
		return true;
	}

	public static final boolean containsPlayer(String username) {
		for (Player p2 : players) {
			if (p2 == null)
				continue;
			if (p2.getUsername().equals(username))
				return true;
		}
		return false;
	}

	public static final boolean containsLobbyPlayer(String username) {
		synchronized (lobbyPlayers) {
			for (Player p2 : lobbyPlayers) {
				if (p2 == null)
					continue;
				if (p2.getUsername().equals(username))
					return true;
			}
			return false;
		}
	}

	public static Player getAllPlayersByUsername(String username) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equalsIgnoreCase(username))
				return player;
		}
		synchronized (lobbyPlayers) {
			for (Player lPlayers : lobbyPlayers) {
				if (lPlayers == null)
					continue;
				if (lPlayers.getUsername().equals(username))
					return lPlayers;
			}
		}
		return null;
	}

	public static Player getPlayer(String username) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equals(username))
				return player;
		}
		return null;
	}

	public static Player getLobbyPlayer(String username) {
		synchronized (lobbyPlayers) {
			for (Player player : lobbyPlayers) {
				if (player == null)
					continue;
				if (player.getUsername().equals(username))
					return player;
			}
			return null;
		}
	}

	public static final Player getPlayerByDisplayName(String username) {
		String formatedUsername = Utils.formatPlayerNameForDisplay(username);
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equalsIgnoreCase(formatedUsername)
					|| player.getDisplayName().equalsIgnoreCase(
							formatedUsername))
				return player;
		}
		return null;
	}

	public static final Player getPlayerByDisplayNameAll(String username) {
		String formatedUsername = Utils.formatPlayerNameForDisplay(username);
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equalsIgnoreCase(formatedUsername)
					|| player.getDisplayName().equalsIgnoreCase(
							formatedUsername))
				return player;
		}
		synchronized (lobbyPlayers) {
			for (Player player : lobbyPlayers) {
				if (player == null)
					continue;
				if (player.getUsername().equalsIgnoreCase(formatedUsername)
						|| player.getDisplayName().equalsIgnoreCase(
								formatedUsername))
					return player;
			}
		}
		return null;
	}

	public static final EntityList<Player> getPlayers() {
		return players;
	}

	public static int getSize() {
		return players.size();
	}

	public static double getPlayerMultiplier() {
		return World.getSize() * 10 / 150;
	}

	public static final List<Player> getLobbyPlayers() {
		synchronized (lobbyPlayers) {
			return new ArrayList<Player>(lobbyPlayers);
		}
	}

	public static final EntityList<NPC> getNPCs() {
		return npcs;
	}

	private World() {

	}

	public static final WorldObject getObject(WorldTile tile) {
		return getRegion(tile.getRegionId()).getStandartObject(tile.getPlane(),
				tile.getXInRegion(), tile.getYInRegion());
	}

	public static final WorldObject getObject(WorldTile tile, int type) {
		return getRegion(tile.getRegionId()).getObjectWithType(tile.getPlane(),
				tile.getXInRegion(), tile.getYInRegion(), type);
	}

	public static final void spawnTemporaryObject(final WorldObject object,
			long time) {
		spawnTemporaryObject(object, time, false);
	}

	public static final boolean removeTemporaryObject(final WorldObject object,
			long time, final boolean clip) {
		removeObject(object);
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final void spawnTemporaryObject(final WorldObject object,
			long time, final boolean clip) {
		spawnObject(object);
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final void spawnTemporaryDivineObject(
			final WorldObject object, long time, final Player player) {
		spawnTemporaryDivineObject(object, time, player, false);
	}

	public static final void spawnTemporarPortableObject(
			final WorldObject object, long time, final Player player) {
		spawnTemporaryDivineObject(object, time, player, false);
	}

	public static final void spawnTemporaryDivineObject(
			final WorldObject object, long time, final Player player,
			final boolean clip) {
		spawnObject(object);
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
					player.divine = 0;
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final boolean isSpawnedObject(WorldObject object) {
		return getRegion(object.getRegionId()).getSpawnedObjects().contains(
				object);
	}

	public static final void spawnObject(WorldObject object) {
		getRegion(object.getRegionId()).spawnObject(object, object.getPlane(),
				object.getXInRegion(), object.getYInRegion(), false);
	}

	public static final void spawnObject(WorldObject object, boolean b) {
		getRegion(object.getRegionId()).spawnObject(object, object.getPlane(),
				object.getXInRegion(), object.getYInRegion(), false);
	}

	public static final void unclipTile(WorldTile tile) {
		getRegion(tile.getRegionId()).unclip(tile.getPlane(),
				tile.getXInRegion(), tile.getYInRegion());
	}

	public static final void removeObject(WorldObject object) {
		getRegion(object.getRegionId()).removeObject(object, object.getPlane(),
				object.getXInRegion(), object.getYInRegion());
	}

	public static final void spawnObjectTemporary(final WorldObject object,
			long time) {
		spawnObjectTemporary(object, time, false, false);
	}

	public static final void spawnObjectTemporary(final WorldObject object,
			long time, final boolean checkObjectInstance,
			boolean checkObjectBefore) {
		final WorldObject before = checkObjectBefore ? World.getObjectWithType(
				object, object.getType()) : null;
		spawnObject(object);
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (checkObjectInstance
							&& World.getObjectWithId(object, object.getId()) != object)
						return;
					if (before != null)
						spawnObject(before);
					else
						removeObject(object); // this method allows to remove
					// object with just tile and type
					// actualy so the removing object
					// may be diferent and still gets
					// removed
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final boolean removeObjectTemporary(final WorldObject object,
			long time) {
		removeObject(object);
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final void spawnTempGroundObject(final WorldObject object,
			final int replaceId, long time) {
		spawnObject(object);
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					removeObject(object);
					addGroundItem(new Item(replaceId), object, null, false, 180);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	public static final WorldObject getStandartObject(WorldTile tile) {
		return getRegion(tile.getRegionId()).getStandartObject(tile.getPlane(),
				tile.getXInRegion(), tile.getYInRegion());
	}

	public static final WorldObject getObjectWithType(WorldTile tile, int type) {
		return getRegion(tile.getRegionId()).getObjectWithType(tile.getPlane(),
				tile.getXInRegion(), tile.getYInRegion(), type);
	}

	public static final WorldObject getObjectWithSlot(WorldTile tile, int slot) {
		return getRegion(tile.getRegionId()).getObjectWithSlot(tile.getPlane(),
				tile.getXInRegion(), tile.getYInRegion(), slot);
	}

	public static final WorldObject getRealObject(WorldTile tile, int slot) {
		return getRegion(tile.getRegionId()).getRealObject(tile.getPlane(),
				tile.getXInRegion(), tile.getYInRegion(), slot);
	}

	public static final boolean containsObjectWithId(WorldTile tile, int id) {
		return getRegion(tile.getRegionId()).containsObjectWithId(
				tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(), id);
	}

	public static final WorldObject getObjectWithId(WorldTile tile, int id) {
		return getRegion(tile.getRegionId()).getObjectWithId(tile.getPlane(),
				tile.getXInRegion(), tile.getYInRegion(), id);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile) {
		// adds item, not invisible, no owner, no time to disapear
		addGroundItem(item, tile, null, false, -1, 2, -1);
	}

	public static final void addGroundItem(final Item item,
			final WorldTile tile, final Player owner/*
													 * null for default
													 */, boolean invisible,
			long hiddenTime/*
							 * default 3 minutes
							 */) {
		addGroundItem(item, tile, owner, invisible, hiddenTime, 2, 150);
	}

	public static final FloorItem addGroundItem(final Item item,
			final WorldTile tile, final Player owner/*
													 * null for default
													 */, boolean invisible,
			long hiddenTime/*
							 * default 3 minutes
							 */, int type) {
		return addGroundItem(item, tile, owner, invisible, hiddenTime, type,
				150);
	}

	public static final void turnPublic(FloorItem floorItem, int publicTime) {
		if (!floorItem.isInvisible())
			return;
		int regionId = floorItem.getTile().getRegionId();
		final Region region = getRegion(regionId);
		if (!region.getGroundItemsSafe().contains(floorItem))
			return;
		Player realOwner = floorItem.hasOwner() ? World.getPlayer(floorItem
				.getOwner()) : null;
		if (!ItemConstants.isTradeable(floorItem)) {
			region.getGroundItemsSafe().remove(floorItem);
			if (realOwner != null) {
				if (realOwner.getMapRegionsIds().contains(regionId))
					realOwner.getPackets().sendRemoveGroundItem(floorItem);
			}
			return;
		}
		floorItem.setInvisible(false);
		for (Player player : getPlayers()) {
			if (player == null || player == realOwner || !player.hasStarted()
					|| player.hasFinished()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendGroundItem(floorItem);
		}
		// disapears after this time
		if (publicTime != -1)
			removeGroundItem(floorItem, publicTime);
	}

	@Deprecated
	public static final void addGroundItemForever(Item item,
			final WorldTile tile) {
		int regionId = tile.getRegionId();
		final FloorItem floorItem = new FloorItem(item, tile, true);
		final Region region = getRegion(tile.getRegionId());
		region.getGroundItemsSafe().add(floorItem);
		for (Player player : getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendGroundItem(floorItem);
		}
	}

	/*
	 * type 0 - gold if not tradeable type 1 - gold if destroyable type 2 - no
	 * gold
	 */
	public static final FloorItem addGroundItem(final Item item,
			final WorldTile tile, final Player owner, boolean invisible,
			long hiddenTime/*
							 * default 3 minutes
							 */, int type, final int publicTime) {
		if (type != 2) {
			if ((type == 0 && !ItemConstants.isTradeable(item)) || type == 1
					&& ItemConstants.isDestroy(item)) {

				int price = item.getDefinitions().getPrice();
				if (price <= 0)
					return null;
				item.setId(995);
				item.setAmount(price);
			}
		}
		final FloorItem floorItem = new FloorItem(item, tile, owner,
				owner != null, invisible);
		final Region region = getRegion(tile.getRegionId());
		region.getGroundItemsSafe().add(floorItem);
		if (invisible) {
			if (owner != null)
				owner.getPackets().sendGroundItem(floorItem);
			// becomes visible after x time
			if (hiddenTime != -1) {
				GameExecutorManager.slowExecutor.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							turnPublic(floorItem, publicTime);
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, hiddenTime, TimeUnit.SECONDS);
			}
		} else {
			// visible
			int regionId = tile.getRegionId();
			for (Player player : getPlayers()) {
				if (player == null || !player.hasStarted()
						|| player.hasFinished()
						|| !player.getMapRegionsIds().contains(regionId))
					continue;
				player.getPackets().sendGroundItem(floorItem);
			}
			// disapears after this time
			if (publicTime != -1)
				removeGroundItem(floorItem, publicTime);
		}
		return floorItem;
	}

	public static final void updateGroundItem(Item item, final WorldTile tile,
			final Player owner) {
		final FloorItem floorItem = World.getRegion(tile.getRegionId())
				.getGroundItem(item.getId(), tile, owner);
		if (floorItem == null) {
			addGroundItem(item, tile, owner, true, 360);
			return;
		}
		floorItem.setAmount(floorItem.getAmount() + item.getAmount());
		owner.getPackets().sendRemoveGroundItem(floorItem);
		owner.getPackets().sendGroundItem(floorItem);

	}

	private static final void removeGroundItem(final FloorItem floorItem,
			long publicTime) {
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					int regionId = floorItem.getTile().getRegionId();
					Region region = getRegion(regionId);
					if (!region.getGroundItemsSafe().contains(floorItem))
						return;
					region.getGroundItemsSafe().remove(floorItem);
					for (Player player : World.getPlayers()) {
						if (player == null
								|| !player.hasStarted()
								|| player.hasFinished()
								|| !player.getMapRegionsIds()
										.contains(regionId))
							continue;
						player.getPackets().sendRemoveGroundItem(floorItem);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, publicTime, TimeUnit.SECONDS);
	}

	public static final boolean removeGroundItem(Player player,
			FloorItem floorItem) {
		return removeGroundItem(player, floorItem, true);
	}

	/*
	 * used for dung
	 */
	public static final boolean removeGroundItem(final FloorItem floorItem) {
		int regionId = floorItem.getTile().getRegionId();
		Region region = getRegion(regionId);
		if (!region.getGroundItemsSafe().contains(floorItem))
			return false;
		region.getGroundItemsSafe().remove(floorItem);
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendRemoveGroundItem(floorItem);
		}
		return true;
	}

	public static final boolean removeGroundItem(Player player,
			final FloorItem floorItem, boolean add) {
		int regionId = floorItem.getTile().getRegionId();
		Region region = getRegion(regionId);
		if (!region.getGroundItemsSafe().contains(floorItem))
			return false;
		if (add
				&& (player.getInventory().getFreeSlots() == 0 && (!floorItem
						.getDefinitions().isStackable() || !player
						.getInventory().containsItem(floorItem.getId(), 1)))) {
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return false;
		}
		region.getGroundItemsSafe().remove(floorItem);
		if (add)
			player.getInventory().addItemMoneyPouch(
					new Item(floorItem.getId(), floorItem.getAmount()));
		if (floorItem.isInvisible()) {
			player.getPackets().sendRemoveGroundItem(floorItem);
			return true;
		} else {
			for (Player p2 : World.getPlayers()) {
				if (p2 == null || !p2.hasStarted() || p2.hasFinished()
						|| !p2.getMapRegionsIds().contains(regionId))
					continue;
				p2.getPackets().sendRemoveGroundItem(floorItem);
			}
			if (floorItem.isForever()) {
				GameExecutorManager.slowExecutor.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							addGroundItemForever(floorItem, floorItem.getTile());
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, 60, TimeUnit.SECONDS);
			}
			return true;
		}
	}

	public static final void sendObjectAnimation(WorldObject object,
			Animation animation) {
		sendObjectAnimation(null, object, animation);
	}

	public static final void sendObjectAnimation(Entity creator,
			WorldObject object, Animation animation) {
		if (creator == null) {
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted()
						|| player.hasFinished()
						|| !player.withinDistance(object))
					continue;
				player.getPackets().sendObjectAnimation(object, animation);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId)
						.getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || !player.hasStarted()
							|| player.hasFinished()
							|| !player.withinDistance(object))
						continue;
					player.getPackets().sendObjectAnimation(object, animation);
				}
			}
		}
	}

	public static final void sendGraphics(Entity creator, Graphics graphics,
			WorldTile tile) {
		if (creator == null) {
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted()
						|| player.hasFinished() || !player.withinDistance(tile))
					continue;
				player.getPackets().sendGraphics(graphics, tile);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId)
						.getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || !player.hasStarted()
							|| player.hasFinished()
							|| !player.withinDistance(tile))
						continue;
					player.getPackets().sendGraphics(graphics, tile);
				}
			}
		}
	}

	public static final Projectile sendProjectileNew(WorldTile from,
			WorldTile to, int graphicId, int startHeight, int endHeight,
			int startTime, double speed, int angle, int slope) {
		return sendProjectile(from, to, false, false, 0, graphicId,
				startHeight, endHeight, startTime, speed, angle, slope);
	}

	public static final Projectile sendProjectile(WorldTile from, WorldTile to,
			boolean adjustFlyingHeight, boolean adjustSenderHeight,
			int senderBodyPartId, int graphicId, int startHeight,
			int endHeight, int startTime, double speed, int angle, int slope) {
		int fromSizeX, fromSizeY;
		if (from instanceof Entity)
			fromSizeX = fromSizeY = ((Entity) from).getSize();
		else if (from instanceof WorldObject) {
			ObjectDefinitions defs = ((WorldObject) from).getDefinitions();
			fromSizeX = defs.getSizeX();
			fromSizeY = defs.getSizeY();
		} else
			fromSizeX = fromSizeY = 1;
		int toSizeX, toSizeY;
		if (to instanceof Entity)
			toSizeX = toSizeY = ((Entity) to).getSize();
		else if (to instanceof WorldObject) {
			ObjectDefinitions defs = ((WorldObject) to).getDefinitions();
			toSizeX = defs.getSizeX();
			toSizeY = defs.getSizeY();
		} else
			toSizeX = toSizeY = 1;

		Projectile projectile = new Projectile(
				from,
				to,
				adjustFlyingHeight,
				adjustSenderHeight,
				senderBodyPartId,
				graphicId,
				startHeight,
				endHeight,
				startTime,
				startTime
						+ (speed == -1 ? Utils.getProjectileTimeSoulsplit(from,
								fromSizeX, fromSizeY, to, toSizeX, toSizeY)
								: Utils.getProjectileTimeNew(from, fromSizeX,
										fromSizeY, to, toSizeX, toSizeY, speed)),
				slope, angle);
		getRegion(from.getRegionId()).addProjectile(projectile);
		return projectile;
	}

	public static final Projectile sendProjectile(Entity shooter,
			WorldTile startTile, WorldTile receiver, int gfxId,
			int startHeight, int endHeight, int speed, int startTime,
			int curve, int startDistanceOffset) {
		return sendProjectileNew(shooter, receiver, gfxId, startHeight,
				endHeight, startTime, Math.round(speed / 10), curve,
				Utils.random(5));
	}

	public static final Projectile sendProjectile(WorldTile shooter,
			Entity receiver, int gfxId, int startHeight, int endHeight,
			int speed, int startTime, int curve, int startDistanceOffset) {
		return sendProjectileNew(shooter, receiver, gfxId, startHeight,
				endHeight, startTime, Math.round(speed / 10), curve,
				Utils.random(5));
	}

	public static final Projectile sendProjectile(Entity shooter,
			WorldTile receiver, int gfxId, int startHeight, int endHeight,
			int speed, int startTime, int curve, int startDistanceOffset) {
		return sendProjectileNew(shooter, receiver, gfxId, startHeight,
				endHeight, startTime, Math.round(speed / 10), curve,
				Utils.random(5));
	}

	public static final Projectile sendProjectile(Entity shooter,
			Entity receiver, int gfxId, int startHeight, int endHeight,
			int speed, int startTime, int curve, int startDistanceOffset) {
		return sendProjectileNew(shooter, receiver, gfxId, startHeight,
				endHeight, startTime, Math.round(speed / 10), curve,
				Utils.random(5));
	}

	public static final boolean isPvpArea(WorldTile tile) {
		return Wilderness.isAtWild(tile);
	}

	public static void sendEngineMessage(String message) {
		for (Player p : World.getPlayers()) {
			p.getPackets().sendGameMessage(
					"<col=FF0000>[MaxScape830] " + Utils.fixChatMessage(message));
		}
	}

	public static void sendWorldMessage(String message, boolean forStaff) {
		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning() || p.isYellOff()
					|| (forStaff && p.getRights() == 0))
				continue;
			p.getPackets().sendGameMessage(message);
		}
	}

	public static final int WORLD_NEWS = 0, SERVER_NEWS = 1, FRIEND_NEWS = 2,
			GAME_NEWS = 3;

	public static void sendNews(String message, int type) {
		sendNews(null, message, type); // dont use type 2(FRIEND_NEWS) with this
		// one
	}

	/*
	 * 0 - all worlds 1 - just this world 2 - friend 3 - game news
	 */
	public static void sendNews(Player from, String message, int type) {
		String m = "<shad=000>News: " + message + "</shad></col>";
		if (type == 0)
			m = "<img=7><col=D80000>" + m;
		else if (type == 1)
			m = "<img=6><col=ba5409>" + m;
		else if (type == 2)
			m = "<img=5><col=079209>" + m;
		else if (type == 3)
			m = "<img=7><col=68b484>" + m;
		else if (type == 4)
			m = "<img=7><col=00c5ff>Lottery: " + message;
		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning() || (type == 2 && p != from))
				continue;
			p.getPackets().sendGameMessage(m, true);
		}
	}

	public static void sendWarbandsNews(String message, int type) {
		String m = "<shad=000>News: " + message + "</shad></col>";
		if (type == 0)
			m = "<img=7><col=D80000>" + m;
		else if (type == 1)
			m = "<img=6><col=ba5409>" + m;
		else if (type == 2)
			m = "<img=5><col=079209>" + m;
		else if (type == 3)
			m = "<img=7><col=68b484>" + m;
		else if (type == 4)
			m = "<img=7><col=00c5ff>Lottery: " + message;

		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning())
				continue;
			p.getPackets().sendGameMessage(m, true);
		}
	}

	public static void sendStaffMessage(String message) {
		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning() || p.getRights() < 1)
				continue;
			p.getPackets().sendGameMessage(message);
		}
	}

	public static void sendIgnoreableWorldMessage(Player sender,
			String message, boolean forStaff) {
		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning() || p.isYellOff()
					|| (forStaff && p.getRights() == 0)
					|| p.getFriendsIgnores().isIgnore(sender.getDisplayName()))
				continue;
			p.getPackets().sendGameMessage(message);
		}
	}

	public static void sendYellMessage(Player sender, String message) {
		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning() || p.isYellOff()
					|| p.getFriendsIgnores().isIgnore(sender.getDisplayName()))
				continue;
			p.getPackets().sendGameMessage(message);
		}
		System.out.println(message);
	}

	@SuppressWarnings("deprecation")
	public static final void sendProjectile(WorldObject object,
			WorldTile startTile, WorldTile endTile, int gfxId, int startHeight,
			int endHeight, int speed, int delay, int curve, int startOffset) {
		for (Player pl : getPlayers()) {
			if (pl == null || !pl.withinDistance(object, 20))
				continue;
			pl.getPackets()
					.sendProjectile(null, startTile, endTile, gfxId,
							startHeight, endHeight, speed, delay, curve,
							startOffset, 1);
		}
	}

	public static void executeAfterLoadRegion(final int regionId,
			final Runnable event) {
		executeAfterLoadRegion(regionId, 0, event);
	}

	public static void executeAfterLoadRegion(final int regionId,
			long startTime, final Runnable event) {
		executeAfterLoadRegion(regionId, startTime, 10000, event);
	}

	public static void executeAfterLoadRegion(final int fromRegionX,
			final int fromRegionY, final int toRegionX, final int toRegionY,
			long startTime, final long expireTime, final Runnable event) {
		final long start = Utils.currentTimeMillis();
		for (int x = fromRegionX; x <= toRegionX; x++) {
			for (int y = fromRegionY; y <= toRegionY; y++) {
				int regionId = MapUtils.encode(Structure.REGION, x, y);
				World.getRegion(regionId, true); // forces check load if not
				// loaded
			}
		}
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					for (int x = fromRegionX; x <= toRegionX; x++) {
						for (int y = fromRegionY; y <= toRegionY; y++) {
							int regionId = MapUtils.encode(Structure.REGION, x,
									y);
							if (!World.isRegionLoaded(regionId)
									&& Utils.currentTimeMillis() - start < expireTime)
								return;
						}
					}
					event.run();
					cancel();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, startTime, 600);
	}

	/*
	 * TODO make this use code from above to save lines lo, they do same
	 */
	public static void executeAfterLoadRegion(final int regionId,
			long startTime, final long expireTime, final Runnable event) {
		final long start = Utils.currentTimeMillis();
		World.getRegion(regionId, true); // forces check load if not loaded
		try {
			GameExecutorManager.fastExecutor.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						if (!World.isRegionLoaded(regionId)
								&& Utils.currentTimeMillis() - start < expireTime)
							return;
						event.run();
						cancel();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}

			}, startTime, 600);
		} catch (IllegalStateException e) {
			Logger.log("World", "Caught timer exception.");
		}
	}

	public static boolean isSafeZone(WorldTile tile) {
		return (tile.getY() >= 3470 && tile.getY() <= 3513
				&& tile.getX() >= 3143 && tile.getX() <= 3186)// Grand
				// Exchange
				|| (tile.getX() >= 2801 && tile.getY() >= 3325
						&& tile.getX() <= 2875 && tile.getY() <= 3395) // Entrana
				|| (tile.getX() >= 3044 && tile.getY() >= 3482
						&& tile.getX() <= 3059 && tile.getY() <= 3498 && tile
						.getPlane() == 1) // Top level of Monastery
				|| (tile.getX() >= 3201 && tile.getY() >= 3201
						&& tile.getX() <= 3229 && tile.getY() <= 3236) // Lumbridge
				// Castle
				|| (tile.getX() >= 2952 && tile.getY() >= 3324
						&& tile.getX() <= 3000 && tile.getY() <= 3354) // Falador
				// Castle
				|| (tile.getX() >= 3091 && tile.getY() >= 3488
						&& tile.getX() <= 3098 && tile.getY() <= 3499) // Edgeville
				// Bank
				|| (tile.getX() >= 3179 && tile.getY() >= 3432
						&& tile.getX() <= 3194 && tile.getY() <= 3446) // Varrock
				// West
				// Bank
				|| (tile.getX() >= 3250 && tile.getY() >= 3416
						&& tile.getX() <= 3257 && tile.getY() <= 3423) // Varrock
				// East
				// Bank
				|| (tile.getX() >= 2943 && tile.getY() >= 3368
						&& tile.getX() <= 2949 && tile.getY() <= 3369) // Falador
				// West
				// Bank
				// Part1
				|| (tile.getX() >= 2943 && tile.getY() >= 3370
						&& tile.getX() <= 2947 && tile.getY() <= 3373) // Falador
				// West
				// Bank
				// Part2
				|| (tile.getX() >= 3009 && tile.getY() >= 3353
						&& tile.getX() <= 3018 && tile.getY() <= 3358) // Falador
				// East
				// Bank
				|| (tile.getX() >= 3088 && tile.getY() >= 3240
						&& tile.getX() <= 3097 && tile.getY() <= 3246) // Draynor
				// Village
				// Bank
				|| (tile.getX() >= 3268 && tile.getY() >= 3161
						&& tile.getX() <= 3272 && tile.getY() <= 3171) // AlKharid
				// Bank
				|| (tile.getX() >= 3300 && tile.getY() >= 3118
						&& tile.getX() <= 3310 && tile.getY() <= 3132) // Shanty
				// Pass
				// Bank
				|| (tile.getX() >= 3508 && tile.getY() >= 3474
						&& tile.getX() <= 3516 && tile.getY() <= 3483) // Canafis
				// Bank
				|| (tile.getX() >= 3686 && tile.getY() >= 3461
						&& tile.getX() <= 3699 && tile.getY() <= 3471) // Port
				// Phanta
				// Bank
				|| (tile.getX() >= 2609 && tile.getY() >= 3088
						&& tile.getX() <= 2616 && tile.getY() <= 3097) // Yannile
				// Bank
				|| (tile.getX() >= 2437 && tile.getY() >= 3082
						&& tile.getX() <= 2447 && tile.getY() <= 3098) // Castle
				// Wars
				// Bank
				|| (tile.getX() >= 1853 && tile.getY() >= 3145
						&& tile.getX() <= 1926 && tile.getY() <= 3191) // Soul
				// Wars
				|| (tile.getX() >= 2872 && tile.getY() >= 3414
						&& tile.getX() <= 2878 && tile.getY() <= 3420) // Taverly
				// bank
				|| (tile.getX() >= 2806 && tile.getY() >= 3438
						&& tile.getX() <= 2812 && tile.getY() <= 3445) // Catherbay
				// bank
				|| (tile.getX() >= 2721 && tile.getY() >= 3490
						&& tile.getX() <= 2730 && tile.getY() <= 3497) // Seers
				// Village
				// bank
				// Part1
				|| (tile.getX() >= 2724 && tile.getY() >= 3487
						&& tile.getX() <= 2727 && tile.getY() <= 3489) // Seers
				// Village
				// bank
				// Part2
				|| (tile.getX() >= 2612 && tile.getY() >= 3330
						&& tile.getX() <= 2621 && tile.getY() <= 3335) // Ardougne
				// West
				// Bank
				|| (tile.getX() >= 2649 && tile.getY() >= 3280
						&& tile.getX() <= 2658 && tile.getY() <= 3287) // Ardougne
				// East
				// Bank
				|| (tile.getX() >= 2334 && tile.getY() >= 3805
						&& tile.getX() <= 2339 && tile.getY() <= 3808) // Neitiznot
				// Bank
				|| (tile.getX() >= 2414 && tile.getY() >= 3801
						&& tile.getX() <= 2418 && tile.getY() <= 3803) // Jatizso
				// Bank
				|| (tile.getX() == 2619 && tile.getY() >= 3893 && tile.getY() <= 3896) // Etceteria
				// Bank
				|| (tile.getX() >= 2097 && tile.getY() >= 3917
						&& tile.getX() <= 2104 && tile.getY() <= 3921) // Lunar
				// Isle
				// Bank
				|| (tile.getX() >= 2528 && tile.getY() >= 4710
						&& tile.getX() <= 2549 && tile.getY() <= 4723) // Mage
				// Bank
				|| (tile.getRegionId() == 10658)
				|| (tile.getRegionId() == 11927)
				|| (tile.getRegionId() == 13625 || tile.getRegionId() == 13626)
				|| (tile.getX() >= 2952 && tile.getY() >= 3220
						&& tile.getX() <= 2959 && tile.getY() <= 3227) // ridmigton
		// poh
		;
	}

	public static boolean canMoveNPC(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (getMask(plane, tileX, tileY) != 0)
					return false;
		return true;
	}

	public static final boolean isMultiArea(WorldTile tile) {
		int destX = tile.getX();
		int destY = tile.getY();
		return (destX >= 3462 && destX <= 3511 && destY >= 9481
				&& destY <= 9521 && tile.getPlane() == 0) // kalphite
															// queen
															// lair
				|| (destX >= 4540 && destX <= 4799 && destY >= 5052
						&& destY <= 5183 && tile.getPlane() == 0) // thzaar
																	// city
				|| (destX >= 1721 && destX <= 1791 && destY >= 5123 && destY <= 5249) // mole
				|| (destX >= 4493 && destX <= 4531 && destY >= 5508 && destY <= 5625) // pest
																						// queen
				|| (destX >= 3664 && destX <= 3695 && destY >= 6035
						&& destY <= 6063 && tile.getPlane() == 2) // sunfreet
				|| (destX >= 3029 && destX <= 3374 && destY >= 3759 && destY <= 3903)// wild
				|| (destX >= 2250 && destX <= 2280 && destY >= 4670 && destY <= 4720)
				|| (destX >= 2301 && destX <= 2368 && destY >= 5484 && destY <= 5506)// dung
				// || (destX >= 2693 && destX <= 2710 && destY >= 9557 && destY
				// <= 9569)//Blink
				|| (destX >= 3198 && destX <= 3380 && destY >= 3904 && destY <= 3970)
				|| (destX >= 3191 && destX <= 3326 && destY >= 3510 && destY <= 3759)
				|| (destX >= 2987 && destX <= 3006 && destY >= 3912 && destY <= 3937)
				|| (destX >= 2245 && destX <= 2295 && destY >= 4675 && destY <= 4720)
				// || (destX >= 2450 && destX <= 3520 && destY >= 9450 && destY
				// <= 9550)
				|| (destX >= 3006 && destX <= 3071 && destY >= 3602 && destY <= 3710)
				|| (destX >= 1790 && destX <= 1987 && destY >= 3194 && destY <= 3273)// soul
																						// wars
				|| (tile.getX() >= 2893 && tile.getX() <= 2931
						&& tile.getY() >= 4433 && tile.getY() <= 4467)// daggs
				|| (destX >= 3134 && destX <= 3192 && destY >= 3519 && destY <= 3646)
				|| (destX >= 2815 && destX <= 2966 && destY >= 5240 && destY <= 5375)// wild
				|| (destX >= 2840 && destX <= 2950 && destY >= 5190 && destY <= 5230) // godwars
				|| (destX >= 3547 && destX <= 3555 && destY >= 9690 && destY <= 9699) // zaros
				|| (destX >= 3180 && destX <= 3192 && destY >= 4612 && destY <= 4626)
				// rock crabs
				|| (destX >= 2689 && destX <= 2728 && destY >= 3709 && destY <= 3731)
				// vorago
				|| (destX >= 3204 && destX <= 3253 && destY >= 9230
						&& destY <= 9275 && tile.getPlane() == 0)
				// charms place
				|| (destX >= 3136 && destX <= 3155 && destY >= 5441 && destY <= 5463)
				|| (destX >= 2979 && destX <= 3009 && destY >= 3106 && destY <= 3131)
				// godwars
				|| TormentedDemon.atTD(tile) // Tormented demon's area
				|| EvilChicken.distanceOf(tile) // evil chicken
				|| (destX >= 2970 && destX <= 3000 && destY >= 4365 && destY <= 4400) // corp
				|| (destX >= 3195 && destX <= 3327 && destY >= 3520 && destY <= 3970)
				|| (destX >= 2376 && 5127 >= destY && destX <= 2422 && destY <= 5168)
				|| (destX >= 2374 && destY >= 5129 && destX <= 2424 && destY <= 5168) // pits
				|| (destX >= 2622 && destY >= 5696 && destX <= 2573 && destY <= 5752) // torms
				|| (destX >= 2368 && destY >= 3072 && destX <= 2431 && destY <= 3135) // castlewars
				// out
				|| (destX >= 2365 && destY >= 9470 && destX <= 2436 && destY <= 9532) // castlewars
				|| (destX >= 2948 && destY >= 5537 && destX <= 3071 && destY <= 5631) // Risk
				// ffa.
				|| (destX >= 2756 && destY >= 5537 && destX <= 2879 && destY <= 5631) // Safe
																						// ffa
				// glacors
				|| (destX >= 4153 && destY >= 5690 && destX <= 4246 && destY <= 5778)
				|| (tile.getX() >= 3011 && tile.getX() <= 3132
						&& tile.getY() >= 10052 && tile.getY() <= 10175)
				// && (tile.getY() >= 10066 || tile.getX() >= 3094))//not closed
				// off so invalid //fortihrny dungeon
				|| (destX >= 1480 && destY >= 4688 && destX <= 1525 && destY <= 4720) // chaos
																						// dwarf
																						// battlefield
				|| (destX >= 976 && destX <= 991 && destY >= 1104 && destY <= 1118)// new
																					// blink
				|| (destX >= 2900 && destX <= 2941 && destY >= 3779 && destY <= 3836)// lucien
				|| (destX >= 2815 && destX <= 2944 && destY >= 3829 && destY <= 3947) // lucien
																						// new
				|| (destX >= 3156 && destX <= 3182 && destY >= 9760 && destY <= 9772)// new
																						// lucien
				|| (destX >= 3774 && destX <= 3852 && destY >= 2814 && destY <= 2887)
				|| (destX >= 3518 && destX <= 3557 && destY >= 5183 && destY <= 5216)// phoenix
		;
		// in

		// multi
	}

	public static final boolean isSinkArea(WorldTile tile) {
		final int destX = tile.getX();
		final int destY = tile.getY();
		return ((destX <= 1613 && destX >= 1534 && destY <= 4425 && destY >= 4346));
	}

	public static final void spawnBurningWeb(final WorldObject object, long time) {
		spawnTemporaryObject(object, time, false);
	}

	public static void soulWars() {
		SoulLobby.minutes = 5;
		SoulWars.startedGame = false;
		SoulLobby.redWait.clear();
		SoulLobby.blueWait.clear();
		SoulLobby.allWaiting.clear();
		SoulWars.playing.clear();
	}
}