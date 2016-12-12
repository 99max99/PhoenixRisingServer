package net.kagani.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.kagani.cache.loaders.BodyDefinitions;
import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.game.HeadIcon;
import net.kagani.game.item.Item;
import net.kagani.game.player.content.PlayerLook;
import net.kagani.game.player.content.clans.ClansManager;
import net.kagani.game.player.controllers.Wilderness;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Utils;

public class Appearence implements Serializable {

	private static final long serialVersionUID = 7655608569741626586L;

	private transient int renderEmote;
	private int title;
	private int[] look;
	private byte[] colour;
	private boolean male;
	private transient boolean glowRed;
	private transient byte[] appeareanceData;
	private transient byte[] md5AppeareanceDataHash;
	private transient byte[] iconsData;
	private transient byte[] md5IconsDataHash;
	private transient short transformedNpcId;
	private transient boolean hidePlayer;
	private transient boolean identityHide;
	private transient int forcedWeapon, forcedShield, forcedAmulet, forcedCape;

	private transient Player player;

	public Appearence() {
		male = Utils.random(2) == 1;
		renderEmote = -1;
		title = -1;
		resetAppearence();
		PlayerLook.randomizeLook(this);
	}

	public void setGlowRed(boolean glowRed) {
		this.glowRed = glowRed;
		generateAppearenceData();
	}

	public void setPlayer(Player player) {
		this.player = player;
		transformedNpcId = -1;
		renderEmote = -1;
		forcedWeapon = forcedShield = forcedAmulet = forcedCape = -1;
	}

	public void transformIntoNPC(int id) {
		transformedNpcId = (short) id;
		generateAppearenceData();
	}

	public void switchHidden() {
		hidePlayer = !hidePlayer;
		generateAppearenceData();
	}

	public void setHidden(boolean hidden) {
		hidePlayer = hidden;
		generateAppearenceData();
	}

	public void setIdentityHide(boolean hide) {
		identityHide = hide;
		generateAppearenceData();
	}

	public void setForcedCape(int cape) {
		forcedCape = cape;
		generateAppearenceData();
	}

	public boolean isIdentityHidden() {
		return identityHide;
	}

	public boolean isHidden() {
		return hidePlayer;
	}

	public boolean isGlowRed() {
		return glowRed;
	}

	public String getTitle() {
		return getTitle(male, title);
	}

	public static String getTitle(boolean male, int title) {
		return title == 0 ? null : ClientScriptMap.getMap(male ? 1093 : 3872)
				.getStringValue(title);
	}

	public int getTitleId() {
		return title;
	}

	public boolean isTitleAfterName() {
		return isTitleAfterName(title);
	}

	public static boolean isTitleAfterName(int title) {
		return title >= 32 && title <= 37;
	}

	public void generateAppearenceData() {
		OutputStream stream = new OutputStream();
		int flag = (player.getSize() - 1) << 3;
		if (!male)
			flag |= 0x1;
		if (transformedNpcId >= 0
				&& NPCDefinitions.getNPCDefinitions(transformedNpcId).aBoolean3190)
			flag |= 0x2;
		boolean showSkillLevel = !player.getCombatDefinitions()
				.isCombatStance() && player.getCombatDefinitions().isSheathe();
		if (showSkillLevel)
			flag |= 0x4;
		String title = getTitle();

		if (player.isIronman())
			if (isMale())
				title = "<col=5F6169><img=11>Ironman </col>";
			else
				title = "<col=5F6169><img=11>Ironwoman </col>";
		else if (player.isHardcoreIronman())
			if (isMale())
				title = "<col=A30920><img=13>Hardcore Ironman </col>";
			else
				title = "<col=A30920><img=13>Hardcore Ironwoman </col>";
		else if (player.getRights() == 0 && player.getChatBadge()
				&& player.isAMember()) {
			if (player.getCustomTitleActive()) {
				if (player.getCustomTitleCapitalize() == false)
					title = "<img=9><col=" + player.getCustomTitleColor() + ">"
							+ player.getCustomTitle() + " </col>";
				else
					title = "<img=9><col=" + player.getCustomTitleColor() + ">"
							+ Utils.fixChatMessage(player.getCustomTitle())
							+ " </col>";
			} else
				title = "<col=000000><img=9></col>";
		} else if (player.isPlatinumMember() || player.isDiamondMember()) {
			if (player.getCustomTitleActive()) {
				if (player.getCustomTitleCapitalize() == false)
					title = "<col=" + player.getCustomTitleColor() + ">"
							+ player.getCustomTitle() + " </col>";
				else
					title = "<col=" + player.getCustomTitleColor() + ">"
							+ Utils.fixChatMessage(player.getCustomTitle())
							+ " </col>";
			}
		}
		if (title != null)
			flag |= isTitleAfterName() ? 0x80 : 0x40; // after/before
		if (identityHide) {
			title = "<col=C86400>";
			flag |= 0x80;
			flag |= 0x40;
		}
		stream.writeByte(flag);
		if (title != null)
			stream.writeVersionedString(title);
		if (identityHide)
			stream.writeVersionedString("</col>");
		stream.writeByte(hidePlayer ? 1 : 0);
		stream.writeBytes(getAppearenceLook());
		stream.writeString(player.getDisplayName());
		boolean separateSummonLevel = player.isCanPvp()
				&& player.getControlerManager().getControler() instanceof Wilderness
				&& player.getFamiliar() == null;
		stream.writeByte(separateSummonLevel ? player.getSkills()
				.getCombatLevel() : player.getSkills()
				.getCombatLevelWithSummoning());
		if (showSkillLevel)
			stream.writeShort(player.getSkills().getTotalLevel());
		else {
			stream.writeByte(separateSummonLevel ? player.getSkills()
					.getCombatLevelWithSummoning() : 0);
			stream.writeByte(-1); // higher level acc name appears in front :P
			/*
			 * stream.writeByte(pvpArea ? player.getSkills().getCombatLevel() :
			 * player.getSkills().getCombatLevelWithSummoning());
			 * stream.writeByte(pvpArea ?
			 * player.getSkills().getCombatLevelWithSummoning() : 0);
			 */
		}
		boolean useNPCDetails = transformedNpcId >= 0;
		if (transformedNpcId >= 0) {
			NPCDefinitions defs = NPCDefinitions
					.getNPCDefinitions(transformedNpcId);
			HashMap<Integer, Object> data = defs.clientScriptData;
			if (data != null)
				useNPCDetails = !data.containsKey(2805);
		}
		stream.writeByte(useNPCDetails ? 1 : 0); // to end here else id
		// need to send more
		// data
		if (useNPCDetails) {
			NPCDefinitions defs = NPCDefinitions
					.getNPCDefinitions(transformedNpcId);
			stream.writeShort(defs.anInt876);
			stream.writeShort(defs.anInt842);
			stream.writeShort(defs.anInt884);
			stream.writeShort(defs.anInt875);
			stream.writeByte(defs.anInt875);
		}

		// done separated for safe because of synchronization
		byte[] appeareanceData = new byte[stream.getOffset()];
		System.arraycopy(stream.getBuffer(), 0, appeareanceData, 0,
				appeareanceData.length);
		byte[] md5Hash = Utils.encryptUsingMD5(appeareanceData);
		this.appeareanceData = appeareanceData;
		md5AppeareanceDataHash = md5Hash;
	}

	/*
	 * public byte[] getAppearenceLook(Items items) {
	 * 
	 * }
	 */

	public byte[] getAppearenceLook() {
		return getAppearenceLook(player.getEquipment().getCosmeticItems()
				.getItems(), look);
	}

	public byte[] getAppearenceLook(Item[] cosmetics, int[] look) {
		OutputStream stream = new OutputStream();
		// player data
		// npc
		if (transformedNpcId >= 0) {
			stream.writeShort(-1); // 65535 tells it a npc
			stream.writeBigSmart(transformedNpcId);
			Item cape = player.getEquipment().getItem(Equipment.SLOT_CAPE);
			stream.writeByte(cape != null ? cape.getDefinitions().getTeamId()
					: 0); // team
		} else {

			Item[] items = new Item[BodyDefinitions.getEquipmentContainerSize()];
			boolean[] skipLook = new boolean[items.length];

			for (int index = 0; index < items.length; index++) {
				Item item = player.getEquipment().isCanDisplayCosmetic() ? cosmetics[index]
						: null;

				// if original one
				if ((index == 3 || index == 5)
						&& item != null
						&& player.getEquipment().getCosmeticItems().getItems() == cosmetics) {
					Item originalWeapon = player.getEquipment().getItem(index); // if
					// diff
					// style
					// weapon
					if (originalWeapon == null
							|| originalWeapon.getDefinitions()
									.getRenderAnimId() != item.getDefinitions()
									.getRenderAnimId()) {
						item = null;
					}
				}

				if (item == null)
					item = player.getEquipment().getItems().get(index);
				if (item != null) {
					items[index] = item;
					int skipSlotLook = item.getDefinitions()
							.getEquipLookHideSlot();
					if (skipSlotLook != -1)
						skipLook[skipSlotLook] = true;
					int skipSlotLook2 = item.getDefinitions()
							.getEquipLookHideSlot2();
					if (skipSlotLook2 != -1)
						skipLook[skipSlotLook2] = true;
				}
			}

			for (int index = 0; index < items.length; index++) {
				if (BodyDefinitions.disabledSlots[index] != 0)
					continue;
				if (items[index] != null
						&& items[index].getDefinitions().equipSlot != -1) {
					stream.writeShort(16384 + items[index].getId());
					continue;
				}
				if (!skipLook[index]) {
					int lookIndex = -1;

					switch (index) {
					case 4:
						lookIndex = 2;
						break;
					case 6:
						lookIndex = 3;
						break;
					case 7:
						lookIndex = 5;
						break;
					case 8:
						lookIndex = 0;
						break;
					case 9:
						lookIndex = 4;
						break;
					case 10:
						lookIndex = 6;
						break;
					case 11:
						lookIndex = 1;
						break;

					}

					if (lookIndex != -1 && look[lookIndex] > 0) {
						stream.writeShort(0x100 + look[lookIndex]);
						continue;
					}
				}
				stream.writeByte(0);
			}

			OutputStream streamModify = new OutputStream();
			int modifyFlag = 0;
			int slotIndex = -1;
			ItemModify[] modify = generateItemModify(items, cosmetics);
			for (int index = 0; index < modify.length; index++) {
				if (BodyDefinitions.disabledSlots[index] != 0)
					continue;
				slotIndex++;
				ItemModify im = modify[index];
				if (im == null)
					continue;
				modifyFlag |= 1 << slotIndex;
				int itemFlag = 0;
				OutputStream streamItem = new OutputStream();
				if (im.maleModelId1 != -1 || im.femaleModelId1 != -1) {
					itemFlag |= 0x1;
					streamItem.writeBigSmart(im.maleModelId1);
					streamItem.writeBigSmart(im.femaleModelId1);
					if (im.maleModelId2 != -2 || im.femaleModelId2 != -2) {
						streamItem.writeBigSmart(im.maleModelId2);
						streamItem.writeBigSmart(im.femaleModelId2);
					}
					if (im.maleModelId3 != -2 || im.femaleModelId3 != -2) {
						streamItem.writeBigSmart(im.maleModelId3);
						streamItem.writeBigSmart(im.femaleModelId3);
					}
				}
				if (im.colors != null) {
					itemFlag |= 0x4;
					streamItem.writeShort(0 | 1 << 4 | 2 << 8 | 3 << 12);
					for (int i = 0; i < 4; i++)
						streamItem.writeShort(im.colors[i]);
				}
				if (im.textures != null) {
					itemFlag |= 0x8;
					streamItem.writeByte(0 | 1 << 4);
					for (int i = 0; i < 2; i++)
						streamItem.writeShort(im.textures[i]);
				}
				streamModify.writeByte(itemFlag);
				streamModify.writeBytes(streamItem.getBuffer(), 0,
						streamItem.getOffset());
			}
			stream.writeShort(modifyFlag);
			stream.writeBytes(streamModify.getBuffer(), 0,
					streamModify.getOffset());
		}

		for (int index = 0; index < colour.length; index++)
			stream.writeByte(colour[index]);

		int renderEmote = getRenderEmote();
		stream.writeShort(renderEmote);

		player.getPackets().sendCSVarInteger(779, renderEmote);

		byte[] data = new byte[stream.getOffset()];
		System.arraycopy(stream.getBuffer(), 0, data, 0, data.length);
		return data;
	}

	public HeadIcon[] getIcons() {
		List<HeadIcon> icons = new ArrayList<HeadIcon>();

		if (player.hasSkull())
			icons.add(new HeadIcon(439, player.getSkullId()));
		int prayerIcon = player.getPrayer().getPrayerHeadIcon();
		if (prayerIcon >= 0)
			icons.add(new HeadIcon(440, prayerIcon));

		return icons.toArray(new HeadIcon[icons.size()]);
	}

	public void generateIconsData() {
		OutputStream stream = new OutputStream();
		HeadIcon[] icons = getIcons();
		int mask = 0;
		for (int i = 0; i < icons.length; i++)
			mask |= 1 << i;
		stream.writeByte(mask);
		for (HeadIcon icon : icons) {
			stream.writeByte(icon.getFileId());
			stream.writeShort(icon.getSpriteId());
		}
		byte[] iconsData = new byte[stream.getOffset()];
		System.arraycopy(stream.getBuffer(), 0, iconsData, 0, iconsData.length);
		byte[] md5Hash = Utils.encryptUsingMD5(iconsData);
		this.iconsData = iconsData;
		md5IconsDataHash = md5Hash;
	}

	private ItemModify[] generateItemModify(Item[] items, Item[] cosmetics) {
		ItemModify[] modify = new ItemModify[BodyDefinitions
				.getEquipmentContainerSize()];
		for (int slotId = 0; slotId < modify.length; slotId++) {
			if ((slotId == Equipment.SLOT_WEAPON || slotId == Equipment.SLOT_SHIELD)
					&& player.getCombatDefinitions().isSheathe()
					&& player.getEquipment().getCosmeticItems().getItems() == cosmetics) {
				Item item = items[slotId];
				if (item != null) {
					int modelId = items[slotId].getDefinitions()
							.getSheatheModelId();
					setItemModifyModel(items[slotId], slotId, modify, modelId,
							modelId, -1, -1, -1, -1);
				}
			}
			if (items[slotId] != null && items[slotId] == cosmetics[slotId]) {
				int[] colors = new int[4];
				colors[0] = player.getEquipment().getCostumeColor();
				colors[1] = colors[0] + 12;
				colors[2] = colors[1] + 12;
				colors[3] = colors[2] + 12;
				setItemModifyColor(items[slotId], slotId, modify, colors);
			} else {
				int id = items[slotId] == null ? -1 : items[slotId].getId();
				if (id == 32152 || id == 32153 || id == 20768 || id == 20770
						|| id == 20772 || id == 20767 || id == 20769
						|| id == 20771)
					setItemModifyColor(
							items[slotId],
							slotId,
							modify,
							id == 32151 || id == 20768 || id == 20767 ? player
									.getMaxedCapeCustomized() : player
									.getCompletionistCapeCustomized());
				else if (id == 30920) { // Silverhawk Boots
					if (player.getSilverhawkFeathers() < 1) {
						// player.getPackets().sendGameMessage("Your Silverhawk boots are empty.");
					} else {
						/*
						 * player.getSkills().addXp(Skills.AGILITY, 1);
						 * player.setSilverhawkFeathers
						 * (player.getSilverhawkFeathers() - 1);
						 */
					}
				} else if (id == 20708 || id == 20709) {
					ClansManager manager = player.getClanManager();
					if (manager == null)
						continue;
					int[] colors = manager.getClan().getMottifColors();
					setItemModifyColor(items[slotId], slotId, modify, colors);
					setItemModifyTexture(
							items[slotId],
							slotId,
							modify,
							new short[] {
									(short) ClansManager
											.getMottifTexture(manager.getClan()
													.getMottifTop()),
									(short) ClansManager
											.getMottifTexture(manager.getClan()
													.getMottifBottom()) });
				} else if (player.getAuraManager().isActivated()
						&& slotId == Equipment.SLOT_AURA) {
					int auraId = player.getEquipment().getAuraId();
					if (auraId == -1)
						continue;
					int modelId = player.getAuraManager().getAuraModelId();
					int modelId2 = player.getAuraManager().getAuraModelId2();
					setItemModifyModel(items[slotId], slotId, modify, modelId,
							modelId, modelId2, modelId2, -1, -1);
				}
			}

		}
		return modify;
	}

	private void setItemModifyModel(Item item, int slotId, ItemModify[] modify,
			int maleModelId1, int femaleModelId1, int maleModelId2,
			int femaleModelId2, int maleModelId3, int femaleModelId3) {
		ItemDefinitions defs = item.getDefinitions();
		if (defs.getMaleWornModelId1() == -1
				|| defs.getFemaleWornModelId1() == -1)
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].maleModelId1 = maleModelId1;
		modify[slotId].femaleModelId1 = femaleModelId1;
		if (defs.getMaleWornModelId2() != -1
				|| defs.getFemaleWornModelId2() != -1) {
			modify[slotId].maleModelId2 = maleModelId2;
			modify[slotId].femaleModelId2 = femaleModelId2;
		}
		if (defs.getMaleWornModelId3() != -1
				|| defs.getFemaleWornModelId3() != -1) {
			modify[slotId].maleModelId2 = maleModelId3;
			modify[slotId].femaleModelId2 = femaleModelId3;
		}
	}

	private void setItemModifyTexture(Item item, int slotId,
			ItemModify[] modify, short[] textures) {
		ItemDefinitions defs = item.getDefinitions();
		if (defs.originalTextureColors == null
				|| defs.originalTextureColors.length != textures.length)
			return;
		if (Arrays.equals(textures, defs.originalTextureColors))
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].textures = textures;
	}

	private void setItemModifyColor(Item item, int slotId, ItemModify[] modify,
			int[] colors) {
		ItemDefinitions defs = item.getDefinitions();
		if (defs.originalModelColors == null
				|| defs.originalModelColors.length != colors.length)
			return;
		if (Arrays.equals(colors, defs.originalModelColors))
			return;
		if (modify[slotId] == null)
			modify[slotId] = new ItemModify();
		modify[slotId].colors = colors;
	}

	private static class ItemModify {

		private int[] colors;
		private short[] textures;
		private int maleModelId1;
		private int femaleModelId1;
		private int maleModelId2;
		private int femaleModelId2;
		private int maleModelId3;
		private int femaleModelId3;

		private ItemModify() {
			maleModelId1 = femaleModelId1 = -1;
			maleModelId2 = femaleModelId2 = -2;
			maleModelId3 = femaleModelId3 = -2;
		}
	}

	public int getSize() {
		if (transformedNpcId >= 0)
			return NPCDefinitions.getNPCDefinitions(transformedNpcId).size;
		return 1;
	}

	public void setRenderEmote(int id) {
		this.renderEmote = id;
		generateAppearenceData();
	}

	public int getRenderEmote() {
		if (renderEmote >= 0)
			return renderEmote;
		if (transformedNpcId >= 0) {
			NPCDefinitions defs = NPCDefinitions
					.getNPCDefinitions(transformedNpcId);
			HashMap<Integer, Object> data = defs.clientScriptData;
			if (data != null && !data.containsKey(2805))
				return defs.renderEmote;
		}
		if (player.getCombatDefinitions().isSheathe()
				&& !player.getCombatDefinitions().isCombatStance())
			return 2699;
		return player.getEquipment().getWeaponStance();
	}

	public void resetAppearence() {
		look = new int[7];
		colour = new byte[10];
		if (male)
			male();
		else
			female();
	}

	public void male() {
		look[0] = 3; // Hair
		look[1] = 14; // Beard
		look[2] = 18; // Torso
		look[3] = 26; // Arms
		look[4] = 34; // Bracelets
		look[5] = 38; // Legs
		look[6] = 42; // Shoes~

		colour[2] = 16;
		colour[1] = 16;
		colour[0] = 3;
		male = true;
		if (player != null)
			player.getEquipment().getCosmeticItems().reset();
	}

	public void female() {
		look[0] = 48; // Hair
		look[1] = -1; // Beard
		look[2] = 57; // Torso
		look[3] = 65; // Arms
		look[4] = 68; // Bracelets
		look[5] = 77; // Legs
		look[6] = 80; // Shoes

		colour[2] = 16;
		colour[1] = 16;
		colour[0] = 3;
		male = false;
		if (player != null)
			player.getEquipment().getCosmeticItems().reset();
	}

	public byte[] getAppeareanceData() {
		return appeareanceData;
	}

	public byte[] getMD5AppeareanceDataHash() {
		return md5AppeareanceDataHash;
	}

	public byte[] getIconsData() {
		return iconsData;
	}

	public byte[] getMD5IconsDataHash() {
		return md5IconsDataHash;
	}

	public boolean isMale() {
		return male;
	}

	public int[] getLook() {
		return look;
	}

	public void setLook(int i, int i2) {
		look[i] = i2;
		if (!male && i == 0)
			look[1] = -1;
	}

	public void setColor(int i, int i2) {
		colour[i] = (byte) i2;
	}

	public void setHairStyle(int i) {
		look[0] = i;
		if (!male)
			look[1] = -1;
	}

	public void setTopStyle(int i) {
		look[2] = i;
	}

	public void setBootsStyle(int i) {
		look[6] = i;
	}

	public int getTopStyle() {
		return look[2];
	}

	public void setArmsStyle(int i) {
		look[3] = i;
	}

	public void setHandsStyle(int i) {
		look[4] = i;
	}

	public void setLegsStyle(int i) {
		look[5] = i;
	}

	public int getHairStyle() {
		return look[0];
	}

	public void setBeardStyle(int i) {
		look[1] = i;
	}

	public int getBeardStyle() {
		return look[1];
	}

	public void setSkinColor(int color) {
		colour[4] = (byte) color;
	}

	public int getSkinColor() {
		return colour[4];
	}

	public void setHairColor(int color) {
		colour[0] = (byte) color;
	}

	public void setTopColor(int color) {
		colour[1] = (byte) color;
	}

	public void setLegsColor(int color) {
		colour[2] = (byte) color;
	}

	public int getHairColor() {
		return colour[0];
	}

	public int getBootColor() {
		return colour[5];
	}

	public void setBootsColor(int color) {
		colour[3] = (byte) color;
	}

	public void setTitle(int title) {
		this.title = title;
		generateAppearenceData();
	}

	public boolean isNPC() {
		return transformedNpcId != -1;
	}

	public int getForcedWeapon() {
		return forcedWeapon;
	}

	public void setForcedWeapon(int forcedWeapon) {
		this.forcedWeapon = forcedWeapon;
		generateAppearenceData();
	}

	public int getForcedShield() {
		return forcedShield;
	}

	public void setForcedShield(int forcedShield) {
		this.forcedShield = forcedShield;
		generateAppearenceData();
	}

	public int getForcedAmulet() {
		return forcedAmulet;
	}

	public void setForcedAmulet(int forcedAmulet) {
		this.forcedAmulet = forcedAmulet;
		generateAppearenceData();
	}
}
