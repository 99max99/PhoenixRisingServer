package net.kagani.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Dumper {

	public static final String IN = "http://services.runescape.com/m=itemdb_rs/bestiary/beastData.json?beastid=";
	public static final String OUT = "beasts1.txt";

	public static void main(String[] args) {
		try {
			dump(IN, OUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void dump(String in, String out) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));

		URL url;
		URLConnection connection;
		BufferedReader reader;
		String page;

		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			url = new URL(in + i);
			connection = url.openConnection();
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			page = reader.readLine();

			if (page == null) {
				continue;
			}

			System.out.println(page);
			writer.write(page);
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}
}