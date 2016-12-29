package net.kagani.game.npc.araxxor;

import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class EggAttack implements AraxxorAttack {

	int X;
	int Y;
	WorldObject One;
	WorldObject Two;
	WorldObject Three;

	@Override
	public int attack(final Araxxor npc, final Player p) {
		final WorldTile SpawnSpiders = new WorldTile(X, Y, p.getPlane());

		if (!p.hasSpawnedEggs) {
			WorldTasksManager.schedule(new WorldTask() {
				int time;

				@Override
				public void run() {
					time++;
					npc.setPhase(1);
					p.AraxxorAttackCount++;
					@SuppressWarnings("unused")
					int hit = 0;
					p.hasSpawnedEggs = true;
					if (time == 1) {
						X = p.FINALAGGX;
						Y = p.FINALAGGY;
						p.eggSpidersX = X;
						p.eggSpidersY = Y;
						// p.sendMessage(""+p.FINALAGGX+" "+p.FINALAGGY+"");
						World.sendGraphics(null, new Graphics(5009),
								new WorldTile(X + 3, Y, p.getPlane()));
						World.sendGraphics(null, new Graphics(5009),
								new WorldTile(X + 2, Y, p.getPlane()));
						World.sendGraphics(null, new Graphics(5009),
								new WorldTile(X + 2, Y + 1, p.getPlane()));
					}
					if (time == 3) {
						WorldObject object = new WorldObject(91641, 22, 1,
								X + 3, Y, p.getPlane(), p);
						WorldObject object2 = new WorldObject(91641, 22, 1,
								X + 2, Y, p.getPlane(), p);
						WorldObject object3 = new WorldObject(91641, 22, 1,
								X + 2, Y + 1, p.getPlane(), p);
						World.spawnObject(new WorldObject(91641, 22, 1, X + 3,
								Y, p.getPlane()));
						World.spawnObject(new WorldObject(91641, 22, 1, X + 2,
								Y, p.getPlane()));
						World.spawnObject(new WorldObject(91641, 22, 1, X + 2,
								Y + 1, p.getPlane()));
						One = object;
						Two = object2;
						Three = object3;
					}
					if (time >= 4) {
						if (p.getX() == X + 3 && p.getY() == Y
								&& !p.AraxxorEggBurst || p.getX() == X + 2
								&& p.getY() == Y && !p.AraxxorEggBurst
								|| p.getX() == X + 2 && p.getY() == Y + 1
								&& !p.AraxxorEggBurst) {
							p.eggBurst(X, Y, p.getPlane());
							World.removeObject(One);
							World.removeObject(Two);
							World.removeObject(Three);
						}
						if (time >= 15) {
							// p.sendMessage("SPAWNED SPIDERS");
							// NPC bob = new NPC(19468, SpawnSpiders, 1, false);
							// World.spawnNPC(19468, SpawnSpiders, 1, false);
							// Entity npc1 = World.spawnNPC(19468, SpawnSpiders,
							// -1, true);
							// final NPC npc2 = (NPC)npc1;
							// npc.spawnEggSpiders(p);

							@SuppressWarnings("unused")
							NPC spider = World.spawnNPC(19468, SpawnSpiders,
									-1, true);
							p.eggBurst(X, Y, p.getPlane());
							World.removeObject(One);
							World.removeObject(Two);
							World.removeObject(Three);
						}

						if (p.AraxxorEggBurst)
							stop();
					}

					hit = Utils.random(0 + Utils.random(150), 400);

					p.applyHit(new Hit(p, hit, hit == 0 ? HitLook.MISSED
							: HitLook.MAGIC_DAMAGE));

					p.applyHit(new Hit(p, 500, HitLook.MAGIC_DAMAGE, 0));

				}
			}, 0, 0);
			;
		}
		// return Utils.random(20, 21);
		return 21;
	}

	@Override
	public boolean canAttack(Araxxor npc, Player victim) {
		return victim.getY() > npc.getBase().getY() + 10;
	}

}
