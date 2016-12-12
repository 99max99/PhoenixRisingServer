package net.kagani.game.minigames.clanwars;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.minigames.clanwars.ClanWars.Rules;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.Drinkables.Drink;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

/**
 * A controler subclass handling players in the clan wars activity.
 * 
 * @author Emperor
 * 
 */
public final class WarControler extends Controller {

	/**
	 * The clan wars instance.
	 */
	private transient ClanWars clanWars;

	/**
	 * Constructs a new {@code WarControler} {@code Object}.
	 */
	public WarControler() {
		/*
		 * empty.
		 */
	}

	@Override
	public void start() {
		this.clanWars = (ClanWars) super.getArguments()[0];
		moved();
	}

	@Override
	public boolean sendDeath() {
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
					if (clanWars.get(Rules.ITEMS_LOST)) {
						Player killer = player
								.getMostDamageReceivedSourcePlayer();
						if (killer != null) {
							player.giveXP();
							killer.reduceDamage(player);
							player.sendItemsOnDeath(killer, true);
						}
					}
					player.reset();
					if (player.isCanPvp()) {
						player.setCanPvp(false);
						if (player.getCurrentFriendsChat() != null
								&& player.getCurrentFriendsChat().getClanWars() != null) {
							if (clanWars.getFirstTeam() == player
									.getCurrentFriendsChat()) {
								player.setNextWorldTile(clanWars
										.getBaseLocation()
										.transform(
												clanWars.getAreaType()
														.getFirstDeathOffsetX(),
												clanWars.getAreaType()
														.getFirstDeathOffsetY(),
												0));
								int firstKills = clanWars.getKills() & 0xFFFF;
								int secondKills = (clanWars.getKills() >> 24 & 0xFFFF) + 1;
								clanWars.setKills(firstKills
										| (secondKills << 24));
							} else {
								WorldTile northEast = clanWars
										.getBaseLocation()
										.transform(
												clanWars.getAreaType()
														.getNorthEastTile()
														.getX()
														- clanWars
																.getAreaType()
																.getSouthWestTile()
																.getX(),
												clanWars.getAreaType()
														.getNorthEastTile()
														.getY()
														- clanWars
																.getAreaType()
																.getSouthWestTile()
																.getY(), 0);
								player.setNextWorldTile(northEast.transform(
										clanWars.getAreaType()
												.getSecondDeathOffsetX(),
										clanWars.getAreaType()
												.getSecondDeathOffsetY(), 0));
								int firstKills = (clanWars.getKills() & 0xFFFF) + 1;
								int secondKills = clanWars.getKills() >> 24 & 0xFFFF;
								clanWars.setKills(firstKills
										| (secondKills << 24));
							}
						}
						clanWars.updateWar();
					}
					player.setNextAnimation(new Animation(-1));
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

	@Override
	public boolean canEat(int heal) {
		if (clanWars.get(Rules.NO_FOOD)) {
			player.getPackets().sendGameMessage(
					"Food has been disabled during this war.");
			return false;
		}
		return true;
	}

	@Override
	public boolean canPot(Drink pot) {
		if (clanWars.get(Rules.NO_POTIONS)) {
			player.getPackets().sendGameMessage(
					"Potions has been disabled during this war.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You cannot teleport out of this arena.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You cannot teleport out of this arena.");
		return false;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendMinigameInterface(265);
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		switch (object.getId()) {
		case 38697:
		case 28140:
		case 38696:
		case 38695:
		case 28139:
		case 38694:
		case 28214:
			clanWars.leave(player, ClanWars.NORMAL);
			return false;
		case 38693:
		case 28194:
			clanWars.useViewingOrb(player);
			return false;
		}
		return false;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (!clanWars.getFirstPlayers().contains(player)
				&& !clanWars.getSecondPlayers().contains(player)) {
			return false;
		}
		if (clanWars.getFirstPlayers().contains(player)
				&& clanWars.getFirstPlayers().contains(target)) {
			player.getPackets().sendGameMessage(
					"You can't attack players in your own team.");
			return false;
		}
		if (clanWars.getSecondPlayers().contains(player)
				&& clanWars.getSecondPlayers().contains(target)) {
			player.getPackets().sendGameMessage(
					"You can't attack players in your own team.");
			return false;
		}
		if (!clanWars.getTimer().isStarted()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (!clanWars.getFirstPlayers().contains(player)
				&& !clanWars.getSecondPlayers().contains(player))
			return false;
		if (clanWars.getFirstPlayers().contains(player)
				&& clanWars.getFirstPlayers().contains(target))
			return false;
		if (clanWars.getSecondPlayers().contains(player)
				&& clanWars.getSecondPlayers().contains(target))
			return false;
		if (!clanWars.getTimer().isStarted())
			return false;
		return true;
	}

	@Override
	public boolean keepCombating(Entity victim) {
		boolean isRanging = player.getCombatDefinitions().getStyle(true) == Combat.RANGE_TYPE
				|| player.getCombatDefinitions().getStyle(false) == Combat.RANGE_TYPE;
		if (player.getCombatDefinitions().getSpellId() > 0) {
			switch (clanWars.getMagicRuleCount()) {
			case 1: // Standard spells only.
				if (player.getCombatDefinitions().getSpellBook() != 0) {
					player.getPackets().sendGameMessage(
							"You can only use modern spells during this war!");
					return false;
				}
				break;
			case 2: // Bind/Snare/Entangle only.
				if (player.getCombatDefinitions().getSpellBook() != 0) {
					player.getPackets().sendGameMessage(
							"You can only use binding spells during this war!");
					return false;
				}
				switch (player.getCombatDefinitions().getSpellId()) {
				case 25: // updated rs3
				case 43:
				case 70:
					break;
				default:
					player.getPackets().sendGameMessage(
							"You can only use binding spells during this war!");
					return false;
				}
				break;
			case 3: // No magic at all.
				player.getPackets().sendGameMessage(
						"Magic combat is not allowed during this war!");
				return false;
			}
		}
		if (isRanging && clanWars.get(Rules.NO_RANGE)) {
			player.getPackets().sendGameMessage(
					"Ranged combat is not allowed during this war!");
			return false;
		}
		if (!isRanging && clanWars.get(Rules.NO_MELEE)
				&& player.getCombatDefinitions().getSpellId() <= 0) {
			player.getPackets().sendGameMessage(
					"Melee combat is not allowed during this war!");
			return false;
		}
		return true;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void moved() {
		switch (clanWars.getAreaType()) {
		case PLATEAU:
		case TURRETS:
			// player.setForceMultiArea(true);
			break;
		case FORSAKEN_QUARRY:
			WorldTile northEast = clanWars
					.getBaseLocation()
					.transform(
							clanWars.getAreaType().getNorthEastTile().getX()
									- clanWars.getAreaType().getSouthWestTile()
											.getX(),
							clanWars.getAreaType().getNorthEastTile().getY()
									- clanWars.getAreaType().getSouthWestTile()
											.getY(), 0).transform(-16, -16, 0);
			WorldTile southWest = clanWars.getBaseLocation().transform(16, 16,
					0);
			// player.setForceMultiArea(player.getX() >= southWest.getX() &&
			// player.getY() >= southWest.getY() && player.getX() <=
			// northEast.getX() && player.getY() <= northEast.getY());
			break;
		}
	}

	@Override
	public void forceClose() {
		clanWars.leave(player, ClanWars.NORMAL);
	}

	@Override
	public void magicTeleported(int type) {
		clanWars.leave(player, ClanWars.TELEPORTED);
	}

	/*
	 * shouldnt call
	 */
	@Override
	public boolean login() {
		player.setNextWorldTile(new WorldTile(2992, 9676, 0));
		return true;
	}

	@Override
	public boolean logout() {
		clanWars.leave(player, ClanWars.LOGOUT);
		return true;
	}
}