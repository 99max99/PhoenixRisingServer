package net.kagani.game.npc.combat;

import java.util.HashMap;

import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class CombatScriptsHandler {

	private static final HashMap<Object, CombatScript> cachedCombatScripts = new HashMap<Object, CombatScript>();
	private static final CombatScript DEFAULT_SCRIPT = new Default();

	@SuppressWarnings("rawtypes")
	public static final void init() {
		try {
			Class[] classes = Utils
					.getClasses("net.kagani.game.npc.combat.impl");
			for (Class c : classes) {
				if (c.isAnonymousClass()) // next
					continue;
				Object o = c.newInstance();
				if (!(o instanceof CombatScript))
					continue;
				CombatScript script = (CombatScript) o;
				for (Object key : script.getKeys())
					cachedCombatScripts.put(key, script);
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static int specialAttack(final NPC npc, final Entity target) {
		CombatScript script = cachedCombatScripts.get(npc.getId());
		if (script == null) {
			script = cachedCombatScripts.get(npc.getDefinitions().getName());
			if (script == null)
				script = DEFAULT_SCRIPT;
		}
		return script.attack(npc, target);
	}
}
