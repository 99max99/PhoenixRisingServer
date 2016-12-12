package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.controllers.DungeonController;
import net.kagani.utils.Utils;

public class HomeTeleport extends Action {

	private final int HOME_ANIMATION = 16385, HOME_GRAPHIC = 3017;

	private int currentTime;
	private WorldTile tile;

	public HomeTeleport(WorldTile tile) {
		this.tile = tile;
	}

	@Override
	public boolean start(final Player player) {
		return process(player);
	}

	public static void useLodestone(Player player, int componentId) {
		int location;
		switch (componentId) {
		case 25:
			location = 44060059;
			break; // Seers' Village
		case 30:
			location = 38768024;
			break; // Eagles' Peak
		case 16:
			location = 54021233;
			break; // Al Kharid
		case 36:
			location = 577407637;
			break; // Ashdale
		case 4:
			location = 52661131;
			break; // Bandit Camp
		case 20:
			location = 50875619;
			break; // Draynor Village
		case 22:
			location = 48614732;
			break; // Falador
		case 24:
			location = 49335440;
			break; // Port Sarim
		case 21:
			location = 50253234;
			break; // Edgeville
		case 18:
			location = 47500761;
			break; // Burthorpe
		case 35:
			player.getDialogueManager().startDialogue("WildernessLodestone");
			return; // Wilderness Volcano
		case 27:
			location = 52661553;
			break; // Varrock
		case 28:
			location = 41438231;
			break; // Yanille
		case 19:
			location = 46386556;
			break; // Catherby
		case 32:
			location = 45239372;
			break; // Karamja
		case 31:
			location = 44437086;
			break; // Fremennik Province
		case 17:
			location = 43158805;
			break; // Ardougne
		case 34:
			location = 36932686;
			break; // Tirannwn
		case 33:
			location = 41487160;
			break; // Oo'glog
		case 26:
			location = 47156595;
			break; // Taverley
		case 23:
			location = 52972694;
			break; // Lumbridge
		case 29:
			location = 57626044;
			break; // Canifis
		case 15:
			location = 34164555;
			break; // Lunar Isle
		case 37:
			location = 304614689;
			break; // Prifinnas

		default:
			location = -1;
			break;
		}
		if (location != -1) {
			if (player.getDungManager().isInside()) {
				player.getPackets().sendGameMessage("You cannot teleport to home while in a dungeon.");
				return;
			}
			if (player.getRights() >= 2)
				player.setNextWorldTile(new WorldTile(location));
			else
				player.getActionManager().setAction(new HomeTeleport(new WorldTile(location)));
			player.setPreviousLodestone(componentId);
		}
	}

	@Override
	public int processWithDelay(Player player) {
		try {
			if (player.teleportType == null) {
				if (currentTime++ == 0) {
					player.setNextAnimation(new Animation(HOME_ANIMATION));
					player.setNextGraphics(new Graphics(HOME_GRAPHIC));
				} else if (currentTime == 18) {
					player.setNextWorldTile(tile);
					player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						Magic.teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
					player.setDirection(6);
					if (player.getControlerManager().getControler() instanceof DungeonController)
						return -1;
					player.lock(11);
				} else if (currentTime == 19) {
					player.setNextGraphics(new Graphics(HOME_GRAPHIC + 1));
					player.setNextAnimation(new Animation(HOME_ANIMATION + 1));
				} else if (currentTime == 23) {
					player.setNextGraphics(new Graphics(3018));
					player.setNextAnimation(new Animation(16386));
				} else if (currentTime == 24)
					player.setNextAnimation(new Animation(16393));
				else if (currentTime == 27) {
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(tile.transform(0, -1, 0));
				} else if (currentTime == 28) {
					if (player.inMemberZone()) {
						int bonus = 0;
						if (player.isBronzeMember())
							bonus = 5;
						else if (player.isSilverMember())
							bonus = 5;
						else if (player.isGoldMember())
							bonus = 10;
						else if (player.isPlatinumMember())
							bonus = 12;
						else if (player.isDiamondMember())
							bonus = 15;
						player.getDialogueManager().startDialogue("SimpleMessage",
								"While training in this area, you will receive an XP boost by a total of " + bonus
										+ "%.");
					}
					return -1;
				}
			} else if (player.teleportType.equals("assasian")) {
				if (currentTime++ == 0) {
					player.setNextAnimation(new Animation(17074));
					player.setNextGraphics(new Graphics(3215));
				} else if (currentTime == 18) {
					player.setNextWorldTile(tile);
					player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						Magic.teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
					player.setDirection(6);
					if (player.getControlerManager().getControler() instanceof DungeonController)
						return -1;
					player.lock(11);
				} else if (currentTime == 19) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				} else if (currentTime == 21) {
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(tile.transform(0, -1, 0));
				} else if (currentTime == 22) {
					if (player.inMemberZone()) {
						int bonus = 0;
						if (player.isBronzeMember())
							bonus = 5;
						else if (player.isSilverMember())
							bonus = 5;
						else if (player.isGoldMember())
							bonus = 10;
						else if (player.isPlatinumMember())
							bonus = 12;
						else if (player.isDiamondMember())
							bonus = 15;
						player.getDialogueManager().startDialogue("SimpleMessage",
								"While training in this area, you will receive an XP boost by a total of " + bonus
										+ "%.");
					}
					return -1;
				}
			} else if (player.teleportType.equals("pegasus")) {
				if (currentTime++ == 0) {
					player.setNextAnimation(new Animation(17106));
					player.setNextGraphics(new Graphics(3223));
				} else if (currentTime == 18) {
					player.setNextWorldTile(tile);
					player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						Magic.teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
					player.setDirection(6);
					if (player.getControlerManager().getControler() instanceof DungeonController)
						return -1;
					player.lock(11);
				} else if (currentTime == 19) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				} else if (currentTime == 21) {
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(tile.transform(0, -1, 0));
				} else if (currentTime == 22) {
					if (player.inMemberZone()) {
						int bonus = 0;
						if (player.isBronzeMember())
							bonus = 5;
						else if (player.isSilverMember())
							bonus = 5;
						else if (player.isGoldMember())
							bonus = 10;
						else if (player.isPlatinumMember())
							bonus = 12;
						else if (player.isDiamondMember())
							bonus = 15;
						player.getDialogueManager().startDialogue("SimpleMessage",
								"While training in this area, you will receive an XP boost by a total of " + bonus
										+ "%.");
					}
					return -1;
				}
			} else if (player.teleportType.equals("gnome")) {
				if (currentTime++ == 0) {
					player.setNextAnimation(new Animation(17191));
					player.setNextGraphics(new Graphics(3254));
				} else if (currentTime == 18) {
					player.setNextWorldTile(tile);
					player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						Magic.teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
					player.setDirection(6);
					if (player.getControlerManager().getControler() instanceof DungeonController)
						return -1;
					player.lock(11);
				} else if (currentTime == 19) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				} else if (currentTime == 21) {
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(tile.transform(0, -1, 0));
				} else if (currentTime == 22) {
					if (player.inMemberZone()) {
						int bonus = 0;
						if (player.isBronzeMember())
							bonus = 5;
						else if (player.isSilverMember())
							bonus = 5;
						else if (player.isGoldMember())
							bonus = 10;
						else if (player.isPlatinumMember())
							bonus = 12;
						else if (player.isDiamondMember())
							bonus = 15;
						player.getDialogueManager().startDialogue("SimpleMessage",
								"While training in this area, you will receive an XP boost by a total of " + bonus
										+ "%.");
					}
					return -1;
				}
			} else if (player.teleportType.equals("sky")) {
				if (currentTime++ == 0) {
					player.setNextAnimation(new Animation(17317));
					player.setNextGraphics(new Graphics(3311));
					player.setNextGraphics(new Graphics(3310));
					player.setNextGraphics(new Graphics(3309));
				} else if (currentTime == 18) {
					player.setNextWorldTile(tile);
					player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						Magic.teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
					player.setDirection(6);
					if (player.getControlerManager().getControler() instanceof DungeonController)
						return -1;
					player.lock(11);
				} else if (currentTime == 19) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				} else if (currentTime == 21) {
					player.setNextAnimation(new Animation(808));
					player.setNextGraphics(new Graphics(-1));
					player.setNextWorldTile(tile.transform(0, -1, 0));
				} else if (currentTime == 22) {
					if (player.inMemberZone()) {
						int bonus = 0;
						if (player.isBronzeMember())
							bonus = 5;
						else if (player.isSilverMember())
							bonus = 5;
						else if (player.isGoldMember())
							bonus = 10;
						else if (player.isPlatinumMember())
							bonus = 12;
						else if (player.isDiamondMember())
							bonus = 15;
						player.getDialogueManager().startDialogue("SimpleMessage",
								"While training in this area, you will receive an XP boost by a total of " + bonus
										+ "%.");
					}
					return -1;
				}
			} else if (player.teleportType.equals("demon")) {
				if (currentTime++ == 0) {
					player.setNextAnimation(new Animation(17108));
					player.setNextGraphics(new Graphics(3224));
					player.setNextGraphics(new Graphics(3225));
				} else if (currentTime == 18) {
					player.setNextWorldTile(tile);
					player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						Magic.teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
					player.setDirection(6);
					if (player.getControlerManager().getControler() instanceof DungeonController)
						return -1;
					player.lock(11);
				} else if (currentTime == 19) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				} else if (currentTime == 21) {
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(tile.transform(0, -1, 0));
				} else if (currentTime == 22) {
					if (player.inMemberZone()) {
						int bonus = 0;
						if (player.isBronzeMember())
							bonus = 5;
						else if (player.isSilverMember())
							bonus = 5;
						else if (player.isGoldMember())
							bonus = 10;
						else if (player.isPlatinumMember())
							bonus = 12;
						else if (player.isDiamondMember())
							bonus = 15;
						player.getDialogueManager().startDialogue("SimpleMessage",
								"While training in this area, you will receive an XP boost by a total of " + bonus
										+ "%.");
					}
					return -1;
				}
			} else if (player.teleportType.equals("")) {
				if (currentTime++ == 0) {
					player.setNextAnimation(new Animation(HOME_ANIMATION));
					player.setNextGraphics(new Graphics(HOME_GRAPHIC));
				} else if (currentTime == 18) {
					player.setNextWorldTile(tile);
					player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						Magic.teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
					player.setDirection(6);
					if (player.getControlerManager().getControler() instanceof DungeonController)
						return -1;
					player.lock(11);
				} else if (currentTime == 19) {
					player.setNextGraphics(new Graphics(HOME_GRAPHIC + 1));
					player.setNextAnimation(new Animation(HOME_ANIMATION + 1));
				} else if (currentTime == 23) {
					player.setNextGraphics(new Graphics(3018));
					player.setNextAnimation(new Animation(16386));
				} else if (currentTime == 24)
					player.setNextAnimation(new Animation(16393));
				else if (currentTime == 27) {
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(tile.transform(0, -1, 0));
				} else if (currentTime == 28) {
					if (player.inMemberZone()) {
						int bonus = 0;
						if (player.isBronzeMember())
							bonus = 5;
						else if (player.isSilverMember())
							bonus = 5;
						else if (player.isGoldMember())
							bonus = 10;
						else if (player.isPlatinumMember())
							bonus = 12;
						else if (player.isDiamondMember())
							bonus = 15;
						player.getDialogueManager().startDialogue("SimpleMessage",
								"While training in this area, you will receive an XP boost by a total of " + bonus
										+ "%.");
					}
					return -1;
				}
			} else {
				if (currentTime++ == 0) {
					player.setNextAnimation(new Animation(HOME_ANIMATION));
					player.setNextGraphics(new Graphics(HOME_GRAPHIC));
				} else if (currentTime == 18) {
					player.setNextWorldTile(tile);
					player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						Magic.teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
					player.setDirection(6);
					if (player.getControlerManager().getControler() instanceof DungeonController)
						return -1;
					player.lock(11);
				} else if (currentTime == 19) {
					player.setNextGraphics(new Graphics(HOME_GRAPHIC + 1));
					player.setNextAnimation(new Animation(HOME_ANIMATION + 1));
				} else if (currentTime == 23) {
					player.setNextGraphics(new Graphics(3018));
					player.setNextAnimation(new Animation(16386));
				} else if (currentTime == 24)
					player.setNextAnimation(new Animation(16393));
				else if (currentTime == 27) {
					player.setNextAnimation(new Animation(-1));
					player.setNextWorldTile(tile.transform(0, -1, 0));
				} else if (currentTime == 28) {
					if (player.inMemberZone()) {
						int bonus = 0;
						if (player.isBronzeMember())
							bonus = 5;
						else if (player.isSilverMember())
							bonus = 5;
						else if (player.isGoldMember())
							bonus = 10;
						else if (player.isPlatinumMember())
							bonus = 12;
						else if (player.isDiamondMember())
							bonus = 15;
						player.getDialogueManager().startDialogue("SimpleMessage",
								"While training in this area, you will receive an XP boost by a total of " + bonus
										+ "%.");
					}
					return -1;
				}
			}
			return 0;
		} catch (Exception e) {
			player.getPackets().sendGameMessage(e + ".");
		}
		return 0;
	}

	@Override
	public boolean process(Player player) {
		int delay = 10000;
		if (player.getControlerManager().getControler() instanceof DungeonController)
			delay = 0;
		if (player.getAttackedByDelay() + delay > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You can't teleport shortly after the end of combat.");
			return false;
		}
		return player.getControlerManager().processMagicTeleport(tile);
	}

	@Override
	public void stop(Player player) {
		player.setNextAnimation(new Animation(-1));
		player.setNextGraphics(new Graphics(-1));
		player.unlock();
	}
}