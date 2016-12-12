package net.kagani.game.player.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kagani.Settings;
import net.kagani.cache.loaders.AnimationDefinitions;
import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.Projectile;
import net.kagani.game.Region;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.dungeonnering.ShadowForgerIhlakhizan;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.npc.fightkiln.HarAken;
import net.kagani.game.npc.fightkiln.HarAkenTentacle;
import net.kagani.game.npc.glacior.Glacyte;
import net.kagani.game.npc.godwars.GodWarMinion;
import net.kagani.game.npc.godwars.armadyl.GodwarsArmadylFaction;
import net.kagani.game.npc.godwars.armadyl.KreeArra;
import net.kagani.game.npc.godwars.bandos.GeneralGraardor;
import net.kagani.game.npc.godwars.bandos.GodwarsBandosFaction;
import net.kagani.game.npc.godwars.saradomin.CommanderZilyana;
import net.kagani.game.npc.godwars.saradomin.GodwarsSaradominFaction;
import net.kagani.game.npc.godwars.zammorak.KrilTstsaroth;
import net.kagani.game.npc.godwars.zaros.Nex;
import net.kagani.game.npc.godwars.zaros.NexMinion;
import net.kagani.game.npc.godwars.zaros.ZarosMinion;
import net.kagani.game.npc.others.ClueNPC;
import net.kagani.game.npc.others.GuthixBlessing;
import net.kagani.game.npc.others.MirrorBackSpider;
import net.kagani.game.npc.pest.PestPortal;
import net.kagani.game.npc.qbd.QueenBlackDragon;
import net.kagani.game.npc.vorago.Vorago;
import net.kagani.game.npc.vorago.VoragoHandler;
import net.kagani.game.npc.vorago.combat.VoragoCombat;
import net.kagani.game.player.ActionBar;
import net.kagani.game.player.ActionBar.Shortcut;
import net.kagani.game.player.CombatDefinitions;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.content.Slayer;
import net.kagani.game.player.controllers.Wilderness;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.MapAreas;
import net.kagani.utils.Utils;

public class PlayerCombatNew extends Action {

    private Entity target;
    private Shortcut nextAbility;

    public void setNextAbility(Shortcut ability) {
	nextAbility = ability;
    }

    public PlayerCombatNew(Entity target) {
	this.target = target;
    }

    @Override
    public boolean start(Player player) {
	if (!process(player))
	    return false;
	player.setNextFaceEntity(target);
	return true;
    }

    @Override
    public boolean process(Player player) {
	//checks if target is ingame and not ded
	if (player.isDead() || player.hasFinished() || player.isCantWalk())
	    return false;
	if (target.isDead() || target.hasFinished()) {
	    //fixes fact u had already started attacked and then target died
	    int mainHandDelay = (int) (player.getCombatDefinitions().getMainHandDelay() - Utils.currentWorldCycle());
	    int offHandDelay = (int) (player.getCombatDefinitions().getOffHandDelay() - Utils.currentWorldCycle());
	    if (mainHandDelay > 0 || offHandDelay > 0) {

		player.setNextAnimation(new Animation(-1));
		//doesnt work <.
		/*	if(player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON) != Combat.MELEE_TYPE
						|| (player.getCombatDefinitions().getType(Equipment.SLOT_SHIELD) != Combat.MELEE_TYPE && player.getEquipment().hasOffHand() && !player.getEquipment().hasShield())) {
					World.sendProjectileNew(player, target, -1, 0, 0, 0, 0, 0, 0);

				}*/

	    }
	    return false;
	}
	//checks if player appears in viewport
	if (!player.withinDistance(target, 16))
	    return false;
	//checks if can interact with that target
	if (target instanceof Player) {
	    Player p2 = (Player) target;
	    if (!player.isCanPvp() || !p2.isCanPvp())
		return false;
	}
	else {
	    NPC n = (NPC) target;
	    if (n.isCantInteract())
		if (n.getId() == 17184) {//Vorago in bring him down
		    Vorago rago = VoragoHandler.vorago;
		    int maxCount = 20*(VoragoHandler.getPlayersCount());
		    double A = Math.floor(maxCount/5);
		    rago.bringDownCount++;
		    if (!player.isSiphoningRago) {
			player.isSiphoningRago = true;
			player.setNextAnimation(new Animation(20394));
			player.setNextFaceEntity(n);
		    } else {
			int i;
			if (rago.bringDownCount <= A) {
			    i = 1;
			} else if (rago.bringDownCount > A && rago.bringDownCount <= 2*A) {
			    i = 2;
			} else if (rago.bringDownCount > 2*A && rago.bringDownCount <= 3*A) {
			    i = 3;
			} else if (rago.bringDownCount > 3*A && rago.bringDownCount <= 4*A) {
			    i = 4;
			} else if (rago.bringDownCount > 4*A && rago.bringDownCount <= 5*A) {
			    i = 5;
			} else {
			    rago.isDown = false;
			    VoragoCombat.sendBringHimDown(n);
			    i = 6;
			    for (Player p : VoragoHandler.getPlayers()) {
				p.setNextAnimation(new Animation(20394 + i));
				p.isSiphoningRago = false;
			    }
			}
			player.setNextAnimation(new Animation(20394+i));
					
		    }
		  
		    return false;
		} else {
		    return false;   
		}
	    //checks if familiar and if so if can attack
	    if (n instanceof Familiar) {
		Familiar familiar = (Familiar) n;
		if (!familiar.canAttack(player))
		    return false;
	    }
	    else {
		Vorago rago = VoragoHandler.vorago;
		if (!rago.canBeAttacked && (n.getId() == 17182 || n.getId() == 17183 || n.getId() == 17184)) {
		    return false;
		}
		//if npc has been defined not to be attackable outside of its path, prevents it
		if (!n.canBeAttackFromOutOfArea() && !MapAreas.isAtArea(n.getMapAreaNameHash(), player))
		    return false;
		//checks if is slayer creature and if so, if player has lvl to attack it
		int slayerLevel = Slayer.getLevelRequirement(n.getName());
		if (slayerLevel > player.getSkills().getLevel(Skills.SLAYER)) {
		    player.getPackets().sendEntityMessage(1, 0xFFFFFF, player, "You need a slayer level of " + slayerLevel + " to know how to wound this monster.");
		    return false;
		}
		//checks for npc exeptions that cant be attacked
		if (isAttackExeption(player, n))
		    return false;
	    }
	}
	//if player is frozen and under, stops attacking, else stands waiting
	if (player.isStunned() || player.isBound())
	    return !Utils.colides(player, target);
	//colission check
	if (Utils.colides(player, target) && !target.hasWalkSteps()) {
	    player.resetWalkSteps();
	    return player.calcFollow(target, true);
	}
	//diagonal check
	if (hasMeleeHand(player) && Math.abs(player.getX() - target.getX()) == 1 && Math.abs(player.getY() - target.getY()) == 1 && !target.hasWalkSteps() && target.getSize() == 1) {
	    player.resetWalkSteps();
	    if (!player.addWalkSteps(target.getX(), player.getY(), 1)) {
		player.resetWalkSteps();
		player.addWalkSteps(player.getX(), target.getY(), 1);
	    }
	    return true;
	}
	//range check
	if (!Utils.isOnRange(player, target, getAttackRange(player)) || !player.clipedProjectile(target, hasMeleeHand(player) && !checkAttackPathAsRange(target))) {
	    if (!player.hasWalkSteps() || target.hasWalkSteps()) {
		player.resetWalkSteps();
		player.calcFollow(target, player.getRun() ? 2 : 1, true, true);
	    }
	}
	else
	    player.resetWalkSteps();
	return true;
    }

    /*
     * those mobs are above water/lava so melee wont be able to reach, therefore check as if range
     */
    private static boolean checkAttackPathAsRange(Entity target) {
	return target instanceof ShadowForgerIhlakhizan || target instanceof PestPortal || target instanceof NexMinion || target instanceof HarAken || target instanceof HarAkenTentacle || target instanceof QueenBlackDragon;
    }

    private boolean isAttackExeption(Player player, NPC n) {
	if (n.getId() == 879) {
	    if (player.getEquipment().getWeaponId() != 2402) {
		player.getPackets().sendGameMessage("I'd better wield Silverlight first.");
		return true;
	    }
	}
	else if (n.getId() >= 14084 && n.getId() <= 14139) {
	    int weaponId = player.getEquipment().getWeaponId();
	    if (!((weaponId >= 13117 && weaponId <= 13146) || (weaponId >= 21580 && weaponId <= 21582))) {
		player.getPackets().sendGameMessage("I'd better wield a silver weapon first.");
		return true;
	    }
	}
	else if (n instanceof GodwarsArmadylFaction) {
	    if (hasMeleeHand(player)) {
		player.getPackets().sendGameMessage("The Aviansie is flying too high for you to attack using melee.");
		return true;
	    }
	}
	else if (n.getId() == 14301 || n.getId() == 14302 || n.getId() == 14303 || n.getId() == 14304) {
	    Glacyte glacyte = (Glacyte) n;
	    if (glacyte.getGlacor().getTargetIndex() != -1 && player.getIndex() != glacyte.getGlacor().getTargetIndex()) {
		player.getPackets().sendGameMessage("This isn't your target.");
		return true;
	    }
	}
	else if (n.getId() == 1007 || n.getId() == 1264 || n.getId() == 5144 || n.getId() == 5145) {
	    ClueNPC npc = (ClueNPC) n;
	    if (npc.getTarget() != player) {
		player.getPackets().sendGameMessage("This isn't your target.");
		return true;
	    }
	}
	else if (n.getId() == 16980) {
	    GuthixBlessing blessing = (GuthixBlessing) n;
	    if (!player.isCanPvp()) {
		player.getPackets().sendGameMessage("You cannot fight other people's followers here.");
		return true;
	    }
	    else if (blessing.getTarget() == player) {
		player.getPackets().sendGameMessage("You cannot fight your own follower.");
		return true;
	    }
	}
	return false;
    }

    private static boolean hasMeleeHand(Player player) {
	return player.getEquipment().getItem(Equipment.SLOT_WEAPON) == null || (player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON) == Combat.MELEE_TYPE || (player.getEquipment().hasOffHand() && player.getCombatDefinitions().getType(Equipment.SLOT_SHIELD) == Combat.MELEE_TYPE));
    }

    private static int calculateAttackRange(Player player, boolean mainHand) {
	int weaponType = player.getCombatDefinitions().getType(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD);
	Item item = player.getEquipment().getItem(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD);
	if (item == null || !mainHand && player.getEquipment().hasShield())
	    return mainHand ? 0 : -1;
	if (weaponType == Combat.MAGIC_TYPE)
	    return 8;
	int id = item.getId();
	if (weaponType == Combat.MELEE_TYPE) {
	    if (mainHand)
		if (item.getName().contains("halberd") || item.getName().contains("polearm"))
		    return 1;
	    return 0;
	}
	int style = player.getCombatDefinitions().getStyle(!mainHand);
	int speed = getAttackSpeed(player, mainHand);
	if (style == Combat.THROWN_STYLE) {
	    if (speed == 5)
		return 7;
	    else if (speed == 6) {// darts, thrownaxe
		if (id == 13879 || id == 13953)
		    return 7;
		else if (id == 21364)//sagie
		    return 9;
		return 4;
	    }
	    else if (speed == 4) {// knives, javelin
		if (id == 30574 || id == 30575)// death lotus darts
		    return 6;
		return 5;
	    }
	}
	else if (style == Combat.ARROW_STYLE || style == Combat.BOLT_STYLE) {
	    if (speed == 4 || speed == 5 || speed == 12)
		return 7;
	    else if (speed == 6) {
		if (id == 24338 || id == 24339 || id == 18331 || id == 18332 || id == 29634)//royal crossbow & slighted bows
		    return 9;
		return 8;
	    }
	}
	return 0;
    }

    private static int getAttackRange(Player player) {
	int mainHandRange = calculateAttackRange(player, true);
	int offHandRange = calculateAttackRange(player, false);
	if (offHandRange == -1)
	    return mainHandRange;
	else if (mainHandRange > offHandRange)
	    return offHandRange;
	else if (offHandRange > mainHandRange)
	    return mainHandRange;
	return mainHandRange;
    }

    @Override
    public int processWithDelay(Player player) {
	int mainHandDelay = (int) (player.getCombatDefinitions().getMainHandDelay() - Utils.currentWorldCycle());
	if (nextAbility == null && mainHandDelay > 0) {
	    if (!player.getEquipment().hasOffHand())
		return 0;
	    int offHandDelay = (int) (player.getCombatDefinitions().getOffHandDelay() - Utils.currentWorldCycle());
	    if (offHandDelay > 0)
		return 0;
	}
	if (player.getActionbar().isComboOn() || player.isStunned() || (player.getActionbar().hasQueuedShortcut()

		&& player.getVarsManager().getBitValue(ActionBar.REGENERATION_VARBIT_ID) == 0

		&& nextAbility == null))
	    return 0;

	//player is walking to atm
	if (!isWithinDistance(player, target)) //doesnt let u attack when u under / while walking out, remove this check if u want
	    return 0;
	if (!player.getControlerManager().keepCombating(target))
	    return -1;
	return attack(player, nextAbility != null || mainHandDelay <= 0);
    }

    public static boolean isWithinDistance(Player player, Entity target) {
	//player is walking to atm
	if (!player.clipedProjectile(target, hasMeleeHand(player) && !checkAttackPathAsRange(target)) || !Utils.isOnRange(player, target, getAttackRange(player)
		//correct extra distance for walk. no glitches this way ^^. even if frozen
		+ (player.hasWalkSteps() && target.hasWalkSteps() ? (player.getRun() && target.getRun() ? 2 : 1) : 0)) || Utils.colides(player, target)) //doesnt let u attack when u under / while walking out, remove this check if u want
	    return false;
	return true;
    }

    private int attack(Player player, boolean mainHand) {
	switch (player.getCombatDefinitions().getType(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD)) {
	case Combat.MAGIC_TYPE:
	    return attackMagic(player, mainHand);
	case Combat.RANGE_TYPE:
	    return attackRange(player, mainHand);
	case Combat.MELEE_TYPE:
	default:
	    return attackMelee(player, mainHand);
	}

    }

    private static interface SpellEffect {

	void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell);

    }

    private static final SpellEffect SMOKE_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    target.getEffectsManager().startEffect(new Effect(EffectType.SMOKE_EFFECT, 17, mainTarget ? 0.95 : 0.97));
	}
    };

    private static final SpellEffect SHADOW_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    target.getEffectsManager().startEffect(new Effect(EffectType.SHADOW_EFFECT, 17, mainTarget ? 0.95 : 0.97));

	}
    };

    private static final SpellEffect BLOOD_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    player.heal((int) (hit.getDamage() * (mainTarget ? 0.05 : 0.03)));
	}
    };

    private static final SpellEffect ICE_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    if (hit.getDamage() > 0) {
		int delay = target instanceof NPC ? 17 : spell == CombatSpell.ICE_BARRAGE ? 16 : spell == CombatSpell.ICE_RUSH ? 12 : 8;
		EffectsManager.addBoundEffect(target, delay, true, 5);
	    }
	}
    };

    private static final SpellEffect CONFUSE_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    target.getEffectsManager().startEffect(new Effect(EffectType.CONFUSE_EFFECT, 100, mainTarget ? 0.95 : 0.97));
	}
    };

    private static final SpellEffect CURSE_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    target.getEffectsManager().startEffect(new Effect(EffectType.CURSE_EFFECT, 100, mainTarget ? 1.05 : 1.03));
	}
    };

    private static final SpellEffect WEAKEN_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    target.getEffectsManager().startEffect(new Effect(EffectType.WEAKEN_EFFECT, 100, mainTarget ? 0.95 : 0.97));
	}
    };

    private static final SpellEffect STAGGER_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    target.getEffectsManager().startEffect(new Effect(EffectType.STAGGER_EFFECT, 100, mainTarget ? 0.90 : 0.93));
	}
    };

    private static final SpellEffect VULNERABILITY_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    target.getEffectsManager().startEffect(new Effect(EffectType.VULNERABILITY_EFFECT, 100, mainTarget ? 1.10 : 1.07));
	}
    };

    private static final SpellEffect ENFEEBLE_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    target.getEffectsManager().startEffect(new Effect(EffectType.ENFEEBLE_EFFECT, 100, mainTarget ? 0.90 : 0.93));
	}
    };

    private static final SpellEffect BIND_EFFECT = new SpellEffect() {
	@Override
	public void execute(Player player, Entity target, Hit hit, boolean mainTarget, CombatSpell spell) {
	    if (!target.isBoundImmune()) {
		int delay = spell == CombatSpell.ENTANGLE ? 66 : spell == CombatSpell.SNARE ? 50 : 33;
		if (target instanceof Player)
		    delay /= 2;
		EffectsManager.addBoundEffect(target, delay, true, 5);
	    }
	}
    };

    //handle spell here(requires emote and project height/angle) and special effects if any
    private static enum CombatSpell {
	//standart spells damage
	AIR_STRIKE(14, 14221, 30, 30, 99, null, false), WATER_STRIKE(16, 14220, 30, 30, 90, null, false), EARTH_STRIKE(18, 14222, 35, 35, 90, null, false), FIRE_STRIKE(21, 14223, 30, 30, 90, null, false), AIR_BOLT(23, 14221, 30, 30, 90, null, false), WATER_BOLT(37, 14220, 30, 30, 90, null, false), EARTH_BOLT(30, 14222, 35, 35, 90, null, false), FIRE_BOLT(33, 14223, 30, 30, 90, null, false), AIR_BLAST(37, 14221, 30, 30, 90, null, false), WATER_BLAST(40, 14220, 30, 30, 90, null, false), EARTH_BLAST(46, 14222, 35, 35, 90, null, false), FIRE_BLAST(51, 14223, 30, 30, 90, null, false), AIR_WAVE(58, 14221, 30, 30, 90, null, false), WATER_WAVE(61, 14220, 30, 30, 90, null, false), EART_WAVE(65, 14222, 35, 35, 90, null, false), FIRE_WAVE(68, 14223, 30, 30, 90, null, false), AIR_SURGE(73, 14221, 30, 30, 90, null, false), WATER_SURGE(78, 14220, 30, 30, 90, null, false), EARTH_SURGE(76, 14222, 35, 35, 90, null, false), FIRE_SURGE(80, 14223, 30, 30, 90, null, false), DIVINE_STORM(54, 10546, 18, 18, 90, null, false), ARMADYL_STORM(69, 10546, 18, 18, 90, null, false), SLAYER_DART(44, 1978, 35, 35, 5, null, false)
	//ancient spells
	, SMOKE_RUSH(81, 1978, 25, 25, 90, SMOKE_EFFECT, false), SHADOW_RUSH(82, 1978, 25, 25, 90, SHADOW_EFFECT, false), BLOOD_RUSH(84, 1978, 22, 22, 90, BLOOD_EFFECT, false), ICE_RUSH(85, 1978, 22, 22, 90, ICE_EFFECT, false), SMOKE_BURST(87, 1979, 25, 25, 90, SMOKE_EFFECT, true), SHADOW_BURST(88, 1979, 25, 25, 90, SHADOW_EFFECT, true), BLOOD_BURST(90, 1979, 22, 22, 90, BLOOD_EFFECT, true), ICE_BURST(91, 1979, 22, 22, 90, ICE_EFFECT, true), SMOKE_BLITZ(93, 1978, 25, 25, 90, SMOKE_EFFECT, false), SHADOW_BLITZ(94, 1978, 25, 25, 90, SHADOW_EFFECT, false), BLOOD_BLITZ(96, 1978, 22, 22, 90, BLOOD_EFFECT, false), ICE_BLITZ(97, 1978, 22, 22, 90, ICE_EFFECT, false), SMOKE_BARRAGE(99, 1979, 25, 25, 90, SMOKE_EFFECT, true), SHADOW_BARRAGE(100, 1979, 25, 25, 90, SHADOW_EFFECT, true), BLOOD_BARRAGE(102, 1979, 22, 22, 90, BLOOD_EFFECT, true), ICE_BARRAGE(103, 1979, 22, 22, 90, ICE_EFFECT, true)
	//standart spells curses
	, CONFUSE(15, 711, 18, 18, 90, CONFUSE_EFFECT, false), WEAKEN(20, 716, 18, 18, 90, WEAKEN_EFFECT, false), CURSE(24, 711, 18, 18, 90, CURSE_EFFECT, false), VULNERABILITY(63, 729, 18, 18, 90, VULNERABILITY_EFFECT, false), ENFEEBLE(66, 729, 18, 18, 90, ENFEEBLE_EFFECT, false), STAGGER(71, 729, 18, 18, 90, STAGGER_EFFECT, false)
	//stndart spells binds
	, BIND(25, 710, 18, 18, 90, BIND_EFFECT, false), SNARE(43, 710, 18, 18, 90, BIND_EFFECT, false), ENTANGLE(70, 710, 18, 18, 90, BIND_EFFECT, false)
	//all books
	, POLYPORE_STRIKE(162, 15448, 50, 32, 90, null, false);

	//0 - bothands. 1 - hand1. 2 - hand2.
	private Animation attackAnim;
	private int id, pStartHeight, pEndHeight, pAngle;
	private SpellEffect effect;
	private boolean multi;

	private CombatSpell(int id, int attackAnim, int pStartHeight, int pEndHeight, int pAngle, SpellEffect effect, boolean multi) {
	    this.id = id;
	    this.attackAnim = new Animation(attackAnim);
	    this.pStartHeight = pStartHeight;
	    this.pEndHeight = pEndHeight;
	    this.pAngle = pAngle;
	    this.effect = effect;
	    this.multi = multi;
	}

	private static CombatSpell getCombatSpell(int id) {
	    for (CombatSpell spell : CombatSpell.values())
		if (spell.id == id)
		    return spell;
	    return null;
	}
    }

    private void resetSpell(Player player, boolean manualCast) {
	if (manualCast) {
	    player.getCombatDefinitions().resetSpells(false);
	    //manual casting no longer resets combat from what im aware of
	    //	player.getActionManager().forceStop();
	}
    }

    private int attackMagic(final Player player, boolean mainHand) {
	int spellId = player.getCombatDefinitions().getSpellId();
	if (spellId == 0) {
	    player.getPackets().sendGameMessage("You need to set a spell in your magic book to use magic.");
	    return -1;
	}

	boolean manualCast = spellId >= 256;
	if (manualCast)
	    spellId -= 256;
	//no need to check lvl. the single fact he could select spell means he has lvl
	GeneralRequirementMap spellData = Magic.getSpellData(spellId);

	if (spellId == 44) { //slayer dart exeption required

	    Item weapon = player.getEquipment().getItem(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD);
	    if (!Magic.hasStaffOfLight(weapon.getId()) && weapon.getId() != 30793 && weapon.getId() != 30796 && weapon.getId() != 4170) {
		//TODO real message
		player.getPackets().sendGameMessage("You can't cast this spell with this weapon.");
		resetSpell(player, manualCast); //cant reset spell earlier cuz else it gets damage for wrong spell
		return -1;
	    }
	}
	else if (spellId == 162) { //polypore  strike exeption required
	    Item weapon = player.getEquipment().getItem(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD);
	    if (weapon.getId() != 22494 && weapon.getId() != 22496) {
		player.getPackets().sendGameMessage("You can't cast this spell with this weapon.");
		resetSpell(player, manualCast); //cant reset spell earlier cuz else it gets damage for wrong spell
		return -1;
	    }
	    if (weapon.getId() == 22494) {
		player.getCharges().addCharges(22496, ItemConstants.getItemDefaultCharges(22496), Equipment.SLOT_WEAPON);
		player.getEquipment().getItem(Equipment.SLOT_WEAPON).setId(22496);
		player.getEquipment().refresh(Equipment.SLOT_WEAPON);
	    }
	    player.getCharges().addCharges(22496, -1, Equipment.SLOT_WEAPON);
	}

	//not having runes no longer resets spell, instead just stops combat
	if (!Magic.checkRunes(player, spellData, (nextAbility == null || player.getEffectsManager().hasActiveEffect(EffectType.METAMORPHISIS)))) {
	    resetSpell(player, manualCast); //cant reset spell earlier cuz else it gets damage for wrong spell
	    return -1;
	}
	if (nextAbility != null)
	    return handleAbilityAttack(player);
	if (mainHand && player.getCombatDefinitions().isUsingSpecialAttack())
	    return handleSpecialAttack(player);

	int pStartHeight;
	int pEndHeight;
	int pAngle;
	boolean multi;
	Animation attackAnim;
	final SpellEffect effect;
	final Hit hit = getHit(player, mainHand, Combat.MAGIC_TYPE);

	final CombatSpell spell = CombatSpell.getCombatSpell(spellId);

	if (spell == null) { //shouldnt happen unless spell not coded
	    attackAnim = new Animation(getAttackAnimation(player, mainHand));
	    pStartHeight = 40;
	    pEndHeight = 36;
	    pAngle = 90;
	    effect = null;
	    multi = false;
	}
	else {
	    attackAnim = spell.attackAnim;
	    pStartHeight = spell.pStartHeight;
	    pEndHeight = spell.pEndHeight;
	    pAngle = spell.pAngle;
	    effect = spell.effect;
	    multi = spell.multi;
	}
	AnimationDefinitions attackAnimDefs = attackAnim.getDefinitions();
	if (player.getCombatDefinitions().getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
	    attackAnim = new Animation(spellData.getIntValue(player.getEquipment().hasTwoHandedWeapon() ? 2919 : 2914));
	Integer startGfx = attackAnimDefs.clientScriptData == null ? null : (Integer) attackAnimDefs.clientScriptData.get(2920);

	int projectileGfx = spellData.getIntValue(2940);
	int projectileDelay;
	int projectileTime;
	int hitDelay;

	int spellBook = Magic.getSpellBook(spellData);

	if (spellBook == 1) { //ancient spells exeption required sadly
	    if (spell == CombatSpell.ICE_RUSH || spell == CombatSpell.ICE_BURST || spell == CombatSpell.ICE_BLITZ) {
		if (spell == CombatSpell.ICE_BLITZ)
		    startGfx = 366;
		projectileTime = 60;
		hitDelay = 2;
	    }
	    else {
		projectileTime = 0;
		hitDelay = 1;
	    }
	    projectileDelay = 0;
	    int type = Magic.getSpellType(spellData);

	    //rldTile from, WorldTile to, int graphicId, int startHeight, int endHeight, int startTime, int speed, int slope, int angle) {
	    //	if (type == 1 || type == 3) //means its smoke or shadow
	    if (spell != CombatSpell.ICE_BLITZ)
		World.sendProjectileNew(player, target, projectileGfx, pStartHeight, pEndHeight, 51, 5, 0, pAngle);
	}
	else {
	    projectileDelay = Magic.isAutoCastSpell(spellData) ? 55 : 15;//attackAnimDefs.getEmoteClientCycles() - (spell == CombatSpell.POLYPORE_STRIKE ? 60 : 35);
	    projectileTime = World.sendProjectileNew(player, target, projectileGfx, pStartHeight, pEndHeight, projectileDelay, 5, 0, pAngle).getEndTime();
	    hitDelay = Utils.projectileTimeToCycles(projectileTime);
	    if (spell == CombatSpell.FIRE_SURGE) { //exeption required for those spells as data not in cache for them or diff
		World.sendProjectileNew(player, target, 2736, pStartHeight, pEndHeight, projectileDelay, 5, 20, 90).getEndTime();
		World.sendProjectileNew(player, target, 2736, pStartHeight, pEndHeight, projectileDelay, 5, 110, 90).getEndTime();

	    }
	    else if (spell == CombatSpell.WATER_SURGE)
		startGfx++;
	    else if (spell == CombatSpell.EARTH_STRIKE)
		startGfx--;
	    else if (spell == CombatSpell.EARTH_BLAST)
		startGfx++;
	    else if (spell == CombatSpell.EART_WAVE)
		startGfx += 2;
	    else if (spell == CombatSpell.EARTH_SURGE)
		startGfx += 3;
	    else if (spell == CombatSpell.CONFUSE)
		startGfx = 102;
	    else if (spell == CombatSpell.WEAKEN)
		startGfx = 105;
	    else if (spell == CombatSpell.CURSE)
		startGfx = 110;
	    else if (spell == CombatSpell.VULNERABILITY)
		startGfx = 167;
	    else if (spell == CombatSpell.ENFEEBLE)
		startGfx = 170;
	    else if (spell == CombatSpell.STAGGER)
		startGfx = 173;
	    else if (spell == CombatSpell.BIND || spell == CombatSpell.SNARE || spell == CombatSpell.ENTANGLE)
		startGfx = 177;
	    else if (spell == CombatSpell.POLYPORE_STRIKE)
		startGfx = 2034;
	}

	player.setNextAnimation(attackAnim);
	if (startGfx != null)
	    player.setNextGraphics(new Graphics(startGfx));

	if (hit.getDamage() == -1) { //splash
	    target.setNextGraphics(new Graphics(85, projectileTime, 96));
	    //	playSound(227, player, target);
	    delayHit(target, hitDelay, hit); //had to write 2x. this one non multi as splash
	}
	else { //process spell
	    if (Magic.getSpellDamage(spellData) == 0)
		hit.setDamage(-1); //no damage spell, but not splash either
	    int targetGfx = spellData.getIntValue(2933);
	    Entity[] targets = !multi ? new Entity[] { target } : getMultiAttackTargets(player);
	    for (final Entity target : targets) {
		if (targetGfx != 0)
		    target.setNextGraphics(new Graphics(targetGfx, projectileTime, spellBook == 1 ? 0 : 96));
		final Hit hitTarget = target == this.target ? hit : getHit(player, target, mainHand, Combat.MAGIC_TYPE, 1, 1, false);
		delayHit(target, hitDelay, hitTarget);
		if (effect != null) {
		    WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
			    if (player.hasFinished() || target.isDead() || target.hasFinished() || (target instanceof NPC && ((NPC) target).isCantInteract()))
				return;
			    effect.execute(player, target, hitTarget, target == PlayerCombatNew.this.target, spell);
			}
		    }, projectileTime == 0 ? 0 : (Utils.projectileTimeToCycles(projectileTime) - 1));
		}
	    }
	}
	resetSpell(player, manualCast); //cant reset spell earlier cuz else it gets damage for wrong spell
	if (player.getCombatDefinitions().getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
	    addAdrenaline(player, mainHand ? player.getEquipment().hasTwoHandedWeapon() ? 3 : 2 : 1);
	setWeaponDelay(player, mainHand);
	return 0;
    }

    public interface MultiAttack {
	public boolean attack();
    }

    public void attackTarget(Entity[] targets, MultiAttack perform) {
	Entity realTarget = target;
	for (Entity t : targets) {
	    target = t;
	    if (!perform.attack())
		break;
	}
	target = realTarget;
    }

    private boolean checkAmmo(Player player, boolean mainHand) {
	ItemDefinitions defs = player.getEquipment().getItem(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD).getDefinitions();
	if (defs.getCSOpcode(2940) != 0) //no need for ammo
	    return true;
	Item ammo = player.getEquipment().getItem(Equipment.SLOT_ARROWS);
	if (ammo == null) {
	    player.getPackets().sendEntityMessage(1, 0xFFFFFF, player, "You have no ammo equipped.");
	    return false;
	}
	int attackStyle = player.getCombatDefinitions().getStyle(!mainHand);
	boolean result;
	switch (attackStyle) {
	case Combat.ARROW_STYLE:
	    result = ammo.getName().contains("arrow");
	    break;
	case Combat.BOLT_STYLE:
	    //handcannon exeption
	    result = defs.id == 15241 ? ammo.getId() == 15243 : ammo.getName().contains("bolt");
	    break;
	case Combat.THROWN_STYLE: //throwables not supposed to use ammo
	default:
	    result = false;
	    break;
	}
	if (result)
	    result = defs.getCSOpcode(750) >= ammo.getDefinitions().getCSOpcode(750);
	    if (!result)
		player.getPackets().sendEntityMessage(1, 0xFFFFFF, player, "You can't use that ammunition with your weapon.");
	    return result;

    }

    private int attackRange(Player player, boolean mainHand) {
	if (!checkAmmo(player, mainHand))
	    return -1;
	if (nextAbility != null)
	    return handleAbilityAttack(player);
	if (mainHand && player.getCombatDefinitions().isUsingSpecialAttack())
	    return handleSpecialAttack(player);

	Animation attackAnim = new Animation(getAttackAnimation(player, mainHand));

	int projectileDelay;// = attackAnim.getDefinitions().getEmoteClientCycles();

	//8=Arrow, 9=Bolt, 10=Thrown
	int attackStyle = player.getCombatDefinitions().getStyle(!mainHand);
	ItemDefinitions defs = player.getEquipment().getItem(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD).getDefinitions();

	int projectileGfx = defs.getCSOpcode(2940);
	if (projectileGfx == 0) { //uses ammo
	    ItemDefinitions ammoDefs = player.getEquipment().getItem(Equipment.SLOT_ARROWS).getDefinitions();
	    projectileGfx = ammoDefs.getCSOpcode(2940);
	}

	//Load graphics no longer exist on rs.
	//Graphics loadGfx = getLoadWeaponGraphic(defs.id, defs.getCSOpcode(2940) != 0 ? -1 : player.getEquipment().getItem(Equipment.SLOT_ARROWS).getId());
	player.setNextAnimation(attackAnim);
	/*if (loadGfx != null)
			player.setNextGraphics(loadGfx);*/

	switch (attackStyle) {
	case Combat.ARROW_STYLE:

	    projectileDelay = 55;

	    //projectileDelay -= 15;
	    if (Combat.hasDarkbow(player)) { //darkbow exeption
		int slope = Utils.random(5);
		int delay = Utils.projectileTimeToCycles(World.sendProjectileNew(player, target, projectileGfx, 40, 41, projectileDelay, 5, slope, 5).getEndTime()) - 1;
		World.sendProjectileNew(player, target, projectileGfx, 40, 41, projectileDelay, 5, slope + 5, 5);
		delayHit(delay, getHit(player, mainHand, Combat.RANGE_TYPE));
		delayHit(delay + 1, getHit(player, mainHand, Combat.RANGE_TYPE));
		removeAmmo(player, 2, delay);
	    }
	    else {
		int delay = Utils.projectileTimeToCycles(World.sendProjectileNew(player, target, projectileGfx, 40, 41, projectileDelay, 5, Utils.random(5), 5).getEndTime()) - 1;
		//Exceptions
		String name = defs.getName().toLowerCase();
		if (name.contains("zaryte") || name.contains("crystal"))
		    player.getCharges().addCharges(defs.id, -1, Equipment.SLOT_WEAPON);
		delayHit(delay, getHit(player, mainHand, Combat.RANGE_TYPE));
		if (defs.getCSOpcode(2940) == 0) //crystal bow doesnt use ammo for instance
		    removeAmmo(player, 1, defs.getName().contains("bow") ? delay : -1);
	    }
	    break;
	case Combat.BOLT_STYLE:

	    projectileDelay = defs.getEquipLookHideSlot() == 5 ? 55 : !mainHand ? 40 : 15;

	    if (defs.id == 15241 && Utils.random(player.getSkills().getLevel(Skills.FIREMAKING) << 1) == 0) {//handcannon exeption
		player.setNextAnimation(new Animation(12175));
		player.setNextGraphics(new Graphics(2140));
		player.getEquipment().getItems().set(3, null);
		player.getEquipment().refresh(3);
		player.getAppearence().generateAppearenceData();
		player.applyHit(new Hit(player, Utils.random(1500) + 100, HitLook.REGULAR_DAMAGE));
		setWeaponDelay(player, mainHand);
		return 0;
	    }
	    //projectileDelay -= 20;
	    int delay = Utils.projectileTimeToCycles(World.sendProjectileNew(player, target, projectileGfx, 40, 41, projectileDelay, 5, Utils.random(5), 5).getEndTime()) - 1;
	    delayHit(delay, getHit(player, mainHand, Combat.RANGE_TYPE));
	    if (defs.getCSOpcode(2940) == 0)
		removeAmmo(player, 1, defs.getName().contains("crossbow") ? delay : -1);
	    break;
	case Combat.THROWN_STYLE:
	    projectileDelay = 35;
	    delay = World.sendProjectileNew(player, target, projectileGfx, 40, 41, projectileDelay, 5, Utils.random(5), 5).getEndTime() - 1;
	    boolean usingChinchompa = defs.id == 10033 || defs.id == 10034;
	    if (usingChinchompa) { //Chinchompa exeption 
		for (Entity target : getMultiAttackTargets(player))
		    delayHit(target, Utils.projectileTimeToCycles(delay), getHit(player, mainHand, Combat.RANGE_TYPE));
	    }
	    else
		delayHit(Utils.projectileTimeToCycles(delay), getHit(player, mainHand, Combat.RANGE_TYPE));
	    boolean deleteAmmo = defs.id == 732 || usingChinchompa;
	    removeAmmo(player, attackStyle == Combat.THROWN_STYLE ? mainHand ? -1 : -2 : 1, !deleteAmmo ? delay : -1);
	default:
	    break;
	}
	if (player.getCombatDefinitions().getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
	    addAdrenaline(player, mainHand ? player.getEquipment().hasTwoHandedWeapon() ? 3 : 2 : 1);
	setWeaponDelay(player, mainHand);
	return 0;
    }

    //quantity -1 = weapon. -2 offhand weapon
    private void removeAmmo(final Player player, final int quantity, int delay) {
	if (nextAbility != null)//Abilities don't remove ammo ^.^
	    return;
	final int ammoId = quantity == -1 ? player.getEquipment().getWeaponId() : quantity == -2 ? player.getEquipment().getShieldId() : player.getEquipment().getAmmoId();
	player.getEquipment().removeAmmo(ammoId, quantity);
	if (delay != -1)
	    WorldTasksManager.schedule(new WorldTask() {

		@Override
		public void run() {
		    World.updateGroundItem(new Item(ammoId, quantity == -1 || quantity == -2 ? 1 : quantity), target.getMiddleWorldTile(), player);
		}
	    }, delay);
    }

    private int attackMelee(Player player, boolean mainHand) {
	if (nextAbility != null)
	    return handleAbilityAttack(player);
	if (mainHand && player.getCombatDefinitions().isUsingSpecialAttack())
	    return handleSpecialAttack(player);

	Animation attackAnim = new Animation(getAttackAnimation(player, mainHand));
	//so that emote end matches cycle end and mark
	/*	attackAnim.setDelay(60-attackAnim.getDefinitions().getEmoteClientCycles());
			System.out.println("d: "+attackAnim.getSpeed());*/

	/*int animClientCycles = attackAnim.getDefinitions().getEmoteClientCycles();
		int worldCycleDelay = animClientCycles / 60;
		int clientCycleDelay = ((worldCycleDelay + 1) * 60 - animClientCycles) - 10;
		if (clientCycleDelay < 0) //some weaps are faster than cycle lol, those few dont need delay(such as new ags emote)
			clientCycleDelay = 0;

		attackAnim.setDelay(clientCycleDelay);*/

	//rs downgraded combat delay <..<.
	player.setNextAnimation(attackAnim);

	if (player.getCombatDefinitions().getCombatMode() != CombatDefinitions.LEGACY_COMBAT_MODE)
	    addAdrenaline(player, mainHand ? player.getEquipment().hasTwoHandedWeapon() ? 3 : 2 : 1);

	delayHit(/*worldCycleDelay*/0, getHit(player, mainHand, Combat.MELEE_TYPE));
	setWeaponDelay(player, mainHand);
	return 0;
    }

    public static void addAdrenaline(Player player, int amt) {
	player.getCombatDefinitions().increaseSpecialAttack(amt);
    }

    public int handleAbilityAttack(final Player player) {
	int abilityId = nextAbility.getId(player);
	GeneralRequirementMap data = GeneralRequirementMap.getMap(ClientScriptMap.getMap(ActionBar.CS_DATA_ID[nextAbility.getType() - 1]).getIntValue(abilityId == 200 ? 2 : abilityId));

	//2915, 6706

	int currentWeapon = player.getEquipment().getWeaponId();

	//		2919 - mainhand
	//	2914 - offhand

	boolean twoHWep = player.getEquipment().hasTwoHandedWeapon();
	ClientScriptMap map = ClientScriptMap.getMap(data.getIntValue(2915));
	final ItemDefinitions def = ItemDefinitions.getItemDefinitions(currentWeapon);
	int emoteId = data.getIntValue(twoHWep ? 2919 : 2914);
	if (emoteId == 0)
	    emoteId = map.getIntValue(currentWeapon == -1 ? 0 : def.getCSOpcode(686));
	if ((nextAbility.getType() == ActionBar.MAGIC_ABILITY_SHORTCUT && nextAbility.getId(player) == 164) || nextAbility.getType() == ActionBar.RANGED_ABILITY_SHORTCUT && nextAbility.getId(player) == 14)
	    emoteId = -1;
	int projectileId = def.getCSOpcode(2940);
	if (projectileId == 0)
	    projectileId = data.getIntValue(2940);
	final int targetGFX = data.getIntValue(2933);

	Animation attackAnim = new Animation(emoteId);
	AnimationDefinitions defs = attackAnim.getDefinitions();

	player.setNextAnimation(attackAnim);

	Integer startGfx = defs.clientScriptData == null ? null : (Integer) defs.clientScriptData.get(2920);
	if (startGfx != null) {
	    Integer startGfxHeight = (Integer) defs.clientScriptData.get(4339);
	    player.setNextGraphics(new Graphics(startGfx, 0, startGfxHeight == null ? 0 : startGfxHeight));
	}

	//if (Settings.DEBUG)
	//System.out.println("Emote: " +emoteId+ ", Projectile: " + projectileId + ", Target GFX: " + targetGFX + ", StartGFX: "+startGfx != null ? startGfx : 0);

	switch (nextAbility.getType()) {
	case ActionBar.MAGIC_ABILITY_SHORTCUT:
	    switch (abilityId) {
	    case 1://WRACK
		delayHit(target, 0, getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, target.getEffectsManager().hasActiveEffect(EffectType.STUNNED) ? 1.25 : 0.94, false, true));
		target.setNextGraphics(new Graphics(targetGFX, 0, 130));
		break;
	    case 6://DRAGON'S BREATH
		Projectile projectile = World.sendProjectileNew(player, target, projectileId, 22, 40, 15, 5, 0, 10);
		int angle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (target.getX() * 2 + target.getSize()), (player.getY() * 2 + player.getSize()) - (target.getY() * 2 + target.getSize()))) / 45d) & 0x7;
		for (Entity t : getMultiAttackTargets(player, target, Utils.getDistance(player, target), 9)) {
		    int nextAngle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (t.getX() * 2 + t.getSize()), (player.getY() * 2 + player.getSize()) - (t.getY() * 2 + t.getSize()))) / 45d) & 0x7;
		    if (nextAngle != angle)
			continue;
		    t.getEffectsManager().startEffect(new Effect(EffectType.PROTECTION_DISABLED, 8));
		    boolean riderAmulet = player.getEquipment().getAmuletId() == 30929;
		    if (riderAmulet && Utils.random(100) < 5) {
			int minumumDamage = getMaxHit(player, t, true, Combat.MAGIC_TYPE, 1.0, true), damage = (int) ((minumumDamage * 0.10) / 3);
			t.getEffectsManager().startEffect(new Effect(EffectType.COMBUST, 10, HitLook.MAGIC_DAMAGE, new Graphics(targetGFX), damage, 4, new WorldTile(t)));
		    }
		    delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, t, true, Combat.MAGIC_TYPE, 1.0, riderAmulet ? 1.98 : 1.88, false, true));
		    break;
		}
		break;
	    case 165://SONIC WAVE
		projectile = World.sendProjectileNew(player, target, projectileId, 32, 40, 40, 5, 0, 10);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 1.57, false, true));
		player.getEffectsManager().startEffect(new Effect(EffectType.MODIFY_ACCURACY, 15, 0.10));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime() / 2, 130));
		break;
	    case 3://IMPACT
	    case 171://DEEP IMPACT
		projectile = World.sendProjectileNew(player, target, projectileId, 32, 37, 15, 5, 0, 10);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, abilityId == 170 ? 2.0 : target.isStunImmune() ? 1.88 : 1.0, false, true));
		target.setStunDelay(abilityId == 170 ? 8 : 5);
		angle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (target.getX() * 2 + target.getSize()), (player.getY() * 2 + player.getSize()) - (target.getY() * 2 + target.getSize()))) / 45d) & 0x7;
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime() / 2, 130, angle, true));
		target.setNextGraphics(new Graphics(3488, projectile.getEndTime(), 92));
		break;
	    case 166://CONCENTRATED BLAST
		player.getEffectsManager().startEffect(new Effect(EffectType.CONCENTRATED_BLAST, 10, projectileId, targetGFX));
		break;
	    case 4://CHAIN
		projectile = World.sendProjectileNew(player, target, projectileId, 22, 39, 15, 5, 0, 10);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 1.0, false, true));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		for (Entity e : getMultiAttackTargets(player, target, 9, 3)) {
		    if (e == target)
			continue;
		    Projectile proj = World.sendProjectileNew(target, e, projectileId, 22, 39, projectile.getEndTime() + 15, 1, 0, 10);
		    e.setNextGraphics(new Graphics(targetGFX, proj.getEndTime(), 130));
		    delayHit(e, Utils.projectileTimeToCycles(proj.getEndTime()), getHit(player, e, true, Combat.MAGIC_TYPE, 1.0, 1.0, false, true));
		}
		break;
	    case 5://COMBUST
		projectile = World.sendProjectileNew(player, target, projectileId, 22, 39, 15, 5, 0, 10);
		if (Combat.getHitChance(player, target, player.getCombatDefinitions().getStyle(false), true) < Utils.random(100)) {
		    delayHit(target, 0, new Hit(player, 0, HitLook.MISSED));
		    break;
		}
		final int minumumDamage = getMaxHit(player, target, true, Combat.MAGIC_TYPE, 1.0, true),
			maximumDamage = getMaxHit(player, target, true, Combat.MAGIC_TYPE, 1.0 + Utils.random(.88), true),
			damage = Utils.random(minumumDamage, maximumDamage) / 5;
		target.setNextGraphics(new Graphics(targetGFX, 0, 130));
		WorldTasksManager.schedule(new WorldTask() {

		    @Override
		    public void run() {
			target.getEffectsManager().startEffect(new Effect(EffectType.COMBUST, 10, HitLook.MAGIC_DAMAGE, new Graphics(targetGFX), damage, 2, player, new WorldTile(target)));
		    }
		});
		break;
	    case 12://OMNIPOWER
		projectile = World.sendProjectileNew(player, target, projectileId, 32, 40, 80, 5, 0, 10);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()), getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 2.0 + Utils.random(2.0), false, true));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		break;
	    case 9://WILD MAGIC
		projectile = World.sendProjectileNew(player, target, projectileId, 32, 42, 40, 5, 0, 10);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 0.5 + Utils.random(1.75), false, true));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		projectile = World.sendProjectileNew(player, target, projectileId, 32, 39, 45, 3, 0, 10);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()), getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 0.5 + Utils.random(1.75), false, true));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		break;
	    case 7://ASPHYXIATE
		player.getEffectsManager().startEffect(new Effect(EffectType.ASPHYXIATE, 10, projectileId, targetGFX));
		break;
	    case 11://TSUNAMI
		angle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (target.getX() * 2 + target.getSize()), (player.getY() * 2 + player.getSize()) - (target.getY() * 2 + target.getSize()))) / 45d) & 0x7;
		World.sendGraphics(player, new Graphics(3560, 0, 0, (angle + 4) & 0x7, true), new WorldTile(player));
		projectile = World.sendProjectileNew(player, target, projectileId, 22, 40, 15, 5, 0, 10);
		for (Entity t : getMultiAttackTargets(player, target, 5, 12, true)) {
		    int nextAngle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (t.getX() * 2 + t.getSize()), (player.getY() * 2 + player.getSize()) - (t.getY() * 2 + t.getSize()))) / 45d) & 0x7;
		    if (nextAngle != angle)
			continue;
		    delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, t, true, Combat.MAGIC_TYPE, 1.0, 2.0 + Utils.random(1.0), false, true));
		}
		player.getEffectsManager().startEffect(new Effect(EffectType.ADRENALINE_GAIN_FOR_CRIT, 50, 10));
		break;
	    case 164://SUNSHINE
		player.getEffectsManager().startEffect(new Effect(EffectType.SUNSHINE, 50, new WorldTile(player)));
		return -1;

	    }
	    break;
	case ActionBar.RANGED_ABILITY_SHORTCUT:
	    if (projectileId == 0) {
		projectileId = ClientScriptMap.getMap(6722).getIntValue(ItemDefinitions.getItemDefinitions(currentWeapon).getCSOpcode(21));
		if (projectileId == 0)
		    projectileId = ClientScriptMap.getMap(6722).getDefaultIntValue();
	    }
	    int startTime = twoHWep ? 44 : 32;
	    boolean usingChin = def.id == 10033 || def.id == 10034;
	    switch (abilityId) {
	    case 1://PIERCING SHOT
		Projectile projectile = World.sendProjectileNew(player, target, projectileId, 41, 38, startTime, 5, 0, 95);
		Entity[] targets = usingChin ? getMultiAttackTargets(player) : new Entity[] { target };
		for (Entity t : targets)
		    delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, t, true, Combat.RANGE_TYPE, 1.0, t.getEffectsManager().hasActiveEffect(EffectType.STUNNED) ? 1.25 : 0.94, false, true));
		break;
	    case 2://BINDING SHOT
	    case 18://TIGHT_BINDINGS
		targets = usingChin ? getMultiAttackTargets(player) : new Entity[] { target };
		for (Entity t : targets) {
		    projectile = World.sendProjectileNew(t == target ? player : target, t, projectileId, 12, 39, 29, 5, 0, 5);
		    delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, t, true, Combat.RANGE_TYPE, 1.0, abilityId == 18 ? 2.0 : t.isStunImmune() ? 1.88D : 1.0D, false, true));
		    t.getEffectsManager().startEffect(new Effect(EffectType.BINDING_SHOT, t instanceof Player ? 16 : 33));
		    t.setStunDelay(5);//Three seconds
		    t.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime() / 2, 92));
		}
		break;
	    case 6://RICOCHET
		projectile = World.sendProjectileNew(player, target, projectileId, 41, 42, startTime, 5, 0, 10);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.0D, 1.0D, false, true));
		for (Entity e : getMultiAttackTargets(player, target, 9, 3)) {
		    if (e == target)
			continue;
		    Projectile proj = World.sendProjectileNew(target, e, projectileId, 41, 42, projectile.getEndTime() + 15, 5, 0, 10);
		    e.setNextGraphics(new Graphics(targetGFX, proj.getEndTime(), 130));
		    delayHit(e, Utils.projectileTimeToCycles(proj.getEndTime()) - 1, getHit(player, e, true, Combat.RANGE_TYPE, 1.0D, 1.0D, false, true));
		}
		break;
	    case 15://DAZING SHOT
		projectile = World.sendProjectileNew(player, target, projectileId, 38, 40, 44, 5, 0, 130);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.0D, 1.57D, false, true));
		target.getEffectsManager().startEffect(new Effect(EffectType.MODIFY_ACCURACY, 15, -0.10));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		break;
	    case 4://SNIPE
		player.getEffectsManager().startEffect(new Effect(EffectType.SNIPE, 5, projectileId, targetGFX, usingChin));
		break;
	    case 5://FRAGMENTATION SHOT
		targets = usingChin ? getMultiAttackTargets(player) : new Entity[] { target };
		projectile = World.sendProjectileNew(player, target, projectileId, 41, 40, startTime, 5, 0, 95);
		if (Combat.getHitChance(player, target, player.getCombatDefinitions().getStyle(false), true) < Utils.random(100)) {
		    delayHit(target, 0, new Hit(player, 0, HitLook.MISSED));
		    break;
		}
		for (Entity t : targets) {
		    final int minumumDamage = getMaxHit(player, t, true, Combat.RANGE_TYPE, 1.0D, true), maximumDamage = getMaxHit(player, t, true, Combat.RANGE_TYPE, 1.0D + Utils.random(.88D), true), damage = Utils.random(minumumDamage, maximumDamage) / 5;
		    WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
			    t.getEffectsManager().startEffect(new Effect(EffectType.FRAGMENTATION, 10, HitLook.RANGE_DAMAGE, new Graphics(3574), damage, 2, player, new WorldTile(target)));
			}
		    });
		}
		break;
	    case 16://NEEDLE_STRIKE
		targets = usingChin ? getMultiAttackTargets(player) : new Entity[] { target };
		for (Entity t : targets) {
		    projectile = World.sendProjectileNew(player, t, projectileId, 41, 40, 35, 5, 0, 78);
		    delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, t, true, Combat.RANGE_TYPE, 1.0D, 1.57D, false, true));
		    player.getEffectsManager().startEffect(new Effect(EffectType.INCREASE_CRIT_CHANCE, 3, 10));
		    t.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		}
		break;
	    case 7://SNAPSHOT
		projectile = World.sendProjectileNew(player, target, projectileId, 32, 42, startTime, 5, 0, 5);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.0 + Utils.random(0.20), false, true));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		projectile = World.sendProjectileNew(player, target, 65535, 32, 39, startTime + 5, 3, 0, 5);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()), getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.0 + Utils.random(1.10), false, true));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		break;
	    case 8://RAPID FIRE
		player.getEffectsManager().startEffect(new Effect(EffectType.RAPID_FIRE, 10, emoteId, projectileId, startTime, usingChin));
		break;
	    case 9://BOMBARDMENT
		projectile = World.sendProjectileNew(player, target, 65535, 41, 40, 60, 2, 0, 5);
		for (Entity e : getMultiAttackTargets(player, target, 1, 9))
		    delayHit(e, Utils.projectileTimeToCycles(projectile.getEndTime()) + 1, getHit(player, e, true, Combat.RANGE_TYPE, 1.0, 2.19, false, true));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 130));
		break;
	    case 10://INCENRARY SHOT
		projectile = World.sendProjectileNew(player, target, projectileId, 38, 39, 44, 5, 0, 130);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) + 4, getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 2.5 + Utils.random(1.0), false, true));
		target.setNextGraphics(new Graphics(targetGFX, projectile.getEndTime(), 0, 0, true));
		target.setNextGraphics(new Graphics(targetGFX + 1, 200, 0, 0, true));
		target.getEffectsManager().startEffect(new Effect(EffectType.INCENDIARY_SHOT, 5));
		player.getEffectsManager().startEffect(new Effect(EffectType.ADRENALINE_GAIN_FOR_CRIT, 50, 10));
		break;
	    case 11://UNLOAD
		player.getEffectsManager().startEffect(new Effect(EffectType.UNLOAD, 10, projectileId, 0.0, usingChin));
		break;
	    case 12://DEADSHOT
		player.setNextGraphics(new Graphics(3519));
		targets = usingChin ? getMultiAttackTargets(player) : new Entity[] { target };
		for (Entity t : targets) {
		    projectile = World.sendProjectileNew(t == target ? player : target, t, projectileId, 41, 39, 70, 5, 0, 5);
		    Hit hit = getHit(player, t, true, Combat.RANGE_TYPE, 1.0, 1.88, false, true);
		    delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) + 1, hit);
		    if (hit.getDamage() > 0) {
			int minDamage = getMaxHit(player, t, true, Combat.RANGE_TYPE, 1.0, true);
			int maxDamage = getMaxHit(player, t, true, Combat.RANGE_TYPE, 3.13, true);
			final int constantDmg = Utils.random(minDamage, maxDamage) / 5;
			WorldTasksManager.schedule(new WorldTask() {

			    @Override
			    public void run() {
				t.getEffectsManager().startEffect(new Effect(EffectType.DEADSHOT, 10, HitLook.RANGE_DAMAGE, new Graphics(3527), constantDmg, 2, player));
			    }
			}, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1);
		    }
		}
		break;
	    case 14://DEATH'S SWIFTNESS
		player.getEffectsManager().startEffect(new Effect(EffectType.DEATHS_SWIFTNESS, 50, new WorldTile(player)));
		return -1;
	    }
	    break;
	case ActionBar.MELEE_ABILITY_SHORTCUT:
	    switch (abilityId) {
	    case 1://SLICE
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.10, false, true));
		break;
	    case 6://BACKHAND
	    case 16://FORCEFUL BACKAHND
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, abilityId == 16 ? 2.0 : 1.0, false, true));
		target.setBoundDelay(5);
		target.setNextGraphics(new Graphics(3488, 0, 92));
		break;
	    case 4://HAVOC
	    case 5://SMASH
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.25, false, true));
		target.getEffectsManager().startEffect(new Effect(EffectType.PROTECTION_DISABLED, 8));// 5 Seconds
		break;
	    case 3://SEVER
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.88, false, true));
		target.getEffectsManager().startEffect(new Effect(EffectType.MODIFY_DAMAGE, 8, -0.10));
		target.getEffectsManager().startEffect(new Effect(EffectType.SEVER, 8));
		break;
	    case 7://SLAUGHTER
		if (Combat.getHitChance(player, target, player.getCombatDefinitions().getStyle(false), true) < Utils.random(100)) {
		    delayHit(target, 0, new Hit(player, 0, HitLook.MISSED));
		    break;
		}
		WorldTasksManager.schedule(new WorldTask() {

		    @Override
		    public void run() {
			if (player.isStunned())//One of the logistics to avoid it is to insta stun.
			    return;
			int minumumDamage = getMaxHit(player, target, true, Combat.MELEE_TYPE, 1.0, true), maximumDamage = getMaxHit(player, target, true, Combat.MELEE_TYPE, 2.50, true), damage = Utils.random(minumumDamage, maximumDamage) / 5;
			target.getEffectsManager().startEffect(new Effect(EffectType.SLAUGHTER, 10, HitLook.MELEE_DAMAGE, new Graphics(3464), damage, 2, player, new WorldTile(target)));
		    }
		});
		break;
	    case 8://FLURRY
		player.getEffectsManager().startEffect(new Effect(EffectType.FLURRY, 10, true));
		break;
	    case 10://OVERPOWER
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 2.0 + Utils.random(2.0), false, true));
		break;
	    case 12://METEOR STRIKE
		int angle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (target.getX() * 2 + target.getSize()), (player.getY() * 2 + player.getSize()) - (target.getY() * 2 + target.getSize()))) / 45d) & 0x7;
		World.sendGraphics(player, new Graphics(3583, 0, 0, angle, true), new WorldTile(player));
		player.getEffectsManager().startEffect(new Effect(EffectType.ADRENALINE_GAIN_FOR_CRIT, 50, 10));
		delayHit(target, 2, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 2.5 + Utils.random(1.0), false, true));
		break;
	    case 9://HURRICANE
		for (Entity t : getMultiAttackTargets(player, target, 1, 9, true))
		    delayHit(t, 0, getHit(player, t, true, Combat.MELEE_TYPE, 1.0, 2.19, false, true));
		break;
	    case 11://MASSACRE
		WorldTasksManager.schedule(new WorldTask() {

		    @Override
		    public void run() {
			int minumumDamage = getMaxHit(player, target, true, Combat.MELEE_TYPE, 1.0, true), maximumDamage = getMaxHit(player, target, true, Combat.MELEE_TYPE, 3.13, true), damage = Utils.random(minumumDamage, maximumDamage) / 5;
			target.getEffectsManager().startEffect(new Effect(EffectType.MASSACRE, 10, HitLook.MELEE_DAMAGE, null, damage, 2, player));
		    }
		});
		delayHit(target, 1, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.88, false, true));
		break;
	    case 14://BALANCED_STRIKE
		double playerPercent = player.getHitpoints() / (double) player.getMaxHitpoints(),
		targetPercent = target.getHitpoints() / (double) target.getMaxHitpoints();
		int damage = 0;
		boolean isTargetHealing = playerPercent > targetPercent;
		if (playerPercent != targetPercent) {
		    damage = (int) (isTargetHealing ? ((targetPercent / playerPercent) * player.getHitpoints()) : ((playerPercent / targetPercent) * target.getHitpoints()));
		    if (damage > 12000)//Damage cap on RS :D
			damage = 12000; //TODO 12000 if blood tendrils only 10000 otherwise.
		    if (damage > 0) {
			if (isTargetHealing)
			    target.heal(damage);
			else
			    player.heal(damage / 2);
		    }
		}
		Hit hit = new Hit(player, damage, HitLook.MELEE_DAMAGE);
		hit.setAbilityMark();
		delayHit(isTargetHealing ? player : target, 1, hit);
		break;
	    }
	    break;
	case ActionBar.STRENGTH_ABILITY_SHORTCUT:
	    switch (abilityId) {
	    case 2://KICK
	    case 13://STOMP
	    case 200://BARGE KICK EXCEPTION
		int angle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (target.getX() * 2 + target.getSize()), (player.getY() * 2 + player.getSize()) - (target.getY() * 2 + target.getSize()))) / 45d) & 0x7;
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.0, abilityId == 13 ? 2.0 : 1.0, false, true);
		delayHit(target, 0, hit);
		if (hit.getDamage() > 0 && abilityId != 200) {
		    if (!target.isStunImmune() && !(target instanceof NPC && ((NPC) target).isCantFollowUnderCombat()))
			if (!target.addWalkSteps(target.getX() - player.getX() + target.getX(), target.getY() - player.getY() + target.getY(), 1))
			    target.setDirection(angle);
		    target.setStunDelay(5);//Three seconds
		}
		break;
	    case 3://PUNISH
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, target.isStunned() ? 1.25 : 0.94, false, true));
		break;
	    case 5://DECIMATE
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, target instanceof Player && ((Player) target).getEquipment().hasShield() ? 2.44 : 1.88, false, true));
		break;
	    case 1://DISMEMBER
		WorldTasksManager.schedule(new WorldTask() {

		    @Override
		    public void run() {
			int minumumDamage = getMaxHit(player, target, true, Combat.MELEE_TYPE, 1.0, true), maximumDamage = getMaxHit(player, target, true, Combat.MELEE_TYPE, 1.88, true), damage = Utils.random(minumumDamage, maximumDamage) / 5;
			target.getEffectsManager().startEffect(new Effect(EffectType.DISMEMBER, 10, HitLook.MELEE_DAMAGE, new Graphics(3465, 0, 94), damage, 2, player));
		    }
		});
		break;
	    case 4://FURY
		player.getEffectsManager().startEffect(new Effect(EffectType.FURY, 8, 0.0));
		break;
	    case 6://CLEAVE
		angle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (target.getX() * 2 + target.getSize()), (player.getY() * 2 + player.getSize()) - (target.getY() * 2 + target.getSize()))) / 45d) & 0x7;
		for (Entity t : getMultiAttackTargets(player, target, 1, 9, true)) {
		    int nextAngle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (t.getX() * 2 + t.getSize()), (player.getY() * 2 + player.getSize()) - (t.getY() * 2 + t.getSize()))) / 45d) & 0x7;
		    if (nextAngle != angle)
			continue;
		    delayHit(t, 0, getHit(player, t, true, Combat.MELEE_TYPE, 1.0, 1.88, false, true));
		}
		break;
	    case 7://ASSAULT
		player.getEffectsManager().startEffect(new Effect(EffectType.ASSAULT, 10, 0.0));
		break;
	    case 8://QUAKE
		for (Entity t : getMultiAttackTargets(player, target, 1, 9, true)) {
		    hit = getHit(player, t, true, Combat.MELEE_TYPE, 1.0, 1.88, false, true);
		    //TODO camera shake
		    if (t instanceof Player)
			((Player) t).getSkills().drainLevel(Skills.DEFENCE, hit.getDamage() / 200);
		    delayHit(t, 2, hit);
		}
		break;
	    case 9://DESTROY
		player.getEffectsManager().startEffect(new Effect(EffectType.DESTROY, 10, attackAnim, startGfx));
		target.setBoundDelay(10);
		break;
	    case 12://PULVERISE
		target.getEffectsManager().startEffect(new Effect(EffectType.PULVERISE, 50));
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 2.5 + Utils.random(1.0), false, true));
		if (target instanceof NPC) {
		    WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
			    if (target.isDead())
				addAdrenaline(player, 50);
			}
		    }, 1);
		}
		break;
	    case 11://FRENZY
		player.getEffectsManager().startEffect(new Effect(EffectType.FRENZY, 10, 0.0));
		break;
	    }
	    break;
	case ActionBar.DEFENCE_ABILITY_SHORTCUT:
	    switch (abilityId) {
	    case 3://PROVOKE
		player.setNextAnimation(new Animation(18130));
		player.getEffectsManager().startEffect(new Effect(EffectType.PROVOKE, 17, target));
		target.getEffectsManager().startEffect(new Effect(EffectType.PROVOKE, 17, player));
		if (target instanceof NPC)
		    ((NPC) target).setTarget(player);
		return -1;//Leave combat
	    case 6://BASH
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.0, false, true);
		if (hit.getDamage() > 0) {
		    int damage = hit.getDamage() + player.getEquipment().getItem(Equipment.SLOT_SHIELD).getDefinitions().getArmor();
		    hit.setDamage(Utils.random(hit.getDamage(), damage));//Best way I could think of doing it.
		}
		delayHit(target, 0, hit);
		break;
	    case 8://DEBILITATE
		hit = getHit(player, target, true, player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON), 1.0, 1.0, false, true);
		if (hit.getDamage() > 0) {
		    int tier = player.getEquipment().hasShield() ? player.getEquipment().getItem(Equipment.SLOT_SHIELD).getDefinitions().getCSOpcode(750) : 0;
		    target.getEffectsManager().startEffect(new Effect(EffectType.DEBILITATE, tier <= 10 ? 8 : tier <= 30 ? 12 : tier <= 69 ? 15 : tier <= 99 ? 16 : 8));
		}
		delayHit(target, 0, hit);
		break;
	    case 13://NATURAL INSTINCT 
		player.getEffectsManager().startEffect(new Effect(EffectType.NATURAL_INSTINCT, 33, target));
		return -1;
	    }
	    break;
	case ActionBar.HEAL_ABILITY_SHORTCUT:
	    switch (abilityId) {
	    case 10://SACRIFICE
		Hit hit = getHit(player, target, true, player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON), 1.0, 1.0, false, true);
		delayHit(0, hit);
		final int remainingHP = target.getHitpoints(); // hit damage could be > than remaining HP
		WorldTasksManager.schedule(new WorldTask() {

		    @Override
		    public void run() {
			player.heal((int) (target.isDead() ? remainingHP : hit.getDamage() * 0.25), 0, 0, true);
		    }
		}, 1);
		target.setNextGraphics(new Graphics(4502, 0, 130));
		break;
	    case 15://SIPHON
		if (!target.getEffectsManager().hasActiveEffect(EffectType.SIPHON_IMMUNITY)) {
		    Player playerTarget = (Player) target;
		    int adrenaline = (int) (playerTarget.getCombatDefinitions().getSpecialAttackPercentage() * 0.10);
		    if (adrenaline > 0) {
			playerTarget.getCombatDefinitions().desecreaseSpecialAttack(adrenaline);
			player.getCombatDefinitions().increaseSpecialAttack(adrenaline);
		    }
		}
		player.getEffectsManager().startEffect(new Effect(EffectType.SIPHON_IMMUNITY, 10));
		break;
	    }
	    break;
	}

	nextAbility = null;
	setWeaponAbilityDelay(player);
	return 0;
    }

    public int handleSpecialAttack(final Player player) {
	Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);

	if (weapon == null || !weapon.getDefinitions().hasSpecialAttack()) {
	    player.getPackets().sendGameMessage("You can only do that with a weapon that can perform a special attack.");
	    player.getCombatDefinitions().desecreaseSpecialAttack(0); //turns spec off if somehow this happened
	    return 0;
	}

	int specAmt = weapon.getDefinitions().getSpecialAmmount();
	if (player.getCombatDefinitions().hasRingOfVigour())
	    specAmt *= 0.9;//This special attack requires 60% adrenaline before it can be used.

	int combatMode = player.getCombatDefinitions().getCombatMode();

	if (player.getCombatDefinitions().getSpecialAttackPercentage() < specAmt) {
	    player.getPackets().sendGameMessage("This special attack requires " + specAmt + "% " + (combatMode == CombatDefinitions.LEGACY_COMBAT_MODE ? "special attack energy" : "adrenaline") + " before it can be used.");
	    player.getCombatDefinitions().desecreaseSpecialAttack(0); //turns spec off
	    return 0;
	}

	final SpecialAttack spec = SpecialAttack.getSpecialAttack(weapon.getId());

	if (spec == SpecialAttack.DRAGON_CLAWS) { //dragon claw exeption, no other specs have exeption
	    Item offhand = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
	    if (offhand == null || offhand.getId() != 25555 && offhand.getId() != 25557 && offhand.getId() != 25952) {
		player.getPackets().sendEntityMessage(1, 0xFFFFFF, player, "Off-hand dragon claw must be equipped to use this special attack.");
		player.getCombatDefinitions().desecreaseSpecialAttack(0); //turns spec off
		return 0;
	    }
	}
	else if (spec == SpecialAttack.RUNE_CLAWS) {
	    Item offhand = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
	    if (offhand == null || offhand.getId() != 25945 && offhand.getId() != 25947) {
		player.getPackets().sendEntityMessage(1, 0xFFFFFF, player, "Off-hand rune claw must be equipped to use this special attack.");
		player.getCombatDefinitions().desecreaseSpecialAttack(0); //turns spec off
		return 0;
	    }
	}
	player.getCombatDefinitions().desecreaseSpecialAttack(specAmt);
	//	addAdrenaline(player, -specAmt);
	if (spec != null) {
	    AnimationDefinitions defs = spec.attackAnim.getDefinitions();
	    player.setNextAnimation(spec.attackAnim);
	    Integer startGfx = defs.clientScriptData == null ? null : (Integer) defs.clientScriptData.get(2920);
	    if (startGfx != null) {
		Integer startGfxHeight = (Integer) defs.clientScriptData.get(4339/*4090*/);
		player.setNextGraphics(new Graphics(startGfx, 0, startGfxHeight == null ? 0 : startGfxHeight));
	    }
	    if (spec.effect != null) {
		Integer targetGfx = defs.clientScriptData == null ? null : (Integer) defs.clientScriptData.get(2933);
		Entity[] targets = !spec.multi ? new Entity[] { target } : getMultiAttackTargets(player);
		for (final Entity target : targets) {
		    int time = spec.effect.executeAttack(player, target, target == PlayerCombatNew.this.target, spec);
		    if (targetGfx != null)
			target.setNextGraphics(new Graphics(targetGfx, time, 93));
		}
	    }

	}
	setWeaponDelay(player, true);
	setWeaponDelay(player, false);
	return 0;
    }

    private static enum SpecialAttack {

	DRAGON_DAGGER(new int[] { 1215, 13465 }, 23954, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.15, 1.15, true));
		delayHit(target, 1, getHit(player, target, true, Combat.MELEE_TYPE, 1.15, 1.15, true));
		return 0;
	    }

	}, false)

	, DRAGON_SCIMITAR(new int[] { 4587, 13477 }, 23935, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.15, 1.0, true);
		delayHit(target, 0, hit);
		if (target instanceof Player && hit.getDamage() > 0)
		    player.getEffectsManager().startEffect(new Effect(EffectType.PROTECTION_DISABLED, 8));
		return 0;
	    }

	}, false)

	, DRAGON_LONGSWORD(new int[] { 1305 }, 23929, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, Utils.random(1.0, 2.5), true));
		return 0;
	    }

	}, false)

	, DRAGON_MACE(new int[] { 1434 }, 23952, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 0.95, Utils.random(1.0, 3.0), true));
		return 0;
	    }

	}, false)

	, GRANITE_MAUL(new int[] { 4153, 13445 }, 1667, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.25, true));
		return 0;
	    }

	}, false)

	, DRAGON_CLAWS(new int[] { 14484, 14486, 23695 }, 24010, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit[] hits = new Hit[4];
		for (int i = 0; i < hits.length; i++)
		    hits[i] = i == 0 || hits[i - 1].getDamage() == 0 ? getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.0, true) : new Hit(player, hits[i - 1].getDamage() / 2 + (i == 3 ? 1 : 0), HitLook.MELEE_DAMAGE);
		    delayHit(target, 0, hits[0], hits[1]);
		    delayHit(target, 1, hits[2], hits[3]);
		    return 0;
	    }
	}, false)

	, DRAGON_HALBERD(new int[] { 3204, 13478 }, 23962, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Entity t = player.getCurrentFaceEntity();
		if (t == null)
		    return 0;
		boolean inPath = mainTarget;
		if (!inPath) {
		    byte[] dirs = Utils.getDirection(Utils.getAngle(t.getX() - player.getX(), t.getY() - player.getY()));
		    for (int distance = -1; distance < 2; distance++) {
			WorldTile tile = new WorldTile(new WorldTile(player.getX() + (dirs[1] * distance) + dirs[0], player.getY() + (dirs[0] * distance) + dirs[1], player.getPlane()));
			if (target.getX() == tile.getX() && target.getY() == tile.getY() && tile.getPlane() == target.getPlane()) {
			    inPath = true;
			    break;
			}
		    }
		}
		if (inPath)
		    delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.0, true));
		return 0;
	    }
	}, true)

	, DRAGON_PICKAXE(new int[] { 15259 }, 23963, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.0, true);
		delayHit(target, 0, hit);
		if (hit.getDamage() > 0) {
		    if (target instanceof Player) {
			Player p2 = (Player) target;
			p2.getSkills().drainLevel(Skills.ATTACK, (int) (p2.getSkills().getLevel(Skills.ATTACK) * .05));
			p2.getSkills().drainLevel(Skills.RANGE, (int) (p2.getSkills().getLevel(Skills.RANGE) * .05));
			p2.getSkills().drainLevel(Skills.MAGIC, (int) (p2.getSkills().getLevel(Skills.MAGIC) * .05));
		    }
		}
		return 0;
	    }
	}, true)

	, DRAGON_HATCHET(new int[] { 6739, 13470 }, 23965, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.0, true);
		delayHit(target, 0, hit);
		if (hit.getDamage() > 0) {
		    if (target instanceof Player) {
			Player p2 = (Player) target;
			p2.getSkills().drainLevel(Skills.ATTACK, (int) (p2.getSkills().getLevel(Skills.ATTACK) * .1));
			p2.getSkills().drainLevel(Skills.DEFENCE, (int) (p2.getSkills().getLevel(Skills.DEFENCE) * .1));
		    }
		}
		return 0;
	    }
	}, true)

	, DRAGON_2H(new int[] { 7158, 13430 }, 7078, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.0, true));
		return 0;
	    }
	}, true)

	, RUNE_CLAWS(new int[] { 3101, 13764 }, 23956, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.15, 1.20, true));
		return 0;
	    }
	}, false)

	, BRACKISH_BLADE(new int[] { 20671, 20673 }, 24004, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 2.0, 1.0, true);
		delayHit(target, 0, hit);
		int damage = hit.getDamage();
		if (damage > 0) {
		    int boost = damage / 57;
		    if (player.getSkills().getLevel(Skills.ATTACK) <= player.getSkills().getLevelForXp(Skills.ATTACK))
			player.getSkills().set(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) + boost);
		    if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getLevelForXp(Skills.STRENGTH))
			player.getSkills().set(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + boost);
		    if (player.getSkills().getLevel(Skills.DEFENCE) <= player.getSkills().getLevelForXp(Skills.DEFENCE))
			player.getSkills().set(Skills.DEFENCE, player.getSkills().getLevel(Skills.DEFENCE) + boost);
		}
		return 0;
	    }
	}, false)

	, BONE_DAGGER(new int[] { 8872, 8874, 8876, 8878 }, 24008, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, Utils.random(0.5, 2.0), true));
		return 0;
	    }
	}, false)

	, DARKLIGHT(new int[] { 6746, 8281 }, 24005, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.0, true);
		delayHit(target, 0, hit);
		if (hit.getDamage() > 0) {
		    if (target instanceof Player) {
			Player p2 = (Player) target;
			p2.getSkills().drainLevel(Skills.STRENGTH, (int) (p2.getSkills().getLevel(Skills.STRENGTH) * .05));
			p2.getSkills().drainLevel(Skills.DEFENCE, (int) (p2.getSkills().getLevel(Skills.DEFENCE) * .05));
		    }
		}
		return 0;
	    }
	}, false)

	, ANCIENT_MACE(new int[] { 11061, 22406 }, 24003, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.ALL_TYPE, 1.10, 1.0, true);
		if (hit.getDamage() > 0) {
		    if (target instanceof Player) {
			Player p2 = (Player) target;
			int targetPrayerPoints = p2.getPrayer().getPrayerpoints();
			int drainedPoints = (int) (hit.getDamage() * .1);
			if (targetPrayerPoints > 0) {
			    if (drainedPoints > targetPrayerPoints)
				drainedPoints = targetPrayerPoints;
			    p2.getPrayer().drainPrayer(drainedPoints);
			    player.getPrayer().restorePrayer(drainedPoints);
			}
		    }
		}
		delayHit(target, 0, hit);
		return 0;
	    }
	}, false)

	, BARRELCHEST_ANCHOR(new int[] { 10887 }, 24005, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.25, Utils.random(0.75, 1.5), true);
		delayHit(target, 0, hit);
		int damage = hit.getDamage();
		if (damage > 0) {
		    if (target instanceof Player) {
			Player p2 = (Player) target;
			int skill = Combat.COMBAT_SKILLS[Utils.random(Combat.COMBAT_SKILLS.length)];
			if (skill == Skills.STRENGTH)//Can't drain strength.
			    skill = Skills.ATTACK;
			p2.getSkills().drainLevel(skill, (int) (damage * .1));
		    }
		}
		return 0;
	    }
	}, false)

	, ABYSSAL_VINE_WHIP(new int[] { 21371, 21372, 21373, 21374, 21375 }, 23930, new SpecialAttackEffect() {

	    private final Graphics VINE = new Graphics(478);

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.10, 1.0, true));
		final WorldTile vineTile = new WorldTile(target);
		WorldTasksManager.schedule(new WorldTask() {

		    private int cycles;

		    @Override
		    public void run() {
			cycles++;
			if (cycles == 10) {
			    stop();
			    return;
			}
			World.sendGraphics(player, VINE, vineTile);
			for (Entity t : getMultiAttackTargets(player, target, 1, 3))
			    if (t.getX() == vineTile.getX() && t.getY() == vineTile.getY() && t.getPlane() == vineTile.getPlane())
				delayHit(t, 0, getHit(player, t, true, Combat.MELEE_TYPE, 1.25, 0.33, true));
		    }
		}, 0, 3);
		return 0;
	    }

	}, false)

	, ABYSSAL_WHIP(new int[] { 4151, 13444, 14661, 15441, 15442, 15443, 15444, 23691 }, 23928, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.10, 1.0, true);
		delayHit(target, 0, hit);
		if (hit.getDamage() > 0 && target instanceof Player) {
		    Player p2 = (Player) target;
		    player.setRunEnergy(player.getRunEnergy() + p2.getRunEnergy());
		    p2.setRunEnergy(0);
		}
		return 0;
	    }

	}, false)

	, KORASI(new int[] { 19784 }, 24007, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 1.5, true));
		target.setNextGraphics(new Graphics(2795, 20, 0));
		return 0;
	    }

	}, true)

	, VESTA_LONGSWORD(new int[] { 13899, 13901 }, 23998, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.35, Utils.random(1.0, 2.5), true));
		return 0;
	    }

	}, false)

	, VESTA_SPEAR(new int[] { 13905, 13907 }, 23997, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Entity t = player.getCurrentFaceEntity();
		if (t == null)
		    return 0;
		boolean inPath = mainTarget;

		byte[] dirs = Utils.getDirection(Utils.getAngle(t.getX() - player.getX(), t.getY() - player.getY()));
		for (int distance = -1; distance < 2; distance++) {
		    if (distance == 0)
			continue;
		    WorldTile tile = new WorldTile(new WorldTile(player.getX() + (dirs[1] * distance) + dirs[0], player.getY() + (dirs[0] * distance), player.getPlane()));
		    if (target.getX() == tile.getX() && target.getY() == tile.getY() && tile.getPlane() == target.getPlane()) {
			inPath = true;
			break;
		    }
		}
		if (inPath)
		    delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.0, true));
		player.getEffectsManager().startEffect(new Effect(EffectType.VESTA_IMMUNITY, 17));
		return 0;
	    }

	}, true)

	, STATIUS_WARHAMMER(new int[] { 13902, 13904 }, 24000, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.00, 1.5, true);
		delayHit(target, 0, hit);
		if (hit.getDamage() > 0) {
		    if (target instanceof Player) {
			Player p2 = (Player) target;
			p2.getSkills().set(Skills.DEFENCE, (int) (player.getSkills().getLevelForXp(Skills.DEFENCE) * .7));
		    }
		}
		return 0;
	    }

	}, false)

	, ARMADYL_GODSWORD(new int[] { 11694, 13450, 23679 }, 23932, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.10, 1.5, true));
		return 0;
	    }

	}, false)

	, BANDOS_GODSWORD(new int[] { 11696, 13451, 23680, 31526, 31524 }, 23934, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.1, true);
		delayHit(target, 0, hit);
		if (hit.getDamage() > 0 && target instanceof Player)
		    ((Player) target).getSkills().drainLevel(Combat.COMBAT_SKILLS[Utils.random(Combat.COMBAT_SKILLS.length)], (int) (hit.getDamage() * 0.1));
		return 0;
	    }

	}, false)

	, SARADOMIN_GODSWORD(new int[] { 11698, 13452, 23681 }, 23933, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1, true);
		delayHit(target, 0, hit);
		int damage = hit.getDamage();
		if (damage > 0) {
		    player.getPrayer().restorePrayer((int) (damage * .25));
		    player.heal((int) (damage * .5));
		}
		return 0;
	    }

	}, false)

	, ZAMORAK_GODSWORD(new int[] { 11700, 13453, 23682 }, 23912, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.75, true);
		delayHit(target, 0, hit);
		int damage = hit.getDamage();
		if (damage > 0) {
		    EffectsManager.addBoundEffect(target, 16, true, 3);
		    target.setNextGraphics(new Graphics(2104, 30, 0));
		}
		return 0;
	    }

	}, false)

	, SARADOMIN_SWORD(new int[] { 11730, 13461, 23690 }, 23937, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.10, true));
		delayHit(target, 0, getHit(player, target, true, Combat.MAGIC_TYPE, 1.25, 1.00, true));
		return 0;
	    }

	}, false)

	, KEENBLADE(new int[] { 23042 }, 23913, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		delayHit(target, 0, getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, Utils.random(1.0, 1.5), true));
		return 0;
	    }

	}, false)

	, DARK_BOW(new int[] { 11235, 13405, 15701, 15702, 15703, 15704 }, 23941, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		ItemDefinitions ammoDefs = player.getEquipment().getItem(Equipment.SLOT_ARROWS).getDefinitions();
		boolean bestArrows = ammoDefs.getName().contains("Dragon") || ammoDefs.getName().contains("Dark");
		Projectile projectile = World.sendProjectileNew(player, target, bestArrows ? 1099 : 1101, 41, 37, 55, 7, Utils.random(5), 15);
		int hitDelay = Utils.projectileTimeToCycles(projectile.getEndTime());
		World.sendProjectileNew(player, target, bestArrows ? 1099 : 1101, 41, 40, 55, 5, Utils.random(5), 15);
		target.setNextGraphics(new Graphics(bestArrows ? 1100 : 1103, projectile.getEndTime(), projectile.getEndHeight()));
		delayHit(target, hitDelay - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.00, 1.5, true));
		delayHit(target, hitDelay - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.00, 1.5, true));
		return projectile.getEndTime();
	    }
	}, false)

	, SEERCUL(new int[] { 6724, 13529 }, 23943, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 3490, 41, 37, 55, 5, Utils.random(5), 15);
		Hit hit = getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.0, true);
		int damage = hit.getDamage();
		if (damage > 0 && target instanceof Player)
		    ((Player) target).getSkills().drainLevel(Skills.MAGIC, damage / 30);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
		return projectile.getEndTime();
	    }

	}, false)

	, MAGIC_SHORTBOW(new int[] { 861, 13528 }, 23967, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 249, 41, 38, 35, 5, Utils.random(5), 15);
		delayHit(target, Utils.projectileTimeToCycles(World.sendProjectileNew(player, target, 249, 41, 37, 15, 5, Utils.random(5), 15).getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 0.85, 1.25, true));
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 0.85, 1.25, true));
		return 0;
	    }

	}, false)

	, MAGIC_COMPOSITE_BOW(new int[] { 10284, 13543 }, 23942, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 249, 41, 39, 50, 5, Utils.random(5), 15);

		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()), getHit(player, target, true, Combat.RANGE_TYPE, 1.25, 1.25, true));
		return projectile.getEndTime();
	    }

	}, false)

	, MORRIGAN_JAVELIN(new int[] { 13879 }, 23919, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.0, true);
		final int dmg = hit.getDamage();
		int hitDelay = Utils.projectileTimeToCycles(World.sendProjectileNew(player, target, 3491, 52, 40, 36, 5, Utils.random(5), 137).getEndTime());
		if (dmg >= 4)
		    WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
			    EffectsManager.startBleedEffect(target, 16, 4, (int) (dmg * 0.25));
			}

		    }, hitDelay);
		delayHit(target, hitDelay - 1, hit);
		return hitDelay;
	    }

	}, false)

	, MORRIGAN_AXE(new int[] { 13883 }, 23999, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 1839, 41, 40, 41, 5, Utils.random(5), 4);
		Hit hit = getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.20, true);
		if (hit.getDamage() > 0 && target instanceof Player)
		    ((Player) target).getEffectsManager().startEffect(new Effect(EffectType.MORRIGAN_AXE, 100));
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()), hit);
		return projectile.getEndTime();
	    }

	}, false)

	, ZANIKS_CROSSBOW(new int[] { 14684, 14686 }, 24012, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 2001, 41, 40, 80, 5, Utils.random(5), 0);

		boolean godAffinity = target instanceof NexMinion || target instanceof GodWarMinion || target instanceof Nex || target instanceof ZarosMinion || target instanceof GeneralGraardor || target instanceof CommanderZilyana || target instanceof KreeArra || target instanceof KrilTstsaroth || target instanceof GodwarsArmadylFaction || target instanceof GodwarsBandosFaction || target instanceof GodwarsSaradominFaction || target instanceof GodwarsBandosFaction;
		addCombatDelay(player, 7);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.0, godAffinity ? 1.5 : 1.0, true));
		return projectile.getEndTime();
	    }

	}, false)

	, HAND_CANNON(new int[] { 15241 }, 12175, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		WorldTasksManager.schedule(new WorldTask() {

		    @Override
		    public void run() {
			Projectile projectile = World.sendProjectileNew(player, target, 2143, 24, 40, 0, 5, Utils.random(5), 4);
			addCombatDelay(player, 7);
			player.setNextAnimation(new Animation(24011));
			delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.45, 1.50, true));
		    }
		}, 3);
		return 0;
	    }

	}, false)

	, RUNE_THROWING_AXE(new int[] { 805 }, 23958, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		List<Entity> targets = new ArrayList<Entity>(3);
		targets.addAll(Arrays.asList(getMultiAttackTargets(player, target, 3, 3)));
		if (targets.size() == 2)
		    targets.add(target);

		int startDelay = 19;
		Entity next = player;
		for (int idx = 0; idx < targets.size(); idx++) {
		    Entity t = targets.get(idx);
		    if (t.isDead())
			continue;
		    Projectile projectile = World.sendProjectileNew(next, t, 258, idx == 0 ? 41 : 25, idx == 0 ? 41 : 25, startDelay, 2, Utils.random(5), idx == 0 ? 10 : 5);
		    startDelay += projectile.getEndTime() - (idx * 35);
		    delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.0, true));
		    next = t;
		}
		return 0;
	    }

	}, false)

	, QUICKBOW(new int[] { 23043 }, 23968, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 2682, 19, 40, 15, 5, Utils.random(5), 4);
		World.sendProjectileNew(player, target, 2682, 19, 40, 40, 5, Utils.random(5), 4);
		Hit hit = getHit(player, target, true, Combat.RANGE_TYPE, 1.25, Utils.random(0.5, 0.75), true);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()), hit, hit);
		return projectile.getEndTime();
	    }

	}, false)

	, SARADOMIN_BOW(new int[] { 19143, 19145 }, 23943, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 3490, 41, 40, 55, 5, Utils.random(5), 5);
		Hit hit = getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.0, true);
		int damage = hit.getDamage();
		if (damage > 10)
		    EffectsManager.startHealEffect(player, damage / 5);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
		return projectile.getEndTime();
	    }

	}, false)

	, ZAMORAK_BOW(new int[] { 19149, 19151 }, 23943, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 3490, 41, 40, 55, 5, Utils.random(5), 5);
		Hit hit = getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.0, true);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
		delayHit(target, Utils.projectileTimeToCycles(World.sendProjectileNew(player, target, 3490, 41, 40, 35, 5, Utils.random(5), 5).getEndTime()) - 1, getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.0, true));
		return projectile.getEndTime();
	    }
	}, false)

	, GUTHIX_BOW(new int[] { 19146, 19148 }, 23943, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 3490, 41, 40, 55, 5, Utils.random(5), 5);
		Hit hit = getHit(player, target, true, Combat.RANGE_TYPE, 1.0, 1.35, true);
		int damage = hit.getDamage();
		if (damage > 10)
		    EffectsManager.startHealEffect(player, damage / 10);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
		return projectile.getEndTime();
	    }

	}, false)

	, ARMADYL_BATTLESTAFF(new int[] { 21777, 21863 }, -1, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		GeneralRequirementMap spellData = Magic.getSpellData(player.getCombatDefinitions().getSpellId());
		if (spellData == null)
		    return 0;
		final int targetGfx = spellData.getIntValue(2933), spellBook = Magic.getSpellBook(spellData);
		addCombatDelay(player, 10);
		WorldTasksManager.schedule(new WorldTask() {

		    int cycle;

		    @Override
		    public void run() {
			player.setNextAnimation(new Animation(10546));
			cycle++;
			if (target.isDead() || cycle == 5) {
			    stop();
			    return;
			}
			Projectile projectile = World.sendProjectileNew(player, target, 1019, 22, 22, 60, 4, Utils.random(5), 5);
			Hit hit = getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 0.6, true);
			delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
			if (targetGfx != 0)
			    target.setNextGraphics(new Graphics(targetGfx, projectile.getEndTime(), spellBook == 1 ? 96 : 0));

		    }
		}, 0, 0);
		return 0;
	    }
	}, false)

	, ZURIELS_STAFF(new int[] { 13867, 13869 }, 24001, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 1.35, true);
		if (hit.getDamage() > 0) {
		    target.getEffectsManager().startEffect(new Effect(EffectType.ADRENALINE_GAIN_DECREASE, 17));
		    CONFUSE_EFFECT.execute(player, target, hit, mainTarget, null);
		}
		delayHit(target, Utils.projectileTimeToCycles(60), hit);
		return 60;
	    }
	}, false)

	, SARADOMIN_STAFF(new int[] { 2417 }, 23925, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 1.35, true);
		if (hit.getDamage() > 0 && target instanceof Player) {
		    Player p2 = (Player) target;
		    p2.getSkills().drainLevel(Skills.MAGIC, (int) (p2.getSkills().getLevel(Skills.MAGIC) * 0.05));
		}
		delayHit(target, Utils.projectileTimeToCycles(60), hit);
		return 60;
	    }
	}, false)

	, GUTHIX_STAFF(new int[] { 2416 }, 23923, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 1.35, true);
		if (hit.getDamage() > 0 && target instanceof Player) {
		    Player p2 = (Player) target;
		    p2.getSkills().drainLevel(Skills.DEFENCE, (int) (p2.getSkills().getLevel(Skills.DEFENCE) * 0.05));
		}
		delayHit(target, Utils.projectileTimeToCycles(60), hit);
		return 60;
	    }
	}, false)

	, ZAMORAK_STAFF(new int[] { 2415 }, 23921, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		Hit hit = getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 1.35, true);
		if (hit.getDamage() > 0 && target instanceof Player) {
		    Player p2 = (Player) target;
		    p2.getSkills().drainLevel(Skills.MAGIC, (int) (p2.getSkills().getLevel(Skills.MAGIC) * 0.05));
		}
		delayHit(target, Utils.projectileTimeToCycles(60), hit);
		return 60;
	    }
	}, false)

	, IBAN_STAFF(new int[] { 1409 }, 23996, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 88, 22, 22, 15, 7, Utils.random(5), 90);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()), getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, Utils.random(1.0, 1.50), true));
		return projectile.getEndTime();
	    }
	}, false)

	, PENANCE_TRIDENT(new int[] { 1409 }, 23996, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		player.getEffectsManager().startEffect(new Effect(EffectType.INCREASE_CRIT_CHANCE, 3, 10));//10% crit chance
		delayHit(target, Utils.projectileTimeToCycles(60), getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, Utils.random(.25, 1.55), true));
		return 60;
	    }
	}, false)

	, MINDSPIKE(new int[] { 23044, 23045, 23046, 23047 }, 14223, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		Projectile projectile = World.sendProjectileNew(player, target, 2731, 30, 30, 55, 5, Utils.random(5), 90);
		delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()), getHit(player, target, true, Combat.MAGIC_TYPE, 1.25, Utils.random(1.0, 1.40), true));
		return projectile.getEndTime();
	    }
	}, false)

	, NOXIOUS_WEAPONS(new int[] { 31725, 31727, 31729, 31731, 31733, 31735, 33330, 33331, 33333, 33334, 33336, 33337, 33396, 33397, 33399, 33400, 33402, 33403, 33462, 33463, 33465, 33466, 33468, 33469 }, 24153, new SpecialAttackEffect() {

	    @Override
	    public int executeAttack(final Player player, final Entity target, boolean mainTarget, SpecialAttack attack) {
		player.setNextGraphics(new Graphics(5005));
		player.getEffectsManager().startEffect(new Effect(EffectType.MIRRORBACK_SPIDER, 17, new MirrorBackSpider(19487, Utils.getFreeTile(player, 1), -1, true)));
		return 60;
	    }
	}, false)

	;

	private int[] weaponIds;
	private Animation attackAnim;
	private SpecialAttackEffect effect;
	private boolean multi;

	private SpecialAttack(int[] weaponIds, int attackAnimId, SpecialAttackEffect effect, boolean multi) {
	    this.weaponIds = weaponIds;
	    attackAnim = new Animation(attackAnimId);
	    this.effect = effect;
	    this.multi = multi;
	}

	private static SpecialAttack getSpecialAttack(int id) {
	    for (SpecialAttack spec : SpecialAttack.values())
		for (int weaponId : spec.weaponIds)
		    if (weaponId == id)
			return spec;
	    return null;
	}
    }

    private static interface SpecialAttackEffect {

	int executeAttack(Player player, Entity target, boolean mainTarget, SpecialAttack attack);

    }

    private Hit getHit(Player player, boolean mainHand, int attackType) {
	return getHit(player, target, mainHand, attackType, 1, 1, false);
    }

    private static Hit getHit(Player player, Entity target, boolean mainHand, int attackType, double accuracyMultiplier, double damageMultiplier, boolean spec) {
	return getHit(player, target, mainHand, attackType, accuracyMultiplier, damageMultiplier, spec, false);
    }

    private static Hit getHit(Player player, Entity target, boolean mainHand, int attackType, double accuracyMultiplier, double damageMultiplier, boolean spec, boolean ability) {
	return getHit(player, target, mainHand, attackType, accuracyMultiplier, damageMultiplier, spec, ability, false);
    }

    //if crit. add 25% of max hit
    public static Hit getHit(Player player, Entity target, boolean mainHand, int attackType, double accuracyMultiplier, double damageMultiplier, boolean spec, boolean ability, boolean ignoreDefence) {
	EffectsManager effects = player.getEffectsManager();
	if (effects.hasActiveEffect(EffectType.MODIFY_ACCURACY)) {
	    accuracyMultiplier += (double) effects.getEffectForType(EffectType.MODIFY_ACCURACY).getArguments()[0];
	    effects.removeEffect(EffectType.MODIFY_ACCURACY);
	}
	if (effects.hasActiveEffect(EffectType.MODIFY_DAMAGE))
	    damageMultiplier += (double) effects.getEffectForType(EffectType.MODIFY_DAMAGE).getArguments()[0];

	if (!ignoreDefence) {
	    double hitChance = Combat.getHitChance(player, target, player.getCombatDefinitions().getStyle(!mainHand), mainHand) * accuracyMultiplier;
	    if (!Settings.HOSTED)
		System.out.println("Hit chance: " + hitChance + " %");
	    if (hitChance < Utils.random(100))
		return new Hit(player, 0, attackType == Combat.MELEE_TYPE ? HitLook.MELEE_DAMAGE : attackType == Combat.RANGE_TYPE ? HitLook.RANGE_DAMAGE : attackType == Combat.MAGIC_TYPE ? HitLook.MAGIC_DAMAGE : HitLook.REGULAR_DAMAGE);
	}
	if (attackType == Combat.MAGIC_TYPE && player.getEffectsManager().hasActiveEffect(EffectType.METAMORPHISIS))
	    damageMultiplier += 0.75;//DMG increases by 75%
	int maxHit = getMaxHit(player, target, mainHand, attackType, damageMultiplier, ability);
	Hit hit = new Hit(player, (int) (Utils.random(spec || ability ? maxHit * 0.33 : 0, maxHit) + 1), attackType == Combat.MELEE_TYPE ? HitLook.MELEE_DAMAGE : attackType == Combat.RANGE_TYPE ? HitLook.RANGE_DAMAGE : attackType == Combat.MAGIC_TYPE ? HitLook.MAGIC_DAMAGE : HitLook.REGULAR_DAMAGE);
	if (Utils.random(100) <= (effects.hasActiveEffect(EffectType.INCREASE_CRIT_CHANCE) ? (int) effects.getEffectForType(EffectType.INCREASE_CRIT_CHANCE).getArguments()[0] : 5)) { //5% chance crit
	    hit.setCriticalMark();
	    hit.setDamage((int) (hit.getDamage() + maxHit * 0.25));
	}
	else if (ability)
	    hit.setAbilityMark();
	if (attackType == Combat.MELEE_TYPE && effects.hasActiveEffect(EffectType.BERSERK))
	    hit.setDamage(hit.getDamage() * 2);
	else if (attackType == Combat.MAGIC_TYPE && effects.hasActiveEffect(EffectType.SUNSHINE) && player.withinDistance((WorldTile) effects.getEffectForType(EffectType.SUNSHINE).getArguments()[0], 3))
	    hit.setDamage((int) (hit.getDamage() * 1.5));
	else if (attackType == Combat.RANGE_TYPE && effects.hasActiveEffect(EffectType.DEATHS_SWIFTNESS) && player.withinDistance((WorldTile) effects.getEffectForType(EffectType.DEATHS_SWIFTNESS).getArguments()[0], 3))
	    hit.setDamage((int) (hit.getDamage() * 1.5));
	if (target.getEffectsManager().hasActiveEffect(EffectType.ANTICIPATION))
	    hit.setDamage((int) (hit.getDamage() * 0.90));
	if (effects.hasActiveEffect(EffectType.PROVOKE) && target != (Entity) effects.getEffectForType(EffectType.PROVOKE).getArguments()[0])
	    hit.setDamage((int) (hit.getDamage() * 0.5));

	if (!ability && Combat.fullDharokEquipped(player)) {
	    double maxhp = player.getMaxHitpoints();
	    double hp = maxhp - player.getHitpoints();
	    if (hp > 0) {
		//nerfed dharok effect up to 50% boost max for now, and setted bk to what it was
		double multiplier = 1 + ((hp / maxhp));
		hit.setDamage((int) (hit.getDamage() * multiplier));
	    }
	}

	//small boost added after all multipliers, including crit it seems
	int weaponType = player.getCombatDefinitions().getType(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD);
	int skill = weaponType == Combat.MAGIC_TYPE ? Skills.MAGIC : weaponType == Combat.RANGE_TYPE ? Skills.RANGE : Skills.STRENGTH;
	int boost = (player.getSkills().getLevel(skill) - player.getSkills().getLevelForXp(skill));
	if (boost > 0)
	    hit.setDamage(hit.getDamage() + Utils.random(boost * 4, boost * 8));
	return hit;
    }

    public static int getMaxHit(Player player, Entity target, boolean mainHand, int attackType, double damageMultiplier, boolean ability) {
	int maxHit = (int) (getMaxHit(player, mainHand, ability) * damageMultiplier * player.getPrayer().getCombatDamageMultiplier(attackType));

	//25% nerf
	if (target instanceof Player)
	    maxHit *= 0.75;

	if (target instanceof Player) {
	    maxHit *= ((Player) target).getPrayer().getEnemyCombatDamageMultiplier(attackType);

	    maxHit *= (1d - ((double) ((Player) target).getSkills().getLevelForXp(Skills.DEFENCE)) * 0.001);

	}
	//System.out.println("maxhit: " + maxHit + ", with crit: " + (maxHit * 1.25));
	//if spec dmg 25-100% else 0-100%
	EffectsManager effects = player.getEffectsManager();
	if (!effects.isEmpty()) {
	    if (effects.hasActiveEffect(EffectType.SHADOW_EFFECT))
		maxHit *= (double) effects.getEffectForType(EffectType.SHADOW_EFFECT).getArguments()[0];
	    if (effects.hasActiveEffect(EffectType.WEAKEN_EFFECT))
		maxHit *= (double) effects.getEffectForType(EffectType.WEAKEN_EFFECT).getArguments()[0];
	    if (effects.hasActiveEffect(EffectType.ENFEEBLE_EFFECT))
		maxHit *= (double) effects.getEffectForType(EffectType.ENFEEBLE_EFFECT).getArguments()[0];
	    if (effects.hasActiveEffect(EffectType.REVENGE))
		maxHit *= (double) effects.getEffectForType(EffectType.REVENGE).getArguments()[0];
	}
	if (target.getEffectsManager().hasActiveEffect(EffectType.CURSE_EFFECT))
	    maxHit *= (double) target.getEffectsManager().getEffectForType(EffectType.CURSE_EFFECT).getArguments()[0];
	if (target.getEffectsManager().hasActiveEffect(EffectType.VULNERABILITY_EFFECT))
	    maxHit *= (double) target.getEffectsManager().getEffectForType(EffectType.VULNERABILITY_EFFECT).getArguments()[0];
	return maxHit;
    }

    //TODO: Prayers increase your ability damage by a set percentage, which can be found on the Prayer page. Prayers do not affect the damage of bleed abilities.
    /*
     * TODO
     * Black mask, Focus sight, Hexcrest, Full slayer helmet and Slayer helmet boost your ability damage by 12.5% when on a Slayer task.
	Salve amulet and Salve amulet (e) boosts your ability damage by respectively 15% and 20% against undead creatures.
	Ferocious ring boosts ability damage by 4% when on a task in Kuradal's Dungeon.
	Balmung boosts ability damage by 45% against Dagannoths.
	Bane ammunition increases ability damage by 18.75% against appropriate monsters.
	TokKul-Zo increases ability damage by 10% against TzHaar creatures.
	Keris increases damage by 33% against Kalphites, and occasionally increases it by 200%.
	Brine sabre and Brackish blade increase damage by 40% against 'rum'-pumped crabs.
	Silverlight and Darklight double ability damage against demons.
	Using the portal to the King Black Dragon in the Lava Maze increases ability damage by 10%.
	Ancient Magicks increase damage by 100% against Muspahs.
     */

    //legacy seems to be autoattack * 2. (1.75 now since rs nerfed it later)
    //eoc seems to be about 1.25(rs buffed it lol)
    private static int getMaxHit(Player player, boolean mainHand, boolean ability) {

	int combatMode = player.getCombatDefinitions().getCombatMode();
	double dmgMultiplier = combatMode == CombatDefinitions.LEGACY_COMBAT_MODE ? 1.75 : combatMode == CombatDefinitions.MOMENTUM_COMBAT_MODE ? 1.5 : 1.00;

	return (int) ((ability ? player.getCombatDefinitions().getAbilitiesDamage() : player.getCombatDefinitions().getHandDamage(!mainHand)) * dmgMultiplier * (mainHand ? 1 : 0.5) * 0.1); //x 0.1 to reduce dmg by 10 as method return x10
    }

    private void delayHit(int delay, final Hit... hits) {
	delayHit(target, delay, hits);
    }

    public static void delayHit(final Entity target, int delay, final Hit... hits) {
	addAttackedByDelay(hits[0].getSource(), target); //called separately since some spells dont do dmg
	WorldTasksManager.schedule(new WorldTask() {

	    @Override
	    public void run() {
		for (Hit hit : hits) {
		    Player player = (Player) hit.getSource();
		    if (player.hasFinished() || target.isDead() || target.hasFinished())
			return;
		    if (target instanceof NPC) {
			NPC n = (NPC) target;
			if (n.isCantInteract()) //if npc stoped being interactable before the hit reached
			    return;
		    }
		    if (hit.isCriticalHit())
			if (player.getEffectsManager().hasActiveEffect(EffectType.ADRENALINE_GAIN_FOR_CRIT))
			    player.getCombatDefinitions().increaseSpecialAttack((int) player.getEffectsManager().getEffectForType(EffectType.ADRENALINE_GAIN_FOR_CRIT).getArguments()[0]);
		    //hit delay is 0 anyway but ye
		    if (hit.getLook() != HitLook.MAGIC_DAMAGE) {
			//Fuck poison I don't understand it u can do it -.-
			/*Effect pois = player.getEffectsManager().getEffectForType(EffectType.WEAPON_POISON);
						if (pois != null && Utils.random(8) == 0) {
							Effect anti = target.getEffectsManager().getEffectForType(EffectType.ANTIPOISON);
							if (anti != null) {
								anti.setCycle(anti.getCycle() - 16);
								target.getEffectsManager().refreshBuffEffect(anti);
							} else {
								EffectsManager.makePoisoned(target, getMaxHit(player, ));
							}
						}*/
			target.setNextGraphics(new Graphics(hit.getDelay() == 0 ? 3466 : 3465, hit.getDelay(), 100 + hit.getDamage() == 0 ? 2 : 11));
		    }
		    target.setNextAnimationNoPriority(new Animation(Combat.getDefenceEmote(target), hit.getDelay()));
		    if (player.getPrayer().usingPrayer(1, 24))
			target.sendSoulSplit(hit, player);
		    if (!(hit.getDamage() == -1))
			target.applyHit(hit); // also reduces damage if needed,
		    //autorelatie
		    autoRelatie(player, target);
		}
	    }
	}, delay);
    }

    public static void autoRelatie(Player player, Entity target) {
	if (target instanceof Player) {
	    Player p2 = (Player) target;
	    p2.closeInterfaces();
	    if (p2.getCombatDefinitions().isAutoRelatie() && !p2.getActionManager().hasSkillWorking() && !p2.hasWalkSteps() && !p2.isLocked() && !p2.getEmotesManager().isDoingEmote())
		p2.getActionManager().setAction(new PlayerCombatNew(player));
	}
	else {
	    NPC n = (NPC) target;
	    if (!n.isUnderCombat() || n.canBeAttackedByAutoRelatie())
		n.setTarget(player);
	}
    }

    private static void playSoundEffect(int soundId, Player player, Entity target) {
	if (soundId == -1)
	    return;
	player.getPackets().sendSoundEffect(soundId);
	if (target instanceof Player) {
	    Player p2 = (Player) target;
	    p2.getPackets().sendSoundEffect(soundId);
	}
    }

    private static void addAttackedByDelay(Entity player, Entity target) {
	target.setAttackedBy(player);
	target.setAttackedByDelay(Utils.currentTimeMillis() + 6000); // 8seconds
	player.setAttackingDelay(Utils.currentTimeMillis() + 6000);
    }

    public static void addAttackingDelay(Entity player) {
	player.setAttackingDelay(Utils.currentTimeMillis() + 6000);
    }

    public static void setWeaponAbilityDelay(Player player) {
	setWeaponDelay(player, true);
	setWeaponDelay(player, false);
	//delay furhter when using ability
	player.getCombatDefinitions().setMainHandDelay(player.getCombatDefinitions().getMainHandDelay() + 3);
	player.getCombatDefinitions().setOffHandDelay(player.getCombatDefinitions().getOffHandDelay() + 3);
    }

    public static void setWeaponDelay(Player player, boolean mainHand) {
	long delay = getAttackSpeed(player, mainHand);
	long nextHitMin = 2;
	if (player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON) != player.getCombatDefinitions().getType(Equipment.SLOT_SHIELD) && player.getEquipment().hasOffHand() && !player.getEquipment().hasShield())
	    nextHitMin += 1;
	if (player.getEffectsManager().hasActiveEffect(EffectType.ADRENALINE_GAIN_DECREASE)) {//Reduces speed by two
	    delay *= 2;
	    nextHitMin *= 2;
	}
	long worldCycle = Utils.currentWorldCycle();
	delay += worldCycle;
	nextHitMin += worldCycle;
	if (mainHand) {
	    if (player.getCombatDefinitions().getMainHandDelay() < delay)
		player.getCombatDefinitions().setMainHandDelay(delay);
	    if (player.getCombatDefinitions().getOffHandDelay() < nextHitMin)
		player.getCombatDefinitions().setOffHandDelay(nextHitMin);
	}
	else {
	    if (player.getCombatDefinitions().getMainHandDelay() < delay)
		player.getCombatDefinitions().setOffHandDelay(delay);
	    if (player.getCombatDefinitions().getMainHandDelay() < nextHitMin)
		player.getCombatDefinitions().setMainHandDelay(nextHitMin);
	}

    }

    public static void addCombatDelay(Player player, int delay) {
	int time = (int) (Utils.currentWorldCycle() + delay);
	if (player.getCombatDefinitions().getMainHandDelay() < time)
	    player.getCombatDefinitions().setMainHandDelay(time);
	if (player.getCombatDefinitions().getOffHandDelay() < time)
	    player.getCombatDefinitions().setOffHandDelay(time);
    }

    public static int getAttackAnimation(Player player, boolean mainHand) {
	Item item = getWeapon(player, mainHand);
	boolean legacy = player.getCombatDefinitions().getCombatMode() == CombatDefinitions.LEGACY_COMBAT_MODE;
	if (item == null)
	    return mainHand ? (legacy ? 422 : 18224) : -1;
	    return item.getDefinitions().getCombatOpcode(mainHand ? (legacy ? 4385 : 2914) : (legacy ? 4389 : 2831)/*4369 : 4373*/);
    }

    public static int getAttackSpeed(Player player, boolean mainHand) {
	Item item = getWeapon(player, mainHand);
	if (item == null)
	    return 4;
	return item.getDefinitions().getAttackSpeed();
    }

    private static Item getWeapon(Player player, boolean mainHand) {
	return player.getEquipment().getItem(mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD);
    }

    @Override
    public void stop(Player player) {
	player.setNextFaceEntity(null);
	player.getActionbar().removeBuffs();
    }

    /*	
     * not in cache sadly

	private Graphics getLoadWeaponGraphic(int weaponId, int arrowId) {
		//TODO knives
		//darts
		if (weaponId == 806)
			return new Graphics(232, 0, 96);
		if (weaponId == 807)
			return new Graphics(233, 0, 96);
		if (weaponId == 808)
			return new Graphics(234, 0, 96);
		if (weaponId == 3093)
			return new Graphics(273, 0, 96);
		if (weaponId == 809)
			return new Graphics(235, 0, 96);
		if (weaponId == 810)
			return new Graphics(236, 0, 96);
		if (weaponId == 811)
			return new Graphics(237, 0, 96);
		if (weaponId == 11230)
			return new Graphics(1123, 0, 96);
		return null;
	}*/

    public Entity[] getMultiAttackTargets(Player player) {
	return getMultiAttackTargets(player, target, 1, 9, false);
    }

    public static Entity[] getMultiAttackTargets(Player player, Entity target) {
	return getMultiAttackTargets(player, target, 1, 9, false);
    }

    public static Entity[] getMultiAttackTargets(Player player, Entity target, int maxDistance, int maxAmtTargets) {
	return getMultiAttackTargets(player, target, maxDistance, maxAmtTargets, false);
    }

    public static Entity[] getMultiAttackTargets(Player player, Entity target, int maxDistance, int maxAmtTargets, boolean usePlayerLoc) {
	List<Entity> possibleTargets = new ArrayList<Entity>();
	possibleTargets.add(target);
	y: for (int regionId : target.getMapRegionsIds()) {
	    Region region = World.getRegion(regionId);
	    if (target instanceof Player) {
		List<Integer> playerIndexes = region.getPlayerIndexes();
		if (playerIndexes == null)
		    continue;
		for (int playerIndex : playerIndexes) {
		    Player p2 = World.getPlayers().get(playerIndex);
		    if (p2 == null || p2 == player || p2 == target || p2.isDead() || !p2.hasStarted() || p2.hasFinished() || !p2.isCanPvp() || !p2.withinDistance(usePlayerLoc ? player : target, maxDistance) || !player.getControlerManager().canHit(p2) || !player.clipedProjectile(p2, false) || (p2.getControlerManager().getControler() instanceof Wilderness && !((Wilderness) p2.getControlerManager().getControler()).isMulti()))
			continue;
		    possibleTargets.add(p2);
		    if (possibleTargets.size() == maxAmtTargets)
			break y;
		}
	    }
	    else {
		List<Integer> npcIndexes = region.getNPCsIndexes();
		if (npcIndexes == null)
		    continue;
		for (int npcIndex : npcIndexes) {
		    NPC n = World.getNPCs().get(npcIndex);
		    if (n == null || n == target || n == player.getFamiliar() || n.isDead() || n.hasFinished() || !n.withinDistance(usePlayerLoc ? player : target, maxDistance) || !n.getDefinitions().hasAttackOption() || !player.getControlerManager().canHit(n) || !player.clipedProjectile(n, false))
			continue;
		    possibleTargets.add(n);
		    if (possibleTargets.size() == maxAmtTargets)
			break y;
		}
	    }
	}
	return possibleTargets.toArray(new Entity[possibleTargets.size()]);
    }

    public Entity getTarget() {
	return target;
    }
}
