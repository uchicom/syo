// (c) 2015 uchicom
package com.uchicom.syo;

import java.io.File;

/**
 * 起動用クラス.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {

	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				File file = new File(arg);
				open(file);
			}
		} else {
			Context.getInstance().start();
		}
	}

	public static void open(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File childFile : file.listFiles()) {
					open(childFile);
				}
			} else if (file.isFile()) {
				Context.getInstance().start(file);
			}
		}
	}


}
