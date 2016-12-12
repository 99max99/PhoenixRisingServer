package net.kagani.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.ClientScriptMap;

public class RuneMusicsDumper {

	public static final void main(String[] args) throws IOException {
		System.out.println("Starting..");
		Cache.init();
		dumpMusics();

	}

	public static boolean dumpMusics() {
		try {
			WebPage page = new WebPage(
					"http://runescape.wikia.com/wiki/Music/track_list");
			try {
				page.load();
			} catch (Exception e) {
				return false;
			}
			List<Integer> ids = new ArrayList<Integer>();
			boolean isNextLine = false;
			String lastLine = "";
			for (String line : page.getLines()) {
				if (!isNextLine) {
					if (line.contains(" automatically."))
						isNextLine = true;
					else
						lastLine = line;
				} else {
					String musicName = lastLine.substring(
							lastLine.indexOf("\">") + 2,
							lastLine.indexOf("</a>"));

					Object key = ClientScriptMap.getMap(1345).getKeyForValue(
							musicName);
					System.out.println(musicName + ", " + key);
					ids.add((int) (long) key);
					isNextLine = false;
				}
			}
			System.out.println(ids);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
