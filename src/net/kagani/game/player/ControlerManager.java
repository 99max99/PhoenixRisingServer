package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.FloorItem;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.content.Drinkables.Drink;
import net.kagani.game.player.controllers.ControlerHandler;
import net.kagani.game.player.controllers.Controller;
import net.kagani.utils.Logger;

public final class ControlerManager implements Serializable {

	private static final long serialVersionUID = 2084691334731830796L;

	private transient Player player;
	private transient Controller controler;
	private transient boolean inited;
	private Object[] lastControlerArguments;

	private String lastControler;

	public ControlerManager() {
		lastControler = "";
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Controller getControler() {
		return controler;
	}

	public void startControler(Object key, Object... parameters) {
		if (controler != null)
			forceStop();
		controler = (Controller) (key instanceof Controller ? key
				: ControlerHandler.getControler(key));
		if (controler == null)
			return;
		controler.setPlayer(player);
		lastControlerArguments = parameters;
		lastControler = (String) key;
		controler.start();
		inited = true;
		Logger.globalLog(player.getUsername(), player.getSession().getIP(),
				new String(" started controller: " + key.toString() + "."));
	}

	public void login() {
		if (lastControler == null)
			return;
		controler = ControlerHandler.getControler(lastControler);
		if (controler == null) {
			forceStop();
			return;
		}
		controler.setPlayer(player);
		if (controler.login())
			forceStop();
		else
			inited = true;
	}

	public void logout() {
		if (controler == null)
			return;
		if (controler.logout())
			forceStop();
	}

	public boolean canMove(int dir) {
		if (controler == null || !inited)
			return true;
		return controler.canMove(dir);
	}

	public boolean addWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (controler == null || !inited)
			return true;
		return controler.checkWalkStep(lastX, lastY, nextX, nextY);
	}

	public boolean canTakeItem(FloorItem item) {
		if (controler == null || !inited)
			return true;
		return controler.canTakeItem(item);
	}

	public boolean keepCombating(Entity target) {
		if (controler == null || !inited)
			return true;
		return controler.keepCombating(target);
	}

	public boolean canEquip(int slotId, int itemId) {
		if (controler == null || !inited)
			return true;
		return controler.canEquip(slotId, itemId);
	}

	public boolean canRemoveEquip(int slotId, int itemId) {
		if (controler == null || !inited)
			return true;
		return controler.canRemoveEquip(slotId, itemId);
	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		if (controler == null || !inited)
			return true;
		return controler.canAddInventoryItem(itemId, amount);
	}

	public void trackXP(int skillId, int addedXp) {
		if (controler == null || !inited)
			return;
		controler.trackXP(skillId, addedXp);
	}

	public void trackLevelUp(int skillId, int level) {
		if (controler == null || !inited)
			return;
		controler.trackLevelUp(skillId, level);
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		if (controler == null || !inited)
			return true;
		return controler.canDeleteInventoryItem(itemId, amount);
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if (controler == null || !inited)
			return true;
		return controler.canUseItemOnItem(itemUsed, usedWith);
	}

	public boolean canAttack(Entity entity) {
		if (controler == null || !inited)
			return true;
		return controler.canAttack(entity);
	}

	public boolean canPlayerOption1(Player target) {
		if (controler == null || !inited)
			return true;
		return controler.canPlayerOption1(target);
	}

	public boolean canPlayerOption2(Player target) {
		if (controler == null || !inited)
			return true;
		return controler.canPlayerOption2(target);
	}

	public boolean canPlayerOption3(Player target) {
		if (controler == null || !inited)
			return true;
		return controler.canPlayerOption3(target);
	}

	public boolean canPlayerOption4(Player target) {
		if (controler == null || !inited)
			return true;
		return controler.canPlayerOption4(target);
	}

	public boolean canHit(Entity entity) {
		if (controler == null || !inited)
			return true;
		return controler.canHit(entity);
	}

	public void moved() {
		if (controler == null || !inited)
			return;
		controler.moved();
	}

	public void magicTeleported(int type) {
		if (controler == null || !inited)
			return;
		player.getAppearence().setRenderEmote(-1);
		controler.magicTeleported(type);
	}

	public void sendInterfaces() {
		if (controler == null || !inited)
			return;
		controler.sendInterfaces();
	}

	public void process() {
		if (controler == null || !inited)
			return;
		controler.process();
	}

	public boolean sendDeath() {
		if (controler == null || !inited)
			return true;
		return controler.sendDeath();
	}

	public boolean canEat(int heal) {
		if (controler == null || !inited)
			return true;
		return controler.canEat(heal);
	}

	public boolean canPot(Drink pot) {
		if (controler == null || !inited)
			return true;
		return controler.canPot(pot);
	}

	public boolean useDialogueScript(Object key) {
		if (controler == null || !inited)
			return true;
		return controler.useDialogueScript(key);
	}

	public boolean processMagicTeleport(WorldTile toTile) {
		if (controler == null || !inited)
			return true;
		return controler.processMagicTeleport(toTile);
	}

	public boolean processItemTeleport(WorldTile toTile) {
		if (controler == null || !inited)
			return true;
		return controler.processItemTeleport(toTile);
	}

	public boolean processObjectTeleport(WorldTile toTile) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectTeleport(toTile);
	}

	public boolean processObjectClick1(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick1(object);
	}

	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (controler == null || !inited)
			return true;
		return controler.processButtonClick(interfaceId, componentId, slotId,
				slotId2, packetId);
	}

	public boolean processNPCClick1(NPC npc) {
		if (controler == null || !inited)
			return true;
		return controler.processNPCClick1(npc);
	}

	public boolean canSummonFamiliar() {
		if (controler == null || !inited)
			return true;
		return controler.canSummonFamiliar();
	}

	public boolean processNPCClick2(NPC npc) {
		if (controler == null || !inited)
			return true;
		return controler.processNPCClick2(npc);
	}

	public boolean processNPCClick3(NPC npc) {
		if (controler == null || !inited)
			return true;
		return controler.processNPCClick3(npc);
	}

	public boolean processNPCClick4(NPC npc) {
		if (controler == null || !inited)
			return true;
		return controler.processNPCClick4(npc);
	}

	public boolean processObjectClick2(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick2(object);
	}

	public boolean processObjectClick3(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick3(object);
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		if (controler == null || !inited)
			return true;
		return controler.processItemOnNPC(npc, item);
	}

	public boolean canDropItem(Item item) {
		if (controler == null || !inited)
			return true;
		return controler.canDropItem(item);
	}

	public void forceStop() {
		if (controler != null) {
			controler.forceClose();
			controler = null;
		}
		lastControlerArguments = null;
		lastControler = null;
		inited = false;
		Logger.globalLog(player.getUsername(), player.getSession().getIP(),
				new String(" current controller has been stopped."));
	}

	public void removeControlerWithoutCheck() {
		controler = null;
		lastControlerArguments = null;
		lastControler = null;
		inited = false;
		Logger.globalLog(player.getUsername(), player.getSession().getIP(),
				new String(" current controller has been stopped."));
	}

	public void setLastController(String controller, Object... args) {
		lastControler = controller;
		lastControlerArguments = args;
	}

	public Object[] getLastControlerArguments() {
		return lastControlerArguments;
	}

	public String getLastController() {
		return lastControler;
	}

	public void setLastControlerArguments(Object[] lastControlerArguments) {
		this.lastControlerArguments = lastControlerArguments;
	}

	public boolean processObjectClick4(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick4(object);
	}

	public boolean processObjectClick5(WorldObject object) {
		if (controler == null || !inited)
			return true;
		return controler.processObjectClick5(object);
	}

	public boolean handleItemOnObject(WorldObject object, Item item) {
		if (controler == null || !inited)
			return true;
		return controler.handleItemOnObject(object, item);
	}

	public boolean processItemOnPlayer(Player p2, Item item, int slot) {
		if (controler == null || !inited)
			return true;
		return controler.processItemOnPlayer(p2, item, slot);
	}

	public void processNPCDeath(NPC id) {
		if (controler == null || !inited)
			return;
		controler.processNPCDeath(id);
	}

	public void processIncommingHit(Hit hit, Entity target) {
		if (controler == null || !inited)
			return;
		controler.processIncommingHit(hit, target);
	}

	public void processIngoingHit(Hit hit) {
		if (controler == null || !inited)
			return;
		controler.processIngoingHit(hit);
	}
}
