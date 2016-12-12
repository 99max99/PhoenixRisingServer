package net.kagani.game.npc.clans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.NPCCustomizationData;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.clans.Clan;
import net.kagani.game.player.content.clans.ClansManager;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class ClanVexillum extends NPC {

	/**
	 * @author: Dylan Page
	 */

	public Player target;
	public OutputStream stream;

	public ClanVexillum(Player player, Clan clan, WorldTile vexTile) {
		super(13634, vexTile, -1, true, true);
		player.getClanManager()
				.getClan()
				.setClanPlanterUsername(
						player.getClanManager().getClan().getMember(player));
		applyMottif(clan);
		player.getInventory().deleteItem(20709, 1);
		player.setNextAnimation(new Animation(21217));
		player.faceEntity(this);
	}

	public void applyMottif(Clan clan) {
		NPCCustomizationData data = new NPCCustomizationData(getDefinitions());
		int[] mottoColors = clan.getMottifColors();

		data.setColor(0, mottoColors[0]);
		data.setColor(1, mottoColors[1]);

		data.setColor(2, mottoColors[2]);
		data.setColor(3, mottoColors[2]);
		data.setColor(4, mottoColors[2]);
		data.setColor(5, mottoColors[2]);
		data.setColor(6, mottoColors[2]);
		data.setColor(7, mottoColors[2]);

		data.setColor(8, mottoColors[3]);
		data.setColor(9, mottoColors[3]);
		data.setColor(10, mottoColors[3]);
		data.setColor(11, mottoColors[3]);
		data.setColor(12, mottoColors[3]);
		data.setColor(13, mottoColors[3]);

		data.setTexture(0, ClansManager.getMottifTexture(clan.getMottifTop()));
		data.setTexture(1,
				ClansManager.getMottifTexture(clan.getMottifBottom()));

		setNextCustomization(data);// since your base is different this would
									// owrk fine
	}

	public void openPlayerClanDetails(Player reader) {
		Player player = World.getPlayer(reader.getClanManager().getClan()
				.getClanPlanterUsername());
		reader.getPackets().sendClanSettings(player.getClanManager(), false);
		reader.getInterfaceManager().sendCentralInterface(1107);
		if (player.getClanManager().getClan().getMottifTop() != 0)
			reader.getPackets().sendIComponentSprite(
					1107,
					96,
					ClansManager.getMottifSprite(player.getClanManager()
							.getClan().getMottifTop()));
		if (player.getClanManager().getClan().getMottifBottom() != 0) {
			player.getClanManager();
			reader.getPackets().sendIComponentSprite(
					1107,
					106,
					ClansManager.getMottifSprite(player.getClanManager()
							.getClan().getMottifBottom()));
		}
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		reader.getPackets().sendIComponentText(1107, 186,
				dateFormat.format(cal.getTime()));
		cal.add(Calendar.MINUTE, player.getClanManager().getClan()
				.getTimeZone());
		reader.getPackets().sendIComponentText(1107, 185,
				dateFormat.format(cal.getTime()));
		reader.getPackets().sendIComponentText(1107, 32, "Clan level: ");
		reader.getPackets().sendIComponentText(1107, 36, "Clan name: ");
		reader.getPackets().sendIComponentText(1107, 34, "Clan exp: ");
		reader.getPackets().sendIComponentText(1107, 38,
				Utils.format(player.getClanManager().getXP()) + " xp");
		reader.getPackets().sendIComponentText(
				1107,
				94,
				Utils.fixChatMessage(player.getClanManager().getClan()
						.getClanPlanterUsername()));
		reader.getPackets().sendIComponentText(1107, 40, player.getClanName());
		reader.getPackets().sendIComponentText(1107, 60,
				player.getClanName() + "'s Information");
		reader.getPackets().sendIComponentText(1107, 87,
				player.getClanName() + " message:");
		if (player.getClanManager().getClan().getClanLeaderUsername() != null)
			reader.getPackets().sendIComponentText(1107, 92,
					player.getClanManager().getClan().getClanLeaderUsername());
		else
			reader.getPackets().sendHideIComponent(1107, 90, true);
		reader.getPackets().sendIComponentText(1107, 62, "");
		for (int i = 88; i < 91; i++)
			reader.getPackets().sendIComponentText(1107, i, "");
	}

	public static void openClanDetails(Player reader) {
		reader.getPackets().sendClanSettings(reader.getClanManager(), false);
		reader.getInterfaceManager().sendCentralInterface(1107);
		if (reader.getClanManager().getClan().getMottifTop() != 0)
			reader.getPackets().sendIComponentSprite(
					1107,
					96,
					ClansManager.getMottifSprite(reader.getClanManager()
							.getClan().getMottifTop()));
		if (reader.getClanManager().getClan().getMottifBottom() != 0) {
			reader.getClanManager();
			reader.getPackets().sendIComponentSprite(
					1107,
					106,
					ClansManager.getMottifSprite(reader.getClanManager()
							.getClan().getMottifBottom()));
		}
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		reader.getPackets().sendIComponentText(1107, 186,
				dateFormat.format(cal.getTime()));
		cal.add(Calendar.MINUTE, reader.getClanManager().getClan()
				.getTimeZone());
		reader.getPackets().sendIComponentText(1107, 185,
				dateFormat.format(cal.getTime()));
		reader.getPackets().sendIComponentText(1107, 32, "Clan level: ");
		reader.getPackets().sendIComponentText(1107, 36, "Clan name: ");
		reader.getPackets().sendIComponentText(1107, 34, "Clan exp: ");
		reader.getPackets().sendIComponentText(1107, 38,
				Utils.format(reader.getClanManager().getXP()) + " xp");
		reader.getPackets().sendIComponentText(
				1107,
				94,
				Utils.fixChatMessage(reader.getClanManager().getClan()
						.getClanPlanterUsername()));
		reader.getPackets().sendIComponentText(1107, 40, reader.getClanName());
		reader.getPackets().sendIComponentText(1107, 60,
				reader.getClanName() + "'s Information");
		reader.getPackets().sendIComponentText(1107, 87,
				reader.getClanName() + " message:");
		if (reader.getClanManager().getClan().getClanLeaderUsername() != null)
			reader.getPackets().sendIComponentText(1107, 92,
					reader.getClanManager().getClan().getClanLeaderUsername());
		else
			reader.getPackets().sendHideIComponent(1107, 90, true);
		reader.getPackets().sendIComponentText(1107, 62, "");
		for (int i = 88; i < 91; i++)
			reader.getPackets().sendIComponentText(1107, i, "");
	}

	public static String getClanPlanterUsername(Player reader) {
		return reader.getClanManager().getClan().getClanPlanterUsername();
	}
}