package net.kagani.game.player;

import java.security.MessageDigest;

import net.kagani.Settings;
import net.kagani.game.Colour;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.HitBar;
import net.kagani.game.World;
import net.kagani.stream.OutputStream;

public final class LocalPlayerUpdate {

	/**
	 * The maximum amount of local players being added per tick. This is to
	 * decrease time it takes to load crowded places (such as home).
	 */
	private static final int MAX_PLAYER_ADD = 15;

	private Player player;

	private byte[] slotFlags;

	private Player[] localPlayers;
	private int[] localPlayersIndexes;
	private int localPlayersIndexesCount;

	private int[] outPlayersIndexes;
	private int outPlayersIndexesCount;

	private int[] regionHashes;

	private byte[][] cachedAppearencesHashes;
	private byte[][] cachedIconsHashes;
	private int totalRenderDataSentLength;

	/**
	 * The amount of local players added this tick.
	 */
	private int localAddedPlayers;

	public Player[] getLocalPlayers() {
		return localPlayers;
	}

	public int getLocalPlayersIndexesCount() {
		return localPlayersIndexesCount;
	}

	public int[] getLocalPlayersIndexes() {
		return localPlayersIndexes;
	}

	public boolean needAppearenceUpdate(int index, byte[] hash) {
		if (totalRenderDataSentLength > ((Settings.PACKET_SIZE_LIMIT - 500) / 2)
				|| hash == null)
			return false;
		return cachedAppearencesHashes[index] == null
				|| !MessageDigest.isEqual(cachedAppearencesHashes[index], hash);
	}

	public boolean needIconsUpdate(int index, byte[] hash) {
		if (totalRenderDataSentLength > ((Settings.PACKET_SIZE_LIMIT - 500) / 2)
				|| hash == null)
			return false;
		return cachedIconsHashes[index] == null
				|| !MessageDigest.isEqual(cachedIconsHashes[index], hash);
	}

	public LocalPlayerUpdate(Player player) {
		this.player = player;
		slotFlags = new byte[2048];
		localPlayers = new Player[2048];
		localPlayersIndexes = new int[Settings.PLAYERS_LIMIT];
		outPlayersIndexes = new int[2048];
		regionHashes = new int[2048];
		cachedAppearencesHashes = new byte[Settings.PLAYERS_LIMIT][];
		cachedIconsHashes = new byte[Settings.PLAYERS_LIMIT][];
	}

	public void init(OutputStream stream) {
		stream.initBitAccess();
		stream.writeBits(30, player.getTileHash());
		localPlayers[player.getIndex()] = player;
		localPlayersIndexes[localPlayersIndexesCount++] = player.getIndex();
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			if (playerIndex == player.getIndex())
				continue;
			Player player = World.getPlayers().get(playerIndex);
			stream.writeBits(18, regionHashes[playerIndex] = player == null ? 0
					: player.getRegionHash());
			outPlayersIndexes[outPlayersIndexesCount++] = playerIndex;

		}
		stream.finishBitAccess();
	}

	private boolean needsRemove(Player p) {
		// can't just do this or you'll get chat from other dungeons
		return p != player
				&& (!p.getCutscenesManager().showYourselfToOthers()
						|| p.hasFinished() || !(player.withinDistance(p,
						player.hasLargeSceneView() ? 182 : 14) && /*
																 * (player.
																 * hasLargeSceneView
																 * () ||
																 */player
						.getMapRegionsIds().contains(p.getRegionId())));
	}

	private boolean needsAdd(Player p) {
		return p != null
				&& p.getCutscenesManager().showYourselfToOthers()
				&& p.isRunning()
				&& (player.withinDistance(p, player.hasLargeSceneView() ? 182
						: 14) && /*
								 * ( player . hasLargeSceneView ( ) ||
								 */player.getMapRegionsIds().contains(
						p.getRegionId())) && localAddedPlayers < MAX_PLAYER_ADD
				&& p.clientHasLoadedMapRegion();
	}

	private void updateRegionHash(OutputStream stream, int lastRegionHash,
			int currentRegionHash, int currentMovementType) {
		int lastRegionX = lastRegionHash >> 8;
		int lastRegionY = 0xff & lastRegionHash;
		int lastPlane = lastRegionHash >> 16;
		int currentRegionX = currentRegionHash >> 8;
		int currentRegionY = 0xff & currentRegionHash;
		int currentPlane = currentRegionHash >> 16;
		int planeOffset = currentPlane - lastPlane;
		if (lastRegionX == currentRegionX && lastRegionY == currentRegionY) {
			stream.writeBits(2, 1);
			stream.writeBits(2, planeOffset);
		} else if (Math.abs(currentRegionX - lastRegionX) <= 1
				&& Math.abs(currentRegionY - lastRegionY) <= 1) {
			int opcode;
			int dx = currentRegionX - lastRegionX;
			int dy = currentRegionY - lastRegionY;
			if (dx == -1 && dy == -1)
				opcode = 0;
			else if (dx == 1 && dy == -1)
				opcode = 2;
			else if (dx == -1 && dy == 1)
				opcode = 5;
			else if (dx == 1 && dy == 1)
				opcode = 7;
			else if (dy == -1)
				opcode = 1;
			else if (dx == -1)
				opcode = 3;
			else if (dx == 1)
				opcode = 4;
			else
				opcode = 6;
			stream.writeBits(2, 2);
			stream.writeBits(5, (planeOffset << 3) + (opcode & 0x7));
		} else {
			int xOffset = currentRegionX - lastRegionX;
			int yOffset = currentRegionY - lastRegionY;
			stream.writeBits(2, 3);
			stream.writeBits(20, (yOffset & 0xff) + ((xOffset & 0xff) << 8)
					+ (planeOffset << 16) + (currentMovementType << 18));
		}
	}

	private void processOutsidePlayers(OutputStream stream,
			OutputStream updateBlockData, boolean nsn2) {
		stream.initBitAccess();
		int skip = 0;
		localAddedPlayers = 0;
		for (int i = 0; i < outPlayersIndexesCount; i++) {
			int playerIndex = outPlayersIndexes[i];

			if (nsn2 ? (0x1 & slotFlags[playerIndex]) == 0
					: (0x1 & slotFlags[playerIndex]) != 0)
				continue;

			if (skip > 0) {
				skip--;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				continue;
			}
			Player p = World.getPlayers().get(playerIndex);
			if (needsAdd(p)) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 0); // request add
				int hash = p.getRegionHash();
				if (hash == regionHashes[playerIndex])
					stream.writeBits(1, 0);
				else {
					stream.writeBits(1, 1);
					updateRegionHash(stream, regionHashes[playerIndex], hash,
							p.getMovementType());
					regionHashes[playerIndex] = hash;
				}
				stream.writeBits(6, p.getXInRegion());
				stream.writeBits(6, p.getYInRegion());
				boolean needAppearenceUpdate = needAppearenceUpdate(
						p.getIndex(), p.getAppearence()
								.getMD5AppeareanceDataHash());
				boolean needIconsUpdate = needIconsUpdate(p.getIndex(), p
						.getAppearence().getMD5IconsDataHash());
				appendUpdateBlock(p, updateBlockData, needAppearenceUpdate,
						needIconsUpdate, true);
				stream.writeBits(1, 1);
				localAddedPlayers++;
				localPlayers[p.getIndex()] = p;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
			} else {
				// no need to update hash if player not near
				/*
				 * int hash = p == null ? regionHashes[playerIndex] :
				 * p.getRegionHash(); if (p != null && hash !=
				 * regionHashes[playerIndex]) { stream.writeBits(1, 1);
				 * updateRegionHash(stream, regionHashes[playerIndex], hash, p);
				 * regionHashes[playerIndex] = hash; } else {
				 */
				stream.writeBits(1, 0); // no update needed
				for (int i2 = i + 1; i2 < outPlayersIndexesCount; i2++) {
					int p2Index = outPlayersIndexes[i2];

					if (nsn2 ? (0x1 & slotFlags[p2Index]) == 0
							: (0x1 & slotFlags[p2Index]) != 0)
						continue;

					Player p2 = World.getPlayers().get(p2Index);
					if (needsAdd(p2)
							|| (p2 != null && p2.getRegionHash() != regionHashes[p2Index]))
						break;
					skip++;
				}
				skipPlayers(stream, skip);
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				// }
			}
		}
		stream.finishBitAccess();
	}

	private void processLocalPlayers(OutputStream stream,
			OutputStream updateBlockData, boolean nsn0) {
		stream.initBitAccess();
		int skip = 0;
		for (int i = 0; i < localPlayersIndexesCount; i++) {
			int playerIndex = localPlayersIndexes[i];

			if (nsn0 ? (0x1 & slotFlags[playerIndex]) != 0
					: (0x1 & slotFlags[playerIndex]) == 0)
				continue;

			if (skip > 0) {
				skip--;
				slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				continue;
			}
			Player p = localPlayers[playerIndex];
			if (needsRemove(p)) {
				stream.writeBits(1, 1); // needs update
				stream.writeBits(1, 0); // no masks update needeed
				stream.writeBits(2, 0); // request remove
				regionHashes[playerIndex] = p.getLastWorldTile() == null ? p
						.getRegionHash() : p.getLastWorldTile().getRegionHash();
				int hash = p.getRegionHash();
				if (hash == regionHashes[playerIndex])
					stream.writeBits(1, 0);
				else {
					stream.writeBits(1, 1);
					updateRegionHash(stream, regionHashes[playerIndex], hash,
							p.getMovementType());
					regionHashes[playerIndex] = hash;
					regionHashes[playerIndex] = hash;
				}
				localPlayers[playerIndex] = null;
			} else {
				boolean needAppearenceUpdate = needAppearenceUpdate(
						p.getIndex(), p.getAppearence()
								.getMD5AppeareanceDataHash());
				boolean needIconsUpdate = needIconsUpdate(p.getIndex(), p
						.getAppearence().getMD5IconsDataHash());
				boolean needUpdate = p.needMasksUpdate()
						|| needAppearenceUpdate
						|| needIconsUpdate
						|| player.getCombatDefinitions()
								.isNeedTargetReticuleUpdate(p);
				if (needUpdate)
					appendUpdateBlock(p, updateBlockData, needAppearenceUpdate,
							needIconsUpdate, false);
				if (p.hasTeleported() || p.getNextWalkDirection() != -1) {
					stream.writeBits(1, 1); // needs update
					stream.writeBits(1, needUpdate ? 1 : 0);
					stream.writeBits(2, 3);
					int xOffset = p.getX() - p.getLastWorldTile().getX();
					int yOffset = p.getY() - p.getLastWorldTile().getY();
					int planeOffset = p.getPlane()
							- p.getLastWorldTile().getPlane();
					int movementType = p.hasTeleported() ? 4 : p
							.getMovementType();
					if (Math.abs(p.getX() - p.getLastWorldTile().getX()) < 16 // 14
							&& Math.abs(p.getY() - p.getLastWorldTile().getY()) < 16) { // 14
						stream.writeBits(1, 0);
						if (xOffset < 0) // viewport used to be 15 now 16
							xOffset += 32;
						if (yOffset < 0)
							yOffset += 32;
						stream.writeBits(15, yOffset + (xOffset << 5)
								+ ((planeOffset & 0x3) << 10)
								| movementType << 12);// 4 forces
														// setmovementtype to
														// teleport for next
														// step
					} else {
						stream.writeBits(1, 1);
						stream.writeBits(3, movementType); // 4 forces
															// setmovementtype
															// to teleport for
															// next step
						stream.writeBits(30, (yOffset & 0x3fff)
								+ ((xOffset & 0x3fff) << 14)
								+ ((planeOffset & 0x3) << 28));
					}
					// not needed as teleport handles walk aswell
					/*
					 * } else if (p.getNextWalkDirection() != -1) { int dx =
					 * Utils.DIRECTION_DELTA_X[p.getNextWalkDirection()]; int dy
					 * = Utils.DIRECTION_DELTA_Y[p.getNextWalkDirection()]; int
					 * opcode; if (p.getNextRunDirection() != -1) { dx +=
					 * Utils.DIRECTION_DELTA_X[p.getNextRunDirection()]; dy +=
					 * Utils.DIRECTION_DELTA_Y[p.getNextRunDirection()]; opcode
					 * = Utils.getPlayerRunningDirection(dx, dy); }else { opcode
					 * = Utils.getPlayerWalkingDirection(dx, dy); }
					 * stream.writeBits(1, 1); if ((dx == 0 && dy == 0)) {
					 * stream.writeBits(1, needUpdate ? 1 : 0);
					 * stream.writeBits(2,p.getNextRunDirection() != -1 ? 2 :
					 * 1); stream.writeBits(p.getNextRunDirection() != -1 ? 4 :
					 * 3, opcode); if(p.getNextRunDirection() == -1)
					 * stream.writeBits(1, 0); }
					 */
				} else if (needUpdate) {
					stream.writeBits(1, 1); // needs update
					stream.writeBits(1, 1);
					stream.writeBits(2, 0);
				} else { // skip
					stream.writeBits(1, 0); // no update needed
					for (int i2 = i + 1; i2 < localPlayersIndexesCount; i2++) {
						int p2Index = localPlayersIndexes[i2];
						if (nsn0 ? (0x1 & slotFlags[p2Index]) != 0
								: (0x1 & slotFlags[p2Index]) == 0)
							continue;
						Player p2 = localPlayers[p2Index];
						if (needsRemove(p2)
								|| p2.hasTeleported()
								|| p2.getNextWalkDirection() != -1
								|| (p2.needMasksUpdate()
										|| needAppearenceUpdate(
												p2.getIndex(),
												p2.getAppearence()
														.getMD5AppeareanceDataHash()) || needIconsUpdate(
											p2.getIndex(), p2.getAppearence()
													.getMD5IconsDataHash())))
							break;
						skip++;
					}
					skipPlayers(stream, skip);
					slotFlags[playerIndex] = (byte) (slotFlags[playerIndex] | 2);
				}

			}
		}
		stream.finishBitAccess();
	}

	private void skipPlayers(OutputStream stream, int amount) {
		stream.writeBits(2, amount == 0 ? 0 : amount > 255 ? 3
				: (amount > 31 ? 2 : 1));
		if (amount > 0)
			stream.writeBits(amount > 255 ? 11 : (amount > 31 ? 8 : 5), amount);
	}

	private void appendUpdateBlock(Player p, OutputStream data,
			boolean needAppearenceUpdate, boolean needIconsUpdate, boolean added) {
		int maskData = 0;
		if (p.getNextAnimation() != null)
			maskData |= 0x8;
		if (!p.getNextHits().isEmpty() || !p.getNextHitBars().isEmpty())
			maskData |= 0x80;
		if (p.isRefreshClanIcon() || added)
			maskData |= 0x400000;
		if (p.getNextForceTalk() != null)
			maskData |= 0x800;
		if (needIconsUpdate)
			maskData |= 0x8000;
		if (needAppearenceUpdate)
			maskData |= 0x40;
		if ((added || p.getNextFaceWorldTile() != null)
				&& (p.getNextRunDirection() == -1
						&& p.getNextWalkDirection() == -1
						&& p.getNextForceMovement() == null
						&& p.getNextFaceEntity() < 0 && !(added && p
						.getLastFaceEntity() != -1)))
			maskData |= 0x2;
		if (p.getNextColour() != null)
			maskData |= 0x40000;
		if (player.getCombatDefinitions().isNeedTargetReticuleUpdate(p))
			maskData |= 0x80000;
		if (p.getNextForceMovement() != null)
			maskData |= 0x4;
		if (p.getNextGraphics4() != null)
			maskData |= 0x10000;
		if (p.getNextGraphics1() != null)
			maskData |= 0x1;
		if (p.getNextFaceEntity() != -2
				|| (added && p.getLastFaceEntity() != -1))
			maskData |= 0x10;
		if (p.getNextGraphics3() != null)
			maskData |= 0x400;
		if (p.getNextGraphics2() != null)
			maskData |= 0x100;
		if (maskData >= 0xff)
			maskData |= 0x20;
		if (maskData >= 0xffff)
			maskData |= 0x4000;

		data.writeShort(0); // rs doesnt use this lol
		data.writeByte(maskData);

		if (maskData >= 0xff)
			data.writeByte(maskData >> 8);
		if (maskData >= 0xffff)
			data.writeByte(maskData >> 16);

		if (p.getNextAnimation() != null)
			applyAnimationMask(p, data);
		if (!p.getNextHits().isEmpty() || !p.getNextHitBars().isEmpty())
			applyHitsMask(p, data);
		if (p.isRefreshClanIcon() || added)
			applyClanMemberMask(p, data);
		if (p.getNextForceTalk() != null)
			applyForceTalkMask(p, data);
		if (needIconsUpdate)
			applyIconsMask(p, data);
		if (needAppearenceUpdate)
			applyAppearanceMask(p, data);
		if ((added || p.getNextFaceWorldTile() != null)
				&& (p.getNextRunDirection() == -1
						&& p.getNextWalkDirection() == -1
						&& p.getNextForceMovement() == null
						&& p.getNextFaceEntity() < 0 && !(added && p
						.getLastFaceEntity() != -1)))
			applyFaceDirectionMask(p, data);
		if (p.getNextColour() != null)
			applyColourMask(p, data);
		if (player.getCombatDefinitions().isNeedTargetReticuleUpdate(p))
			applyTargetReticuleMask(p, data);
		if (p.getNextForceMovement() != null)
			applyForceMovementMask(p, data);
		if (p.getNextGraphics4() != null)
			applyGraphicsMask4(p, data);
		if (p.getNextGraphics1() != null)
			applyGraphicsMask1(p, data);
		if (p.getNextFaceEntity() != -2
				|| (added && p.getLastFaceEntity() != -1))
			applyFaceEntityMask(p, data);
		if (p.getNextGraphics3() != null)
			applyGraphicsMask3(p, data);
		if (p.getNextGraphics2() != null)
			applyGraphicsMask2(p, data);
	}

	private void applyColourMask(Player p, OutputStream data) {
		/**
		 * updateBuild.aByte11643 = buf.read128Byte(877571984);
		 * updateBuild.aByte11644 = buf.read128Byte(624278611);
		 * updateBuild.aByte11626 = buf.readByte128((byte) -106);
		 * updateBuild.aByte11605 = (byte) buf.readUnsigned128Byte((byte) 101);
		 * updateBuild.anInt11641 = (client.cycles +
		 * buf.readUnsignedShortLE128((byte) 8)) * 1008238467;
		 * updateBuild.anInt11642 = (client.cycles +
		 * buf.readUnsignedShortLE128((byte) 39)) * -1559016237;
		 */
		Colour color = p.getNextColour();
		int colours = color.getColours();
		data.write128Byte(colours & 0xFF);
		data.write128Byte(colours >> 8 & 0xFF);
		data.writeByte128(colours >> 16 & 0xFF);
		data.write128Byte(colours >> 24 & 0xFF);
		data.writeShortLE128(color.getDelay());
		data.writeShortLE128(color.getDuration());
	}

	private void applyForceTalkMask(Player p, OutputStream data) {
		data.writeString(p.getNextForceTalk().getText());
	}

	private void applyHitsMask(Player p, OutputStream data) {
		data.writeByte(p.getNextHits().size());
		for (Hit hit : p.getNextHits()) {
			boolean interactingWith = hit.interactingWith(player, p);
			if (hit.missed() && !interactingWith) {
				data.writeSmart(32766);
				data.writeByteC(hit.getDamageDisplay(player)); // dont ask me
																// why, 32766
																// sets dmg but
																// no hitmark.
			} else {
				if (hit.getSoaking() != null) {
					data.writeSmart(32767);
					data.writeSmart(hit.getMark(player, p));
					data.writeSmart(hit.getDamageDisplay(player));
					data.writeSmart(hit.getSoaking().getMark(player, p));
					data.writeSmart(hit.getSoaking().getDamageDisplay(player));
				} else {
					data.writeSmart(hit.getMark(player, p));
					data.writeSmart(hit.getDamageDisplay(player));
				}
			}
			data.writeSmart(hit.getDelay());
		}
		data.writeByte128(p.getNextHitBars().size());
		for (HitBar bar : p.getNextHitBars()) {
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

	private void applyFaceEntityMask(Player p, OutputStream data) {
		data.writeShort(p.getNextFaceEntity() == -2 ? p.getLastFaceEntity() : p
				.getNextFaceEntity());

	}

	private void applyFaceDirectionMask(Player p, OutputStream data) {
		data.writeShort128(p.getDirection()); // also works as face tile as dir
		// calced on setnextfacetile
	}

	private void applyClanMemberMask(Player p, OutputStream data) {
		data.write128Byte((player.getClanManager() != null && player
				.getClanManager().isMemberOnline(p)) ? 1 : 0);
	}

	private void applyGraphicsMask1(Player p, OutputStream data) {
		data.writeShort(p.getNextGraphics1().getId());
		data.writeInt(p.getNextGraphics1().getSettingsHash());
		data.writeByte128(p.getNextGraphics1().getSettings2Hash());
	}

	private void applyGraphicsMask2(Player p, OutputStream data) {
		data.writeShortLE128(p.getNextGraphics2().getId());
		data.writeIntLE(p.getNextGraphics2().getSettingsHash());
		data.writeByte(p.getNextGraphics2().getSettings2Hash());
	}

	private void applyGraphicsMask3(Player p, OutputStream data) {
		data.writeShortLE128(p.getNextGraphics3().getId());
		data.writeIntV1(p.getNextGraphics3().getSettingsHash());
		data.writeByte(p.getNextGraphics3().getSettings2Hash());
	}

	private void applyGraphicsMask4(Player p, OutputStream data) {
		data.writeShort(p.getNextGraphics4().getId());
		data.writeIntV2(p.getNextGraphics4().getSettingsHash());
		data.write128Byte(p.getNextGraphics4().getSettings2Hash());
	}

	private void applyTargetReticuleMask(Player p, OutputStream data) {
		Graphics reticle = player.getCombatDefinitions().getTargetReticule(p);
		data.writeShortLE(reticle.getId());
		data.writeIntLE(reticle.getSettingsHash());
		data.writeByte128(reticle.getSettings2Hash());
	}

	private void applyAnimationMask(Player p, OutputStream data) {
		for (int id : p.getNextAnimation().getIds())
			data.writeBigSmart(id);
		data.writeByteC(p.getNextAnimation().getDelay());
	}

	private void applyAppearanceMask(Player p, OutputStream data) {
		byte[] renderData = p.getAppearence().getAppeareanceData();
		totalRenderDataSentLength += renderData.length;
		cachedAppearencesHashes[p.getIndex()] = p.getAppearence()
				.getMD5AppeareanceDataHash();

		data.writeByteC(renderData.length);
		data.writeBytes128(renderData);
	}

	private void applyIconsMask(Player p, OutputStream data) {
		byte[] renderData = p.getAppearence().getIconsData();
		totalRenderDataSentLength += renderData.length;
		cachedIconsHashes[p.getIndex()] = p.getAppearence()
				.getMD5IconsDataHash();
		data.writeByteC(renderData.length);
		data.writeBytesReverse(renderData);
	}

	private void applyForceMovementMask(Player p, OutputStream data) {
		data.writeByteC(p.getNextForceMovement().getToFirstTile().getX()
				- p.getX());
		data.writeByte(p.getNextForceMovement().getToFirstTile().getY()
				- p.getY());
		data.writeByteC(p.getNextForceMovement().getToSecondTile() == null ? 0
				: p.getNextForceMovement().getToSecondTile().getX() - p.getX());
		data.writeByteC(p.getNextForceMovement().getToSecondTile() == null ? 0
				: p.getNextForceMovement().getToSecondTile().getY() - p.getY());
		data.writeShortLE(p.getNextForceMovement().getFirstTileTicketDelay() * 30);
		data.writeShort128(p.getNextForceMovement().getToSecondTile() == null ? 0
				: (p.getNextForceMovement().getSecondTileTicketDelay() * 30));
		data.writeShort(p.getNextForceMovement().getDirection());
	}

	public OutputStream createPacketAndProcess() {
		OutputStream stream = new OutputStream();
		OutputStream updateBlockData = new OutputStream();
		stream.writePacketVarShort(player, 21);
		processLocalPlayers(stream, updateBlockData, true);
		processLocalPlayers(stream, updateBlockData, false);
		processOutsidePlayers(stream, updateBlockData, true);
		processOutsidePlayers(stream, updateBlockData, false);
		stream.writeBytes(updateBlockData.getBuffer(), 0,
				updateBlockData.getOffset());
		stream.endPacketVarShort();

		totalRenderDataSentLength = 0;
		localPlayersIndexesCount = 0;
		outPlayersIndexesCount = 0;
		for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
			slotFlags[playerIndex] >>= 1;
			Player player = localPlayers[playerIndex];
			if (player == null)
				outPlayersIndexes[outPlayersIndexesCount++] = playerIndex;
			else
				localPlayersIndexes[localPlayersIndexesCount++] = playerIndex;
		}
		return stream;
	}

}