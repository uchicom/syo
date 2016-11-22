// (c) 2015 uchicom
package com.uchicom.syo.util;

import java.util.ResourceBundle;

public class ResourceUtil {

	/** リソースバンドル */
	public static final ResourceBundle resourceBundle = ResourceBundle
			.getBundle("resource");

	/**
	 * リソースから文字列を取得する.
	 *
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		String value = resourceBundle.getString(key);
		return value == null ? key : value;
	}

	/**
	 * リソースから文字列をカンマ区切りで配列で取得する.
	 *
	 * @param key
	 * @return
	 */
	public static String[] getStrings(String key) {
		return getStrings(key, ",");
	}
	/* (c) 2015 uchicom */

	/**
	 * リソースから文字列を配列で取得する.
	 *
	 * @param key
	 * @param separator
	 * @return
	 */
	public static String[] getStrings(String key, String separator) {
		String value = getString(key);
		return value.split(separator);
	}
}
