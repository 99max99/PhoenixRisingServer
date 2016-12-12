package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.clanwars.ClanWarRequestController;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.actions.PlayerCombatNew;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.controllers.DTControler;
import net.kagani.utils.Utils;

public final class CombatDefinitions implements Serializable {

	private static final long serialVersionUID = 2102201264836121104L;
	public static final int STAB_ATTACK = 0, SLASH_ATTACK = 1,
			CRUSH_ATTACK = 2, RANGE_ATTACK = 4, MAGIC_ATTACK = 3;
	public static final int STAB_DEF = 5, SLASH_DEF = 6, CRUSH_DEF = 7,
			RANGE_DEF = 9, MAGIC_DEF = 8, SUMMONING_DEF = 10;
	public static final int STRENGTH_BONUS = 14, RANGED_STR_BONUS = 15,
			MAGIC_DAMAGE = 17, PRAYER_BONUS = 16;
	public static final int ABSORVE_MELEE_BONUS = 11, ABSORVE_RANGE_BONUS = 13,
			ABSORVE_MAGE_BONUS = 12;
	public static final int MANUAL_COMBAT_MODE = 0, REVOLUTION_COMBAT_MODE = 1,
			MOMENTUM_COMBAT_MODE = 2, LEGACY_COMBAT_MODE = 3;

	public static final int SHARED = -1;
	private transient Player player;
	private transient boolean usingSpecialAttack;
	private transient int[] bonuses;
	private transient int[] stats;

	// saving stuff

	private byte specialAttackPercentage;
	private boolean autoRelatie;
	private boolean defensiveCasting;
	private transient boolean instantAttack;
	private transient boolean dungeonneringSpellBook;
	private transient boolean combatStance;
	private transient Entity currentTarget;
	private transient int currentTargetMaxHP, currentTargetHP;
	private transient Object[] currentTargetData;

	private transient long mainHandDelay, offHandDelay;

	public byte spellBook;
	private short mainHandSpell;
	private boolean sheathe;
	private transient boolean forceNoSheathe;
	private byte meleeCombatExperience;
	private byte rangedCombatExperience;
	private byte magicCombatExperience;
	private byte combatMode;
	private boolean manualCast;
	private boolean allowAbilityQueueing;
	private boolean showCombatModeIcon;

	public int getSpellId() {
		Integer tempCastSpell = (Integer) player.getTemporaryAttributtes().get(
				"tempCastSpell");
		if (tempCastSpell != null)
			return tempCastSpell + 256;
		return mainHandSpell;
	}

	public int getAutoCastSpell() {
		return mainHandSpell;
	}

	/*
	 * the only thing that removes autospell now is when u switch spellbook
	 */
	public void resetSpells(boolean removeAutoSpell) {
		player.getTemporaryAttributtes().remove("tempCastSpell");
		if (removeAutoSpell) {
			mainHandSpell = 0;
			refreshAutoCastSpell();
		}
	}

	public boolean isAllowAbilityQueueing() {
		return allowAbilityQueueing;
	}

	public void setAutoCastSpell(int id) {
		mainHandSpell = mainHandSpell == id ? 0 : (short) id;
		player.getPackets().sendGameMessage(
				mainHandSpell == 0 ? "Auto-cast spell cleared."
						: "Main-hand spell set to: "
								+ Magic.getSpellName(Magic.getSpellData(id))
								+ ".");
		refreshAutoCastSpell();
		if (player.getInterfaceManager().containsInterface(1462))
			player.getEquipment().refreshEquipmentInterfaceBonuses();
	}

	public void refreshAutoCastSpell() {
		player.getVarsManager().sendVarBit(43, mainHandSpell);
		setDefaultAbilityMenu(); // needs to be sent when setting autocast
	}

	public void refreshAutoAbilityTrigger() {
		player.getVarsManager().sendVarBit(21682,
				getCombatMode() == REVOLUTION_COMBAT_MODE ? 1 : 0);
	}

	public void setDefaultAbilityMenu() {
		setMagicAbilityMenu(player.isLegacyMode() ? 0 : 1);
	}

	public void setMagicAbilityMenu(int menu) {
		player.getVarsManager().forceSendVarBit(18791, menu);
	}

	public CombatDefinitions() {
		autoRelatie = true;
		sheathe = true;
		allowAbilityQueueing = true;
	}

	public boolean isSheathe() {
		return !player.isUnderCombat() && !forceNoSheathe && sheathe;
	}

	public void switchSheathe() {
		if (player.isUnderCombat()) {
			player.getPackets().sendGameMessage(
					"You can't do that while in combat.");
			return;
		}
		sheathe = !sheathe;
		player.setNextAnimation(new Animation(sheathe ? 18027 : 18028));
		player.getAppearence().generateAppearenceData();

	}

	public void setSpellBook(int id) {
		if (id == 3)
			dungeonneringSpellBook = true;
		else
			spellBook = (byte) id;
		resetSpells(true);
		refreshSpellBook();
	}

	public int getSpellBook() {
		return spellBook;

	}

	public boolean isDefensiveCasting() {
		return defensiveCasting;
	}

	public void refreshSpellBook() {
		player.getVarsManager().sendVarBit(0, spellBook);
		player.getVarsManager().sendVar(3226, -1); // charge staff option
		// appears in spells if not
		// -1
	}

	// TODO remove
	public static final int getMeleeDefenceBonus(int bonusId) {
		return 0;
	}

	// TODO remove
	public static final int getMeleeBonusStyle(int weaponId, int attackStyle) {
		return 0;
	}

	public static final int getXpStyle(int weaponId, int attackStyle) {
		if (weaponId != -1 && weaponId != -2) {
			String weaponName = ItemDefinitions.getItemDefinitions(weaponId)
					.getName().toLowerCase();
			if (weaponName.contains("whip")) {
				switch (attackStyle) {
				case 0:
					return Skills.ATTACK;
				case 1:
					return SHARED;
				case 2:
				default:
					return Skills.DEFENCE;
				}
			}
			if (weaponName.contains("halberd")) {
				switch (attackStyle) {
				case 0:
					return SHARED;
				case 1:
					return Skills.STRENGTH;
				case 2:
				default:
					return Skills.DEFENCE;
				}
			}
			if (weaponName.contains("mindspike")
					|| weaponName.contains("staff")
					|| weaponName.contains("granite mace")
					|| weaponName.contains("hammer")
					|| weaponName.contains("tzhaar-ket-em")
					|| weaponName.contains("tzhaar-ket-om")
					|| weaponName.contains("maul")) {
				switch (attackStyle) {
				case 0:
					return Skills.ATTACK;
				case 1:
					return Skills.STRENGTH;
				case 2:
				default:
					return Skills.DEFENCE;
				}
			}
			if (weaponName.contains("godsword") || weaponName.contains("sword")
					|| weaponName.contains("2h")) {
				switch (attackStyle) {
				case 0:
					return Skills.ATTACK;
				case 1:
					return Skills.STRENGTH;
				case 2:
					return Skills.STRENGTH;
				case 3:
				default:
					return Skills.DEFENCE;
				}
			}
		}
		switch (weaponId) {
		case -1:
		case -2:
			switch (attackStyle) {
			case 0:
				return Skills.ATTACK;
			case 1:
				return Skills.STRENGTH;
			case 2:
			default:
				return Skills.DEFENCE;
			}
		default:
			switch (attackStyle) {
			case 0:
				return Skills.ATTACK;
			case 1:
				return Skills.STRENGTH;
			case 2:
				return SHARED;
			case 3:
			default:
				return Skills.DEFENCE;
			}
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		bonuses = new int[18];
		if (getCombatMode() != LEGACY_COMBAT_MODE)
			specialAttackPercentage = 0;
	}

	public int[] getBonuses() {
		return bonuses;
	}

	/*
	 * stats
	 * 
	 * 0 - mainhand damage 1 - offhand accuracy 2 - offhand damage 3 - offhand
	 * accuracy 5 - ability damage 5 - health 6 - prayer bonus 7 - armor 8 -
	 * armor melee weakness multiplier 9 - armor range weakness multiplier 10 -
	 * armor mage weakness multiplier 11 - damage reduction multiplier (PvE) 12
	 * - damage reduction multiplier (PvP) 13 - accuracy penalty
	 */

	public static final int MAINHAND_DAMAGE = 0, MAINHAND_ACCURACY = 1,
			OFFHAND_DAMAGE = 2, OFFHAND_ACCURACY = 3, ABILITY_DAMAGE = 4,
			HEALTH = 5, PRAYER_B = 6, ARMOR = 7, ARMOR_MELEE_WEAKNESS = 8,
			ARMOR_RANGE_WEAKNESS = 9, ARMOR_MAGIC_WEAKNESS = 10,
			MELEE_ACCURACY_PENALTY = 13, RANGE_ACCURACY_PENALTY = 14,
			MAGE_ACCURACY_PENALTY = 15;

	public int getType(int slot) {
		Item item = player.getEquipment().getItem(slot);
		if (item != null) {
			ItemDefinitions defs = item.getDefinitions();
			if (defs.isMeleeTypeWeapon() || defs.isMeleeTypeGear())
				return Combat.MELEE_TYPE;
			if (defs.isRangeTypeWeapon() || defs.isRangeTypeGear())
				return Combat.RANGE_TYPE;
			if (defs.isMagicTypeWeapon() || defs.isMagicTypeGear())
				return Combat.MAGIC_TYPE;
		}
		return Combat.ALL_TYPE;
	}

	public void sendAbilityVars() {
		player.getVarsManager().sendVarBit(22192, 1);// Combat stances for Barb
		// assualt
		player.getVarsManager().sendVarBit(18021, 250);// The 'sun shine'
		// abilities
		player.getVarsManager().sendVarBit(21067, 1);// Sacrifice
		player.getVarsManager().sendVarBit(21068, 1);// Devotion
		player.getVarsManager().sendVarBit(21069, 1);// Transfigure
		player.getVarsManager().sendVarBit(22464, 1);// Tendrils
	}

	public void unlockSheatheButton() {
		player.getPackets().sendIComponentSettings(1477, 36, 1, 1, 4);
	}

	public void unlockMagicAbilities() {
		player.getPackets().sendIComponentSettings(1461, 1, 0, 175, 10320902);
		player.getPackets().sendIComponentSettings(1461, 7, 6, 14, 2);
	}

	public void unlockMeleeAbilities() {
		player.getPackets().sendIComponentSettings(1460, 1, 0, 175, 10320902);
		player.getPackets().sendIComponentSettings(1460, 5, 6, 14, 2);
	}

	public void unlockRangeAbilities() {
		player.getPackets().sendIComponentSettings(1452, 1, 0, 175, 10320902);
		player.getPackets().sendIComponentSettings(1452, 7, 6, 14, 2);
	}

	public void unlockDefenceAbilities() {
		player.getPackets().sendIComponentSettings(1449, 1, 0, 175, 10320902);
		player.getPackets().sendIComponentSettings(1449, 7, 6, 14, 2);
	}

	public void refreshBonuses() {

		stats = new int[16];
		int mainHandType = getType(Equipment.SLOT_WEAPON);
		int offHandType = getType(Equipment.SLOT_SHIELD);
		int meleeGearArmor = 0;
		int rangeGearArmor = 0;
		int mageGearArmor = 0;
		int allGearArmor = 0;
		boolean hasShield = player.getEquipment().hasShield();
		for (int i = 0; i < player.getEquipment().getItems().getSize(); i++) {
			Item item = player.getEquipment().getItem(i);
			if (item == null
					|| (item.getId() >= 22346 && item.getId() <= 22348 && !(player
							.getControlerManager().getControler() instanceof DTControler)))
				continue;
			ItemDefinitions defs = item.getDefinitions();
			int type = getType(i);

			if (i != Equipment.SLOT_SHIELD
					&& !(i == Equipment.SLOT_ARROWS
							&& mainHandType == Combat.RANGE_TYPE && player
							.getEquipment().getItem(Equipment.SLOT_WEAPON)
							.getDefinitions().getCSOpcode(2940) != 0)) {

				int damage = (int) (defs.getDamage(mainHandType) * Utils
						.random(1.55, 2.00));
				if (i == Equipment.SLOT_ARROWS && item.getId() == 29617
						&& !Combat.hasDarkbow(player))
					damage /= 2;
				else if (i == Equipment.SLOT_ARROWS && item.getId() == 28465
						&& !Combat.hasAscensionCrossbow(player, true))
					damage /= 2;
				// 28465

				if (i == Equipment.SLOT_ARROWS) { // cap arrows
					Item weapon = player.getEquipment().getItem(
							Equipment.SLOT_WEAPON);
					int maxDamage = weapon == null ? 0 : (int) (weapon
							.getDefinitions().getRangedLevel() * 96);
					if (damage > maxDamage)
						damage = maxDamage;
				}

				stats[MAINHAND_DAMAGE] += damage;
				stats[MAINHAND_ACCURACY] += defs.getAccuracy(mainHandType);

				if (i == Equipment.SLOT_WEAPON) {

					int abilityDamage = 0;

					Item weapon = player.getEquipment().getItem(
							Equipment.SLOT_WEAPON);

					// if weapon has its own ammo then treat as the same way as
					// melee(weap instead of ammo 4 calc)
					if (mainHandType == Combat.RANGE_TYPE
							&& weapon.getDefinitions().getCSOpcode(2940) == 0) {
						Item ammo = player.getEquipment().getItem(
								Equipment.SLOT_ARROWS);
						if (ammo != null) {
							int ammoDamage = ammo.getDefinitions()
									.getRangedLevel() * 96;
							// caps dmg at weap lvl
							int maxDamage = weapon == null ? 0 : (int) (weapon
									.getDefinitions().getRangedLevel() * 96);
							abilityDamage = ammoDamage > maxDamage ? maxDamage
									: ammoDamage;
							if (player.getEquipment().hasTwoHandedWeapon())
								abilityDamage *= 1.5;
							else if (offHandType == Combat.RANGE_TYPE
									&& !hasShield) {
								Item offhand = player.getEquipment().getItem(
										Equipment.SLOT_SHIELD);
								maxDamage = offhand == null ? 0
										: (int) (offhand.getDefinitions()
												.getRangedLevel() * 96);
								abilityDamage += (ammoDamage > maxDamage ? maxDamage
										: ammoDamage) / 2;
							}
						}
					} else if (mainHandType == Combat.RANGE_TYPE
							|| mainHandType == Combat.MELEE_TYPE) { // melee
						// and
						// range(when
						// weapon
						// doesnt
						// use
						// ammo)
						abilityDamage = weapon == null ? 0
								: (int) ((mainHandType == Combat.RANGE_TYPE ? weapon
										.getDefinitions().getRangedLevel()
										: weapon.getDefinitions()
												.getAttackLevel()) * 96);
						if (player.getEquipment().hasTwoHandedWeapon())
							abilityDamage *= 1.5;
						else if (offHandType == mainHandType && !hasShield) {
							Item offhand = player.getEquipment().getItem(
									Equipment.SLOT_SHIELD);
							abilityDamage += offhand == null ? 0
									: (mainHandType == Combat.RANGE_TYPE ? offhand
											.getDefinitions().getRangedLevel()
											: offhand.getDefinitions()
													.getAttackLevel()) * 96 / 2;
						}
					}
					stats[ABILITY_DAMAGE] += abilityDamage;

				} else if (i != Equipment.SLOT_ARROWS) {
					// the extra bonuses from armor :p
					stats[ABILITY_DAMAGE] += damage;
				}
			}
			if (!hasShield
					&& i != Equipment.SLOT_WEAPON
					&& !(i == Equipment.SLOT_ARROWS
							&& offHandType == Combat.RANGE_TYPE && player
							.getEquipment().getItem(Equipment.SLOT_SHIELD)
							.getDefinitions().getCSOpcode(2940) != 0)) {

				int damage = (int) (defs.getDamage(offHandType) * Utils.random(
						1.55, 2.00));
				if (i == Equipment.SLOT_ARROWS && item.getId() == 29617) // dark
					// arrow
					// dmg
					// reduction
					// if
					// not
					// dark
					// bow
					damage /= 2;
				else if (i == Equipment.SLOT_ARROWS && item.getId() == 28465
						&& !Combat.hasAscensionCrossbow(player, false)) // ascension
					// bolts
					// dmg
					// reduction
					// if
					// not
					// ancension
					// crossbow
					damage /= 2;

				if (i == Equipment.SLOT_ARROWS) { // cap arrows
					Item weapon = player.getEquipment().getItem(
							Equipment.SLOT_SHIELD);
					int maxDamage = weapon == null ? 0 : (int) (weapon
							.getDefinitions().getRangedLevel() * 96);
					if (damage > maxDamage)
						damage = maxDamage;
				}

				stats[OFFHAND_DAMAGE] += damage;
				stats[OFFHAND_ACCURACY] += defs.getAccuracy(offHandType);
			}

			stats[HEALTH] += defs.getHealth();
			stats[PRAYER_B] += defs.getPrayerBonus();
			int armor = defs.getArmor();
			if (armor > 0) {
				stats[ARMOR] += armor;

				if (type != Combat.ALL_TYPE
						&& (i == Equipment.SLOT_CHEST || i == Equipment.SLOT_LEGS)
						&& player.getEquipment().getWeaponId() != -1
						&& type != mainHandType) {
					if (mainHandType == Combat.MELEE_TYPE)
						stats[MELEE_ACCURACY_PENALTY] += armor
								* (mainHandType == Combat.RANGE_TYPE ? 1.5
										: 0.8);
					else if (mainHandType == Combat.RANGE_TYPE)
						stats[RANGE_ACCURACY_PENALTY] += armor
								* (mainHandType == Combat.MAGIC_TYPE ? 1.5
										: 0.8);
					else if (mainHandType == Combat.MAGIC_TYPE)
						stats[MAGE_ACCURACY_PENALTY] += armor
								* (mainHandType == Combat.MELEE_TYPE ? 1.5
										: 0.8);
				}
				if (type == Combat.ALL_TYPE) {
					allGearArmor += armor;
				} else if (type == Combat.MELEE_TYPE) {
					meleeGearArmor += armor;
				} else if (type == Combat.RANGE_TYPE) {
					rangeGearArmor += armor;
				} else if (type == Combat.MAGIC_TYPE) {
					mageGearArmor += armor;
				}
			}
		}
		stats[ARMOR_MELEE_WEAKNESS] = (int) (Combat.getWeaknessMultiplier(
				stats[ARMOR], rangeGearArmor, meleeGearArmor + allGearArmor,
				mageGearArmor) * 100);
		stats[ARMOR_RANGE_WEAKNESS] = (int) (Combat.getWeaknessMultiplier(
				stats[ARMOR], mageGearArmor, rangeGearArmor + allGearArmor,
				meleeGearArmor) * 100);
		stats[ARMOR_MAGIC_WEAKNESS] = (int) (Combat.getWeaknessMultiplier(
				stats[ARMOR], meleeGearArmor, mageGearArmor + allGearArmor,
				rangeGearArmor) * 100);

	}

	public void resetSpecialAttack() {
		desecreaseSpecialAttack(0);
		specialAttackPercentage = (byte) (player.getCombatDefinitions()
				.getCombatMode() == CombatDefinitions.LEGACY_COMBAT_MODE ? 100
				: 0);
		refreshSpecialAttackPercentage();
	}

	public void restoreSpecialAttack() {
		if (player.getFamiliar() != null)
			player.getFamiliar().restoreSpecialAttack(15);
		if (specialAttackPercentage == 100)
			return;
		restoreSpecialAttack(10);
		if (specialAttackPercentage == 100 || specialAttackPercentage == 50)
			player.getPackets().sendGameMessage(
					"<col=00FF00>Your special attack energy is now "
							+ specialAttackPercentage + "%.");
	}

	public void restoreSpecialAttack(int percentage) {
		if (specialAttackPercentage >= 100
				|| player.getInterfaceManager().containsScreenInterface())
			return;
		specialAttackPercentage += specialAttackPercentage > (100 - percentage) ? 100 - specialAttackPercentage
				: percentage;
		if (player.getCombatDefinitions().getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
			player.addAdrenalineBar();
		refreshSpecialAttackPercentage();

	}

	public void init() {
		refreshUsingSpecialAttack();
		refreshSpecialAttackPercentage();
		refreshAutoRelatie();
		refreshSpellBook();
		refreshAutoCastSpell();
		refreshManualCast();
		refreshManualSpellCasting();
		setDefaultAbilityMenu();
		refreshMeleeCombatExperience();
		refreshRangedCombatExperience();
		refreshMagicCombatExperience();
		refreshCombatMode();
		refreshAutoAbilityTrigger();
		refreshShowCombatModeIcon();
		refreshAllowAbilityQueueing();
	}

	/*
	 * enables cast option manualy when not under combat or if manual cast
	 * setted true (btw manual cast cant be switched in 812, rs added it in 815
	 * anyway ima add option serversided)
	 */
	public void refreshManualCast() {
		player.getVarsManager().sendVar(616,
				!manualCast && currentTarget != null ? 0 : -1);
		player.getVarsManager().sendVar(623,
				!manualCast && currentTarget != null ? 0 : -1);
	}

	public void refreshManualSpellCasting() {
		player.getVarsManager().sendVarBit(22843, manualCast ? 1 : 0);
	}

	public void switchManualSpellCasting() {
		manualCast = !manualCast;
		refreshManualSpellCasting();
	}

	public void setCombatExperienceStyle(int style) {
		int type = player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON);
		if (type == Combat.ALL_TYPE || type == Combat.MELEE_TYPE)
			setMeleeCombatExperience(style == 0 ? 1 : style == 1 ? 0 : style);
		else if (type == Combat.RANGE_TYPE)
			setRangedCombatExperience(style == 0 ? 1 : style == 1 ? 0 : 2);
		else if (type == Combat.MAGIC_TYPE)
			setMagicCombatExperience(style == 0 ? 1 : style == 1 ? 0 : 2);
	}

	public void setMeleeCombatExperience(int style) {
		this.meleeCombatExperience = (byte) style;
		refreshMeleeCombatExperience();
	}

	public void setRangedCombatExperience(int style) {
		this.rangedCombatExperience = (byte) style;
		refreshRangedCombatExperience();
	}

	public void setMagicCombatExperience(int style) {
		this.magicCombatExperience = (byte) style;
		refreshMagicCombatExperience();
	}

	public void refreshMeleeCombatExperience() {
		player.getVarsManager().sendVarBit(1906, meleeCombatExperience);
	}

	public int getMeleeCombatExperience() {
		return meleeCombatExperience;
	}

	public void refreshRangedCombatExperience() {
		player.getVarsManager().sendVarBit(1907, rangedCombatExperience);
	}

	public int getRangedCombatExperience() {
		return rangedCombatExperience;
	}

	public void refreshMagicCombatExperience() {
		player.getVarsManager().sendVarBit(1908, magicCombatExperience);
	}

	public int getMagicCombatExperience() {
		return magicCombatExperience;
	}

	public void refreshCombatMode() {
		// forcing atm due to inter being auto and legacy is setted in eoc.
		// later on remove force
		player.getVarsManager().sendVarBit(21685, combatMode);
	}

	public void switchShowCombatModeIcon() {
		showCombatModeIcon = !showCombatModeIcon;
		refreshShowCombatModeIcon();
	}

	public void refreshShowCombatModeIcon() {
		player.getVarsManager().sendVarBit(21686,
				!player.isLegacyMode() && showCombatModeIcon ? 1 : 0);
	}

	public void switchAllowAbilityQueueing() {
		allowAbilityQueueing = !allowAbilityQueueing;
		refreshAllowAbilityQueueing();
	}

	public void refreshAllowAbilityQueueing() {
		player.getVarsManager().sendVarBit(21684,
				!player.isLegacyMode() && allowAbilityQueueing ? 0 : 1);
	}

	public void setCombatMode(int mode) {
		/*
		 * if (mode != LEGACY_COMBAT_MODE) {
		 * player.getPackets().sendGameMessage(
		 * "This mode is currently disabled."); mode = LEGACY_COMBAT_MODE; }
		 */
		if (player.isLegacyMode() && mode != LEGACY_COMBAT_MODE) // someone
			// trying to
			// hack to use
			// another mode
			// while in
			// legacy
			return;
		int oldMode = combatMode;
		combatMode = (byte) mode;
		refreshCombatMode();
		if (mode == oldMode)
			return;
		if (oldMode == LEGACY_COMBAT_MODE || mode == LEGACY_COMBAT_MODE) {
			sheathe = false; // turns it off by default, ofc u can turn on bk
			specialAttackPercentage = (byte) (mode == LEGACY_COMBAT_MODE ? 100
					: 0);
			refreshSpecialAttackPercentage();
			player.getInterfaceManager().sendMeleeAbilities();
			player.getAppearence().generateAppearenceData(); // changes stance
			// aswell
		}
		if (mode == REVOLUTION_COMBAT_MODE) {
			player.getPackets().sendGameMessage("Revolution is now active.",
					true);
			refreshAutoAbilityTrigger();
		} else if (oldMode == REVOLUTION_COMBAT_MODE) {
			player.getPackets().sendEntityMessage(1, 0xFFFFFF, player,
					"Revolution is no longer active.");
			refreshAutoAbilityTrigger();
		}
		if (mode == MOMENTUM_COMBAT_MODE)
			player.getPackets()
					.sendGameMessage("Momentum is now active.", true);
		else if (oldMode == MOMENTUM_COMBAT_MODE) {
			player.getPackets().sendEntityMessage(1, 0xFFFFFF, player,
					"Momentum is no longer active.");
		}
	}

	public int getCombatMode() {
		return player.isLegacyMode() ? LEGACY_COMBAT_MODE : combatMode; // legacy
	}

	public void sendUnlockAttackStylesButtons() {
		for (int componentId = 7; componentId <= 10; componentId++)
			player.getPackets().sendUnlockIComponentOptionSlots(884,
					componentId, -1, 0, 0);
	}

	public void switchUsingSpecialAttack() {
		usingSpecialAttack = !usingSpecialAttack;
		refreshUsingSpecialAttack();
	}

	public void setUsingSpecialAttack() {
		usingSpecialAttack = true;
		refreshUsingSpecialAttack();
	}

	public void desecreaseSpecialAttack(int amount) {
		usingSpecialAttack = false;
		refreshUsingSpecialAttack();
		if (amount > 0) {
			specialAttackPercentage -= amount;
			refreshSpecialAttackPercentage();
			if (player.getCombatDefinitions().getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
				player.addAdrenalineBar();
		}
	}

	public void increaseSpecialAttack(int amount) {
		if (amount != 0) {
			int spec = specialAttackPercentage + amount;
			specialAttackPercentage = (byte) (spec < 0 ? 0 : spec > 100 ? 100
					: spec);
			refreshSpecialAttackPercentage();
			if (player.getCombatDefinitions().getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
				player.addAdrenalineBar();
		}
	}

	public boolean hasRingOfVigour() {
		return player.getEquipment().getRingId() == 19669;
	}

	public int getSpecialAttackPercentage() {
		return specialAttackPercentage;
	}

	public void refreshUsingSpecialAttack() {
		player.getVarsManager().sendVar(680, usingSpecialAttack ? 1 : 0);
	}

	public void refreshSpecialAttackPercentage() {
		player.getVarsManager().sendVar(679, specialAttackPercentage * 10);
	}

	public void switchAutoRelatie() {
		autoRelatie = !autoRelatie;
		refreshAutoRelatie();
	}

	/*
	 * needs to be force send sadly
	 */
	public void refreshAutoRelatie() {
		player.getVarsManager().forceSendVar(462, autoRelatie ? 0 : 1);
	}

	public boolean isUsingSpecialAttack() {
		return usingSpecialAttack;
	}

	// TODO remove this
	public int getAttackStyle() {
		return 0;
	}
	
	public boolean isAutoRelatie() {
		return autoRelatie;
	}

	public void setAutoRelatie(boolean autoRelatie) {
		this.autoRelatie = autoRelatie;
	}

	public boolean isDungeonneringSpellBook() {
		return dungeonneringSpellBook;
	}

	public void removeDungeonneringBook() {
		if (dungeonneringSpellBook) {
			dungeonneringSpellBook = false;
			player.getInterfaceManager().sendMagicBook();
		}
	}

	public boolean isInstantAttack() {
		return instantAttack;
	}

	public void setInstantAttack(boolean instantAttack) {
		this.instantAttack = instantAttack;
	}

	public boolean isCombatStance() {
		return combatStance;
	}

	public void setCombatStance(boolean combatStance) {
		this.combatStance = combatStance;
	}

	private boolean isForceNoSheathe() {
		return player.isCanPvp()
				|| player.getControlerManager().getControler() instanceof ClanWarRequestController;
	}

	public void processCombatStance() {
		boolean forceSheathe = isForceNoSheathe();
		boolean underCombat = player.isUnderCombat();
		if (forceNoSheathe != forceSheathe) {
			forceNoSheathe = forceSheathe;
			if (underCombat == combatStance)
				player.getAppearence().generateAppearenceData();
		}
		if (underCombat != combatStance) {
			// wait until def emote performs, cuz render anims cant be delayed
			if (underCombat && player.getNextAnimation() == null)
				return;
			combatStance = underCombat;
			if (!combatStance) {
				if (isSheathe())
					player.setNextAnimationNoPriority(new Animation(18027));
				else if (getCombatMode() != LEGACY_COMBAT_MODE)
					player.setNextAnimationNoPriority(new Animation(player
							.getEquipment().getWeaponEndCombatEmote()));
			}
			player.getVarsManager().sendVarBit(1899, underCombat ? 1 : 0); // makes
			// menus
			// not
			// open
			// and
			// say
			// under
			// combat
			// when
			// u
			// try
			// to
			// open
			if (player.getInterfaceManager().isMenuOpen())
				player.getInterfaceManager().closeMenu();
			// player.getInterfaceManager().refreshInterface();
			player.getDialogueManager().finishConfirmDialogue();
			// TODO get animation from itemdefs and perform it here. taking out
			// or in weapon.
			player.getAppearence().generateAppearenceData();
			if (!underCombat && currentTarget != null)
				setCurrentTarget(null);
		}
		if (underCombat) {
			Entity target = player.getActionManager().getAction() instanceof PlayerCombatNew ? ((PlayerCombatNew) player
					.getActionManager().getAction()).getTarget() : null;
			if (target != null && currentTarget != target) {
				setCurrentTarget(target);
			} else if (currentTarget != null
					&& (currentTarget.hasFinished() || !player.withinDistance(
							currentTarget, 16)))
				setCurrentTarget(null);
		} else if (getCombatMode() != LEGACY_COMBAT_MODE
				&& specialAttackPercentage > 0) {
			boolean hasRegen = player.getEffectsManager().hasActiveEffect(
					EffectType.REGENERATE);
			increaseSpecialAttack(hasRegen ? -10 : -5);
			if (hasRegen)
				player.heal((int) (player.getMaxHitpoints() * 0.02), 0, 0, true);

		}
	}

	private void refreshCurrentTargetData() {
		currentTargetData = getCurrentTargetData();
		player.getPackets().sendExecuteScript(82, currentTarget.getName(),
				currentTarget.getCombatLevel(), getCurrentTargetWeakness(), 30,
				30, 0);
		updateTargetBuffs(true);
	}

	private void setCurrentTarget(Entity target) {
		currentTarget = target;
		refreshManualCast(); // makes cast spell appear or not
		player.getPackets().sendCurrentTarget(target);
		if (target != null) {
			player.getPackets()
					.sendEntityInterface(target, true, 1488, 2, 1490);
			refreshCurrentTargetData();
		} else {
			player.getPackets().closeInterface(
					InterfaceManager.getComponentUId(1488, 2));
		}

	}

	public Object[] getCurrentTargetData() {
		Entity currentTarget = this.currentTarget; // to prevent syc issues
		if (currentTarget == null)
			return null;
		return new Object[] { currentTarget.getIndex(),
				currentTarget.getName(), currentTarget.getCombatLevel(),
				getCurrentTargetWeakness() };
	}

	private int getCurrentTargetWeakness() {
		Entity currentTarget = this.currentTarget; // to prevent syc issues
		if (currentTarget == null)
			return 9286; // none
		int weakness = 0;
		if (currentTarget instanceof NPC) {
			weakness = ClientScriptMap.getMap(6745).getIntValue(
					((NPC) currentTarget).getWeaknessStyle());
			if (weakness == 197) // mobs have no melee type(only styles), thats
				// none
				weakness = 9286;
		} else {
			int weaknessType = ((Player) currentTarget).getCombatDefinitions()
					.getWeaknessType();
			weakness = weaknessType == Combat.MELEE_TYPE ? 197
					: weaknessType == Combat.RANGE_TYPE ? 200
							: weaknessType == Combat.MAGIC_TYPE ? 202 : 9286;
		}
		return weakness;
	}

	public void refreshTargetBuffs() {
		Entity currentTarget = this.currentTarget; // to prevent syc issues
		if (currentTarget == null)
			return;
		Object[] data = getCurrentTargetData();
		if (data == null || currentTargetData == null
				|| currentTargetData[0] == null)
			return;

		if (!currentTargetData[0].equals(data[0])) // index changed such as
			// disapearing for
			// awhile(dig)
			setCurrentTarget(currentTarget); // forces to even change index(move
		// inter)
		else if (!currentTargetData[1].equals(data[1])
				|| !currentTargetData[2].equals(data[2])
				|| !currentTargetData[3].equals(data[3]))
			refreshCurrentTargetData(); // forces to refresh name/lvl/weakness +
		// forcebuff to appear
		else
			// refresh buffs normaly if any changed
			updateTargetBuffs(false);
	}

	private void updateTargetBuffs(boolean update) {
		if (!player.hasBuffTimersEnabled())
			return;

		Entity currentTarget = this.currentTarget;
		if (currentTarget == null)// to prevent syc issues
			return;

		Player targetPlayer = currentTarget instanceof Player ? (Player) currentTarget
				: null;

		if (targetPlayer != null) {
			int maxHp = currentTarget.getMaxHitpoints();
			if (maxHp != currentTargetMaxHP)
				player.getPackets().sendCSVarInteger(3700,
						currentTargetMaxHP = currentTarget.getMaxHitpoints());
			int hp = currentTarget.getHitpoints();
			if (hp != currentTargetHP)
				player.getPackets()
						.sendCSVarInteger(3701, currentTargetHP = hp);
		}
		// prayer normal
		update |= player.getVarsManager().sendVarBit(
				1953,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 0)
						&& player.getSkills().getLevel(Skills.PRAYER) < 10 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1954,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 0)
						&& player.getSkills().getLevel(Skills.PRAYER) < 28 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1955,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 0)
						&& player.getSkills().getLevel(Skills.PRAYER) >= 28 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1956,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 1)
						&& player.getSkills().getLevel(Skills.PRAYER) < 13 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1957,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 1)
						&& player.getSkills().getLevel(Skills.PRAYER) < 31 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1958,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 1)
						&& player.getSkills().getLevel(Skills.PRAYER) >= 31 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1959,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 2)
						&& player.getSkills().getLevel(Skills.PRAYER) < 16 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1960,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 2)
						&& player.getSkills().getLevel(Skills.PRAYER) < 34 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1961,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 2)
						&& player.getSkills().getLevel(Skills.PRAYER) >= 34 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1962,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 7) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1963,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 8) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1964,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 9) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1965,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 11) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1966,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 12) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1967,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 13) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1968,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 14) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1969,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 15) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1970,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 16) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1971,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 3)
						&& player.getSkills().getLevel(Skills.PRAYER) < 26 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1972,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 3)
						&& player.getSkills().getLevel(Skills.PRAYER) < 44 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1973,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 3)
						&& player.getSkills().getLevel(Skills.PRAYER) >= 44 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1974,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 5)
						&& player.getSkills().getLevel(Skills.PRAYER) < 27 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1975,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 5)
						&& player.getSkills().getLevel(Skills.PRAYER) < 45 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1976,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 5)
						&& player.getSkills().getLevel(Skills.PRAYER) >= 45 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1977,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 4)
						&& player.getSkills().getLevel(Skills.PRAYER) < 26 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1978,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 4)
						&& player.getSkills().getLevel(Skills.PRAYER) < 44 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1979,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 4)
						&& player.getSkills().getLevel(Skills.PRAYER) >= 44 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1980,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 6)
						&& player.getSkills().getLevel(Skills.PRAYER) < 27 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1981,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 6)
						&& player.getSkills().getLevel(Skills.PRAYER) < 45 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1982,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 6)
						&& player.getSkills().getLevel(Skills.PRAYER) >= 45 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				1983,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 10) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1984,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 17) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1985,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 19) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1986,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 18) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1987,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 21) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1988,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(0, 20) ? 1 : 0);
		// prayer curses
		update |= player.getVarsManager().sendVarBit(
				1989,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 8) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1990,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 3) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1991,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 5) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1992,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 7) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1993,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 20) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1994,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 16) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1995,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 18) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1996,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 26) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1997,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 27) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1998,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 0) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1999,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 1) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2000,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 2) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2001,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 4) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2002,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 6) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2003,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 9) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2004,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 10) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2005,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 11) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2006,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 12) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2007,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 13) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2008,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 14) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2009,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 15) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2010,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 17) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2011,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 19) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2013,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 21) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2014,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 22) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2015,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 23) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2016,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 24) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2017,
				targetPlayer != null
						&& targetPlayer.getPrayer().usingPrayer(1, 25) ? 1 : 0);

		// stast boost / reduce
		update |= player.getVarsManager()
				.sendVarBit(
						2018,
						targetPlayer != null
								&& !targetPlayer.getEffectsManager()
										.hasActiveEffect(EffectType.OVERLOAD)
								&& targetPlayer.getSkills().getLevel(
										Skills.ATTACK) > targetPlayer
										.getSkills().getLevelForXp(
												Skills.ATTACK) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2019,
						targetPlayer != null
								&& targetPlayer.getSkills().getLevel(
										Skills.ATTACK) < targetPlayer
										.getSkills().getLevelForXp(
												Skills.ATTACK) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2020,
						targetPlayer != null
								&& !targetPlayer.getEffectsManager()
										.hasActiveEffect(EffectType.OVERLOAD)
								&& targetPlayer.getSkills().getLevel(
										Skills.STRENGTH) > targetPlayer
										.getSkills().getLevelForXp(
												Skills.STRENGTH) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2021,
						targetPlayer != null
								&& targetPlayer.getSkills().getLevel(
										Skills.STRENGTH) < targetPlayer
										.getSkills().getLevelForXp(
												Skills.STRENGTH) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2022,
						targetPlayer != null
								&& !targetPlayer.getEffectsManager()
										.hasActiveEffect(EffectType.OVERLOAD)
								&& targetPlayer.getSkills().getLevel(
										Skills.DEFENCE) > targetPlayer
										.getSkills().getLevelForXp(
												Skills.DEFENCE) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2023,
						targetPlayer != null
								&& targetPlayer.getSkills().getLevel(
										Skills.DEFENCE) < targetPlayer
										.getSkills().getLevelForXp(
												Skills.DEFENCE) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2024,
						targetPlayer != null
								&& !targetPlayer.getEffectsManager()
										.hasActiveEffect(EffectType.OVERLOAD)
								&& targetPlayer.getSkills().getLevel(
										Skills.RANGE) > targetPlayer
										.getSkills()
										.getLevelForXp(Skills.RANGE) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2025,
						targetPlayer != null
								&& targetPlayer.getSkills().getLevel(
										Skills.RANGE) < targetPlayer
										.getSkills()
										.getLevelForXp(Skills.RANGE) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2026,
						targetPlayer != null
								&& !targetPlayer.getEffectsManager()
										.hasActiveEffect(EffectType.OVERLOAD)
								&& targetPlayer.getSkills().getLevel(
										Skills.MAGIC) > targetPlayer
										.getSkills()
										.getLevelForXp(Skills.MAGIC) ? 1 : 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2027,
						targetPlayer != null
								&& targetPlayer.getSkills().getLevel(
										Skills.MAGIC) < targetPlayer
										.getSkills()
										.getLevelForXp(Skills.MAGIC) ? 1 : 0);

		// prayer points boost/reduce
		update |= player
				.getVarsManager()
				.sendVarBit(
						2030,
						targetPlayer != null
								&& targetPlayer.getPrayer().getPrayerpoints() > targetPlayer
										.getPrayer().getMaxPrayerpoints() ? 1
								: 0);
		// 2031. prayer points reduced. not used

		// poison delays
		update |= player.getVarsManager().sendVarBit(
				2033,
				targetPlayer != null
						&& targetPlayer.getEffectsManager().hasActiveEffect(
								EffectType.ANTIPOISON) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2037,
				targetPlayer != null
						&& targetPlayer.getEffectsManager().hasActiveEffect(
								EffectType.PRAYER_RENEWAL) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				20383,
				targetPlayer != null
						&& targetPlayer.getEffectsManager().hasActiveEffect(
								EffectType.OVERLOAD) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2034,
				targetPlayer != null
						&& targetPlayer.getEffectsManager().hasActiveEffect(
								EffectType.FIRE_IMMUNITY) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2035,
				targetPlayer != null
						&& targetPlayer.getEffectsManager().hasActiveEffect(
								EffectType.SUPER_FIRE_IMMUNITY) ? 1 : 0);

		// ratings boost / reduce
		update |= player.getVarsManager()
				.sendVarBit(
						2038,
						targetPlayer != null
								&& targetPlayer.getMeleeAttackRating() < 0 ? 1
								: 0);
		update |= player.getVarsManager()
				.sendVarBit(
						2039,
						targetPlayer != null
								&& targetPlayer.getMeleeAttackRating() > 0 ? 1
								: 0);
		update |= player.getVarsManager().sendVarBit(
				2040,
				targetPlayer != null
						&& targetPlayer.getMeleeStrengthRating() < 0 ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2041,
				targetPlayer != null
						&& targetPlayer.getMeleeStrengthRating() > 0 ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2050,
				targetPlayer != null && targetPlayer.getDefenceRating() < 0 ? 1
						: 0);
		update |= player.getVarsManager().sendVarBit(
				2051,
				targetPlayer != null && targetPlayer.getDefenceRating() > 0 ? 1
						: 0);

		// hitpoints boost
		update |= player.getVarsManager().sendVarBit(
				2028,
				currentTarget.getHitpoints() > currentTarget.getMaxHitpoints()
						|| (targetPlayer != null && targetPlayer
								.getVarsManager().getBitValue(16463) > 0) ? 1
						: 0);
		// hitpoints reduced 2029 not used

		// other
		update |= player.getVarsManager().sendVarBit(
				2032,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.POISON) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(1910,
				currentTarget.isStunned() ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(1911,
				currentTarget.isBound() ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(2055,
				currentTarget.isStunImmune() ? 1 : 0);

		// abilities
		update |= player.getVarsManager().sendVarBit(
				1912,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.SEVER) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1913,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.SLAUGHTER) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1914,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.MASSACRE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1915,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.REGENERATE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1916,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.ANTICIPATION) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1917,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.FREEDOM) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1918,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.PROVOKE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1919,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.RESONANCE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1920,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.PREPARATION) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1921,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.REFLECT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1922,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.DEBILITATE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1923,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.REVENGE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1924,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.BARRICADE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1925,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.REJUVINATE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1926,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.IMMORTALITY) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1927,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.DISMEMBER) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1928,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.FURY) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1929,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.DESTROY) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1930,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.BERSERK) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1931,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.FRENZY) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1932,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.PULVERISE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1933,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.COMBUST) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1934,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.DETONATE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1935,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.METAMORPHISIS) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1936,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.CONFUSE_EFFECT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1937,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.WEAKEN_EFFECT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1938,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.CURSE_EFFECT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1939,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.VULNERABILITY_EFFECT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1940,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.ENFEEBLE_EFFECT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1941,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.STAGGER_EFFECT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1942,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.BINDING_SHOT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1943,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.FRAGMENTATION) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1944,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.INCENDIARY_SHOT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1945,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.UNLOAD) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1946,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.DEADSHOT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1947,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.ASSAULT) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1948,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.FLURRY) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1949,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.SNIPE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1950,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.RAPID_FIRE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1951,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.TELEPORT_BLOCK) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				1952,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.INCITE) ? 1 : 0);
		update |= player.getVarsManager().sendVarBit(
				2036,
				currentTarget.getEffectsManager().hasActiveEffect(
						EffectType.ASPHYXIATE) ? 1 : 0);
		if (update)
			player.updateBuffs();

	}

	/*
	 * //selects ur current target if u attacking, else selects last entity to
	 * attack you, and only when youre under combat
	 */
	public Entity getCurrentTarget() {
		return currentTarget;
	}

	/*
	 * does same as bellow but its faster, just to check for entity update
	 */
	public boolean isNeedTargetReticuleUpdate(Entity entity) {
		return player.isTargetReticule() && !entity.isDead()
				&& (isAttackingPlayer(entity) || entity == currentTarget);
	}

	public Graphics getTargetReticule(Entity entity) {
		if (!isNeedTargetReticuleUpdate(entity))
			return null;
		int size = entity.getSize() - 1;
		if (size > 3)
			size = 3;
		return new Graphics(
				((entity == currentTarget ? (isAttackingPlayer(entity) ? 4188
						: 4172) : 4180) + size * 2) - 1, 0, 0, 0, true);
	}

	private boolean isAttackingPlayer(Entity entity) {
		return (entity instanceof Player && ((Player) entity)
				.getCombatDefinitions().getCurrentTarget() == player)
				|| (entity instanceof NPC && ((NPC) entity).getCombat()
						.getTarget() == player);
	}

	public int getWeaknessType() {
		if (stats[ARMOR_MELEE_WEAKNESS] > stats[ARMOR_RANGE_WEAKNESS]
				&& stats[ARMOR_MELEE_WEAKNESS] > stats[ARMOR_MAGIC_WEAKNESS])
			return Combat.MELEE_TYPE;
		if (stats[ARMOR_RANGE_WEAKNESS] > stats[ARMOR_MELEE_WEAKNESS]
				&& stats[ARMOR_RANGE_WEAKNESS] > stats[ARMOR_MAGIC_WEAKNESS])
			return Combat.RANGE_TYPE;
		if (stats[ARMOR_MAGIC_WEAKNESS] > stats[ARMOR_RANGE_WEAKNESS]
				&& stats[ARMOR_MAGIC_WEAKNESS] > stats[ARMOR_MELEE_WEAKNESS])
			return Combat.MAGIC_TYPE;

		return Combat.ALL_TYPE;
	}

	public int[] getStats() {
		return stats;
	}

	public int getAbilitiesDamage() {
		int mainHandType = getType(Equipment.SLOT_WEAPON);

		if (mainHandType == Combat.MAGIC_TYPE && this.mainHandSpell != 0) {
			/*
			 * Item ammo = player.getEquipment().getItem(Equipment.SLOT_ARROWS);
			 * if (ammo != null) { int ammoDamage =
			 * ammo.getDefinitions().getRangedLevel() * 96; //caps dmg at weap
			 * lvl int maxDamage = weapon == null ? 0 : (int)
			 * (weapon.getDefinitions().getRangedLevel() * 96); abilityDamage =
			 * ammoDamage > maxDamage ? maxDamage : ammoDamage; if
			 * (player.getEquipment().hasTwoHandedWeapon()) abilityDamage *=
			 * 1.5; else if (offHandType == Combat.RANGE_TYPE) { Item offhand =
			 * player.getEquipment().getItem(Equipment.SLOT_SHIELD); maxDamage =
			 * offhand == null ? 0 : (int)
			 * (offhand.getDefinitions().getRangedLevel() * 96); abilityDamage
			 * += (ammoDamage > maxDamage ? maxDamage : ammoDamage) / 2; } }
			 */
			int spell = getSpellId();
			if (spell > 0) {
				if (spell >= 256) // manual cast
					spell -= 256;
				Item weapon = player.getEquipment().getItem(
						Equipment.SLOT_WEAPON);
				if (weapon != null) {
					Integer weaponLvl = (Integer) weapon.getDefinitions().clientScriptData
							.get(750);
					if (weaponLvl == null)
						weaponLvl = 1;
					GeneralRequirementMap spellData = Magic.getSpellData(spell);

					int magicLevel = player.getSkills().getLevelForXp(
							Skills.MAGIC);

					int levelCap = spellData.getIntValue(2879);
					if (magicLevel < levelCap)
						levelCap = magicLevel;

					int spellDamage = (levelCap > weaponLvl ? weaponLvl
							: levelCap) * 96;
					int spellMaxDamage = Magic.getSpellDamage(spellData);

					if (spellDamage > spellMaxDamage)
						spellDamage = spellMaxDamage;

					int damage = (spellDamage);
					if (player.getEquipment().hasTwoHandedWeapon()) {
						damage = (int) (damage * 1.5);
					} else {
						Item offhand = player.getEquipment().getItem(
								Equipment.SLOT_SHIELD);
						if (offhand != null
								&& getType(Equipment.SLOT_SHIELD) == Combat.MAGIC_TYPE
								&& !player.getEquipment().hasShield()) {
							weaponLvl = (Integer) offhand.getDefinitions().clientScriptData
									.get(750);
							if (weaponLvl == null)
								weaponLvl = 1;
							spellDamage = (levelCap > weaponLvl ? weaponLvl
									: levelCap) * 96;
							if (spellDamage > spellMaxDamage)
								spellDamage = spellMaxDamage;
							damage += spellDamage / 2;
							damage += (int) (Combat.getAccuracy(magicLevel) / 3.7);
						}
					}

					// skill bonus
					damage += (int) (Combat.getAccuracy(magicLevel) / (player
							.getEquipment().hasTwoHandedWeapon() ? 1.65 : 3.7));

					// the extra stats from the armor
					return damage + stats[ABILITY_DAMAGE];

					// return 9.6*spellLevel + (weaponLvl * 7.8);
				}
			}
			return (int) (Combat.getAccuracy(player.getSkills().getLevel(
					Skills.STRENGTH)) / 2.7);
		}

		return (int) (stats[ABILITY_DAMAGE] + (Combat.getAccuracy(player
				.getSkills().getLevel(
						mainHandType == Combat.RANGE_TYPE ? Skills.RANGE
								: Skills.STRENGTH)) / 2.7));
	}

	public int getHandDamage(boolean offhand) {
		int mainHandType = getType(offhand ? Equipment.SLOT_SHIELD
				: Equipment.SLOT_WEAPON);
		if (mainHandType == Combat.MAGIC_TYPE && this.mainHandSpell != 0) {
			int spell = getSpellId();
			if (spell > 0) {
				if (spell >= 256) // manual cast
					spell -= 256;

				Item weapon = player.getEquipment()
						.getItem(
								offhand ? Equipment.SLOT_SHIELD
										: Equipment.SLOT_WEAPON);
				if (weapon != null) {
					Integer weaponLvl = (Integer) weapon.getDefinitions().clientScriptData
							.get(750);
					if (weaponLvl == null)
						weaponLvl = 1;
					GeneralRequirementMap spellData = Magic.getSpellData(spell);

					int magicLevel = player.getSkills().getLevelForXp(
							Skills.MAGIC);

					int levelCap = spellData.getIntValue(2879);
					if (magicLevel < levelCap)
						levelCap = magicLevel;

					int spellDamage = (levelCap > weaponLvl ? weaponLvl
							: levelCap) * 96;
					int spellMaxDamage = Magic.getSpellDamage(spellData);

					if (spellDamage > spellMaxDamage)
						spellDamage = spellMaxDamage;

					int damage = (spellDamage);
					if (player.getEquipment().hasTwoHandedWeapon())
						damage = (int) (damage * 1.5) + (weaponLvl * 78);

					// skill bonus
					damage += (int) (Combat.getAccuracy(magicLevel) / (player
							.getEquipment().hasTwoHandedWeapon() ? 1.65 : 3.7));

					// the extra stats from the armor
					return damage
							+ getStats()[offhand ? OFFHAND_DAMAGE
									: MAINHAND_DAMAGE];

					// return 9.6*spellLevel + (weaponLvl * 7.8);
				}

				/*
				 * return (int)
				 * (Combat.getAccuracy(player.getSkills().getLevel(Skills.MAGIC)
				 * )/2.7 +
				 * Magic.getSpellDamage(Magic.getSpellData(spell))*(player.
				 * getEquipment().hasTwoHandedWeapon() ? 1 : 0.66));
				 */
			}
			return (int) (Combat.getAccuracy(player.getSkills().getLevel(
					Skills.STRENGTH)) / 2.7);
		} else if (mainHandType == Combat.RANGE_TYPE) {
			return (int) (getStats()[offhand ? OFFHAND_DAMAGE : MAINHAND_DAMAGE] + (Combat
					.getAccuracy(player.getSkills().getLevel(Skills.RANGE)) / 2.7));
		}
		// melee
		return (int) (getStats()[offhand ? OFFHAND_DAMAGE : MAINHAND_DAMAGE] + (Combat
				.getAccuracy(player.getSkills().getLevel(Skills.STRENGTH)) / 2.7));
	}

	public int getSkillAccuracy(boolean offhand) {
		int handType = getType(offhand ? Equipment.SLOT_SHIELD
				: Equipment.SLOT_WEAPON);
		return (int) (Combat.getAccuracy(player.getSkills().getLevel(
				handType == Combat.MAGIC_TYPE ? Skills.MAGIC
						: handType == Combat.RANGE_TYPE ? Skills.RANGE
								: Skills.ATTACK)) * 1);
	}

	public int getDefenceArmor() {
		return (int) Combat.getAccuracy(player.getSkills().getLevel(
				Skills.DEFENCE));
	}

	public int getStyle(boolean offhand) {
		Item weapon = player.getEquipment().getItem(
				offhand ? Equipment.SLOT_SHIELD : Equipment.SLOT_WEAPON);
		if (weapon != null) {
			if (weapon.getDefinitions().isMagicTypeWeapon()) {
				int spell = getSpellId();
				if (spell > 0) {
					if (spell >= 256) // manual cast
						spell -= 256;
					int spellType = Magic.getSpellType(Magic
							.getSpellData(spell));
					return spellType;

				}
			}
			return weapon.getDefinitions().getCombatStyle();
		}
		return Combat.NONE_STYLE;
	}

	public long getMainHandDelay() {
		return mainHandDelay;
	}

	public void setMainHandDelay(long mainHandDelay) {
		this.mainHandDelay = mainHandDelay;
	}

	public long getOffHandDelay() {
		return offHandDelay;
	}

	public void setOffHandDelay(long offHandDelay) {
		this.offHandDelay = offHandDelay;
	}

	public boolean onStrengthMenu() {
		return player.getVarsManager().getBitValue(18787) == 1;
	}

	public boolean onDefenceMenu() {
		return player.getVarsManager().getBitValue(18793) == 0;
	}

	public void setStrengthMenu(int menu) {
		player.getVarsManager().setVarBit(18787, menu);
	}

	public void setDefenceMenu(int menu) {
		player.getVarsManager().setVarBit(18793, menu);
	}
}
