package net.kagani.game.npc;

import net.kagani.game.npc.araxxor.Araxxor;
import net.kagani.game.player.Player;

public interface AraxxorAttack {

	int attack(Araxxor npc, Player victim);

	boolean canAttack(Araxxor npc, Player victim);

}