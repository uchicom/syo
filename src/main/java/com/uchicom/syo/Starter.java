// (c) 2015 uchicom
package com.uchicom.syo;

import java.awt.Rectangle;
import java.io.File;

public class Starter {

	public static void start(File file) {
		Context.getInstance().start(file);
	}

	public static void start(File file, Rectangle rectangle) {
		Context.getInstance().start(file, rectangle);
	}
	public static void open(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File childFile : file.listFiles()) {
					open(childFile);
				}
			} else if (file.isFile()) {
				Starter.start(file);
			}
		}
	}
	public static void open(File file, Rectangle rectangle) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File childFile : file.listFiles()) {
					open(childFile, rectangle);
				}
			} else if (file.isFile()) {
				Starter.start(file, rectangle);
			}
		}
	}

}
