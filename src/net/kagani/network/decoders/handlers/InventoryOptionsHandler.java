package net.kagani.network.decoders.handlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.fistofguthix.MinigameManager;
import net.kagani.game.npc.clans.ClanVexillum;
import net.kagani.game.npc.familiar.Familiar.SpecialAttack;
import net.kagani.game.npc.others.PolyporeCreature;
import net.kagani.game.npc.others.Revenant;
import net.kagani.game.npc.vorago.Vorago;
import net.kagani.game.npc.vorago.VoragoHandler;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Inventory;
import net.kagani.game.player.MoneyPouch;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.ActionBar.MagicAbilityShortcut;
import net.kagani.game.player.QuestManager.Quests;
import net.kagani.game.player.actions.AscensionBoltCreation;
import net.kagani.game.player.actions.Firemaking;
import net.kagani.game.player.actions.Fletching;
import net.kagani.game.player.actions.GemCutting;
import net.kagani.game.player.actions.GemTipCutting;
import net.kagani.game.player.actions.HerbCleaning;
import net.kagani.game.player.actions.Herblore;
import net.kagani.game.player.actions.Portables;
import net.kagani.game.player.actions.Smithing;
import net.kagani.game.player.actions.Summoning;
import net.kagani.game.player.actions.TrapAction;
import net.kagani.game.player.actions.Fletching.FletchData;
import net.kagani.game.player.actions.GemCutting.Gem;
import net.kagani.game.player.actions.GemTipCutting.GemTips;
import net.kagani.game.player.actions.Portables.Portable;
import net.kagani.game.player.actions.Summoning.Pouch;
import net.kagani.game.player.actions.divination.DivinePlacing;
import net.kagani.game.player.actions.divination.WeavingEnergy.Energy;
import net.kagani.game.player.content.AccessorySmithing;
import net.kagani.game.player.content.AncientEffigies;
import net.kagani.game.player.content.Bonds;
import net.kagani.game.player.content.Coalbag;
import net.kagani.game.player.content.Consumables;
import net.kagani.game.player.content.Dicing;
import net.kagani.game.player.content.DragonfireShield;
import net.kagani.game.player.content.Drinkables;
import net.kagani.game.player.content.DwarfMultiCannon;
import net.kagani.game.player.content.ExplorerRing;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.content.FlyingEntityHunter;
import net.kagani.game.player.content.GodswordCreating;
import net.kagani.game.player.content.ItemColouring;
import net.kagani.game.player.content.ItemColouring.Colourables;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.ItemSets;
import net.kagani.game.player.content.ItemTransportation;
import net.kagani.game.player.content.Lamps;
import net.kagani.game.player.content.LightSource;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.content.MysteryBox;
import net.kagani.game.player.content.Nest;
import net.kagani.game.player.content.OrnamentKits;
import net.kagani.game.player.content.PrayerBooks;
import net.kagani.game.player.content.Runecrafting;
import net.kagani.game.player.content.SkillCapeCustomizer;
import net.kagani.game.player.content.SkillsDialogue;
import net.kagani.game.player.content.Slayer;
import net.kagani.game.player.content.SpiritshieldCreating;
import net.kagani.game.player.content.TreeSaplings;
import net.kagani.game.player.content.WeaponPoison;
import net.kagani.game.player.content.Burying.Bone;
import net.kagani.game.player.content.Drinkables.Drink;
import net.kagani.game.player.content.FlyingEntityHunter.FlyingEntities;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.controllers.Barrows;
import net.kagani.game.player.controllers.FightKiln;
import net.kagani.game.player.controllers.SorceressGarden;
import net.kagani.game.player.controllers.Wilderness;
import net.kagani.game.player.dialogues.impl.AmuletAttaching;
import net.kagani.game.player.dialogues.impl.AttachingOrbsDialouge;
import net.kagani.game.player.dialogues.impl.LeatherCraftingD;
import net.kagani.game.player.dialogues.impl.SqirkFruitSqueeze;
import net.kagani.game.player.dialogues.impl.CombinationsD.Combinations;
import net.kagani.game.player.dialogues.impl.SqirkFruitSqueeze.SqirkFruit;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.stream.InputStream;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class InventoryOptionsHandler {

	public static void handleItemOption2(final Player player, final int slotId, final int itemId, Item item) {
		if (player.isLocked() || player.isStunned() || player.getEmotesManager().isDoingEmote())
			return;
		if (Firemaking.isFiremaking(player, itemId))
			return;
		if (ExplorerRing.handleOption(player, item, slotId, 2))
			return;
		else if (itemId == 20709) {
			long currentTime = Utils.currentTimeMillis();
			if (player.getClanManager() == null) {
				player.getPackets().sendGameMessage("You must be in a clan to do that!");
				return;
			}
			if (player.getAttackedByDelay() + 2500 > currentTime) {
				player.getPackets().sendGameMessage("You can't do this while in combat.");
				return;
			}
			if (!player.getUsername().equalsIgnoreCase(ClanVexillum.getClanPlanterUsername(player))) {
				if (player.getTemporaryAttributtes().contains("placedClanVex")) {
					ClanVexillum vex = (ClanVexillum) player.getTemporaryAttributtes().remove("placedClanVex");
					vex.finish();
				}
				player.getTemporaryAttributtes().put("placedClanVex",
						new ClanVexillum(player, player.getClanManager().getClan(),
								new WorldTile(player.getX() + 1, player.getY(), player.getPlane())));
			} else
				player.getPackets().sendGameMessage("You have already placed a vexillum!");
		} else if (itemId == 4155)
			player.getSlayerManager().checkKillsLeft();
		else if (itemId == 31846)
			player.getReaperTasks().checkKillsLeft();
		else if (itemId == 18339)
			Coalbag.withdrawCoal(player, 1);
		else if (itemId == 15262)
			ItemSets.openSkillPack(player, itemId, 12183, 5000, player.getInventory().getAmountOf(itemId));
		else if (itemId == 15362)
			ItemSets.openSkillPack(player, itemId, 230, 50, player.getInventory().getAmountOf(itemId));
		else if (itemId == 15363)
			ItemSets.openSkillPack(player, itemId, 228, 50, player.getInventory().getAmountOf(itemId));
		else if (itemId == 15364)
			ItemSets.openSkillPack(player, itemId, 222, 50, player.getInventory().getAmountOf(itemId));
		else if (itemId == 15365)
			ItemSets.openSkillPack(player, itemId, 9979, 50, player.getInventory().getAmountOf(itemId));
		else if (itemId == 1225) {
			// player.getPackets().sendInputIntegerScript("What would you like
			// to do when you grow up?");
			// player.getTemporaryAttributtes().put("xformring", Boolean.TRUE);
		} else if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			if (itemId == 5509)
				pouch = 0;
			if (itemId == 5510 || itemId == 5511)
				pouch = 1;
			if (itemId == 5512)
				pouch = 2;
			if (itemId == 5514)
				pouch = 3;
			Runecrafting.emptyPouch(player, pouch);
			player.stopAll(false);
		} else if (itemId >= 15086 && itemId <= 15100) {
			Dicing.handleRoll(player, itemId, true);
			return;
		} else if (itemId == 6583 || itemId == 7927) {
			AccessorySmithing.ringTransformation(player, itemId);
		} else if (item.getDefinitions().containsInventoryOption(1, "Extinguish")) {
			if (LightSource.extinguishSource(player, slotId, false))
				return;
		} else {
			handleWear(player, slotId, item);
		}
	}

	public static void handleWear(final Player player, final int slotId, Item item) {
		if (player.isEquipDisabled())
			return;
		if (item.getId() == 22332) {
			player.getPackets().sendGameMessage("You can't wear this item.");
			return;
		}
		if (player.getSwitchItemCache().isEmpty()) {
			player.getSwitchItemCache().add(slotId);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					List<Integer> slots = player.getSwitchItemCache();
					int[] slot = new int[slots.size()];
					for (int i = 0; i < slot.length; i++)
						slot[i] = slots.get(i);
					player.getSwitchItemCache().clear();
					ButtonHandler.processWear(player, slot);
					player.stopAll(false, true, false);
				}
			});
		} else if (!player.getSwitchItemCache().contains(slotId)) {
			player.getSwitchItemCache().add(slotId);
		}
	}

	public static void dig(final Player player) {
		player.resetWalkSteps();
		player.setNextAnimation(new Animation(830));
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.unlock();
				if (player.getTreasureTrailsManager().useDig())
					return;
				if (Barrows.digIntoGrave(player))
					return;
				if (player.getX() == 3005 && player.getY() == 3376 || player.getX() == 2999 && player.getY() == 3375
						|| player.getX() == 2996 && player.getY() == 3377
						|| player.getX() == 2989 && player.getY() == 3378
						|| player.getX() == 2987 && player.getY() == 3387
						|| player.getX() == 2984 && player.getY() == 3387) {
					// mole
					player.setNextWorldTile(new WorldTile(1752, 5137, 0));
					player.getPackets()
							.sendGameMessage("You seem to have dropped down into a network of mole tunnels.");
					return;
				} else if (player.withinDistance(new WorldTile(2748, 3734, 0), 2)) {
					player.lock();
					player.setNextGraphics(new Graphics(80, 5, 60));
					FadingScreen.fade(player, 1000, new Runnable() {

						@Override
						public void run() {
							player.unlock();
							player.setNextWorldTile(new WorldTile(2696, 10121, 0));
						}
					});
					player.getPackets().sendGameMessage("You fall through the ground into a network of tunnels.");
					return;
				}
				player.getPackets().sendGameMessage("You find nothing.");
			}

		});
	}

	public static void handleItemOption1(final Player player, final int slotId, final int itemId, Item item) {
		if (player.isLocked() || player.isStunned() || player.getEmotesManager().isDoingEmote())
			return;
		if (Drinkables.drink(player, item, slotId))
			return;
		player.stopAll(false);
		Pouch sumPouch = Pouch.forId(itemId);
		if (sumPouch != null)
			Summoning.spawnFamiliar(player, sumPouch);
		switch (item.getId()) {

		/**
		 * QUEST ITEMS
		 **/

		case 24313:
			if (player.getControlerManager().getControler() != null) {
				player.getPackets().sendGameMessage(
						"You must finish the story you are currently playing before entering the dream world.");
				return;
			} else if (player.getQuestManager().getQuestStage(Quests.SONG_FROM_THE_DEPTHS) == -2
					|| player.getQuestManager().getQuestStage(Quests.SONG_FROM_THE_DEPTHS) == -1) {
				player.getInventory().deleteItem(24313, 28);
				player.getPackets().sendGameMessage("Please submit a bug report telling us how you got that.");
				return;
			} else if (player.getX() < 2938 || player.getY() < 3190 || player.getX() > 2998 || player.getY() > 3260) {
				player.getPackets()
						.sendGameMessage("The potion won't have any affect as you are not close enough to Waylan.");
				return;
			} else {
				player.lock(6);
				player.setNextAnimationNoPriority(new Animation(
						(player.getCombatDefinitions().isCombatStance() && !player.isLegacyMode()) ? 18003 : 18000));
				// player.getPackets().sendSound(4580, 0, 1);//potion drink
				// sound
				player.getInventory().deleteItem(24313, 1);
				player.getControlerManager().startControler("SongFromTheDepthsSurface");
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you drink the potion, you fall asleep, but you still have some control over your body.");
			}
			break;

		/**
		 * Divination
		 */
		case 29313:
		case 29314:
		case 29315:
		case 29316:
		case 29317:
		case 29318:
		case 29319:
		case 29320:
		case 29321:
		case 29322:
		case 29323:
		case 29324:
			Energy energy = Energy.getEnergy(itemId);
			if (energy != null)
				player.getDialogueManager().startDialogue("WeavingD", item, energy);
			else
				player.getPackets().sendGameMessage("Weaving for this energy has not been added.");
			break;

		case 9044:
		case 9046:
		case 9048:
			player.getDialogueManager().startDialogue("PharaohSceptre", itemId);
			break;

		case 9050:
			player.getPackets().sendGameMessage("Your sceptre has has run out of charges.");
			break;

		case 22332:
			player.stopAll();
			player.getInterfaceManager().sendCentralInterface(1153);
			player.getPackets().sendIComponentText(1153, 139, "-1");
			player.getPackets().sendIComponentText(1153, 143, "-1");
			player.getPackets().sendIComponentText(1153, 134, "-1");
			break;

		case 32335:
			if (player.isHardcoreIronman())
				player.getDialogueManager().startDialogue("JarOfDivineLight");
			else
				player.getPackets().sendGameMessage("You need to be an Hardcore Ironman to use this item.");
			break;

		case 26384:
		case 6199:
			if (player.getInventory().containsItem(item.getId(), 1))
				MysteryBox.handleReward(player, item, slotId);
			else
				player.getPackets()
						.sendGameMessage("You don't have the required items in your inventory to open this item.");
			break;

		case 20709:
			handleWear(player, slotId, item);
			break;

		case 23030:
			player.getPetManager().spawnPet(itemId, true);
			break;

		/**
		 * Quest Item Creating
		 */

		/**
		 * Dramen
		 */
		case 771:
			if (player.getInventory().containsItem(Fletching.KNIFE, 1)
					|| player.getToolbelt().containsItem(Fletching.KNIFE)) {
				if (!player.getInventory().containsItem(771, 1)) {
					player.getPackets().sendGameMessage("You need a Dramen Branch to create a staff.");
					return;
				}
				player.setNextAnimation(new Animation(6702));
				player.getInventory().deleteItem(771, 1);
				player.getInventory().addItem(772, 1);
			} else
				player.getPackets().sendGameMessage("You do not have a knife in your inventory or toolbelt.");

			break;

		case 15484:
			player.getInterfaceManager().gazeOrbOfOculus();
			break;

		case 30920:
			player.getPackets().sendGameMessage("You have " + player.getSilverhawkFeathers() + "% charges left.");
			break;

		case 30915:
			if (player.getSilverhawkFeathers() >= 100) {
				player.getPackets().sendGameMessage("You may only fill max 500 Silverhawk feathers at a time.");
				return;
			}
			if (player.getInventory().containsItem(30915, 5)) {
				int amount = player.getInventory().getAmountOf(30915);
				int charge = amount / 5;
				player.getInventory().deleteItem(30915, amount);
				player.setSilverhawkFeathers(charge + player.getSilverhawkFeathers());
				player.getPackets().sendGameMessage("You have charged your Silverhawk boots with " + charge + "%.");
			} else
				player.getPackets().sendGameMessage("You need at least 5 Silverhawk feathers.");
			break;

		case 27234:
		case 27235:
		case 27236:
			player.getDDToken().claimToken(itemId);
			break;

		case 31770:
		case 31771:
		case 31772:
			if (System.currentTimeMillis() - player.delay > 800 && player.getInventory().containsItem(itemId, 1)) {
				player.delay = System.currentTimeMillis();
				int skill = itemId == 31770 ? 0 : itemId == 31771 ? 4 : 6;
				double xp = player.getSkills().getTotalLevel() * 5;
				player.getSkills().addXp(skill, xp);
				player.getInventory().deleteItem(slotId, item);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You received " + Utils.format(xp * Settings.XP_RATE) + " xp in "
								+ player.getSkills().getSkillName(skill) + ".");
			}
			break;

		case 3062:
			if (System.currentTimeMillis() - player.delay > 800 && player.getInventory().containsItem(itemId, 1)) {
				player.delay = System.currentTimeMillis();
				if (player.getInventory().getAmountOf(itemId) >= 5) {
					for (int i = 0; i < player.getInventory().getAmountOf(itemId); i++) {
						if (!player.getInventory().hasFreeSlots()) {
							player.getPackets().sendGameMessage("Not enough space in your inventory.");
							return;
						}
						int ITEMS[] = { 23244, 23610, 26314, 1392, 537, 892, 11212, 560, 565, 386, 15273, 28465, 220 };
						int rewardId = ITEMS[(int) (Math.random() * ITEMS.length)];
						int amount = rewardId == 560 || rewardId == 565 ? Utils.random(500, 1000)
								: rewardId == 892 || rewardId == 11212 ? Utils.random(200, 500) : Utils.random(25, 75);
						player.getInventory().deleteItem(itemId, 1);
						player.getInventory().addItem(rewardId, amount);
					}
					player.getDialogueManager().startDialogue("SimpleMessage", player.getInventory().getAmountOf(itemId)
							+ " strange boxes were opened to make it easier for you.");
				} else {
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("Not enough space in your inventory.");
						return;
					}
					int ITEMS[] = { 23244, 23610, 26314, 1392, 537, 892, 11212, 560, 565, 386, 15273, 28465, 220 };
					int rewardId = ITEMS[(int) (Math.random() * ITEMS.length)];
					int amount = rewardId == 560 || rewardId == 565 ? Utils.random(500, 1000)
							: rewardId == 892 || rewardId == 11212 ? Utils.random(200, 500) : Utils.random(25, 75);
					player.getInventory().deleteItem(itemId, 1);
					player.getInventory().addItem(rewardId, amount);
					player.getDialogueManager().startDialogue("SimpleMessage", "You received x" + Utils.format(amount)
							+ " " + ItemDefinitions.getItemDefinitions(rewardId).getName() + " from the strange box.");
				}
			}
			break;
		}
		int leatherIndex = LeatherCraftingD.getIndex(item.getId());
		if (leatherIndex != -1) {
			player.getDialogueManager().startDialogue("LeatherCraftingD", leatherIndex, false);
			return;
		}
		if (ExplorerRing.handleOption(player, item, slotId, 1))
			return;
		if (itemId == 11159) {
			if (!player.getInventory().containsItem(11159, 1) || player.getInventory().getFreeSlots() < 1)
				return;
			player.getInventory().deleteItem(11159, 1);
			Item items[] = { new Item(10150), new Item(10010), new Item(10006), new Item(10031), new Item(10029),
					new Item(596), new Item(10008) };
			for (Item xyz : items) {
				if (!player.getInventory().addItem(xyz.getId(), 1))
					World.addGroundItem(xyz, player, player, true, 180);
			}
		}
		if (itemId == 12853 || itemId == 12855) {
			if (MinigameManager.INSTANCE().fistOfGuthix().team(player) != null)
				MinigameManager.INSTANCE().fistOfGuthix().team(player).handleItems(player, item);
		}
		if (itemId == 28626) { // vitalis
			if (player.getInventory().containsItem(28626, 1)) {
				player.getInventory().deleteItem(28626, 1);
				player.getInventory().addItem(28630, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33832) { // general awwdor
			if (player.getInventory().containsItem(33832, 1)) {
				player.getInventory().deleteItem(33832, 1);
				player.getInventory().addItem(33806, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33833) { // commander miniana
			if (player.getInventory().containsItem(33833, 1)) {
				player.getInventory().deleteItem(33833, 1);
				player.getInventory().addItem(33807, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33832) { // k'ril tinyroth
			if (player.getInventory().containsItem(33831, 1)) {
				player.getInventory().deleteItem(33831, 1);
				player.getInventory().addItem(33805, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33830) { // chick'ara
			if (player.getInventory().containsItem(33830, 1)) {
				player.getInventory().deleteItem(33830, 1);
				player.getInventory().addItem(33804, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33834) { // nexterminator
			if (player.getInventory().containsItem(33834, 1)) {
				player.getInventory().deleteItem(33834, 1);
				player.getInventory().addItem(33808, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33835) { // mallory
			if (player.getInventory().containsItem(33835, 1)) {
				player.getInventory().deleteItem(33835, 1);
				player.getInventory().addItem(33810, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33836) { // ellie
			if (player.getInventory().containsItem(33836, 1)) {
				player.getInventory().deleteItem(33836, 1);
				player.getInventory().addItem(33811, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33837) { // corporeal puppy
			if (player.getInventory().containsItem(33837, 1)) {
				player.getInventory().deleteItem(33837, 1);
				player.getInventory().addItem(33812, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33838) { // molly
			if (player.getInventory().containsItem(33838, 1)) {
				player.getInventory().deleteItem(33838, 1);
				player.getInventory().addItem(33813, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33839) { // shrimpy
			if (player.getInventory().containsItem(33839, 1)) {
				player.getInventory().deleteItem(33839, 1);
				player.getInventory().addItem(33814, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33840) { // kalphite grubling
			if (player.getInventory().containsItem(33840, 1)) {
				player.getInventory().deleteItem(33840, 1);
				player.getInventory().addItem(33815, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33841) { // kalphite grublet
			if (player.getInventory().containsItem(33841, 1)) {
				player.getInventory().deleteItem(33841, 1);
				if (player.getBank().containsItem(33816)) {
					player.getInventory().addItem(33817, 1);
				} else {
					player.getInventory().addItem(33816, 1);
				}
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33842) { // King black dragonling
			if (player.getInventory().containsItem(33842, 1)) {
				player.getInventory().deleteItem(33842, 1);
				player.getInventory().addItem(33818, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33843) { // legio primulus
			if (player.getInventory().containsItem(33843, 1)) {
				player.getInventory().deleteItem(33843, 1);
				player.getInventory().addItem(33819, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33844) { // legio secundulus
			if (player.getInventory().containsItem(33844, 1)) {
				player.getInventory().deleteItem(33844, 1);
				player.getInventory().addItem(33820, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33845) { // legio tertioulus
			if (player.getInventory().containsItem(33845, 1)) {
				player.getInventory().deleteItem(33845, 1);
				player.getInventory().addItem(33821, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33846) { // legio quartulus
			if (player.getInventory().containsItem(33846, 1)) {
				player.getInventory().deleteItem(33846, 1);
				player.getInventory().addItem(33822, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33847) { // legio quintulus
			if (player.getInventory().containsItem(33847, 1)) {
				player.getInventory().deleteItem(33847, 1);
				player.getInventory().addItem(33823, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33848) { // legio legio sextulus
			if (player.getInventory().containsItem(33848, 1)) {
				player.getInventory().deleteItem(33848, 1);
				player.getInventory().addItem(33824, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33849) { // queen black dragonling
			if (player.getInventory().containsItem(33849, 1)) {
				player.getInventory().deleteItem(33849, 1);
				player.getInventory().addItem(33825, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33850) { // prime hatchling
			if (player.getInventory().containsItem(33850, 1)) {
				player.getInventory().deleteItem(33850, 1);
				player.getInventory().addItem(33826, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33851) { // rex hatchling
			if (player.getInventory().containsItem(33851, 1)) {
				player.getInventory().deleteItem(33851, 1);
				player.getInventory().addItem(33827, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33852) { // supreme hatchling
			if (player.getInventory().containsItem(33852, 1)) {
				player.getInventory().deleteItem(33852, 1);
				player.getInventory().addItem(33828, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 33716) { // bombi
			if (player.getInventory().containsItem(33716, 1)) {
				player.getInventory().deleteItem(33716, 1);
				player.getInventory().addItem(33817, 1);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"As you inspect the " + item.getName() + ", it magically transforms into a creature!");
				player.setNextGraphics(new Graphics(1935));
			}
			return;
		}
		if (itemId == 29294) {
			DivinePlacing.placeDivine(player, itemId, 87285, 34107, 1, 14);
			// DivinePlacing.fucker = player;
			player.getPackets().sendGameMessage("You spawn a divine bronze rock.");
		} else if (itemId == 29492) {
			Bonds.redeem(player);
		} else if (itemId == 29295) {
			player.getPackets().sendGameMessage("You spawn a divine iron rock.");
			DivinePlacing.placeDivine(player, itemId, 87286, 57572, 15, 14);
		} else if (itemId == 29296) {
			DivinePlacing.placeDivine(player, itemId, 87287, 87266, 30, 14);
			player.getPackets().sendGameMessage("You spawn a divine coal rock.");
		} else if (itemId == 29297) {
			DivinePlacing.placeDivine(player, itemId, 87288, 87267, 55, 14);
			player.getPackets().sendGameMessage("You spawn a divine mithril rock.");
		} else if (itemId == 29298) {
			DivinePlacing.placeDivine(player, itemId, 87289, 87268, 70, 14);
			player.getPackets().sendGameMessage("You spawn a divine adamantite rock.");
		} else if (itemId == 29299) {
			DivinePlacing.placeDivine(player, itemId, 87290, 87269, 85, 14);
			player.getPackets().sendGameMessage("You spawn a divine runite rock.");
		} else if (itemId == 28600 || itemId == 28602 || itemId == 28604)
			if (player.getInventory().containsItems(new Item[] { new Item(28600), new Item(28602), new Item(28604) })) { // checks
				// inventory
				// for
				// 3
				// pieces
				player.getInventory().deleteItem(28600, 1);
				player.getInventory().deleteItem(28602, 1);
				player.getInventory().deleteItem(28604, 1);
				player.getInventory().addItem(28606, 1);
				player.getPackets().sendGameMessage("You create a Maul of Omens from your weapon pieces.");
				return;
			} else {
				player.getPackets().sendGameMessage("You need all 3 pieces of the " + "Maul to combine it."); // if
				// you
				// don't
				// have
				// 3
				// pieces
			}
		else if (itemId == 28436)
			player.getActionManager().setAction(new AscensionBoltCreation(itemId));
		// divine trees
		else if (itemId == 29304) {
			DivinePlacing.placeDivine(player, itemId, 87295, 87274, 1, 8);
			player.getPackets().sendGameMessage("You spawn a divine tree.");
		} else if (itemId == 29305) {
			DivinePlacing.placeDivine(player, itemId, 87296, 87275, 15, 8);
			player.getPackets().sendGameMessage("You spawn a divine oak tree.");
		} else if (itemId == 29306) {
			DivinePlacing.placeDivine(player, itemId, 87297, 87276, 30, 8);
			player.getPackets().sendGameMessage("You spawn a divine willow tree.");
		} else if (itemId == 29307) {
			DivinePlacing.placeDivine(player, itemId, 87298, 87277, 45, 8);
			player.getPackets().sendGameMessage("You spawn a divine maple tree.");
		} else if (itemId == 29308) {
			DivinePlacing.placeDivine(player, itemId, 87299, 87278, 60, 8);
			player.getPackets().sendGameMessage("You spawn a divine yew tree.");
		} else if (itemId == 29309) {
			DivinePlacing.placeDivine(player, itemId, 87300, 87279, 75, 8);
			player.getPackets().sendGameMessage("You spawn a divine magic tree.");
		} else if (itemId >= 27153 && itemId <= 27155) {
			if (System.currentTimeMillis() - player.delay > 500 && player.getInventory().containsItem(itemId, 1)) {
				player.delay = System.currentTimeMillis();
				int a = Utils.random(750, 8000);
				int b = Utils.random(1, 84);
				int c = player.getSkills().getTotalLevel();
				int d = itemId == 27155 ? Utils.random(35, 133)
						: itemId == 27154 ? Utils.random(11, 55) : Utils.random(4, 20);
				int amount = a + (c - b) * d;
				player.getInventory().deleteItem(itemId, 1);
				player.getMoneyPouch().setAmount(amount, false);
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You find " + Utils.format(amount) + " coins inside the " + item.getName().toLowerCase() + ".");
			}
		}
		// portables
		else if (itemId == 31041) {
			Portables.deployPortable(player, itemId, Portable.PORTABLEFORGE.getId(), Portable.PORTABLEFORGE.getId());
		} else if (itemId == 31042) {
			Portables.deployPortable(player, itemId, Portable.PORTABLERANGE.getId(), Portable.PORTABLERANGE.getId());
		} else if (itemId == 31043) {
			// Portables.deployPortable(player, itemId,
			// Portable.PORTABLESAWMILL.getId(),
			// Portable.PORTABLESAWMILL.getId());
		} else if (itemId == 31044) {
			// Portables.deployPortable(player, itemId,
			// Portable.PORTABLEWELL.getId(), Portable.PORTABLEWELL.getId());
		} else if (itemId == 25205) {
			Portables.deployPortable(player, itemId, Portable.PORTABLEBANK.getId(), Portable.PORTABLEBANK.getId());
		}
		// herbolore
		else if (itemId == 29310) {
			DivinePlacing.placeDivine(player, itemId, 87301, 87280, 9, 15);
			player.getPackets().sendGameMessage("You spawn a divine herb patch I.");
		} else if (itemId == 29311) {
			DivinePlacing.placeDivine(player, itemId, 87302, 87281, 44, 15);
			player.getPackets().sendGameMessage("You spawn a divine herb patch II.");
		} else if (itemId == 29312) {
			DivinePlacing.placeDivine(player, itemId, 87303, 87282, 67, 15);
			player.getPackets().sendGameMessage("You spawn a divine herb patch III.");
		} else if (item.getId() == Gem.OPAL.getCut())
			GemTipCutting.cut(player, GemTips.OPAL);
		else if (item.getId() == Gem.JADE.getCut())
			GemTipCutting.cut(player, GemTips.JADE);
		else if (item.getId() == Gem.RED_TOPAZ.getCut())
			GemTipCutting.cut(player, GemTips.RED_TOPAZ);
		else if (item.getId() == Gem.SAPPHIRE.getCut())
			GemTipCutting.cut(player, GemTips.SAPPHIRE);
		else if (item.getId() == Gem.EMERALD.getCut())
			GemTipCutting.cut(player, GemTips.EMERALD);
		else if (item.getId() == Gem.RUBY.getCut())
			GemTipCutting.cut(player, GemTips.RUBY);
		else if (item.getId() == Gem.DIAMOND.getCut())
			GemTipCutting.cut(player, GemTips.DIAMOND);
		else if (item.getId() == Gem.DRAGONSTONE.getCut())
			GemTipCutting.cut(player, GemTips.DRAGONSTONE);
		else if (item.getId() == Gem.ONYX.getCut())
			GemTipCutting.cut(player, GemTips.ONYX);
		else if (item.getId() == Gem.HYDRIX.getCut())
			GemTipCutting.cut(player, GemTips.HYDRIX);
		// hunting
		else if (itemId == 29300) {
			DivinePlacing.placeDivine(player, itemId, 87291, 87270, 1, 21);
			player.getPackets().sendGameMessage("You spawn a divine kebbit burrow.");
		} else if (itemId == 29301) {
			DivinePlacing.placeDivine(player, itemId, 87292, 87271, 1, 21);
			player.getPackets().sendGameMessage("You spawn a divine bird snare.");
		} else if (itemId == 29302) {
			DivinePlacing.placeDivine(player, itemId, 87293, 87272, 23, 21);
			player.getPackets().sendGameMessage("You spawn a divine deadfall trap.");
		} else if (itemId == 29303) {
			DivinePlacing.placeDivine(player, itemId, 87294, 87273, 53, 21);
			player.getPackets().sendGameMessage("You spawn a divine box trap.");
		}
		// fishing
		else if (itemId == 31080) {
			DivinePlacing.placeDivine(player, itemId, 90232, 90223, 10, 10);
			player.getPackets().sendGameMessage("You spawn a divine crayfish bubble.");
		} else if (itemId == 31081) {
			DivinePlacing.placeDivine(player, itemId, 90233, 90224, 10, 10);
			player.getPackets().sendGameMessage("You spawn a divine herring bubble.");
		} else if (itemId == 31082) {
			DivinePlacing.placeDivine(player, itemId, 90234, 90225, 20, 10);
			player.getPackets().sendGameMessage("You spawn a divine trout bubble.");
		} else if (itemId == 31083) {
			DivinePlacing.placeDivine(player, itemId, 90235, 90226, 30, 10);
			player.getPackets().sendGameMessage("You spawn a divine salmon bubble.");
		} else if (itemId == 31084) {
			DivinePlacing.placeDivine(player, itemId, 90236, 90227, 40, 10);
			player.getPackets().sendGameMessage("You spawn a divine lobster bubble.");
		} else if (itemId == 31085) {
			DivinePlacing.placeDivine(player, itemId, 90237, 90228, 50, 10);
			player.getPackets().sendGameMessage("You spawn a divine swordfish bubble.");
		} else if (itemId == 31086) {
			DivinePlacing.placeDivine(player, itemId, 90238, 90229, 76, 10);
			player.getPackets().sendGameMessage("You spawn a divine shark bubble.");
		} else if (itemId == 31087) {
			DivinePlacing.placeDivine(player, itemId, 90239, 90230, 85, 10);
			player.getPackets().sendGameMessage("You spawn a divine cavefish bubble.");
		} else if (itemId == 31088) {
			DivinePlacing.placeDivine(player, itemId, 90240, 90231, 90, 10);
			player.getPackets().sendGameMessage("You spawn a divine rocktail bubble.");
		} else if (itemId == 18339)
			player.getPackets().sendGameMessage(Coalbag.getCoal(player));
		// div not being used
		else if (itemId == 31310) {
			DivinePlacing.placeDivine(player, itemId, 66526, 66528, 65, 1);
			player.getPackets().sendGameMessage("You spawn a divine simulacrum I.");
		} else if (itemId == 31311) {
			DivinePlacing.placeDivine(player, itemId, 66529, 66531, 65, 1);
			player.getPackets().sendGameMessage("You spawn a divine simulacrum II.");
		} else if (player.getTreasureTrailsManager().useItem(item, slotId))
			return;
		else if (Consumables.eat(player, slotId, item))
			return;
		else if (AttachingOrbsDialouge.isAttachingOrb(player, item, new Item(AttachingOrbsDialouge.BATTLESTAFF)))
			return;
		else if (itemId == 2574)
			player.getTreasureTrailsManager().useSextant();
		else if (item.getId() == 20667)
			Magic.useVecnaSkull(player);
		else if (item.getId() == 25205) {
			if (!World.isTileFree(player.getPlane(), player.getX(), player.getY() - 1, 3)) {
				player.getPackets().sendGameMessage("You need clear space outside in order to place a deposit box.");
				return;
			}
			if (player.getControlerManager().getControler() != null
					&& !(player.getControlerManager().getControler() instanceof Wilderness)) {
				player.getPackets().sendGameMessage("You can't set a deposit box here.");
				return;
			}
			player.getInventory().deleteItem(slotId, item);
			player.setNextAnimation(new Animation(832));
			player.lock(1);
			World.spawnObjectTemporary(
					new WorldObject(73268, 10, 0, player.getX() + 1, player.getY(), player.getPlane()), 3600 * 1000);
		} else if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			if (itemId == 5509)
				pouch = 0;
			if (itemId == 5510)
				pouch = 1;
			if (itemId == 5512)
				pouch = 2;
			if (itemId == 5514)
				pouch = 3;
			Runecrafting.fillPouch(player, pouch);
			return;
		} else if (itemId == 952) {// spade
			dig(player);
			return;
		} else if (itemId == 10952) {
			if (Slayer.isUsingBell(player))
				return;
		} else if (HerbCleaning.clean(player, item, slotId))
			return;
		else if (TrapAction.isTrap(player, new WorldTile(player), itemId))
			return;
		else if (Bone.forId(itemId) != null) {
			Bone.bury(player, slotId);
			return;
		} else if (Magic.useTabTeleport(player, itemId))
			return;
		// else if (item.getId() == 22370)
		// Summoning.openDreadNipSelection(player);
		else if (item.getId() == 7509 || item.getId() == 7510) {
			player.setNextForceTalk(new ForceTalk("Ow! It nearly broke my tooth!"));
			player.getPackets().sendGameMessage("The rock cake resists all attempts to eat it.");
			player.applyHit(new Hit(
					player, player.getHitpoints() - 10 < 35
							? player.getHitpoints() - 35 < 0 ? 0 : player.getHitpoints() - 35 : 10,
					HitLook.REGULAR_DAMAGE));

		} else if (ItemTransportation.transportationDialogue(player, item, true))
			return;
		else if (Lamps.isSelectable(itemId) || Lamps.isSkillLamp(itemId) || Lamps.isOtherLamp(itemId))
			Lamps.processLampClick(player, slotId, itemId, item.getId() == 20935 ? 60 : 1);
		/*
		 * else if (FallenStars.isSelectable(itemId) ||
		 * FallenStars.isStarLamp(itemId) || FallenStars.isOtherStar(itemId))
		 * FallenStars.processClick(player, slotId, itemId);
		 */
		else if (LightSource.lightSource(player, slotId))
			return;
		else if (LightSource.extinguishSource(player, slotId, false))
			return;
		else if (itemId == 299) {
			if (player.withinDistance(Settings.HOME_LOCATION, 120)) {
				player.getPackets().sendGameMessage("You can't plant flowers here.");
				return;
			} else if (player.isCanPvp()) {
				player.getPackets().sendGameMessage("You cant plant a seed while doing this action.");
				return;
			} else if (World.getStandartObject(player) != null) {
				player.getPackets().sendGameMessage("You can't plant a flower here.");
				return;
			}
			player.setNextAnimation(new Animation(827));
			final WorldObject object = new WorldObject(2980 + Utils.random(8), 10, 0, player.getX(), player.getY(),
					player.getPlane());
			World.spawnObjectTemporary(object, 25000);
			player.getInventory().deleteItem(299, 1);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
						if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1))
							if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1))
								if (!player.addWalkSteps(player.getX(), player.getY() - 1, 1))
									return;
					player.getDialogueManager().startDialogue("FlowerPickD", object);
				}
			}, 2);
		} else if (itemId == 4251)
			Magic.useEctoPhial(player, item);
		else if (itemId == 18782)
			player.getDialogueManager().startDialogue("DragonkinLamp");
		else if (itemId == 15262)
			ItemSets.openSkillPack(player, itemId, 12183, 5000, 1);
		else if (itemId == 15362)
			ItemSets.openSkillPack(player, itemId, 230, 50, 1);
		else if (itemId == 15363)
			ItemSets.openSkillPack(player, itemId, 228, 50, 1);
		else if (itemId == 15364)
			ItemSets.openSkillPack(player, itemId, 222, 50, 1);
		else if (itemId == 15365)
			ItemSets.openSkillPack(player, itemId, 9979, 50, 1);
		else if (itemId == 15367)
			ItemSets.openSkillPack(player, itemId, 5419, 50, 1);
		else if (itemId == 15366)
			ItemSets.openSkillPack(player, itemId, 5377, 50, 1);
		else if (Herblore.isRawIngredient(player, itemId))
			return;
		else if (itemId == 2798 || itemId == 3565 || itemId == 3576 || itemId == 19042)
			player.getTreasureTrailsManager().openPuzzle(itemId);
		else if (itemId == 22445)
			player.getDialogueManager().startDialogue("NeemDrupeSqueeze");
		else if (itemId == 1775 || itemId == 23193)
			player.getDialogueManager().startDialogue("GlassBlowingD", itemId == 23193 ? 1 : 0);
		else if (itemId == 1775 || itemId == 32845)
			player.getDialogueManager().startDialogue("GlassBlowingD", itemId == 32845 ? 1 : 0);
		else if (itemId == 22444)
			PolyporeCreature.sprinkleOil(player, null);
		else if (itemId == 550)
			player.getInterfaceManager().sendCentralInterface(270);
		else if (itemId == AncientEffigies.SATED_ANCIENT_EFFIGY || itemId == AncientEffigies.GORGED_ANCIENT_EFFIGY
				|| itemId == AncientEffigies.NOURISHED_ANCIENT_EFFIGY
				|| itemId == AncientEffigies.STARVED_ANCIENT_EFFIGY)
			player.getDialogueManager().startDialogue("AncientEffigiesD", itemId);
		else if (itemId == 4155)
			player.getDialogueManager().startDialogue("EnchantedGemDialouge",
					player.getSlayerManager().getCurrentMaster().getNPCId());
		else if (itemId >= 23653 && itemId <= 23658)
			FightKiln.useCrystal(player, itemId);
		else if (itemId == 20124 || itemId == 20123 || itemId == 20122 || itemId == 20121)
			GodswordCreating.attachKeys(player);
		else if (itemId == 6) {
			DwarfMultiCannon.type = DwarfMultiCannon.CANNON_TYPE.NORMAL;
			DwarfMultiCannon.setUp(player);
		} else if (itemId == 20494) {
			DwarfMultiCannon.type = DwarfMultiCannon.CANNON_TYPE.GOLD;
			DwarfMultiCannon.setUp(player);
		} else if (itemId == 20498) {
			DwarfMultiCannon.type = DwarfMultiCannon.CANNON_TYPE.ROYALE;
			DwarfMultiCannon.setUp(player);
		} else if (itemId == 15707)
			player.getDungManager().openPartyInterface();
		else if (Nest.isNest(itemId))
			Nest.searchNest(player, slotId);
		else if (itemId == 14057) // broomstick
			player.setNextAnimation(new Animation(10532));
		else if (itemId == 21776) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < 77) {
				player.getPackets()
						.sendGameMessage("You need a Crafting level of at least 77 in order to combine the shards.");
				return;
			} else if (player.getInventory().containsItem(itemId, 100)) {
				player.setNextAnimation(new Animation(713));
				player.setNextGraphics(new Graphics(1383));
				player.getInventory().deleteItem(new Item(itemId, 100));
				player.getInventory().addItem(new Item(21775, 1));
				player.getSkills().addXp(Skills.CRAFTING, 150);
				player.getPackets().sendGameMessage("You combine the shards into an orb.");
			} else {
				player.getPackets()
						.sendGameMessage("You need at least 100 shards in order to create an orb of armadyl.");
			}
		} else if (itemId == 5974) {
			if (!player.getInventory().containsItemToolBelt(Smithing.HAMMER)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a hammer in order to break open a coconut.");
				return;
			}
			player.getInventory().addItem(new Item(5976, 1));
			player.getInventory().deleteItem(new Item(5974, 1));
			player.getPackets()
					.sendGameMessage("You smash the coconut with a hammer and it breaks into two symmetrical pieces.");
		} else if (itemId == 24352)
			player.getDialogueManager().startDialogue("DragonBoneUpgradeKiteInfoD");
		else if (itemId == SqirkFruitSqueeze.SqirkFruit.AUTUMM.getFruitId())
			player.getDialogueManager().startDialogue("SqirkFruitSqueeze", SqirkFruit.AUTUMM);
		else if (itemId == SqirkFruitSqueeze.SqirkFruit.SPRING.getFruitId())
			player.getDialogueManager().startDialogue("SqirkFruitSqueeze", SqirkFruit.SPRING);
		else if (itemId == SqirkFruitSqueeze.SqirkFruit.SUMMER.getFruitId())
			player.getDialogueManager().startDialogue("SqirkFruitSqueeze", SqirkFruit.SUMMER);
		else if (itemId == SqirkFruitSqueeze.SqirkFruit.WINTER.getFruitId())
			player.getDialogueManager().startDialogue("SqirkFruitSqueeze", SqirkFruit.WINTER);
		else if (item.getDefinitions().getName().startsWith("Burnt"))
			player.getDialogueManager().startDialogue("SimplePlayerMessage", "Ugh, this is inedible.");
		else if ((item.getDefinitions().containsInventoryOption(0, "Craft")
				|| item.getDefinitions().containsInventoryOption(0, "Fletch"))
				&& SkillsDialogue.selectTool(player, item.getId())) {
			return;
		}
		if (Settings.DEBUG) {
			Logger.log("ItemHandler", "Item option 1: " + itemId + ", slotId: " + slotId);
			player.getPackets().sendGameMessage("Item option 1: " + itemId + ", slotId " + slotId + ".", true);
		}
	}

	/*
	 * returns the other
	 */
	public static Item contains(int id1, Item item1, Item item2) {
		if (item1.getId() == id1)
			return item2;
		if (item2.getId() == id1)
			return item1;
		return null;
	}

	public static boolean contains(int id1, int id2, Item... items) {
		boolean containsId1 = false;
		boolean containsId2 = false;
		for (Item item : items) {
			if (item.getId() == id1)
				containsId1 = true;
			else if (item.getId() == id2)
				containsId2 = true;
		}
		return containsId1 && containsId2;
	}

	public static void handleInterfaceOnInterface(final Player player, InputStream stream) {

		int usedWithId = stream.readUnsignedShortLE();
		int interface1 = stream.readInt();
		int fromSlot = stream.readUnsignedShort128();
		int itemUsedId = stream.readUnsignedShortLE();
		int toSlot = stream.readUnsignedShortLE();
		int interface2 = stream.readInt();

		int interfaceId = interface1 >> 16;
		int interfaceComponent = interface1 - (interfaceId << 16);
		int interfaceId2 = interface2 >> 16;
		@SuppressWarnings("unused")
		int interface2Component = interface2 - (interfaceId2 << 16);

		if (Settings.DEBUG)
			Logger.log("ItemHandler", "ItemOnItem " + usedWithId + ", " + toSlot + ", " + interfaceId + ", "
					+ interfaceComponent + ", " + fromSlot + ", " + itemUsedId);

		if (player.isLocked() || player.isStunned() || player.getEmotesManager().isDoingEmote())
			return;
		player.stopAll();
		if (interfaceId == 1430 && interfaceComponent >= 55 && interfaceComponent <= 229
				&& (interfaceId2 == Inventory.INVENTORY_INTERFACE || interfaceId2 == Inventory.INVENTORY_INTERFACE_2)
				&& !player.getInterfaceManager().containsInventoryInter()) {
			Item item = player.getInventory().getItem(toSlot);
			if (item == null || item.getId() != usedWithId)
				return;
			player.getActionbar().pushShortcutOnSomething((interfaceComponent - 55) / 13, item);
			return;
		}
		if ((interfaceId == 747 || interfaceId == 662)
				&& (interfaceId2 == Inventory.INVENTORY_INTERFACE || interfaceId2 == Inventory.INVENTORY_INTERFACE_2)) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.ITEM) {
					if (player.getFamiliar().hasSpecialOn())
						player.getFamiliar().submitSpecial(toSlot);
				}
			}
			return;
		}
		if ((interfaceId == Inventory.INVENTORY_INTERFACE || interfaceId == Inventory.INVENTORY_INTERFACE_2)
				&& interfaceId == interfaceId2 && !player.getInterfaceManager().containsInventoryInter()) {
			if (toSlot >= 28 || fromSlot >= 28 || toSlot == fromSlot)
				return;
			Item usedWith = player.getInventory().getItem(toSlot);
			Item itemUsed = player.getInventory().getItem(fromSlot);
			if (itemUsed == null || usedWith == null || itemUsed.getId() != itemUsedId
					|| usedWith.getId() != usedWithId)
				return;
			if (!player.getControlerManager().canUseItemOnItem(itemUsed, usedWith))
				return;
			FletchData fletch = Fletching.isFletchingCombination(usedWith, itemUsed);
			if (fletch != null) {
				player.getDialogueManager().startDialogue("FletchingD", fletch);
				return;
			}
			int herblore = Herblore.isHerbloreSkill(itemUsed, usedWith);
			if (herblore > -1) {
				player.getDialogueManager().startDialogue("HerbloreD", herblore, itemUsed, usedWith);
				return;
			}
			int leatherIndex = LeatherCraftingD.getIndex(itemUsedId) == -1 ? LeatherCraftingD.getIndex(usedWithId)
					: LeatherCraftingD.getIndex(itemUsedId);
			if (leatherIndex != -1 && ((itemUsedId == 1733 || usedWithId == 1733)
					|| LeatherCraftingD.isExtraItem(usedWithId) || LeatherCraftingD.isExtraItem(itemUsedId))) {
				player.getDialogueManager().startDialogue("LeatherCraftingD", leatherIndex, false);
				return;
			}
			Combinations combination = Combinations.isCombining(itemUsedId, usedWithId);
			if (combination != null) {
				player.getDialogueManager().startDialogue("CombinationsD", combination);
				return;
			} else if (Firemaking.isFiremaking(player, itemUsed, usedWith))
				return;
			else if (OrnamentKits.attachKit(player, itemUsed, usedWith, fromSlot, toSlot))
				return;
			else if (AmuletAttaching.isAttaching(itemUsedId, usedWithId))
				player.getDialogueManager().startDialogue("AmuletAttaching");
			else if (GemCutting.isCutting(player, itemUsed, usedWith))
				return;
			else if (AttachingOrbsDialouge.isAttachingOrb(player, itemUsed, usedWith))
				return;
			else if (TreeSaplings.hasSaplingRequest(player, itemUsedId, usedWithId)) {
				if (itemUsedId == 5354)
					TreeSaplings.plantSeed(player, usedWithId, fromSlot);
				else
					TreeSaplings.plantSeed(player, itemUsedId, toSlot);
			} else if (Drinkables.mixPot(player, itemUsed, usedWith, fromSlot, toSlot, true) != -1)
				return;
			else if (WeaponPoison.poison(player, itemUsed, usedWith, false))
				return;
			else if (PrayerBooks.isGodBook(itemUsedId, false) || PrayerBooks.isGodBook(usedWithId, false)) {
				PrayerBooks.bindPages(player, itemUsed.getName().contains(" page ") ? usedWithId : itemUsedId);
			} else if (contains(22498, 554, itemUsed, usedWith) || contains(22498, 22448, itemUsed, usedWith)) {
				if (player.getSkills().getLevel(Skills.FARMING) < 80) {
					player.getPackets()
							.sendGameMessage("You need a Farming level of 80 in order to make a polypore staff.");
					return;
				} else if (!player.getInventory().containsItem(22448, 3000)) {
					player.getPackets()
							.sendGameMessage("You need 3,000 polypore spores in order to make a polypore staff.");
					return;
				} else if (!player.getInventory().containsItem(554, 15000)) {
					player.getPackets()
							.sendGameMessage("You need 15,000 fire runes in order to make a polypore staff.");
					return;
				}
				player.setNextAnimation(new Animation(15434));
				player.lock(2);
				player.getInventory().deleteItem(554, 15000);
				player.getInventory().deleteItem(22448, 3000);
				player.getInventory().deleteItem(22498, 1);
				player.getInventory().addItem(22494, 1);
				player.getPackets().sendGameMessage(
						"You attach the polypore spores and infuse the fire runes to the stick in order to create a staff.");
			} else if (contains(22496, 22448, itemUsed, usedWith)) {
				if (player.getSkills().getLevel(Skills.FARMING) < 80) {
					player.getPackets()
							.sendGameMessage("You need a Farming level of 80 in order to recharge polypore staff.");
					return;
				}
				int charges = 3000 - player.getCharges().getCharges(22496);
				if (!player.getInventory().containsItem(22448, charges)) {
					player.getPackets().sendGameMessage(
							"You need " + charges + " polypore spores in order to recharge polypore staff.");
					return;
				}
				player.setNextAnimation(new Animation(15434));
				player.lock(2);
				player.getInventory().deleteItem(22448, charges);
				player.getInventory().deleteItem(22496, 1);
				player.getCharges().resetCharges(22496);
				player.getInventory().addItem(22494, 1);
				player.getPackets().sendGameMessage("You attach the polypore spores to the staff.");
			} else if (Colourables.forItem(itemUsedId, usedWithId) != null) {
			       if (ItemColouring.ApplyDyeToItems(player, itemUsedId, usedWithId)) {
			        return;
			       }
			      }
			      if (Colourables.forProduct(itemUsedId) != null) {
			       if (ItemColouring.RemoveDyeFromItems(player, itemUsedId, usedWithId)) {
			        return;
			       }
			      } else if (Colourables.forProduct(usedWithId) != null) {
			       if (ItemColouring.RemoveDyeFromItems(player, usedWithId, itemUsedId)) {
			        return;
			       }
			} else if (contains(11710, 11712, itemUsed, usedWith) || contains(11710, 11714, itemUsed, usedWith)
					|| contains(11712, 11714, itemUsed, usedWith))
				GodswordCreating.joinPieces(player, false);
			else if (Slayer.createSlayerHelmet(player, itemUsedId, usedWithId))
				return;
			else if (itemUsedId == 23191 || usedWithId == 23191) {
				Drink pot = Drinkables.getDrink(itemUsedId == 23191 ? usedWithId : itemUsedId);
				if (pot == null)
					return;
				player.getDialogueManager().startDialogue("FlaskDecantingD", pot);
			} else if (itemUsedId == 30372) {
				if (usedWith.getDefinitions().isNoted()) {
					player.getPackets().sendGameMessage("You can't note a note.");
					return;
				}
				if (usedWith.getDefinitions().isDungeoneeringItem()) {
					player.getPackets().sendGameMessage("You can't note this item.");
					return;
				}
				if (usedWith.getDefinitions().isLended()) {
					player.getPackets().sendGameMessage("You can't note this item.");
					return;
				}
				if (usedWith.getDefinitions().isStackable()) {
					player.getPackets().sendGameMessage("You can't note this item.");
					return;
				}
				if (!ItemDefinitions.getItemDefinitions(usedWithId + 1).isNoted()) {
					player.getPackets().sendGameMessage("You can't note this item.");
					return;
				}
				if (ItemDefinitions.getItemDefinitions(usedWithId + 1).getId() > Utils.getItemDefinitionsSize()) {
					player.getPackets().sendGameMessage("That item id is not supported in the cache.");
					return;
				}
				if (player.getInventory().containsItem(30372, 1) && player.getInventory().containsItem(usedWithId, 1)) {
					player.getInventory().deleteItem(30372, 1);
					player.getInventory().deleteItem(usedWithId, 1);
					player.getInventory().addItem(usedWithId + 1, 1);
				}
			} else if (usedWithId == 30372) {
				if (itemUsed.getDefinitions().isNoted()) {
					player.getPackets().sendGameMessage("You can't note a note.");
					return;
				}
				if (itemUsed.getDefinitions().isDungeoneeringItem()) {
					player.getPackets().sendGameMessage("You can't note this item.");
					return;
				}
				if (itemUsed.getDefinitions().isLended()) {
					player.getPackets().sendGameMessage("You can't note this item.");
					return;
				}
				if (itemUsed.getDefinitions().isStackable()) {
					player.getPackets().sendGameMessage("You can't note this item.");
					return;
				}
				if (!ItemDefinitions.getItemDefinitions(itemUsedId + 1).isNoted()) {
					player.getPackets().sendGameMessage("You can't note this item.");
					return;
				}
				if (ItemDefinitions.getItemDefinitions(itemUsedId + 1).getId() > Utils.getItemDefinitionsSize()) {
					player.getPackets().sendGameMessage("That item id is not supported in the cache.");
					return;
				}
				if (player.getInventory().containsItem(30372, 1) && player.getInventory().containsItem(itemUsedId, 1)) {
					player.getInventory().deleteItem(30372, 1);
					player.getInventory().deleteItem(itemUsedId, 1);
					player.getInventory().addItem(itemUsedId + 1, 1);
				}
			} else if (itemUsedId == 18339 || itemUsedId == 453) {
				Coalbag.addCoal(player);
			} else if (itemUsedId == 18339 || itemUsedId == 454) {
				player.getPackets().sendGameMessage("You can't fill in with noted coals!");
			} else if (itemUsedId == 27068 && usedWithId == 11716) {
				if (player.getInventory().containsItem(27068, 10) && player.getInventory().containsItem(11716, 1)) {
					player.getInventory().deleteItem(27068, 10);
					player.getInventory().deleteItem(11716, 1);
					player.getInventory().addItem(31463, 1);
					player.getPackets().sendGameMessage("You successfully create a chaotic spear.");
				} else {
					player.getPackets().sendGameMessage(
							"You need 10 chaotic spikes and a zamorakian spear to create a chaotic spear.");
				}
			} else if (usedWithId == 27068 && itemUsedId == 11716) {
				if (player.getInventory().containsItem(27068, 10) && player.getInventory().containsItem(11716, 1)) {
					player.getInventory().deleteItem(27068, 10);
					player.getInventory().deleteItem(11716, 1);
					player.getInventory().addItem(31463, 1);
				} else {
					player.getPackets().sendGameMessage(
							"You need 10 chaotic spikes and a zamorakian spear to create a chaotic spear.");
				}
			} else if (itemUsedId == 5733) {
				if (player.getRights() < 2) {
					player.getInventory().deleteItem(5733, 28);
					return;
				}
				player.getInventory().deleteItem(usedWithId, 1);
				player.getPackets()
						.sendGameMessage("Oi - The Rotten Potato ate the " + usedWith.getDefinitions().getName() + "!");
			} else if (usedWithId == 5733) {
				if (player.getRights() < 2) {
					player.getInventory().deleteItem(5733, 28);
					return;
				}
				player.getInventory().deleteItem(itemUsedId, 1);
				player.getPackets()
						.sendGameMessage("Oi - The Rotten Potato ate the " + itemUsed.getDefinitions().getName() + "!");
			} else if (usedWithId == 20767 && itemUsedId == 20768) {
				player.getInventory().deleteItem(20767, 1);
				player.getInventory().deleteItem(20768, 1);
				player.getInventory().addItem(32151, 1);
				player.getPackets().sendGameMessage("You attach your hood to your cape.");
			} else if (itemUsedId == 20767 && usedWithId == 20768) {
				player.getInventory().deleteItem(20767, 1);
				player.getInventory().deleteItem(20768, 1);
				player.getInventory().addItem(32151, 1);
				player.getPackets().sendGameMessage("You attach your hood to your cape.");
			} else if (usedWithId == 20769 && itemUsedId == 20770) {
				player.getInventory().deleteItem(20769, 1);
				player.getInventory().deleteItem(20770, 1);
				player.getInventory().addItem(32152, 1);
				player.getPackets().sendGameMessage("You attach your hood to your cape.");
			} else if (itemUsedId == 20769 && usedWithId == 20770) {
				player.getInventory().deleteItem(20769, 1);
				player.getInventory().deleteItem(20770, 1);
				player.getInventory().addItem(32152, 1);
				player.getPackets().sendGameMessage("You attach your hood to your cape.");
			} else if (usedWithId == 20771 && itemUsedId == 20772) {
				player.getInventory().deleteItem(20771, 1);
				player.getInventory().deleteItem(20772, 1);
				player.getInventory().addItem(32153, 1);
				player.getPackets().sendGameMessage("You attach your hood to your cape.");
			} else if (itemUsedId == 20771 && usedWithId == 20772) {
				player.getInventory().deleteItem(20771, 1);
				player.getInventory().deleteItem(20772, 1);
				player.getInventory().addItem(32153, 1);
				player.getPackets().sendGameMessage("You attach your hood to your cape.");
			} else if (contains(11690, 11702, itemUsed, usedWith))
				GodswordCreating.attachHilt(player, 0);
			else if (contains(11690, 11704, itemUsed, usedWith))
				GodswordCreating.attachHilt(player, 1);
			else if (contains(11690, 11706, itemUsed, usedWith))
				GodswordCreating.attachHilt(player, 2);
			else if (contains(11690, 11708, itemUsed, usedWith))
				GodswordCreating.attachHilt(player, 3);
			else if (contains(SpiritshieldCreating.HOLY_ELIXIR, SpiritshieldCreating.SPIRIT_SHIELD, itemUsed, usedWith))
				player.getPackets().sendGameMessage("The shield must be blessed at an altar.");
			else if (contains(SpiritshieldCreating.SPIRIT_SHIELD, 13746, itemUsed, usedWith)
					|| contains(SpiritshieldCreating.SPIRIT_SHIELD, 13748, itemUsed, usedWith)
					|| contains(SpiritshieldCreating.SPIRIT_SHIELD, 13750, itemUsed, usedWith)
					|| contains(SpiritshieldCreating.SPIRIT_SHIELD, 13752, itemUsed, usedWith))
				player.getPackets().sendGameMessage("You need a blessed spirit shield to attach the sigil to.");
			else if (contains(SqirkFruitSqueeze.SqirkFruit.AUTUMM.getFruitId(), Herblore.PESTLE_AND_MORTAR, itemUsed,
					usedWith))
				player.getDialogueManager().startDialogue("SqirkFruitSqueeze", SqirkFruit.AUTUMM);
			else if (contains(SqirkFruitSqueeze.SqirkFruit.SPRING.getFruitId(), Herblore.PESTLE_AND_MORTAR, itemUsed,
					usedWith))
				player.getDialogueManager().startDialogue("SqirkFruitSqueeze", SqirkFruit.SPRING);
			else if (contains(SqirkFruitSqueeze.SqirkFruit.SUMMER.getFruitId(), Herblore.PESTLE_AND_MORTAR, itemUsed,
					usedWith))
				player.getDialogueManager().startDialogue("SqirkFruitSqueeze", SqirkFruit.SUMMER);
			else if (contains(SqirkFruitSqueeze.SqirkFruit.WINTER.getFruitId(), Herblore.PESTLE_AND_MORTAR, itemUsed,
					usedWith))
				player.getDialogueManager().startDialogue("SqirkFruitSqueeze", SqirkFruit.WINTER);
			else if (contains(5976, 229, itemUsed, usedWith)) {
				player.getInventory().deleteItem(new Item(5976, 1));
				player.getInventory().deleteItem(new Item(229, 1));
				player.getInventory().addItem(new Item(Herblore.COCONUT_MILK, 1));
				player.getInventory().addItem(new Item(5978, 1));
				player.getPackets().sendGameMessage("You pour the milk of the coconut into a vial.");
			} else if (contains(4151, 21369, itemUsed, usedWith)) {
				if (!player.getSkills().hasRequiriments(Skills.ATTACK, 75, Skills.SLAYER, 80)) {
					player.getPackets().sendGameMessage(
							"You need an attack level of 75 and slayer level of 80 in order to attach the whip vine to the whip.");
					return;
				}
				player.getInventory().replaceItem(21371, 1, toSlot);
				player.getInventory().deleteItem(fromSlot, itemUsed);
				player.getPackets().sendGameMessage("You attach the whip vine to the abbysal whip.");
			} else if (contains(985, 987, itemUsed, usedWith)) { // crystal key
				// make
				player.getInventory().deleteItem(toSlot, usedWith);
				itemUsed.setId(989);
				player.getInventory().refresh(fromSlot);
				player.getPackets().sendGameMessage("You join the two halves of the key together.");
			} else
				player.getPackets().sendGameMessage("Nothing interesting happens.");
			if (Settings.DEBUG)
				Logger.log("ItemHandler", "Used:" + itemUsed.getId() + ", With:" + usedWith.getId());
		} else if ((interfaceId == 1461 && interfaceComponent == 1)
				&& (interfaceId2 == Inventory.INVENTORY_INTERFACE || interfaceId2 == Inventory.INVENTORY_INTERFACE_2)
				&& !player.getInterfaceManager().containsInventoryInter()) {
			if (toSlot >= 28)
				return;
			Item item = player.getInventory().getItem(toSlot);
			if (item == null || item.getId() != usedWithId)
				return;
			player.getActionbar().useAbility(new MagicAbilityShortcut(fromSlot), item);
			// Magic.handleSpellOnItem(player, fromSlot, (byte) toSlot);
		}
	}

	public static void handleItemOption3(Player player, int slotId, int itemId, Item item) {
		if (player.isLocked() || player.isStunned() || player.getEmotesManager().isDoingEmote())
			return;
		player.stopAll(false);
		FlyingEntities impJar = FlyingEntities.forItem((short) itemId);
		if (impJar != null)
			FlyingEntityHunter.openJar(player, impJar, slotId);
		if (LightSource.lightSource(player, slotId))
			return;
		if (OrnamentKits.splitKit(player, item))
			return;
		if (ExplorerRing.handleOption(player, item, slotId, 3))
			return;
		if (item.getDefinitions().isBindItem())
			player.getDungManager().bind(item, slotId);
		else if (itemId >= 11095 && itemId <= 11103 && (itemId & 0x1) != 0)
			Revenant.useForinthryBrace(player, item, slotId);
		else if (itemId == 19748)
			renewSummoningPoints(player);
		else if (itemId >= 13281 && itemId <= 13288)
			player.getSlayerManager().checkKillsLeft();
		else if (itemId == 15707)
			Magic.sendTeleportSpell(player, 13652, 13654, 2602, 2603, 1, 0, new WorldTile(3447, 3694, 0), 10, true,
					Magic.ITEM_TELEPORT);
		else if (itemId == 32151 || itemId == 32152 || itemId == 32153 || itemId == 20767 || itemId == 20769
				|| itemId == 20771)
			SkillCapeCustomizer.startCustomizing(player, itemId);
		else if (itemId == 18339) {
			player.getPackets().sendInputLongTextScript("Enter amount to withdraw (withdrawn as notes)");
			player.getTemporaryAttributtes().put("coalWithdraw", Boolean.TRUE);
		} else if (itemId == 24437 || itemId == 24439 || itemId == 24440 || itemId == 24441)
			player.getDialogueManager().startDialogue("FlamingSkull", item, slotId);
		else if (Equipment.getItemSlot(itemId) == Equipment.SLOT_AURA)
			player.getAuraManager().sendTimeRemaining(itemId);
		else if (itemId == 20709) {
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2968, 3285, 0));
		} else if (PrayerBooks.isGodBook(itemId, true))
			PrayerBooks.sermanize(player, itemId);
		else if (itemId == 28606) {// Finish him!
			if (VoragoHandler.vorago.pushBackDamage <= VoragoHandler.vorago.startPushBack / 10) {
				if (Utils.isOnRange(VoragoHandler.vorago, player, 1)) {

					Vorago n = VoragoHandler.vorago;
					n.setCantInteract(true);
					WorldTasksManager.schedule(new WorldTask() {
						int count = 0;

						@Override
						public void run() {

							if (count == 1) {
								n.getCombat().removeTarget();
								n.canDie = true;
							}
							if (count == 2) {
								player.faceEntity(n);
							}
							if (count == 3) {
								player.setNextAnimation(new Animation(20387));
								n.sendDeath(player);
								player.getInventory().removeItems(item);
							}
							count++;
						}
					}, 0, 0);
				} else {
					player.getPackets().sendGameMessage("You need to be closer to Vorago");// TODO
					// find
					// act
					// message
				}
			} else {
				player.getPackets().sendGameMessage("Vorago isn't in the right place");// TODO
				// find
				// act
				// message
			}
		} else if (itemId == 27477)
			player.getDialogueManager().startDialogue("SixthAgeCircuit");
		else if (itemId == 22332) {
			Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(3098, 3162, 3));
		} else if (itemId == 21371) {
			player.getInventory().replaceItem(4151, 1, slotId);
			player.getInventory().addItem(21369, 1);
			player.getPackets().sendGameMessage("You split the whip vine from the abbysal whip.");
		} else if (itemId == 4155) {
			player.getInterfaceManager().sendCentralInterface(1309);
			player.getPackets().sendIComponentText(1309, 37, "List Co-Op Partner");
		} else if (itemId == 11694 || itemId == 11696 || itemId == 11698 || itemId == 11700)
			GodswordCreating.dismantleGS(player, item, slotId);
		else if (itemId == 23044 || itemId == 23045 || itemId == 23046 || itemId == 23047)
			player.getDialogueManager().startDialogue("MindSpikeD", itemId, slotId);
		else if (item.getDefinitions().containsOption("Teleport")
				&& ItemTransportation.transportationDialogue(player, item, true))
			return;
		else if (player.getCharges().checkCharges(item))
			return;
		if (Settings.DEBUG) {
			Logger.log("ItemHandler", "Item option 3: " + itemId + ", slotId: " + slotId);
			player.getPackets().sendGameMessage("Item option 3: " + itemId + ", slotId " + slotId + ".", true);
		}
	}

	private static void renewSummoningPoints(Player player) {
		int summonLevel = player.getSkills().getLevelForXp(Skills.SUMMONING);
		if (player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
			player.lock(3);
			player.setNextAnimation(new Animation(8502));
			player.setNextGraphics(new Graphics(1308));
			player.getSkills().set(Skills.SUMMONING, summonLevel);
			player.getPackets().sendGameMessage("You have recharged your Summoning points.", true);
		} else
			player.getPackets().sendGameMessage("You already have full Summoning points.");
	}

	public static void handleItemOption4(Player player, int slotId, int itemId, Item item) {
		if (ExplorerRing.handleOption(player, item, slotId, 4))
			return;
		if (Settings.DEBUG) {
			Logger.log("ItemHandler", "Item option 4: " + itemId + ", slotId: " + slotId);
			player.getPackets().sendGameMessage("Item option 4: " + itemId + ", slotId " + slotId + ".", true);
		}
	}

	public static void handleItemOption5(Player player, int slotId, int itemId, Item item) {
		if (ExplorerRing.handleOption(player, item, slotId, 5))
			return;
		if (Settings.DEBUG) {
			Logger.log("ItemHandler", "Item option 5: " + itemId + ", slotId: " + slotId);
			player.getPackets().sendGameMessage("Item option 5: " + itemId + ", slotId " + slotId + ".", true);
		}
	}

	public static void handleItemOption6(Player player, int slot, int itemId, Item item) {
		if (player.isLocked() || player.isStunned() || player.getEmotesManager().isDoingEmote())
			return;
		player.stopAll(false);
		switch (itemId) {
		case 15345:
			if (player.isLocked() || player.getControlerManager().getControler() != null) {
				player.getPackets().sendGameMessage("You cannot tele anywhere from here.");
				return;
			}
			player.getDialogueManager().startDialogue("ArdougneCloak", false);
			break;
		case 15347:
		case 15349:
		case 19748:
			if (player.isLocked() || player.getControlerManager().getControler() != null) {
				player.getPackets().sendGameMessage("You cannot tele anywhere from here.");
				return;
			}
			player.getDialogueManager().startDialogue("ArdougneCloak", true);
			break;
		}
		if (player.getToolbelt().addItem(slot, item))
			return;
		if ((item.getDefinitions().containsOption("Rub") || item.getDefinitions().containsOption("Cabbage-port"))
				&& ItemTransportation.transportationDialogue(player, item, true))
			return;
		else if (Drinkables.emptyPot(player, item, slot))
			return;
		if (ExplorerRing.handleOption(player, item, slot, 6))
			return;
		else if (item.getDefinitions().isBindItem())
			player.getDungManager().bind(item, slot);
		else if (itemId == 20767 || itemId == 32151)
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2276, 3339, 1));
		else if (itemId == 995) {
			if (player.isCanPvp()) {
				player.getPackets()
						.sendGameMessage("You cannot acess your money pouch within a player-vs-player zone.");
				return;
			}
			player.getMoneyPouch().sendDynamicInteraction(item.getAmount(), false, MoneyPouch.TYPE_POUCH_INVENTORY);
		} else if (itemId == 1438)
			Runecrafting.locate(player, 3127, 3405);
		else if (itemId == 1440)
			Runecrafting.locate(player, 3306, 3474);
		else if (itemId == 1442)
			Runecrafting.locate(player, 3313, 3255);
		else if (itemId == 1444)
			Runecrafting.locate(player, 3185, 3165);
		else if (itemId == 1446)
			Runecrafting.locate(player, 3053, 3445);
		else if (itemId == 1448)
			Runecrafting.locate(player, 2982, 3514);
		else if (itemId == 1458)
			Runecrafting.locate(player, 2858, 3381);
		else if (itemId == 1454)
			Runecrafting.locate(player, 2408, 4377);
		else if (itemId == 1452)
			Runecrafting.locate(player, 3060, 3591);
		else if (itemId == 1462)
			Runecrafting.locate(player, 2872, 3020);
		else if (itemId == 14057)
			SorceressGarden.teleportToSocreressGarden(player, true);
		else if (itemId == 18339)
			Coalbag.addCoal(player);
		else if (itemId == 21776) {
			if (Herblore.isRawIngredient(player, item.getId()))
				return;
		} else if (itemId == 11283)
			DragonfireShield.empty(player);
		else if (itemId == 15492 || itemId == 13263)
			Slayer.dissasembleSlayerHelmet(player, itemId == 15492);
		else if (Slayer.isBlackMask(itemId)) {
			player.getInventory().replaceItem(8921, 1, slot);
			player.getPackets().sendGameMessage("You remove all the charges from the black mask.");
		} else
			player.getPackets().sendGameMessage("Nothing interesting happens.");
	}

	public static void handleItemOption7(Player player, int slotId, int itemId, Item item) {
		if (player.isLocked() || player.isStunned() || player.getEmotesManager().isDoingEmote())
			return;
		if (!player.getControlerManager().canDropItem(item))
			return;
		player.stopAll(false);
		if (item.getDefinitions().isDestroyItem() && item.getId() != 30372) {
			player.getDialogueManager().startDialogue("DestroyItemOption", slotId, item);
			return;
		}
		if (GrandExchange.getPrice(item.getId()) >= 1000000) {
			player.getDialogueManager().startDialogue("HighValueItemOption", item);
			return;
		}
		if (player.getPetManager().spawnPet(itemId, true))
			return;
		if (item.getId() == 707 || item.getId() == 703) {
			player.setNextForceTalk(new ForceTalk("Ow! The " + item.getName().toLowerCase() + " exploded!"));
			int damage = item.getId() == 703 ? 350 : 650;
			player.applyHit(new Hit(player,
					player.getHitpoints() - damage < 35
							? player.getHitpoints() - 35 < 0 ? 0 : player.getHitpoints() - 35 : damage,
					HitLook.REGULAR_DAMAGE));
			player.setNextAnimation(new Animation(827));
			player.setNextGraphics(new Graphics(954));
			player.getInventory().deleteItem(slotId, item);
			return;
		}

		player.getInventory().deleteItem(slotId, item);
		if (player.getCharges().degradeCompletly(item))
			return;
		if (player.getRights() >= 2) {
			return;
		}
		if (player.isBeginningAccount()) {
			World.addGroundItem(item, new WorldTile(player), player, true, 60, 2, 0);
		} else if (player.getControlerManager().getControler() instanceof Wilderness && ItemConstants.isTradeable(item))
			World.addGroundItem(item, new WorldTile(player), player, false, -1);
		else if (System.currentTimeMillis() - player.lastDrop > 500) {
			World.addGroundItem(item, new WorldTile(player), player, true, 60);
			Logger.globalLog(player.getUsername(), player.getSession().getIP(),
					new String(" has dropped item [ id: " + item.getId() + ", amount: " + item.getAmount() + " ]."));
		}
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			final String FILE_PATH = "data/logs/dropped/";
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + player.getUsername() + ".txt", true));
			writer.newLine();
			writer.write(
					"[" + dateFormat.format(cal.getTime()) + ", IP: " + player.getSession().getIP() + "] " + "Player: "
							+ player.getUsername() + " - Dropped: " + item.getName() + " - Amount: " + item.getAmount()
							+ " at: " + player.getX() + ", " + player.getY() + ", " + player.getPlane() + "");
			writer.flush();
			writer.close();
		} catch (IOException er) {
			System.out.println("Error picking up.");
		}
	}

	public static void handleItemOption8(Player player, int slotId, int itemId, Item item) {
		if (itemId >= 15084 && itemId <= 15100)
			Dicing.handleRoll(player, itemId, true);
		player.getInventory().sendExamine(slotId);
	}
}