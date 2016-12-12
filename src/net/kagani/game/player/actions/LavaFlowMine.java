package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.WorldObject;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.mining.MiningBase;
import net.kagani.utils.Utils;

public class LavaFlowMine extends MiningBase {

	/**
	 * @author: Dylan Page
	 */

	private WorldObject object;

	public LavaFlowMine(WorldObject object) {
		this.object = object;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		setActionDelay(player, getMiningDelay(player));
		return true;
	}

	private int getMiningDelay(Player player) {
		int summoningBonus = 0;
		if (player.getFamiliar() != null) {
			if (player.getFamiliar().getId() == 7342
					|| player.getFamiliar().getId() == 7342)
				summoningBonus += 10;
			else if (player.getFamiliar().getId() == 6832
					|| player.getFamiliar().getId() == 6831)
				summoningBonus += 1;
		}
		int oreBaseTime = 50;
		int oreRandomTime = 20;
		int mineTimer = oreBaseTime
				- (player.getSkills().getLevel(Skills.MINING) + summoningBonus)
				- Utils.random(pickaxeTime);
		if (mineTimer < 1 + oreRandomTime)
			mineTimer = 1 + Utils.random(oreRandomTime);
		mineTimer /= player.getAuraManager().getMininingAccurayMultiplier();
		return mineTimer;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(getEmoteId(player.getEquipment()
				.getWeaponId())));
		player.faceObject(object);
		if (checkAll(player)) {
			if (Utils.random(25) < 1) {
				player.stopAll();
			}
			if (Utils.random(18) == 0) {
				addXP(player);
			}
			return true;
		}
		return false;
	}

	private boolean checkAll(Player player) {
		if (!hasPickaxe(player)) {
			player.getPackets().sendGameMessage(
					"You dont have the required level to use this pickaxe.");
			return false;
		}
		if (!hasMiningLevel(player)) {
			return false;
		}
		return true;
	}

	private boolean hasMiningLevel(Player player) {
		if (68 > player.getSkills().getLevel(Skills.MINING)) {
			player.getPackets().sendGameMessage(
					"You need a mining level of 68 to mine this rock.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		addXP(player);
		return getMiningDelay(player);
	}

	private void addXP(Player player) {
		double totalXp = Utils.random(47, 59);
		if (hasMiningSuit(player))
			totalXp *= 1.056;
		player.getSkills().addXp(Skills.MINING, totalXp);
		player.getPackets().sendGameMessage("You mine away some crust.", true);
	}

	private boolean hasMiningSuit(Player player) {
		if (player.getEquipment().getHatId() == 20789
				&& player.getEquipment().getChestId() == 20791
				&& player.getEquipment().getBootsId() == 20787
				&& player.getEquipment().getLegsId() == 20790
				&& player.getEquipment().getBootsId() == 20788)
			return true;
		return false;
	}

	private int getEmoteId(int pickaxe) {
		switch (pickaxe) {
		case 15259: // dragon pickaxe
			return 12190;
		case 1275: // rune pickaxe
			return 624;
		case 1271: // adam pickaxe
			return 628;
		case 1273: // mith pickaxe
			return 629;
		case 1269: // steel pickaxe
			return 627;
		case 1267: // iron pickaxe
			return 626;
		case 1265: // bronze axe
			return 625;
		case 32646: // crystal axe
			return 25159;
		case 13661: // Inferno adze
			return 10222;
		}
		return 625;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}