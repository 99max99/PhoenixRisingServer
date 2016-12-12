package net.kagani.game.player.controllers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceMovement;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.warbands.Warbands;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.others.GraveStone;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.thieving.Thieving;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.controllers.events.DeathEvent;
import net.kagani.game.player.dialogues.impl.Mandrith_Nastroth;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Wilderness extends Controller {

	private boolean showingSkull;
	private boolean multi;

	@Override
	public void start() {
		refreshMulti();
	}

	@Override
	public boolean login() {
		moved();
		if (isAtWild(player)) {
			int count = 0;
			for (int i = 0; i < 28; i++) {
				if (player.getInventory().getItem(i) == null)
					continue;
				if (player.getInventory().getItem(i).getId() == 27637
						|| player.getInventory().getItem(i).getId() == 27636
						|| player.getInventory().getItem(i).getId() == 27639
						|| player.getInventory().getItem(i).getId() == 27640
						|| player.getInventory().getItem(i).getId() == 27638) {
					player.getInventory().deleteItem(player.getInventory().getItem(i));
					count++;
					continue;
				}
			}
			if (count > 0)
				player.getPackets()
						.sendGameMessage("Any resources you were carrying from the warbands event have been removed.");
		}
		return false;
	}

	private void refreshMulti() {
		boolean multiArea = isMultiZone(player.getX(), player.getY());
		if (multiArea != multi)
			setMulti(multiArea);
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
		player.getPackets().sendHideIComponent(745, 1, !multi);
	}

	/*
	 * keep combating is called while in combat every time attack happens
	 */
	@Override
	public boolean keepCombating(Entity target) {
		if (target instanceof NPC)
			return true;
		if (!canAttack(target))
			return false;
		if (target.getAttackedBy() != player && player.getAttackedBy() != target)
			player.setWildernessSkull();
		if (player.getCombatDefinitions().getSpellId() <= 0
				&& Utils.inCircle(new WorldTile(3105, 3933, 0), target, 24)) {
			player.getPackets().sendGameMessage("You can only use magic in the arena.");
			return false;
		}
		return true;
	}

	/*
	 * can attack checks when you first attack
	 */
	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (player.isCanPvp() && !p2.isCanPvp()) {
				player.getPackets().sendGameMessage("That player is not in the wilderness.");
				return false;
			}
			if (Math.abs(player.getSkills().getCombatLevel() - p2.getSkills().getCombatLevel()) > getWildLevel(
					player)) {
				player.getPackets().sendGameMessage("The difference between your Combat level and the Combat level of "
						+ p2.getDisplayName() + " is too great.");
				player.getPackets()
						.sendGameMessage("He needs to move deeper into the Wilderness before you can attack him.");
				return false;
			}
			if (target instanceof Player
					&& ((Player) target).getControlerManager().getControler() instanceof Wilderness) {
				if (!multi || !(((Wilderness) ((Player) target).getControlerManager().getControler()).multi)) {
					if (player.getAttackedBy() != target && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
						player.getPackets().sendGameMessage("You are already in combat.");
						return false;
					}
					if (target.getAttackedBy() != player && target.getAttackedByDelay() > Utils.currentTimeMillis()) {
						player.getPackets().sendGameMessage("That player is already in combat.");
						return false;
					}
				}
			}
			return true;
		}
		return true;
	}

	/*
	 * dont make it spam here. canhit happens at barrages and stuff to check if
	 * target can be hited no need to check if target is in wild. actualy all
	 * code does it, but, checked at can attack to set a different message than
	 * default
	 */
	@Override
	public boolean canHit(Entity target) {
		if (target instanceof NPC)
			return true;
		Player p2 = (Player) target;
		if (Warbands.warband != null) {
			if (Warbands.warband.isCarryingResources(p2))
				return true;
		}
		if (Math.abs(player.getSkills().getCombatLevel() - p2.getSkills().getCombatLevel()) > getWildLevel(player))
			return false;
		if (target instanceof Player && ((Player) target).getControlerManager().getControler() instanceof Wilderness) {
			if (!multi || !(((Wilderness) ((Player) target).getControlerManager().getControler()).multi)) {
				if ((player.getAttackedBy() != target && player.getAttackedByDelay() > Utils.currentTimeMillis())
						|| (target.getAttackedBy() != player
								&& target.getAttackedByDelay() > Utils.currentTimeMillis()))
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		if (Warbands.warband != null) {
			if (Warbands.warband.isCarryingResources(player)) {
				player.getPackets().sendGameMessage("You cannot teleport whilst carrying resources!");
				return false;
			}
		}
		if (getWildLevel(player) > 20) {
			player.getPackets().sendGameMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		if (player.getEffectsManager().hasActiveEffect(EffectType.TELEPORT_BLOCK)) {
			player.getPackets().sendGameMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;

	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		if (Warbands.warband != null) {
			if (Warbands.warband.isCarryingResources(player)) {
				player.getPackets().sendGameMessage("You cannot teleport whilst carrying resources!");
				return false;
			}
		}
		if (getWildLevel(player) > 30) {
			player.getPackets().sendGameMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		if (player.getEffectsManager().hasActiveEffect(EffectType.TELEPORT_BLOCK)) {
			player.getPackets().sendGameMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		if (player.getEffectsManager().hasActiveEffect(EffectType.TELEPORT_BLOCK)) {
			player.getPackets().sendGameMessage("A mysterious force prevents you from teleporting.");
			return false;
		}
		return true;
	}

	public static final Map<String, Set<Long>> killNames = new ConcurrentHashMap<String, Set<Long>>();
	public static final Map<String, Set<Long>> killIPS = new ConcurrentHashMap<String, Set<Long>>();
	public static final Map<String, Set<Long>> killMACS = new ConcurrentHashMap<String, Set<Long>>();

	public void dropArtefact(Player killer) {
		String[] keys = new String[] { killer.getUsername() + "@" + player.getUsername(),
				killer.getSession().getIP() + "@" + player.getSession().getIP(),
				killer.getLastGameMAC() + "@" + player.getLastGameMAC() };

		if (killer.getLastGameMAC().equals(player.getLastGameMAC())) {
			return;
		}

		@SuppressWarnings("unchecked")
		Set<Long>[] times = new Set[] { killNames.get(keys[0]), killIPS.get(keys[1]), killMACS.get(keys[2]) };

		if (times[0] == null || times[1] == null || times[2] == null) {
			times[0] = new HashSet<Long>();
			times[1] = new HashSet<Long>();
			times[2] = new HashSet<Long>();
			killNames.put(keys[0], times[0]);
			killIPS.put(keys[1], times[1]);
			killMACS.put(keys[2], times[2]);
		}

		long current = Utils.currentTimeMillis();
		for (int i = 0; i < times.length; i++) {
			int count = 0;
			for (Iterator<Long> iterator = times[i].iterator(); iterator.hasNext();) {
				long time = iterator.next();
				if (current < time + 60 * 60 * 1000) {
					// expired
					iterator.remove();
				} else
					count++;
			}
			if (count >= 1)
				return;
		}

		int cb1 = player.getSkills().getCombatLevel();
		if (cb1 < 50)
			return;
		int cb2 = killer.getSkills().getCombatLevel();
		if (cb2 < 50)
			return;
		if (getRiskedWealth(player) < 76000 || getRiskedWealth(killer) < 76000)
			return;
		for (Set<Long> set : times) {
			set.add(current);
		}
		killer.setKillCount(killer.getKillCount() + 1);
		player.setDeathCount(player.getDeathCount() + 1);
		if (Utils.currentTimeMillis() < killer.getLastArtefactTime() + 5 * 60 * 1000)
			return;
		if (Math.abs(cb1 - cb2) > 15)
			return;
		int rareChance = 16;
		if (getWildLevel(killer) >= 30)
			rareChance--;
		int artefact = Mandrith_Nastroth.ARTEFACTS[Utils.random(Mandrith_Nastroth.ARTEFACTS.length - 3)
				+ (Utils.random(rareChance) != 0 ? 3 : 0)];
		killer.setLastArtefactTime(current);

		World.addGroundItem(new Item(artefact), new WorldTile(player), killer, true, 60);
	}

	public static long getRiskedWealth(Player player) {
		Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(player, true, player.hasSkull(),
				player.getPrayer().isProtectingItem());
		Item[][] items = GraveStone.getItemsKeptOnDeath(player, slots);
		if (items.length <= 1) // risking just 1 item or 0
			return 0;
		long riskedWealth = 0;
		for (Item item : items[1])
			riskedWealth += GrandExchange.getPrice(item.getId()) * item.getAmount();
		return riskedWealth;
	}

	public void showSkull() {
		player.getInterfaceManager().sendMinigameInterface(381);
	}

	public static boolean isDitch(int id) {
		return id >= 1440 && id <= 1444 || id >= 65076 && id <= 65087;
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (isDitch(object.getId())) {
			player.lock();
			player.setNextAnimation(new Animation(6132));
			final WorldTile toTile = new WorldTile(
					object.getRotation() == 1 || object.getRotation() == 3 ? object.getX() + 2 : player.getX(),
					object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() - 1 : player.getY(),
					object.getPlane());

			player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2,
					object.getRotation() == 0 || object.getRotation() == 2 ? ForceMovement.SOUTH : ForceMovement.EAST));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(toTile);
					player.faceObject(object);
					removeIcon();
					removeControler();
					player.resetReceivedDamage();
					player.unlock();
				}
			}, 2);
			return false;
		} else if (object.getId() == 2557 || object.getId() == 65717) {
			player.getPackets().sendGameMessage("It seems it is locked, maybe you should try something else.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(final WorldObject object) {
		if (object.getId() == 2557 || object.getId() == 65717) {
			Thieving.pickDoor(player, object);
			return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (isAtWild(player))
			showSkull();
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
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						player.giveXP();
						killer.reduceDamage(player);
						// if (killer.canIncreaseKillCount(player))
						dropArtefact(killer);
						killer.setAttackedByDelay(Utils.currentTimeMillis() + 8000); // imunity
					}
					if (player.getRights() < 2)
						player.sendItemsOnDeath(killer);
					player.reset();
					player.setNextWorldTile(DeathEvent.HUBS[2]); // edgevile
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					removeIcon();
					removeControler();
					player.getMusicsManager().playMusicEffect(MusicsManager.DEATH_MUSIC_EFFECT);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void magicTeleported(int teleType) {
		if (!isAtWild(player.getNextWorldTile())) {
			player.setCanPvp(false);
			removeIcon();
			removeControler();
		}
	}

	@Override
	public void moved() {
		refreshMulti();
		boolean isAtWild = isAtWild(player);
		boolean isAtWildSafe = isAtWildSafe(player);
		if (!showingSkull && isAtWild && !isAtWildSafe) {
			showingSkull = true;
			player.setCanPvp(true);
			showSkull();
		} else if (showingSkull && (isAtWildSafe || !isAtWild)) {
			removeIcon();
		} else if (!isAtWildSafe && !isAtWild) {
			player.setCanPvp(false);
			removeIcon();
			removeControler();
		} else if ((player.getX() == 3386 || player.getX() == 3387) && player.getY() == 3615) {
			removeIcon();
			player.setCanPvp(false);
			removeControler();
			player.getControlerManager().startControler("Kalaboss");
		}
	}

	public void removeIcon() {
		if (showingSkull) {
			showingSkull = false;
			player.setCanPvp(false);

			player.getInterfaceManager().removeMinigameInterface();
			player.getEquipment().refresh(null);
		}
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void forceClose() {
		removeIcon();
		setMulti(false);
	}

	public static final boolean isAtWild(WorldTile tile) {// TODO fix this
		return (tile.getX() >= 3011 && tile.getX() <= 3132 && tile.getY() >= 10052 && tile.getY() <= 10175) // fortihrny
				// dungeon
				|| (tile.getX() >= 2940 && tile.getX() <= 3395 && tile.getY() >= 3525 && tile.getY() <= 4000)
				|| (tile.getX() >= 3264 && tile.getX() <= 3279 && tile.getY() >= 3279 && tile.getY() <= 3672)
				|| (tile.getX() >= 2756 && tile.getX() <= 2875 && tile.getY() >= 5512 && tile.getY() <= 5627)
				|| (tile.getX() >= 3158 && tile.getX() <= 3181 && tile.getY() >= 3679 && tile.getY() <= 3697)
				|| (tile.getX() >= 3280 && tile.getX() <= 3183 && tile.getY() >= 3885 && tile.getY() <= 3888)
				|| (tile.getX() >= 3012 && tile.getX() <= 3059 && tile.getY() >= 10303 && tile.getY() <= 10351);
	}

	public static boolean isAtWildSafe(WorldTile tile) {
		return (tile.getX() >= 2940 && tile.getX() <= 3395 && tile.getY() <= 3524 && tile.getY() >= 3523);
	}

	public boolean isMulti() {
		return multi;
	}

	public static int getWildLevel(WorldTile tile) {
		int wildLevel = tile.getY() > 9900 ? ((tile.getY() - 9912) / 8 + 1) : ((tile.getY() - 3520) / 8 + 1);
		return wildLevel < 7 ? 7 : wildLevel;
		/**
		 * if (tile.getY() > 9900) return (tile.getY() - 9912) / 8 + 1; return
		 * (tile.getY() - 3520) / 8 + 1;
		 */
	}

	private static boolean isMultiZone(int destX, int destY) {
		return (destX >= 3029 && destX <= 3374 && destY >= 3759 && destY <= 3903) || // wild
				(destX >= 2250 && destX <= 2280 && destY >= 4670 && destY <= 4720)
				|| (destX >= 3198 && destX <= 3380 && destY >= 3904 && destY <= 3970)
				|| (destX >= 3191 && destX <= 3326 && destY >= 3510 && destY <= 3759)
				|| (destX >= 2987 && destX <= 3006 && destY >= 3912 && destY <= 3937)
				|| (destX >= 2245 && destX <= 2295 && destY >= 4675 && destY <= 4720)
				|| (destX >= 3070 && destX <= 3290 && destY >= 9821 && destY <= 10003)
				|| (destX >= 3006 && destX <= 3071 && destY >= 3602 && destY <= 3710)
				|| (destX >= 3134 && destX <= 3192 && destY >= 3519 && destY <= 3646)
				|| (destX >= 2815 && destX <= 2966 && destY >= 5240 && destY <= 5375);
	}

}
