package net.kagani.game.player.dialogues.impl.skills;

import net.kagani.game.player.content.SkillsDialogue.ToolReference;
import net.kagani.game.player.dialogues.Dialogue;

public class ChooseAToolD extends Dialogue {

	private ToolReference[] tools;

	@Override
	public void start() {
		String message = (String) parameters[0];
		tools = (ToolReference[]) parameters[1];
		for (int i = 0; i < 15; i++)
			player.getPackets().sendCSVarInteger(1703 + i,
					i >= tools.length ? -1 : tools[i].getToolId());
		player.getPackets().sendIComponentText(1179, 0, message);
		player.getInterfaceManager().sendCentralInterface(1179);
	}

	public static int getOptionSlot(int componentId) {
		return componentId == 16 ? 0 : componentId == 33 ? 1
				: componentId == 36 ? 2 : componentId == 39 ? 3 : 4;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int option = getOptionSlot(componentId);
		end();
		if (option >= tools.length)
			return;
		tools[option].select(player);
	}

	@Override
	public void finish() {
		player.getInterfaceManager().removeCentralInterface();
	}
}