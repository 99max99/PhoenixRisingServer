package net.kagani.game.player;

import net.kagani.Settings;
import net.kagani.cache.loaders.QuickChatOptionDefinition;
import net.kagani.stream.OutputStream;

public class QuickChatMessage extends PublicChatMessage {

	private QuickChatOptionDefinition option;
	private byte[] encoded;

	public QuickChatMessage(Player player, QuickChatOptionDefinition option,
			long[] data) {
		super(null, 0x8000);
		this.option = option;
		OutputStream output = new OutputStream(option.getTotalResponseSize());
		writeQuickChatData(player, output, option, data);
		encoded = output.getBuffer();
	}

	public QuickChatOptionDefinition getDefinition() {
		return option;
	}

	public byte[] getEncoded() {
		return encoded;
	}

	public static void writeQuickChatData(Player player, OutputStream message,
			QuickChatOptionDefinition option, long[] data) {
		if (option.dynamicDataTypes != null) {
			for (int i = 0; i < option.dynamicDataTypes.length; i++) {
				QuickChatOptionDefinition.QuickChatStringType type = option
						.getType(i);
				long value = 0;
				switch (type.id) {
				case 0: // General purpose datamap lookup, just send value back
				case 1: // Item search
				case 10: // Item search (tradeable?)
					value = data[i]; // Just broadcast the data
					break;
				case 2:
					// There doesn't seem to be any options that use this (in
					// 667), although it is a valid id
					if (Settings.DEBUG)
						System.out.println("unknown quickchat option type 2 "
								+ data[i]); // it
											// is
											// supposed
											// to
											// send/recv
											// 4
											// bytes
					break;
				case 4: // Skill lookup, client sends 1 byte but always 0?
					value = player.getSkills().getLevelForXp(
							option.staticData[i][0]);
					break;
				case 6: // Slayer assignment lookup
					if (option.staticData[i][1] == 395) { // This should always
															// be true as there
															// is only one such
															// option (in 667)
						value = 0; // TODO: player.getSlayer... //as per datamap
									// 1563
						break;
					}
					// Only option in 667
					if (Settings.DEBUG)
						System.out.println("Unknown slayer lookup: "
								+ option.staticData[i][1]);
					break;
				case 7: // There doesn't seem to be any options that use this,
						// although it is a valid id
					if (Settings.DEBUG)
						System.out.println("unknown quickchat option type 7 "
								+ option.staticData[i][0]); // server
															// is
															// supposed
															// to
															// send
															// 1
															// byte,
															// and
															// has
															// 1
															// static
															// data
															// id,
															// no
															// data
															// received
					break;
				case 8: // 'Rank' lookup
					switch (option.staticData[i][0]) {
					case 1405:
						value = 0; // TODO: Fist of guthix rating
						break;
					case 2225:
						value = 0; // TODO: Loyalty points
						break;
					case 1447:
						value = 0; // TODO: Mobilising armies rank
						break;
					case 1448:
						value = 0; // TODO: Mobilising armies reward credit
						break;
					case 101:
						value = 0; // TODO: Quest points
						break;
					case 2421:
						value = player.getDominionTower().getTotalScore();
						break;
					case 2639:
						value = player.getCrucibleHighScore();
						break;
					default:
						// These are all options in 667
						if (Settings.DEBUG)
							System.out.println("unkown option type 8 "
									+ option.staticData[i][0]);
						break;
					}
					break;
				case 9: // 'Points' lookup, these seem to be more prone to
						// changes than type 8 (both send an int back)
					switch (option.staticData[i][0]) {
					case 4562:
						value = 0; // Fist of guthix hunted charges
						break;
					case 5505:
						value = player.getStealingCreationPoints();
						break;
					case 5276:
						value = 0; // Penguins spotted
						break;
					case 6351:
						value = 0; // Mobilising armies investment credits
						break;
					case 6764:
						value = 0; // Champion challenges completed
						break;
					case 7093:
						value = 0; // Familiarisation piles of raw shards
						break;
					case 7198:
						value = player.getHitpoints();
						break;
					case 7550:
						value = 0; // Dungeon floor
						break;
					case 9065:
						value = 0; // Livid farm produce
						break;
					case 9067:
						value = 0; // Livid farm spells unlocked
						break;
					case 10057:
						value = player.getDominionTower()
								.getKilledBossesCount();
						break;
					case 10118:
						value = player.getDominionTower().getMaxFloorClimber();
						break;
					case 10119:
						value = player.getDominionTower()
								.getMaxFloorEndurance();
						break;

					default:
						// These are all options in 667
						if (Settings.DEBUG)
							System.out.println("unkown option type 9 "
									+ option.staticData[i][0]);
						break;
					}
					break;
				case 11: // Skill lookup for next level & xp table datamap
					value = player.getSkills().getLevelForXp(
							option.staticData[i][1]);
					break;
				case 12:
					value = 0; // Friend chat player count
					break;
				case 13:
					value = 0; // Clan wars average combat level
					break;
				case 14: // Soul wars data lookup
					switch (option.staticData[i][0]) {
					case 850:
						value = 0; // Blue avatar level
						break;
					case 851:
						value = 0; // Red avatar level
						break;
					case 866:
						value = 0; // Blue avatar health %
						break;
					case 867:
						value = 0; // Red avatar health %
						break;
					default:
						if (Settings.DEBUG)
							// These are all the options in 667
							System.out.println("unknown soul wars option "
									+ option.staticData[i][0]);
						break;
					}
					break;
				case 15: // Self combat level lookup
					value = player.getSkills().getCombatLevel();
					break;
				}
				message.writeDynamic(type.serverToClientBytes, value);
			}
		}
	}
}