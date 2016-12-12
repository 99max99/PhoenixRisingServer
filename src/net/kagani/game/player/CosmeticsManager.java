package net.kagani.game.player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.kagani.Settings;
import net.kagani.cache.loaders.BodyDefinitions;
import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.game.Animation;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.network.decoders.WorldPacketsDecoder;

public class CosmeticsManager implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -6960190414485444043L;

	public static final int COSMETIC_TYPE_MENU_VARBIT = 673;

	private static final Map<Integer, Integer> cosmeticsVars1 = new HashMap<Integer, Integer>();
	private static final Map<Integer, Integer> cosmeticsVars2 = new HashMap<Integer, Integer>();

	static {
		setVars();
	}

	// autounlocked, no var. blame rs XD, book of faces, the stuffed title, hide
	// all(invisbile item)
	private static final int[] FREE_COSMETICS = { 14289, 14538, 5692, 19139,
			19140, 21095, 21096, 21097, 21103, 21102 };

	public static enum CosmeticType {
		APPEARENCE(1), WARDROBE(2), TITLE(3), ANIMATION(4), PET(5);

		private int type;

		private CosmeticType(int type) {
			this.type = type;
		}
	}

	private transient Player player;
	private transient Item[] cosmeticPreview;

	private boolean showingAllItems;
	private List<Integer> unlockedCosmetics;

	public CosmeticsManager() {
		resetUnlockedCosmetics();
		showingAllItems = true;
	}

	public void init() {
		refreshVars();
		refreshShowingAllItems();
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (unlockedCosmetics == null) // temporary
			resetUnlockedCosmetics();
	}

	private void resetUnlockedCosmetics() {
		unlockedCosmetics = new ArrayList<Integer>();
		for (int data : FREE_COSMETICS)
			unlockedCosmetics.add(data);
	}

	// save unlocked cosmetics and so on here.

	public void open(CosmeticType type) {
		player.getVarsManager().setVarBit(COSMETIC_TYPE_MENU_VARBIT, type.type);
		player.getPackets().sendExecuteScript(6460, type.type); // does same
		// send var but
		// also clears
		// up
		sendPreviewTitle(player.getAppearence().getTitleId());
		for (int i = 0; i < MENUS_COMPONENT_IDS.length; i++)
			player.getPackets().sendUnlockIComponentOptionSlots(1311,
					MENUS_COMPONENT_IDS[i], 0, 1000, 0, 1);
		if (type == CosmeticType.WARDROBE) {
			player.getPackets().sendUnlockIComponentOptionSlots(1351, 234, 0,
					3, 0);
			cosmeticPreview = player.getEquipment().getCosmeticItems()
					.getItemsCopy();
			refreshCosmeticsPreview();
		}
		sendRenderAnimation();
	}

	private void sendRenderAnimation() {
		Item weapon = cosmeticPreview == null ? null
				: cosmeticPreview[Equipment.SLOT_WEAPON];
		if (weapon == null)
			weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);

		player.getPackets().sendCSVarInteger(
				779,
				weapon == null ? 2697 : weapon.getDefinitions()
						.getRenderAnimId());
	}

	private void revert() {
		CosmeticType type = getCurrentCosmeticType();
		if (type == CosmeticType.WARDROBE) {
			cosmeticPreview = player.getEquipment().getCosmeticItems()
					.getItemsCopy();
			player.getPackets().sendAppearenceLook();
		} else if (type == CosmeticType.TITLE)
			sendPreviewTitle(player.getAppearence().getTitleId());
		else if (type == CosmeticType.ANIMATION) {
			sendPreviewAnimation(-1);
			player.getPackets().sendGameMessage("type: " + type.type + ".");
		}
		sendPreviewOptions(-1, true);
	}

	private void sendPreviewTitle(int titleId) {
		String title = Appearence.getTitle(player.getAppearence().isMale(),
				titleId);
		player.getPackets().sendExecuteScript(6453,
				Appearence.isTitleAfterName(titleId) ? 0 : 1,
				title == null ? "" : title);
	}

	public void close() {
		if (getCurrentCosmeticType() == CosmeticType.WARDROBE) {
			player.getPackets().sendAppearenceLook();
			player.getAppearence().generateAppearenceData();
			cosmeticPreview = null;
		}
		player.getVarsManager().sendVarBit(COSMETIC_TYPE_MENU_VARBIT, 0);
	}

	private void setCosmetic(ClientScriptMap map, int slot, boolean preview) {
		int itemId = map.getIntValue(slot);
		if (itemId == -1)
			return;
		Item item = new Item(itemId);
		// no model <.<
		if (item.getDefinitions().equipSlot != -1
				&& (player.getAppearence().isMale() ? item.getDefinitions()
						.getMaleWornModelId1() == -1 : item.getDefinitions()
						.getFemaleWornModelId1() == -1)) {
			return;
		}

		cosmeticPreview[slot] = item;

		if (!preview) {
			Item oldItem = player.getEquipment().getCosmeticItems().get(slot);
			boolean remove = oldItem != null && oldItem.getId() == item.getId();
			player.getEquipment().getCosmeticItems()
					.set(slot, remove ? null : item);

		}
	}

	private void sendPreviewOptions(int id, boolean hide) {
		if (id == -1 || hide)
			player.getTemporaryAttributtes().remove(Key.CURRENT_COSMETIC);
		else
			player.getTemporaryAttributtes().put(Key.CURRENT_COSMETIC, id);

		boolean canBuy = (isAutoUnlocked(id) || hasCosmeticVar(id, true) || (getCurrentCosmeticType() == CosmeticType.TITLE && (player
				.isAMember())));

		player.getPackets().sendHideIComponent(1311, 174, hide || canBuy);
		player.getPackets().sendHideIComponent(1311, 194, hide || !canBuy);
		if (!hide && canBuy)
			player.getPackets().sendIComponentText(1311, 681,
					unlockedCosmetics.contains(id) ? "Apply" : "Apply");
	}

	private void setCosmetic(GeneralRequirementMap data, boolean preview) {
		ClientScriptMap map = ClientScriptMap.getMap(data.getIntValue(2542));
		if (data.getIntValue(2532) == 20) {
			cosmeticPreview = new Item[BodyDefinitions
					.getEquipmentContainerSize()];
			if (!preview)
				player.getEquipment().getCosmeticItems().reset();
			for (Long slot : map.getValues().keySet())
				setCosmetic(map, slot.intValue(), preview);
		} else {
			int slot = data.getIntValue(2532);
			cosmeticPreview[slot] = null;
			setCosmetic(map, slot, preview);
		}
		refreshCosmeticsPreview();
		sendRenderAnimation();
		if (preview)
			sendPreviewOptions(data.getId(), !preview);
	}

	private void refreshCosmeticsPreview() {
		player.getPackets().sendAppearanceLook(cosmeticPreview,
				player.getAppearence().getLook());
		player.getPackets().sendItems(670,
				player.getEquipment().getCosmeticItems());
		player.getPackets().sendItems(671, cosmeticPreview);
	}

	// -1 means TODO that menu type(22 menus, im adding them as i need them)
	private static final int[] MENUS_COMPONENT_IDS = { 217, 242, 243, 244, 245,
			246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258,
			259, 260, 261, 262 };

	// data.getIntValue(2547) (boolean cant preview/buy such as event)

	// menu id - related to componentId
	private Integer[] getCosmeticIds(CosmeticType type, int menuId) {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		for (int index = 0; index < getCosmeticsIds(type).getSize(); index++) {
			GeneralRequirementMap data = getCosmeticData(type, index);
			if (data.getIntValue(2532) == menuId
					&& ((showingAllItems && data.getIntValue(2547) == 0)
							|| isAutoUnlocked(data.getId()) || unlockedCosmetics
							.contains(data.getId())
							&& hasCosmeticVar(data.getId(),
									data.getIntValue(2546) == 1))) {
				ids.add(index);
			}
		}
		return ids.toArray(new Integer[ids.size()]);
	}

	// if no cosmetic var, dont let it buy as cant display for now(resuming not
	// available)
	private boolean hasCosmeticVar(int id, boolean loyality) {
		return cosmeticsVars1.get(id) != null
				|| (loyality && cosmeticsVars2.get(id) != null);
	}

	private static boolean isAutoUnlocked(int id) {
		for (int i : FREE_COSMETICS)
			if (i == id)
				return true;
		return false;
	}

	// now the hell part :SSSSSS. also need newer vars after rs, or unlocking
	// will simply bug the list(to prevent that, ima force to check player vars)
	public void refreshVars() {
		for (int cosmeticId : unlockedCosmetics) {
			Integer varbitId = cosmeticsVars1.get(cosmeticId);
			if (varbitId == null)
				varbitId = cosmeticsVars2.get(cosmeticId);
			if (varbitId != null)
				player.getVarsManager().sendVarBit(varbitId, 1);
		}
	}

	private void unlockOutfit(int outfitId) {
		for (int menuId = 0; menuId < BodyDefinitions
				.getEquipmentContainerSize(); menuId++) {
			int mapId = ClientScriptMap.getMap(8464).getIntValue(menuId);
			if (mapId == -1)
				continue;
			ClientScriptMap map = ClientScriptMap.getMap(mapId);
			for (Object id : map.getValues().values()) {
				GeneralRequirementMap data = GeneralRequirementMap
						.getMap((int) id);
				if (data.getIntValue(4419) == outfitId) {
					unlockedCosmetics.add(data.getId());
				}
			}
		}
		unlockedCosmetics.add(outfitId);
	}

	/*
	 * unlocks whole outfit items if its outfit or part of one, else jut item
	 * itself
	 */
	private void unlockItem(GeneralRequirementMap data) {
		if (data.getIntValue(2532) == 20)
			unlockOutfit(data.getId());
		else {
			int outfitId = data.getIntValue(4419);
			if (outfitId > 0)
				unlockOutfit(outfitId);
			else
				unlockedCosmetics.add(data.getId());
		}
	}

	/*
	 * -1 all
	 */
	private void clearCosmetic(boolean preview, int slot) {
		if (preview) {
			if (slot == -1)
				cosmeticPreview = new Item[BodyDefinitions
						.getEquipmentContainerSize()];
			else
				cosmeticPreview[slot] = null;
		} else {
			if (slot == -1)
				player.getEquipment().getCosmeticItems().reset();
			else
				player.getEquipment().getCosmeticItems().set(slot, null);
		}
		refreshCosmeticsPreview();

	}

	public void handleButtons(int componentId, int slotId, int slotId2,
			int packetId) {
		if (componentId == 669)
			switchShowingAllItems();
		else if (componentId == 686)
			revert();
		else if (componentId == 678) { // buy / apply
			Integer id = (Integer) player.getTemporaryAttributtes().remove(
					Key.CURRENT_COSMETIC);
			if (id == null)
				return;
			apply(id);
		} else {
			for (int menuId = 0; menuId < MENUS_COMPONENT_IDS.length; menuId++) {
				if (MENUS_COMPONENT_IDS[menuId] == componentId) {
					CosmeticType type = getCurrentCosmeticType();
					if (type == null) // impossible unless you open inter
						// manualy
						return;
					int index = slotId / 3;
					if (type == CosmeticType.WARDROBE) {

						if (menuId == 19) {// TODO custom slots

							return;
						}

						if (menuId == 21) {// clear all
							if (index == 0)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										-1);
							else if (index == 1)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_HAT);
							else if (index == 2)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_CAPE);
							else if (index == 3)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_AMULET);
							else if (index == 4)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_WEAPON);
							else if (index == 5)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_CHEST);
							else if (index == 6)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_SHIELD);
							else if (index == 7)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_LEGS);
							else if (index == 8)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_HANDS);
							else if (index == 9)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_FEET);
							else if (index == 10)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_AURA);
							else if (index == 11)
								clearCosmetic(
										packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
										Equipment.SLOT_WINGS);
							sendPreviewOptions(-1, true);
							return;
						}

						int dataId = ClientScriptMap.getMap(
								ClientScriptMap.getMap(8464)
										.getIntValue(menuId)).getIntValue(
								slotId / 3);
						if (dataId == -1)
							return;
						GeneralRequirementMap data = GeneralRequirementMap
								.getMap(dataId);

						/*
						 * if(!(isAutoUnlocked(data.getId()) ||
						 * hasCosmeticVar(data.getId(), data.getIntValue(2546)
						 * == 1))) player.getPackets().sendGameMessage(
						 * "This cosmetic var is not currently added. Name: "
						 * +data.getValue(2533));
						 */

						if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
							setCosmetic(data, true); // preview cosmetic
						} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
							apply(data.getId());

					} else if (type == CosmeticType.APPEARENCE) {

						Integer[] listIds = getCosmeticIds(type, menuId);
						if (index >= listIds.length) // cant happen
							return;

						GeneralRequirementMap data = getCosmeticData(type,
								listIds[index]);
						if (menuId == 2 || menuId == 5) {
							int beardId = ClientScriptMap.getMap(703)
									.getIntValue(data.getIntValue(2772));

							if (menuId != 2
									&& !(isAutoUnlocked(data.getId()) || hasCosmeticVar(
											data.getId(),
											data.getIntValue(2546) == 1)))
								// player.getPackets().sendGameMessage("This cosmetic var is not currently added.");
								player.getAppearence().setBeardStyle(beardId);
						} else {

							int dataId = ClientScriptMap.getMap(
									player.getAppearence().isMale() ? 2338
											: 2341).getIntValue(
									data.getIntValue(2772));
							if (dataId == -1)
								return;
							GeneralRequirementMap data2 = GeneralRequirementMap
									.getMap(dataId);
							if (menuId != 1
									&& !(isAutoUnlocked(data.getId()) || hasCosmeticVar(
											data.getId(),
											data.getIntValue(2546) == 1)))
								// player.getPackets().sendGameMessage("This cosmetic var is not currently added. Name: "+
								// data2.getValue(792)+", "+data.getId());
								player.getAppearence().setHairStyle(
										data2.getIntValue(788));
						}
						if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
							sendPreviewOptions(data.getId(), false);
					} else {
						if (type == CosmeticType.TITLE && menuId == 20) { // clear
							sendPreviewTitle(0);
							if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
								player.getAppearence().setTitle(0);
								player.getPackets().sendGameMessage(
										"Your title has been cleared.");
								sendPreviewOptions(-1, true);
							}
							return;
						}
						Integer[] listIds = getCosmeticIds(type, menuId);
						if (index >= listIds.length) // cant happen
							return;
						GeneralRequirementMap data = getCosmeticData(type,
								listIds[index]);
						if (type == CosmeticType.ANIMATION) {
							if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
								sendPreviewAnimation(data.getId());
								sendPreviewOptions(data.getId(), false);
								player.setNextAnimation(new Animation(data
										.getId()));
							}
						} else if (type == CosmeticType.TITLE) {
							if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
								sendPreviewTitle(data.getIntValue(2543));
								sendPreviewOptions(data.getId(), false);
							} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
								apply(data.getId());
						}

						/*
						 * if (menuId != 20 && !(isAutoUnlocked(data.getId()) ||
						 * hasCosmeticVar(data.getId(), data.getIntValue(2546)
						 * == 1))) player.getPackets().sendGameMessage(
						 * "This cosmetic var is not currently added. Name: " +
						 * data.getValue(2533));
						 */
						/*try {
							BufferedWriter writer = new BufferedWriter(
									new FileWriter(
											"information/dumps/cosmetics.txt",
											true));
							writer.write(data.getValues() + "");
							writer.newLine();
							writer.flush();
							writer.close();
						} catch (IOException er) {
							er.printStackTrace();
						}*/
						if (Settings.DEBUG)
							player.getPackets().sendGameMessage(
									"data: " + data.getValues());

					}
					break;
				}
			}
		}
	}

	public void buy(int id, CosmeticType type) {
		if (!player.isAMemberGreaterThanGold()) {
			player.getPackets().sendGameMessage(
					"You need to be a gold member to do this.");
			return;
		}
		unlockedCosmetics.add(id);
		unlockItem(GeneralRequirementMap.getMap(id));
		player.getAppearence().generateAppearenceData();
		apply(id);
		refreshVars();
		/*
		 * player.getInterfaceManager().closeMenu();
		 * player.getPackets().sendGameMessage
		 * ("Bought cosmetic, please reopen interface again.");
		 */
	}

	public void apply(int id) {
		if (!player.isAMember()) {
			player.getPackets().sendGameMessage(
					"You need to be a member to do this.");
			return;
		}
		CosmeticType type = getCurrentCosmeticType();
		GeneralRequirementMap data = GeneralRequirementMap.getMap(id);
		if (type != CosmeticType.TITLE && !player.isAMemberGreaterThanGold()) {
			player.getPackets().sendGameMessage(
					"You need to be a gold member to do this.");
			return;
		}
		if (type == CosmeticType.TITLE) {
			if (unlockedCosmetics.contains(id)) {
				int titleId = data.getIntValue(2543);
				player.getAppearence().setTitle(
						player.getAppearence().getTitleId() == titleId ? 0
								: titleId);
				player.getPackets()
						.sendGameMessage(
								player.getAppearence().getTitleId() == 0 ? "Your title has been cleared."
										: "Your title has been changed to: "
												+ player.getAppearence()
														.getTitle() + ".");
				//
			} else {
				buy(id, type);
				int titleId = data.getIntValue(2543);
				player.getAppearence().setTitle(
						player.getAppearence().getTitleId() == titleId ? 0
								: titleId);
				player.getPackets()
						.sendGameMessage(
								player.getAppearence().getTitleId() == 0 ? "Your title has been cleared."
										: "Your title has been changed to: "
												+ player.getAppearence()
														.getTitle() + ".");
			}
		} else if (type == CosmeticType.WARDROBE) {
			if (unlockedCosmetics.contains(id)) {
				setCosmetic(data, false);
			} else
				buy(id, type);
		}
		revert();

	}

	public static GeneralRequirementMap getCosmeticData(CosmeticType type,
			int cosmeticId) {
		int dataId = getCosmeticsIds(type).getIntValue(cosmeticId);
		if (dataId == -1) // shouldnt happen but lets be safe
			return null;
		return GeneralRequirementMap.getMap(dataId);
	}

	public void refreshShowingAllItems() {
		player.getVarsManager().sendVarBit(678, showingAllItems ? 1 : 0);
	}

	public void switchShowingAllItems() {
		showingAllItems = !showingAllItems;
		refreshShowingAllItems();
	}

	public CosmeticType getCurrentCosmeticType() {
		int id = player.getVarsManager().getBitValue(COSMETIC_TYPE_MENU_VARBIT);
		for (CosmeticType type : CosmeticType.values())
			if (type.type == id)
				return type;
		return null;
	}

	public static ClientScriptMap getCosmeticsIds(CosmeticType type) {
		return ClientScriptMap.getMap(ClientScriptMap.getMap(5958).getIntValue(
				type.type));
	}

	/*
	 * animation and so on
	 */
	public void sendPreviewAnimation(int id) {
		player.getPackets().sendExecuteScript(2716, id);
	}

	// dataId - varId

	// script 6488 - vars for cosmeticVars1
	// script 6214 - vars for cosmeticVars2
	private static void setVars() {
		cosmeticsVars1.put(11113, 860);
		cosmeticsVars1.put(11114, 861);
		cosmeticsVars1.put(11115, 862);
		cosmeticsVars1.put(11116, 863);
		cosmeticsVars1.put(11117, 864);
		cosmeticsVars1.put(11118, 865);
		cosmeticsVars1.put(11119, 866);
		cosmeticsVars1.put(11120, 867);
		cosmeticsVars1.put(11121, 868);
		cosmeticsVars1.put(11122, 869);
		cosmeticsVars1.put(11123, 870);
		cosmeticsVars1.put(11124, 871);
		cosmeticsVars1.put(11125, 872);
		cosmeticsVars1.put(11126, 873);
		cosmeticsVars1.put(11127, 874);
		cosmeticsVars1.put(11128, 875);
		cosmeticsVars1.put(11129, 876);
		cosmeticsVars1.put(11130, 877);
		cosmeticsVars1.put(11131, 878);
		cosmeticsVars1.put(11132, 879);
		cosmeticsVars1.put(11133, 880);
		cosmeticsVars1.put(11134, 881);
		cosmeticsVars1.put(11135, 882);
		cosmeticsVars1.put(11136, 883);
		cosmeticsVars1.put(11137, 884);
		cosmeticsVars1.put(11138, 885);
		cosmeticsVars1.put(11139, 886);
		cosmeticsVars1.put(11140, 887);
		cosmeticsVars1.put(11141, 888);
		cosmeticsVars1.put(11142, 889);
		cosmeticsVars1.put(11143, 890);
		cosmeticsVars1.put(11144, 891);
		cosmeticsVars1.put(11145, 892);
		cosmeticsVars1.put(11146, 893);
		cosmeticsVars1.put(11147, 894);
		cosmeticsVars1.put(11148, 895);
		cosmeticsVars1.put(9918, 895);
		cosmeticsVars1.put(11149, 896);
		cosmeticsVars1.put(11150, 897);
		cosmeticsVars1.put(11151, 898);
		cosmeticsVars1.put(11152, 899);
		cosmeticsVars1.put(11153, 900);
		cosmeticsVars1.put(9919, 899);
		cosmeticsVars1.put(9920, 900);
		cosmeticsVars1.put(11154, 901);
		cosmeticsVars1.put(11080, null); // TODO manual
		cosmeticsVars1.put(11081, null); // TODO manual
		cosmeticsVars1.put(11082, null); // TODO manual
		cosmeticsVars1.put(11083, null); // TODO manual
		cosmeticsVars1.put(11084, null); // TODO manual
		cosmeticsVars1.put(11085, null); // TODO manual
		cosmeticsVars1.put(11086, null); // TODO manual
		cosmeticsVars1.put(11087, null); // TODO manual
		cosmeticsVars1.put(11088, null); // TODO manual
		cosmeticsVars1.put(11089, null); // TODO manual
		cosmeticsVars1.put(11090, null); // TODO manual
		cosmeticsVars1.put(11091, null); // TODO manual
		cosmeticsVars1.put(11092, null); // TODO manual
		cosmeticsVars1.put(11093, null); // TODO manual
		cosmeticsVars1.put(11094, null); // TODO manual
		cosmeticsVars1.put(11095, null); // TODO manual
		cosmeticsVars1.put(11096, null); // TODO manual
		cosmeticsVars1.put(11097, null); // TODO manual
		cosmeticsVars1.put(11098, null); // TODO manual
		cosmeticsVars1.put(11099, null); // TODO manual
		cosmeticsVars1.put(11100, null); // TODO manual
		cosmeticsVars1.put(11101, null); // TODO manual
		cosmeticsVars1.put(11102, null); // TODO manual
		cosmeticsVars1.put(11103, null); // TODO manual
		cosmeticsVars1.put(11104, null); // TODO manual
		cosmeticsVars1.put(11105, null); // TODO manual
		cosmeticsVars1.put(11106, null); // TODO manual
		cosmeticsVars1.put(11107, null); // TODO manual
		cosmeticsVars1.put(11108, null); // TODO manual
		cosmeticsVars1.put(11109, null); // TODO manual
		cosmeticsVars1.put(11110, null); // TODO manual
		cosmeticsVars1.put(11111, null); // TODO manual
		cosmeticsVars1.put(11112, null); // TODO manual
		cosmeticsVars1.put(11155, null); // TODO manual
		cosmeticsVars1.put(11156, null); // TODO manual
		cosmeticsVars1.put(11157, null); // TODO manual
		cosmeticsVars1.put(11158, null); // TODO manual
		cosmeticsVars1.put(11159, null); // TODO manual
		cosmeticsVars1.put(11160, null); // TODO manual
		cosmeticsVars1.put(11161, null); // TODO manual
		cosmeticsVars1.put(11162, null); // TODO manual
		cosmeticsVars1.put(11163, null); // TODO manual
		cosmeticsVars1.put(11164, null); // TODO manual
		cosmeticsVars1.put(11165, null); // TODO manual
		cosmeticsVars1.put(11166, null); // TODO manual
		cosmeticsVars1.put(11167, 915);
		cosmeticsVars1.put(11168, 917);
		cosmeticsVars1.put(11169, 919);
		cosmeticsVars1.put(11170, 921);
		cosmeticsVars1.put(11171, 923);
		cosmeticsVars1.put(11172, 925);
		cosmeticsVars1.put(11173, 927);
		cosmeticsVars1.put(11174, 929);
		cosmeticsVars1.put(11175, 931);
		cosmeticsVars1.put(11398, 780);
		cosmeticsVars1.put(11399, 781);
		cosmeticsVars1.put(11400, 782);
		cosmeticsVars1.put(11401, 783);
		cosmeticsVars1.put(11402, 784);
		cosmeticsVars1.put(11403, 786);
		cosmeticsVars1.put(11404, 785);
		cosmeticsVars1.put(11405, null); // TODO manual
		cosmeticsVars1.put(11406, null); // TODO manual
		cosmeticsVars1.put(11407, 789);
		cosmeticsVars1.put(11390, 765);
		cosmeticsVars1.put(11391, 766);
		cosmeticsVars1.put(11392, 767);
		cosmeticsVars1.put(11393, 768);
		cosmeticsVars1.put(11394, 769);
		cosmeticsVars1.put(11395, 770);
		cosmeticsVars1.put(11396, 771);
		cosmeticsVars1.put(11397, 772);
		cosmeticsVars1.put(11389, null); // TODO manual
		cosmeticsVars1.put(11419, 722);
		cosmeticsVars1.put(11420, 723);
		cosmeticsVars1.put(11421, 724);
		cosmeticsVars1.put(11422, 725);
		cosmeticsVars1.put(11423, 726);
		cosmeticsVars1.put(11424, 727);
		cosmeticsVars1.put(11425, 728);
		cosmeticsVars1.put(9922, 728);
		cosmeticsVars1.put(11426, 729);
		cosmeticsVars1.put(9923, 729);
		cosmeticsVars1.put(11427, 730);
		cosmeticsVars1.put(11428, 731);
		cosmeticsVars1.put(9924, 731);
		cosmeticsVars1.put(11444, null); // TODO manual
		cosmeticsVars1.put(11445, null); // TODO manual
		cosmeticsVars1.put(11446, null); // TODO manual
		cosmeticsVars1.put(11447, null); // TODO manual
		cosmeticsVars1.put(11448, null); // TODO manual
		cosmeticsVars1.put(11449, null); // TODO manual
		cosmeticsVars1.put(11456, 948);
		cosmeticsVars1.put(11457, 950);
		cosmeticsVars1.put(11458, 952);
		cosmeticsVars1.put(11459, 954);
		cosmeticsVars1.put(11460, 956);
		cosmeticsVars1.put(11461, 958);
		cosmeticsVars1.put(11462, 960);
		cosmeticsVars1.put(7231, 962);
		cosmeticsVars1.put(7233, 963);
		cosmeticsVars1.put(7234, 964);
		cosmeticsVars1.put(7237, 966);
		cosmeticsVars1.put(7238, 967);
		cosmeticsVars1.put(7235, 965);
		cosmeticsVars1.put(7239, 968);
		cosmeticsVars1.put(7240, 969);
		cosmeticsVars1.put(7241, 970);
		cosmeticsVars1.put(7245, 972);
		cosmeticsVars1.put(7246, 973);
		cosmeticsVars1.put(7244, 971);
		cosmeticsVars1.put(7247, 974);
		cosmeticsVars1.put(7249, 975);
		cosmeticsVars1.put(7250, 976);
		cosmeticsVars1.put(12326, 977);
		cosmeticsVars1.put(12328, 979);
		cosmeticsVars1.put(12329, 980);
		cosmeticsVars1.put(12327, 978);
		cosmeticsVars1.put(12330, 981);
		cosmeticsVars1.put(12331, 982);
		cosmeticsVars1.put(9921, 982);
		cosmeticsVars1.put(13674, 792);
		cosmeticsVars1.put(13675, 793);
		cosmeticsVars1.put(13676, 794);
		cosmeticsVars1.put(13677, 795);
		cosmeticsVars1.put(13678, 796);
		cosmeticsVars1.put(13679, 797);
		cosmeticsVars1.put(13680, 798);
		cosmeticsVars1.put(13681, 799);
		cosmeticsVars1.put(13682, 800);
		cosmeticsVars1.put(13683, 801);
		cosmeticsVars1.put(13684, 802);
		cosmeticsVars1.put(13685, 803);
		cosmeticsVars1.put(13686, 804);
		cosmeticsVars1.put(13687, 805);
		cosmeticsVars1.put(13688, 806);
		cosmeticsVars1.put(13689, 807);
		cosmeticsVars1.put(13690, 808);
		cosmeticsVars1.put(13691, 809);
		cosmeticsVars1.put(13692, 810);
		cosmeticsVars1.put(13693, 811);
		cosmeticsVars1.put(13694, 812);
		cosmeticsVars1.put(13695, null); // TODO manual
		cosmeticsVars1.put(13696, null); // TODO manual
		cosmeticsVars1.put(13836, 738);
		cosmeticsVars1.put(13837, 739);
		cosmeticsVars1.put(13838, 740);
		cosmeticsVars1.put(13839, 741);
		cosmeticsVars1.put(13840, 742);
		cosmeticsVars1.put(13841, 743);
		cosmeticsVars1.put(13842, 744);
		cosmeticsVars1.put(13843, 745);
		cosmeticsVars1.put(13844, 746);
		cosmeticsVars1.put(13845, 747);
		cosmeticsVars1.put(13846, 748);
		cosmeticsVars1.put(13847, 749);
		cosmeticsVars1.put(13848, 750);
		cosmeticsVars1.put(13849, 751);
		cosmeticsVars1.put(13850, 752);
		cosmeticsVars1.put(13851, 753);
		cosmeticsVars1.put(13852, 754);
		cosmeticsVars1.put(13853, 755);
		cosmeticsVars1.put(13854, 756);
		cosmeticsVars1.put(13855, 757);
		cosmeticsVars1.put(13856, 758);
		cosmeticsVars1.put(13857, 759);
		cosmeticsVars1.put(13858, 760);
		cosmeticsVars1.put(13859, 761);
		cosmeticsVars1.put(13860, 762);
		cosmeticsVars1.put(13861, 763);
		cosmeticsVars1.put(13863, 815);
		cosmeticsVars1.put(13864, 816);
		cosmeticsVars1.put(13865, 817);
		cosmeticsVars1.put(13866, 818);
		cosmeticsVars1.put(13867, 819);
		cosmeticsVars1.put(13868, 820);
		cosmeticsVars1.put(13869, 821);
		cosmeticsVars1.put(9904, 821);
		cosmeticsVars1.put(13870, 822);
		cosmeticsVars1.put(9905, 822);
		cosmeticsVars1.put(13871, 823);
		cosmeticsVars1.put(13872, 824);
		cosmeticsVars1.put(13873, 828);
		cosmeticsVars1.put(13874, 829);
		cosmeticsVars1.put(14490, 692);
		cosmeticsVars1.put(14491, 693);
		cosmeticsVars1.put(14492, 694);
		cosmeticsVars1.put(14493, 695);
		cosmeticsVars1.put(14494, 696);
		cosmeticsVars1.put(14495, 697);
		cosmeticsVars1.put(14496, 698);
		cosmeticsVars1.put(14497, 699);
		cosmeticsVars1.put(14498, 700);
		cosmeticsVars1.put(14499, 701);
		cosmeticsVars1.put(14500, 702);
		cosmeticsVars1.put(14501, 703);
		cosmeticsVars1.put(14502, 704);
		cosmeticsVars1.put(14503, 705);
		cosmeticsVars1.put(9917, 705);
		cosmeticsVars1.put(14505, 706);
		cosmeticsVars1.put(14504, 707);
		cosmeticsVars1.put(14507, 708);
		cosmeticsVars1.put(14506, 709);
		cosmeticsVars1.put(14518, null); // TODO manual
		cosmeticsVars1.put(14519, null); // TODO manual
		cosmeticsVars1.put(14520, null); // TODO manual
		cosmeticsVars1.put(19634, null); // TODO manual
		cosmeticsVars1.put(14521, null); // TODO manual
		cosmeticsVars1.put(19635, null); // TODO manual
		cosmeticsVars1.put(14522, null); // TODO manual
		cosmeticsVars1.put(14523, null); // TODO manual
		cosmeticsVars1.put(14524, null); // TODO manual
		cosmeticsVars1.put(14536, 222);
		cosmeticsVars1.put(14537, 222);
		cosmeticsVars1.put(14530, 710);
		cosmeticsVars1.put(14531, 712);
		cosmeticsVars1.put(14532, 714);
		cosmeticsVars1.put(14533, 716);
		cosmeticsVars1.put(14534, 718);
		cosmeticsVars1.put(14535, 720);
		cosmeticsVars1.put(9906, 774);
		cosmeticsVars1.put(9907, 774);
		cosmeticsVars1.put(9908, 775);
		cosmeticsVars1.put(9909, 776);
		cosmeticsVars1.put(9910, 776);
		cosmeticsVars1.put(9911, 777);
		cosmeticsVars1.put(9912, 777);
		cosmeticsVars1.put(9913, 778);
		cosmeticsVars1.put(9914, 778);
		cosmeticsVars1.put(9915, 779);
		cosmeticsVars1.put(9916, 779);
		cosmeticsVars1.put(975, 16812);
		cosmeticsVars1.put(3109, 16813);
		cosmeticsVars1.put(1534, 16813);
		cosmeticsVars1.put(3110, 16814);
		cosmeticsVars1.put(3119, 16815);
		cosmeticsVars1.put(3120, 16816);
		cosmeticsVars1.put(3121, 16817);
		cosmeticsVars1.put(3122, 16818);
		cosmeticsVars1.put(3123, 16819);
		cosmeticsVars1.put(3124, 16820);
		cosmeticsVars1.put(3128, 16822);
		cosmeticsVars1.put(3126, 16823);
		cosmeticsVars1.put(3125, 16823);
		cosmeticsVars1.put(3131, 16824);
		cosmeticsVars1.put(3132, 16825);
		cosmeticsVars1.put(3134, 16826);
		cosmeticsVars1.put(3138, 16827);
		cosmeticsVars1.put(5524, 16828);
		cosmeticsVars1.put(5544, 16829);
		cosmeticsVars1.put(5552, 16830);
		cosmeticsVars1.put(16548, 16938);
		cosmeticsVars1.put(16549, 16939);
		cosmeticsVars1.put(16550, 16940);
		cosmeticsVars1.put(16551, 16941);
		cosmeticsVars1.put(16552, 16942);
		cosmeticsVars1.put(16553, 16943);
		cosmeticsVars1.put(16554, 16944);
		cosmeticsVars1.put(16555, 16945);
		cosmeticsVars1.put(16556, 16946);
		cosmeticsVars1.put(16557, 16947);
		cosmeticsVars1.put(16558, 16948);
		cosmeticsVars1.put(16559, 16949);
		cosmeticsVars1.put(16560, 16950);
		cosmeticsVars1.put(16561, 16951);
		cosmeticsVars1.put(16562, 16952);
		cosmeticsVars1.put(16563, 16953);
		cosmeticsVars1.put(16564, 16954);
		cosmeticsVars1.put(16565, 16955);
		cosmeticsVars1.put(17330, 16890);
		cosmeticsVars1.put(17331, 16891);
		cosmeticsVars1.put(17332, 16892);
		cosmeticsVars1.put(17333, 16893);
		cosmeticsVars1.put(17334, 16894);
		cosmeticsVars1.put(18792, 16895);
		cosmeticsVars1.put(18793, 16896);
		cosmeticsVars1.put(18794, 16897);
		cosmeticsVars1.put(18795, 16898);
		cosmeticsVars1.put(18796, 16899);
		cosmeticsVars1.put(16584, null); // TODO manual
		cosmeticsVars1.put(16585, null); // TODO manual
		cosmeticsVars1.put(16586, null); // TODO manual
		cosmeticsVars1.put(16587, null); // TODO manual
		cosmeticsVars1.put(16588, null); // TODO manual
		cosmeticsVars1.put(16589, null); // TODO manual
		cosmeticsVars1.put(17294, 17507);
		cosmeticsVars1.put(17295, 17508);
		cosmeticsVars1.put(17296, 17509);
		cosmeticsVars1.put(17297, 17510);
		cosmeticsVars1.put(17298, 17511);
		cosmeticsVars1.put(17299, 17512);
		cosmeticsVars1.put(17306, 17519);
		cosmeticsVars1.put(17307, 17520);
		cosmeticsVars1.put(17308, 17521);
		cosmeticsVars1.put(17309, 17522);
		cosmeticsVars1.put(17310, 17523);
		cosmeticsVars1.put(17311, 17524);
		cosmeticsVars1.put(17300, 17513);
		cosmeticsVars1.put(17301, 17514);
		cosmeticsVars1.put(17302, 17515);
		cosmeticsVars1.put(17303, 17516);
		cosmeticsVars1.put(17304, 17517);
		cosmeticsVars1.put(17305, 17518);
		cosmeticsVars1.put(17312, 17525);
		cosmeticsVars1.put(17313, 17526);
		cosmeticsVars1.put(17314, 17527);
		cosmeticsVars1.put(17315, 17528);
		cosmeticsVars1.put(17316, 17529);
		cosmeticsVars1.put(17317, 17530);
		cosmeticsVars1.put(17318, 17531);
		cosmeticsVars1.put(17319, 17532);
		cosmeticsVars1.put(17320, 17533);
		cosmeticsVars1.put(17321, 17534);
		cosmeticsVars1.put(17322, 17535);
		cosmeticsVars1.put(17323, 17536);
		cosmeticsVars1.put(19019, 17628);
		cosmeticsVars1.put(19022, 17629);
		cosmeticsVars1.put(19025, 17630);
		cosmeticsVars1.put(17324, 17537);
		cosmeticsVars1.put(17325, 17537);
		cosmeticsVars1.put(17326, 17537);
		cosmeticsVars1.put(17327, 17537);
		cosmeticsVars1.put(17328, 17537);
		cosmeticsVars1.put(17329, 17537);
		cosmeticsVars1.put(19028, 19028); // TODO manual
		cosmeticsVars1.put(19029, 19029); // TODO manual
		cosmeticsVars1.put(19030, null); // TODO manual
		cosmeticsVars1.put(19031, null); // TODO manual
		cosmeticsVars1.put(19032, null); // TODO manual
		cosmeticsVars1.put(23223, null); // TODO manual
		cosmeticsVars1.put(383, 17617);
		cosmeticsVars1.put(19042, 17617);
		cosmeticsVars1.put(19043, 17617);
		cosmeticsVars1.put(19044, 17617);
		cosmeticsVars1.put(19046, 17617);
		cosmeticsVars1.put(19047, 17617);
		cosmeticsVars1.put(19045, 17617);
		cosmeticsVars1.put(19048, 17742);
		cosmeticsVars1.put(19049, 17742);
		cosmeticsVars1.put(19050, 17742);
		cosmeticsVars1.put(19051, 17742);
		cosmeticsVars1.put(19053, 17742);
		cosmeticsVars1.put(19054, 17742);
		cosmeticsVars1.put(19052, 17742);
		cosmeticsVars1.put(19121, 17805);
		cosmeticsVars1.put(19122, 17806);
		cosmeticsVars1.put(19123, 17807);
		cosmeticsVars1.put(19124, 17808);
		cosmeticsVars1.put(13697, 17873);
		cosmeticsVars1.put(14961, 17873);
		cosmeticsVars1.put(14962, 17873);
		cosmeticsVars1.put(14963, 17873);
		cosmeticsVars1.put(14964, 17873);
		cosmeticsVars1.put(19147, 17873);
		cosmeticsVars1.put(19148, 17871);
		cosmeticsVars1.put(19149, 17871);
		cosmeticsVars1.put(19150, 17871);
		cosmeticsVars1.put(19151, 17871);
		cosmeticsVars1.put(19152, 17871);
		cosmeticsVars1.put(19153, 17871);
		cosmeticsVars1.put(19154, 17871);
		cosmeticsVars1.put(19155, 17870);
		cosmeticsVars1.put(19156, 17870);
		cosmeticsVars1.put(19157, 17870);
		cosmeticsVars1.put(19158, 17870);
		cosmeticsVars1.put(19159, 17870);
		cosmeticsVars1.put(19160, 17870);
		cosmeticsVars1.put(19167, 17872);
		cosmeticsVars1.put(19168, 17872);
		cosmeticsVars1.put(19169, 17872);
		cosmeticsVars1.put(19170, 17872);
		cosmeticsVars1.put(19171, 17872);
		cosmeticsVars1.put(19172, 17872);
		cosmeticsVars1.put(19173, 17872);
		cosmeticsVars1.put(19161, 17874);
		cosmeticsVars1.put(19162, 17874);
		cosmeticsVars1.put(19163, 17874);
		cosmeticsVars1.put(19164, 17874);
		cosmeticsVars1.put(19165, 17874);
		cosmeticsVars1.put(19166, 17874);
		cosmeticsVars1.put(19125, 17816);
		cosmeticsVars1.put(19126, 17817);
		cosmeticsVars1.put(19127, 17818);
		cosmeticsVars1.put(19128, 17819);
		cosmeticsVars1.put(19129, 17820);
		cosmeticsVars1.put(19130, 17821);
		cosmeticsVars1.put(19131, 17822);
		cosmeticsVars1.put(19132, 17823);
		cosmeticsVars1.put(19133, 17824);
		cosmeticsVars1.put(19134, 17825);
		cosmeticsVars1.put(19135, 17826);
		cosmeticsVars1.put(19136, 17827);
		cosmeticsVars1.put(19137, 17828);
		cosmeticsVars1.put(19138, 17829);
		cosmeticsVars1.put(9891, 17876);
		cosmeticsVars1.put(14509, 17876);
		cosmeticsVars1.put(14510, 17876);
		cosmeticsVars1.put(14511, 17876);
		cosmeticsVars1.put(19059, 17876);
		cosmeticsVars1.put(19060, 17876);
		cosmeticsVars1.put(19061, 17880);
		cosmeticsVars1.put(19196, 18036);
		cosmeticsVars1.put(19213, 18036);
		cosmeticsVars1.put(19214, 18036);
		cosmeticsVars1.put(19215, 18036);
		cosmeticsVars1.put(19216, 18036);
		cosmeticsVars1.put(19217, 18036);
		cosmeticsVars1.put(19218, 18037);
		cosmeticsVars1.put(19219, 18037);
		cosmeticsVars1.put(19220, 18037);
		cosmeticsVars1.put(19221, 18037);
		cosmeticsVars1.put(19222, 18037);
		cosmeticsVars1.put(19223, 18037);
		cosmeticsVars1.put(19224, 18038);
		cosmeticsVars1.put(19225, 18038);
		cosmeticsVars1.put(19226, 18038);
		cosmeticsVars1.put(19227, 18038);
		cosmeticsVars1.put(19228, 18038);
		cosmeticsVars1.put(19229, 18038);
		cosmeticsVars1.put(19230, 18039);
		cosmeticsVars1.put(19231, 18039);
		cosmeticsVars1.put(19232, 18039);
		cosmeticsVars1.put(19233, 18039);
		cosmeticsVars1.put(19234, 18039);
		cosmeticsVars1.put(19235, 18039);
		cosmeticsVars1.put(19236, 18040);
		cosmeticsVars1.put(19237, 18041);
		cosmeticsVars1.put(19238, 18042);
		cosmeticsVars1.put(19239, 18043);
		cosmeticsVars1.put(6334, 18198);
		cosmeticsVars1.put(19195, 18168);
		cosmeticsVars1.put(19197, 18168);
		cosmeticsVars1.put(19209, 18168);
		cosmeticsVars1.put(19210, 18168);
		cosmeticsVars1.put(19306, 18168);
		cosmeticsVars1.put(19307, 18168);
		cosmeticsVars1.put(19208, 18168);
		cosmeticsVars1.put(19308, 18169);
		cosmeticsVars1.put(19309, 18169);
		cosmeticsVars1.put(19310, 18169);
		cosmeticsVars1.put(19312, 18169);
		cosmeticsVars1.put(19311, 18169);
		cosmeticsVars1.put(19313, 18169);
		cosmeticsVars1.put(19314, 18169);
		cosmeticsVars1.put(19315, 18170);
		cosmeticsVars1.put(19316, 18170);
		cosmeticsVars1.put(19317, 18170);
		cosmeticsVars1.put(19319, 18170);
		cosmeticsVars1.put(19318, 18170);
		cosmeticsVars1.put(19320, 18170);
		cosmeticsVars1.put(19321, 18171);
		cosmeticsVars1.put(19322, 18171);
		cosmeticsVars1.put(19323, 18171);
		cosmeticsVars1.put(19325, 18171);
		cosmeticsVars1.put(19324, 18171);
		cosmeticsVars1.put(19326, 18171);
		cosmeticsVars1.put(19327, 18171);
		cosmeticsVars1.put(19328, 18172);
		cosmeticsVars1.put(19329, 18172);
		cosmeticsVars1.put(19330, 18172);
		cosmeticsVars1.put(19332, 18172);
		cosmeticsVars1.put(19331, 18172);
		cosmeticsVars1.put(19333, 18172);
		cosmeticsVars1.put(19417, 18227);
		cosmeticsVars1.put(19418, 18228);
		cosmeticsVars1.put(19419, 18229);
		cosmeticsVars1.put(19421, 18229);
		cosmeticsVars1.put(19420, 18229);
		cosmeticsVars1.put(19422, 18229);
		cosmeticsVars1.put(19423, 18230);
		cosmeticsVars1.put(19534, 18285);
		cosmeticsVars1.put(19535, 18285);
		cosmeticsVars1.put(19536, 18285);
		cosmeticsVars1.put(19538, 18285);
		cosmeticsVars1.put(19537, 18285);
		cosmeticsVars1.put(19539, 18285);
		cosmeticsVars1.put(19540, 18286);
		cosmeticsVars1.put(19561, 18322);
		cosmeticsVars1.put(19562, 18322);
		cosmeticsVars1.put(19563, 18322);
		cosmeticsVars1.put(19565, 18322);
		cosmeticsVars1.put(19564, 18322);
		cosmeticsVars1.put(19566, 18322);
		cosmeticsVars1.put(19567, 18323);
		cosmeticsVars1.put(19568, 18323);
		cosmeticsVars1.put(19569, 18323);
		cosmeticsVars1.put(19571, 18323);
		cosmeticsVars1.put(19570, 18323);
		cosmeticsVars1.put(19572, 18323);
		cosmeticsVars1.put(19573, 18324);
		cosmeticsVars1.put(19574, 18324);
		cosmeticsVars1.put(19575, 18324);
		cosmeticsVars1.put(19577, 18324);
		cosmeticsVars1.put(19576, 18324);
		cosmeticsVars1.put(19578, 18324);
		cosmeticsVars1.put(19579, 18325);
		cosmeticsVars1.put(19580, 18325);
		cosmeticsVars1.put(19581, 18325);
		cosmeticsVars1.put(19583, 18325);
		cosmeticsVars1.put(19582, 18325);
		cosmeticsVars1.put(19584, 18325);
		cosmeticsVars1.put(19605, 18389);
		cosmeticsVars1.put(19606, 18390);
		cosmeticsVars1.put(19607, 18391);
		cosmeticsVars1.put(19608, 18392);
		cosmeticsVars1.put(19614, 18478);
		cosmeticsVars1.put(19644, 18478);
		cosmeticsVars1.put(19645, 18478);
		cosmeticsVars1.put(19646, 18478);
		cosmeticsVars1.put(19647, 18478);
		cosmeticsVars1.put(19648, 18478);
		cosmeticsVars1.put(19649, 18478);
		cosmeticsVars1.put(19650, 18478);
		cosmeticsVars1.put(19636, null); // TODO manual
		cosmeticsVars1.put(19637, null); // TODO manual
		cosmeticsVars1.put(19638, null); // TODO manual
		cosmeticsVars1.put(19639, null); // TODO manual
		cosmeticsVars1.put(19640, null); // TODO manual
		cosmeticsVars1.put(19641, null); // TODO manual
		cosmeticsVars1.put(19642, null); // TODO manual
		cosmeticsVars1.put(19643, null); // TODO manual
		cosmeticsVars1.put(18808, 18483);
		cosmeticsVars1.put(19411, 18484);
		cosmeticsVars1.put(19412, 18499);
		cosmeticsVars1.put(19413, 18500);
		cosmeticsVars1.put(19674, 18501);
		cosmeticsVars1.put(19675, 18502);
		cosmeticsVars1.put(19676, 18503);
		cosmeticsVars1.put(19677, 18504);
		cosmeticsVars1.put(19414, 18531);
		cosmeticsVars1.put(19424, 18532);
		cosmeticsVars1.put(21064, 18756);
		cosmeticsVars1.put(21065, 18757);
		cosmeticsVars1.put(28300, 22523);
		cosmeticsVars1.put(21066, 18762);
		cosmeticsVars1.put(28301, 22524);
		cosmeticsVars1.put(21067, 18759);
		cosmeticsVars1.put(21068, 18758);
		cosmeticsVars1.put(21069, 18761);
		cosmeticsVars1.put(21070, 18760);
		cosmeticsVars1.put(20281, 18605);
		cosmeticsVars1.put(22576, 20052);
		cosmeticsVars1.put(22577, 20074);
		cosmeticsVars1.put(22578, 20074);
		cosmeticsVars1.put(22579, 20053);
		cosmeticsVars1.put(22580, 20054);
		cosmeticsVars1.put(22581, 20055);
		cosmeticsVars1.put(22582, 20056);
		cosmeticsVars1.put(22583, 20057);
		cosmeticsVars1.put(22584, 20057);
		cosmeticsVars1.put(22585, 20058);
		cosmeticsVars1.put(22586, 20061);
		cosmeticsVars1.put(22587, 20073);
		cosmeticsVars1.put(22588, 20073);
		cosmeticsVars1.put(22589, 20062);
		cosmeticsVars1.put(22590, 20063);
		cosmeticsVars1.put(22591, 20064);
		cosmeticsVars1.put(22592, 20065);
		cosmeticsVars1.put(22593, 20066);
		cosmeticsVars1.put(22594, 20066);
		cosmeticsVars1.put(22595, 20067);
		cosmeticsVars1.put(22596, 20067);
		cosmeticsVars1.put(22597, 20068);
		cosmeticsVars1.put(22598, 20069);
		cosmeticsVars1.put(22599, 20070);
		cosmeticsVars1.put(20301, 1891);
		cosmeticsVars1.put(21375, 1891);
		cosmeticsVars1.put(21376, 1891);
		cosmeticsVars1.put(21377, 1891);
		cosmeticsVars1.put(21378, 1891);
		cosmeticsVars1.put(21379, 1891);
		cosmeticsVars1.put(21380, 1891);
		cosmeticsVars1.put(21381, 5993);
		cosmeticsVars1.put(21382, 5993);
		cosmeticsVars1.put(21383, 5993);
		cosmeticsVars1.put(21384, 5993);
		cosmeticsVars1.put(21385, 5993);
		cosmeticsVars1.put(21386, 5993);
		cosmeticsVars1.put(21387, 5993);
		cosmeticsVars1.put(21388, 19941);
		cosmeticsVars1.put(21389, 19941);
		cosmeticsVars1.put(21390, 19941);
		cosmeticsVars1.put(21391, 19941);
		cosmeticsVars1.put(21392, 19941);
		cosmeticsVars1.put(21393, 19941);
		cosmeticsVars1.put(21394, 19942);
		cosmeticsVars1.put(21395, 19942);
		cosmeticsVars1.put(21396, 19942);
		cosmeticsVars1.put(21397, 19942);
		cosmeticsVars1.put(21398, 19942);
		cosmeticsVars1.put(21399, 19942);
		cosmeticsVars1.put(21400, 19943);
		cosmeticsVars1.put(21401, 19943);
		cosmeticsVars1.put(21402, 19943);
		cosmeticsVars1.put(21403, 19943);
		cosmeticsVars1.put(21404, 19943);
		cosmeticsVars1.put(21405, 19943);
		cosmeticsVars1.put(21406, 19943);
		cosmeticsVars1.put(21407, 19944);
		cosmeticsVars1.put(21408, 19944);
		cosmeticsVars1.put(21409, 19944);
		cosmeticsVars1.put(21410, 19944);
		cosmeticsVars1.put(21411, 19944);
		cosmeticsVars1.put(21412, 19944);
		cosmeticsVars1.put(21413, 19944);
		cosmeticsVars1.put(21414, 19945);
		cosmeticsVars1.put(21415, 19945);
		cosmeticsVars1.put(21416, 19945);
		cosmeticsVars1.put(21417, 19945);
		cosmeticsVars1.put(21418, 19945);
		cosmeticsVars1.put(21419, 19945);
		cosmeticsVars1.put(21420, 19946);
		cosmeticsVars1.put(21421, 19946);
		cosmeticsVars1.put(21422, 19946);
		cosmeticsVars1.put(21423, 19946);
		cosmeticsVars1.put(21424, 19946);
		cosmeticsVars1.put(21425, 19946);
		cosmeticsVars1.put(20338, 18671);
		cosmeticsVars1.put(20340, 18672);
		cosmeticsVars1.put(20342, 18673);
		cosmeticsVars1.put(20339, 18668);
		cosmeticsVars1.put(20341, 18669);
		cosmeticsVars1.put(20343, 18670);
		cosmeticsVars1.put(21078, null); // TODO manual
		cosmeticsVars1.put(21079, null); // TODO manual
		cosmeticsVars1.put(21080, null); // TODO manual
		cosmeticsVars1.put(21081, null); // TODO manual
		cosmeticsVars1.put(21082, null); // TODO manual
		cosmeticsVars1.put(21083, null); // TODO manual
		cosmeticsVars1.put(21084, null); // TODO manual
		cosmeticsVars1.put(21085, null); // TODO manual
		cosmeticsVars1.put(21086, null); // TODO manual
		cosmeticsVars1.put(21087, null); // TODO manual
		cosmeticsVars1.put(21088, 18778);
		cosmeticsVars1.put(21091, 18779);
		cosmeticsVars1.put(22565, null); // TODO manual
		cosmeticsVars1.put(22566, 20011);
		cosmeticsVars1.put(22567, 20012);
		cosmeticsVars1.put(22568, 20013);
		cosmeticsVars1.put(22569, 20014);
		cosmeticsVars1.put(22570, 20015);
		cosmeticsVars1.put(22561, null); // TODO manual
		cosmeticsVars1.put(22562, null); // TODO manual
		cosmeticsVars1.put(22563, null); // TODO manual
		cosmeticsVars1.put(22564, null); // TODO manual
		cosmeticsVars1.put(23127, null); // TODO manual
		cosmeticsVars1.put(23128, null); // TODO manual
		cosmeticsVars1.put(23132, null); // TODO manual
		cosmeticsVars1.put(23133, null); // TODO manual
		cosmeticsVars1.put(23111, 20370);
		cosmeticsVars1.put(23112, 20370);
		cosmeticsVars1.put(23113, 20370);
		cosmeticsVars1.put(23114, 20370);
		cosmeticsVars1.put(23115, 20370);
		cosmeticsVars1.put(23119, 20373);
		cosmeticsVars1.put(23120, 20373);
		cosmeticsVars1.put(23116, 20374);
		cosmeticsVars1.put(23117, 20374);
		cosmeticsVars1.put(23118, 20375);
		cosmeticsVars1.put(23121, 20411);
		cosmeticsVars1.put(23122, 20411);
		cosmeticsVars1.put(23123, 20411);
		cosmeticsVars1.put(23124, 20411);
		cosmeticsVars1.put(23125, 20411);
		cosmeticsVars1.put(22571, null); // TODO manual
		cosmeticsVars1.put(23004, null); // TODO manual
		cosmeticsVars1.put(23005, null); // TODO manual
		cosmeticsVars1.put(23006, null); // TODO manual
		cosmeticsVars1.put(23007, null); // TODO manual
		cosmeticsVars1.put(23008, null); // TODO manual
		cosmeticsVars1.put(23009, null); // TODO manual
		cosmeticsVars1.put(23010, null); // TODO manual
		cosmeticsVars1.put(23011, null); // TODO manual
		cosmeticsVars1.put(23012, null); // TODO manual
		cosmeticsVars1.put(23013, null); // TODO manual
		cosmeticsVars1.put(23014, null); // TODO manual
		cosmeticsVars1.put(23015, null); // TODO manual
		cosmeticsVars1.put(23016, null); // TODO manual
		cosmeticsVars1.put(23017, null); // TODO manual
		cosmeticsVars1.put(23018, null); // TODO manual
		cosmeticsVars1.put(23019, null); // TODO manual
		cosmeticsVars1.put(23020, null); // TODO manual
		cosmeticsVars1.put(23021, null); // TODO manual
		cosmeticsVars1.put(23022, null); // TODO manual
		cosmeticsVars1.put(23023, null); // TODO manual
		cosmeticsVars1.put(23024, null); // TODO manual
		cosmeticsVars1.put(23025, null); // TODO manual
		cosmeticsVars1.put(23026, null); // TODO manual
		cosmeticsVars1.put(23027, null); // TODO manual
		cosmeticsVars1.put(23028, null); // TODO manual
		cosmeticsVars1.put(23029, null); // TODO manual
		cosmeticsVars1.put(23030, null); // TODO manual
		cosmeticsVars1.put(23031, null); // TODO manual
		cosmeticsVars1.put(23032, null); // TODO manual
		cosmeticsVars1.put(23033, null); // TODO manual
		cosmeticsVars1.put(23034, null); // TODO manual
		cosmeticsVars1.put(23035, null); // TODO manual
		cosmeticsVars1.put(23036, null); // TODO manual
		cosmeticsVars1.put(23037, null); // TODO manual
		cosmeticsVars1.put(23038, null); // TODO manual
		cosmeticsVars1.put(23039, null); // TODO manual
		cosmeticsVars1.put(23040, null); // TODO manual
		cosmeticsVars1.put(23041, null); // TODO manual
		cosmeticsVars1.put(23042, null); // TODO manual
		cosmeticsVars1.put(23043, null); // TODO manual
		cosmeticsVars1.put(23044, null); // TODO manual
		cosmeticsVars1.put(23045, null); // TODO manual
		cosmeticsVars1.put(23046, null); // TODO manual
		cosmeticsVars1.put(23047, null); // TODO manual
		cosmeticsVars1.put(23048, null); // TODO manual
		cosmeticsVars1.put(23049, null); // TODO manual
		cosmeticsVars1.put(23050, null); // TODO manual
		cosmeticsVars1.put(23051, null); // TODO manual
		cosmeticsVars1.put(23052, null); // TODO manual
		cosmeticsVars1.put(23053, null); // TODO manual
		cosmeticsVars1.put(23054, null); // TODO manual
		cosmeticsVars1.put(23055, null); // TODO manual
		cosmeticsVars1.put(23056, null); // TODO manual
		cosmeticsVars1.put(23057, null); // TODO manual
		cosmeticsVars1.put(23058, null); // TODO manual
		cosmeticsVars1.put(23059, null); // TODO manual
		cosmeticsVars1.put(23060, null); // TODO manual
		cosmeticsVars1.put(23061, null); // TODO manual
		cosmeticsVars1.put(23062, null); // TODO manual
		cosmeticsVars1.put(23063, null); // TODO manual
		cosmeticsVars1.put(23064, null); // TODO manual
		cosmeticsVars1.put(23065, null); // TODO manual
		cosmeticsVars1.put(23066, null); // TODO manual
		cosmeticsVars1.put(23067, null); // TODO manual
		cosmeticsVars1.put(23068, null); // TODO manual
		cosmeticsVars1.put(23069, null); // TODO manual
		cosmeticsVars1.put(23070, null); // TODO manual
		cosmeticsVars1.put(23072, null); // TODO manual
		cosmeticsVars1.put(23071, null); // TODO manual
		cosmeticsVars1.put(23073, null); // TODO manual
		cosmeticsVars1.put(23074, null); // TODO manual
		cosmeticsVars1.put(23075, null); // TODO manual
		cosmeticsVars1.put(23076, null); // TODO manual
		cosmeticsVars1.put(23077, null); // TODO manual
		cosmeticsVars1.put(23081, null); // TODO manual
		cosmeticsVars1.put(23080, null); // TODO manual
		cosmeticsVars1.put(23079, null); // TODO manual
		cosmeticsVars1.put(23078, null); // TODO manual
		cosmeticsVars1.put(11051, null); // TODO manual
		cosmeticsVars1.put(11052, 20412);
		cosmeticsVars1.put(11053, 20413);
		cosmeticsVars1.put(11054, 20414);
		cosmeticsVars1.put(11055, 20415);
		cosmeticsVars1.put(11056, 20416);
		cosmeticsVars1.put(23130, 20407);
		cosmeticsVars1.put(21210, 20547);
		cosmeticsVars1.put(21211, 20548);
		cosmeticsVars1.put(21212, 20551);
		cosmeticsVars1.put(23185, 20553);
		cosmeticsVars1.put(11061, null); // TODO manual
		cosmeticsVars1.put(11062, null); // TODO manual
		cosmeticsVars1.put(20278, null); // TODO manual
		cosmeticsVars1.put(23186, 20586);
		cosmeticsVars1.put(23197, 20616);
		cosmeticsVars1.put(23198, 20616);
		cosmeticsVars1.put(23199, 20616);
		cosmeticsVars1.put(23200, 20616);
		cosmeticsVars1.put(23201, 20616);
		cosmeticsVars1.put(23202, 20616);
		cosmeticsVars1.put(23203, 20616);
		cosmeticsVars1.put(23204, 20617);
		cosmeticsVars1.put(23205, 20617);
		cosmeticsVars1.put(23206, 20617);
		cosmeticsVars1.put(23207, 20617);
		cosmeticsVars1.put(23208, 20617);
		cosmeticsVars1.put(23209, 20617);
		cosmeticsVars1.put(23210, 20618);
		cosmeticsVars1.put(23211, 20618);
		cosmeticsVars1.put(23212, 20618);
		cosmeticsVars1.put(23213, 20618);
		cosmeticsVars1.put(23214, 20618);
		cosmeticsVars1.put(23215, 20618);
		cosmeticsVars1.put(23216, 20618);
		cosmeticsVars1.put(23217, 20619);
		cosmeticsVars1.put(23218, 20619);
		cosmeticsVars1.put(23219, 20619);
		cosmeticsVars1.put(23220, 20619);
		cosmeticsVars1.put(23221, 20619);
		cosmeticsVars1.put(23222, 20619);
		cosmeticsVars1.put(6975, 21297);
		cosmeticsVars1.put(25026, 21297);
		cosmeticsVars1.put(25027, 21297);
		cosmeticsVars1.put(25677, 21297);
		cosmeticsVars1.put(25678, 21297);
		cosmeticsVars1.put(25679, 21297);
		cosmeticsVars1.put(25680, 21297);
		cosmeticsVars1.put(25681, 21298);
		cosmeticsVars1.put(25682, 21298);
		cosmeticsVars1.put(25683, 21298);
		cosmeticsVars1.put(25684, 21298);
		cosmeticsVars1.put(25765, 21298);
		cosmeticsVars1.put(25766, 21298);
		cosmeticsVars1.put(25767, 21299);
		cosmeticsVars1.put(25772, 21299);
		cosmeticsVars1.put(25773, 21299);
		cosmeticsVars1.put(25774, 21299);
		cosmeticsVars1.put(25775, 21299);
		cosmeticsVars1.put(25780, 21299);
		cosmeticsVars1.put(25781, 21299);
		cosmeticsVars1.put(25782, 21300);
		cosmeticsVars1.put(25783, 21300);
		cosmeticsVars1.put(25784, 21300);
		cosmeticsVars1.put(25785, 21300);
		cosmeticsVars1.put(25786, 21300);
		cosmeticsVars1.put(25787, 21300);
		cosmeticsVars1.put(25788, 21301);
		cosmeticsVars1.put(25789, 21302);
		cosmeticsVars1.put(11060, 20614);
		cosmeticsVars1.put(23191, 20615);
		cosmeticsVars1.put(24043, 20678);
		cosmeticsVars1.put(24044, 20678);
		cosmeticsVars1.put(24045, 20678);
		cosmeticsVars1.put(24046, 20678);
		cosmeticsVars1.put(24047, 20678);
		cosmeticsVars1.put(24048, 20678);
		cosmeticsVars1.put(24049, 20678);
		cosmeticsVars1.put(24050, 20678);
		cosmeticsVars1.put(24051, 20679);
		cosmeticsVars1.put(24052, 20679);
		cosmeticsVars1.put(24053, 20679);
		cosmeticsVars1.put(24054, 20679);
		cosmeticsVars1.put(24055, 20679);
		cosmeticsVars1.put(24056, 20679);
		cosmeticsVars1.put(24057, 20679);
		cosmeticsVars1.put(24058, 20679);
		cosmeticsVars1.put(24059, 20679);
		cosmeticsVars1.put(24042, 20680);
		cosmeticsVars1.put(24031, 20705);
		cosmeticsVars1.put(24032, 20706);
		cosmeticsVars1.put(24038, 20725);
		cosmeticsVars1.put(24039, 20727);
		cosmeticsVars1.put(24040, 20726);
		cosmeticsVars1.put(24041, 14429);
		cosmeticsVars1.put(24126, 20732);
		cosmeticsVars1.put(24127, 20730);
		cosmeticsVars1.put(24128, 20731);
		cosmeticsVars1.put(24166, 20798);
		cosmeticsVars1.put(24167, 20798);
		cosmeticsVars1.put(24168, 20798);
		cosmeticsVars1.put(24169, 20798);
		cosmeticsVars1.put(24170, 20798);
		cosmeticsVars1.put(24171, 20798);
		cosmeticsVars1.put(24172, 20798);
		cosmeticsVars1.put(24173, 20799);
		cosmeticsVars1.put(24174, 20799);
		cosmeticsVars1.put(24175, 20799);
		cosmeticsVars1.put(24176, 20799);
		cosmeticsVars1.put(24177, 20799);
		cosmeticsVars1.put(24178, 20799);
		cosmeticsVars1.put(24179, 20799);
		cosmeticsVars1.put(24180, 20799);
		cosmeticsVars1.put(24125, 20789);
		cosmeticsVars1.put(24136, 20789);
		cosmeticsVars1.put(24137, 20789);
		cosmeticsVars1.put(24138, 20789);
		cosmeticsVars1.put(24139, 20789);
		cosmeticsVars1.put(24140, 20789);
		cosmeticsVars1.put(24196, 20886);
		cosmeticsVars1.put(24199, 20889);
		cosmeticsVars1.put(21104, 20858);
		cosmeticsVars1.put(21105, 20859);
		cosmeticsVars1.put(21106, 20860);
		cosmeticsVars1.put(21107, 20861);
		cosmeticsVars1.put(14993, 21015);
		cosmeticsVars1.put(21109, 21017);
		cosmeticsVars1.put(24191, 21001);
		cosmeticsVars1.put(24192, 21001);
		cosmeticsVars1.put(24193, 21001);
		cosmeticsVars1.put(24194, 21001);
		cosmeticsVars1.put(24980, 21001);
		cosmeticsVars1.put(24981, 21001);
		cosmeticsVars1.put(24982, 21001);
		cosmeticsVars1.put(24983, 21001);
		cosmeticsVars1.put(24984, 21001);
		cosmeticsVars1.put(24985, 21001);
		cosmeticsVars1.put(24986, 21001);
		cosmeticsVars1.put(24987, 21001);
		cosmeticsVars1.put(24988, 21001);
		cosmeticsVars1.put(24989, 21001);
		cosmeticsVars1.put(24990, 21001);
		cosmeticsVars1.put(24991, 21001);
		cosmeticsVars1.put(24992, 21001);
		cosmeticsVars1.put(24993, 21001);
		cosmeticsVars1.put(24994, 21001);
		cosmeticsVars1.put(24995, 21001);
		cosmeticsVars1.put(24996, 21001);
		cosmeticsVars1.put(24997, 21001);
		cosmeticsVars1.put(24998, 21002);
		cosmeticsVars1.put(24999, 21002);
		cosmeticsVars1.put(25000, 21002);
		cosmeticsVars1.put(26469, 21002);
		cosmeticsVars1.put(25001, 21002);
		cosmeticsVars1.put(26470, 21002);
		cosmeticsVars1.put(25002, 21002);
		cosmeticsVars1.put(25003, 21002);
		cosmeticsVars1.put(25004, 21002);
		cosmeticsVars1.put(25005, 21002);
		cosmeticsVars1.put(25006, 21002);
		cosmeticsVars1.put(25007, 21002);
		cosmeticsVars1.put(26471, 21002);
		cosmeticsVars1.put(25008, 21002);
		cosmeticsVars1.put(25009, 21002);
		cosmeticsVars1.put(25010, 21002);
		cosmeticsVars1.put(25011, 21002);
		cosmeticsVars1.put(26472, 21002);
		cosmeticsVars1.put(7013, 21249);
		cosmeticsVars1.put(14992, 21243);
		cosmeticsVars1.put(25697, 21226);
		cosmeticsVars1.put(25698, 21227);
		cosmeticsVars1.put(25689, 25689); // TODO manual
		cosmeticsVars1.put(25690, 25690); // TODO manual
		cosmeticsVars1.put(25691, 25691); // TODO manual
		cosmeticsVars1.put(25692, null); // TODO manual
		cosmeticsVars1.put(25693, null); // TODO manual
		cosmeticsVars1.put(25694, null); // TODO manual
		cosmeticsVars1.put(25695, null); // TODO manual
		cosmeticsVars1.put(25696, null); // TODO manual
		cosmeticsVars1.put(25055, 21044);
		cosmeticsVars1.put(25056, 21045);
		cosmeticsVars1.put(25057, 21038);
		cosmeticsVars1.put(25058, 21038);
		cosmeticsVars1.put(25059, 21039);
		cosmeticsVars1.put(25060, 21039);
		cosmeticsVars1.put(25061, 21041);
		cosmeticsVars1.put(25062, 21041);
		cosmeticsVars1.put(25063, 21042);
		cosmeticsVars1.put(25064, 21042);
		cosmeticsVars1.put(25065, 21047);
		cosmeticsVars1.put(25066, 21048);
		cosmeticsVars1.put(25745, 21291);
		cosmeticsVars1.put(25746, 21291);
		cosmeticsVars1.put(25747, 21291);
		cosmeticsVars1.put(25748, 21291);
		cosmeticsVars1.put(25749, 21291);
		cosmeticsVars1.put(25750, 21291);
		cosmeticsVars1.put(25751, 21291);
		cosmeticsVars1.put(25752, 21292);
		cosmeticsVars1.put(25755, 21292);
		cosmeticsVars1.put(25756, 21292);
		cosmeticsVars1.put(25757, 21292);
		cosmeticsVars1.put(25758, 21292);
		cosmeticsVars1.put(25759, 21292);
		cosmeticsVars1.put(25760, 21292);
		cosmeticsVars1.put(25761, 21292);
		cosmeticsVars1.put(26394, 21365);
		cosmeticsVars1.put(26457, 21365);
		cosmeticsVars1.put(26458, 21365);
		cosmeticsVars1.put(26459, 21365);
		cosmeticsVars1.put(26460, 21366);
		cosmeticsVars1.put(26461, 21366);
		cosmeticsVars1.put(26462, 21366);
		cosmeticsVars1.put(26463, 21366);
		cosmeticsVars1.put(26464, 21362);
		cosmeticsVars1.put(26465, 21362);
		cosmeticsVars1.put(26466, 21362);
		cosmeticsVars1.put(26467, 21362);
		cosmeticsVars1.put(26468, 21363);
		cosmeticsVars1.put(26399, 21354);
		cosmeticsVars1.put(26400, 21354);
		cosmeticsVars1.put(26401, 21354);
		cosmeticsVars1.put(26402, 21354);
		cosmeticsVars1.put(26404, 21354);
		cosmeticsVars1.put(26403, 21354);
		cosmeticsVars1.put(26405, 21354);
		cosmeticsVars1.put(26406, 21354);
		cosmeticsVars1.put(26407, 21355);
		cosmeticsVars1.put(26408, 21355);
		cosmeticsVars1.put(26409, 21355);
		cosmeticsVars1.put(26410, 21355);
		cosmeticsVars1.put(26412, 21355);
		cosmeticsVars1.put(26411, 21355);
		cosmeticsVars1.put(26413, 21355);
		cosmeticsVars1.put(26414, 21355);
		cosmeticsVars1.put(25794, null); // TODO manual
		cosmeticsVars1.put(26415, null); // TODO manual
		cosmeticsVars1.put(26416, null); // TODO manual
		cosmeticsVars1.put(26570, 21466);
		cosmeticsVars1.put(26784, 21891);
		cosmeticsVars1.put(26564, null); // TODO manual
		cosmeticsVars1.put(26565, null); // TODO manual
		cosmeticsVars1.put(26566, 21444);
		cosmeticsVars1.put(26567, 21446);
		cosmeticsVars1.put(26568, 21448);
		cosmeticsVars1.put(26569, 21450);
		cosmeticsVars1.put(26528, null); // TODO manual
		cosmeticsVars1.put(26529, null); // TODO manual
		cosmeticsVars1.put(26832, 21900);
		cosmeticsVars1.put(26833, 21901);
		cosmeticsVars1.put(26834, 21902);
		cosmeticsVars1.put(26835, 21903);
		cosmeticsVars1.put(26741, null); // TODO manual
		cosmeticsVars1.put(26742, null); // TODO manual
		cosmeticsVars1.put(26743, null); // TODO manual
		cosmeticsVars1.put(26744, null); // TODO manual
		cosmeticsVars1.put(26745, null); // TODO manual
		cosmeticsVars1.put(26746, null); // TODO manual
		cosmeticsVars1.put(26747, null); // TODO manual
		cosmeticsVars1.put(26748, null); // TODO manual
		cosmeticsVars1.put(26749, null); // TODO manual
		cosmeticsVars1.put(26750, null); // TODO manual
		cosmeticsVars1.put(26751, null); // TODO manual
		cosmeticsVars1.put(26752, null); // TODO manual
		cosmeticsVars1.put(26753, null); // TODO manual
		cosmeticsVars1.put(26754, null); // TODO manual
		cosmeticsVars1.put(26755, null); // TODO manual
		cosmeticsVars1.put(26756, null); // TODO manual
		cosmeticsVars1.put(26757, null); // TODO manual
		cosmeticsVars1.put(26758, null); // TODO manual
		cosmeticsVars1.put(26759, null); // TODO manual
		cosmeticsVars1.put(26760, null); // TODO manual
		cosmeticsVars1.put(26761, null); // TODO manual
		cosmeticsVars1.put(26762, null); // TODO manual
		cosmeticsVars1.put(26763, null); // TODO manual
		cosmeticsVars1.put(26764, null); // TODO manual
		cosmeticsVars1.put(26765, null); // TODO manual
		cosmeticsVars1.put(26766, null); // TODO manual
		cosmeticsVars1.put(26767, null); // TODO manual
		cosmeticsVars1.put(26768, null); // TODO manual
		cosmeticsVars1.put(26769, null); // TODO manual
		cosmeticsVars1.put(26770, null); // TODO manual
		cosmeticsVars1.put(26771, null); // TODO manual
		cosmeticsVars1.put(26772, null); // TODO manual
		cosmeticsVars1.put(26773, null); // TODO manual
		cosmeticsVars1.put(26774, null); // TODO manual
		cosmeticsVars1.put(26775, null); // TODO manual
		cosmeticsVars1.put(26776, null); // TODO manual
		cosmeticsVars1.put(26777, null); // TODO manual
		cosmeticsVars1.put(26778, null); // TODO manual
		cosmeticsVars1.put(26779, null); // TODO manual
		cosmeticsVars1.put(26780, null); // TODO manual
		cosmeticsVars1.put(26781, null); // TODO manual
		cosmeticsVars1.put(26782, null); // TODO manual
		cosmeticsVars1.put(26783, null); // TODO manual
		cosmeticsVars1.put(26788, 21904);
		cosmeticsVars1.put(26789, 21906);
		cosmeticsVars1.put(26790, 21908);
		cosmeticsVars1.put(26791, 21910);
		cosmeticsVars1.put(26787, 22032);
		cosmeticsVars1.put(26739, 21987);
		cosmeticsVars1.put(26740, 21988);
		cosmeticsVars1.put(26887, null); // TODO manual
		cosmeticsVars1.put(26888, null); // TODO manual
		cosmeticsVars1.put(26889, 21958);
		cosmeticsVars1.put(26891, 21958);
		cosmeticsVars1.put(26792, 21913);
		cosmeticsVars1.put(26793, 21913);
		cosmeticsVars1.put(26794, 21913);
		cosmeticsVars1.put(26795, 21913);
		cosmeticsVars1.put(26796, 21913);
		cosmeticsVars1.put(26797, 21913);
		cosmeticsVars1.put(26798, 21913);
		cosmeticsVars1.put(26799, 21913);
		cosmeticsVars1.put(26800, 21913);
		cosmeticsVars1.put(26801, 21914);
		cosmeticsVars1.put(26802, 21914);
		cosmeticsVars1.put(26803, 21914);
		cosmeticsVars1.put(26804, 21914);
		cosmeticsVars1.put(26805, 21914);
		cosmeticsVars1.put(26806, 21914);
		cosmeticsVars1.put(26807, 21914);
		cosmeticsVars1.put(26808, 21914);
		cosmeticsVars1.put(26809, 21914);
		cosmeticsVars1.put(26810, 21914);
		cosmeticsVars1.put(26811, 21914);
		cosmeticsVars1.put(26812, 21914);
		cosmeticsVars1.put(26813, 21914);
		cosmeticsVars1.put(26814, 21916);
		cosmeticsVars1.put(26816, 21916);
		cosmeticsVars1.put(26815, 21916);
		cosmeticsVars1.put(26817, 21916);
		cosmeticsVars1.put(26818, 21916);
		cosmeticsVars1.put(26819, 21916);
		cosmeticsVars1.put(26820, 21916);
		cosmeticsVars1.put(26821, 21916);
		cosmeticsVars1.put(26822, 21916);
		cosmeticsVars1.put(26825, 21915);
		cosmeticsVars1.put(26824, 21915);
		cosmeticsVars1.put(26823, 21915);
		cosmeticsVars1.put(26826, 21915);
		cosmeticsVars1.put(26827, 21915);
		cosmeticsVars1.put(26828, 21915);
		cosmeticsVars1.put(26829, 21915);
		cosmeticsVars1.put(26830, 21915);
		cosmeticsVars1.put(26831, 21915);
		cosmeticsVars1.put(28218, 22375);
		cosmeticsVars1.put(28219, 22375);
		cosmeticsVars1.put(28220, 22375);
		cosmeticsVars1.put(28221, 22375);
		cosmeticsVars1.put(28222, 22375);
		cosmeticsVars1.put(28223, 22375);
		cosmeticsVars1.put(28224, 22375);
		cosmeticsVars1.put(28225, 22375);
		cosmeticsVars1.put(28226, 22375);
		cosmeticsVars1.put(28227, 22375);
		cosmeticsVars1.put(28228, 22375);
		cosmeticsVars1.put(28229, 22375);
		cosmeticsVars1.put(28230, 22375);
		cosmeticsVars1.put(28231, 22375);
		cosmeticsVars1.put(28232, 22375);
		cosmeticsVars1.put(28233, 22375);
		cosmeticsVars1.put(28234, 22375);
		cosmeticsVars1.put(28235, 22375);
		cosmeticsVars1.put(28236, 22375);
		cosmeticsVars1.put(28237, 22375);
		cosmeticsVars1.put(28238, 22378);
		cosmeticsVars1.put(28239, 22378);
		cosmeticsVars1.put(28240, 22378);
		cosmeticsVars1.put(28241, 22378);
		cosmeticsVars1.put(28242, 22376);
		cosmeticsVars1.put(28243, 22376);
		cosmeticsVars1.put(28244, 22376);
		cosmeticsVars1.put(28245, 22376);
		cosmeticsVars1.put(28246, 22377);
		cosmeticsVars1.put(28247, 22377);
		cosmeticsVars1.put(28248, 22377);
		cosmeticsVars1.put(28249, 22377);
		cosmeticsVars1.put(27904, 22280);
		cosmeticsVars1.put(27905, 22281);
		cosmeticsVars1.put(27906, 22282);
		cosmeticsVars1.put(28250, 22367);
		cosmeticsVars1.put(28253, 22370);
		cosmeticsVars1.put(28217, 22379);
		cosmeticsVars1.put(28261, 22441);
		cosmeticsVars1.put(28262, 22442);
		cosmeticsVars1.put(28263, 22443);
		cosmeticsVars1.put(28264, 22444);
		cosmeticsVars1.put(28265, 22445);
		cosmeticsVars1.put(28267, 22449);
		cosmeticsVars1.put(28266, 22448);
		cosmeticsVars1.put(28268, 22446);
		cosmeticsVars1.put(28374, 22633);
		cosmeticsVars1.put(28375, 22634);
		cosmeticsVars1.put(28376, 22635);
		cosmeticsVars1.put(28377, 22636);
		cosmeticsVars1.put(28378, 22636);
		cosmeticsVars1.put(28379, 22637);
		cosmeticsVars1.put(28380, 22637);
		cosmeticsVars1.put(28381, 22638);
		cosmeticsVars1.put(28384, 18920);
		cosmeticsVars1.put(28385, 18921);
		cosmeticsVars1.put(28408, 22640);
		cosmeticsVars1.put(28409, 22641);
		cosmeticsVars1.put(28410, 22642);
		cosmeticsVars1.put(1763, null); // TODO manual
		cosmeticsVars1.put(28318, 22544);
		cosmeticsVars1.put(28320, 22546);
		cosmeticsVars1.put(28322, 22548);
		cosmeticsVars1.put(28386, null); // TODO manual
		cosmeticsVars1.put(28387, null); // TODO manual
		cosmeticsVars1.put(28388, null); // TODO manual
		cosmeticsVars1.put(28389, null); // TODO manual
		cosmeticsVars1.put(28390, null); // TODO manual
		cosmeticsVars1.put(28382, 28382); // TODO manual
		cosmeticsVars1.put(28383, 28338); // TODO manual
		cosmeticsVars1.put(28391, 28391); // TODO manual
		cosmeticsVars1.put(28392, 22646);
		cosmeticsVars1.put(28393, 22646);
		cosmeticsVars1.put(28394, 22646);
		cosmeticsVars1.put(28395, 22646);
		cosmeticsVars1.put(28396, 22646);
		cosmeticsVars1.put(28397, 22646);
		cosmeticsVars1.put(28398, 22646);
		cosmeticsVars1.put(28399, 22646);
		cosmeticsVars1.put(28400, 22647);
		cosmeticsVars1.put(28401, 22647);
		cosmeticsVars1.put(28402, 22647);
		cosmeticsVars1.put(28403, 22647);
		cosmeticsVars1.put(28404, 22647);
		cosmeticsVars1.put(28405, 22647);
		cosmeticsVars1.put(28406, 22647);
		cosmeticsVars1.put(28407, 22647);
		cosmeticsVars1.put(28211, 21633);
		cosmeticsVars1.put(28212, 21635);
		cosmeticsVars1.put(28213, 21637);
		cosmeticsVars1.put(28210, 22776);
	}
}