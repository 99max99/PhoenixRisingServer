package net.kagani.game.player.content;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.game.Graphics;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Color;
import net.kagani.utils.Utils;

public class DailyTasks implements Serializable {

	public enum Tasks {
		SKILLING(
				1,
				new Object[][] {
						// description, level, task amount, reward id, reward
						// amount, skill reward, skill exp
						/**
						 * Mining - handled
						 */
						{ "Runite ore", 85, 50, 452, 50, Skills.MINING, 15000 },
						{ "Adamantite ore", 70, 65, 450, 65, Skills.MINING,
								12500 },
						{ "Mithril ore", 55, 85, 448, 85, Skills.MINING, 10000 },
						{ "Gold ore", 40, 95, 445, 95, Skills.MINING, 8000 },
						{ "Coal", 30, 120, 454, 120, Skills.MINING, 6500 },
						{ "Silver ore", 20, 150, 443, 150, Skills.MINING, 5000 },
						{ "Pure essence", 30, 250, 7937, 500, Skills.MINING,
								6500 },
						{ "Iron ore", 15, 175, 441, 175, Skills.MINING, 5000 },
						{ "Copper ore", 1, 200, 437, 200, Skills.MINING, 2500 },
						/**
						 * Woodcutting - handled
						 */
						{ "Magic", 75, 50, 1514, 50, Skills.WOODCUTTING, 15000 },
						{ "Yew", 60, 65, 1516, 65, Skills.WOODCUTTING, 12500 },
						{ "Maple", 45, 85, 1518, 85, Skills.WOODCUTTING, 10000 },
						{ "Willow", 30, 100, 1520, 100, Skills.WOODCUTTING,
								8000 },
						{ "Oak", 15, 175, 1522, 150, Skills.WOODCUTTING, 5000 },
						/**
						 * Firemaking - bonfiring and reg handled
						 */
						{ "Magic logs", 75, 100, 1514, 100, Skills.FIREMAKING,
								15000 },
						{ "Yew logs", 60, 150, 1516, 150, Skills.FIREMAKING,
								12500 },
						{ "Mahogany logs", 50, 175, 8836, 175,
								Skills.FIREMAKING, 10000 },
						{ "Maple logs", 45, 175, 1518, 175, Skills.FIREMAKING,
								9000 },
						{ "Willow logs", 30, 200, 1520, 200, Skills.FIREMAKING,
								7500 },
						{ "Oak logs", 15, 215, 1522, 215, Skills.FIREMAKING,
								4000 },
						/**
						 * Farming - handled
						 */
						// allotments
						{ "Potato seed", 1, 5, 1943, 5, Skills.FARMING, 1000 },
						{ "Onion seed", 5, 5, 1958, 5, Skills.FARMING, 2000 },
						{ "Cabbage seed", 7, 5, 1966, 5, Skills.FARMING, 2250 },
						{ "Tomato seed", 12, 4, 1983, 4, Skills.FARMING, 3000 },
						{ "Sweetcorn seed", 20, 4, 5987, 4, Skills.FARMING,
								3500 },
						{ "Strawberry seed", 31, 3, 5505, 3, Skills.FARMING,
								4750 },
						{ "Watermelon seed", 47, 3, 5983, 3, Skills.FARMING,
								5500 },
						// flowers
						{ "Marigold seed", 2, 5, 6011, 5, Skills.FARMING, 1250 },
						{ "Rosemary seed", 11, 4, 6015, 4, Skills.FARMING, 2750 },
						{ "Nasturtium seed", 24, 3, 6013, 3, Skills.FARMING,
								3750 },
						{ "Woad seed", 25, 3, 1793, 3, Skills.FARMING, 4000 },
						{ "Limpwurt seed", 26, 3, 225, 3, Skills.FARMING, 4250 },
						{ "White lily seed", 52, 1, 14583, 1, Skills.FARMING,
								10000 },
						// herbs
						{ "Guam seed", 9, 5, 200, 5, Skills.FARMING, 2500 },
						{ "Marrentill seed", 14, 4, 202, 4, Skills.FARMING,
								3000 },
						{ "Tarromin seed", 19, 4, 204, 4, Skills.FARMING, 3500 },
						{ "Harralander seed", 26, 3, 206, 3, Skills.FARMING,
								4250 },
						{ "Ranarr seed", 32, 3, 208, 3, Skills.FARMING, 5750 },
						{ "Spirit weed seed", 36, 3, 12175, 3, Skills.FARMING,
								6250 },
						{ "Toadflax seed", 38, 3, 3050, 3, Skills.FARMING, 6500 },
						{ "Irit seed", 44, 3, 210, 3, Skills.FARMING, 7250 },
						{ "Wergali seed", 46, 3, 14837, 3, Skills.FARMING, 7650 },
						{ "Avantoe seed", 50, 2, 212, 2, Skills.FARMING, 9250 },
						{ "Kwuarm seed", 56, 2, 214, 2, Skills.FARMING, 12500 },
						{ "Snapdragon seed", 62, 2, 3052, 2, Skills.FARMING,
								15250 },
						{ "Dwarf weed seed", 79, 2, 218, 2, Skills.FARMING,
								22500 },
						{ "Torstol seed", 85, 1, 220, 1, Skills.FARMING, 26500 },
						{ "Fellstalk seed", 91, 1, 21627, 1, Skills.FARMING,
								32500 },
						// saplings
						{ "Oak sapling", 15, 1, 1522, 5, Skills.FARMING, 3125 },
						{ "Willow sapling", 30, 1, 1520, 10, Skills.FARMING,
								5250 },
						{ "Maple sapling", 45, 1, 1518, 15, Skills.FARMING,
								7500 },
						{ "Yew sapling", 60, 1, 1516, 20, Skills.FARMING, 15000 },
						{ "Magic sapling", 75, 1, 1514, 30, Skills.FARMING,
								18250 },

						/**
						 * Herblore - handled
						 */
						// overloads && extremes
						{ "Overload (3)", 96, 20, 15333, 20, Skills.HERBLORE,
								30000 },
						{ "Extreme ranging (3)", 92, 23, 15325, 20,
								Skills.HERBLORE, 27500 },
						{ "Extreme magic (3)", 91, 25, 15321, 25,
								Skills.HERBLORE, 25000 },
						{ "Extreme defence (3)", 90, 27, 15317, 27,
								Skills.HERBLORE, 23500 },
						{ "Extreme strength (3)", 89, 29, 15313, 29,
								Skills.HERBLORE, 22000 },
						// other pots
						// required amount is going to be random from here (too
						// many potions)
						{ "Super antifire (3)", 85, 32, 15305, 32,
								Skills.HERBLORE, 20000 },
						{ "Saradomin brew (3)", 81, 35, 6688, 35,
								Skills.HERBLORE, 19500 },
						{ "Zamorak brew (3)", 78, 35, 190, 35, Skills.HERBLORE,
								19250 },
						{ "Magic potion (3)", 76, 38, 3043, 38,
								Skills.HERBLORE, 18500 }, // (super magic
															// potion)
						{ "Ranging potion (3)", 72, 38, 170, 38,
								Skills.HERBLORE, 18250 }, // (super ranged
															// potion)
						{ "Antifire (3)", 69, 40, 2455, 40, Skills.HERBLORE,
								18000 },
						{ "Super defence (3)", 66, 45, 164, 45,
								Skills.HERBLORE, 17750 },
						{ "Super restore (3)", 63, 50, 3027, 50,
								Skills.HERBLORE, 17450 },
						{ "Super strength (3)", 55, 45, 158, 45,
								Skills.HERBLORE, 17300 },
						{ "Super energy (3)", 52, 43, 3019, 43,
								Skills.HERBLORE, 17400 },
						{ "Super attack (3)", 45, 45, 146, 45, Skills.HERBLORE,
								17000 },
						{ "Prayer potion (3)", 38, 55, 140, 55,
								Skills.HERBLORE, 16500 },
						{ "Energy potion (3)", 26, 60, 3011, 60,
								Skills.HERBLORE, 12500 },
						{ "Defence potion (3)", 5, 70, 134, 70,
								Skills.HERBLORE, 7500 },
						{ "Strength potion (3)", 3, 75, 116, 75,
								Skills.HERBLORE, 5000 },
						{ "Attack potion (3)", 1, 80, 122, 80, Skills.HERBLORE,
								2500 },
						/**
						 * Cooking - handled
						 */
						{ "Raw Rocktail", 93, 40, 15273, 40, Skills.COOKING,
								20000 },
						{ "Raw Cavefish", 88, 50, 15267, 50, Skills.COOKING,
								17500 },
						{ "Raw Shark", 80, 55, 386, 55, Skills.COOKING, 15000 },
						{ "Raw Monkfish", 62, 60, 7947, 60, Skills.COOKING,
								12500 },
						{ "Raw Swordfish", 45, 75, 374, 75, Skills.COOKING,
								10000 },
						{ "Raw Lobster", 40, 90, 380, 90, Skills.COOKING, 8000 },
						{ "Raw Tuna", 30, 115, 362, 115, Skills.COOKING, 7500 },
						{ "Raw Anchovies", 1, 130, 320, 130, Skills.COOKING,
								5000 },
						{ "Raw Shrimps", 1, 150, 316, 150, Skills.COOKING, 2500 },
						/**
						 * Fishing - handled
						 */
						{ "Rocktail", 90, 40, 15271, 40, Skills.FISHING, 20000 },
						{ "Cavefish", 85, 50, 15265, 50, Skills.FISHING, 17500 },
						{ "Shark", 76, 55, 384, 50, Skills.FISHING, 15000 },
						// {"Monkfish", 60, 60, 7945, 60, Skills.FISHING,
						// 12500}, // add monkfish before making this
						// live//disabled temp
						{ "Swordfish", 50, 75, 372, 75, Skills.FISHING, 10000 },
						{ "Lobster", 40, 90, 378, 90, Skills.FISHING, 8000 },
						{ "Tuna", 35, 115, 360, 115, Skills.FISHING, 7500 },
						{ "Anchovies", 15, 130, 322, 130, Skills.FISHING, 5000 },
						{ "Shrimps", 1, 150, 318, 150, Skills.FISHING, 2500 },

						/**
						 * Smithing - handled
						 */
						// smelting - handled
						{ "Rune bar", 85, 100, 2364, 100, Skills.SMITHING,
								17500 },// runite bars
						{ "Adamant bar", 70, 125, 2362, 125, Skills.SMITHING,
								15000 },// adamant abrs
						{ "Mithril bar", 50, 150, 2360, 150, Skills.SMITHING,
								13000 },// mithril bar
						{ "Gold bar", 40, 175, 2358, 175, Skills.SMITHING,
								11000 },// gold bars
						{ "Steel bar", 30, 200, 2354, 200, Skills.SMITHING,
								9000 },// steel bars
						{ "Silver bar", 20, 225, 2356, 225, Skills.SMITHING,
								7000 },// silver bars
						{ "Iron bar", 15, 235, 2352, 235, Skills.SMITHING, 5000 },// iron
																					// bars
						{ "Bronze bar", 1, 240, 2350, 240, Skills.SMITHING,
								3500 },// bronze bars
						// handled below
						// rune
						// armour
						{ "Rune platebody", 99, 20, 1128, 20, Skills.SMITHING,
								20000 },// 3 bars
						{ "Rune chainbody", 96, 20, 1320, 20, Skills.SMITHING,
								20000 },// 3 bars
						{ "Rune platelegs", 99, 30, 1080, 30, Skills.SMITHING,
								20000 },// 3 bars
						{ "Rune plateskirt", 99, 30, 1094, 30, Skills.SMITHING,
								20000 },// 3 bars
						{ "Rune helm", 88, 50, 1148, 50, Skills.SMITHING, 20000 },// 1
																					// bar
						{ "Rune sq shield", 93, 20, 1186, 20, Skills.SMITHING,
								20000 },// 3 bars
						{ "Rune kiteshield", 97, 20, 1202, 20, Skills.SMITHING,
								20000 },// 3 bars
						// weapons
						{ "Rune hatchet", 86, 50, 1360, 50, Skills.SMITHING,
								20000 },// 1 bar
						{ "Rune pickaxe", 90, 25, 1276, 25, Skills.SMITHING,
								20000 },// 2 bars
						{ "Rune knife", 92, 50, 868, 250, Skills.SMITHING,
								20000 },// 1 bar - each bar gives 5 knives
						{ "Rune warhammer", 94, 20, 1348, 20, Skills.SMITHING,
								20000 },// 3 bars
						{ "Rune 2h sword", 99, 50, 1320, 50, Skills.SMITHING,
								20000 },// 1 bar
						{ "Rune scimitar", 90, 25, 1334, 25, Skills.SMITHING,
								20000 },// 2 bars
						{ "Rune mace", 87, 50, 1433, 50, Skills.SMITHING, 20000 },// 1
																					// bar
						{ "Rune dagger", 85, 50, 1214, 50, Skills.SMITHING,
								20000 },// 1 bar
						{ "Rune longsword", 89, 50, 1304, 50, Skills.SMITHING,
								20000 },// 1 bar
						// adamant
						// armour
						{ "Adamant platebody", 88, 30, 1124, 30,
								Skills.SMITHING, 17500 },// 3 bars
						{ "Adamant chainbody", 81, 30, 1112, 30,
								Skills.SMITHING, 17500 },// 3 bars
						{ "Adamant platelegs", 86, 40, 1074, 40,
								Skills.SMITHING, 17500 },// 3 bars
						{ "Adamant plateskirt", 86, 40, 1092, 40,
								Skills.SMITHING, 17500 },// 3 bars
						{ "Adamant helm", 73, 60, 1146, 60, Skills.SMITHING,
								17500 },// 1 bar
						{ "Adamant sq shield", 78, 25, 1184, 25,
								Skills.SMITHING, 17500 },// 3 bars
						{ "Adamant kiteshield", 82, 25, 1200, 25,
								Skills.SMITHING, 17500 },// 3 bars
						// weapons
						{ "Adamant hatchet", 80, 60, 1358, 60, Skills.SMITHING,
								15000 },// 1 bar
						{ "Adamant pickaxe", 75, 35, 1272, 35, Skills.SMITHING,
								15000 },// 2 bars
						{ "Adamant knife", 77, 60, 867, 300, Skills.SMITHING,
								15000 },// 1 bar - each bar gives 5 knives
						{ "Adamant warhammer", 79, 30, 1346, 30,
								Skills.SMITHING, 15000 },// 3 bars
						{ "Adamant 2h sword", 84, 60, 1318, 60,
								Skills.SMITHING, 15000 },// 1 bar
						{ "Adamant scimitar", 75, 35, 1332, 35,
								Skills.SMITHING, 15000 },// 2 bars
						{ "Adamant mace", 72, 60, 1431, 60, Skills.SMITHING,
								15000 },// 1 bar
						{ "Adamant dagger", 70, 60, 1212, 60, Skills.SMITHING,
								15000 },// 1 bar
						{ "Adamant longsword", 76, 60, 1302, 60,
								Skills.SMITHING, 15000 },// 1 bar
						// mithril
						// armour
						{ "Mithril platebody", 68, 40, 1122, 40,
								Skills.SMITHING, 15000 },// 3 bars
						{ "Mithril chainbody", 61, 40, 1110, 40,
								Skills.SMITHING, 15000 },// 3 bars
						{ "Mithril platelegs", 66, 60, 1072, 60,
								Skills.SMITHING, 15000 },// 3 bars
						{ "Mithril plateskirt", 66, 60, 1086, 60,
								Skills.SMITHING, 15000 },// 3 bars
						{ "Mithril helm", 53, 70, 1144, 70, Skills.SMITHING,
								15000 },// 1 bar
						{ "Mithril sq shield", 58, 40, 1182, 40,
								Skills.SMITHING, 15000 },// 3 bars
						{ "Mithril kiteshield", 62, 40, 1198, 40,
								Skills.SMITHING, 15000 },// 3 bars
						// weapons
						{ "Mithril hatchet", 51, 70, 1356, 70, Skills.SMITHING,
								15000 },// 1 bar
						{ "Mithril pickaxe", 55, 45, 1274, 45, Skills.SMITHING,
								15000 },// 2 bars
						{ "Mithril knife", 57, 70, 866, 350, Skills.SMITHING,
								15000 },// 1 bar - each bar gives 5 knives
						{ "Mithril warhammer", 59, 40, 1344, 40,
								Skills.SMITHING, 15000 },// 3 bars
						{ "Mithril 2h sword", 64, 70, 1316, 70,
								Skills.SMITHING, 15000 },// 1 bar
						{ "Mithril scimitar", 55, 45, 1330, 45,
								Skills.SMITHING, 15000 },// 2 bars
						{ "Mithril mace", 52, 70, 1429, 70, Skills.SMITHING,
								15000 },// 1 bar
						{ "Mithril dagger", 50, 70, 1210, 70, Skills.SMITHING,
								15000 },// 1 bar
						{ "Mithril longsword", 56, 70, 1300, 70,
								Skills.SMITHING, 15000 },// 1 bar
						// steel
						// armour
						{ "Steel platebody", 48, 55, 1120, 55, Skills.SMITHING,
								12500 },// 3 bars
						{ "Steel chainbody", 41, 55, 1106, 55, Skills.SMITHING,
								12500 },// 3 bars
						{ "Steel platelegs", 46, 70, 1070, 70, Skills.SMITHING,
								12500 },// 3 bars
						{ "Steel plateskirt", 46, 70, 1084, 70,
								Skills.SMITHING, 12500 },// 3 bars
						{ "Steel helm", 33, 80, 1142, 80, Skills.SMITHING,
								12500 },// 1 bar
						{ "Steel sq shield", 38, 55, 1178, 55, Skills.SMITHING,
								12500 },// 3 bars
						{ "Steel kiteshield", 42, 55, 1194, 55,
								Skills.SMITHING, 12500 },// 3 bars
						// weapons
						{ "Steel hatchet", 31, 80, 1354, 80, Skills.SMITHING,
								12500 },// 1 bar
						{ "Steel pickaxe", 35, 65, 1270, 65, Skills.SMITHING,
								12500 },// 2 bars
						{ "Steel knife", 37, 80, 865, 400, Skills.SMITHING,
								12500 },// 1 bar - each bar gives 5 knives
						{ "Steel warhammer", 39, 50, 1340, 50, Skills.SMITHING,
								12500 },// 3 bars
						{ "Steel 2h sword", 44, 80, 1312, 80, Skills.SMITHING,
								12500 },// 1 bar
						{ "Steel scimitar", 35, 55, 1326, 55, Skills.SMITHING,
								12500 },// 2 bars
						{ "Steel mace", 32, 80, 1425, 80, Skills.SMITHING,
								12500 },// 1 bar
						{ "Steel dagger", 30, 80, 1208, 80, Skills.SMITHING,
								12500 },// 1 bar
						{ "Steel longsword", 36, 80, 1296, 80, Skills.SMITHING,
								12500 },// 1 bar
						// iron
						// armour
						{ "Iron platebody", 33, 65, 1116, 65, Skills.SMITHING,
								8000 },// 3 bars
						{ "Iron chainbody", 26, 65, 1102, 65, Skills.SMITHING,
								8000 },// 3 bars
						{ "Iron platelegs", 31, 80, 1068, 80, Skills.SMITHING,
								8000 },// 3 bars
						{ "Iron plateskirt", 31, 80, 1082, 80, Skills.SMITHING,
								8000 },// 3 bars
						{ "Iron helm", 18, 90, 1138, 90, Skills.SMITHING, 8000 },// 1
																					// bar
						{ "Iron sq shield", 23, 65, 1176, 65, Skills.SMITHING,
								8000 },// 3 bars
						{ "Iron kiteshield", 27, 65, 1192, 65, Skills.SMITHING,
								8000 },// 3 bars
						// weapons
						{ "Iron hatchet", 16, 90, 1350, 90, Skills.SMITHING,
								8000 },// 1 bar
						{ "Iron pickaxe", 20, 75, 1268, 75, Skills.SMITHING,
								8000 },// 2 bars
						{ "Iron knife", 22, 90, 863, 450, Skills.SMITHING, 8000 },// 1
																					// bar
																					// -
																					// each
																					// bar
																					// gives
																					// 5
																					// knives
						{ "Iron warhammer", 24, 60, 1336, 60, Skills.SMITHING,
								8000 },// 3 bars
						{ "Iron 2h sword", 29, 90, 1310, 90, Skills.SMITHING,
								8000 },// 1 bar
						{ "Iron scimitar", 20, 65, 1324, 65, Skills.SMITHING,
								8000 },// 2 bars
						{ "Iron mace", 17, 90, 1421, 90, Skills.SMITHING, 8000 },// 1
																					// bar
						{ "Iron dagger", 15, 90, 1204, 90, Skills.SMITHING,
								8000 },// 1 bar
						{ "Iron longsword", 21, 90, 1294, 90, Skills.SMITHING,
								8000 },// 1 bar
						// bronze
						// armour
						{ "Bronze platebody", 18, 75, 1118, 75,
								Skills.SMITHING, 4500 },// 3 bars
						{ "Bronze chainbody", 11, 75, 1104, 75,
								Skills.SMITHING, 4500 },// 3 bars
						{ "Bronze platelegs", 16, 90, 1076, 90,
								Skills.SMITHING, 4500 },// 3 bars
						{ "Bronze plateskirt", 16, 90, 1088, 90,
								Skills.SMITHING, 4500 },// 3 bars
						{ "Bronze helm", 1, 100, 1140, 100, Skills.SMITHING,
								4500 },// 1 bar
						{ "Bronze sq shield", 8, 75, 1174, 75, Skills.SMITHING,
								4500 },// 3 bars
						{ "Bronze kiteshield", 12, 75, 1190, 75,
								Skills.SMITHING, 4500 },// 3 bars
						// weapons
						{ "Bronze hatchet", 1, 100, 1352, 100, Skills.SMITHING,
								4500 },// 1 bar
						{ "Bronze pickaxe", 5, 85, 1266, 85, Skills.SMITHING,
								4500 },// 2 bars
						{ "Bronze knife", 7, 100, 864, 500, Skills.SMITHING,
								4500 },// 1 bar - each bar gives 5 knives
						{ "Bronze warhammer", 9, 70, 1338, 70, Skills.SMITHING,
								4500 },// 3 bars
						{ "Bronze 2h sword", 14, 100, 1308, 100,
								Skills.SMITHING, 4500 },// 1 bar
						{ "Bronze scimitar", 5, 75, 1322, 75, Skills.SMITHING,
								4500 },// 2 bars
						{ "Bronze mace", 2, 100, 1423, 100, Skills.SMITHING,
								4500 },// 1 bar
						{ "Bronze dagger", 1, 100, 1206, 100, Skills.SMITHING,
								4500 },// 1 bar
						{ "Bronze longsword", 6, 100, 1292, 100,
								Skills.SMITHING, 4500 },// 1 bar

						/**
						 * Summoning
						 */
						// really can't be fucked
						// 168 = 5 inventories = max (for now)
						// {"Spirit wolf pouch", 1, 168, 12048, 168,
						// Skills.SUMMONING, 1000},
						// {"Dreadfowl pouch", 4, 150, 12044, 150,
						// Skills.SUMMONING, 1250},

						/**
						 * Thieving - handled
						 */
						// npcs - handled
						{ "Man", 1, 120, 995, 10000, Skills.THIEVING, 2750 },
						{ "Guard", 40, 85, 995, 75000, Skills.THIEVING, 7750 },
						{ "Paladin", 70, 50, 995, 200000, Skills.THIEVING,
								12500 },
						{ "Hero", 80, 40, 995, 300000, Skills.THIEVING, 15000 },
						{ "Trader", 90, 30, 995, 500000, Skills.THIEVING, 20000 },
						// stalls - handled
						{ "Baker's stall", 5, 170, 995, 15000, Skills.THIEVING,
								3000 },
						{ "Silk stall", 20, 160, 995, 25000, Skills.THIEVING,
								4000 },
						{ "Fur stall", 35, 150, 995, 45000, Skills.THIEVING,
								6000 },
						{ "Silver stall", 50, 120, 995, 75000, Skills.THIEVING,
								7000 },
						{ "Spice stall", 65, 90, 995, 150000, Skills.THIEVING,
								9000 },
						{ "Gem stall", 75, 70, 995, 200000, Skills.THIEVING,
								12000 },

						/**
						 * Crafting - handled
						 */
						// clay - handled
						{ "Pot (unfired)", 1, 100, 1788, 100, Skills.CRAFTING,
								2000 },// pot (unf)
						{ "Pie dish (unfired)", 7, 150, 1790, 150,
								Skills.CRAFTING, 2500 },// pie dish unf
						{ "Bowl (unfired)", 8, 175, 1792, 175, Skills.CRAFTING,
								3000 },// bowls
						{ "Unfired plant pot", 19, 200, 5353, 200,
								Skills.CRAFTING, 3500 },// plant pot
						{ "Unfired pot lid", 1, 100, 4439, 100,
								Skills.CRAFTING, 2000 },
						// gems - handled
						// {"Uncut onyx", 67, 3, 6574, 3, Skills.CRAFTING,
						// 20000},//also adding 30 uncut onyx would be op af :p
						{ "Uncut dragonstone", 55, 50, 1616, 50,
								Skills.CRAFTING, 17500 },
						{ "Uncut diamond", 43, 60, 1602, 60, Skills.CRAFTING,
								15000 },
						{ "Uncut ruby", 34, 75, 1604, 75, Skills.CRAFTING,
								12500 },
						{ "Uncut emerald", 27, 80, 1606, 80, Skills.CRAFTING,
								11000 },
						{ "Uncut sapphire", 20, 90, 1608, 90, Skills.CRAFTING,
								9000 },
						{ "Uncut red topaz", 16, 110, 1614, 110,
								Skills.CRAFTING, 8200 },
						{ "Uncut jade", 13, 125, 1612, 125, Skills.CRAFTING,
								7500 },
						{ "Uncut opal", 1, 150, 1610, 150, Skills.CRAFTING,
								5000 },
						// leathers - handled
						// regular
						{ "Leather gloves", 1, 200, 1060, 150, Skills.CRAFTING,
								1200 },
						{ "Leather boots", 7, 180, 1062, 180, Skills.CRAFTING,
								1800 },
						{ "Leather cowl", 9, 170, 1168, 170, Skills.CRAFTING,
								2200 },
						{ "Leather vambraces", 11, 160, 1064, 160,
								Skills.CRAFTING, 2500 },
						{ "Leather body", 14, 150, 1130, 150, Skills.CRAFTING,
								2600 },
						{ "Leather chaps", 18, 145, 1096, 145, Skills.CRAFTING,
								3200 },
						{ "Leather shield", 19, 140, 25806, 140,
								Skills.CRAFTING, 3500 },
						{ "Coif", 38, 130, 1168, 130, Skills.CRAFTING, 5000 },
						// hard leather
						{ "Hard leather boots", 27, 100, 25822, 100,
								Skills.CRAFTING, 3500 },
						{ "Hard leather gloves", 25, 100, 25876, 100,
								Skills.CRAFTING, 3700 },
						{ "Hardleather body", 27, 100, 1132, 100,
								Skills.CRAFTING, 4000 },
						{ "Hard leather shield", 28, 100, 25809, 100,
								Skills.CRAFTING, 5000 },
						// green d'hide
						{ "Green d'hide vambraces", 57, 100, 1066, 100,
								Skills.CRAFTING, 7000 },
						{ "Green d'hide chaps", 60, 100, 1100, 100,
								Skills.CRAFTING, 7500 },
						{ "Green d'hide body", 63, 100, 1136, 100,
								Skills.CRAFTING, 7700 },
						{ "Green d'hide shield", 64, 100, 25795, 100,
								Skills.CRAFTING, 7900 },
						// blue d'hide
						{ "Blue d'hide vambraces", 66, 100, 2488, 100,
								Skills.CRAFTING, 8000 },
						{ "Blue d'hide chaps", 68, 100, 2494, 100,
								Skills.CRAFTING, 8500 },
						{ "Blue d'hide body", 71, 100, 2500, 100,
								Skills.CRAFTING, 8700 },
						{ "Blue d'hide shield", 72, 100, 25797, 100,
								Skills.CRAFTING, 8900 },
						// red d'hide
						{ "Red d'hide vambraces", 73, 100, 2490, 100,
								Skills.CRAFTING, 9000 },
						{ "Red d'hide chaps", 75, 100, 2496, 100,
								Skills.CRAFTING, 9500 },
						{ "Red d'hide body", 77, 100, 2502, 100,
								Skills.CRAFTING, 9700 },
						{ "Red d'hide shield", 78, 100, 25799, 100,
								Skills.CRAFTING, 9900 },
						// black d'hide
						{ "Black d'hide vambraces", 79, 100, 2492, 100,
								Skills.CRAFTING, 10000 },
						{ "Black d'hide chaps", 82, 100, 2498, 100,
								Skills.CRAFTING, 10500 },
						{ "Black d'hide body", 84, 100, 2504, 100,
								Skills.CRAFTING, 10700 },
						{ "Black d'hide shield", 85, 100, 25801, 100,
								Skills.CRAFTING, 10900 },
						// royal d'hide
						{ "Royal d'hide vambraces", 87, 100, 24377, 100,
								Skills.CRAFTING, 13000 },
						{ "Royal d'hide chaps", 89, 100, 24380, 100,
								Skills.CRAFTING, 13500 },
						{ "Royal d'hide body", 93, 100, 24383, 100,
								Skills.CRAFTING, 13700 },
						/**
						 * Fletching - handled poorly
						 */

						/**
						 * Agility - handled
						 */
						{ "Roof", 90, 15, 995, 100000, Skills.AGILITY, 20000 },// adv
																				// barb
																				// -
																				// handled
						{ "Cliffside", 52, 25, 995, 75000, Skills.AGILITY,
								17500 },// wildy agility - handled
						{ "Crumbling wall", 35, 35, 995, 50000, Skills.AGILITY,
								15000 },// reg barb - handled
						{ "Obstacle pipe", 1, 50, 995, 10000, Skills.AGILITY,
								5000 },// reg gnome - handled

						/**
						 * Slayer
						 */
						/**
						 * Runecrafting
						 */
						/**
						 * Construction
						 */
						/**
						 * Hunter - handled
						 */
						// {"Crimson swifts", 1, 150, 9979, 150, Skills.HUNTER,
						// 1250}, //150
						{ "Baby impling", 17, 140, 11239, 140, Skills.HUNTER,
								2500 }, // 125
						{ "Young impling,", 22, 125, 11241, 125, Skills.HUNTER,
								3250 }, // 100
						{ "Gourmet impling", 28, 110, 11243, 110,
								Skills.HUNTER, 4000 },// 75
						{ "Earth impling", 36, 100, 11245, 100, Skills.HUNTER,
								6500 },
						{ "Essence impling", 42, 85, 11247, 85, Skills.HUNTER,
								7250 },
						{ "Eclectic impling", 50, 80, 11249, 80, Skills.HUNTER,
								8250 },
						{ "Spirit impling", 54, 70, 15514, 70, Skills.HUNTER,
								8750 },
						{ "Nature impling", 58, 65, 11251, 65, Skills.HUNTER,
								9250 },
						{ "Magpie impling", 65, 55, 11253, 65, Skills.HUNTER,
								10250 },
						{ "Ninja impling", 74, 45, 11255, 45, Skills.HUNTER,
								13450 },
						{ "Kingly impling", 91, 35, 15518, 35, Skills.HUNTER,
								18000 },

				});

		public static Tasks forId(int id) {
			for (Tasks task : Tasks.values()) {
				if (task.id == id)
					return task;
			}
			return null;
		}

		private int id;
		private Object[][] data;

		private Tasks(int id, Object[][] data) {
			this.id = id;
			this.data = data;
		}

		public int getId() {
			return id;
		}

	}

	private static final long serialVersionUID = -6153292068483584431L;

	public static DailyTasks generateDailyTask(Player player, Tasks task) {
		DailyTasks daily = null;
		while (true) {
			int random = Utils.random(task.data.length - 1);
			int requiredLevel = (Integer) task.data[random][1];
			int skill = (Integer) task.data[random][5];
			if (player.getSkills().getLevel(skill) < requiredLevel)
				continue;
			if (!player.settings[skill])// wont assign a daily task if the skill
										// is set to false
				continue;
			// below is a chance to block "undesirable tasks" if the players
			// level is significantly
			// higher than the tasks required level
			if (player.getSkills().getLevel(skill) >= requiredLevel + 40
					&& Utils.random(2) == 0)
				continue;
			else if (player.getSkills().getLevel(skill) >= requiredLevel + 30
					&& Utils.random(3) == 0)
				continue;
			else if (player.getSkills().getLevel(skill) >= requiredLevel + 20
					&& Utils.random(4) == 0)
				continue;
			int amount = (Integer) task.data[random][2];
			if (amount < 1)
				amount = 1;
			if (daily == null) {
				daily = new DailyTasks(task, random, amount);
				player.setDailyTask(daily);
				player.getPackets()
						.sendGameMessage(
								Color.ORANGE,
								(player.getDailyTask().hasDoneDaily ? "Daily Challenge Updated:"
										: "New Daily Challenge:")
										+ " "
										+ player.getDailyTask()
												.reformatTaskName(
														player.getDailyTask()
																.getName())
										+ " (0 / " + amount + ").");
				player.setDailyDate(player.getDailyTask().getTodayDate());
			}
			break;

		}
		return daily;
	}

	private Tasks task;
	private int taskId, setAmount, completedAmount;

	public DailyTasks(Tasks task, int taskId, int setAmount) {
		this.task = task;
		this.taskId = taskId;
		this.setAmount = setAmount;
	}

	public boolean hasDoneDaily = false;

	public void decrementTask() {
		if (Settings.DEBUG && Settings.WORLD_ID != 1)
			setAmount -= 10;
		else
			setAmount--;
		if (Settings.DEBUG && Settings.WORLD_ID != 1)
			completedAmount += 10;
		else
			completedAmount++;
	}

	public int getTotalAmount() {
		return (Integer) task.data[taskId][2];
	}

	public int getAmountCompleted() {
		return completedAmount;
	}

	public Tasks getTask() {
		return task;
	}

	public int getExp() {
		return (Integer) task.data[taskId][6];
	}

	public int getSkill() {
		return (Integer) task.data[taskId][5];
	}

	public int getReward() {
		return (Integer) task.data[taskId][3];
	}

	public int getRewardAmount() {
		return (Integer) task.data[taskId][4];
	}

	public String getName() {
		return (String) task.data[taskId][0];
	}

	public int getTaskAmount() {
		return setAmount;
	}

	public int getTaskId() {
		return taskId;
	}

	public String reformatTaskName(String name) {
		switch (getName().toLowerCase()) {
		/**
		 * Mining
		 */
		case "runite ore":
			return "Mining - Runite ore";
		case "adamantite ore":
			return "Mining - Adamantite ore";
		case "mithril ore":
			return "Mining - Mithril ore";
		case "gold ore":
			return "Mining - Gold ore";
		case "coal":
			return "Mining - Coal";
		case "silver ore":
			return "Mining - Silver ore";
		case "pure essence":
			return "Mining - Pure essence";
		case "iron ore":
			return "Mining - Iron ore";
		case "copper ore":
			return "Mining - Copper ore";
			/**
			 * Woodcutting
			 */
		case "magic":
			return "Woodcutting - Magic logs";
		case "yew":
			return "Woodcutting - Yew logs";
		case "maple":
			return "Woodcutting - Maple logs";
		case "willow":
			return "Woodcutting - Willow logs";
		case "oak":
			return "Woodcutting - Oak logs";
			/**
			 * Firemaking
			 */
		case "magic logs":
			return "Firemaking - Magic logs";
		case "yew logs":
			return "Firemaking - Yew logs";
		case "mahogany logs":
			return "Firemaking - Mahogany logs";
		case "maple logs":
			return "Firemaking - Maple logs";
		case "willow logs":
			return "Firemaking - Willow logs";
		case "oak logs":
			return "Firemaking - Oak logs";
			/**
			 * Farming
			 */
			// allotments
		case "potato seed":
			return "Farming - Potato seeds";
		case "onion seed":
			return "Farming - Onion seeds";
		case "cabbage seed":
			return "Farming - Cabbage seeds";
		case "tomato seed":
			return "Farming - Tomato seeds";
		case "sweetcorn seed":
			return "Farming - Sweetcorn seeds";
		case "strawberry seed":
			return "Farming - Strawberry seeds";
		case "watermelon seed":
			return "Farming - Watermelon seeds";
			// flowers
		case "marigold seed":
			return "Farming - Marigold seeds";
		case "rosemary seed":
			return "Farming - Rosemary seeds";
		case "nasturtium seed":
			return "Farming - Nasturtium seeds";
		case "woad seed":
			return "Farming - Woad seeds";
		case "limpwurt seed":
			return "Farming - Limpwurt seeds";
		case "white lily seed":
			return "Farming - White Lily seeds";
			// herbs
		case "guam seed":
			return "Farming - Guam seeds";
		case "marrentill seed":
			return "Farming - Marrentill seeds";
		case "tarromin seed":
			return "Farming - Tarromin seeds";
		case "harralander seed":
			return "Farming - Harralander seeds";
		case "ranarr seed":
			return "Farming - Ranarr seeds";
		case "spirit weed seed":
			return "Farming - Spirit Weed seeds";
		case "toadflax seed":
			return "Farming - Toadflax seeds";
		case "irit seed":
			return "Farming - Irit seeds";
		case "wergali seed":
			return "Farming - Wergali seeds";
		case "avantoe seed":
			return "Farming - Avantoe seeds";
		case "kwuarm seed":
			return "Farming - Kwuarm seeds";
		case "snapdragon seed":
			return "Farming - Snapdragon seeds";
		case "dwarf weed seed":
			return "Farming - Dwarf Weed seeds";
		case "torstol seed":
			return "Farming - Torstol seeds";
		case "fellstalk seed":
			return "Farming - Fellstalk seeds";

			// saplings
		case "oak sapling":
			return "Farming - an Oak Tree";
		case "willow sapling":
			return "Farming - a Willow Tree";
		case "maple sapling":
			return "Farming - a Maple Tree";
		case "yew sapling":
			return "Farming - a Yew Tree";
		case "magic sapling":
			return "Farming - a Magic Tree";

			/**
			 * Herblore
			 */
		case "overload (3)":
			return "Herblore - Overloads (3)";
		case "extreme ranging (3)":
			return "Herblore - Extreme Ranging Potions (3)";
		case "extreme magic (3)":
			return "Herblore - Extreme Magic Potions (3)";
		case "extreme defence (3)":
			return "Herblore - Extreme Defence Potions (3)";
		case "extreme strength (3)":
			return "Herblore - Extreme Strength Potions (3)";
		case "super antifire (3)":
			return "Herblore - Super Antifire Potions (3)";
		case "saradomin brew (3)":
			return "Herblore - Saradomin Brews (3)";
		case "ranging potion (3)":
			return "Herblore - Ranging potions (3)";
		case "zamorak brew (3)":
			return "Herblore - Zamorak Brews (3)";
		case "magic potion (3)":
			return "Herblore - Magic Potions (3)";
		case "ranged potion (3)":
			return "Herblore - Ranged Potions (3)";
		case "antifire (3)":
			return "Herblore - Antifire Potions (3)";
		case "super defence (3)":
			return "Herblore - Super Defence Potions (3)";
		case "super restore (3)":
			return "Herblore - Super Restore Potions (3)";
		case "super strength (3)":
			return "Herblore - Super Strength Potions (3)";
		case "super energy (3)":
			return "Herblore - Super Energy Potions (3)";
		case "super attack (3)":
			return "Herblore - Super Attack Potions (3)";
		case "prayer potion (3)":
			return "Herblore - Prayer Potions (3)";
		case "energy potion (3)":
			return "Herblore - Energy Potions (3)";
		case "defence potion (3)":
			return "Herblore - Defence Potions (3)";
		case "strength potion (3)":
			return "Herblore - Strength Potions (3)";
		case "attack potion (3)":
			return "Herblore - Attack Potions (3)";
			/**
			 * Cooking
			 */
		case "raw rocktail":
			return "Cooking - Rocktails";
		case "raw cavefish":
			return "Cooking - Cavefish";
		case "raw shark":
			return "Cooking - Shark";
		case "raw monkfish":
			return "Cooking - Monkfish";
		case "raw swordfish":
			return "Cooking - Swordfish";
		case "raw lobster":
			return "Cooking - Lobsters";
		case "raw tuna":
			return "Cooking - Tuna";
		case "raw anchovies":
			return "Cooking - Anchovies";
		case "raw shrimps":
			return "Cooking - Shrimps";
			/**
			 * Fishing
			 */
		case "rocktail":
			return "Fishing - Rocktails";
		case "cavefish":
			return "Fishing - Cavefish";
		case "shark":
			return "Fishing - Sharks";
		case "monkfish":
			return "Fishing - Monkfish";
		case "swordfish":
			return "Fishing - Swordfish";
		case "lobster":
			return "Fishing - Lobsters";
		case "tuna":
			return "Fishing - Tuna";
		case "anchovies":
			return "Fishing - Anchovies";
		case "shrimps":
			return "Fishing - Shrimps";
			/**
			 * Smithing
			 */
			// smelting
		case "rune bar":
			return "Smithing - Runite bars";
		case "adamant bar":
			return "Smithing - Adamantite bars";
		case "mithril bar":
			return "Smithing - Mithril bars";
		case "gold bar":
			return "Smithing - Gold bars";
		case "silver bar":
			return "Smithing - Silver bars";
		case "steel bar":
			return "Smithing - Steel bars";
		case "iron bar":
			return "Smithing - Iron bars";
		case "bronze bar":
			return "Smithing - Bronze bars";
			// rune
			// armour
		case "rune platebody":
			return "Smithing - Rune Platebodies";
		case "rune chainbody":
			return "Smithing - Rune Chainbodies";
		case "rune platelegs":
			return "Smithing - Rune Platelegs";
		case "rune plateskirt":
			return "Smithing - Rune Plateskirts";
		case "rune helm":
			return "Smithing - Rune Helmets";
		case "rune sq shield":
			return "Smithing - Rune Sq Shields";
		case "rune kiteshield":
			return "Smithing - Rune Kiteshields";
			// weapons
		case "rune hatchet":
			return "Smithing - Rune Hatchets";
		case "rune pickaxe":
			return "Smithing - Rune Pickaxes";
		case "rune knife":
			return "Smithing - Rune Knives";
		case "rune warhammer":
			return "Smithing - Rune Warhammers";
		case "rune 2h sword":
			return "Smithing - Rune 2h Swords";
		case "rune scimitar":
			return "Smithing - Rune Scimitars";
		case "rune mace":
			return "Smithing - Rune Maces";
		case "rune dagger":
			return "Smithing - Rune Daggers";
		case "rune longsword":
			return "Smithing - Rune Longswords";
			// adamant
			// armour
		case "adamant platebody":
			return "Smithing - Adamant Platebodies";
		case "adamant chainbody":
			return "Smithing - Adamant Chainbodies";
		case "adamant platelegs":
			return "Smithing - Adamant Platelegs";
		case "adamant plateskirt":
			return "Smithing - Adamant Plateskirts";
		case "adamant helm":
			return "Smithing - Adamant Helmets";
		case "adamant sq shield":
			return "Smithing - Adamant Sq Shields";
		case "adamant kiteshield":
			return "Smithing - Adamant Kiteshields";
			// weapons
		case "adamant hatchet":
			return "Smithing - Adamant Hatchets";
		case "adamant pickaxe":
			return "Smithing - Adamant Pickaxes";
		case "adamant knife":
			return "Smithing - Adamant Knives";
		case "adamant warhammer":
			return "Smithing - Adamant Warhammers";
		case "adamant 2h sword":
			return "Smithing - Adamant 2h Swords";
		case "adamant scimitar":
			return "Smithing - Adamant Scimitars";
		case "adamant mace":
			return "Smithing - Adamant Maces";
		case "adamant dagger":
			return "Smithing - Adamant Daggers";
		case "adamant longsword":
			return "Smithing - Adamant Longswords";
			// mithril
			// armour
		case "mithril platebody":
			return "Smithing - Mithril Platebodies";
		case "mithril chainbody":
			return "Smithing - Mithril Chainbodies";
		case "mithril platelegs":
			return "Smithing - Mithril Platelegs";
		case "mithril plateskirt":
			return "Smithing - Mithril Plateskirts";
		case "mithril helm":
			return "Smithing - Mithril Helmets";
		case "mithril sq shield":
			return "Smithing - Mithril Sq Shields";
		case "mithril kiteshield":
			return "Smithing - Mithril Kiteshields";
			// weapons
		case "mithril hatchet":
			return "Smithing - Mithril Hatchets";
		case "mithril pickaxe":
			return "Smithing - Mithril Pickaxes";
		case "mithril knife":
			return "Smithing - Mithril Knives";
		case "mithril warhammer":
			return "Smithing - Mithril Warhammers";
		case "mithril 2h sword":
			return "Smithing - Mithril 2h Swords";
		case "mithril scimitar":
			return "Smithing - Mithril Scimitars";
		case "mithril mace":
			return "Smithing - Mithril Maces";
		case "mithril dagger":
			return "Smithing - Mithril Daggers";
		case "mithril longsword":
			return "Smithing - Mithril Longswords";
			// steel
			// armour
		case "steel platebody":
			return "Smithing - Steel Platebodies";
		case "steel chainbody":
			return "Smithing - Steel Chainbodies";
		case "steel platelegs":
			return "Smithing - Steel Platelegs";
		case "steel plateskirt":
			return "Smithing - Steel Plateskirts";
		case "steel helm":
			return "Smithing - Steel Helmets";
		case "steel sq shield":
			return "Smithing - Steel Sq Shields";
		case "steel kiteshield":
			return "Smithing - Steel Kiteshields";
			// weapons
		case "steel hatchet":
			return "Smithing - Steel Hatchets";
		case "steel pickaxe":
			return "Smithing - Steel Pickaxes";
		case "steel knife":
			return "Smithing - Steel Knives";
		case "steel warhammer":
			return "Smithing - Steel Warhammers";
		case "steel 2h sword":
			return "Smithing - Steel 2h Swords";
		case "steel scimitar":
			return "Smithing - Steel Scimitars";
		case "steel mace":
			return "Smithing - Steel Maces";
		case "steel dagger":
			return "Smithing - Steel Daggers";
		case "steel longsword":
			return "Smithing - Steel Longswords";
			// iron
			// armour
		case "iron platebody":
			return "Smithing - Iron Platebodies";
		case "iron chainbody":
			return "Smithing - Iron Chainbodies";
		case "iron platelegs":
			return "Smithing - Iron Platelegs";
		case "iron plateskirt":
			return "Smithing - Iron Plateskirts";
		case "iron helm":
			return "Smithing - Iron Helmets";
		case "iron sq shield":
			return "Smithing - Iron Sq Shields";
		case "iron kiteshield":
			return "Smithing - Iron Kiteshields";
			// weapons
		case "iron hatchet":
			return "Smithing - Iron Hatchets";
		case "iron pickaxe":
			return "Smithing - Iron Pickaxes";
		case "iron knife":
			return "Smithing - Iron Knives";
		case "iron warhammer":
			return "Smithing - Iron Warhammers";
		case "iron 2h sword":
			return "Smithing - Iron 2h Swords";
		case "iron scimitar":
			return "Smithing - Iron Scimitars";
		case "iron mace":
			return "Smithing - Iron Maces";
		case "iron dagger":
			return "Smithing - Iron Daggers";
		case "iron longsword":
			return "Smithing - Iron Longswords";
			// bronze
			// armour
		case "bronze platebody":
			return "Smithing - Bronze Platebodies";
		case "bronze chainbody":
			return "Smithing - Bronze Chainbodies";
		case "bronze platelegs":
			return "Smithing - Bronze Platelegs";
		case "bronze plateskirt":
			return "Smithing - Bronze Plateskirts";
		case "bronze helm":
			return "Smithing - Bronze Helmets";
		case "bronze sq shield":
			return "Smithing - Bronze Sq Shields";
		case "bronze kiteshield":
			return "Smithing - Bronze Kiteshields";
			// weapons
		case "bronze hatchet":
			return "Smithing - Bronze Hatchets";
		case "bronze pickaxe":
			return "Smithing - Bronze Pickaxes";
		case "bronze knife":
			return "Smithing - Bronze Knives";
		case "bronze warhammer":
			return "Smithing - Bronze Warhammers";
		case "bronze 2h sword":
			return "Smithing - Bronze 2h Swords";
		case "bronze scimitar":
			return "Smithing - Bronze Scimitars";
		case "bronze mace":
			return "Smithing - Bronze Maces";
		case "bronze dagger":
			return "Smithing - Bronze Daggers";
		case "bronze longsword":
			return "Smithing - Bronze Longswords";
			/**
			 * Summoning
			 */
			// case "spirit wolf pouch": return "(Summon) Spirit Wolf Pouches";
			// case "dreadfowl pouch": return "(Summon) Dreadfowl Pouches";
			/**
			 * Thieving
			 */
			// npcs
		case "man":
			return "Thieving - from Men or Women";
		case "guard":
			return "Thieving - from Guards";
		case "paladin":
			return "Thieving - from Paladins";
		case "hero":
			return "Thieving - from Heroes";
		case "trader":
			return "Thieving - from Dwarf Traders";
			// stalls
		case "baker's stall":
			return "Thieving - a Baker's stall";
		case "fur stall":
			return "Thieving - a Fur stall";
		case "silk stall":
			return "Thieving - a Silk stall";
		case "silver stall":
			return "Thieving - a Silver stall";
		case "spice stall":
			return "Thieving - a Spice stall";
		case "gem stall":
			return "Thieving - a Gem stall";

			/**
			 * Crafting
			 */
			// gems
		case "uncut dragonstone":
			return "Crafting -  Uncut Dragonstone";
		case "uncut diamond":
			return "Crafting -  Uncut Diamond";
		case "uncut ruby":
			return "Crafting -  Uncut Ruby";
		case "uncut emerald":
			return "Crafting -  Uncut Emerald";
		case "uncut sapphire":
			return "Crafting -  Uncut Sapphire";
		case "uncut red topaz":
			return "Crafting -  Uncut Red Topaz";
		case "uncut jade":
			return "Crafting -  Uncut Jade";
		case "uncut opal":
			return "Crafting -  Uncut Opal";
			// leathers
			// regular
		case "leather gloves":
			return "Crafting - Leather gloves";
		case "leather boots":
			return "Crafting - Leather boots";
		case "leather cowl":
			return "Crafting - Leather cowls";
		case "leather vambraces":
			return "Crafting - Leather vambraces";
		case "leather body":
			return "Crafting - Leather bodies";
		case "leather chaps":
			return "Crafting - Leather chaps";
		case "leather shield":
			return "Crafting - Leather shields";
		case "coif":
			return "Crafting - Leather coifs";
			// hard leather
		case "hard leather boots":
			return "Crafting - Hard leather boots";
		case "hard leather gloves":
			return "Crafting - Hard leather gloves";
		case "hardleather body":
			return "Crafting - Hard leather bodies";
		case "hard leather shield":
			return "Crafting - Hard leather shields";
			// green d'hide
		case "green d'hide vambraces":
			return "Crafting - Green d'hide vambraces";
		case "green d'hide chaps":
			return "Crafting - Green d'hide chaps";
		case "green d'hide body":
			return "Crafting - Green d'hide bodies";
		case "green d'hide shield":
			return "Crafting - Green d'hide shields";
			// blue d'hide
		case "blue d'hide vambraces":
			return "Crafting - Blue d'hide vambraces";
		case "blue d'hide chaps":
			return "Crafting - Blue d'hide chaps";
		case "blue d'hide body":
			return "Crafting - Blue d'hide bodies";
		case "blue d'hide shield":
			return "Crafting - Blue d'hide shields";
			// red d'hide
		case "red d'hide vambraces":
			return "Crafting - Red d'hide vambraces";
		case "red d'hide chaps":
			return "Crafting - Red d'hide chaps";
		case "red d'hide body":
			return "Crafting - Red d'hide bodies";
		case "red d'hide shield":
			return "Crafting - Red d'hide shields";
			// black d'hide
		case "black d'hide vambraces":
			return "Crafting - Black d'hide vambraces";
		case "black d'hide chaps":
			return "Crafting - Black d'hide chaps";
		case "black d'hide body":
			return "Crafting - Black d'hide bodies";
		case "black d'hide shield":
			return "Crafting - Black d'hide shields";
			// royal d'hide
		case "royal d'hide vambraces":
			return "Crafting - Royal d'hide vambraces";
		case "royal d'hide chaps":
			return "Crafting - Royal d'hide chaps";
		case "royal d'hide body":
			return "Crafting - Royal d'hide bodies";
			/**
			 * Fletching
			 */
			// fletch unstrung
		case "magic shieldbow (u)":
			return "Fletching - Magic Shieldbows (u)";
		case "magic shortbow (u)":
			return "Fletching - Magic Shortbows (u)";
		case "yew shieldbow (u)":
			return "Fletching - Yew Shieldbows (u)";
		case "yew shortbow (u)":
			return "Fletching - Yew Shortbows (u)";
		case "maple shieldbow (u)":
			return "Fletching - Maple Shieldbows (u)";
		case "maple shortbow (u)":
			return "Fletching - Maple Shortbows (u)";
		case "willow shieldbow (u)":
			return "Fletching - Willow Shieldbows (u)";
		case "willow shortbow (u)":
			return "Fletching - Willow Shortbows (u)";
		case "oak shieldbow (u)":
			return "Fletching - Oak Shieldbows (u)";
		case "oak shortbow (u)":
			return "Fletching - Oak Shortbows (u)";
			// bolas
		case "bolas":
			return "Fletching - Bolas";
			// string unstrung
		case "magic shieldbow":
			return "Fletching - Magic Shieldbows (u)";
		case "magic shortbow":
			return "Fletching - Magic Shortbows (u)";
		case "yew shieldbow":
			return "Fletching - Yew Shieldbows (u)";
		case "yew shortbow":
			return "Fletching - Yew Shortbows (u)";
		case "maple shieldbow":
			return "Fletching - Maple Shieldbows (u)";
		case "maple shortbow":
			return "Fletching - Maple Shortbows (u)";
		case "willow shieldbow":
			return "Fletching - Willow Shieldbows (u)";
		case "willow shortbow":
			return "Fletching - Willow Shortbows (u)";
		case "oak shieldbow":
			return "Fletching - Oak Shieldbows (u)";
		case "oak shortbow":
			return "Fletching - Oak Shortbows (u)";
			/**
			 * Agility
			 */
		case "roof":
			return "Agility - Adavanced Barbarian Outpost Agility Course"; // (Agility)?
		case "cliffside":
			return "Agility - Wilderness Agility Course";
		case "crumbling wall":
			return "Agility - Barbarian Outpost Agility Course";
		case "obstacle pipe":
			return "Agility - Gnome Agility Course";
			/**
			 * Slayer
			 */
			/**
			 * Runecrafting
			 */
			/**
			 * Construction
			 */
			/**
			 * Hunter
			 */
			// case "crimson swifts": return "Hunter - Crimson Swifts";
		case "baby impling":
			return "Hunter - Baby Impling";
		case "young impling":
			return "Hunter - Young Implings";
		case "gourmet impling":
			return "Hunter - Gourmet Implings";
		case "earth impling":
			return "Hunter - Earth Implings";
		case "essence impling":
			return "Hunter - Essence Implings";
		case "eclectic impling":
			return "Hunter - Eclectic Implings";
		case "spirit impling":
			return "Hunter - Spirit Implings";
		case "nature impling":
			return "Hunter - Nature Implings";
		case "magpie impling":
			return "Hunter - Magpie Implings";
		case "ninja impling":
			return "Hunter - Ninja Implings";
		case "kingly impling":
			return "Hunter - Kingly Implings";
		}
		return name;
	}

	public int getTodayDate() {
		Calendar cal = new GregorianCalendar();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		return (month * 100 + day);
	}

	public void generateDailyTasks(Player player, boolean ignoreCurrent) {
		if (ignoreCurrent) {
			generateDailyTask(player, Tasks.SKILLING);
			player.completedDaily = false;
			return;
		}
		if (player.getDailyDate() == getTodayDate()) {
			if (player.getDailyTask() != null && !player.completedDaily) {
				player.getPackets()
						.sendGameMessage(
								Color.ORANGE,
								(player.getDailyTask().hasDoneDaily ? "Daily Challenge Updated:"
										: "New Daily Challenge:")
										+ " "
										+ player.getDailyTask()
												.reformatTaskName(
														player.getDailyTask()
																.getName())
										+ " ("
										+ player.getDailyTask()
												.getAmountCompleted()
										+ " / "
										+ player.getDailyTask()
												.getTotalAmount() + ").");
			} else if (player.completedDaily && !player.claimedDailyReward) {
				player.getPackets()
						.sendGameMessage(
								Color.ORANGE,
								"You have completed your Daily Challenge. Talk to a Challenge Mistress to claim your reward");
			}
		} else {
			generateDailyTask(player, Tasks.SKILLING);
			player.completedDaily = false;
		}
	}

	/**
	 * 
	 * @param player
	 *            Sets the player
	 * @param type
	 *            1: NPCs, 2: Objects, 3: Items
	 * @param id
	 *            The item etc, used to determine the name.
	 * @param skilltype
	 *            The skill to check when processing the tasks; used to prevent
	 *            the woodcutting task checking off the firemaking task and vice
	 *            versa
	 */
	public void incrementTask(Player player, int type, int id, int skilltype) {
		if (player.completedDaily)
			return;
		if (skilltype != getSkill())// checks for proper skill when completing
			return;
		if (player.getDailyTask() != null) {
			boolean completingTask = false;
			if (type == 1) {
				if (NPCDefinitions
						.getNPCDefinitions(id)
						.getName()
						.toLowerCase()
						.contains(player.getDailyTask().getName().toLowerCase()))
					completingTask = true;
			} else if (type == 2) {
				if (ObjectDefinitions.getObjectDefinitions(id).name
						.toLowerCase().contains(
								player.getDailyTask().getName().toLowerCase()))
					completingTask = true;
			} else if (type == 3) {
				if (ItemDefinitions
						.getItemDefinitions(id)
						.getName()
						.toLowerCase()
						.contains(player.getDailyTask().getName().toLowerCase()))
					completingTask = true;
			}
			if (!completingTask)// checks to make sure that the player is
								// completing the task before incrementing.
				return;
			player.claimedDailyReward = false;
			player.getDailyTask().decrementTask();
			player.getPackets().sendGameMessage(
					Color.ORANGE,
					"Daily Challenge Updated:"
							+ " "
							+ player.getDailyTask().reformatTaskName(
									player.getDailyTask().getName()) + " ("
							+ player.getDailyTask().getAmountCompleted()
							+ " / " + player.getDailyTask().getTotalAmount()
							+ ").", true);
			if (player.getDailyTask().getTaskAmount() < 1) {
				completeTask(player);
				if (!hasDoneDaily)
					hasDoneDaily = true;
			}
		}
	}

	public void completeTask(Player player) {
		player.getPackets().sendGameMessage(
				Color.ORANGE,
				"You have completed: "
						+ player.getDailyTask().reformatTaskName(
								player.getDailyTask().getName()) + " ("
						+ player.getDailyTask().getAmountCompleted() + "/"
						+ player.getDailyTask().getTotalAmount() + ").</col>");
		player.getPackets().sendGameMessage(Color.ORANGE,
				"Talk to a Challenge Mistress to claim your reward.");
		player.setNextGraphics(new Graphics(1765));
		player.completedDaily = true;
		player.claimedDailyReward = false;
	}
}