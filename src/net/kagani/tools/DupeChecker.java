package net.kagani.tools;

import java.io.File;
import java.io.IOException;

import net.kagani.cache.Cache;
import net.kagani.login.account.Account;
import net.kagani.utils.saving.JsonFileManager;

public class DupeChecker {

	/**
	 * @author: Dylan Page
	 */

	public static void main(String[] args) throws IOException {
		Cache.init();
		File file = new File("data/accounts");
		String accounts[] = file.list();
		String username = "";
		Account account = null;
		if (JsonFileManager.containsAccount(username))
			account = JsonFileManager.loadAccount(username);
		if (account == null)
			return;
	}
}