package com.uchicom.syo.util;

/* (c) 2015 uchicom */
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
}
