// (c) 2015 uchicom
package com.uchicom.syo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {

	public static String readFile(File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (FileInputStream is = new FileInputStream(file)) {
			int length = 0;
			byte[] bytes = new byte[1024 * 4];
			while ((length = is.read(bytes)) > 0) {
				baos.write(bytes, 0, length);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new String(baos.toByteArray());
	}
}