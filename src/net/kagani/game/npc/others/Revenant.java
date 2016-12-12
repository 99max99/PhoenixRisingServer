package net.kagani.game.npc.others;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class Revenant extends NPC {

	public Revenant(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setDropRateFactor(2); // duplicates
	}

	@Override
	public void spawn() {
		super.spawn();
		setNextAnimation(new Animation(getSpawnAnimation()));
	}

	public static void useForinthryBrace(Player player, Item item, int slot) {
		int newId = item.getId() == 11103 ? -1 : item.getId() + 2;
		player.getInventory().getItems()
				.set(slot, newId == -1 ? null : new Item(newId));
		player.getInventory().refresh(slot);
		player.getPackets().sendGameMessage(
				newId != -1 ? "Your Forinthry bracelet has "
						+ Utils.NUMBERS[(11103 - item.getId()) / 2]
						+ " charge left."
						: "Your Forinthry bracelet turns to dust.", true);
		player.getPackets().sendGameMessage(
				"For one minute, revenants cannot damage you.");
		player.getPackets().sendGameMessage(
				"For one hour, revenants will be unagressive to you.");
		player.getTemporaryAttributtes().put(Key.REVENEANT_IVULNERABILITY,
				Utils.currentWorldCycle());
	}

	@Override
	public ArrayList<Entity> getPossibleTargets(boolean checkNPCs,
			boolean checkPlayers) {
		int size = getSize();
		int agroRatio = getCombatDefinitions().getAgroRatio();
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playerIndexes != null) {
				for (int playerIndex : playerIndexes) {
					Player player = World.getPlayers().get(playerIndex);
					if (player == null
							|| player.getPlane() != getPlane()
							|| player.isDead()
							|| player.hasFinished()
							|| !player.isRunning()
							|| player.getAppearence().isHidden()
							|| !Utils.isOnRange(getX(), getY(), size,
									player.getX(), player.getY(),
									player.getSize(), agroRatio)
							|| !clipedProjectile(player, false))
						continue;
					Long ivulnerability = (Long) player
							.getTemporaryAttributtes().get(
									Key.REVENEANT_IVULNERABILITY);
					if (ivulnerability != null
							&& ivulnerability + 6000 > Utils
									.currentWorldCycle())
						continue;
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}

	public int getSpawnAnimation() {
		switch (getId()) {
		case 13465:
			return 7410;
		case 13466:
		case 13467:
		case 13468:
		case 13469:
			return 7447;
		case 13470:
		case 13471:
			return 7485;
		case 13472:
			return -1;
		case 13473:
			return 7426;
		case 13474:
			return 7403;
		case 13475:
			return 7457;
		case 13476:
			return 7464;
		case 13477:
			return 7478;
		case 13478:
			return 7416;
		case 13479:
			return 7471;
		case 13480:
			return 7440;
		case 13481:
		default:
			return -1;
		}
	}

}
