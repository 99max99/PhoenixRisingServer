package net.kagani.game.player.controllers;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.minigames.Crucible;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class CrucibleControler extends Controller {

	private Player target;

	@Override
	public void start() {
		sendInterfaces();
		player.setCanPvp(true);
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (Crucible.isImmune(p2))
				return false;
			if (p2 != this.target) {
				player.getPackets()
						.sendGameMessage(
								"You can only attack your target or a Supreme Champion.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (Crucible.isImmune(p2))
				return false;
			if (p2 != this.target)
				return false;
		}
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendMinigameInterface(1296);
		if (target != null)
			player.getPackets().sendIComponentText(1296, 25,
					target.getDisplayName());
	}

	@Override
	public boolean logout() {
		if (isInside())
			Crucible.removePlayer(player, this, true);
		return false;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (interfaceId == 1291 && componentId >= 4 && componentId <= 16) {
			Crucible.travel(player, Crucible.getFissure(componentId), this);
			return false;
		} else if (interfaceId == 1298) {
			if (componentId == 21)
				Crucible.payBountyFee(player, this);
			else if (componentId == 20)
				player.closeInterfaces();
			return false;
		}
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You cannot teleport from an instance shard world.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You cannot teleport from an instance shard world.");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You cannot teleport from an instance shard world.");
		return false;
	}

	@Override
	public void process() {
		Long immune = (Long) player.getTemporaryAttributtes().get(
				"CrucibleImmune");
		long currentTime = Utils.currentTimeMillis();
		if (immune != null && immune > Utils.currentTimeMillis()) {
			player.getPackets().sendIComponentText(1296, 26,
					"" + (((immune - currentTime) / 3000) + 1));
			if (player.getSkullId() != 15 || !player.hasSkull())
				player.setSkullInfiniteDelay(15);
		} else if (target != null) {
			if (player.getSkullId() != 14 || !player.hasSkull())
				player.setSkullInfiniteDelay(14);
			if (player.getTemporaryAttributtes().remove("CrucibleImmune") != null)
				player.getPackets().sendIComponentText(1296, 26, "None");
		} else if (player.hasSkull()) {
			player.removeSkull();
			if (player.getTemporaryAttributtes().remove("CrucibleImmune") != null)
				player.getPackets().sendIComponentText(1296, 26, "None");
		}
	}

	@Override
	public boolean sendDeath() {
		final CrucibleControler thisControler = this;
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
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						player.giveXP();
						killer.reduceDamage(player);
						killer.increaseKillCount(player);
						killer.getInventory().addItemDrop(24444, 1);
						killer.increaseCrucibleHighScore();
						Crucible.setImmune(killer, 21);
						killer.getHintIconsManager().addHintIcon(player.getX(),
								player.getY(), 0, 0, 2, 0, -1, true);
					}
					player.sendItemsOnDeath(killer, true);
					player.reset();
					Crucible.goBank(player, thisControler);
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
	public boolean login() {
		start();
		if (isInside())
			Crucible.addPlayer(player, this);
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 72921 || object.getId() == 72922) {
			Crucible.leaveArena(player);
			return false;
		} else if (object.getId() >= 72923 && object.getId() <= 72935) {
			Crucible.quickTravel(player, this);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		if (object.getId() >= 72923 && object.getId() <= 72935) {
			Crucible.openFissureTravel(player);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick3(WorldObject object) {
		if (object.getId() >= 72923 && object.getId() <= 72935) {
			Crucible.goBank(player, this);
			return false;
		}
		return true;
	}

	@Override
	public void forceClose() {
		if (isInside())
			Crucible.removePlayer(player, this, false);
		player.setCanPvp(false);
		player.removeSkull();
		player.getHintIconsManager().removeAll();
		player.getInterfaceManager().removeMinigameInterface();
	}

	public boolean isInside() {
		if (getArguments() == null || getArguments().length == 0)
			return false;
		return (Boolean) getArguments()[0];
	}

	public void setInside(boolean inside) {
		if (getArguments() == null || getArguments().length == 0)
			this.setArguments(new Object[1]);
		getArguments()[0] = inside;
	}

	public Player getTarget() {
		return target;
	}

	public void setTarget(Player target) {
		this.target = target;
		if (target != null) {
			player.getPackets().sendIComponentText(1296, 25,
					target.getDisplayName());
			player.getHintIconsManager().removeAll();
			player.getHintIconsManager().addHintIcon(target, 9, -1, false);
		} else {
			player.getHintIconsManager().removeUnsavedHintIcon();
			player.getPackets().sendIComponentText(1296, 25, "None");
		}
	}
}