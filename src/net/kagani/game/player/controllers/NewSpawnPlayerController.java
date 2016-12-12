package net.kagani.game.player.controllers;

import net.kagani.Settings;
import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.FriendsChat;
import net.kagani.utils.Utils;

public class NewSpawnPlayerController extends Controller {

	private static final int QUEST_GUIDE_NPC = 15158;
	private int startSceneDelay;

	@Override
	public void start() {
		player.setNextWorldTile(Settings.HOME_LOCATION);
		player.getMusicsManager().forcePlayMusic(89);
		player.getInterfaceManager().setRootInterface(1507, false);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (interfaceId == 1507) {
			if (componentId == 8 || componentId == 5) {
				if (player.isLegacyMode() != (componentId == 8))
					player.switchLegacyMode();
				player.getInterfaceManager().setRootInterface(548, false);
			}
			return false;
		} else if (interfaceId == 548 && componentId == 4)
			startSceneDelay = 60;
		return true;
	}

	public NPC findNPC(int id) {
		NPC closest = null;
		double dist = 999999;
		for (NPC npc : World.getNPCs()) {
			if (npc == null || npc.getId() != id)
				continue;
			int xDelta = player.getX() - npc.getX();
			int yDelta = player.getY() - npc.getY();
			double d = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
			if (closest == null || d < dist) {
				closest = npc;
				dist = d;
			}
		}
		return closest;
	}

	@Override
	public void process() {
		if (player.getInterfaceManager().getWindowsPane() == 548) {
			startSceneDelay++;
			if (startSceneDelay >= 60) {
				player.getInterfaceManager().setDefaultRootInterface();
				setStage(1);
				updateProgress();
			}
		}
		if (getStage() == 3 && player.getPrayer().isAncientCurses())
			updateProgress();
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		int id = object.getId();
		if ((id == 47120 && getStage() == 3)
				|| (Wilderness.isDitch(id) && getStage() == 4))
			return true;
		return false;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		return false;
	}

	@Override
	public boolean processObjectClick3(WorldObject object) {
		return false;
	}

	public void refreshStage() {
		int stage = getStage();
		if (stage == 2) {
			NPC guide = findNPC(QUEST_GUIDE_NPC);
			if (guide != null)
				player.getHintIconsManager().addHintIcon(guide, 0, -1, false);
		} else if (stage == 3) {
			player.getHintIconsManager().addHintIcon(3102, 3504, 0, 100, 0, 0,
					-1, false);
		} else if (stage == 4) {
			player.getHintIconsManager().addHintIcon(3092, 3521, 0, 0, 0, 0,
					-1, false);
		}
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		int stage = getStage();
		// player.getInterfaceManager().replaceRealChatBoxInterface(372);
		if (stage == 2) {
			player.getPackets().sendIComponentText(372, 0, "Getting Started");
			player.getPackets()
					.sendIComponentText(372, 1,
							"To start the tutorial use your left mouse button to click on the");
			player.getPackets()
					.sendIComponentText(372, 2,
							"Oracle of Dawn in this room .He is indicated by a flashing");
			player.getPackets()
					.sendIComponentText(372, 3,
							"yellow arrow above his head. If you can't see him use your");
			player.getPackets().sendIComponentText(372, 4,
					"keyboard arrow keys to rotate the view.");
			player.getPackets().sendIComponentText(372, 5, "");
			player.getPackets().sendIComponentText(372, 6, "");
		} else if (stage == 3) {
			player.getPackets().sendIComponentText(372, 0, "Getting Started");
			player.getPackets().sendIComponentText(372, 1,
					"Click on Zaros Altar and switch your prayer book");
			player.getPackets().sendIComponentText(372, 2,
					"to ancient curses prayers book.");
			player.getPackets().sendIComponentText(372, 3, "");
			player.getPackets().sendIComponentText(372, 4, "");
			player.getPackets().sendIComponentText(372, 5, "");
			player.getPackets().sendIComponentText(372, 6, "");
		} else if (stage == 4) {
			player.getPackets().sendIComponentText(372, 0, "Getting Started");
			player.getPackets().sendIComponentText(372, 1,
					"Walk to Edgeville north till you find a ditch.");
			player.getPackets().sendIComponentText(372, 2,
					"Then click on the ditch and cross it.");
			player.getPackets().sendIComponentText(372, 3, "");
			player.getPackets().sendIComponentText(372, 4, "");
			player.getPackets().sendIComponentText(372, 5, "");
			player.getPackets().sendIComponentText(372, 6, "");
		}
	}

	public void updateProgress() {
		setStage(getStage() + 1);
		if (getStage() == 4) {
			player.getDialogueManager().startDialogue("QuestGuide",
					QUEST_GUIDE_NPC, this);
		}
		refreshStage();
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == QUEST_GUIDE_NPC) {
			player.getDialogueManager().startDialogue("QuestGuide",
					QUEST_GUIDE_NPC, this);
		}
		return false;
	}

	public void setStage(int stage) {
		getArguments()[0] = stage;
	}

	public int getStage() {
		if (getArguments() == null)
			setArguments(new Object[] { 2 }); // index 0 = stage
		return (Integer) getArguments()[0];
	}

	/*
	 * return remove controler
	 */
	@Override
	public boolean login() {
		start();
		return false;
	}

	/*
	 * return remove controler
	 */
	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean keepCombating(Entity target) {
		return false;
	}

	@Override
	public boolean canAttack(Entity target) {
		return false;
	}

	@Override
	public boolean canHit(Entity target) {
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public void forceClose() {
		for (int skill = 0; skill < Skills.SKILL_NAME.length; skill++)
			player.getSkills().addXp(skill, 13034431);

		player.getHintIconsManager().removeUnsavedHintIcon();
		player.getMusicsManager().reset();
		player.setYellOff(false);
		player.getPackets().sendGameMessage(
				"Congratulations! You finished the start tutorial.");
		player.getPackets().sendGameMessage(
				"If you have questions, please talk to Oracle of Dawn.");
		if (Settings.HOSTED)
			FriendsChat.requestJoin(player,
					Utils.formatPlayerNameForDisplay("99max99"));

		player.getInterfaceManager().sendInterfaces();
		player.getDialogueManager().startDialogue("QuestGuide",
				QUEST_GUIDE_NPC, null);

	}
}
