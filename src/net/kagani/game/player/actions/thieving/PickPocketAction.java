package net.kagani.game.player.actions.thieving;

import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.content.skillertasks.SkillTasks;
import net.kagani.utils.Utils;

/**
 * Handels the pick pocketing.
 * 
 * @author Raghav/Own4g3
 * 
 */
public class PickPocketAction extends Action {

	/**
	 * Pick pocketing npc.
	 */
	private NPC npc;

	/**
	 * Data of an npc.
	 */
	private PickPocketableNPC npcData;

	/**
	 * The npc stun animation.
	 */
	private static final Animation STUN_ANIMATION = new Animation(422),

	/**
	 * The pick pocketing animation.
	 */
	PICKPOCKETING_ANIMATION = new Animation(881),

	/**
	 * The double loot animation.
	 */
	DOUBLE_LOOT_ANIMATION = new Animation(5074),

	/**
	 * The triple loot animation.
	 */
	TRIPLE_LOOT_ANIMATION = new Animation(5075),

	/**
	 * The quadruple loot animation.
	 */
	QUADRUPLE_LOOT_ANIMATION = new Animation(5078);

	/**
	 * The double loot gfx.
	 */
	private static final Graphics DOUBLE_LOOT_GFX = new Graphics(873),

	/**
	 * The triple loot gfx.
	 */
	TRIPLE_LOOT_GFX = new Graphics(874),

	/**
	 * The quadruple loot gfx.
	 */
	QUADRUPLE_LOOT_GFX = new Graphics(875);

	/**
	 * The index to use in the levels required arrays.
	 */
	private int index;

	/**
	 * Constructs a new {@code PickpocketAction} {@code Object}.
	 * 
	 * @param npc
	 *            The npc to whom the player is pickpocketing.
	 * @param npcData
	 *            Data of an npc.
	 */
	public PickPocketAction(NPC npc, PickPocketableNPC npcData) {
		this.npc = npc;
		this.npcData = npcData;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			int thievingLevel = player.getSkills().getLevel(Skills.THIEVING);
			int agilityLevel = player.getSkills().getLevel(Skills.AGILITY);
			if (Utils.random(50) < 5) {
				for (int i = 0; i < 4; i++) {
					if (npcData.getThievingLevels()[i] <= thievingLevel
							&& npcData.getAgilityLevels()[i] <= agilityLevel)
						index = i;
				}
			}
			player.faceEntity(npc);
			player.setNextAnimation(getAnimation());
			player.setNextGraphics(getGraphics());
			player.getPackets().sendGameMessage(
					"You attempt to pick the "
							+ npc.getDefinitions().getName().toLowerCase()
							+ "'s pocket...", true);
			setActionDelay(player, 2);
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		if (!isSuccesfull(player)) {
			player.getPackets().sendGameMessage(
					"You fail to pick the "
							+ npc.getDefinitions().getName().toLowerCase()
							+ "'s pocket.");
			npc.faceEntity(player);
			player.setNextAnimation(new Animation(424));
			player.setNextGraphics(new Graphics(80, 5, 60));
			player.getPackets().sendGameMessage("You've been stuned.");
			player.applyHit(new Hit(player, npcData.getStunDamage(),
					HitLook.REGULAR_DAMAGE));
			if (npcData.equals(PickPocketableNPC.MASTER_FARMER)
					|| npcData.equals(PickPocketableNPC.FARMER)) {
				npc.setNextForceTalk(new ForceTalk(
						"Cor blimey mate, what are ye doing in me pockets?"));
				npc.setNextAnimation(STUN_ANIMATION);
			} else if (npcData.equals(PickPocketableNPC.DESERT_PHONIX))
				npc.setNextForceTalk(new ForceTalk("Squawk!"));
			else {
				npc.setNextForceTalk(new ForceTalk(
						"What do you think you're doing?"));
				npc.setNextAnimation(STUN_ANIMATION);
			}
			player.lock(npcData.getStunTime());
			stop(player);
		} else {
			player.getPackets().sendGameMessage(getMessage(player), true);
			double totalXp = npcData.getExperience();
			if (hasTheivingSuit(player))
				totalXp *= 1.025;
			player.getSkills().addXp(Skills.THIEVING, totalXp);
			for (int i = 0; i <= index; i++) {
				Item item = npcData.getLoot()[Utils
						.random(npcData.getLoot().length)];
				player.getSkillTasks().decreaseTask(SkillTasks.POCKET1);
				player.getSkillTasks().decreaseTask(SkillTasks.POCKET2);
				player.getSkillTasks().decreaseTask(SkillTasks.POCKET3);
				player.getSkillTasks().decreaseTask(SkillTasks.POCKET4);
				player.getInventory().addItemMoneyPouch(item);
			}
			if (player.getDailyTask() != null)
				player.getDailyTask().incrementTask(player, 1, npc.getId(),
						Skills.THIEVING);
		}
		return -1;
	}

	@Override
	public void stop(Player player) {
		npc.setNextFaceEntity(null);
		setActionDelay(player, 3);
	}

	private boolean hasTheivingSuit(Player player) {
		if (player.getEquipment().getHatId() == 21482
				&& player.getEquipment().getChestId() == 21480
				&& player.getEquipment().getLegsId() == 21481
				&& player.getEquipment().getBootsId() == 21483)
			return true;
		return false;
	}

	/**
	 * Checks if the player is succesfull to thiev or not.
	 * 
	 * @param player
	 *            The player.
	 * @return {@code True} if succesfull, {@code false} if not.
	 */
	private boolean isSuccesfull(Player player) {
		int thievingLevel = player.getSkills().getLevel(Skills.THIEVING);
		int levelRequirement = npcData.getThievingLevels()[0];
		int increasedChance = getIncreasedChance(player);

		double probability = (Utils.random(thievingLevel
				+ (levelRequirement / 6)) + increasedChance);

		if (levelRequirement < 10) {
			if (Utils.random(1, 5) == 4)
				return true;
			return false;
		}

		if (probability < levelRequirement / (Utils.random(2.35, 2.75)))
			return false;

		return true;
	}

	/**
	 * Gets the increased chance for succesfully pickpocketing.
	 * 
	 * @param player
	 *            The player.
	 * @return The amount of increased chance.
	 */
	private int getIncreasedChance(Player player) {
		int chance = (Utils.random(1,
				player.getSkills().getLevel(Skills.THIEVING) / 10));

		if (Equipment.getItemSlot(Equipment.SLOT_HANDS) == 10075)
			chance += 12;
		player.getEquipment();
		if (Equipment.getItemSlot(Equipment.SLOT_CAPE) == 15349)
			chance += 15;
		if (npc.getDefinitions().getName().contains("H.A.M")) {
			for (Item item : player.getEquipment().getItems().getItems()) {
				if (item != null
						&& item.getDefinitions().getName().contains("H.A.M")) {
					chance += 3;
				}
			}
		}
		return chance;
	}

	/**
	 * Gets the message to send when finishing.
	 * 
	 * @param player
	 *            The player.
	 * @return The message.
	 */
	private String getMessage(Player player) {
		switch (index) {
		case 0:
			return "You succesfully pick the "
					+ npc.getDefinitions().getName().toLowerCase()
					+ "'s pocket.";
		case 1:
			return "Your lighting-fast reactions allow you to steal double loot.";
		case 2:
			return "Your lighting-fast reactions allow you to steal triple loot.";
		case 3:
			return "Your lighting-fast reactions allow you to steal quadruple loot.";
		}
		return null;
	}

	/**
	 * Checks everything before starting.
	 * 
	 * @param player
	 *            The player.
	 * @return
	 */
	private boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Skills.THIEVING) < npcData
				.getThievingLevels()[0]) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a thieving level of "
							+ npcData.getThievingLevels()[0]
							+ " to steal from this npc.");
			return false;
		}
		if (player.getInventory().getFreeSlots() < 1) {
			player.getPackets().sendGameMessage(
					"You don't have enough space in your inventory.");
			return false;
		}
		if (player.getAttackedBy() != null
				&& player.getAttackedByDelay() > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage(
					"You can't do this while you're under combat.");
			return false;
		}
		if (npc.getAttackedBy() != null
				&& npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("The npc is under combat.");
			return false;
		}
		if (npc.isDead()) {
			player.getPackets().sendGameMessage("Too late, the npc is dead.");
			return false;
		}
		return true;

	}

	/**
	 * Gets the animation to perform.
	 * 
	 * @param player
	 *            The player.
	 * @return The animation.
	 */
	private Animation getAnimation() {
		switch (index) {
		case 0:
			return PICKPOCKETING_ANIMATION;
		case 1:
			return DOUBLE_LOOT_ANIMATION;
		case 2:
			return TRIPLE_LOOT_ANIMATION;
		case 3:
			return QUADRUPLE_LOOT_ANIMATION;
		}
		return null;
	}

	/**
	 * Gets the graphic to perform.
	 * 
	 * @param player
	 *            The player.
	 * @return The graphic.
	 */
	private Graphics getGraphics() {
		switch (index) {
		case 0:
			return null;
		case 1:
			return DOUBLE_LOOT_GFX;
		case 2:
			return TRIPLE_LOOT_GFX;
		case 3:
			return QUADRUPLE_LOOT_GFX;
		}
		return null;
	}
}