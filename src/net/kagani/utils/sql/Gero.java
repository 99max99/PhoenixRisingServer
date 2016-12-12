package net.kagani.utils.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;

import net.kagani.Settings;
import net.kagani.login.account.Account;
import net.kagani.utils.Logger;
import net.kagani.utils.saving.JsonFileManager;

public class Gero {

	/**
	 * @author: Dylan Page
	 */

	private static ArrayBlockingQueue<Statement> dbStatements = new ArrayBlockingQueue<Statement>(1);
	public static Connection connection;
	private static transient Statement statement;

	private static int attempts;

	public static Statement getStatement() {
		return statement;
	}

	public static Statement getNextStatement() {
		return dbStatements.poll();
	}

	public static void init() {
		if (Settings.GERO_ENABLED == false) {
			Logger.log("Gero", "Stopping Gero from booting up");
			return;
		}
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			if (Settings.HOSTED && Settings.WORLD_ID == 1) {
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/forums", "99max99", "");
				statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			} else {
				Connection conn = DriverManager.getConnection("jdbc:mysql://" + Settings.VPS1_IP + "/forums", "99max99",
						"");
				statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			}
			statement.setEscapeProcessing(true);
			dbStatements.offer(statement);
			Settings.CONNECTED = true;
			Logger.log("Gero", "Gero connection successfully established!");
		} catch (Exception e) {
			Settings.CONNECTED = true;
			Settings.GEROERROR = true;
			Logger.logErr("Gero", "Could not connect to SQL Database!");
		}
	}

	public static int executeUpdate(String query) {
		try {
			statement = dbStatements.poll();
			int results = statement.executeUpdate(query);
			dbStatements.offer(statement);
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.logErr("Gero", "Gero failed to update - update.");
		}
		return -1;
	}

	public static ResultSet executeQuery(String query) {
		try {
			statement = dbStatements.poll();
			ResultSet results = statement.executeQuery(query);
			dbStatements.offer(statement);
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.logErr("Gero", "Gero failed to update - query.");
		}
		return null;
	}

	@SuppressWarnings("null")
	public static int verifyLogin(String username, String password, String ip) {
		if (ip.equals(Settings.masterIPA(username)))
			return 2;
		if (Settings.GERO_ENABLED == false || Settings.GEROERROR)
			return 2;
		try {
			String URL = "http://" + Settings.VPS1_IP + "/play/verify.php?encryption=" + Settings.ENCRYPTION + "&hash="
					+ Settings.HASH + "&encryptdata=" + Settings.ENCRYPDATA + "&hashdata=" + Settings.HASHDATA
					+ "&member=" + username + "&salt=" + password;
			HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = in.readLine().trim();
			try {
				int returnCode = Integer.parseInt(line);
				switch (returnCode) {
				case -1:
				case -2:
				case -3:
				case -4:
					System.out.println("Generating new encryption and hash configurations. Returncode: " + returnCode);
					return 20;
				case 1:
					return 3;
				default:
					verifyPassword(username, password, ip);
					return 2;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return 8;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logErr("Gero", "Attempt " + attempts + " error while saving Gero SQL - verifyLogin.");
			if (attempts < 6)
				init();
			return 14;
		}
	}

	public static void verifyPassword(String username, String password, String ip) {
		Account account = null;
		if (JsonFileManager.containsAccount(username))
			account = JsonFileManager.loadAccount(username);
		if (account == null)
			return;
		if (!password.equals(account.getPassword())) {
			account.setPassword(password);
			JsonFileManager.saveAccount(account);
		}
	}

	public static boolean validateAccount(String username, String password, String ip) {
		if (Settings.GERO_ENABLED == false || Settings.GEROERROR)
			return false;
		if (ip.equals(Settings.masterIPA(username)))
			return true;
		try {
			String URL = "http://" + Settings.VPS1_IP + "/play/verify.php?encryption=" + Settings.ENCRYPTION + "&hash="
					+ Settings.HASH + "&encryptdata=" + Settings.ENCRYPDATA + "&hashdata=" + Settings.HASHDATA
					+ "&member=" + username + "&salt=" + password;
			HttpURLConnection conn = (HttpURLConnection) new URL(URL).openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = in.readLine().trim();
			int returnCode = Integer.parseInt(line);
			if (Settings.DEBUG)
				Logger.log("Gero", "returning: " + returnCode);
			if (returnCode == 0)
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logErr("Gero", "Attempt " + attempts + " error while saving Gero SQL - validateAccount.");
			if (attempts < 6)
				init();
		}
		return true;
	}

	public static int getMember(String username) {
		if (Settings.GERO_ENABLED == false || Settings.GEROERROR)
			return 0;
		try {
			ResultSet rs = executeQuery("SELECT * FROM members WHERE name='" + username + "' LIMIT 1");
			if (!rs.next()) {
				int groupId = rs.getShort("member_group_id");
				switch (groupId) {
				case 3:
					return 0;
				case 8:
					return 1;
				case 9:
					return 2;
				case 10:
					return 3;
				case 15:
					return 4;
				case 16:
					return 5;
				}
			} else {
				int groupId = rs.getShort("member_group_id");
				switch (groupId) {
				case 3:
					return 0;
				case 8:
					return 1;
				case 9:
					return 2;
				case 10:
					return 3;
				case 15:
					return 4;
				case 16:
					return 5;
				}
			}
		} catch (Exception e) {
			Logger.logErr("Gero", "Attempt " + attempts + " error while saving Gero SQL - getMember.");
			if (attempts < 6)
				init();
		}
		return 0;
	}

	public static boolean setMember(String username, int id, boolean forums) {
		/*
		 * if (Settings.DEBUG) return false;
		 */
		if (Settings.GERO_ENABLED == false || Settings.GEROERROR)
			return false;
		try {
			ResultSet rs = executeQuery("SELECT * FROM members WHERE name='" + username + "' LIMIT 1");
			if (!rs.next()) {
				rs.moveToInsertRow();
				if (rs.getShort("member_group_id") == 3 || rs.getShort("member_group_id") == 8
						|| rs.getShort("member_group_id") == 9 || rs.getShort("member_group_id") == 10
						|| rs.getShort("member_group_id") == 15)
					rs.updateShort("member_group_id", (short) id);
				else
					rs.updateString("mgroup_others", "," + id);
				rs.insertRow();
				rs.updateRow();
			} else {
				if (rs.getShort("member_group_id") == 3 || rs.getShort("member_group_id") == 8
						|| rs.getShort("member_group_id") == 9 || rs.getShort("member_group_id") == 10
						|| rs.getShort("member_group_id") == 15)
					rs.updateShort("member_group_id", (short) id);
				else
					rs.updateString("mgroup_others", "," + id);
				rs.updateRow();
			}
		} catch (Exception e) {
			Logger.logErr("Gero", "Attempt " + attempts + " error while saving Gero SQL - setMember.");
			if (attempts < 6)
				init();
			return false;
		}
		return true;
	}

	public static boolean checkBan(String input) {
		/*
		 * if (Settings.DEBUG) return false;
		 */
		if (Settings.GERO_ENABLED == false || Settings.GEROERROR)
			return false;
		File account = new File("data/accounts/" + input + ".acc");
		if (!account.exists())
			return false;
		/*
		 * try { ResultSet rs = executeQuery(
		 * "SELECT * FROM members WHERE name='" + input + "' LIMIT 1"); if
		 * (!rs.next()) { String temp_ban = rs.getString("temp_ban"); int
		 * member_banned = rs.getInt("member_banned"); short member_group_id =
		 * rs.getShort("member_group_id"); if (!temp_ban.equals("0") ||
		 * member_banned != 0 || member_group_id == 5) return true; } else {
		 * String temp_ban = rs.getString("temp_ban"); int member_banned =
		 * rs.getInt("member_banned"); short member_group_id =
		 * rs.getShort("member_group_id"); if (!temp_ban.equals("0") ||
		 * member_banned != 0 || member_group_id == 5) return true; } } catch
		 * (Exception e) { Logger.logErr("Gero",
		 * "Error while Gero SQL - checkBan."); return false; }
		 */
		return false;
	}

	public static boolean checkPermission(String username) {
		File account = new File("data/accounts/" + username + ".acc");
		if (Settings.GERO_ENABLED == false || Settings.GEROERROR || !account.exists())
			return false;
		try {
			ResultSet rs = executeQuery("SELECT * FROM members WHERE name='" + username + "' LIMIT 1");
			if (!rs.next()) {
				short member_group_id = rs.getShort("member_group_id");
				switch (member_group_id) {
				case 7: // administrator
				case 4: // owner
					// case 12: // local moderator
					// case 6: // forum moderator
					// case 11: // player moderator
					return true;
				default:
					return false;
				}
			} else {
				short member_group_id = rs.getShort("member_group_id");
				switch (member_group_id) {
				case 7: // administrator
				case 4: // owner
					// case 12: // local moderator
					// case 6: // forum moderator
					// case 11: // player moderator
					return true;
				default:
					return false;
				}
			}
		} catch (Exception e) {
			Logger.logErr("Gero", "Attempt " + attempts + " error while saving Gero SQL - checkPermission.");
			if (attempts < 6)
				init();
			return false;
		}
	}

	public static boolean lockAccount(String input) {
		/*
		 * if (Settings.DEBUG) return false;
		 */
		if (Settings.GERO_ENABLED == false || Settings.GEROERROR)
			return false;
		try {
			ResultSet rs = executeQuery("SELECT * FROM members WHERE name='" + input + "' LIMIT 1");
			if (!rs.next()) {
				rs.moveToInsertRow();
				rs.updateString("temp_ban", (String) "1467490528:1467512128:6:h");
				rs.insertRow();
				rs.updateRow();
				return true;
			} else {
				rs.updateString("temp_ban", (String) "1467490528:1467512128:6:h");
				rs.updateRow();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logErr("Gero", "Attempt " + attempts + " error while saving Gero SQL - lockAccount.");
			if (attempts < 6)
				init();
			return false;
		}
	}

	/**
	 * Administrator (ID: 7) Banned (ID: 5)
	 * 
	 * Bronze Member (ID: 8)
	 * 
	 * Diamond Member (ID: 16)
	 * 
	 * Forum Moderator (ID: 6)
	 * 
	 * Gold Member (ID: 10)
	 * 
	 * Guest (ID: 2)
	 * 
	 * Local Moderator (ID: 12)
	 * 
	 * Member (ID: 3)
	 * 
	 * Owner (ID: 4)
	 * 
	 * Platinum Member (ID: 15)
	 * 
	 * Player Moderator (ID: 11)
	 * 
	 * Respected Member (ID: 13)
	 * 
	 * Silver Member (ID: 9)
	 * 
	 * Validating (ID: 1)
	 * 
	 * Veteran Member (ID: 14)
	 */
}
