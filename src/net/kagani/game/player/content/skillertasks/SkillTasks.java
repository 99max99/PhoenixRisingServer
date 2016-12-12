package net.kagani.game.player.content.skillertasks;

import java.io.Serializable;

import net.kagani.game.player.Skills;

public enum SkillTasks implements Serializable {

	/**
	 * @author: King Fox
	 * @author: Miles
	 * @author: Dylan Page
	 */

	/**
	 * Agility
	 */
	BARB1("Barbarian Laps Easy",
			"Complete 5 laps around the Barbarian Agility Course.",
			SkillerTasks.EASY, 5, Skills.AGILITY, 35), BARB2(
			"Barbarian Laps Medium",
			"Complete 10 laps around the Barbarian Agility Course.",
			SkillerTasks.MEDIUM, 10, Skills.AGILITY, 35), BARB3(
			"Barbarian Adv Laps Hard",
			"Complete 25 laps around the Barbarian Adv Agility Course.",
			SkillerTasks.HARD, 25, Skills.AGILITY, 90), BARB4(
			"Barbarian Adv Laps Elite",
			"Complete 50 laps around the Barbarian Adv Agility Course.",
			SkillerTasks.ELITE, 50, Skills.AGILITY, 90), GNOME1(
			"Gnome Laps Easy",
			"Complete 5 laps around the Gnome Agility Course.",
			SkillerTasks.EASY, 5, Skills.AGILITY, 1), GNOME2(
			"Gnome Laps Medium",
			"Complete 10 laps around the Gnome Agility Course.",
			SkillerTasks.MEDIUM, 10, Skills.AGILITY, 1), GNOME3(
			"Gnome Laps Hard",
			"Complete 25 laps around the Gnome Adv Agility Course.",
			SkillerTasks.HARD, 25, Skills.AGILITY, 85), WILD1(
			"Wilderness Laps Medium",
			"Complete 10 laps around the Wilderness Agility Course.",
			SkillerTasks.MEDIUM, 10, Skills.AGILITY, 52), WILD2(
			"Wilderness Laps Hard",
			"Complete 25 laps around the Wilderness Agility Course.",
			SkillerTasks.HARD, 25, Skills.AGILITY, 52), WILD3(
			"Wilderness Laps Elite",
			"Complete 50 laps around the Wilderness Agility Course.",
			SkillerTasks.ELITE, 50, Skills.AGILITY, 52),

	/**
	 * Divination
	 */
	DIV1("Divinaton Easy", "Harvest 25 pale wisps.", SkillerTasks.EASY, 25,
			Skills.DIVINATION, 1), DIV1_2("Divinaton Medium",
			"Harvest 50 pale wisps.", SkillerTasks.MEDIUM, 50,
			Skills.DIVINATION, 1), DIV1_3("Divinaton Hard",
			"Harvest 100 pale wisps.", SkillerTasks.HARD, 100,
			Skills.DIVINATION, 1), DIV1_4("Divinaton Elite",
			"Harvest 250 pale wisps.", SkillerTasks.ELITE, 250,
			Skills.DIVINATION, 1), DIV2("Divinaton Easy",
			"Harvest 25 gleaming wisps.", SkillerTasks.EASY, 25,
			Skills.DIVINATION, 50), DIV2_2("Divinaton Medium",
			"Harvest 50 gleaming wisps.", SkillerTasks.MEDIUM, 50,
			Skills.DIVINATION, 50), DIV2_3("Divinaton Hard",
			"Harvest 100 gleaming wisps.", SkillerTasks.HARD, 100,
			Skills.DIVINATION, 50), DIV2_4("Divinaton Elite",
			"Harvest 250 gleaming wisps.", SkillerTasks.ELITE, 250,
			Skills.DIVINATION, 50), DIV3("Divinaton Easy",
			"Harvest 25 brilliant wisps.", SkillerTasks.EASY, 25,
			Skills.DIVINATION, 80), DIV3_2("Divinaton Medium",
			"Harvest 50 brilliant wisps.", SkillerTasks.MEDIUM, 50,
			Skills.DIVINATION, 80), DIV3_3("Divinaton Hard",
			"Harvest 100 brilliant wisps.", SkillerTasks.HARD, 100,
			Skills.DIVINATION, 80), DIV3_4("Divinaton Elite",
			"Harvest 250 brilliant wisps.", SkillerTasks.ELITE, 250,
			Skills.DIVINATION, 80), DIV4("Divinaton Easy",
			"Harvest 25 incandescent wisps.", SkillerTasks.EASY, 25,
			Skills.DIVINATION, 95), DIV4_2("Divinaton Medium",
			"Harvest 50 incandescent wisps.", SkillerTasks.MEDIUM, 50,
			Skills.DIVINATION, 95), DIV4_3("Divinaton Hard",
			"Harvest 100 incandescent wisps.", SkillerTasks.HARD, 100,
			Skills.DIVINATION, 95), DIV4_4("Divinaton Elite",
			"Harvest 250 incandescent wisps.", SkillerTasks.ELITE, 250,
			Skills.DIVINATION, 95),

	/**
	 * Cooking
	 */
	CANCHOVIES1("Cook Anchovies Easy", "Cook 50 Raw Anchovies.",
			SkillerTasks.EASY, 50, Skills.COOKING, 1), CANCHOVIES2(
			"Cook Anchovies Medium", "Cook 100 Raw Anchovies.",
			SkillerTasks.MEDIUM, 100, Skills.COOKING, 1), CHERRING1(
			"Cook Herring Easy", "Cook 50 Raw Herring.", SkillerTasks.EASY, 50,
			Skills.COOKING, 5), CHERRING2("Cook Herring Mediun",
			"Cook 100 Raw Herring.", SkillerTasks.MEDIUM, 100, Skills.COOKING,
			5), CLOBSTER1("Cook Lobster Medium", "Cook 100 Raw Lobster.",
			SkillerTasks.MEDIUM, 100, Skills.COOKING, 40), CLOBSTER2(
			"Cook Lobster Hard", "Cook 250 Raw Lobster.", SkillerTasks.HARD,
			250, Skills.COOKING, 40), CROCKTAIL1("Cook Rocktail Hard",
			"Cook 100 Raw Rocktail.", SkillerTasks.HARD, 100, Skills.COOKING,
			93), CROCKTAIL2("Cook Rocktail Elite", "Cook 250 Raw Rocktail.",
			SkillerTasks.ELITE, 250, Skills.COOKING, 93), CSALMON1(
			"Cook Salmon Easy", "Cook 50 Raw Salmon.", SkillerTasks.EASY, 50,
			Skills.COOKING, 25), CSALMON2("Cook Salmon Medium",
			"Cook 100 Raw Salmon.", SkillerTasks.MEDIUM, 100, Skills.COOKING,
			25), CSHARK1("Cook Shark Medium", "Cook 100 Raw Shark.",
			SkillerTasks.MEDIUM, 100, Skills.COOKING, 80), CSHARK2(
			"Cook Shark Hard", "Cook 150 Raw Shark.", SkillerTasks.HARD, 150,
			Skills.COOKING, 80), CSHARK3("Cook Shark Elite",
			"Cook 250 Raw Shark.", SkillerTasks.ELITE, 250, Skills.COOKING, 80), CSHRIMP1(
			"Cook Shrimp Easy", "Cook 50 Raw Shrimp.", SkillerTasks.EASY, 50,
			Skills.COOKING, 1), CSHRIMP2("Cook Shrimp Medium",
			"Cook 100 Raw Shrimp.", SkillerTasks.MEDIUM, 100, Skills.COOKING, 1), CSWORD1(
			"Cook Swordfish Medium", "Cook 100 Raw Swordfish.",
			SkillerTasks.MEDIUM, 100, Skills.COOKING, 45), CSWORD2(
			"Cook Swordfish Hard", "Cook 150 Raw Swordfish.",
			SkillerTasks.HARD, 150, Skills.COOKING, 45), CTROUT1(
			"Cook Trout Easy", "Cook 100 Raw Trout.", SkillerTasks.EASY, 100,
			Skills.COOKING, 15), CTROUT2("Cook Trout Medium",
			"Cook 100 Raw Trout.", SkillerTasks.MEDIUM, 100, Skills.COOKING, 15), CTUNA1(
			"Cook Tuna Easy", "Cook 50 Raw Tuna.", SkillerTasks.EASY, 50,
			Skills.COOKING, 30), CTUNA2("Cook Tuna", "Cook 100 Raw Tuna.",
			SkillerTasks.MEDIUM, 100, Skills.COOKING, 30),

	/**
	 * Farming
	 */
	FLOWER1("Harvest Flowers Easy", "Harvest 10 Flowers.", SkillerTasks.EASY,
			10, Skills.FARMING, 2), FLOWER2("Harvest Flowers Medium",
			"Harvest 25 Flowers.", SkillerTasks.MEDIUM, 25, Skills.FARMING, 2), FLOWER3(
			"Harvest Flowers Hard", "Harvest 40 Flowers.", SkillerTasks.HARD,
			40, Skills.FARMING, 2), FLOWER4("Harvest Flowers Elite",
			"Harvest 55 Flowers.", SkillerTasks.ELITE, 55, Skills.FARMING, 2), FRUIT1(
			"Harvest Fruit Easy", "Harvest 10 Fruit.", SkillerTasks.EASY, 10,
			Skills.FARMING, 27), FRUIT2("Harvest Fruit Medium",
			"Harvest 25 Fruit.", SkillerTasks.MEDIUM, 25, Skills.FARMING, 27), FRUIT3(
			"Harvest Fruit Hard", "Harvest 40 Fruit.", SkillerTasks.HARD, 40,
			Skills.FARMING, 27), FRUIT4("Harvest Fruit Elite",
			"Harvest 55 Fruit.", SkillerTasks.ELITE, 55, Skills.FARMING, 27), HERB1(
			"Harvest Herbs Easy", "Harvest 10 Herbs.", SkillerTasks.EASY, 10,
			Skills.FARMING, 9), HERB2("Harvest Herbs Medium",
			"Harvest 25 Herbs.", SkillerTasks.MEDIUM, 25, Skills.FARMING, 9), HERB3(
			"Harvest Herbs Hard", "Harvest 40 Herbs.", SkillerTasks.HARD, 40,
			Skills.FARMING, 9), HERB4("Harvest Herbs Elite",
			"Harvest 55 Herbs.", SkillerTasks.ELITE, 55, Skills.FARMING, 9), RAKE1(
			"Rake Weeds Easy", "You must rake any patch 50 times.",
			SkillerTasks.EASY, 50, Skills.FARMING, 1), RAKE2(
			"Rake Weeds Medium", "You must rake any patch 120 times.",
			SkillerTasks.MEDIUM, 120, Skills.FARMING, 1), VEG1(
			"Harvest Vegetables Easy", "Harvest 10 Vegetables.",
			SkillerTasks.EASY, 10, Skills.FARMING, 1), VEG2(
			"Harvest Vegetables Medium", "Harvest 25 Vegetables.",
			SkillerTasks.MEDIUM, 25, Skills.FARMING, 1), VEG3(
			"Harvest Vegetables Hard", "Harvest 40 Vegetables.",
			SkillerTasks.HARD, 40, Skills.FARMING, 1), VEG4(
			"Harvest Vegetables Elite", "Harvest 55 Vegetables.",
			SkillerTasks.ELITE, 55, Skills.FARMING, 1),

	/**
	 * Firemaking
	 */
	FBON1("Fuel Bonfire Medium", "Add 100 logs into a Bonfire.",
			SkillerTasks.MEDIUM, 100, Skills.FIREMAKING, 1), FBON2(
			"Fuel Bonfire Hard", "Add 250 logs into a Bonfire.",
			SkillerTasks.HARD, 250, Skills.FIREMAKING, 1), FBON3(
			"Fuel Bonfire Elite", "Add 500 logs into a Bonfire.",
			SkillerTasks.ELITE, 500, Skills.FIREMAKING, 1), FOAK1(
			"Burn Oak Easy", "Burn 50 Oak Logs.", SkillerTasks.EASY, 50,
			Skills.FIREMAKING, 15), FOAK2("Burn Oak Medium",
			"Burn 100 Oak Logs.", SkillerTasks.MEDIUM, 100, Skills.FIREMAKING,
			15), FMAGIC1("Magic Logs Hard", "Burn 100 Magic Logs.",
			SkillerTasks.HARD, 100, Skills.FIREMAKING, 75), FMAGIC2(
			"Magic Logs Elite", "Burn 250 Magic Logs.", SkillerTasks.ELITE,
			250, Skills.FIREMAKING, 75), FMAPLE1("Burn Maple Medium",
			"Burn 100 Maple Logs.", SkillerTasks.MEDIUM, 100,
			Skills.FIREMAKING, 45), FMAPLE2("Burn Maple Hard",
			"Burn 250 Maple Logs.", SkillerTasks.HARD, 250, Skills.FIREMAKING,
			45), FNORMAL1("Burn Normal Easy", "Burn 50 Regular Logs.",
			SkillerTasks.EASY, 50, Skills.FIREMAKING, 1), FNORMAL2(
			"Burn Normal Medium", "Burn 100 Regular Logs.",
			SkillerTasks.MEDIUM, 100, Skills.FIREMAKING, 1), FWILLOW1(
			"Burn Willow Easy", "Burn 50 Willow Logs.", SkillerTasks.EASY, 50,
			Skills.FIREMAKING, 30), FWILLOW2("Burn Willow Medium",
			"Burn 100 Willow Logs.", SkillerTasks.MEDIUM, 100,
			Skills.FIREMAKING, 30), FWILLOW3("Burn Willow Hard",
			"Burn 250 Willow Logs.", SkillerTasks.HARD, 250, Skills.FIREMAKING,
			30), FYEW1("Burn Yew Medium", "Burn 50 Yew Logs.",
			SkillerTasks.MEDIUM, 50, Skills.FIREMAKING, 60), FYEW2(
			"Burn Yew Hard", "Burn 100 Yew Logs.", SkillerTasks.HARD, 100,
			Skills.FIREMAKING, 60), FYEW3("Burn Yew Elite",
			"Burn 250 Yew Logs.", SkillerTasks.ELITE, 250, Skills.FIREMAKING,
			60),

	/**
	 * Fishing
	 */
	FANCHOVIES1("Fish Anchovies Easy", "Catch 50 Raw Anchovies.",
			SkillerTasks.EASY, 50, Skills.FISHING, 15), FANCHOVIES2(
			"Fish Anchovies Medium", "Catch 100 Raw Anchovies.",
			SkillerTasks.MEDIUM, 100, Skills.FISHING, 15), FHERRING1(
			"Fish Herring Easy", "Catch 50 Raw Herring.", SkillerTasks.EASY,
			50, Skills.FISHING, 10), FHERRING2("Fish Herring Medium",
			"Catch 100 Raw Herring.", SkillerTasks.MEDIUM, 100, Skills.FISHING,
			10), FLOBSTER1("Fish Lobster Medium", "Catch 100 Raw Lobster.",
			SkillerTasks.MEDIUM, 100, Skills.FISHING, 40), FLOBSTER2(
			"Fish Lobster Hard", "Catch 250 Raw Lobster.", SkillerTasks.HARD,
			250, Skills.FISHING, 40), FROCKTAIL1("Fish Rocktail Hard",
			"Catch 100 Raw Rocktail.", SkillerTasks.HARD, 100, Skills.FISHING,
			90), FROCKTAIL2("Fish Rocktail Elite", "Catch 250 Raw Rocktail.",
			SkillerTasks.ELITE, 250, Skills.FISHING, 90), FSALMON1(
			"Fish Salmon Easy", "Catch 50 Raw Salmon.", SkillerTasks.EASY, 50,
			Skills.FISHING, 30), FSALMON2("Fish Salmon Medium",
			"Catch 100 Raw Salmon.", SkillerTasks.MEDIUM, 100, Skills.FISHING,
			30), FSHARK1("Fish Shark Medium", "Catch 50 Raw Shark.",
			SkillerTasks.MEDIUM, 50, Skills.FISHING, 76), FSHARK2(
			"Fish Shark Hard", "Catch 100 Raw Shark.", SkillerTasks.HARD, 100,
			Skills.FISHING, 76), FSHARK3("Fish Shark Elite",
			"Catch 250 Raw Shark.", SkillerTasks.ELITE, 250, Skills.FISHING, 76), FSHRIMP1(
			"Fish Shrimp Easy", "Catch 50 Raw Shrimp.", SkillerTasks.EASY, 50,
			Skills.FISHING, 1), FSHRIMP2("Fish Shrimp Medium",
			"Catch 100 Raw Shrimp.", SkillerTasks.MEDIUM, 100, Skills.FISHING,
			1), FSWORD1("Fish Swordfish Medium", "Catch 100 Raw Swordfish.",
			SkillerTasks.MEDIUM, 100, Skills.FISHING, 50), FSWORD2(
			"Fish Swordfish Hard", "Catch 250 Raw Swordfish.",
			SkillerTasks.HARD, 250, Skills.FISHING, 50), FTROUT1(
			"Fish Trout Easy", "Catch 100 Raw Trout.", SkillerTasks.EASY, 100,
			Skills.FISHING, 20), FTROUT2("Fish Trout Medium",
			"Catch 250 Raw Trout.", SkillerTasks.MEDIUM, 250, Skills.FISHING,
			20), FTUNA1("Fish Tuna Easy", "Catch 50 Raw Tuna.",
			SkillerTasks.EASY, 50, Skills.FISHING, 35), FTUNA2(
			"Fish Tuna Medium", "Catch 100 Raw Tuna.", SkillerTasks.MEDIUM,
			100, Skills.FISHING, 35),

	/**
	 * Herblore
	 */
	CLEAN1("Clean Herbs Easy", "Clean 20 Grimy Herbs.", SkillerTasks.EASY, 20,
			Skills.HERBLORE, 1), CLEAN2("Clean Herbs Medium",
			"Clean 50 Grimy Herbs.", SkillerTasks.MEDIUM, 50, Skills.HERBLORE,
			1), CLEAN3("Clean Herbs Hard", "Clean 100 Grimy Herbs.",
			SkillerTasks.HARD, 100, Skills.HERBLORE, 1), CLEAN4(
			"Clean Herbs Elite", "Clean 250 Grimy Herbs.", SkillerTasks.ELITE,
			250, Skills.HERBLORE, 1), POTION1("Create Potions Easy",
			"Create 20 Potions.", SkillerTasks.EASY, 20, Skills.HERBLORE, 1), POTION2(
			"Create Potions Medium", "Create 50 Potions.", SkillerTasks.MEDIUM,
			50, Skills.HERBLORE, 1), POTION3("Create Potions Hard",
			"Create 100 Potions.", SkillerTasks.HARD, 100, Skills.HERBLORE, 1), POTION4(
			"Create Potions Elite", "Create 250 Potions.", SkillerTasks.ELITE,
			250, Skills.HERBLORE, 1),

	/**
	 * Hunter
	 */
	BIRD1("Bird Snare Easy", "Catch 50 birds using a Bird Snare.",
			SkillerTasks.EASY, 50, Skills.HUNTER, 1), BIRD2(
			"Bird Snare Medium", "Catch 100 birds using a Bird Snare.",
			SkillerTasks.MEDIUM, 100, Skills.HUNTER, 1), BIRD3(
			"Bird Snare Hard", "Catch 250 birds using a Bird Snare.",
			SkillerTasks.HARD, 250, Skills.HUNTER, 1), BIRD4(
			"Bird Snare Elite", "Catch 500 birds using a Bird Snare.",
			SkillerTasks.ELITE, 500, Skills.HUNTER, 1), BOX1("Box Trap Easy",
			"Catch 50 animals using a Box Trap.", SkillerTasks.EASY, 50,
			Skills.HUNTER, 27), BOX2("Box Trap Medium",
			"Catch 100 animals using a Box Trap.", SkillerTasks.MEDIUM, 100,
			Skills.HUNTER, 27), BOX3("Box Trap Hard",
			"Catch 250 animals using a Box Trap.", SkillerTasks.HARD, 250,
			Skills.HUNTER, 27), BOX4("Box Trap Elite",
			"Catch 500 animals using a Box Trap.", SkillerTasks.ELITE, 500,
			Skills.HUNTER, 27), NET1("Butterfly Net Easy",
			"Catch 50 critters using a Butterfly Net.", SkillerTasks.EASY, 50,
			Skills.HUNTER, 15), NET2("Butterfly Net Medium",
			"Catch 100 critters using a Butterfly Net.", SkillerTasks.MEDIUM,
			100, Skills.HUNTER, 15), NET3("Butterfly Net Hard",
			"Catch 250 critters using a Butterfly Net.", SkillerTasks.HARD,
			250, Skills.HUNTER, 15), NET4("Butterfly Net Elite",
			"Catch 500 critters using a Butterfly Net.", SkillerTasks.ELITE,
			500, Skills.HUNTER, 15),

	/**
	 * Mining
	 */
	MADAMANT1("Mine Adamant Hard", "Mine 100 ores from an Adamant Rock.",
			SkillerTasks.HARD, 100, Skills.MINING, 70), MADAMANT2(
			"Mine Adamant Elite", "Mine 250 ores from an Adamant Rock.",
			SkillerTasks.ELITE, 250, Skills.MINING, 70), MCOAL1(
			"Mine Coal Easy", "Mine 50 ores from a Coal Rock.",
			SkillerTasks.EASY, 500, Skills.MINING, 30), MCOAL2(
			"Mine Coal Medium", "Mine 100 ores from a Coal Rock.",
			SkillerTasks.MEDIUM, 100, Skills.MINING, 30), MCOAL3(
			"Mine Coal Hard", "Mine 250 ores from a Coal Rock.",
			SkillerTasks.HARD, 250, Skills.MINING, 30), MCOPPER1(
			"Mine Copper Easy", "Mine 50 ores from a Copper Rock.",
			SkillerTasks.EASY, 50, Skills.MINING, 1), MCOPPER2(
			"Mine Copper Medium", "Mine 250 ores from a Copper Rock.",
			SkillerTasks.MEDIUM, 250, Skills.MINING, 1), MGEM1(
			"Mine Gems Medium", "Mine 100 gems from a Gem Rock.",
			SkillerTasks.MEDIUM, 100, Skills.MINING, 40), MGEM2(
			"Mine Gems Medium", "Mine 115 gems from a Gem Rock.",
			SkillerTasks.MEDIUM, 115, Skills.MINING, 40), MGEM3(
			"Mine Gems Medium", "Mine 120 gems from a Gem Rock.",
			SkillerTasks.MEDIUM, 120, Skills.MINING, 40), MGEM4(
			"Mine Gems Medium", "Mine 125 gems from a Gem Rock.",
			SkillerTasks.MEDIUM, 125, Skills.MINING, 40), MESSENCE1(
			"Mine Essence Medium", "Mine 50 ores from an Essence Mine.",
			SkillerTasks.MEDIUM, 50, Skills.MINING, 1), MESSENCE2(
			"Mine Essence Medium", "Mine 100 ores from an Essence Mine.",
			SkillerTasks.MEDIUM, 100, Skills.MINING, 1), MESSENCE3(
			"Mine Essence Medium", "Mine 250 ores from an Essence Mine.",
			SkillerTasks.MEDIUM, 250, Skills.MINING, 1), MESSENCE4(
			"Mine Essence Medium", "Mine 500 ores from an Essence Mine.",
			SkillerTasks.MEDIUM, 500, Skills.MINING, 1), MGOLD1(
			"Mine Gold Easy", "Mine 50 ores from a Gold Rock.",
			SkillerTasks.EASY, 50, Skills.MINING, 40), MGOLD2(
			"Mine Gold Medium", "Mine 100 ores from a Gold Rock.",
			SkillerTasks.MEDIUM, 100, Skills.MINING, 40), MGOLD3(
			"Mine Gold Hard", "Mine 250 ores from a Gold Rock.",
			SkillerTasks.HARD, 250, Skills.MINING, 40), MIRON1(
			"Mine Iron Easy", "Mine 50 ores from a Iron Rock.",
			SkillerTasks.EASY, 50, Skills.MINING, 15), MIRON2(
			"Mine Iron Medium", "Mine 100 ores from a Iron Rock.",
			SkillerTasks.MEDIUM, 100, Skills.MINING, 15), MIRON3(
			"Mine Iron Hard", "Mine 250 ores from a Iron Rock.",
			SkillerTasks.HARD, 250, Skills.MINING, 15), MMITHRIL1(
			"Mine Mithril Medium", "Mine 100 ores from a Mithril Rock.",
			SkillerTasks.MEDIUM, 100, Skills.MINING, 55), MMITHRIL2(
			"Mine Mithril Hard", "Mine 250 ores from a Mithril Rock.",
			SkillerTasks.HARD, 250, Skills.MINING, 55), MRUNE1(
			"Mine Rune Hard", "Mine 100 ores from a Rune Rock.",
			SkillerTasks.HARD, 100, Skills.MINING, 85), MRUNE2(
			"Mine Rune Elite", "Mine 250 ores from a Rune Rock.",
			SkillerTasks.ELITE, 250, Skills.MINING, 85), MSILVER1(
			"Mine Silver Easy", "Mine 50 ores from a Silver Rock.",
			SkillerTasks.EASY, 50, Skills.MINING, 20), MSILVER2(
			"Mine Silver Medium", "Mine 100 ores from a Silver Rock.",
			SkillerTasks.MEDIUM, 100, Skills.MINING, 20), MSILVER3(
			"Mine Silver Hard", "Mine 250 ores from a Silver Rock.",
			SkillerTasks.HARD, 250, Skills.MINING, 20), MTIN1("Mine Tin Easy",
			"Mine 50 ores from a Tin Rock.", SkillerTasks.EASY, 50,
			Skills.MINING, 1), MTIN2("Mine Tin Medium",
			"Mine 100 ores from a Tin Rock.", SkillerTasks.MEDIUM, 100,
			Skills.MINING, 1), SERENSTONES1("Mine Seren Stones Easy",
			"Mine 25 Seren Stones.", SkillerTasks.EASY, 25, Skills.MINING, 89), SERENSTONES2(
			"Mine Seren Stones Medium", "Mine 50 Seren Stones.",
			SkillerTasks.MEDIUM, 50, Skills.MINING, 89), SERENSTONES3(
			"Mine Seren Stones Hard", "Mine 100 Seren Stones.",
			SkillerTasks.HARD, 100, Skills.MINING, 89), SERENSTONES4(
			"Mine Seren Stones Elite", "Mine 250 Seren Stones.",
			SkillerTasks.ELITE, 250, Skills.MINING, 89),

	/**
	 * Runecrafting
	 */
	CRAFT1("Craft Rune Easy", "Craft 100 Runes.", SkillerTasks.EASY, 100,
			Skills.RUNECRAFTING, 1), CRAFT2("Craft Rune Medium",
			"Craft 250 Runes.", SkillerTasks.MEDIUM, 250, Skills.RUNECRAFTING,
			1), CRAFT3("Craft Rune Hard", "Craft 500 Runes.",
			SkillerTasks.HARD, 500, Skills.RUNECRAFTING, 1), CRAFT4(
			"Craft Rune Elite", "Craft 1000 Runes.", SkillerTasks.ELITE, 1000,
			Skills.RUNECRAFTING, 1),

	/**
	 * Smithing
	 */
	SADAMANT1("Smelt Adamant Hard", "Smelt 100 Adamant Bars.",
			SkillerTasks.HARD, 100, Skills.SMITHING, 70), SADAMANT2(
			"Smelt Adamant Elite", "Smelt 250 Adamant Bars.",
			SkillerTasks.ELITE, 250, Skills.SMITHING, 70), SBRONZE1(
			"Smelt Bronze Easy", "Smelt 50 Bronze Bars.", SkillerTasks.EASY,
			50, Skills.SMITHING, 1), SBRONZE2("Smelt Bronze Medium",
			"Smelt 100 Bronze Bars.", SkillerTasks.MEDIUM, 100,
			Skills.SMITHING, 1), SCANNON1("Smelt Cannon Balls Easy",
			"Smelt 50 Cannon Balls.", SkillerTasks.EASY, 50, Skills.SMITHING,
			35), SCANNON2("Smelt Cannon Balls Medium",
			"Smelt 100 Cannon Balls.", SkillerTasks.MEDIUM, 100,
			Skills.SMITHING, 35), SCANNON3("Smelt Cannon Balls Hard",
			"Smelt 250 Cannon Balls.", SkillerTasks.HARD, 250, Skills.SMITHING,
			35), SCANNON4("Smelt Cannon Balls Elite",
			"Smelt 500 Cannon Balls.", SkillerTasks.ELITE, 500,
			Skills.SMITHING, 35), SGOLD1("Smelt Gold Easy",
			"Smelt 50 Gold Bars.", SkillerTasks.EASY, 50, Skills.SMITHING, 40), SGOLD2(
			"Smelt Gold Medium", "Smelt 100 Gold Bars.", SkillerTasks.MEDIUM,
			100, Skills.SMITHING, 40), SGOLD3("Smelt Gold Hard",
			"Smelt 250 Gold Bars.", SkillerTasks.HARD, 250, Skills.SMITHING, 40), SIRON1(
			"Smelt Iron Easy", "Smelt 50 Iron Bars.", SkillerTasks.EASY, 50,
			Skills.SMITHING, 15), SIRON2("Smelt Iron Medium",
			"Smelt 100 Iron Bars.", SkillerTasks.MEDIUM, 100, Skills.SMITHING,
			15), SMITHRIL1("Smelt Mithril Medium", "Smelt 100 Mithril Bars.",
			SkillerTasks.MEDIUM, 100, Skills.SMITHING, 50), SMITHRIL2(
			"Smelt Mithril Hard", "Smelt 250 Mithril Bars.", SkillerTasks.HARD,
			250, Skills.SMITHING, 50), SMITHRIL3("Smelt Mithril Elite",
			"Smelt 500 Mithril Bars.", SkillerTasks.ELITE, 500,
			Skills.SMITHING, 50), SRUNE1("Smelt Rune Hard",
			"Smelt 100 Rune Bars.", SkillerTasks.HARD, 100, Skills.SMITHING, 85), SRUNE2(
			"Smelt Rune Elite", "Smelt 250 Rune Bars.", SkillerTasks.ELITE,
			250, Skills.SMITHING, 85), SSILVER1("Smelt Silver Easy",
			"Smelt 50 Silver Bars.", SkillerTasks.EASY, 50, Skills.SMITHING, 20), SSILVER2(
			"Smelt Silver Medium", "Smelt 100 Silver Bars.",
			SkillerTasks.MEDIUM, 100, Skills.SMITHING, 20), SSTEEL1(
			"Smelt Steel Easy", "Smelt 50 Steel Bars.", SkillerTasks.EASY, 50,
			Skills.SMITHING, 30), SSTEEL2("Smelt Steel Medium",
			"Smelt 100 Steel Bars.", SkillerTasks.MEDIUM, 100, Skills.SMITHING,
			30),

	/**
	 * Thieving
	 */
	POCKET1("Pickpocket Easy", "Steal from 20 People.", SkillerTasks.EASY, 20,
			Skills.THIEVING, 1), POCKET2("Pickpocket Medium",
			"Steal from 50 People.", SkillerTasks.MEDIUM, 50, Skills.THIEVING,
			1), POCKET3("Pickpocket Hard", "Steal from 100 People.",
			SkillerTasks.HARD, 100, Skills.THIEVING, 1), POCKET4(
			"Pickpocket Elite", "Steal from 250 People.", SkillerTasks.ELITE,
			250, Skills.THIEVING, 1), STALL1("Thieve Stall Easy",
			"Steal from 20 Stalls.", SkillerTasks.EASY, 20, Skills.THIEVING, 1), STALL2(
			"Thieve Stall Medium", "Steal from 50 Stalls.",
			SkillerTasks.MEDIUM, 50, Skills.THIEVING, 1), STALL3(
			"Thieve Stall Hard", "Steal from 100 Stalls.", SkillerTasks.HARD,
			100, Skills.THIEVING, 1), STALL4("Thieve Stall Elite",
			"Steal from 250 Stalls.", SkillerTasks.ELITE, 250, Skills.THIEVING,
			1),

	/**
	 * Woodcutting
	 */
	WIVY1("Chop Ivy Medium", "Chop 100 pieces of Choking Ivy.",
			SkillerTasks.MEDIUM, 100, Skills.WOODCUTTING, 68), WIVY2(
			"Chop Ivy Hard", "Chop 250 pieces of Choking Ivy.",
			SkillerTasks.HARD, 250, Skills.WOODCUTTING, 68), WIVY3(
			"Chop Ivy Elite", "Chop 500 pieces of Choking Ivy.",
			SkillerTasks.ELITE, 500, Skills.WOODCUTTING, 68), WOAK1(
			"Chop Oak Easy", "Chop 50 logs from an Oak Tree.",
			SkillerTasks.EASY, 50, Skills.WOODCUTTING, 15), WOAK2(
			"Chop Oak Medium", "Chop 100 logs from an Oak Tree.",
			SkillerTasks.MEDIUM, 10, Skills.WOODCUTTING, 15), WMAGIC1(
			"Magic Logs Hard", "Chop 100 logs from a Magic Tree.",
			SkillerTasks.HARD, 100, Skills.WOODCUTTING, 75), WMAGIC2(
			"Magic Logs Elite", "Chop 250 logs from a Magic Tree.",
			SkillerTasks.ELITE, 250, Skills.WOODCUTTING, 75), WELDER1(
			"Elder Logs Hard", "Chop 250 logs from a Elder Logs.",
			SkillerTasks.HARD, 250, Skills.WOODCUTTING, 91), WELDER2(
			"Elder Logs Elite", "Chop 500 logs from a Magic Tree.",
			SkillerTasks.ELITE, 500, Skills.WOODCUTTING, 91), WMAPLE1(
			"Chop Maple Medium", "Chop 100 logs from a Maple Tree.",
			SkillerTasks.MEDIUM, 100, Skills.WOODCUTTING, 45), WMAPLE2(
			"Chop Maple Hard", "Chop 250 logs from a Maple Tree.",
			SkillerTasks.HARD, 250, Skills.WOODCUTTING, 45), WNORMAL1(
			"Chop Normal Easy", "Chop 50 logs from a Regular Tree.",
			SkillerTasks.EASY, 50, Skills.WOODCUTTING, 1), WNORMAL2(
			"Chop Normal Medium", "Chop 100 logs from a Regular Tree.",
			SkillerTasks.MEDIUM, 100, Skills.WOODCUTTING, 1), WWILLOW1(
			"Chop Willow Easy", "Chop 50 logs from a Willow Tree.",
			SkillerTasks.EASY, 50, Skills.WOODCUTTING, 30), WWILLOW2(
			"Chop Willow Medium", "Chop 100 logs from a Willow Tree.",
			SkillerTasks.MEDIUM, 100, Skills.WOODCUTTING, 30), WWILLOW3(
			"Chop Willow Hard", "Chop 250 logs from a Willow Tree.",
			SkillerTasks.HARD, 250, Skills.WOODCUTTING, 30), WYEW1(
			"Chop Yew Medium", "Chop 100 logs from a Yew Tree.",
			SkillerTasks.MEDIUM, 100, Skills.WOODCUTTING, 60), WYEW2(
			"Chop Yew Hard", "Chop 250 logs from a Yew Tree.",
			SkillerTasks.HARD, 250, Skills.WOODCUTTING, 60), WYEW3(
			"Chop Yew Elite", "Chop 500 logs from a Yew Tree.",
			SkillerTasks.ELITE, 500, Skills.WOODCUTTING, 60);

	private String assignment;
	private String description;
	private int difficulty;
	private int amount;
	private int skill;
	private int level;

	SkillTasks(String assignment, String description, int difficulty,
			int amount, int skill, int level) {
		this.assignment = assignment;
		this.description = description;
		this.difficulty = difficulty;
		this.amount = amount;
		this.skill = skill;
		this.level = level;
	}

	public int getAmount() {
		return amount;
	}

	public String getAssignment() {
		return assignment;
	}

	public String getDescription() {
		return description;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public int getLevel() {
		return level;
	}

	public int getSkill() {
		return skill;
	}
}