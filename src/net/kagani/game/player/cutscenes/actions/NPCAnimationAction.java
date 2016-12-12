package net.kagani.game.player.cutscenes.actions;

import net.kagani.game.Animation;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

public class NPCAnimationAction extends CutsceneAction {

	private Animation anim;

	public NPCAnimationAction(int cachedObjectIndex, Animation anim,
			int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.anim = anim;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextAnimation(anim);
	}

}
