package net.kagani.game.npc.dungeonnering;

import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;

@SuppressWarnings("serial")
public class RuneboundBehemoth extends DungeonBoss {

	private static final String[] ARTIFACT_TYPE = { "Melee", "Range", "Magic" };
	private BehemothArtifact[] artifacts;

	public RuneboundBehemoth(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
		artifacts = new BehemothArtifact[3];
		for (int idx = 0; idx < artifacts.length; idx++)
			artifacts[idx] = new BehemothArtifact(idx);
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		if (artifacts != null)
			for (BehemothArtifact artifact : artifacts)
				artifact.cycle();
		super.processNPC();
	}

	@Override
	public void processHit(Hit hit) {
		if (hit.getDamage() > 0)
			reduceHit(hit);
		super.processHit(hit);
	}

	public void reduceHit(Hit hit) {
		if ((hit.getLook() == HitLook.MELEE_DAMAGE && artifacts[0]
				.isPrayerEnabled())
				|| (hit.getLook() == HitLook.RANGE_DAMAGE && artifacts[1]
						.isPrayerEnabled())
				|| (hit.getLook() == HitLook.MAGIC_DAMAGE && artifacts[2]
						.isPrayerEnabled()))
			return;
		hit.setDamage(0);
	}

	public void activateArtifact(Player player, WorldObject object, int type) {
		BehemothArtifact artifact = artifacts[type];
		if (artifact.isActive())// Hax unit 2k13
			return;
		WorldObject o = new WorldObject(object);
		o.setId(type == 0 ? 53980 : type == 1 ? 53982 : 53981);
		World.spawnObjectTemporary(o, 30000, true, true);
		artifact.setActive(true, true);
		sendNPCTransformation();
	}

	public void resetTransformation() {
		for (BehemothArtifact artifact : artifacts)
			artifact.setActive(false, false);
		sendNPCTransformation();
	}

	private void sendNPCTransformation() {
		setNextNPCTransformation(getNPCId());
	}

	public int getNPCId() {
		boolean melee = artifacts[0].isPrayerEnabled();
		boolean range = artifacts[1].isPrayerEnabled();
		boolean magic = artifacts[2].isPrayerEnabled();
		if (melee && magic && range)
			return 11767;
		else if (melee && range)
			return 11842;
		else if (melee && magic)
			return 11827;
		else if (magic && range)
			return 11857;
		else if (melee)
			return 11797;
		else if (range)
			return 11782;
		else if (magic)
			return 11752;
		return 11812;
	}

	class BehemothArtifact {

		final int type;
		private boolean active;
		private int cycle;

		public BehemothArtifact(int type) {
			this.type = type;
		}

		public void cycle() {
			if (active) {
				cycle++;
				if (cycle == 50)
					setActive(false, true);
				else if (cycle == 25)
					sendNPCTransformation();
			}
		}

		public boolean isPrayerEnabled() {
			return cycle < 25 && active;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active, boolean message) {
			this.active = active;
			if (!active)
				cycle = 0;
			if (message) {
				for (Player p2 : getManager().getParty().getTeam()) {
					if (getManager().isAtBossRoom(p2))
						continue;
					p2.getPackets().sendGameMessage(
							"The " + ARTIFACT_TYPE[type]
									+ " artifact has been "
									+ (active ? "desactivated" : "re-charged")
									+ "!");
				}
			}
		}
	}
}
