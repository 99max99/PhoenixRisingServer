package net.kagani.game.player.content;

import java.util.List;
import java.util.Map;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Entity;
import net.kagani.game.Region;
import net.kagani.game.World;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.CombatDefinitions;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public final class Combat {

	public static final int MELEE_TYPE = 0, RANGE_TYPE = 1, MAGIC_TYPE = 2, ALL_TYPE = 3;

	public static final double WEAKNESS_MULTIPLIER_STYLE = 0.7, WEAKNESS_MULTIPLIER_WEAK = 0.65,
			WEAKNESS_MULTIPLIER_NEUTRAL = 0.55, WEAKNESS_MULTIPLIER_STRONG = 0.45;

	public static final int NONE_STYLE = 0, ARROW_STYLE = 8, BOLT_STYLE = 9, THROWN_STYLE = 10;

	public static final int TYPE_AIR = 1, TYPE_WATER = 2, TYPE_EARTH = 3, TYPE_FIRE = 4;

	public static double getWeaknessMultiplier(double totalArmor, double weakArmor, double neutralArmor,
			double strongArmor) {
		return totalArmor == 0 ? WEAKNESS_MULTIPLIER_NEUTRAL
				: ((weakArmor * WEAKNESS_MULTIPLIER_WEAK + neutralArmor * WEAKNESS_MULTIPLIER_NEUTRAL
						+ strongArmor * WEAKNESS_MULTIPLIER_STRONG) / totalArmor);
	}

	public static int[] COMBAT_SKILLS = { Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH, Skills.RANGE, Skills.MAGIC };

	public static boolean rollHit(double att, double def) {
		if (att < 0) // wont happen unless low att lv plus negative bonus
			return false;
		if (def < 0) // wont happen unless low def lv plus negative bonus
			return true;
		return Utils.random((int) (att + def)) >= def;
	}

	public static int getStyleType(int style) {
		if (style >= 1 && style <= 4)
			return MAGIC_TYPE;
		if (style >= 5 && style <= 7)
			return MELEE_TYPE;
		if (style >= 8 && style <= 10)
			return RANGE_TYPE;
		return ALL_TYPE;
	}

	public static void giveXP(Entity from, double totalXP) {
		if (totalXP == 0)
			return;
		Map<Entity, Integer> dmgReceived = from.getReceivedDamage();

		int totalDmgReceived = from.getTotalDamageReceived();

		for (Entity source : dmgReceived.keySet()) {
			if (!(source instanceof Player))
				continue;
			Integer dmgDealt = dmgReceived.get(source);
			double receivedXP = dmgDealt * totalXP / totalDmgReceived;

			Player player = (Player) source;
			player.getSkills().addXp(Skills.HITPOINTS, receivedXP / 3);
			int combatStyle = player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON);
			if (combatStyle == Combat.MELEE_TYPE || combatStyle == Combat.ALL_TYPE) {
				switch (player.getCombatDefinitions().getMeleeCombatExperience()) {
				case 0:
					player.getSkills().addXp(Skills.ATTACK, receivedXP / 3);
					player.getSkills().addXp(Skills.STRENGTH, receivedXP / 3);
					player.getSkills().addXp(Skills.DEFENCE, receivedXP / 3);
					break;
				case 1:
					player.getSkills().addXp(Skills.ATTACK, receivedXP);
					break;
				case 2:
					player.getSkills().addXp(Skills.STRENGTH, receivedXP);
					break;
				case 3:
					player.getSkills().addXp(Skills.DEFENCE, receivedXP);
					break;
				}
			} else if (combatStyle == Combat.RANGE_TYPE) {
				switch (player.getCombatDefinitions().getRangedCombatExperience()) {
				case 0:
					player.getSkills().addXp(Skills.RANGE, receivedXP / 2);
					player.getSkills().addXp(Skills.DEFENCE, receivedXP / 2);
					break;
				case 1:
					player.getSkills().addXp(Skills.RANGE, receivedXP);
					break;
				case 2:
					player.getSkills().addXp(Skills.DEFENCE, receivedXP);
					break;
				}
			} else if (combatStyle == Combat.MAGIC_TYPE) {
				switch (player.getCombatDefinitions().getMagicCombatExperience()) {
				case 0:
					player.getSkills().addXp(Skills.MAGIC, receivedXP / 2);
					player.getSkills().addXp(Skills.DEFENCE, receivedXP / 2);
					break;
				case 1:
					player.getSkills().addXp(Skills.MAGIC, receivedXP);
					break;
				case 2:
					player.getSkills().addXp(Skills.DEFENCE, receivedXP);
					break;
				}
			}
			if (Settings.DEBUG)
				System.out.println(from.getName() + " gave: " + totalXP * Settings.XP_RATE + "xp");
		}

	}

	public static double getHitChance(Entity from, Entity target, int attackStyle, boolean mainHand) {
		double accuracy;
		int attackType = from instanceof Player ? Combat.getStyleType(attackStyle) : attackStyle;
		if (from instanceof Player) {
			// This handles PVP
			Player player = (Player) from;
			accuracy = player.getCombatDefinitions().getSkillAccuracy(!mainHand)
					+ (player.getCombatDefinitions().getStats()[mainHand ? CombatDefinitions.MAINHAND_ACCURACY
							: CombatDefinitions.OFFHAND_ACCURACY] * 10);
			accuracy -= player.getCombatDefinitions().getStats()[attackType == MAGIC_TYPE
					? CombatDefinitions.MAGE_ACCURACY_PENALTY
							: attackType == RANGE_TYPE ? CombatDefinitions.RANGE_ACCURACY_PENALTY
									: CombatDefinitions.MELEE_ACCURACY_PENALTY];
			// System.out.println(Arrays.toString(player.getCombatDefinitions().getStats()));

			accuracy *= player.getPrayer().getCombatRatingMultiplier(attackType);

			if (attackType == RANGE_TYPE) {
				int rangeLevel = player.getSkills().getLevel(4);
				accuracy *= Utils.random(1.0, rangeLevel > 10 ? (1.125 * 15) / (rangeLevel / 10) : 1.5);
			}

		} else {
			accuracy = ((NPC) from).getBonuses()[3 + attackType] * 10;
			accuracy += accuracy / 2.5; // lvl stat
		}

		if (target instanceof Player)
			accuracy *= ((Player) target).getPrayer().getEnemyCombatRatingMultiplier(attackType);

		double defence;

		if (target instanceof Player) {
			Player playerTarget = (Player) target;
			defence = playerTarget.getCombatDefinitions().getDefenceArmor()
					+ playerTarget.getCombatDefinitions().getStats()[CombatDefinitions.ARMOR];

			if (playerTarget.getEffectsManager().hasActiveEffect(EffectType.BERSERK))
				defence *= 0.5;

			double multiplier = playerTarget.getCombatDefinitions().getStats()[attackType == MELEE_TYPE
					? CombatDefinitions.ARMOR_MELEE_WEAKNESS
							: attackType == RANGE_TYPE ? CombatDefinitions.ARMOR_RANGE_WEAKNESS
									: CombatDefinitions.ARMOR_MAGIC_WEAKNESS];

			int styleBonus = (int) (defence * (Combat.WEAKNESS_MULTIPLIER_NEUTRAL - (multiplier / 100)));

			defence += styleBonus;

			double prayerMultiplier = playerTarget.getPrayer().getDefenceRatingMultiplier();
			if (from instanceof NPC) {
				NPC npc = (NPC) from;

				if (npc.getCombatLevel() > 300) {
					if (playerTarget.getPrayer().isMeleeProtecting()) {
						int randomInt = Utils.random(100);
						if (randomInt != 8)
							prayerMultiplier *= 3;
						else
							prayerMultiplier *= 1.75;
					} else {
						prayerMultiplier *= 1.75;
					}
				}
			}

			defence *= prayerMultiplier;
		} else {
			NPC npcTarget = (NPC) target;
			int weaknessStyle = npcTarget.getWeaknessStyle();

			double multiplier = Combat.WEAKNESS_MULTIPLIER_NEUTRAL;

			if (attackStyle == weaknessStyle)
				multiplier = Combat.WEAKNESS_MULTIPLIER_STYLE; // less 15% def
			// bonus
			else if (attackType == npcTarget.getStrengthType())
				multiplier = Combat.WEAKNESS_MULTIPLIER_STRONG; // extra 10% def
			// bonus
			else if (attackType == Combat.getStyleType(weaknessStyle))
				multiplier = Combat.WEAKNESS_MULTIPLIER_WEAK; // less 10% def
			// bonus

			defence = npcTarget.getBonuses()[6] * 10;
			defence += defence / 2.3; // lvl stat

			int styleBonus = (int) (defence * (Combat.WEAKNESS_MULTIPLIER_NEUTRAL - multiplier));

			defence += styleBonus;

		}

		if (from instanceof Player)
			defence *= ((Player) from).getPrayer().getEnemyDefenceRatingMultiplier();

		if (from.getEffectsManager().hasActiveEffect(EffectType.SMOKE_EFFECT))
			accuracy *= (double) from.getEffectsManager().getEffectForType(EffectType.SMOKE_EFFECT).getArguments()[0];
		if (from.getEffectsManager().hasActiveEffect(EffectType.CONFUSE_EFFECT))
			accuracy *= (double) from.getEffectsManager().getEffectForType(EffectType.CONFUSE_EFFECT).getArguments()[0];
		if (from.getEffectsManager().hasActiveEffect(EffectType.STAGGER_EFFECT))
			accuracy *= (double) from.getEffectsManager().getEffectForType(EffectType.STAGGER_EFFECT).getArguments()[0];

		return (accuracy / defence * Combat.WEAKNESS_MULTIPLIER_NEUTRAL) * 100;
	}

	public static double getAccuracy(double tierLevel) {
		return (0.0008 * Math.pow(tierLevel, 3) + tierLevel * 4 + 40) * 10;
	}

	public static boolean hasAntiDragProtection(Entity target) {
		if (target instanceof NPC)
			return false;
		Player p2 = (Player) target;
		int shieldId = p2.getEquipment().getShieldId();
		return shieldId == 1540 || shieldId == 11283 || shieldId == 11284 || shieldId == 16933;
	}

	public static boolean hasDarkbow(Player player) {
		int weaponId = player.getEquipment().getWeaponId();
		return (weaponId == 11235 || weaponId >= 15701 && weaponId <= 15704);
	}

	public static boolean hasAscensionCrossbow(Player player, boolean mainHand) {
		Item item = player.getEquipment().getItem(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD);
		if (item != null)
			return item.getName().contains(mainHand ? "Ascension crossbow" : "Off-hand Ascension crossbow");
		return false;
	}

	public static final boolean usingGoliathGloves(Player player) {
		String name = player.getEquipment().getItem(Equipment.SLOT_SHIELD) != null
				? player.getEquipment().getItem(Equipment.SLOT_SHIELD).getDefinitions().getName().toLowerCase() : "";
				if (player.getEquipment().getItem((Equipment.SLOT_HANDS)) != null) {
					if (player.getEquipment().getItem(Equipment.SLOT_HANDS).getDefinitions().getName().toLowerCase()
							.contains("goliath") && player.getEquipment().getWeaponId() == -1) {
						if (name.contains("defender") && name.contains("dragonfire shield"))
							return true;
						return true;
					}
				}
				return false;
	}

	public static final boolean fullVeracsEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Verac's");
	}

	public static final boolean fullGuthanEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Guthan's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Guthan's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Guthan's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Guthan's");
	}

	public static final boolean fullDharokEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Dharok's");
	}

	public static Entity getLastTarget(Player from) {
		Region region = World.getRegion(from.getRegionId());
		if (from.isCanPvp()) {
			List<Integer> playerIndexes = region.getPlayerIndexes();
			if (playerIndexes != null) {
				for (int playerIndex : playerIndexes) {
					Player player = World.getPlayers().get(playerIndex);
					if (player == null || player.isDead() || player.hasFinished() || !player.isCanPvp()
							|| (from.getAttackedBy() != player && from.getAttackedByDelay() > Utils.currentTimeMillis())
							|| player.getAttackedBy() != from || !from.withinDistance(player, 16))
						continue;
					return player;
				}
			}
		}
		List<Integer> npcsIndexes = region.getNPCsIndexes();
		if (npcsIndexes != null) {
			for (int npcIndex : npcsIndexes) {
				NPC npc = World.getNPCs().get(npcIndex);
				if (npc == null || npc.isDead() || npc.hasFinished()
						|| (from.getAttackedBy() != npc && from.getAttackedByDelay() > Utils.currentTimeMillis())
						|| npc.getAttackedBy() != from || !from.withinDistance(npc, 16))
					continue;
				return npc;
			}
		}
		return null;
	}

	public static boolean hasRingOfWealth(Player player) {
		int ringId = player.getEquipment().getRingId();
		return ringId == 2572 || (ringId >= 20653 && ringId <= 20659);
	}

	public static boolean hasAntiFireProtection(Entity target) {
		return target instanceof Player && ((Player) target).hasFireImmunity();
	}

	public static int getDefenceEmote(Entity target) {
		if (target instanceof NPC) {
			NPC n = (NPC) target;
			return n.getCombatDefinitions().getDefenceEmote();
		} else {
			Player p = (Player) target;
			boolean legacy = p.getCombatDefinitions().getCombatMode() == CombatDefinitions.LEGACY_COMBAT_MODE;
			if (p.getEquipment().getShieldId() != -1)
				return legacy ? 424 : 18346;
			Item weapon = p.getEquipment().getItem(Equipment.SLOT_WEAPON);
			if (weapon == null)
				return legacy ? 424 : 18346;
			int emote = weapon.getDefinitions().getCombatOpcode(legacy ? 4387 : 2917 /* 4371 */);
			return emote == 0 ? legacy ? 424 : 18346 : emote;
		}
	}

	public static boolean isUndead(Entity target) {
		if (target instanceof Player)
			return false;
		NPC npc = (NPC) target;
		String name = npc.getDefinitions().getName().toLowerCase();
		return name.contains("aberrant spectre") || name.contains("zombie") || name.contains("ankou")
				|| name.contains("crawling hand") || name.contains("ghost") || name.contains("ghast")
				|| name.contains("mummy") || name.contains("revenant") || name.contains("shade") || npc.getId() == 8125
				|| (npc.getId() >= 2044 && npc.getId() <= 2057) || name.contains("undead");

	}

	private Combat() {

	}
}