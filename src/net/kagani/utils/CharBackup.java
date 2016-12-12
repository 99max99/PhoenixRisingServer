package net.kagani.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CharBackup {

	/**
	 * @author: Dylan Page
	 */

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM dd yyyy");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		date = null;
		dateFormat = null;
		return currentDate;
	}

	/**
	 * Inits the.
	 */
	public static void init() {
		File f1 = new File("./data/accounts/");
		File f2 = new File("./data/backups/char/" + getDate() + ".zip");
		if (!f2.exists()) {
			try {
				if (f1.list().length == 0) {
					System.out.println("The char folder is empty.");
					return;
				}
				zipDirectory(f1, f2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Zip.
	 * 
	 * @param directory
	 *            the directory
	 * @param base
	 *            the base
	 * @param zos
	 *            the zos
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static final void zip(File directory, File base, ZipOutputStream zos)
			throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[20000];
		int read = 0;
		for (int i = 0, n = files.length; i < n; i++) {
			if (files[i].isDirectory()) {
				zip(files[i], base, zos);
			} else {
				FileInputStream in = new FileInputStream(files[i]);
				ZipEntry entry = new ZipEntry(files[i].getPath().substring(
						base.getPath().length() + 1));
				zos.putNextEntry(entry);
				while (-1 != (read = in.read(buffer))) {
					zos.write(buffer, 0, read);
				}
				in.close();
			}
		}
	}

	/**
	 * Zip directory.
	 * 
	 * @param f
	 *            the f
	 * @param zf
	 *            the zf
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final void zipDirectory(File f, File zf) throws IOException {
		ZipOutputStream z = new ZipOutputStream(new FileOutputStream(zf));
		zip(f, f, z);
		z.close();
	}
}