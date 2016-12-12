package net.kagani.utils.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import net.kagani.Settings;
import net.kagani.utils.Logger;

public class SqlLogging {

	/**
	 * @author: Dylan Page
	 */

	private static Connection connection = null;

	private static PreparedStatement statement = null;

	private static Calendar calender = Calendar.getInstance();

	public static Connection init() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			Logger.log("SqlLogging",
					"Unable to connect to driver 'org.gjt.mm.mysql.Driver'.");
		}
		try {
			connection = DriverManager.getConnection(Settings.DB_ADDRESS
					+ "forums", "99max99", "");
			Logger.log("SqlLogging", "Connected to sql database.");
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.logErr("SqlLogging", "Unable to connect to database.");
		}
		return connection;
	}

	public static void handleLogging(String dbTable, String... input) {
		try {
			connection.setAutoCommit(false);
			statement = connection.prepareStatement("insert into " + dbTable
					+ " (attack_xp) VALUES('" + input + "')");
			statement.executeUpdate();
			connection.commit();
			Logger.log("SqlLogging", "Written to database table '" + dbTable
					+ "'.");
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logErr("SqlLogging",
					"Error while writing to database table '" + dbTable + "'.");
		}
	}

	public static void upload() {
		try {
			String reader = read("chat", "test");
			handleLogging("chat", "test", reader, "PUBLIC");
		} catch (IOException e) {
			e.printStackTrace();
			Logger.logErr("SqlLogging", "Error while reading file.");
		}
	}

	private static String read(String path, String name) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("data/logs/"
				+ path + "/" + name + ".txt"));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	private SqlLogging() {

	}
}
