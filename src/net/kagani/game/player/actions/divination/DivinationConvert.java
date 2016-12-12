package net.kagani.game.player.actions.divination;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;

/**
 * 
 * @author Trenton
 * 
 */
public class DivinationConvert extends Action {

	// TODO boon xp boosts and it's done

	public enum ConvertMode {
		CONVERT_TO_ENERGY, CONVERT_TO_XP, CONVERT_TO_MORE_XP;
	}

	private MemoryInfo info;
	private boolean enriched;
	private ConvertMode mode;

	public DivinationConvert(Player player, Object[] args) {
		setMode((ConvertMode) args[0]);
		checkAll(player);
	}

	public boolean checkAll(Player player) {
		for (Item item : player.getInventory().getItems().getItems()) {
			if (item == null)
				continue;
			for (MemoryInfo i : MemoryInfo.values()) {
				if (item.getId() == i.getMemoryId()) {
					info = i;
					enriched = false;
					return true;
				}
				if (item.getId() == i.getEnrichedMemoryId()) {
					info = i;
					enriched = true;
					return true;
				}
			}
		}
		player.getPackets().sendGameMessage("You don't have any memories to convert.");
		return false;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (!checkAll(player)) {
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		switch (mode) {
		case CONVERT_TO_ENERGY:
			player.setNextAnimation(new Animation(21232));
			player.setNextGraphics(new Graphics(4239));
			player.getSkills().addXp(Skills.DIVINATION, 1);
			player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
			player.getInventory().addItem(info.getEnergyId(), 3);
			break;
		case CONVERT_TO_XP:
			player.setNextAnimation(new Animation(21234));
			player.setNextGraphics(new Graphics(4240));
			player.getSkills().addXp(Skills.DIVINATION, info.getXp());
			player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
			break;
		case CONVERT_TO_MORE_XP:
			if (!player.getInventory().containsItem(info.getEnergyId(), 5)) {
				setMode(ConvertMode.CONVERT_TO_XP);
				player.setNextAnimation(new Animation(21234));
				player.setNextGraphics(new Graphics(4240));
				player.getSkills().addXp(Skills.DIVINATION, info.getXp());
				player.getInventory().deleteItem(enriched ? info.getEnrichedMemoryId() : info.getMemoryId(), 1);
			} else {
				player.setNextAnimation(new Animation(21234));
				player.setNextGraphics(new Graphics(4240));
				player.getSkills().addXp(Skills.DIVINATION, info.getXp());
			}
			if (player.getInventory().containsItem(info.getEnrichedMemoryId(), 1)) {
				player.getInventory().deleteItem(info.getEnrichedMemoryId(), 1);
			} else {
				player.getInventory().deleteItem(info.getMemoryId(), 1);
			}
			player.getInventory().deleteItem(info.getEnergyId(), 5);
			break;
		}
		return 2;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 2);
	}

	public boolean isEnriched() {
		return enriched;
	}

	public void setEnriched(boolean enriched) {
		this.enriched = enriched;
	}

	public MemoryInfo getInfo() {
		return info;
	}

	public void setInfo(MemoryInfo info) {
		this.info = info;
	}

	public ConvertMode getMode() {
		return mode;
	}

	public void setMode(ConvertMode mode) {
		this.mode = mode;
	}
}