package net.kagani.tools;

import net.kagani.utils.NPCCombatDefinitionsL;

public class CombatDefsRemaker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NPCCombatDefinitionsL.init();
		System.out.println(NPCCombatDefinitionsL.npcCombatDefinitions.size());

	}

}
