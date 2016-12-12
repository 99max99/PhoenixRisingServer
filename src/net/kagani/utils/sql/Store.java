package net.kagani.utils.sql;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import net.kagani.Settings;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.utils.Logger;

public class Store {

	/**
	 * @author: 99max99 M
	 */

	/** The connection. */
	private static Connection connection;

	/** The las connection. */
	private static long lasConnection = System.currentTimeMillis();

	static {
		init();
	}

	/**
	 * Claim payment.
	 * 
	 * @param player
	 *            the p
	 * @param name
	 *            the name
	 */
	public static void claimPayment(final Player player) {
		try {
			if (System.currentTimeMillis() - lasConnection > 10000) {
				destroyConnection();
				init();
				lasConnection = System.currentTimeMillis();
			}
			Statement s = connection.createStatement();
			String namy = player.getUsername();
			String name2 = namy.replaceAll(" ", "_");
			String query = "SELECT * FROM itemstore WHERE username = '" + name2 + "'";
			ResultSet rs = s.executeQuery(query);
			boolean claimed = false;
			while (rs.next()) {
				int prod = Integer.parseInt(rs.getString("productid"));
				int price = Integer.parseInt(rs.getString("price"));
				if (prod == 1 && price == 1) {
					player.setBronzeMember(true);
					player.setSilverMember(false);
					player.setGoldMember(false);
					player.setPlatinumMember(false);
					player.setDiamondMember(false);
					World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
							+ " has bought Bronze Membership. Thank you!", false);
					player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
					Gero.setMember(player.getUsername(), 8, true);
					claimed = true;
				} else if (prod == 2 && price == 25) {
					player.setBronzeMember(false);
					player.setSilverMember(true);
					player.setGoldMember(false);
					player.setPlatinumMember(false);
					player.setDiamondMember(false);
					World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
							+ " has bought Silver Membership. Thank-you!", false);
					player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
					Gero.setMember(player.getUsername(), 9, true);
					claimed = true;
				} else if (prod == 3 && price == 50) {
					player.setBronzeMember(false);
					player.setSilverMember(false);
					player.setGoldMember(true);
					player.setPlatinumMember(false);
					player.setDiamondMember(false);
					World.sendWorldMessage(
							"<col=FF0000><img=5>" + player.getDisplayName() + " has bought Gold Membership. Thank-you!",
							false);
					player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
					Gero.setMember(player.getUsername(), 10, true);
					claimed = true;
				} else if (prod == 7 && price == 100) {
					player.setBronzeMember(false);
					player.setSilverMember(false);
					player.setGoldMember(false);
					player.setPlatinumMember(true);
					player.setDiamondMember(false);
					World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
							+ " has bought Platinum Membership. Thank-you!", false);
					player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
					Gero.setMember(player.getUsername(), 15, true);
					claimed = true;
				} else if (prod == 8 && price == 150) {
					player.setBronzeMember(false);
					player.setSilverMember(false);
					player.setGoldMember(false);
					player.setPlatinumMember(false);
					player.setDiamondMember(true);
					World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
							+ " has bought Diamond Membership. Thank-you!", false);
					player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
					Gero.setMember(player.getUsername(), 16, true);
					claimed = true;

				} else if (prod == 4 && price == 5) {
					World.sendWorldMessage(
							"<col=FF0000><img=5>" + player.getDisplayName() + " has bought a bond. Thank-you!", false);
					player.getPackets().sendGameMessage("Thank you, your bond has been placed your the bank.");
					player.getBank().addItem(29492, 1, true);
					claimed = true;
				} else if (prod == 5 && price == 25) {
					World.sendWorldMessage(
							"<col=FF0000><img=5>" + player.getDisplayName() + " has bought 5 bonds. Thank-you!", false);
					player.getPackets().sendGameMessage("Thank you, your bonds has been placed your the bank.");
					player.getBank().addItem(29492, 5, true);
					claimed = true;
				} else if (prod == 6 && price == 50) {
					World.sendWorldMessage(
							"<col=FF0000><img=5>" + player.getDisplayName() + " has bought 10 bonds. Thank-you!",
							false);
					player.getPackets().sendGameMessage("Thank you, your bonds has been placed your the bank.");
					player.getBank().addItem(29492, 10, true);
					claimed = true;

				} else if (prod == 9 && price == 10) {
					World.sendWorldMessage(
							"<col=FF0000><img=5>" + player.getDisplayName() + " has bought a mystery box. Thank-you!",
							false);
					player.getPackets().sendGameMessage("Thank you, your mystery box has been placed your the bank.");
					player.getBank().addItem(6199, 1, true);
					claimed = true;
				} else if (prod == 10 && price == 45) {
					World.sendWorldMessage(
							"<col=FF0000><img=5>" + player.getDisplayName() + " has bought 5 mystery boxes. Thank-you!",
							false);
					player.getPackets().sendGameMessage("Thank you, your mystery boxes has been placed your the bank.");
					player.getBank().addItem(6199, 5, true);
					claimed = true;
				} else if (prod == 11 && price == 90) {
					World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
							+ " has bought 10 mystery boxes. Thank-you!", false);
					player.getPackets().sendGameMessage("Thank you, your mystery boxes has been placed your the bank.");
					player.getBank().addItem(6199, 10, true);
					claimed = true;
				}
				if (claimed) {
					s.execute("DELETE FROM `itemstore` WHERE `username` = '" + name2 + "';");
					player.setNextGraphics(new Graphics(1765));
					final String FILE_PATH = "data/logs/orders.txt";
					try {
						Calendar cal = Calendar.getInstance();
						BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true));
						writer.write("[" + cal.getTime() + "] - $" + price);
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (IOException e) {

					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Destroy connection.
	 */
	public static void destroyConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inits the.
	 */
	public static void init() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			if (Settings.HOSTED && Settings.WORLD_ID == 1) {
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MaxScape830", "99max99", "");
			} else {
				connection = DriverManager.getConnection("jdbc:mysql://" + Settings.VPS1_IP + "/MaxScape830", "99max99",
						"");
			}
		} catch (Exception e) {
			Logger.logErr("Store", "Could not connect to Store SQL Database.");
		}
	}
}
