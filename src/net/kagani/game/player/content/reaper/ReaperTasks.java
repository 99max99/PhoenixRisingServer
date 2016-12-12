package net.kagani.game.player.content.reaper;

public enum ReaperTasks {

	/**
	 * @author: Dylan Page
	 */

	// ARAXXOR(19457, 120, 20000, 20, "Araxxor",
	// "I have nothing to tell you about this creature."),

	BARROWS(
			2025,
			60,
			7000,
			7,
			"Barrows Brothers",
			"Any of the 6 (7 after Ritual of the Mahjarrat) barrows brothers can be slain for this task. The assigned number requires 4-8 chests to be opened, not brothers killed."),

	CHAOSELEMENTAL(3200, 60, 10000, 10, "Chaos Elemental",
			"Be aware the Chaos Elemental is located inside the Wilderness."),

	VORAGO(
			17161,
			120,
			20000,
			25,
			"Vorago",
			"A complete task of hard-mode Vorago awards 25k xp and 25 points. A group of 5 or more players is recommended for this task."),

	NEX(13447, 110, 17000, 17, "Nex", "A group is recommended for this task."),

	CORP(8133, 70, 12000, 12, "Corporeal Beast",
			"I have nothing to tell you about this creature."),

	KALPHITEKING(16697, 100, 15000, 15, "Kalphite King",
			"A group of two or more players is required for this task."),

	KALPHITEQUEEN(
			1158,
			60,
			10000,
			10,
			"Kalphite Queen",
			"	The Exiled Kalphite Queen can be completed to fulfil this task but offers no additional rewards."),

	KBD(50, 50, 7000, 7, "King Black Dragon",
			"I have nothing to tell you about this creature."),

	QBD(15454, 80, 10000, 10, "Queen Black Dragon",
			"I have nothing to tell you about this creature."),

	SARADOMIN(6247, 80, 12000, 12, "Commander Zilyana",
			"A complete task of hard-mode Zilyana awards 15k xp and 15 points."),

	ZAMORAK(6203, 80, 12000, 12, "K'ril Tsutsaroth",
			"A complete task of hard-mode K'ril awards 15k xp and 15 points."),

	ARMADYL(6222, 80, 12000, 12, "Kree'arra",
			"A complete task of hard-mode Kree'arra awards 15k xp and 15 points."),

	BANDOS(6260, 80, 12000, 12, "General Graador",
			"A complete task of hard-mode Graardor awards 15k xp and 15 points."),

	DKS(2883, 70, 10000, 10, "Dagannoth Kings",
			"Any of the three dagannoth kings can be slain for this task."),

	GIANTMOLE(3340, 50, 7000, 7, "Giant Mole",
			"A complete task of hard-mode Giant mole awards 10,000xp and 10 points."),

	// ASCENSIONS(1, 100, 15000, 15, "Legions",
	// "Any of the six legiones can be slain for this task."),

	JAD(
			2745,
			60,
			10000,
			10,
			"TzTok-Jad",
			"TokHaar-Jad encountered during the TokHaar Fight Kiln count towards this task and are recommended."),

	;

	private int npcId;
	private int requirement;
	private int xp;
	private int points;
	private String name;
	private String hints;

	ReaperTasks(int npcId, int requirement, int xp, int points, String name,
			String hints) {
		this.npcId = npcId;
		this.requirement = requirement;
		this.name = name;
		this.hints = hints;
	}

	public int getNPCId() {
		return npcId;
	}

	public int getRequirement() {
		return requirement;
	}

	public int getXP() {
		return xp;
	}

	public int getPoints() {
		return points;
	}

	public String getName() {
		return name;
	}

	public String getHints() {
		return hints;
	}
}