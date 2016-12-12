package net.kagani.game.player.controllers;

import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.FlyingEntityHunter;
import net.kagani.game.player.content.FlyingEntityHunter.DynamicFormula;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Falconry extends Controller {

	public int[] xp = { 103, 132, 156 };
	public int[] furRewards = { 10125, 10115, 10127 };
	public int[] levels = { 43, 57, 69 };

	public static void beginFalconry(Player player) {
		try {
			if ((player.getEquipment().getItem(3) != null && player
					.getEquipment().getItem(3).getId() == -1)
					|| (player.getEquipment().getItem(5) != null && player
							.getEquipment().getItem(5).getId() == -1)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need both hands free to use a falcon.");
				return;
			} else if (player.getSkills().getLevel(Skills.HUNTER) < 43) {
				player.getDialogueManager()
						.startDialogue("SimpleMessage",
								"You need a Hunter level of at least 43 to use a falcon, come back later.");
				return;
			}
			player.getControlerManager().startControler("Falconry");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("beginFalconry");
		}
	}

	@Override
	public void start() {
		try {
			player.setNextAnimation(new Animation(1560));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(new WorldTile(2371, 3619, 0));
				}
			});
			player.getEquipment().getItems().set(3, new Item(10024, 1));
			player.getEquipment().refresh(3);
			player.getAppearence().generateAppearenceData();
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Simply click on the target and try your luck.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("start");
		}
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (slotId == 3 || slotId == 5)
			return false;
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControlerManager().forceStop();
	}

	@Override
	public void forceClose() {
		try {
			player.getEquipment().getItems().set(3, new Item(-1, 1));
			player.getEquipment().refresh(3);
			player.getInventory().deleteItem(10024, Integer.MAX_VALUE);
			player.setNextAnimation(new Animation(-1));
			player.getAppearence().generateAppearenceData();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("forceClose");
		}
	}

	@Override
	public boolean processNPCClick1(final NPC npc) {
		try {
			player.setNextFaceEntity(npc);
			if (npc.getDefinitions().getName().toLowerCase().contains("kebbit")) {
				if (player.getTemporaryAttributtes().get("falconReleased") != null) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You cannot catch a kebbit without your falcon.");
					return false;
				}
				int level = levels[(npc.getId() - 5098)];
				if (proccessFalconAttack(npc)) {
					if (player.getSkills().getLevel(Skills.HUNTER) < level) {
						player.getDialogueManager().startDialogue(
								"SimpleMessage",
								"You need a Hunter level of " + level
										+ " to capture this kebbit.");
						return true;
					} else if (FlyingEntityHunter.isSuccessful(player, level,
							new DynamicFormula() {
								@Override
								public int getExtraProbablity(Player player) {
									if (player.getEquipment().getGlovesId() == 10075)
										return 3;
									return 1;
								}
							})) {
						player.getEquipment().getItems()
								.set(3, new Item(10023, 1));
						player.getEquipment().refresh(3);
						player.getAppearence().generateAppearenceData();
						player.getTemporaryAttributtes().put("falconReleased",
								true);
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								World.sendProjectile(player, npc, 918, 41, 16,
										31, 35, 16, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										npc.setNextNPCTransformation(npc
												.getId() - 4);
										player.getTemporaryAttributtes().put(
												"ownedFalcon", npc);
										player.getPackets()
												.sendGameMessage(
														"The falcon successfully swoops down and captures the kebbit.");
										player.getHintIconsManager()
												.addHintIcon(npc, 1, -1, false);
									}
								});
							}
						});
					} else {
						player.getEquipment().getItems()
								.set(3, new Item(10023, 1));
						player.getEquipment().refresh(3);
						player.getAppearence().generateAppearenceData();
						player.getTemporaryAttributtes().put("falconReleased",
								true);
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								World.sendProjectile(player, npc, 918, 41, 16,
										31, 35, 16, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										World.sendProjectile(npc, player, 918,
												41, 16, 31, 35, 16, 0);
										WorldTasksManager
												.schedule(new WorldTask() {
													@Override
													public void run() {
														player.getEquipment()
																.getItems()
																.set(3,
																		new Item(
																				10024,
																				1));
														player.getEquipment()
																.refresh(3);
														player.getAppearence()
																.generateAppearenceData();
														player.getTemporaryAttributtes()
																.remove("falconReleased");
														player.getPackets()
																.sendGameMessage(
																		"The falcon swoops down on the kebbit, but just barely misses catching it.");
													}
												});
									}
								}, Utils.getDistance(player, npc) > 3 ? 2 : 1);
							}
						});
					}
				}
				return false;
			} else if (npc.getDefinitions().getName().toLowerCase()
					.contains("gyr falcon")) {
				NPC kill = (NPC) player.getTemporaryAttributtes().get(
						"ownedFalcon");
				if (kill == null)
					return false;
				if (kill != npc) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"This isn't your kill!");
					return false;
				}
				npc.setNextNPCTransformation(npc.getId() + 4);
				npc.setRespawnTask();
				player.getInventory().addItem(
						new Item(furRewards[(npc.getId() - 5094)], 1));
				player.getInventory().addItem(new Item(526, 1));
				player.getSkills().addXp(Skills.HUNTER,
						xp[(npc.getId() - 5094)]);
				player.getPackets()
						.sendGameMessage(
								"You retreive the falcon as well as the fur of the dead kebbit.");
				player.getHintIconsManager().removeUnsavedHintIcon();
				player.getEquipment().getItems().set(3, new Item(10024, 1));
				player.getEquipment().refresh(3);
				player.getAppearence().generateAppearenceData();
				player.getTemporaryAttributtes().remove("ownedFalcon");
				player.getTemporaryAttributtes().remove("falconReleased");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("processNPCClick1");
		}
		return true;
	}

	private boolean proccessFalconAttack(NPC target) {
		try {
			int distanceX = player.getX() - target.getX();
			int distanceY = player.getY() - target.getY();
			int size = player.getSize();
			int maxDistance = 16;
			player.resetWalkSteps();
			if ((!player.clipedProjectile(target, maxDistance == 0))
					|| distanceX > size + maxDistance
					|| distanceX < -1 - maxDistance
					|| distanceY > size + maxDistance
					|| distanceY < -1 - maxDistance) {
				if (!player.calcFollow(target, 2, true, true))
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("proccessFalconAttack");
		}
		return true;
	}
}