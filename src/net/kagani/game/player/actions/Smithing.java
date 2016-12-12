package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.WorldObject;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Utils;



public class Smithing extends Action {

    public static final int HAMMER = 2347, DUNGEONEERING_HAMMER = 17883;
    int random = Utils.random(700);

    
    
    public enum ForgingBar {

		
		BRONZE_DAGGER(1, 6.2, new Item[] { new Item(2349) }, new Item(1205)),

		Off_HAND_BRONZE_Dagger(1, 12.5, new Item[] { new Item(2349) }, new Item(25692)),

		BRONZE_HATCHET(1, 12.5, new Item[] { new Item(2349) }, new Item(1351)),

		BRONZE_MACE(2, 12.5, new Item[] { new Item(2349) }, new Item(1422)),

		OFF_HAND_BRONZE_MACE(2, 12.5, new Item[] { new Item(2349) }, new Item(25674)),

		BRONZE_HELM(3, 12.5, new Item[] { new Item(2349) }, new Item(1139)),

		BRONZE_BLOTS_UNF(3, 12.5, new Item[] { new Item(2349) }, new Item(9375, 10)),

		BRONZE_SWORD(4, 12.5, new Item[] { new Item(2349) }, new Item(1277)),

		OFF_HAND_BRONZE_SWORD(4, 12.5, new Item[] { new Item(2349) }, new Item(25710)),

		BRONZE_WIRE(4, 12.5, new Item[] { new Item(2349) }, new Item(1794)),

		BRONZE_DART_TIP(4, 12.5, new Item[] { new Item(2349) }, new Item(819, 10)),

		BRONZE_NAILS(4, 12.5, new Item[] { new Item(2349) }, new Item(4819, 15)),

		BRONZE_ARROWHEADS(5, 12.5, new Item[] { new Item(2349) }, new Item(39, 15)),

		BRONZE_SCIMITAR(5, 25, new Item[] { new Item(2349, 2) }, new Item(1321)),

		OFF_HAND_BRONZE_SCIMITAR(5, 25, new Item[] { new Item(2349, 2) }, new Item(25743)),

		BRONZE_PICKAXE(5, 25, new Item[] { new Item(2349, 2) }, new Item(1265)),

		BRONZE_LIMBS(6, 12.5, new Item[] { new Item(2349, 1) }, new Item(9420)),

		BRONZE_LONGSWORD(6, 25, new Item[] { new Item(2349, 2) }, new Item(1291)),

		OFF_HAND_BRONZE_LONGSWORD(6, 25, new Item[] { new Item(2349, 2) }, new Item(25725)),

		BRONZE_KNIFE(7, 12.5, new Item[] { new Item(2349) }, new Item(864, 5)),

		OFF_HAND_BRONZE_KNIFE(7, 12.5, new Item[] { new Item(2349) }, new Item(25897, 5)),

		BRONZE_THROWINAXE(7, 12.5, new Item[] { new Item(2349) }, new Item(800, 5)),

		OFF_HANDBRONZE_THROWINAXE(7, 12.5, new Item[] { new Item(2349) }, new Item(25903, 5)),

		BRONZE_FULL_HELM(7, 25, new Item[] { new Item(2349, 2) }, new Item(11550)),

		BRONZE_SQ_SHIELD(8, 25, new Item[] { new Item(2349, 2) }, new Item(1173)),

		BRONZE_WARHAMMER(9, 37.5, new Item[] { new Item(2349, 3) }, new Item(1337)),

		OFF_HAND_BRONZE_WARHAMMER(9, 37.5, new Item[] { new Item(2349, 3) }, new Item(25579)),

		BRONZE_BATTLEAXE(10, 37.5, new Item[] { new Item(2349, 3) }, new Item(1375)),

		OFF_HAND_BRONZE_BATTLEAXE(10, 37.5, new Item[] { new Item(2349, 3) }, new Item(25761)),

		BRONZE_CHAINBODY(11, 37.5, new Item[] { new Item(2349, 3) }, new Item(1103)),

		BRONZE_KITESHIELD(12, 37.5, new Item[] { new Item(2349, 3) }, new Item(1189)),

		BRONZE_CLAW(13, 25, new Item[] { new Item(2349, 2) }, new Item(3095)),

		OFF_HAND_BRONZE_CLAW(13, 25, new Item[] { new Item(2349, 2) }, new Item(25933)),

		BRONZE_2H_SWORD(14, 37.5, new Item[] { new Item(2349, 3) }, new Item(1307)),

		BRONZE_PLATELEGS(16, 37.5, new Item[] { new Item(2349, 3) }, new Item(1075)),

		BRONZE_PLATESKIRT(16, 37.5, new Item[] { new Item(2349, 3) }, new Item(1087)),

		BRONZE_PLATEBODY(18, 62.5, new Item[] { new Item(2349, 5) }, new Item(1117)),

		
		IRON_DAGGER(15, 25, new Item[] { new Item(2351) }, new Item(1203)),

		Off_HAND_IRON_Dagger(15, 25, new Item[] { new Item(2351) }, new Item(25694)),

		IRON_HATCHET(16, 25, new Item[] { new Item(2351) }, new Item(1349)),

		IRON_SPIT(16, 25, new Item[] { new Item(2351) }, new Item(7225)),

		IRON_MACE(17, 25, new Item[] { new Item(2351) }, new Item(1420)),

		OFF_HAND_IRON_MACE(17, 25, new Item[] { new Item(2351) }, new Item(25676)),

		IRON_HELM(18, 25, new Item[] { new Item(2351) }, new Item(1137)),

		IRON_BLOTS_UNF(18, 25, new Item[] { new Item(2351) }, new Item(9377, 10)),

		IRON_SWORD(19, 25, new Item[] { new Item(2351) }, new Item(1279)),

		OFF_HAND_IRON_SWORD(19, 25, new Item[] { new Item(2351) }, new Item(25712)),

		IRON_DART_TIP(19, 25, new Item[] { new Item(2351) }, new Item(820, 10)),

		IRON_NAILS(19, 25, new Item[] { new Item(2351) }, new Item(4820, 15)),

		IRON_ARROWHEADS(20, 25, new Item[] { new Item(2351) }, new Item(40, 15)),

		IRON_SCIMITAR(20, 25, new Item[] { new Item(2351, 2) }, new Item(1323)),

		OFF_HAND_IRON_SCIMITAR(20, 25, new Item[] { new Item(2351, 2) }, new Item(25745)),

		IRON_PICKAXE(20, 25, new Item[] { new Item(2351, 2) }, new Item(1267)),

		IRON_LONGSWORD(21, 25, new Item[] { new Item(2351, 2) }, new Item(1293)),

		OFF_HAND_IRON_LONGSWORD(21, 25, new Item[] { new Item(2351, 2) }, new Item(25727)),

		IRON_KNIFE(22, 25, new Item[] { new Item(2351) }, new Item(863, 5)),

		OFF_HAND_IRON_KNIFE(22, 25, new Item[] { new Item(2351) }, new Item(25896, 5)),

		IRON_THROWINAXE(22, 25, new Item[] { new Item(2351) }, new Item(801, 5)),

		OFF_HANDIRON_THROWINAXE(22, 25, new Item[] { new Item(2351) }, new Item(25904, 5)),

		IRON_FULL_HELM(22, 25, new Item[] { new Item(2351, 2) }, new Item(1153)),

		IRON_LIMBS(23, 25, new Item[] { new Item(2351, 1) }, new Item(9423)),

		IRON_SQ_SHIELD(23, 50, new Item[] { new Item(2351, 2) }, new Item(1175)),

		IRON_WARHAMMER(24, 75, new Item[] { new Item(2351, 3) }, new Item(1335)),

		OFF_HAND_IRON_WARHAMMER(24, 75, new Item[] { new Item(2351, 3) }, new Item(25781)),

		IRON_BATTLEAXE(25, 75, new Item[] { new Item(2351, 3) }, new Item(1363)),

		OFF_HAND_IRON_BATTLEAXE(25, 75, new Item[] { new Item(2351, 3) }, new Item(25763)),

		OIL_LANTERN_FRAME(26, 75, new Item[] { new Item(2351) }, new Item(4540)),

		IRON_CHAINBODY(26, 75, new Item[] { new Item(2351, 3) }, new Item(1101)),

		IRON_KITESHIELD(27, 75, new Item[] { new Item(2351, 3) }, new Item(1191)),

		IRON_CLAW(28, 25, new Item[] { new Item(2351, 2) }, new Item(3096)),

		OFF_HAND_IRON_CLAW(28, 25, new Item[] { new Item(2351, 2) }, new Item(25935)),

		IRON_2H_SWORD(29, 75, new Item[] { new Item(2351, 3) }, new Item(1309)),

		IRON_PLATELEGS(31, 75, new Item[] { new Item(2351, 3) }, new Item(1067)),

		IRON_PLATESKIRT(31, 75, new Item[] { new Item(2351, 3) }, new Item(1081)),

		IRON_PLATEBODY(33, 125, new Item[] { new Item(2351, 5) }, new Item(1115)),

		
		STEEL_DAGGER(30, 37.5, new Item[] { new Item(2353) }, new Item(1207)),

		Off_HAND_STEEL_Dagger(30, 75, new Item[] { new Item(2353) }, new Item(25969)),

		STEEL_HATCHET(31, 75, new Item[] { new Item(2353) }, new Item(1353)),

		STEEL_MACE(32, 75, new Item[] { new Item(2353) }, new Item(1424)),

		OFF_HAND_STEEL_MACE(32, 75, new Item[] { new Item(2353) }, new Item(25678)),

		STEEL_HELM(33, 75, new Item[] { new Item(2353) }, new Item(1141)),

		STEEL_BLOTS_UNF(33, 75, new Item[] { new Item(2353) }, new Item(9378, 10)),

		STEEL_SWORD(34, 75, new Item[] { new Item(2353) }, new Item(1281)),

		OFF_HAND_STEEL_SWORD(34, 75, new Item[] { new Item(2353) }, new Item(25714)),

		STEEL_DART_TIP(34, 75, new Item[] { new Item(2353) }, new Item(821, 10)),

		STEEL_NAILS(34, 75, new Item[] { new Item(2353) }, new Item(1539, 15)),

		STEEL_ARROWHEADS(35, 75, new Item[] { new Item(2353) }, new Item(41, 15)),

		

		STEEL_SCIMITAR(35, 75, new Item[] { new Item(2353, 2) }, new Item(1325)),

		OFF_HAND_STEEL_SCIMITAR(35, 75, new Item[] { new Item(2353, 2) }, new Item(25747)),

		STEEL_PICKAXE(35, 75, new Item[] { new Item(2353, 2) }, new Item(1269)),

		STEEL_LIMBS(36, 75, new Item[] { new Item(2353, 1) }, new Item(9425)),

		STEEL_LONGSWORD(36, 75, new Item[] { new Item(2353, 2) }, new Item(1295)),

		OFF_HAND_STEEL_LONGSWORD(36, 75, new Item[] { new Item(2353, 2) }, new Item(25729)),

		STEEL_KNIFE(37, 75, new Item[] { new Item(2353) }, new Item(865, 5)),

		OFF_HAND_STEEL_KNIFE(37, 75, new Item[] { new Item(2353) }, new Item(25898, 5)),

		STEEL_THROWINAXE(37, 75, new Item[] { new Item(2353) }, new Item(802, 5)),

		OFF_HANDSTEEL_THROWINAXE(37, 75, new Item[] { new Item(2353) }, new Item(25905, 5)),

		STEEL_FULL_HELM(37, 75, new Item[] { new Item(2353, 2) }, new Item(1157)),

		STEEL_SQ_SHIELD(38, 75, new Item[] { new Item(2353, 2) }, new Item(1177)),

		STEEL_WARHAMMER(39, 112.5, new Item[] { new Item(2353, 3) }, new Item(1339)),

		OFF_HAND_STEEL_WARHAMMER(39, 112.5, new Item[] { new Item(2353, 3) }, new Item(25783)),

		STEEL_BATTLEAXE(40, 112.5, new Item[] { new Item(2353, 3) }, new Item(1365)),

		OFF_HAND_STEEL_BATTLEAXE(40, 112.5, new Item[] { new Item(2353, 3) }, new Item(25765)),

		STEEL_CHAINBODY(41, 112.5, new Item[] { new Item(2353, 3) }, new Item(1105)),

		STEEL_KITESHIELD(42, 112.5, new Item[] { new Item(2353, 3) }, new Item(1193)),

		STEEL_CLAW(43, 75, new Item[] { new Item(2353, 2) }, new Item(3097)),

		OFF_HAND_STEEL_CLAW(43, 75, new Item[] { new Item(2353, 2) }, new Item(25937)),

		STEEL_2H_SWORD(44, 112.5, new Item[] { new Item(2353, 3) }, new Item(1311)),

		STEEL_PLATELEGS(46, 112.5, new Item[] { new Item(2353, 3) }, new Item(1069)),

		STEEL_PLATESKIRT(46, 112.5, new Item[] { new Item(2353, 3) }, new Item(1083)),

		STEEL_PLATEBODY(48, 187.5, new Item[] { new Item(2353, 5) }, new Item(1119)),

		
		MITHRIL_DAGGER(50, 50, new Item[] { new Item(2359) }, new Item(1209)),

		Off_HAND_MITHRIL_Dagger(50, 90, new Item[] { new Item(2359) }, new Item(25700)),

		MITHRIL_HATCHET(51, 90, new Item[] { new Item(2359) }, new Item(1355)),

		MITHRIL_MACE(52, 90, new Item[] { new Item(2359) }, new Item(1428)),

		OFF_HAND_MITHRIL_MACE(52, 90, new Item[] { new Item(2359) }, new Item(25682)),

		MITHRIL_HELM(53, 90, new Item[] { new Item(2359) }, new Item(1143)),

		MITHRIL_BLOTS_UNF(53, 90, new Item[] { new Item(2359) }, new Item(9379, 10)),

		MITHRIL_SWORD(54, 90, new Item[] { new Item(2359) }, new Item(1285)),

		OFF_HAND_MITHRIL_SWORD(54, 90, new Item[] { new Item(2359) }, new Item(25718)),

		MITHRIL_DART_TIP(54, 90, new Item[] { new Item(2359) }, new Item(822, 10)),

		MITHRIL_NAILS(54, 90, new Item[] { new Item(2359) }, new Item(4822, 15)),

		MITHRIL_ARROWHEADS(55, 90, new Item[] { new Item(2359) }, new Item(42, 15)),

		MITHRIL_SCIMITAR(55, 100, new Item[] { new Item(2359, 2) }, new Item(1329)),

		OFF_HAND_MITHRIL_SCIMITAR(55, 100, new Item[] { new Item(2359, 2) }, new Item(25751)),

		MITHRIL_PICKAXE(55, 100, new Item[] { new Item(2359, 2) }, new Item(1273)),

		MITHRIL_LIMBS(56, 90, new Item[] { new Item(2359, 1) }, new Item(9427)),

		MITHRIL_LONGSWORD(56, 100, new Item[] { new Item(2359, 2) }, new Item(1299)),

		OFF_HAND_MITHRIL_LONGSWORD(56, 90, new Item[] { new Item(2359, 2) }, new Item(25733)),

		MITHRIL_KNIFE(57, 100, new Item[] { new Item(2359) }, new Item(866, 5)),

		OFF_HAND_MITHRIL_KNIFE(57, 90, new Item[] { new Item(2359) }, new Item(25899, 5)),

		MITHRIL_THROWINAXE(57, 90, new Item[] { new Item(2359) }, new Item(806, 5)),

		OFF_HANDMITHRIL_THROWINAXE(57, 90, new Item[] { new Item(2359) }, new Item(25906, 5)),

		MITHRIL_FULL_HELM(57, 90, new Item[] { new Item(2359, 2) }, new Item(1159)),

		MITHRIL_SQ_SHIELD(58, 90, new Item[] { new Item(2359, 2) }, new Item(1181)),

		MITHRIL_GRAPPLE_TIP(59, 112.5, new Item[] { new Item(2359, 1) }, new Item(9416)),

		MITHRIL_WARHAMMER(59, 112.5, new Item[] { new Item(2359, 3) }, new Item(1343)),

		OFF_HAND_MITHRIL_WARHAMMER(59, 112.5, new Item[] { new Item(2359, 3) }, new Item(25787)),

		MITHRIL_BATTLEAXE(60, 112.5, new Item[] { new Item(2359, 3) }, new Item(1369)),

		OFF_HAND_MITHRIL_BATTLEAXE(60, 112.5, new Item[] { new Item(2359, 3) }, new Item(25769)),

		MITHRIL_CHAINBODY(61, 112.5, new Item[] { new Item(2359, 3) }, new Item(1109)),

		MITHRIL_KITESHIELD(62, 112.5, new Item[] { new Item(2359, 3) }, new Item(1197)),

		MITHRIL_CLAW(63, 90, new Item[] { new Item(2359, 2) }, new Item(3099)),

		OFF_HAND_MITHRIL_CLAW(63, 90, new Item[] { new Item(2359, 2) }, new Item(25941)),

		MITHRIL_2H_SWORD(64, 150, new Item[] { new Item(2359, 3) }, new Item(1315)),

		MITHRIL_PLATELEGS(66, 150, new Item[] { new Item(2359, 3) }, new Item(1071)),

		MITHRIL_PLATESKIRT(66, 150, new Item[] { new Item(2359, 3) }, new Item(1085)),

		MITHRIL_PLATEBODY(68, 250, new Item[] { new Item(2359, 5) }, new Item(1121)),

		

		ADAMANT_DAGGER(70, 62.5, new Item[] { new Item(2361) }, new Item(1211)),

		OFF_HAND_ADAMANT_DAGGER(70, 62.5, new Item[] { new Item(2361) }, new Item(25702)),

		ADAMANT_HATCHET(71, 62.5, new Item[] { new Item(2361) }, new Item(1357)),

		ADAMANT_MACE(72, 62.5, new Item[] { new Item(2361) }, new Item(1430)),

		OFF_HAND_ADAMANT_MACE(73, 62.5, new Item[] { new Item(2361) }, new Item(25684)),

		ADAMANT_HELM(73, 62.5, new Item[] { new Item(2361) }, new Item(1145)),

		ADAMANT_BOLTS(74, 62.5, new Item[] { new Item(2361) }, new Item(9380, 10)),

		ADAMANT_SWORD(74, 62.5, new Item[] { new Item(2361) }, new Item(1287)),

		OFF_HAND_ADAMANT_SWORD(74, 62.5, new Item[] { new Item(2361) }, new Item(25720)),

		ADAMANT_DART_TIP(74, 62.5, new Item[] { new Item(2361) }, new Item(823)),

		ADAMANT_NAILS(74, 62.5, new Item[] { new Item(2361) }, new Item(4823, 15)),

		ADAMANT_ARROWHEADS(75, 62.5, new Item[] { new Item(2361) }, new Item(43, 15)),

		ADAMANT_SCIMITAR(75, 125, new Item[] { new Item(2361, 2) }, new Item(1331)),

		OFF_HAND_ADAMANT_SCIMITAR(75, 125, new Item[] { new Item(2361, 2) }, new Item(25753)),

		ADAMANT_PICKAXE(75, 125, new Item[] { new Item(2361, 2) }, new Item(1271)),

		ADAMANT_LIMBS(76, 62.5, new Item[] { new Item(2361) }, new Item(9429)),

		ADAMANT_LONGSWORD(76, 125, new Item[] { new Item(2361, 2) }, new Item(1301)),

		OFF_HAND_ADAMANT_LONGSWORD(76, 125, new Item[] { new Item(2361, 2) }, new Item(25735)),

		ADAMANT_KNIFE(77, 62.5, new Item[] { new Item(2361) }, new Item(867)),

		OFF_HAND_ADAMANT_KNIFE(77, 62.5, new Item[] { new Item(2361) }, new Item(25900)),

		ADAMANT_THOWING_AXE(77, 62.5, new Item[] { new Item(2361) }, new Item(804, 5)),

		OFF_HAND_ADAMANT_THOWING_AXE(77, 62.5, new Item[] { new Item(2361) }, new Item(25907, 5)),

		ADAMANT_FULL_HELM(77, 62.5, new Item[] { new Item(2361, 2) }, new Item(1161)),

		ADAMANT_SQ_SHIELD(78, 125, new Item[] { new Item(2361, 2) }, new Item(1183)),

		ADAMANT_WARHAMMER(79, 187.5, new Item[] { new Item(2361, 3) }, new Item(1345)),

		OFF_HAND_ADAMANT_WARHAMMER(79, 187.5, new Item[] { new Item(2361, 3) }, new Item(25789)),

		ADAMANT_BATTLEAXE(80, 187.5, new Item[] { new Item(2361, 3) }, new Item(1371)),

		OFF_HAND_ADAMANT_BATTLEAXE(80, 187.5, new Item[] { new Item(2361, 3) }, new Item(25771)),

		ADAMANT_CHAINBODY(81, 187.5, new Item[] { new Item(2361, 3) }, new Item(1111)),

		ADAMANT_KITESHIELD(82, 187.5, new Item[] { new Item(2361, 3) }, new Item(1199)),

		ADAMANT_CLAW(83, 125, new Item[] { new Item(2361, 2) }, new Item(3100)),

		OFF_HAND_ADAMANT_CLAW(83, 125, new Item[] { new Item(2361, 2) }, new Item(25943)),

		ADAMANT_2H_SWORD(84, 187.5, new Item[] { new Item(2361, 3) }, new Item(1317)),

		ADAMANT_PLATELEGS(86, 187.5, new Item[] { new Item(2361, 3) }, new Item(1073)),

		ADAMANT_PLATESKIRT(86, 187.5, new Item[] { new Item(2361, 3) }, new Item(1091)),

		ADAMANT_PLATEBODY(88, 312.5, new Item[] { new Item(2361, 5) }, new Item(1123)),

		
		RUNE_DAGGER(85, 75, new Item[] { new Item(2363) }, new Item(1213)),

		OFF_HAND_RUNE_DAGGER(85, 75, new Item[] { new Item(2363) }, new Item(25704)),

		RUNE_HATCHET(86, 75, new Item[] { new Item(2363) }, new Item(1359)),

		RUNE_MACE(87, 75, new Item[] { new Item(2363) }, new Item(1432)),

		OFF_HAND_RUNE_MACE(87, 75, new Item[] { new Item(2363) }, new Item(25686)),

		RUNE_HELM(88, 75, new Item[] { new Item(2363) }, new Item(1147)),

		RUNE_BOLTS(88, 75, new Item[] { new Item(2363) }, new Item(9381, 10)),

		RUNE_SWORD(89, 75, new Item[] { new Item(2363) }, new Item(1289)),

		OFF_HAND_RUNE_SWORD(89, 75, new Item[] { new Item(2363) }, new Item(25722)),

		RUNE_DART_TIP(89, 75, new Item[] { new Item(2363) }, new Item(824)),

		RUNE_NAILS(89, 75, new Item[] { new Item(2363) }, new Item(4824, 15)),

		RUNE_ARROWHEADS(90, 75, new Item[] { new Item(2363) }, new Item(44, 15)),

		RUNE_SCIMITAR(90, 150, new Item[] { new Item(2363, 2) }, new Item(1333)),

		OFF_HAND_RUNE_SCIMITAR(90, 150, new Item[] { new Item(2363, 2) }, new Item(25755)),

		RUNE_PICKAXE(90, 150, new Item[] { new Item(2363, 2) }, new Item(1275)),

		RUNE_LIMBS(91, 75, new Item[] { new Item(2363) }, new Item(9431)),

		RUNE_LONGSWORD(91, 150, new Item[] { new Item(2363, 2) }, new Item(1303)),

		OFF_HAND_RUNE_LONGSWORD(91, 150, new Item[] { new Item(2363, 2) }, new Item(25737)),

		RUNE_KNIFE(92, 75, new Item[] { new Item(2363) }, new Item(868)),

		OFF_HAND_RUNE_KNIFE(92, 75, new Item[] { new Item(2363) }, new Item(25901)),

		RUNE_THOWING_AXE(92, 75, new Item[] { new Item(2363) }, new Item(805, 5)),

		OFF_HAND_RUNE_THOWING_AXE(92, 75, new Item[] { new Item(2363) }, new Item(25908, 5)),

		RUNE_FULL_HELM(92, 75, new Item[] { new Item(2363, 2) }, new Item(1163)),

		RUNE_SQ_SHIELD(93, 150, new Item[] { new Item(2363, 2) }, new Item(1185)),

		RUNE_WARHAMMER(94, 225, new Item[] { new Item(2363, 3) }, new Item(1347)),

		OFF_HAND_RUNE_WARHAMMER(94, 225, new Item[] { new Item(2363, 3) }, new Item(25791)),

		RUNE_BATTLEAXE(95, 225, new Item[] { new Item(2363, 3) }, new Item(1373)),

		OFF_HAND_RUNE_BATTLEAXE(95, 225, new Item[] { new Item(2363, 3) }, new Item(25773)),

		RUNE_CHAINBODY(96, 225, new Item[] { new Item(2363, 3) }, new Item(1113)),

		RUNE_KITESHIELD(97, 225, new Item[] { new Item(2363, 3) }, new Item(1201)),

		RUNE_CLAW(98, 150, new Item[] { new Item(2363, 2) }, new Item(3101)),

		OFF_HAND_RUNE_CLAW(98, 150, new Item[] { new Item(2363, 2) }, new Item(25945)),

		RUNE_2H_SWORD(99, 225, new Item[] { new Item(2363, 3) }, new Item(1319)),

		RUNE_PLATELEGS(99, 225, new Item[] { new Item(2363, 3) }, new Item(1079)),

		RUNE_PLATESKIRT(99, 225, new Item[] { new Item(2363, 3) }, new Item(1093)),

		RUNE_PLATEBODY(99, 375, new Item[] { new Item(2363, 5) }, new Item(1127));



		public static ForgingBar getBarByProduce(int id) {
			for(ForgingBar bar : ForgingBar.values()) {
			if(bar.getProducedItem().getId() == id)
				return bar;
			}
			return null;
		}

		public static ForgingBar getBar(int id) {
			for(ForgingBar bar : ForgingBar.values()) {
			for(Item item : bar.getItemsRequired())
				if(item.getId() == id)
				return bar;
			}
			return null;
		}

		public static ForgingBar getBar(Player player) {
	    	for(ForgingBar bar : ForgingBar.values()) {
				for(Item item : bar.getItemsRequired())
		    		if(player.getInventory().containsItems(new Item(item.getId())))
						return bar;
	    	}
	    return null;
	}

	private int levelRequired;
	private double experience;
	private Item[] barsRequired;
	private Item producedItem;
	

	private ForgingBar(int levelRequired, double experience, Item[] itemsRequired, Item producedItem) {
	    this.levelRequired = levelRequired;
	    this.experience = experience;
	    this.barsRequired = itemsRequired;
	    this.producedItem = producedItem;
	}

	public Item[] getItemsRequired() {
	    return barsRequired;
	}

	public int getLevelRequired() {
	    return levelRequired;
	}

	public Item getProducedItem() {
	    return producedItem;
	}

	public double getExperience() {
	    return experience;
	}

    }

    
    
    public ForgingBar bar;
    public WorldObject object;
    public int ticks;

    public Smithing(ForgingBar bar, WorldObject object, int ticks) {
	this.object = object;
	this.bar = bar;
	this.ticks = ticks;
    }

    @Override
    public boolean start(Player player) {
	if (bar == null || player == null || object == null) {
	    return false;
	}
	if (!player.getInventory().containsItem(HAMMER, 1)) {
	    player.getPackets().sendGameMessage("You need a hammer to create a " + bar.getProducedItem().getDefinitions().getName() + ".");
	    return false;
	}
	if (bar.getItemsRequired().length > 1) {
	    if (!player.getInventory().containsItemToolBelt(bar.getItemsRequired()[0].getId(), bar.getItemsRequired()[0].getAmount())) {
		player.getPackets().sendGameMessage("You need " + bar.getItemsRequired()[0].getAmount() + " " + bar.getItemsRequired()[0].getDefinitions().getName() + "'s to create a " + bar.getProducedItem().getDefinitions().getName() + ".");
		return false;
	    }
	}
	if (player.getSkills().getLevel(Skills.SMITHING) < bar.getLevelRequired()) {
	    player.getPackets().sendGameMessage("You need a Smithing level of at least " + bar.getLevelRequired() + " to create " + bar.getProducedItem().getDefinitions().getName());
	    return false;
	}
	player.getPackets().sendGameMessage("You place the required bars and attempt to create a " + bar.getProducedItem().getDefinitions().getName() + ".", true);
	return true;
    }

    @Override
    public boolean process(Player player) {
	if (bar == null || player == null || object == null) {
	    return false;
	}
	if (!player.getInventory().containsItem(HAMMER, 1)) {
	    player.getPackets().sendGameMessage("You need a hammer to create a " + bar.getProducedItem().getDefinitions().getName() + ".");
	    return false;
	}
	if (bar.getItemsRequired().length > 1) {
	    if (!player.getInventory().containsItemToolBelt(bar.getItemsRequired()[0].getId(), bar.getItemsRequired()[0].getAmount())) {
		player.getPackets().sendGameMessage("You need " + bar.getItemsRequired()[0].getAmount() + " " + bar.getItemsRequired()[0].getDefinitions().getName() + "'s to create a " + bar.getProducedItem().getDefinitions().getName() + ".");
		return false;
	    }
	}
	if (player.getSkills().getLevel(Skills.SMITHING) < bar.getLevelRequired()) {
	    player.getPackets().sendGameMessage("You need a Smithing level of at least " + bar.getLevelRequired() + " to create " + bar.getProducedItem().getDefinitions().getName());
	    return false;
	}
	player.faceObject(object);
	return true;
    }

    @Override
    public int processWithDelay(Player player) {
	ticks--;
	player.setNextAnimation(new Animation(898));
	double xp = bar.getExperience() * 1;
	player.getSkills().addXp(Skills.SMITHING, xp);
	
	
	if (random == 57 && !player.getInventory().containsItem(32084, 1) && !player.getBank().containsItem(32084) && player.getInventory().getFreeSlots() >= 1 && player.getSkills().getLevelForXp(Skills.AGILITY) >= 99){
	    player.getInventory().addItem(32084, 1);
	    player.getPackets().sendGameMessage("<col=a6aba6>As you are begin smithing, a lump of crystal detaches itself from the bar, you quickly place it in your backpack.");
	}
	
	else if (random == 57 && !player.getInventory().containsItem(32084, 1) && !player.getBank().containsItem(32084) && player.getInventory().getFreeSlots() == 0 && player.getSkills().getLevelForXp(Skills.AGILITY) >= 99){
	    player.getBank().addItem(32084, 1, true);
	    player.getPackets().sendGameMessage("<col=a6aba6>As you are begin smithing, a lump of crystal detaches itself from the bar, the crystal has been sent to your bank.");
	}
	
	
	for (Item required : bar.getItemsRequired()) {
	    if (required.getId() == 4 || required.getId() == 2976 || required.getId() == 1594 || required.getId() == 1599 || required.getId() == 5523)
		continue;
	    player.getInventory().deleteItem(required.getId(), required.getAmount());
	}
	int amount = bar.getProducedItem().getAmount();
	if (bar.getProducedItem().getDefinitions().isStackable())
	    amount *= 1;
	player.getInventory().addItem(bar.getProducedItem().getId(), amount);
	player.getPackets().sendGameMessage("You have successfully created a " + bar.getProducedItem().getDefinitions().getName() + ".", true);
	if (ticks > 0) {
	    return 3;
	}
	return -1;
    }

    @Override
    public void stop(Player player) {
	setActionDelay(player, 3);
    }
}