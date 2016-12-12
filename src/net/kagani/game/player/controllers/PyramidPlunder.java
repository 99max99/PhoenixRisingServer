package net.kagani.game.player.controllers;

import net.kagani.Settings;
import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class PyramidPlunder extends Controller {

	/**
	 * @author: Dylan Page
	 */

	public WorldTile INSIDE = new WorldTile(1927, 4477, 0);
	public WorldTile OUTSIDE = new WorldTile(3288, 2802, 0);

	private boolean[] pyramidSnakes = new boolean[60000];
	private boolean[] pyramidChests = new boolean[60000];

	private int picklockDoor;
	private int room = 1;

	private long timer = 0;

	private long lastSearch = 0;

	@Override
	public void process() {
		timeOut();
	}

	@Override
	public void start() {
		timer = System.currentTimeMillis();
		picklockDoor = generateDoor();
		player.setNextWorldTile(INSIDE);
		player.getPackets().sendGameMessage("You find a door! You open it successfully.", true);
		sendInterfaces();
		if (Settings.DEBUG)
			Logger.log("Pyramid Plunder", "Door: " + picklockDoor);
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (player.getEmotesManager().isDoingEmote()) {
			player.getPackets().sendGameMessage("You can't do that while perfoming an emote.");
			return true;
		}
		if (player.isLocked()) {
			player.getPackets().sendGameMessage("You can't do that while perfoming an action.");
			return true;
		}
		player.faceObject(object);
		switch (object.getId()) {
		case 16520: // urns
		case 16525:
		case 16530:
		case 16521:
		case 16526:
		case 16518:
		case 16523:
		case 16528:
		case 16532:
		case 16529:
		case 16524:
		case 16527:
		case 16522:
		case 16531:
		case 16519:
			if (pyramidSnakes[object.getId()]) {
				player.getPackets().sendGameMessage("You have already looted this.");
				return true;
			}
			if (System.currentTimeMillis() - lastSearch < 2000)
				return true;
			lastSearch = System.currentTimeMillis();
			if (Utils.random(4) == 0) {
				fail(object.getId());
				return true;
			}
			pyramidSnakes[object.getId()] = true;
			WorldTasksManager.schedule(new WorldTask() {
				int loop;

				@Override
				public void run() {
					if (loop == 0) {
						player.setNextAnimation(new Animation(23306));
					} else if (loop == 1) {
						player.setNextAnimation(new Animation(23307));
					} else if (loop == 2) {
						if (Utils.random(4) == 0) {
							if (Utils.random(3) == 0) {
								player.getInventory().addItem(Utils.random(9042, 9040), 1);
							} else {
								player.getMoneyPouch().setAmount(Utils.random(300), false);
							}
							player.getPackets().sendGameMessage("You successfully loot the urn!", true);
						} else {
							player.getPackets().sendGameMessage("You find nothing.", true);
						}
						player.getSkills().addXp(Skills.THIEVING, 60 * room / 2);
						stop();
					}
					loop++;
				}
			}, 0, 1);
			break;
		case 16547: // sacrophagus
		case 59795:
			if (pyramidChests[object.getId()]) {
				player.getPackets().sendGameMessage("You have already looted this.");
				return true;
			}
			if (System.currentTimeMillis() - lastSearch < 2000)
				return true;
			lastSearch = System.currentTimeMillis();
			if (Utils.random(4) == 0) {
				fail(object.getId());
				return true;
			}
			pyramidChests[object.getId()] = true;
			WorldTasksManager.schedule(new WorldTask() {
				int loop;

				@Override
				public void run() {
					if (loop == 0) {
						player.setNextAnimation(new Animation(23309));
					} else if (loop == 1) {
						player.setNextAnimation(new Animation(23310));
					} else if (loop == 5) {
						player.getMoneyPouch().setAmount(Utils.random(500), false);
						player.getPackets().sendGameMessage("You successfully loot the sacrophagus!", true);
						player.getSkills().addXp(Skills.THIEVING, 40 * room / 2);
						stop();
					}
					loop++;
				}
			}, 0, 1);
			break;
		case 16537: // golden chest
			if (pyramidChests[object.getId()]) {
				player.getPackets().sendGameMessage("You have already looted this.");
				return true;
			}
			if (Utils.random(4) == 0) {
				fail(object.getId());
				return true;
			}
			if (System.currentTimeMillis() - lastSearch < 2000)
				return true;
			lastSearch = System.currentTimeMillis();
			if (Utils.random(250) == 0) {
				player.getInventory().addItem(9044, 1);
				player.getDialogueManager().startDialogue("SimpleMessage", "You have found a Pharaoh's sceptre.");
				World.sendNews(player, player.getDisplayName() + " found the Pharaoh's sceptre in Pyramid Plunder!", 1);
			}
			player.getMoneyPouch().setAmount(Utils.random(500), false);
			pyramidChests[object.getId()] = true;
			player.getPackets().sendGameMessage("You successfully loot the golden chest!", true);
			player.setNextAnimation(new Animation(23314));
			player.getSkills().addXp(Skills.THIEVING, 40 * room / 2);
			break;
		case 16458: // leave
			player.getDialogueManager().startDialogue("PyramidPlunderD", this);
			break;
		case 16541: // next room
		case 16542:
		case 16539:
		case 16540:
			if (picklockDoor == object.getId()) {
				if (room == 2 && player.getSkills().getLevel(Skills.THIEVING) <= 31) {
					player.getPackets()
							.sendGameMessage("You need a thieving level of at least 31 to proceed to this room.");
					return true;
				} else if (room == 3 && player.getSkills().getLevel(Skills.THIEVING) <= 41) {
					player.getPackets()
							.sendGameMessage("You need a thieving level of at least 41 to proceed to this room.");
					return true;
				} else if (room == 4 && player.getSkills().getLevel(Skills.THIEVING) <= 51) {
					player.getPackets()
							.sendGameMessage("You need a thieving level of at least 51 to proceed to this room.");
					return true;
				} else if (room == 5 && player.getSkills().getLevel(Skills.THIEVING) <= 61) {
					player.getPackets()
							.sendGameMessage("You need a thieving level of at least 61 to proceed to this room.");
					return true;
				} else if (room == 6 && player.getSkills().getLevel(Skills.THIEVING) <= 71) {
					player.getPackets()
							.sendGameMessage("You need a thieving level level t least 71 to proceed to this room.");
					return true;
				} else if (room == 7 && player.getSkills().getLevel(Skills.THIEVING) <= 81) {
					player.getPackets()
							.sendGameMessage("You need a thieving level of at least 81 to proceed to this room.");
					return true;
				} else if (room == 8 && player.getSkills().getLevel(Skills.THIEVING) <= 91) {
					player.getPackets()
							.sendGameMessage("You need a thieving level of at least 91 to proceed to this room.");
					return true;
				}
				player.setNextAnimation(new Animation(23314));
				if (Utils.random(2) == 0) {
					player.getPackets().sendGameMessage("Your attempt fails.");
					return true;
				}
				room++;
				player.getSkills().addXp(Skills.THIEVING, 40 * room / 2);
				sendInterfaces();
				for (int i = 0; i < 60000; i++) {
					pyramidChests[i] = false;
					pyramidSnakes[i] = false;
				}
				generateTeleport();
				picklockDoor = generateDoor();
				if (Settings.DEBUG)
					Logger.log("Pyramid Pludner", "Door: " + picklockDoor);
			} else {
				player.getPackets().sendGameMessage("This door leads to a dead end.");
			}
			break;
		case 16517:
			if (room == 1 || room == 2) {
				if (player.getY() == 4473 || player.getY() == 4474 || player.getY() == 4475) {
					player.getSkills().addXp(Skills.THIEVING, 10 * room / 2);
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4472, 0, false);
				} else {
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4474, 0, false);
				}
			} else if (room == 3) {
				if (player.getY() >= 4466 && player.getY() <= 4471) {
					player.getSkills().addXp(Skills.THIEVING, 10 * room / 2);
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4465, 0, false);
				} else {
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4468, 0, false);
				}
			} else if (room == 4) {
				if (player.getX() >= 1927 && player.getX() <= 1930) {
					player.getSkills().addXp(Skills.THIEVING, 10 * room / 2);
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(1931, player.getY(), 0, false);
				} else {
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(1929, player.getY(), 0, false);
				}
			} else if (room == 5) {
				if (player.getX() >= 1962 && player.getX() <= 1965) {
					player.getSkills().addXp(Skills.THIEVING, 10 * room / 2);
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(1961, player.getY(), 0, false);
				} else {
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(1963, player.getY(), 0, false);
				}
			} else if (room == 6) {
				if (player.getY() >= 4424 && player.getY() <= 4427) {
					player.getSkills().addXp(Skills.THIEVING, 10 * room / 2);
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4429, 0, false);
				} else {
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4426, 0, false);
				}
			} else if (room == 7) {
				if (player.getY() >= 4421 && player.getY() <= 4424) {
					player.getSkills().addXp(Skills.THIEVING, 10 * room / 2);
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4426, 0, false);
				} else {
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4423, 0, false);
				}
			} else if (room == 8) {
				if (player.getY() >= 4420 && player.getY() <= 4423) {
					player.getSkills().addXp(Skills.THIEVING, 10 * room / 2);
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4425, 0, false);
				} else {
					player.setNextAnimation(new Animation(24910));
					player.addWalkSteps(player.getX(), 4422, 0, false);
				}
			}
			break;
		default:
			player.stopAll();
			player.getPackets().sendGameMessage("Nothing interesting happens.");
			break;
		}
		return false;
	}

	private void timeOut() {
		long totalTime = System.currentTimeMillis() - timer;
		long sec = 300 - totalTime / 1000;
		if (sec == 60)
			player.getPackets().sendGameMessage("You have less than a minute left.");
		if (sec == 150)
			player.getPackets().sendGameMessage("You have less than 3 minutes left.");
		if (System.currentTimeMillis() - timer > 300000) {
			reset();
			player.setNextWorldTile(OUTSIDE);
			player.closeInterfaces();
			removeControler();
			player.getDialogueManager().startDialogue("SimpleNPCMessage", 4476,
					"You've had your five minutes of plundering. Now be off with you!");
		} else {
			if (Settings.DEBUG) {
				if (Utils.random(10) == 0)
					Logger.log("PyramidPlunder", player.getDisplayName() + " has " + sec + " seconds left");
			}
		}
	}

	private void generateTeleport() {
		if (room >= 9) {
			reset();
			player.setNextWorldTile(OUTSIDE);
			player.closeInterfaces();
			removeControler();
		}
		switch (room) {
		case 1:
			player.setNextWorldTile(new WorldTile(1927, 4477, 0));
			break;
		case 2:
			player.setNextWorldTile(new WorldTile(1954, 4477, 0));
			break;
		case 3:
			player.setNextWorldTile(new WorldTile(1977, 4471, 0));
			break;
		case 4:
			player.setNextWorldTile(new WorldTile(1927, 4453, 0));
			break;
		case 5:
			player.setNextWorldTile(new WorldTile(1965, 4444, 0));
			break;
		case 6:
			player.setNextWorldTile(new WorldTile(1927, 4424, 0));
			break;
		case 7:
			player.setNextWorldTile(new WorldTile(1943, 4421, 0));
			break;
		case 8:
			player.setNextWorldTile(new WorldTile(1974, 4420, 0));
			break;
		}
	}

	private void fail(int objectId) {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(23306));
				} else if (loop == 1) {
					player.setNextAnimation(new Animation(23308));
				} else if (loop == 2) {
					player.applyHit(new Hit(player, Utils.random(150), HitLook.POISON_DAMAGE));
					player.setNextForceTalk(new ForceTalk("Ow!"));
					pyramidSnakes[objectId] = true;
					pyramidChests[objectId] = true;
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		switch (object.getId()) {
		case 16520: // urns
		case 16525:
		case 16530:
		case 16521:
		case 16526:
		case 16518:
		case 16523:
		case 16528:
		case 16532:
		case 16529:
		case 16527:
		case 16522:
		case 16531:
			player.getPackets().sendGameMessage("It does not seem like there are any snakes there.");
			break;
		case 16458: // leave
			reset();
			player.setNextWorldTile(OUTSIDE);
			player.closeInterfaces();
			removeControler();
			break;
		default:
			player.getPackets().sendGameMessage("Nothing interesting happens.");
			break;
		}
		return false;
	}

	private int getLevel() {
		switch (room) {
		case 1:
			return 21;
		case 2:
			return 31;
		case 3:
			return 41;
		case 4:
			return 51;
		case 5:
			return 61;
		case 6:
			return 71;
		case 7:
			return 81;
		case 8:
			return 91;
		}
		return 11;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendMinigameInterface(428);
		player.getPackets().sendIComponentText(428, 4, "Room: " + room + "/8");
		player.getPackets().sendIComponentText(428, 5, "Level: " + getLevel());
		player.getVarsManager().sendVar(822, 5);
	}

	public void reset() {
		for (int i = 0; i < 60000; i++) {
			pyramidChests[i] = false;
			pyramidSnakes[i] = false;
		}
		player.stopAll();
		player.reset();
		picklockDoor = 0;
		timer = 0;
		room = 0;
		player.getInterfaceManager().removeMinigameInterface();
	}

	private int generateDoor() {
		switch (Utils.random(4)) {
		case 0:
			return 16541;
		case 1:
			return 16540;
		case 2:
			return 16539;
		case 3:
			return 16542;
		}
		return 16541;
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				player.stopAll();
				if (loop == 0) {
					player.setNextAnimation(player.getDeathAnimation());
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					player.setNextAnimation(new Animation(-1));
					reset();
					player.setNextWorldTile(OUTSIDE);
					player.closeInterfaces();
					removeControler();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		reset();
		player.setNextWorldTile(OUTSIDE);
		player.closeInterfaces();
		removeControler();
		return false;
	}

	@Override
	public boolean logout() {
		reset();
		player.setNextWorldTile(OUTSIDE);
		player.closeInterfaces();
		removeControler();
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		reset();
		player.closeInterfaces();
		removeControler();
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		reset();
		player.closeInterfaces();
		removeControler();
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		reset();
		player.closeInterfaces();
		removeControler();
		return true;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		reset();
		player.closeInterfaces();
		removeControler();
		return true;
	}
}