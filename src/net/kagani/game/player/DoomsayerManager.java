package net.kagani.game.player;

import java.io.Serializable;

public class DoomsayerManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2046805849079670380L;

	private int[] warnings;
	private boolean[] messages;

	private transient Player player;

	private static final int TOOGLE_WARNING_VARBIT = 1133;

	public static final int CLAN_WARS_SAFE_WARNING = 22,
			CLAN_WARS_DANGEROUS_WARNING = 31;
	private static final int[] DOOMSAYER_WARNINGS_BIT_VARS = { -1, 1134, 1135,
			1136, 1137, 1138, 1139, 1140, 1141, 1142, 1143, 1146, 1147, 1148,
			1149, 1150, 1151, 1152, 1153, 1154, 1155, -1, 1144, 1159, 1160,
			1161, 1162, 1163, 1164, 1165, 1166, 1156, -1, 1167, 1168, 1169,
			1170 };

	/*
	 * btw client automaticaly filters them, no need to check doomsayer for
	 * those messages, just send
	 */
	public static final int SINKHOLE_MESSAGE = 0, GOBLIN_RAID_MESSAGE = 1,
			DEMON_RAID_MESSAGE = 2, WILDERNESS_WARBAND_MESSAGE = 3,
			WORLD_EVENT_MESSAGE = 4;

	private static final int[] DOOMSAYER_MESSAGES_BIT_VARS = { 9784, 16442,
			17864, 18271, 21225 };

	public DoomsayerManager() {
		/*
		 * 0-5 warning on locked 6 - warning on 7 - warning off
		 */
		warnings = new int[DOOMSAYER_WARNINGS_BIT_VARS.length];
		/*
		 * false - on true - off
		 */
		messages = new boolean[DOOMSAYER_MESSAGES_BIT_VARS.length];
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		refreshWarnings();
		refreshMessages();
	}

	public void openDoomsayer() {
		player.stopAll();
		player.getInterfaceManager().sendCentralInterface(583);
	}

	private void refreshWarnings() {
		for (int i = 0; i < warnings.length; i++)
			if (DOOMSAYER_WARNINGS_BIT_VARS[i] != -1)
				refreshWarning(i);
	}

	private void refreshMessages() {
		for (int i = 0; i < messages.length; i++)
			if (DOOMSAYER_MESSAGES_BIT_VARS[i] != -1)
				refreshMessage(i);
	}

	private void refreshWarning(int id) {
		player.getVarsManager()
				.sendVarBit(
						DOOMSAYER_WARNINGS_BIT_VARS[id],
						id == CLAN_WARS_SAFE_WARNING
								|| id == CLAN_WARS_DANGEROUS_WARNING ? (warnings[id] == 7 ? 1
								: 0)
								: warnings[id]);
	}

	private void refreshMessage(int id) {
		player.getVarsManager().sendVarBit(DOOMSAYER_MESSAGES_BIT_VARS[id],
				messages[id] ? 1 : 0);
	}

	public void toogleWarning(int id) {
		toogleWarning(id, true);
	}

	public void toogleWarning(int id, boolean message) {
		// this one cant be turned on in interface no mater how many times you
		// acess the area
		if ((id == CLAN_WARS_SAFE_WARNING || id == CLAN_WARS_DANGEROUS_WARNING)
				&& message && warnings[id] != 7) {
			player.getPackets().sendGameMessage(
					"You may turn this warning off when you enter the "
							+ (id == 22 ? "safe" : "dangerous")
							+ " free-for-all arena.");
			return;
		}
		if (warnings[id] < 6 && message) {
			player.getPackets()
					.sendGameMessage(
							"You cannot toogle this warning screen on or off. You need to go to the area it is linked to enough times to have the option to do so.");
			return;
		}
		warnings[id] = warnings[id] != 7 ? 7 : 6;
		refreshWarning(id);
		if (message)
			player.getPackets()
					.sendGameMessage(
							warnings[id] == 7 ? "You have toggled this warning screen off. You will no longer see this warning screen."
									: "You have toggled this warning screen on. You will see this interface again.");
	}

	public boolean isWarningOff(int id) {
		return warnings[id] == 7;
	}

	public boolean isMessageOff(int id) {
		return messages[id];
	}

	public void toogleCurrentWarning() {
		toogleWarning(player.getVarsManager()
				.getBitValue(TOOGLE_WARNING_VARBIT), false);
	}

	public static final int NORMAL_WARNING = 0, WILDERNESS_DITCH_WARNING = 1,
			CRUCIBLE_WARNING_MESSAGE = 2, CLAN_WARS_WARNING_MESSAGE = 3;

	public void openWarning(int type, int id, String warningMessage,
			String enterMessage) {
		if (type == NORMAL_WARNING) {
			player.getInterfaceManager().sendCentralInterface(1262);
			if (enterMessage != null)
				player.getPackets()
						.sendIComponentText(1262, 15, warningMessage);
			if (enterMessage != null)
				player.getPackets().sendIComponentText(1262, 18, enterMessage);
			player.getPackets().sendHideIComponent(1262, 10, false); // warning
		} else if (type == WILDERNESS_DITCH_WARNING)
			player.getInterfaceManager().sendCentralInterface(382);
		else if (type == CRUCIBLE_WARNING_MESSAGE)
			player.getInterfaceManager().sendCentralInterface(1292);
		else if (type == CLAN_WARS_WARNING_MESSAGE) {
			player.getInterfaceManager().sendCentralInterface(793);
			player.getVarsManager().sendVarBit(4094,
					id == CLAN_WARS_DANGEROUS_WARNING ? 1 : 0);
		}
		player.getVarsManager().sendVarBit(TOOGLE_WARNING_VARBIT, id);

	}

	public void increaseWarningCount(int id) {
		if (warnings[id] >= 6)
			return;
		warnings[id]++;
	}

	public void toogleMessage(int id) {
		messages[id] = !messages[id];
		refreshMessage(id);
		String message = messages[id] ? "You will no longer " : "You will now ";
		if (id == SINKHOLE_MESSAGE)
			message += "be notified when a sinkhole is about to spawn";
		else if (id == GOBLIN_RAID_MESSAGE)
			message += messages[id] ? "see notifications when goblins begin raiding"
					: "be notified when there is a goblin raid in the progress";
		else if (id == DEMON_RAID_MESSAGE)
			message += messages[id] ? "see notifications when demons begin invading"
					: "be notified when there is a demonic incursion in the progress";
		else if (id == WILDERNESS_WARBAND_MESSAGE)
			message += messages[id] ? "see notifications when when wilderness warbands events begin"
					: "be notified when there is a wilderness warband camp in progress";
		else if (id == WORLD_EVENT_MESSAGE)
			message += messages[id] ? "see notifications of news concerning a world event"
					: "be notified of news concerning a world event";
		message += ".";
		player.getPackets().sendGameMessage(message);
	}

}
