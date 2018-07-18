// (c) 2015 uchicom
package com.uchicom.syo.util;

import java.awt.FontMetrics;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class TextUtil {

	public static String getConvertText(String text) {
		char[] charArray = text.toCharArray();
		StringBuffer stringBuffer = new StringBuffer(charArray.length);
		boolean underBar = false;
		boolean edit = false;
		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] == '_') {
				underBar = true;
			} else if (underBar) {
				if (charArray[i] >= 'a' && charArray[i] <= 'z') {
					stringBuffer.append((char)(charArray[i] + ('A' - 'a')));
					if (!edit)edit = true;
				} else {
					stringBuffer.append('_');
					stringBuffer.append(charArray[i]);
				}
				underBar = false;
			} else {
				stringBuffer.append(charArray[i]);
			}
		}
		if (edit) {
			return stringBuffer.toString();
		} else {
			return text;
		}
	}

	public static String getSetterText(String text) {
		return getAccessorText(text, "set");
	}

	public static String getGetterText(String text) {
		return getAccessorText(text, "get");
	}
	public static String getAccessorText(String text, String access) {
		String convertText = getConvertText(text);
		char[] charArray = convertText.toCharArray();
		StringBuffer stringBuffer = new StringBuffer(charArray.length);
		boolean start = true;
		for (int i = 0; i < charArray.length; i++) {
			if (start) {
				if (charArray[i] >= 'a' && charArray[i] <= 'z') {
					stringBuffer.append(access);
					stringBuffer.append((char)(charArray[i] + ('A' - 'a')));
				} else {
					stringBuffer.append(charArray[i]);
				}
				start = false;
			} else if (charArray[i] == '\n') {

				stringBuffer.append(charArray[i]);
				start = true;
			} else {
				stringBuffer.append(charArray[i]);
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 文字の長さから文字数を算出する
	 * @param width
	 * @param text
	 * @return
	 */
	public static int getIndex(FontMetrics metrics, int width, String text) {
		if (width == 0)
			return 0;
		int max = text.length();
		int sumWidth = 0;
		for (int i = 0; i < max; i++) {
			sumWidth += metrics.charWidth(text.charAt(i));
			if (sumWidth >= width) {
				return i + 1;
			}
		}
		return max - 1;
	}

	public static String getString(FontMetrics metrics, int startWidth, int endWidth, String text) {

		int max = text.length();
		int sumWidth = 0;
		int beginIndex = -1;
		int endIndex = -1;
		if (startWidth == 0) {
			beginIndex = 0;
		}
		if (endWidth == 0) {
			endIndex = 0;
		}
		for (int i = 0; i < max; i++) {
			if (beginIndex >= 0 && endIndex >= 0) {
				break;
			}
			sumWidth += metrics.charWidth(text.charAt(i));
			if (beginIndex < 0 && sumWidth >= startWidth) {
				beginIndex = i + 1;
			}
			if (endIndex < 0 && sumWidth >= endWidth) {
				endIndex = i + 1;
			}
		}
		if (beginIndex >= 0 && endIndex >= 0) {
			return text.substring(beginIndex, endIndex);
		} else {
			return "";
		}
	}


	public static String getString(FontMetrics metrics, int startWidth, int endWidth, String text, Element element) {

		int max = text.length();
		int sumWidth = 0;
		int beginIndex = -1;
		int endIndex = -1;
		if (startWidth == 0) {
			beginIndex = 0;
		}
		if (endWidth == 0) {
			endIndex = 0;
		}
		for (int i = 0; i < max; i++) {
			if (beginIndex >= 0 && endIndex >= 0) {
				break;
			}
			sumWidth += metrics.charWidth(text.charAt(i));
			if (beginIndex < 0 && sumWidth >= startWidth) {
				beginIndex = i + 1;
			}
			if (endIndex < 0 && sumWidth >= endWidth) {
				endIndex = i + 1;
			}
		}
		Document doc = element.getDocument();
		if (beginIndex >= 0 && endIndex >= 0) {
			String value = text.substring(beginIndex, endIndex);
			try {
				doc.remove(element.getStartOffset() + beginIndex, endIndex - beginIndex);
			} catch (BadLocationException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			return value;
		} else {
			return "";
		}
	}
}
