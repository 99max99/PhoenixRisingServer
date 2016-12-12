package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.player.content.reports.PlayerReporting;

public class PlayerExamineManager implements Serializable {

	private static final long serialVersionUID = 6506692526193840181L;

	private transient Player player;

	private byte status;
	private String personalMessage;
	public boolean privacyOn;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		refreshStatus();
		refreshPrivacyOn();
	}

	public void openExamineSettings() {
		if (player.getInterfaceManager().containsScreenInterface()
				|| player.getInterfaceManager().containsBankInterface()) {
			player.getPackets()
					.sendGameMessage(
							"Please finish what you are doing before opening the examine settings interface.");
			return;
		}
		player.stopAll();
		player.getInterfaceManager().sendCentralInterface(1561);
		refreshPersonalMessage();
	}

	/*
	 * if over existing ids it just shows 0 anyway
	 */
	public void setStatus(int status) {
		this.status = (byte) status;
		refreshStatus();
	}

	public void clearPersonalMessage() {
		personalMessage = null;
		refreshPersonalMessage();
	}

	private void refreshPersonalMessage() {
		player.getPackets().sendCSVarString(4670,
				personalMessage == null ? "" : personalMessage);
	}

	public void setPersonalMessage() {
		player.getPackets().sendInputLongTextScript(
				"Enter new personal message here:");
		player.getTemporaryAttributtes()
				.put(Key.PERSONAL_MESSAGE, Boolean.TRUE);
	}

	public void setPersonalMessage(String message) {
		personalMessage = message;
		refreshPersonalMessage();
	}

	public void switchPrivacyOn() {
		privacyOn = !privacyOn;
		refreshPrivacyOn();
	}

	public void refreshStatus() {
		player.getVarsManager().sendVarBit(26169, status);
	}

	public void refreshPrivacyOn() {
		player.getVarsManager().sendVarBit(26171, privacyOn ? 1 : 0);
	}

	public void openExamineDetails(Player target) {
		player.getInterfaceManager().setWindowInterface(
				InterfaceManager.PLAYER_EXAMINE_COMPONENT_ID, 1560);
		boolean privacyOn = target.getPlayerExamineManager().privacyOn;
		if (!privacyOn) {
			for (int varbit = 26071; varbit < 26123; varbit++) {
				int skill = Skills.FIXED_SLOTS[(varbit - 26071) / 2];
				player.getVarsManager().sendVarBit(
						varbit,
						varbit % 2 != 0 ? target.getSkills().getLevel(skill)
								: target.getSkills().getLevelForXp(skill));
			}
			player.getVarsManager().sendVar(4934,
					(int) target.getSkills().getXp(Skills.ATTACK));
			player.getVarsManager().sendVar(4935,
					(int) target.getSkills().getXp(Skills.STRENGTH));
			player.getVarsManager().sendVar(4936,
					(int) target.getSkills().getXp(Skills.DEFENCE));
			player.getVarsManager().sendVar(4937,
					(int) target.getSkills().getXp(Skills.RANGE));
			player.getVarsManager().sendVar(4938,
					(int) target.getSkills().getXp(Skills.PRAYER));
			player.getVarsManager().sendVar(4939,
					(int) target.getSkills().getXp(Skills.MAGIC));
			player.getVarsManager().sendVar(4940,
					(int) target.getSkills().getXp(Skills.RUNECRAFTING));
			player.getVarsManager().sendVar(4941,
					(int) target.getSkills().getXp(Skills.CONSTRUCTION));
			player.getVarsManager().sendVar(4942,
					(int) target.getSkills().getXp(Skills.DUNGEONEERING));
			player.getVarsManager().sendVar(4943,
					(int) target.getSkills().getXp(Skills.HITPOINTS));
			player.getVarsManager().sendVar(4944,
					(int) target.getSkills().getXp(Skills.AGILITY));
			player.getVarsManager().sendVar(4945,
					(int) target.getSkills().getXp(Skills.HERBLORE));
			player.getVarsManager().sendVar(4946,
					(int) target.getSkills().getXp(Skills.THIEVING));
			player.getVarsManager().sendVar(4947,
					(int) target.getSkills().getXp(Skills.CRAFTING));
			player.getVarsManager().sendVar(4948,
					(int) target.getSkills().getXp(Skills.FLETCHING));
			player.getVarsManager().sendVar(4949,
					(int) target.getSkills().getXp(Skills.SLAYER));
			player.getVarsManager().sendVar(4950,
					(int) target.getSkills().getXp(Skills.HUNTER));
			player.getVarsManager().sendVar(4951,
					(int) target.getSkills().getXp(Skills.DIVINATION));
			player.getVarsManager().sendVar(4952,
					(int) target.getSkills().getXp(Skills.MINING));
			player.getVarsManager().sendVar(4953,
					(int) target.getSkills().getXp(Skills.SMITHING));
			player.getVarsManager().sendVar(4954,
					(int) target.getSkills().getXp(Skills.FISHING));
			player.getVarsManager().sendVar(4955,
					(int) target.getSkills().getXp(Skills.COOKING));
			player.getVarsManager().sendVar(4956,
					(int) target.getSkills().getXp(Skills.FIREMAKING));
			player.getVarsManager().sendVar(4957,
					(int) target.getSkills().getXp(Skills.WOODCUTTING));
			player.getVarsManager().sendVar(4958,
					(int) target.getSkills().getXp(Skills.FARMING));
			player.getVarsManager().sendVar(4959,
					(int) target.getSkills().getXp(Skills.SUMMONING));

			player.getVarsManager()
					.sendVar(4960, target.isLegacyMode() ? 1 : 2);// Enables
			// something
			player.getVarsManager().sendVar(4961,
					target.getCombatDefinitions().getStats()[11]);// PvE
			// Dmg
			// reduction
			player.getVarsManager().sendVar(4962, target.getHitpoints());
			player.getVarsManager().sendVar(4963,
					target.getPrayer().getPrayerpoints() * 10);
			player.getVarsManager().sendVar(4964,
					target.getCombatDefinitions().getHandDamage(false) / 10);
			player.getVarsManager().sendVar(4965,
					target.getCombatDefinitions().getStats()[5]);

			player.getInterfaceManager().setInterface(true, 1560, 18, 1557);
			player.getInterfaceManager().setInterface(true, 1560, 17, 1559);
		}

		player.getInterfaceManager().setInterface(true, 1560, 16, 1558);

		Item[] items = target.getEquipment().getItems().getItemsCopy();
		if (privacyOn) {
			items[Equipment.SLOT_RING] = null;
			items[Equipment.SLOT_ARROWS] = null;
			items[Equipment.SLOT_AURA] = null;
			items[Equipment.SLOT_POCKET] = null;
			items[Equipment.SLOT_WINGS] = null;
		}
		player.getPackets().sendItems(742, items);
		player.getPackets().sendItems(
				743,
				privacyOn ? items : target.getEquipment().getCosmeticItems()
						.getItems());
		player.getPackets().sendIComponentSettings(1558, 16, 0, 18, 2046); // allows
		// to
		// click
		// items
		player.getPackets().sendCSVarString(4669, target.getDisplayName());
		String personalMessage = target.getPlayerExamineManager().personalMessage;
		player.getPackets().sendCSVarString(4671,
				personalMessage == null ? "" : personalMessage);// Personal
		// message
		// TODO
		player.getPackets().sendCSVarString(4672,
				target.getClanManager() != null ? target.getClanName() : "");

		player.getVarsManager().sendVarBit(26172,
				target.getPlayerExamineManager().status);// Status
		player.getVarsManager().sendVarBit(26173,
				target.getAppearence().getTitleId());
		player.getVarsManager().sendVar(4985,
				target.getPlayerExamineManager().privacyOn ? 1 : 0);

		// slot 0 - 18
		for (int i = 4986; i <= 5004; i++)
			player.getVarsManager().sendVar(i, -1); // slot stop flashing, but
		// also removes purcharse
		// option
		player.getPackets().sendOtherPlayerOnIComponent(1558, 19, target);
		player.getVarsManager().sendVar(5005,
				target.getAppearence().getRenderEmote());

		// player.getPackets().sendAppearanceLook2();
		player.getTemporaryAttributtes().put(Key.PLAYER_EXAMINE,
				target.getDisplayName());
	}

	public void closeExamineDetails() {
		player.getInterfaceManager().removeWindowInterface(
				InterfaceManager.PLAYER_EXAMINE_COMPONENT_ID);
		player.getTemporaryAttributtes().remove(Key.PLAYER_EXAMINE);
	}

	public void reportPlayer() {
		String report = (String) player.getTemporaryAttributtes().get(
				Key.PLAYER_EXAMINE);
		if (report == null)
			return;

		if (player.getRights() >= 1)
			player.getDialogueManager().startDialogue("AddOffenceD", report);
		else
			PlayerReporting.report(player, report);
	}
}