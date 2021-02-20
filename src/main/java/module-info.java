// (c) 2021 uchicom
module com.uchicom.syo {
	requires transitive java.desktop;
	requires com.uchicom.util;
	requires com.uchicom.ui;
	requires jdk.jshell;
	exports com.uchicom.syo;
	exports com.uchicom.syo.action.display;
	exports com.uchicom.syo.action.edit;
	exports com.uchicom.syo.action.file;
	exports com.uchicom.syo.action.help;
	exports com.uchicom.syo.action.script;
	exports com.uchicom.syo.action.search;
}
