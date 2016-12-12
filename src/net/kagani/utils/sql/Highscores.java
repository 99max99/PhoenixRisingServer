package net.kagani.utils.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ArrayBlockingQueue;

import net.kagani.Settings;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class Highscores {

	public Player runPlayer;

	public static Connection con;
	public static boolean connected;
	public static Statement stmt;

	private static ArrayBlockingQueue<Statement> dbStatements = new ArrayBlockingQueue<Statement>(1);
	private static transient Statement statement;

	public static Statement getStatement() {
		return statement;
	}

	public static int count = 0;

	public static void init() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			if (Settings.HOSTED && Settings.WORLD_ID == 1) {
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MaxScape830", "99max99", "");
				statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				statement.setEscapeProcessing(true);
				dbStatements.offer(statement);
			} else {
				Connection conn = DriverManager.getConnection("jdbc:mysql://" + Settings.VPS1_IP + "/MaxScape830", "99max99",
						"");

				statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				statement.setEscapeProcessing(true);
				dbStatements.offer(statement);
			}
		} catch (Exception e) {
			Logger.logErr("Highscores", "Could not connect to SQL Database!");
		}
	}

	public static Statement getNextStatement() {
		return dbStatements.poll();
	}

	/**
	 * Query.
	 *
	 * @param s
	 *            the s
	 * @return the result set
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static int executeUpdate(String query) {
		try {
			statement = dbStatements.poll();
			int results = statement.executeUpdate(query);
			dbStatements.offer(statement);
			return results;
		} catch (SQLException ex) {
			Logger.log("Highscores", "Highscores failed to update.");
		}
		return -1;
	}

	public static ResultSet executeQuery(String query) {
		try {
			statement = dbStatements.poll();
			ResultSet results = statement.executeQuery(query);
			dbStatements.offer(statement);
			return results;
		} catch (SQLException ex) {
			Logger.log("Highscores", "Highscores failed to update.");
		}
		return null;
	}

	public static boolean saveHighScore(Player player) {
		if (player.getRights() == 2)
			return true;
		if (player.getSkills().getTotalLevel() < 150)
			return true;
		if (player.getUsername().equalsIgnoreCase("99max99k") || player.getUsername().equalsIgnoreCase("99max99kk")
				|| player.getUsername().equalsIgnoreCase("99max99kkk"))
			return true;
		for (int names = 0; names < Settings.SERVER_ADMINISTRATORS.length; names++) {
			if (player.getUsername().equalsIgnoreCase(Settings.SERVER_ADMINISTRATORS[names])) {
				return true;
			}
		}
		try {
			String username = Utils.formatPlayerNameForDisplay(player.getUsername());
			ResultSet rs = executeQuery("SELECT * FROM hs_users WHERE username='" + username + "' LIMIT 1");
			if (!rs.next()) {
				rs.moveToInsertRow();
				rs.updateString("username", username);
				rs.updateInt("ironman", player.isHardcoreIronman() ? 2 : player.isIronman() ? 1 : 0);
				if (player.getRights() < 1 || !player.isAnIronMan()) {
					rs.updateInt("member", player.isAMember() ? 1 : 0);
				} else {
					rs.updateInt("member", 0);
				}
				rs.updateInt("rights", player.getRights());
				rs.updateString("overall_xp", getTotalXp(player));
				for (int i = 0; i < 26; i++) {
					rs.updateInt("" + Skills.SKILL_NAME[i].toLowerCase() + "_xp", (int) player.getSkills().getXp(i));
				}
				rs.insertRow();
			} else {
				rs.updateString("username", username);
				rs.updateInt("ironman", player.isHardcoreIronman() ? 2 : player.isIronman() ? 1 : 0);
				if (player.getRights() < 1 || !player.isAnIronMan()) {
					rs.updateInt("member", player.isAMember() ? 1 : 0);
				} else {
					rs.updateInt("member", 0);
				}
				rs.updateInt("rights", player.getRights());
				rs.updateString("overall_xp", getTotalXp(player));
				for (int i = 0; i < 26; i++) {
					rs.updateInt("" + Skills.SKILL_NAME[i].toLowerCase() + "_xp", (int) player.getSkills().getXp(i));
				}
				rs.updateRow();
			}
		} catch (Exception e) {
			Logger.log("Highscores", "Error while saving Highscores SQL.");
			return false;
		}
		return true;
	}

	public static String getTotalXp(Player player) {
		double totalxp = 0;
		for (double xp : player.getSkills().getXp()) {
			totalxp += xp;
		}
		NumberFormat formatter = new DecimalFormat("#######");
		return "" + formatter.format(totalxp) + "";
	}
}
