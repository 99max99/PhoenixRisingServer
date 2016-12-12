package net.kagani.game.player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.kagani.Settings;
import net.kagani.game.Graphics;
import net.kagani.game.HeadIcon;
import net.kagani.game.Hit;
import net.kagani.game.HitBar;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Utils;

public final class LocalNPCUpdate {

	private Player player;
	private LinkedList<NPC> localNPCs;

	public void reset() {
		localNPCs.clear();
	}

	public LocalNPCUpdate(Player player) {
		this.player = player;
		localNPCs = new LinkedList<NPC>();
	}

	public OutputStream createPacketAndProcess() {
		boolean largeSceneView = player.hasLargeSceneView();
		OutputStream stream = new OutputStream();
		OutputStream updateBlockData = new OutputStream();
		stream.writePacketVarShort(player, largeSceneView ? 143 : 85);
		processLocalNPCsInform(stream, updateBlockData, largeSceneView);
		stream.writeBytes(updateBlockData.getBuffer(), 0,
				updateBlockData.getOffset());
		stream.endPacketVarShort();
		return stream;
	}

	private void processLocalNPCsInform(OutputStream stream,
			OutputStream updateBlockData, boolean largeSceneView) {
		stream.initBitAccess();
		processInScreenNPCs(stream, updateBlockData, largeSceneView);
		addInScreenNPCs(stream, updateBlockData, largeSceneView);
		if (updateBlockData.getOffset() > 0)
			stream.writeBits(15, 32767);
		stream.finishBitAccess();
	}

	private void processInScreenNPCs(OutputStream stream,
			OutputStream updateBlockData, boolean largeSceneView) {
		stream.writeBits(8, localNPCs.size());
		// for (NPC n : localNPCs.toArray(new NPC[localNPCs.size()])) {
		for (Iterator<NPC> it = localNPCs.iterator(); it.hasNext();) {
			NPC n = it.next();
			if (n.hasFinished()
					|| !(n.withinDistance(player,
							player.hasLargeSceneView() ? 64 : 14) && (!player
							.hasLargeSceneView() || player.getMapRegionsIds()
							.contains(n.getRegionId()))) || n.hasTeleported()) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 3);
				it.remove();
				continue;
			}
			boolean needUpdate = n.needMasksUpdate()
					|| player.getCombatDefinitions()
							.isNeedTargetReticuleUpdate(n);
			boolean walkUpdate = n.getNextWalkDirection() != -1;
			stream.writeBits(1, (needUpdate || walkUpdate) ? 1 : 0);
			if (walkUpdate) {
				stream.writeBits(2, n.getNextRunDirection() == -1 ? 1 : 2);
				if (n.getNextRunDirection() != -1)
					stream.writeBits(1, 1);
				stream.writeBits(3,
						Utils.getNpcMoveDirection(n.getNextWalkDirection()));
				if (n.getNextRunDirection() != -1)
					stream.writeBits(3,
							Utils.getNpcMoveDirection(n.getNextRunDirection()));
				stream.writeBits(1, needUpdate ? 1 : 0);
			} else if (needUpdate)
				stream.writeBits(2, 0);
			if (needUpdate)
				appendUpdateBlock(n, updateBlockData, false);
		}
	}

	private void addInScreenNPCs(OutputStream stream,
			OutputStream updateBlockData, boolean largeSceneView) {
		for (int regionId : player.getMapRegionsIds()) {
			List<Integer> indexes = World.getRegion(regionId).getNPCsIndexes();
			if (indexes == null)
				continue;
			for (int npcIndex : indexes) {
				if (localNPCs.size() == Settings.LOCAL_NPCS_LIMIT)
					break;
				NPC n = World.getNPCs().get(npcIndex);
				if (n == null
						|| n.hasFinished()
						|| localNPCs.contains(n)
						|| !(n.withinDistance(player,
								player.hasLargeSceneView() ? 64 : 14) && (!player
								.hasLargeSceneView() || player
								.getMapRegionsIds().contains(n.getRegionId())))
						|| n.isDead())
					continue;
				stream.writeBits(15, n.getIndex());
				// now always true because of hp
				boolean needUpdate = true;// n.needMasksUpdate() ||
											// n.getLastFaceEntity() != -1 ||
											// n.getCustomCombatLevel() >= 0 ||
											// n.getCustomName() != null ||
											// player.getCombatDefinitions().isNeedTargetReticleUpdate(n);
				int x = n.getX() - player.getX();
				int y = n.getY() - player.getY();
				if (largeSceneView) {
					if (x < 127)
						x += 256;
					if (y < 127)
						y += 256;
				} else {
					if (x < 15)
						x += 32;
					if (y < 15)
						y += 32;
				}

				stream.writeBits(largeSceneView ? 8 : 5, x);
				stream.writeBits(2, n.getPlane());
				stream.writeBits(largeSceneView ? 8 : 5, y);
				stream.writeBits(3, (n.getDirection() >> 11) - 4);
				stream.writeBits(1, needUpdate ? 1 : 0);// +;
				stream.writeBits(15, n.getId());// +
				stream.writeBits(1, n.hasTeleported() ? 1 : 0);// +;

				localNPCs.add(n);
				if (needUpdate)
					appendUpdateBlock(n, updateBlockData, true);
			}
		}
	}

	private void appendUpdateBlock(NPC n, OutputStream data, boolean added) {
		int maskData = 0;
		if (n.getNextForceMovement() != null)
			maskData |= 0x1000;
		if (n.getCustomCombatLevel() >= 0)
			maskData |= 0x400000;
		if (n.getNextAnimation() != null)
			maskData |= 0x40;
		if (added || n.isNeedTargetInformationUpdate())
			maskData |= 0x20000;
		if (n.getNextForceTalk() != null)
			maskData |= 0x4;
		if (added || n.isRefreshHeadIcon())
			maskData |= 0x80000;
		if (n.getNextGraphics1() != null)
			maskData |= 0x20;
		if (n.getNextGraphics4() != null)
			maskData |= 0x8000000;
		if (n.getNextGraphics3() != null)
			maskData |= 0x4000000;
		if (n.getNextFaceWorldTile() != null && n.getNextRunDirection() == -1
				&& n.getNextWalkDirection() == -1)
			maskData |= 0x1;
		if (!n.getNextHits().isEmpty() || !n.getNextHitBars().isEmpty())
			maskData |= 0x8;
		if (n.getNextFaceEntity() != -2
				|| (added && n.getLastFaceEntity() != -1))
			maskData |= 0x10;
		if (n.getCustomName() != null)
			maskData |= 0x40000;
		if (player.getCombatDefinitions().isNeedTargetReticuleUpdate(n))
			maskData |= 0x1000000;
		if (n.getNextTransformation() != null)
			maskData |= 0x2;
		if (n.getNextGraphics2() != null)
			maskData |= 0x2000;

		if (maskData >= 0xff)
			maskData |= 0x80;
		if (maskData >= 0xffff)
			maskData |= 0x8000;
		if (maskData >= 0xffffff)
			maskData |= 0x100000;

		data.writeShort(0); // rs doesnt use this lol
		data.writeByte(maskData);

		if (maskData >= 0xff)
			data.writeByte(maskData >> 8);
		if (maskData >= 0xffff)
			data.writeByte(maskData >> 16);
		if (maskData >= 0xffffff)
			data.writeByte(maskData >> 24);

		if (n.getNextForceMovement() != null)
			applyForceMovementMask(n, data);
		if (n.getCustomCombatLevel() >= 0)
			applyChangeLevelMask(n, data);
		if (n.getNextAnimation() != null)
			applyAnimationMask(n, data);
		if (added || n.isNeedTargetInformationUpdate())
			applyTargetInformationMask(n, data);
		if (n.getNextForceTalk() != null)
			applyForceTalkMask(n, data);
		if (added || n.isRefreshHeadIcon())
			applyIconMask(n, data);
		if (n.getNextGraphics1() != null)
			applyGraphicsMask1(n, data);
		if (n.getNextGraphics4() != null)
			applyGraphicsMask4(n, data);
		if (n.getNextGraphics3() != null)
			applyGraphicsMask3(n, data);
		if (n.getNextFaceWorldTile() != null && n.getNextRunDirection() == -1
				&& n.getNextWalkDirection() == -1)
			applyFaceWorldTileMask(n, data);
		if (!n.getNextHits().isEmpty() || !n.getNextHitBars().isEmpty())
			applyHitsMask(n, data);
		if (n.getNextFaceEntity() != -2
				|| (added && n.getLastFaceEntity() != -1))
			applyFaceEntityMask(n, data);
		if (n.getCustomName() != null)
			applyNameChangeMask(n, data);
		if (player.getCombatDefinitions().isNeedTargetReticuleUpdate(n))
			applyTargetReticuleMask(n, data);
		if (n.getNextTransformation() != null)
			applyTransformationMask(n, data);
		if (n.getNextGraphics2() != null)
			applyGraphicsMask2(n, data);
	}

	private void applyTargetInformationMask(NPC n, OutputStream data) {
		data.writeByte(1);
		data.writeByte(3);
		data.writeIntLE(n.getHitpoints());
		data.write24BitIntegerV3(n.getMaxHitpoints());
	}

	private void applyTargetReticuleMask(NPC n, OutputStream data) {
		Graphics reticle = player.getCombatDefinitions().getTargetReticule(n);
		data.writeShortLE128(reticle.getId());
		data.writeIntV2(reticle.getSettingsHash());
		data.writeByte128(reticle.getSettings2Hash());
	}

	private void applyChangeLevelMask(NPC n, OutputStream data) {
		data.writeShort128(n.getCombatLevel());
	}

	private void applyNameChangeMask(NPC npc, OutputStream data) {
		data.writeString(npc.getName());
	}

	private void applyTransformationMask(NPC n, OutputStream data) {
		data.writeBigSmart(n.getNextTransformation().getToNPCId());
	}

	private void applyForceTalkMask(NPC n, OutputStream data) {
		data.writeString(n.getNextForceTalk().getText());
	}

	private void applyForceMovementMask(NPC n, OutputStream data) {
		data.writeByte(n.getNextForceMovement().getToFirstTile().getX()
				- n.getX());
		data.writeByteC(n.getNextForceMovement().getToFirstTile().getY()
				- n.getY());
		data.write128Byte(n.getNextForceMovement().getToSecondTile() == null ? 0
				: n.getNextForceMovement().getToSecondTile().getX() - n.getX());
		data.writeByteC(n.getNextForceMovement().getToSecondTile() == null ? 0
				: n.getNextForceMovement().getToSecondTile().getY() - n.getY());
		data.writeShortLE128((n.getNextForceMovement()
				.getFirstTileTicketDelay() * 600) / 20);
		data.writeShort128(n.getNextForceMovement().getToSecondTile() == null ? 0
				: ((n.getNextForceMovement().getSecondTileTicketDelay() * 600) / 20));
		data.writeShortLE(n.getNextForceMovement().getDirection());
	}

	private void applyFaceWorldTileMask(NPC n, OutputStream data) {
		data.writeShort128((n.getNextFaceWorldTile().getX() * 2)
				+ n.getNextFaceWorldTile().getSizeX());
		data.writeShortLE((n.getNextFaceWorldTile().getY() * 2)
				+ n.getNextFaceWorldTile().getSizeY());
	}

	private void applyHitsMask(NPC n, OutputStream data) {
		data.write128Byte(n.getNextHits().size());
		for (Hit hit : n.getNextHits()) {
			boolean interactingWith = hit.interactingWith(player, n);
			if (hit.missed() && !interactingWith) {
				data.writeSmart(32766);
				data.writeByteC(hit.getDamageDisplay(player)); // dont ask me
																// why, 32766
																// sets dmg but
																// no hitmark.
			} else {
				if (hit.getSoaking() != null) {
					data.writeSmart(32767);
					data.writeSmart(hit.getMark(player, n));
					data.writeSmart(hit.getDamageDisplay(player));
					data.writeSmart(hit.getSoaking().getMark(player, n));
					data.writeSmart(hit.getSoaking().getDamageDisplay(player));
				} else {
					data.writeSmart(hit.getMark(player, n));
					data.writeSmart(hit.getDamageDisplay(player));
				}
			}
			data.writeSmart(hit.getDelay());
		}
		data.writeByte128(n.getNextHitBars().size());
		for (HitBar bar : n.getNextHitBars()) {
			data.writeSmart(bar.getType());
			int perc = bar.getPercentage();
			int toPerc = bar.getToPercentage();
			boolean display = bar.display(player);
			data.writeSmart(display ? perc != toPerc ? 1 : 0 : 32767);
			if (display) {
				data.writeSmart(bar.getDelay());
				data.writeByte(perc);
				if (toPerc != perc)
					data.writeByte(toPerc);
			}
		}
	}

	private void applyFaceEntityMask(NPC n, OutputStream data) {
		data.writeShort128(n.getNextFaceEntity() == -2 ? n.getLastFaceEntity()
				: n.getNextFaceEntity());
	}

	private void applyAnimationMask(NPC n, OutputStream data) {
		for (int id : n.getNextAnimation().getIds())
			data.writeBigSmart(id);
		data.writeByte(n.getNextAnimation().getDelay());
	}

	private void applyGraphicsMask4(NPC n, OutputStream data) {
		data.writeShortLE128(n.getNextGraphics4().getId());
		data.writeIntV1(n.getNextGraphics4().getSettingsHash());
		data.writeByteC(n.getNextGraphics4().getSettings2Hash());
	}

	private void applyGraphicsMask3(NPC n, OutputStream data) {
		data.writeShortLE(n.getNextGraphics3().getId());
		data.writeIntLE(n.getNextGraphics3().getSettingsHash());
		data.writeByte(n.getNextGraphics3().getSettings2Hash());
	}

	private void applyGraphicsMask2(NPC n, OutputStream data) {
		data.writeShortLE(n.getNextGraphics2().getId());
		data.writeIntLE(n.getNextGraphics2().getSettingsHash());
		data.write128Byte(n.getNextGraphics2().getSettings2Hash());
	}

	private void applyGraphicsMask1(NPC n, OutputStream data) {
		data.writeShort(n.getNextGraphics1().getId());
		data.writeInt(n.getNextGraphics1().getSettingsHash());
		data.writeByteC(n.getNextGraphics1().getSettings2Hash());
	}

	private void applyIconMask(NPC n, OutputStream data) {
		HeadIcon[] icons = n.getIcons();
		int mask = 0;
		for (int i = 0; i < icons.length; i++)
			mask |= 1 << i;
		data.writeByteC(mask);
		for (HeadIcon icon : icons) {
			data.writeBigSmart(icon.getSpriteId());
			data.writeSmart(icon.getFileId() + 1);
		}
	}
}
