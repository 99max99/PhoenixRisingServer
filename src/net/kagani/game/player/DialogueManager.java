package net.kagani.game.player;

import java.io.IOException;

import net.kagani.game.player.dialogues.ConfirmDialogue;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.player.dialogues.DialogueHandler;

public class DialogueManager {

	private Player player;
	private Dialogue lastDialogue;
	private ConfirmDialogue lastConfirmDialogue;

	public DialogueManager(Player player) {
		this.player = player;
	}

	public void sendLogoutDialogue() {
		player.getDialogueManager().sendConfirmDialogue(6,
				new ConfirmDialogue() {
					@Override
					public void process(int option) {
						player.logout(option == 0);
					}

					@Override
					public void finish() {
					}
				});
	}

	public void sendConfirmDialogue(int type, ConfirmDialogue dialogue) {
		finishConfirmDialogue();
		if (type == 9 && player.isInstantSwitchToLegacy()) {
			dialogue.process(1);
			return;
		}
		player.getVarsManager().sendVar(3813, type);
		player.getInterfaceManager().sendConfirmDialogue();
		player.getPackets().sendIComponentSettings(26, 22, -1, -1, 2);
		lastConfirmDialogue = dialogue;
	}

	public void handleConfirmDialogue(int componentId) {
		if (lastConfirmDialogue != null) {
			if (componentId != 22) {// 20 is cancel. 16 is option1
				if (componentId == 6
						&& player.getVarsManager().getValue(3813) == 9)
					player.switchInstantSwitchToLegacy();
				lastConfirmDialogue.process(componentId == 18 ? 0 : 1);
			}
			finishConfirmDialogue();
		}
	}

	public void finishConfirmDialogue() {
		if (lastConfirmDialogue != null) {
			lastConfirmDialogue.finish();
			player.getInterfaceManager().closeConfirmDialogue();
			lastConfirmDialogue = null;
		}
	}

	public void startDialogue(Object key, Object... parameters) {
		if (!player.getControlerManager().useDialogueScript(key))
			return;
		if (lastDialogue != null)
			lastDialogue.finish();
		lastDialogue = DialogueHandler.getDialogue(key);
		if (lastDialogue == null)
			return;
		lastDialogue.parameters = parameters;
		lastDialogue.setPlayer(player);
		lastDialogue.start();
	}

	public void continueDialogue(int interfaceId, int componentId)
			throws ClassNotFoundException, IOException {
		if (lastDialogue == null)
			return;
		lastDialogue.run(interfaceId, componentId);
	}

	public void finishDialogue() {
		if (lastDialogue == null)
			return;
		lastDialogue.finish();
		lastDialogue = null;
		if (player.getInterfaceManager().containsDialogueInterface())
			player.getInterfaceManager().removeDialogueInterface();
	}

	public Dialogue getLast() {
		return lastDialogue;
	}
}