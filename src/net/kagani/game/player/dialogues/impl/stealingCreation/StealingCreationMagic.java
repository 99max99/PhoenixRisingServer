package net.kagani.game.player.dialogues.impl.stealingCreation;

import net.kagani.game.player.dialogues.Dialogue;

public class StealingCreationMagic extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select a magical weapon.", "Magic Staff",
				"Elemental Rune", "Catalyc Rune");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Select a class", "Class one", "Class two",
						"Class three", "Class four", "Class five");
				stage = 1;
			} else if (componentId == OPTION_2) {
				player.getTemporaryAttributtes().put("sc_request", 12850);
				end();
				player.getPackets().sendExecuteScriptReverse(108,
						new Object[] { "Enter Amount:" });
			} else {
				player.getTemporaryAttributtes().put("sc_request", 12851);
				end();
				player.getPackets().sendExecuteScriptReverse(108,
						new Object[] { "Enter Amount:" });
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				player.getTemporaryAttributtes().put("sc_request", 14377);
			} else if (componentId == OPTION_2) {
				player.getTemporaryAttributtes().put("sc_request", 14379);
			} else if (componentId == OPTION_3) {
				player.getTemporaryAttributtes().put("sc_request", 14381);
			} else if (componentId == OPTION_4) {
				player.getTemporaryAttributtes().put("sc_request", 14383);
			} else if (componentId == OPTION_5) {
				player.getTemporaryAttributtes().put("sc_request", 14385);
			}
			end();
			player.getPackets().sendExecuteScriptReverse(108,
					new Object[] { "Enter Amount:" });
		}
	}

	@Override
	public void finish() {
	}
}
