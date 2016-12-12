package net.kagani.utils.saving;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ConcurrentModificationException;

import net.kagani.game.player.Player;
import net.kagani.login.account.Account;
import net.kagani.utils.Logger;

public class JsonFileManager {

	private static final String ACCOUNT = "data/accounts/";
	private static final String ACCOUNT_BACKUP = "data/accounts/backup/";

	private JsonFileManager() {

	}

	public synchronized static final boolean isValidAccount(String username) {
		return new File(ACCOUNT + username + ".acc").exists();
	}

	public synchronized static final boolean isValidPlayer(String username) {
		return new File(ACCOUNT + username + ".ap").exists();
	}

	public synchronized static Account loadAccount(String username) {
		try {
			return (Account) loadJsonFile(new File(ACCOUNT + username + ".acc"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		try {
			Logger.log("JsonFileManager", "Recovering account: " + username);
			return (Account) loadJsonFile(new File(ACCOUNT_BACKUP + username
					+ ".acc"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	public synchronized static void saveAccount(Account account) {
		try {
			saveJsonFile(account, new File(ACCOUNT + account.getUsername()
					+ ".acc"));
		} catch (ConcurrentModificationException e) {
			// happens because saving and logging out same time
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static String readFile(String filename) {
		String content = null;
		File file = new File(filename);
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public synchronized static final boolean containsAccount(String username) {
		return new File(ACCOUNT + username + ".acc").exists();
	}

	public static final Object loadPlayerFile(File f) throws IOException,
			ClassNotFoundException {
		if (!f.exists())
			return null;
		ObjectInputStream inputStream = new ObjectInputStream(
				new FileInputStream(f));
		JsonReader jr = new JsonReader(inputStream);
		Player p = (Player) jr.readObject();
		return p;
	}

	public static final Object loadJsonFile(File f) throws IOException,
			ClassNotFoundException {
		if (!f.exists())
			return null;
		ObjectInputStream inputStream = new ObjectInputStream(
				new FileInputStream(f));
		JsonReader jr = new JsonReader(inputStream);
		Account p = (Account) jr.readObject();
		return p;
	}

	public static final void saveJsonFile(Player p, File f) throws IOException {
		ObjectOutputStream outputStream = new ObjectOutputStream(
				new FileOutputStream(f));
		JsonWriter jw = new JsonWriter(outputStream);
		jw.write(p);
		jw.close();
	}

	public static final void saveJsonFile(Account a, File f) throws IOException {
		ObjectOutputStream outputStream = new ObjectOutputStream(
				new FileOutputStream(f));
		JsonWriter jw = new JsonWriter(outputStream);
		jw.write(a);
		jw.close();
	}

}