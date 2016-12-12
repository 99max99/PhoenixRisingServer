/**
 * @author 99max99 Magenz - Ventyz Productions
 *
 * SoulWarsController.java created on 31.des.2014.
 */
package net.kagani.game.player.controllers.soulwars;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.soulwars.SoulWars;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Inventory;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

// TODO: Auto-generated Javadoc
/**
 * The Class SoulWarsController.
 */
public class SoulWarsController extends Controller {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.ventyz.server.model.character.controllers.Controller#canEquip(int,
	 * int)
	 */
	@Override
	public boolean canEquip(int slotId, int itemId) {
		Item item = new Item(itemId);
		if (item.getDefinitions().getEquipSlot() == Equipment.SLOT_CAPE) {
			player.getPackets().sendGameMessage(
					"You cannot equip a cape during this minigame.");
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.ventyz.server.model.character.controllers.Controller#canHit(net.ventyz
	 * .server.world.Entity)
	 */
	@Override
	public boolean canHit(Entity target) {
		Player p2 = (Player) target;
		if (player.getEquipment().getCapeId() == p2.getEquipment().getCapeId()) {
			player.getPackets().sendGameMessage(
					"You can't attack your team mate.");
			return false;
		}
		return true;
	}

	/** Details. */
	public static final Animation BURY_ANIMATION = new Animation(827);

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kagani.game.player.controlers.Controler#start()
	 */

	@Override
	public void start() {
		sendSwInterfaces();
		// SoulWarsGameTimer();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kagani.game.player.controlers.Controler#sendInterfaces()
	 */
	/**
	 * Send sw interfaces.
	 */
	public void sendSwInterfaces() {

		player.getInterfaceManager().sendMinigameInterface(836);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kagani.game.player.controlers.Controler#processButtonClick(int,
	 * int, int, int)
	 */

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (interfaceId == 193 && componentId == 48) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You cant teleport during the soulWars match.");
			return false;
		}
		if (interfaceId == 192 && componentId == 24) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You cant teleport during the soulWars match.");
			return false;
		}
		if (interfaceId == 182 && componentId == 13) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Please leave SoulWars before doing this.");
			return false;
		}
		if (interfaceId == 182 && componentId == 6) {

			player.getDialogueManager().startDialogue("SimpleMessage",
					"Please leave SoulWars before doing this.");
			return false;
		}
		if (interfaceId == 746 && componentId == 79) {
			return false;
		}
		if (interfaceId == Inventory.INVENTORY_INTERFACE) {
			Item item = player.getInventory().getItem(slotId);

			if (item != null) {
				if (item.getId() == 526) {
					if (player.didPassRed == true)
						SoulWars.redRequirment++;
					player.getInventory().deleteItem(526, 1);
					player.addFoodDelay(3000);
					player.getPackets().sendSoundEffect(2738, 0, 1);
					player.setNextAnimation(BURY_ANIMATION);
					player.getPackets().sendGameMessage(
							"Your avatar's slayer requirment rises...");
					return false;
				}
				if (player.didPassBlue == true) {
					SoulWars.blueRequirment++;
					player.getInventory().deleteItem(526, 1);
					player.addFoodDelay(3000);
					player.getPackets().sendSoundEffect(2738, 0, 1);
					player.setNextAnimation(BURY_ANIMATION);
					player.getPackets().sendGameMessage(
							"Your avatar's slayer requirment rises...");
					return false;

				} else if (item.getId() == 4049 || item.getId() == 4050
						|| item.getId() == 12853 || item.getId() == 14640
						|| item.getId() == 14648) {
					doBandageEffect(item);
					return false;
				}
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.kagani.game.player.controlers.Controler#processMagicTeleport(net.
	 * MaxScape830.game.WorldTile)
	 */

	// TODO object click for exit

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager()
				.startDialogue("SimpleMessage",
						"A magical force prevents you from teleporting from the arena.");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.ventyz.server.model.character.controllers.Controller#processItemTeleport
	 * (net.ventyz.server.world.WorldTile)
	 */
	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager()
				.startDialogue("SimpleMessage",
						"A magical force prevents you from teleporting from the arena.");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.ventyz.server.model.character.controllers.Controller#magicTeleported
	 * (int)
	 */
	@Override
	public void magicTeleported(int type) {
		player.getControlerManager().forceStop();
	}

	/*
	 * Handle these on object click
	 */

	/**
	 * Do bandage effect.
	 *
	 * @param item
	 *            the item
	 */
	private void doBandageEffect(Item item) {
		int gloves = player.getEquipment().getGlovesId();
		player.heal((int) (player.getMaxHitpoints() * (gloves >= 11079
				&& gloves <= 11084 ? 0.15 : 0.10)));
		int restoredEnergy = (int) (player.getRunEnergy() * 1.3);
		player.setRunEnergy(restoredEnergy > 100 ? 100 : restoredEnergy);
		player.getInventory().deleteItem(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ventyz.server.model.character.controllers.Controller#sendDeath()
	 */
	@Override
	public boolean sendDeath() {
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets()
							.sendGameMessage("Your soul faded away.");
					// player.getgetAppearence().transformIntoNPC(103);

				} else if (loop == 3) {
					checkRespawn(player);
					player.setNextAnimation(new Animation(-1));
					// player.getgetAppearence().transformIntoNPC(-1);
					player.reset();
				} else if (loop == 4) {
					player.getPackets().sendMusicEffectOld(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	/**
	 * Check respawn.
	 *
	 * @param player
	 *            the player
	 */
	public static void checkRespawn(Player player) {
		if (player.didPassBlue == true) {
			player.setNextWorldTile(new WorldTile(1843, 3218, 0));
		}
		if (player.didPassRed == true) {
			player.setNextWorldTile(new WorldTile(1933, 3245, 0));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.kagani.game.player.controlers.Controler#processObjectClick1(net.kagani
	 * .game.WorldObject)
	 */

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 42024 || object.getId() == 42023) {
			player.getInventory().addItem(4049, 1);// Bandages
			return true;
		}
		if (object.getId() == 42027 || object.getId() == 42028) {
			player.getInventory().addItem(113, 1);// Potions
			return true;
		}
		if (object.getId() == 42019) {// Red Neutral Barrier
			if (player.getX() == 1933 && player.getY() == 3244) {
				player.addWalkSteps(1933, 3243, player.getPlane(), false);
				player.getAppearence().transformIntoNPC(-1);
			}
			if (player.getX() == 1933 && player.getY() == 3243) {
				player.addWalkSteps(1933, 3244, player.getPlane(), false);
			}
		}
		if (object.getId() == 42020) {// Blue Neutral Barrier
			if (player.getX() == 1842 && player.getY() == 3219) {
				player.addWalkSteps(1842, 3220, player.getPlane(), false);
				player.getAppearence().transformIntoNPC(-1);
			}
		}
		if (object.getId() == 42018) {// Red Barrier
			if (player.getX() == 1958 && player.getY() == 3239) {

				player.addWalkSteps(1959, 3239, player.getPlane(), false);

			}
		}
		if (object.getId() == 42015) {// Blue Barrier
			if (player.getX() == 1816 && player.getY() == 3225) {

				player.addWalkSteps(1815, 3225, player.getPlane(), false);

			}
			if (player.getX() == 1815 && player.getY() == 3225) {
				player.addWalkSteps(1816, 3225, player.getPlane(), false);

			}
		}
		if (object.getId() == 42026) {
			player.getPackets().sendGameMessage(
					"Barricades are currently unavalible");
		}

		if (object.getId() == 42021 || object.getId() == 42022) {// Portals
			removeControler();
			SoulWars.leaveGame(player);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.kagani.game.player.controlers.Controler#canHit(net.kagani.game.Entity
	 * )
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.kagani.game.player.controlers.Controler#canAttack(net.kagani.game
	 * .Entity)
	 */

	/*
	 * public boolean canAttack(Entity target) { if (player.didPassBlue == true
	 * && target == SoulWars.BlueAvatar || player.didPassRed == true && target
	 * == SoulWars.RedAvatar) { player.getgetPackets().sendGameMessage(
	 * "You can't attack the avatar of your own team!"); return false; }
	 * if(target == SoulWars.BlueAvatar && SoulWars.blueRequirment >=100) {
	 * player.getPackets().sendGameMessage(
	 * "The avatar of creation is impervious to your attack."); return false; }
	 * if (target == SoulWars.RedAvatar && SoulWars.redRequirment >=100){
	 * player.getPackets().sendGameMessage(
	 * "The avatar of destruction is impervious to your attack."); return false;
	 * }
	 * 
	 * return true; }
	 */

	/**
	 * Soul Wars Game TImer
	 */

	/*
	 * private static void SoulWarsGameTimer() {
	 * CoresManager.fastExecutor.schedule(new TimerTask() {
	 * 
	 * @Override public void run() { for (Player player : World.getPlayers()) {
	 * if (player.didPassBlue == true || player.didPassRed == true) {
	 * player.getgetPackets().sendIComponentText(836, 9, "" +
	 * SoulWars.aocDeaths);//blue player.getgetPackets().sendIComponentText(836,
	 * 10, "" + SoulWars.blueRequirment);//blue
	 * player.getgetPackets().sendIComponentText(836, 11, "" +
	 * SoulWars.BlueAvatar.getHitpoints() + "%");//blue
	 * player.getgetPackets().sendIComponentText(836, 13, "" +
	 * SoulWars.aodDeaths);//red player.getgetPackets().sendIComponentText(836,
	 * 14, "" + SoulWars.redRequirment);//red
	 * player.getgetPackets().sendIComponentText(836, 15, "" +
	 * SoulWars.RedAvatar.getHitpoints() + "%");//red
	 * 
	 * player.getgetPackets().sendIComponentText(836, 27, "" +SoulWars.gameTime
	 * + " mins."); if(SoulWars.gameTime == 0) { SoulWars.endGame(); } } } }
	 * 
	 * 
	 * }, 0L, 1000L); }
	 * 
	 * public boolean isAtRedGrave() { return (player.getX() >= 1927 &&
	 * player.getX() <= 1939 && player.getY() <= 3252 && player.getY() >= 3239);
	 * } public boolean isAtBlueGrave() { return (player.getX() >= 1836 &&
	 * player.getX() <= 1848 && player.getY() <= 3224 && player.getY() >= 3213);
	 * }
	 */
}
