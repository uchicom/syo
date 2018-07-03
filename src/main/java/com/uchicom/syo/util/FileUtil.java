// (c) 2015 uchicom
package com.uchicom.syo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class FileUtil {

	public static String readFile(File file, String charsetName) throws UnsupportedEncodingException {
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
		Charset charset = Charset.forName(charsetName);
		String value = new String(baos.toByteArray(), charset);
		if (value.getBytes(charset).length == baos.size()) {
			return value;
		}

		charset = Charset.forName("SJIS");
		value = new String(baos.toByteArray(), charset);
		if (value.getBytes(charset).length == baos.size()) {
			return value;
		}
		charset = Charset.forName("UTF8");
		value = new String(baos.toByteArray(), charset);
		return value;

	}
}
