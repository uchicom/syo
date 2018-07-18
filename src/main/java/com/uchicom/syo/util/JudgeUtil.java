// (c) 2017 uchicom
package com.uchicom.syo.util;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class JudgeUtil {

	private static final String UTF_8 = "utf-8";
	private static final String SJIS = "sjis";

	/**
	 * バイト列から文字コードを判定する
	 * @param bytes
	 * @return
	 */
	public static String getCharset(byte[] bytes) {
		int index = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (index == 0) {
				if (0x00 <= (0xFF & bytes[0]) && (0xFF & bytes[0]) <= 0x7e) {
					//ASCIIコードの場合
				} else {
					//ASCII以外なのでカウントアップ
					//UTF8の
					//(1)c2～df+80～bf		2バイト
					//(2)e0～ef+80～bf+80～bf	3バイト
					if (0x80 <= (0xFF & bytes[0]) && (0xFF & bytes[0]) <= 0x9f) {
						//（あ）0x80～0x9f
						return SJIS;
					} else if (0xf0 <= (0xFF & bytes[0]) && (0xFF & bytes[0]) <= 0xf7) {
						//(3)f0～f7+80～bf +80～bf +80～bf	4バイト
						return UTF_8;
					} else if (0xf8 <= (0xFF & bytes[0]) && (0xFF & bytes[0]) <= 0xfb) {
						//(4)f8～fb+80～bf +80～bf +80～bf +80～bf	5バイト
						return UTF_8;
					} else if (0xfc <= (0xFF & bytes[0]) && (0xFF & bytes[0]) <= 0xfd) {
						//(5)fc～fd+80～bf +80～bf +80～bf +80～bf +80～bf	6バイト
						return UTF_8;
					}
					//上記3,4,5以外はShiftjisの可能性あり
					index++;
				}
			} else {
				//2文字目か3文字目か
			}
		}
		return "";
	}
}
