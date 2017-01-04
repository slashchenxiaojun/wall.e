package org.hacker.core;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

public class Play {
	private static final String play = "play.properties";
	
	private static Prop pro = PropKit.use(play, "UTF-8");
	
	public static boolean isLocal() {
		return getProperty(Dict.CONFIG_PMR_MODE).equals("local");
	}
	
	public static boolean isNetWork() {
		return getProperty(Dict.CONFIG_PMR_MODE).equals("network");
	}
	
	public static boolean isDebug() {
		return getProperty(Dict.CONFIG_AUT_MODE).equals("debug");
	}
	
	public static boolean isSSO() {
		return getProperty(Dict.CONFIG_AUT_MODE).equals("sso");
	}
	
	public static boolean isNone() {
		return getProperty(Dict.CONFIG_AUT_MODE).equals("none");
	}
	
	public static boolean isNormal() {
		return getProperty(Dict.CONFIG_AUT_MODE).equals("normal");
	}
	
	public static boolean isJFinalDebug() {
	  return getProperty(Dict.CONFIG_JFINAL_MODE).equals("true");
	}
	
	public static String getProperty(String key) {
		return pro.get(key);
	}
}
