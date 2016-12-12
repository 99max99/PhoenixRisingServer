package net.kagani.game.player.content;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.player.CombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.PlayerCombatNew;
import net.kagani.utils.Utils;

public class Consumables {

	private static final int COOKING_LEVEL_OPCODE = 2951, HEAL_MODIFIER = 25;
	public static final Animation EAT_ANIM = new Animation(18001), COMBAT_EAT_ANIM = new Animation(18002);

	public static final int getCookingLevel(int id) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
		return defs.isNoted() ? 0 : defs.getCSOpcode(COOKING_LEVEL_OPCODE);
	}

	public static boolean isConsumable(Item item) {
		return getCookingLevel(item.getId()) > 0;
	}

	public static enum Effect {
		SUMMER_PIE(7218, 7220) {

			@Override
			public void activateEffect(Player player) {
				int runEnergy = (int) (player.getRunEnergy() * 1.1);
				if (runEnergy > 100)
					runEnergy = 100;
				player.setRunEnergy(runEnergy);
				int level = player.getSkills().getLevel(Skills.AGILITY);
				int realLevel = player.getSkills().getLevelForXp(Skills.AGILITY);
				player.getSkills().set(Skills.AGILITY, level >= realLevel ? realLevel + 5 : level + 5);
			}

		},

		GARDEN_PIE(7178, 7180) {

			@Override
			public void activateEffect(Player player) {
				int level = player.getSkills().getLevel(Skills.FARMING);
				int realLevel = player.getSkills().getLevelForXp(Skills.FARMING);
				player.getSkills().set(Skills.FARMING, level >= realLevel ? realLevel + 3 : level + 3);
			}

		},

		FISH_PIE(7188, 7190) {

			@Override
			public void activateEffect(Player player) {
				int level = player.getSkills().getLevel(Skills.FISHING);
				int realLevel = player.getSkills().getLevelForXp(Skills.FISHING);
				player.getSkills().set(Skills.FISHING, level >= realLevel ? realLevel + 3 : level + 3);
			}
		},

		ADMIRAL_PIE(7198, 7200) {
			@Override
			public void activateEffect(Player player) {
				int level = player.getSkills().getLevel(Skills.FISHING);
				int realLevel = player.getSkills().getLevelForXp(Skills.FISHING);
				player.getSkills().set(Skills.FISHING, level >= realLevel ? realLevel + 5 : level + 5);
			}
		},

		WILD_PIE(7208, 7210) {
			@Override
			public void activateEffect(Player player) {
				int level = player.getSkills().getLevel(Skills.SLAYER);
				int realLevel = player.getSkills().getLevelForXp(Skills.SLAYER);
				player.getSkills().set(Skills.SLAYER, level >= realLevel ? realLevel + 4 : level + 4);
				int level2 = player.getSkills().getLevel(Skills.RANGE);
				int realLevel2 = player.getSkills().getLevelForXp(Skills.RANGE);
				player.getSkills().set(Skills.RANGE, level2 >= realLevel2 ? realLevel2 + 4 : level2 + 4);
			}
		},

		SPICY_STEW_EFFECT(7513) {
			@Override
			public void activateEffect(Player player) {
				if (Utils.random(100) > 5) {
					int level = player.getSkills().getLevel(Skills.COOKING);
					int realLevel = player.getSkills().getLevelForXp(Skills.COOKING);
					player.getSkills().set(Skills.COOKING, level >= realLevel ? realLevel + 6 : level + 6);
				} else {
					int level = player.getSkills().getLevel(Skills.COOKING);
					player.getSkills().set(Skills.COOKING, level <= 6 ? 0 : level - 6);
				}
			}

		},

		CABAGE_MESSAGE(1965) {
			@Override
			public void activateEffect(Player player) {
				player.getPackets().sendGameMessage("You don't really like it much.", true);
			}
		},

		ONION_MESSAGE(1957) {
			@Override
			public void activateEffect(Player player) {
				player.getPackets().sendGameMessage(
						"It hurts to see a grown " + (player.getAppearence().isMale() ? "male" : "female") + " cry.");
			}
		},

		POISION_KARMAMWANNJI_EFFECT(3146) {
			@Override
			public void activateEffect(Player player) {
				player.applyHit(new Hit(player, 50, HitLook.POISON_DAMAGE));
			}
		},
		PURPLE_SWEET(10476) {
			@Override
			public void activateEffect(Player player) {
				int newRunEnergy = (int) (player.getRunEnergy() * 1.2);
				player.setRunEnergy(newRunEnergy > 100 ? 100 : newRunEnergy);
			}
		},
		BISCUITS(19467) {
			@Override
			public void activateEffect(Player player) {
				player.getPrayer().restorePrayer(10);
			}
		},
		RAW_CAVE_POTATO(17817) {

			@Override
			public void activateEffect(Player player) {
				player.getPackets().sendGameMessage("You must really be hungry.");
			}
		},

		ROCKTAIL_EFFECT(15272) {
			@Override
			public int getHitpointsModification(Player player) {
				return (int) (player.getMaxHitpoints() * 0.10);
			}
		},

		ROCKTAIL_SOUP_EFFECT(26313) {
			@Override
			public int getHitpointsModification(Player player) {
				return (int) (player.getMaxHitpoints() * 0.15);
			}
		};

		private int[] id;

		private Effect(int... id) {
			this.id = id;
		}

		public int getHitpointsModification(Player player) {
			return 0;
		}

		public void activateEffect(Player player) {
		}

		public static Effect forId(int id) {
			for (Effect effect : Effect.values()) {
				for (int requestId : effect.id)
					if (requestId == id)
						return effect;
			}
			return null;
		}
	}

	public static boolean eat(Player player, int slot, Item item) {
		if (!isConsumable(item))
			return false;
		if (player.getFoodDelay() > Utils.currentTimeMillis() || player.getPotDelay() > Utils.currentTimeMillis())
			return true;
		int heal = getHealAmount(player, item);
		if (!player.getControlerManager().canEat(heal))
			return true;
		player.stopAll(false, false);
		player.setNextAnimation((player.getCombatDefinitions().isCombatStance() && !player.isLegacyMode())
				? COMBAT_EAT_ANIM : EAT_ANIM);

		int pieces = item.getDefinitions().getCSOpcode(2970);
		player.addFoodDelay(pieces == 2 ? 600 : 1800);
		PlayerCombatNew.addCombatDelay(player, 3);// TODO test

		int nextId = item.getId() + 2, nextOpcode = ItemDefinitions.getItemDefinitions(nextId).getCSOpcode(2281);
		if (nextOpcode == 0 || nextOpcode != item.getDefinitions().getCSOpcode(2281))
			nextId = -1;
		String name = item.getDefinitions().getName().toLowerCase(), message = "You eat the " + name + ".";
		if (pieces == 3) {
			if (nextId != -1)
				message = "You eat a part of the " + name + ".";
		} else if (pieces == 2) {
			if (nextId == -1)
				message = "You eat the remaining " + name + ".";
			else
				message = "You eat half of the " + name + ".";
		}
		player.getPackets().sendGameMessage(message, true);

		boolean hasEffect = item.getDefinitions().getCSOpcode(2665) == 1;
		int hpMod = 0;
		if (hasEffect) {
			Effect effect = Effect.forId(item.getId());
			if (effect != null) {
				effect.activateEffect(player);
				hpMod = effect.getHitpointsModification(player);
			}
		}
		player.heal(heal, hpMod, 0, true);
		player.getInventory().replaceItem(nextId, item.getAmount(), slot);
		if (player.getCombatDefinitions().getCurrentTarget() != null
				&& player.getCombatDefinitions().getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
			PlayerCombatNew.addAdrenaline(player, -10);
		return true;
	}

	public static int getHealAmount(Player player, Item item) {
		int level = getCookingLevel(item.getId());
		int heal = level * HEAL_MODIFIER;
		int pieces = item.getDefinitions().getCSOpcode(2970);
		int maxHeal = player.getSkills().getLevel(Skills.HITPOINTS) * HEAL_MODIFIER;
		if (heal > maxHeal)
			heal = maxHeal;
		if (pieces > 0)
			heal /= pieces;
		if (heal < 200)
			heal = 200;
		return heal;
	}

	public static int getNextFoodSlot(Player player) {
		Item[] inv = player.getInventory().getItems().getItems();
		for (int slot = 0; slot < inv.length; slot++) {
			Item item = inv[slot];
			if (item == null)
				continue;
			if (isConsumable(item))
				return slot;
		}
		return -1;
	}
}