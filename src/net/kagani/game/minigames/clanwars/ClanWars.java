package net.kagani.game.minigames.clanwars;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.map.MapUtils;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.ViewingOrb;
import net.kagani.game.player.content.FriendsChat;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;

/**
 * Handles the clan wars activity.
 * 
 * @author Emperor
 * 
 */
public final class ClanWars implements Serializable {

	/**
	 * The list of currently active clan wars.
	 */
	private static final List<ClanWars> currentWars = new CopyOnWriteArrayList<ClanWars>();

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 3329643510646371055L;

	/**
	 * The possible rules.
	 * 
	 * @author Emperor
	 * 
	 */
	public static enum Rules {
		NO_FOOD(5288), NO_POTIONS(5289), NO_PRAYER(5290), NO_MAGIC(-1), NO_MELEE(
				5284), NO_RANGE(5285), NO_FAMILIARS(5287), ITEMS_LOST(5283);

		/**
		 * The config id.
		 */
		private final int configId;

		/**
		 * Constructs a new {@code Rules} {@code Object}.
		 * 
		 * @param configId
		 *            The config id.
		 */
		private Rules(int configId) {
			this.configId = configId;
		}
	}

	/**
	 * The first team.
	 */
	private transient final FriendsChat firstTeam;

	/**
	 * The second team.
	 */
	private transient final FriendsChat secondTeam;

	/**
	 * The list of players ingame, of the first team.
	 */
	private transient final List<Player> firstPlayers = new CopyOnWriteArrayList<Player>();

	/**
	 * The list of players ingame, of the second team.
	 */
	private transient final List<Player> secondPlayers = new CopyOnWriteArrayList<Player>();

	/**
	 * The wall objects list.
	 */
	private transient List<WorldObject> wallObjects;

	/**
	 * The victory type for this war.
	 */
	private transient int victoryType = -1;

	/**
	 * The amount of time left.
	 */
	private transient int timeLeft = -1;

	/**
	 * The current magic rule's counter.
	 */
	private transient int magicRuleCount;

	/**
	 * The current area type.
	 */
	private transient AreaType areaType = AreaType.CLASSIC_AREA;

	/**
	 * A bit set containing the rules which have been activated.
	 */
	private transient final BitSet rules = new BitSet();

	/**
	 * The base location used during this war.
	 */
	private transient WorldTile baseLocation;

	/**
	 * The current clan wars timer instance.
	 */
	private transient ClanWarsTimer timer;

	/**
	 * The amount of kills done.
	 */
	private transient int kills = 0;

	private transient WorldTile[] views;

	private boolean ended;

	/**
	 * Constructs a new {@code ClanWars} {@code Object}.
	 * 
	 * @param first
	 *            The first team.
	 * @param second
	 *            The second team.
	 */
	public ClanWars(FriendsChat first, FriendsChat second) {
		this.firstTeam = first;
		this.secondTeam = second;
	}

	/**
	 * Flags a rule if the rule was previously inactivated, unflags the rule if
	 * the rule was previously activated.
	 * 
	 * @param rule
	 *            The rule to switch.
	 * @param player
	 *            The player switching the rule.
	 */
	public void switchRule(Rules rule, Player player) {
		Player other = (Player) player.getTemporaryAttributtes().get(
				"clan_request_p");
		if (other == null
				|| player.getTemporaryAttributtes().get("clan_wars") != other
						.getTemporaryAttributtes().get("clan_wars")) {
			return;
		}
		if (rule == Rules.NO_MAGIC) {
			if (get(Rules.NO_RANGE) && get(Rules.NO_MELEE)) {
				player.getPackets()
						.sendGameMessage(
								"You can't activate all combat style rules, how would you fight?");
				return;
			} else {
				magicRuleCount = ++magicRuleCount % 4;
			}
			sendConfig(player, other, 5286, magicRuleCount);
			return;
		}
		if (magicRuleCount != 0
				&& ((rule == Rules.NO_MELEE && get(Rules.NO_RANGE)) || (rule == Rules.NO_RANGE && get(Rules.NO_MELEE)))) {
			player.getPackets()
					.sendGameMessage(
							"You can't activate all combat style rules, how would you fight?");
		} else {
			rules.set(rule.ordinal(), !rules.get(rule.ordinal()));
		}
		sendConfig(player, other, rule.configId, rules.get(rule.ordinal()) ? 1
				: 0);
	}

	/**
	 * Sends a config to both the players.
	 * 
	 * @param player
	 *            The first player.
	 * @param other
	 *            The other player.
	 * @param configId
	 *            The config id.
	 * @param value
	 *            The value.
	 */
	public static void sendConfig(Player player, Player other, int configId,
			int value) {
		boolean resetAccept = false;
		if (player.getTemporaryAttributtes().get("accepted_war_terms") == Boolean.TRUE) {
			player.getTemporaryAttributtes().remove("accepted_war_terms");
			resetAccept = true;
		}
		if (other.getTemporaryAttributtes().get("accepted_war_terms") == Boolean.TRUE) {
			other.getTemporaryAttributtes().remove("accepted_war_terms");
			resetAccept = true;
		}
		if (resetAccept) {
			player.getVarsManager().sendVarBit(5293, 0);
			other.getVarsManager().sendVarBit(5293, 0);
		}
		player.getVarsManager().sendVarBit(configId, value);
		other.getVarsManager().sendVarBit(configId, value);
	}

	public static boolean hasMemberItems(Player player) {
		List<Item> collective = new LinkedList<Item>();
		Collections.addAll(collective, player.getEquipment().getItems()
				.toArray());
		Collections.addAll(collective, player.getInventory().getItems()
				.toArray());
		for (Item item : collective) {
			if (item == null)
				continue;
			if (item.getDefinitions().isMembersOnly())
				return true;
		}
		return false;
	}

	/**
	 * Sends the victory type configuration.
	 * 
	 * @param p
	 *            The player.
	 */
	private void sendVictoryConfiguration(Player p) {
		switch (victoryType) {
		case -1:
			p.getVarsManager().sendVarBit(5280, 0);
			break;
		case 25:
			p.getVarsManager().sendVarBit(5280, 1);
			break;
		case 50:
			p.getVarsManager().sendVarBit(5280, 2);
			break;
		case 100:
			p.getVarsManager().sendVarBit(5280, 3);
			break;
		case 200:
			p.getVarsManager().sendVarBit(5280, 4);
			break;
		case 400:
			p.getVarsManager().sendVarBit(5280, 5);
			break;
		case 750:
			p.getVarsManager().sendVarBit(5280, 6);
			break;
		case 1_000:
			p.getVarsManager().sendVarBit(5280, 7);
			break;
		case 2_500:
			p.getVarsManager().sendVarBit(5280, 8);
			break;
		case 5_000:
			p.getVarsManager().sendVarBit(5280, 9);
			break;
		case 10_000:
			p.getVarsManager().sendVarBit(5280, 10);
			break;
		case -2:
			p.getVarsManager().sendVarBit(5280, 15);
			break;
		}
	}

	/**
	 * Sends the time configuration.
	 * 
	 * @param p
	 *            The player.
	 */
	private void sendTimeConfiguration(Player p) {
		switch (timeLeft) {
		case 500:
			p.getVarsManager().sendVarBit(5281, 1);
			break;
		case 1_000:
			p.getVarsManager().sendVarBit(5281, 2);
			break;
		case 3_000:
			p.getVarsManager().sendVarBit(5281, 3);
			break;
		case 6_000:
			p.getVarsManager().sendVarBit(5281, 4);
			break;
		case 9_000:
			p.getVarsManager().sendVarBit(5281, 5);
			break;
		case 12_000:
			p.getVarsManager().sendVarBit(5281, 6);
			break;
		case 15_000:
			p.getVarsManager().sendVarBit(5281, 7);
			break;
		case 18_000:
			p.getVarsManager().sendVarBit(5281, 8);
			break;
		case 24_000:
			p.getVarsManager().sendVarBit(5281, 9);
			break;
		case 30_000:
			p.getVarsManager().sendVarBit(5281, 10);
			break;
		case 36_000:
			p.getVarsManager().sendVarBit(5281, 11);
			break;
		case 48_000:
			p.getVarsManager().sendVarBit(5281, 12);
			break;
		case -1:
			p.getVarsManager().sendVarBit(5281, 0);
			break;
		}
	}

	/**
	 * Checks if a rule has been activated.
	 * 
	 * @param rule
	 *            The rule to check.
	 * @return {@code True} if so.
	 */
	public boolean get(Rules rule) {
		return rules.get(rule.ordinal());
	}

	/**
	 * Gets the firstTeam.
	 * 
	 * @return The firstTeam.
	 */
	public FriendsChat getFirstTeam() {
		return firstTeam;
	}

	/**
	 * Gets the secondTeam.
	 * 
	 * @return The secondTeam.
	 */
	public FriendsChat getSecondTeam() {
		return secondTeam;
	}

	/**
	 * Sends the interface for challenge request.
	 * 
	 * @param p
	 *            The player to send to interface to.
	 * @param other
	 */
	public void sendInterface(final Player p, final Player other) {
		p.getTemporaryAttributtes().put("clan_wars", this);
		p.getInterfaceManager().sendCentralInterface(791);
		p.getPackets().sendUnlockIComponentOptionSlots(791, 141, 0, 63, 0);
		p.getVarsManager().sendVarBit(5291, 0);
		p.getVarsManager().sendVarBit(5292, 0);
		p.getVarsManager().sendVarBit(5293, 0);
		p.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				p.getTemporaryAttributtes().remove("accepted_war_terms");
				other.getTemporaryAttributtes().remove("accepted_war_terms");
				p.getTemporaryAttributtes().remove("clan_request_p");
				other.getTemporaryAttributtes().remove("clan_request_p");
				p.getTemporaryAttributtes().remove("clan_wars");
				other.getTemporaryAttributtes().remove("clan_wars");
				other.setCloseInterfacesEvent(null);
				other.closeInterfaces();
			}
		});
	}

	/**
	 * Called when the player accepts the challenge terms.
	 * 
	 * @param player
	 *            The player.
	 */
	public void accept(final Player player) {
		final Player other = (Player) player.getTemporaryAttributtes().get(
				"clan_request_p");
		if (other != null
				&& (Boolean) other.getTemporaryAttributtes().get(
						"accepted_war_terms") == Boolean.TRUE) {
			other.lock();
			player.lock();
			player.getTemporaryAttributtes().remove("accepted_war_terms");
			other.getTemporaryAttributtes().remove("accepted_war_terms");
			player.setCloseInterfacesEvent(null);
			other.setCloseInterfacesEvent(null);
			player.stopAll();
			other.stopAll();
			GameExecutorManager.slowExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						for (Player p : firstTeam.getLocalMembers()) {
							if (p != player && p != other) {
								p.getPackets()
										.sendGameMessage(
												"<col=FF0000>Your clan has been challenged to a clan war!</col>");
								p.getPackets()
										.sendGameMessage(
												"<col=FF0000>Step through the purple portal in the Challenge Hall.</col>");
								p.getPackets()
										.sendGameMessage(
												"<col=FF0000>Battle will commence in 2 minutes.</col>");
							}
						}
						for (Player p : secondTeam.getLocalMembers()) {
							if (p != player && p != other) {
								p.getPackets()
										.sendGameMessage(
												"<col=FF0000>Your clan has been challenged to a clan war!</col>");
								p.getPackets()
										.sendGameMessage(
												"<col=FF0000>Step through the purple portal in the Challenge Hall.</col>");
								p.getPackets()
										.sendGameMessage(
												"<col=FF0000>Battle will commence in 2 minutes.</col>");
							}
						}
						int width = (areaType.getNorthEastTile().getX() - areaType
								.getSouthWestTile().getX()) / 8 + 1;
						int height = (areaType.getNorthEastTile().getY() - areaType
								.getSouthWestTile().getY()) / 8 + 1;
						int[] newCoords = MapBuilder.findEmptyChunkBound(width,
								height);
						MapBuilder.copyAllPlanesMap(areaType.getSouthWestTile()
								.getChunkX(), areaType.getSouthWestTile()
								.getChunkY(), newCoords[0], newCoords[1],
								width, height);
						baseLocation = new WorldTile(newCoords[0] << 3,
								newCoords[1] << 3, 0);
						int[] regionPos = MapUtils.convert(
								MapUtils.Structure.CHUNK,
								MapUtils.Structure.REGION, newCoords);
						World.executeAfterLoadRegion(regionPos[0],
								regionPos[1], regionPos[0] + width / 8,
								regionPos[1] + height / 8, 0, 10000,
								new Runnable() {

									@Override
									public void run() {
										WallHandler.loadWall(ClanWars.this);
										firstTeam.setClanWars(ClanWars.this);
										secondTeam.setClanWars(ClanWars.this);
										GameExecutorManager.fastExecutor
												.scheduleAtFixedRate(
														timer = new ClanWarsTimer(
																ClanWars.this),
														600, 600);
										enter(player, player, false);
										enter(other, other, false);
										currentWars.add(ClanWars.this);
									}

								});
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}

			});
			return;
		}
		player.getTemporaryAttributtes().put("accepted_war_terms", true);
	}

	public static void enter(Player p, String target) {
		p.getTemporaryAttributtes().clear();
		Player p2 = World.getPlayerByDisplayName(target);
		if (p2 == null || p2.getCurrentFriendsChat() == null
				|| p2.getCurrentFriendsChat().getClanWars() == null) {
			p.getPackets().sendGameMessage("Couldn't find " + target + ".");
			return;
		}
		enter(p, p2, true);
	}

	/**
	 * Enters the purple portal.
	 * 
	 * @param p
	 *            The player.
	 */
	public static boolean enter(Player p, Player friend, boolean viewing) {
		boolean hasWar = friend.getCurrentFriendsChat() != null
				&& friend.getCurrentFriendsChat().getClanWars() != null;
		final ClanWars c = hasWar ? friend.getCurrentFriendsChat()
				.getClanWars() : null;
		if (c == null || c.timer == null || c.ended) {
			return false;
		}
		p.lock(3);
		c.sendVictoryConfiguration(p);
		c.sendTimeConfiguration(p);
		p.getPackets().sendCSVarInteger(271, hasWar ? 1 : 0);
		p.getInterfaceManager().sendMinigameInterface(265);
		if (!viewing && hasWar && c.timer.isStarted() && c.isKnockOut()) {
			viewing = true;
			p.getPackets().sendGameMessage("The war has already started!");
		}
		if (hasWar) {
			if (c.get(Rules.NO_FAMILIARS) && p.getFamiliar() != null) {
				p.getPackets().sendGameMessage(
						"You can't enter the clan war with a familiar.");
				return false;
			}
			if (c.get(Rules.NO_PRAYER)) {
				p.getPrayer().closeAllPrayers();
			}
			if (c.firstTeam == friend.getCurrentFriendsChat()) {
				if (viewing)
					p.setNextWorldTile(c.getBaseLocation().transform(
							c.getAreaType().getFirstDeathOffsetX(),
							c.getAreaType().getFirstDeathOffsetY(), 0));
				else
					p.setNextWorldTile(c.baseLocation.transform(
							c.areaType.getFirstSpawnOffsetX(),
							c.areaType.getFirstSpawnOffsetY(), 0));
				c.firstPlayers.add(p);
				c.timer.refresh(p, true);
			} else {
				WorldTile northEast = c.baseLocation.transform(c.areaType
						.getNorthEastTile().getX()
						- c.areaType.getSouthWestTile().getX(), c.areaType
						.getNorthEastTile().getY()
						- c.areaType.getSouthWestTile().getY(), 0);
				if (viewing)
					p.setNextWorldTile(northEast.transform(c.getAreaType()
							.getSecondDeathOffsetX(), c.getAreaType()
							.getSecondDeathOffsetY(), 0));
				else
					p.setNextWorldTile(northEast.transform(
							c.areaType.getSecondSpawnOffsetX(),
							c.areaType.getSecondSpawnOffsetY(), 0));
				c.secondPlayers.add(p);
				c.timer.refresh(p, false);
			}
			p.getControlerManager().startControler("clan_war", c);
			if (!viewing) {
				p.setCanPvp(true);
			}
			for (Player player : c.firstPlayers) {
				c.timer.refresh(player, true);
			}
			for (Player player : c.secondPlayers) {
				c.timer.refresh(player, false);
			}
			p.getPackets().sendMusicEffectOld(c.timer.getMusicEffect());
			return true;
		}
		return false;
	}

	public static final int LOGOUT = 2, TELEPORTED = 1, NORMAL = 1;
	public static final WorldTile OUTSIDE = new WorldTile(2992, 9676, 0);;

	/*
	 * 0 - leave normaly, 1 - teleported - 2 - leave logout
	 */
	public void leave(Player p, int type) {
		p.lock(3);
		firstPlayers.remove(p);
		secondPlayers.remove(p);
		p.stopAll();
		p.reset();
		if (type == LOGOUT) {
			p.setLocation(OUTSIDE);
		} else {
			p.setCanPvp(false);
			p.sendDefaultPlayersOptions();
			if (type == NORMAL)
				p.setNextWorldTile(OUTSIDE);
			p.getInterfaceManager().removeMinigameInterface();
		}

		p.getControlerManager().removeControlerWithoutCheck();
		p.getControlerManager().startControler("clan_wars_request");
		updateWar();
	}

	/*
	 * player may not be in
	 */
	public void leaveFC(Player p) {
		if (p.getControlerManager().getControler() instanceof WarControler
				&& p.isCanPvp()) {
			p.lock(3);
			if (firstPlayers.contains(p))
				p.setNextWorldTile(getBaseLocation().transform(
						getAreaType().getFirstDeathOffsetX(),
						getAreaType().getFirstDeathOffsetY(), 0));
			else {
				WorldTile northEast = getBaseLocation().transform(
						getAreaType().getNorthEastTile().getX()
								- getAreaType().getSouthWestTile().getX(),
						getAreaType().getNorthEastTile().getY()
								- getAreaType().getSouthWestTile().getY(), 0);
				p.setNextWorldTile(northEast.transform(getAreaType()
						.getSecondDeathOffsetX(), getAreaType()
						.getSecondDeathOffsetY(), 0));
			}
			p.setCanPvp(false);
			p.reset();
			updateWar();
		}
	}

	/**
	 * Updates the war.
	 */
	public void updateWar() {
		if (timer.isStarted() && isKnockOut()) {
			if ((getPlayersInside(false) == 0 || getPlayersInside(true) == 0)) {
				timer.cancel();
				endWar();
			}
		} else if (timer.isStarted()
				&& !isMostKills()
				&& ((kills & 0xFFFF) >= victoryType || (kills >> 24 & 0xFFFF) >= victoryType)) {
			timer.cancel();
			endWar();
		}
		for (Player p : firstPlayers) {
			timer.refresh(p, true);
		}
		for (Player p : secondPlayers) {
			timer.refresh(p, false);
		}
	}

	public void lockPeople(int time) {
		for (Player player : firstPlayers) {
			player.lock(time);
			player.stopAll();
		}
		for (Player player : secondPlayers) {
			player.lock(time);
			player.stopAll();
		}
	}

	/**
	 * Ends the current war.
	 */
	public void endWar() {
		if (ended)
			return;
		ended = true;
		timer.cancel();
		lockPeople(7);
		currentWars.remove(this);
		firstTeam.setClanWars(null);
		secondTeam.setClanWars(null);

		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				int count1 = getPlayersInside(false);
				int count2 = getPlayersInside(true);
				int firstType;
				int secondType;
				if (timer.isTimeOut()) {
					firstType = 1;
					secondType = 1;
				} else if (isKnockOut() && count1 == count2) {
					firstType = 3;
					secondType = 3;
				} else if (isMostKills()
						&& (kills >> 24 & 0xFFFF) == (kills & 0xFFFF)) {
					firstType = 2;
					secondType = 2;
				} else if (isKnockOut()) {
					boolean firstWon = count1 > count2;
					firstType = firstWon ? 4
							: 8 + (timer.getTimeLeft() == 0 ? 3 : 0);
					secondType = firstWon ? 8
							: 4 + (timer.getTimeLeft() == 0 ? 3 : 0);
				} else if (isMostKills()) {
					boolean firstWon = (kills & 0xFFFF) > (kills >> 24 & 0xFFFF);
					firstType = firstWon ? 6 : 10;
					secondType = firstWon ? 10 : 6;
				} else {
					if ((kills & 0xFFFF) >= victoryType) {
						firstType = 5;
						secondType = 9;
					} else if ((kills >> 24 & 0xFFFF) >= victoryType) {
						firstType = 9;
						secondType = 5;
					} else if ((kills >> 24 & 0xFFFF) == (kills & 0xFFFF)) {
						firstType = 2;
						secondType = 2;
					} else if ((kills & 0xFFFF) > (kills >> 24 & 0xFFFF)) {
						firstType = 6;
						secondType = 10;
					} else {
						firstType = 10;
						secondType = 6;
					}
				}
				String firstMessage = "Your clan "
						+ (firstType < 4 ? "drawed."
								: firstType < 8 ? "is victorious!"
										: "has been defeated!");
				String secondMessage = "Your clan "
						+ (secondType < 4 ? "drawed."
								: secondType < 8 ? "is victorious!"
										: "has been defeated!");

				for (Player player : firstPlayers) {
					leave(player, NORMAL);
					player.getInterfaceManager().sendCentralInterface(790);
					player.getPackets().sendCSVarInteger(268, firstType);
					player.getPackets().sendGameMessage(firstMessage);
					player.getPackets().sendMusicEffectOld(
							firstType < 4 ? 293 : firstType < 8 ? 292 : 294);
				}
				for (Player player : secondPlayers) {
					leave(player, NORMAL);
					player.getInterfaceManager().sendCentralInterface(790);
					player.getPackets().sendCSVarInteger(268, secondType);
					player.getPackets().sendGameMessage(secondMessage);
					player.getPackets().sendMusicEffectOld(
							secondType < 4 ? 293 : secondType < 8 ? 292 : 294);
				}
				GameExecutorManager.slowExecutor.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							int width = (areaType.getNorthEastTile().getX() - areaType
									.getSouthWestTile().getX()) / 8 + 1;
							int height = (areaType.getNorthEastTile().getY() - areaType
									.getSouthWestTile().getY()) / 8 + 1;
							MapBuilder.destroyMap(baseLocation.getChunkX(),
									baseLocation.getChunkY(), width, height);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}, 1200, TimeUnit.MILLISECONDS);
			}
		}, 6);
	}

	/**
	 * Gets the victoryType.
	 * 
	 * @return The victoryType.
	 */
	public int getVictoryType() {
		return victoryType;
	}

	/**
	 * Checks if the victory type is knock-out.
	 * 
	 * @return {@code True} if so (thus victory type equals {@code -1}).
	 */
	public boolean isKnockOut() {
		return victoryType == -1;
	}

	/**
	 * Checks if the victory type is most kills.
	 * 
	 * @return {@code True} if so (thus victory type equals {@code -2}).
	 */
	public boolean isMostKills() {
		return victoryType == -2;
	}

	/**
	 * Sets the victoryType.
	 * 
	 * @param victoryType
	 *            The victoryType to set.
	 * @param p
	 *            The player.
	 * @param other
	 *            The other player.
	 */
	public void setVictoryType(int victoryType, Player p, Player other) {
		this.victoryType = victoryType;
		sendVictoryConfiguration(p);
		sendVictoryConfiguration(other);
	}

	/**
	 * Gets the timeLeft.
	 * 
	 * @return The timeLeft.
	 */
	public int getTimeLeft() {
		return timeLeft;
	}

	/**
	 * Sets the timeLeft.
	 * 
	 * @param timeLeft
	 *            The timeLeft to set.
	 * @param p
	 *            The player.
	 * @param other
	 *            The other player.
	 */
	public void setTimeLeft(int timeLeft, Player p, Player other) {
		this.timeLeft = timeLeft;
		sendTimeConfiguration(p);
		sendTimeConfiguration(other);
	}

	/**
	 * Gets the clan wars timer.
	 * 
	 * @return The clan wars timer.
	 */
	public ClanWarsTimer getTimer() {
		return timer;
	}

	/**
	 * Gets the areaType.
	 * 
	 * @return The areaType.
	 */
	public AreaType getAreaType() {
		return areaType;
	}

	public void useViewingOrb(Player player) {
		if (ended) {
			player.getPackets().sendGameMessage(
					"You cannot use the orb while the war is ending!");
			return;
		}
		if (views == null) {
			views = new WorldTile[5];
			int width = (areaType.getNorthEastTile().getX() - areaType
					.getSouthWestTile().getX());
			int height = (areaType.getNorthEastTile().getY() - areaType
					.getSouthWestTile().getY());
			views[0] = baseLocation.transform(width / 2, height / 2, 0);
			views[1] = baseLocation.transform(width / 2, height - 10, 0);
			views[2] = baseLocation.transform(width - 10, height / 2, 0);
			views[3] = baseLocation.transform(width / 2, 10, 0);
			views[4] = baseLocation.transform(10, height / 2, 0);
		}
		player.getActionManager().setAction(new ViewingOrb(views));
	}

	/**
	 * Sets the areaType.
	 * 
	 * @param areaType
	 *            The areaType to set.
	 */
	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	/**
	 * Gets the magicRuleCount.
	 * 
	 * @return The magicRuleCount.
	 */
	public int getMagicRuleCount() {
		return magicRuleCount;
	}

	/**
	 * Sets the magicRuleCount.
	 * 
	 * @param magicRuleCount
	 *            The magicRuleCount to set.
	 */
	public void setMagicRuleCount(int magicRuleCount) {
		this.magicRuleCount = magicRuleCount;
	}

	/**
	 * Gets the baseLocation.
	 * 
	 * @return The baseLocation.
	 */
	public WorldTile getBaseLocation() {
		return baseLocation;
	}

	/**
	 * Sets the baseLocation.
	 * 
	 * @param baseLocation
	 *            The baseLocation to set.
	 */
	public void setBaseLocation(WorldTile baseLocation) {
		this.baseLocation = baseLocation;
	}

	/**
	 * Gets the wallObjects.
	 * 
	 * @return The wallObjects.
	 */
	public List<WorldObject> getWallObjects() {
		return wallObjects;
	}

	/**
	 * Sets the wallObjects.
	 * 
	 * @param wallObjects
	 *            The wallObjects to set.
	 */
	public void setWallObjects(List<WorldObject> wallObjects) {
		this.wallObjects = wallObjects;
	}

	/**
	 * Gets the firstPlayers.
	 * 
	 * @return The firstPlayers.
	 */
	public List<Player> getFirstPlayers() {
		return firstPlayers;
	}

	/**
	 * Gets the secondPlayers.
	 * 
	 * @return The secondPlayers.
	 */
	public List<Player> getSecondPlayers() {
		return secondPlayers;
	}

	public int getPlayersInside(boolean second) {
		int count = 0;
		for (Player player : second ? secondPlayers : firstPlayers) {
			if (player.isCanPvp())
				count++;
		}
		return count;
	}

	public Player getPlayerInside() {
		for (Player player : firstPlayers) {
			if (player.isCanPvp())
				return player;
		}
		for (Player player : secondPlayers) {
			if (player.isCanPvp())
				return player;
		}
		return null;
	}

	/**
	 * Gets the kills.
	 * 
	 * @return The kills.
	 */
	public int getKills() {
		return kills;
	}

	/**
	 * Sets the current kills.
	 * 
	 * @param kills
	 *            The kills.
	 */
	public void setKills(int kills) {
		this.kills = kills;
	}

	/**
	 * Gets the currentwars.
	 * 
	 * @return The currentwars.
	 */
	public static List<ClanWars> getCurrentwars() {
		return currentWars;
	}
}