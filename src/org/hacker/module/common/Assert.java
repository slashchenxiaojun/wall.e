package org.hacker.module.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Assert {
	private Assert() {}
	private static final String 
	EL_YYYYMMDD 	  = "(\\d{4})-(0\\d{1}|1[0-2])-(0[1-9]|[12]\\d{1}|3[01])",
	EL_YYYYMMDDHHMM   = "(\\d{4})-(0\\d{1}|1[0-2])-(0[1-9]|[12]\\d{1}|3[01]) (0\\d{1}|1\\d{1}|2[0-3]):[0-5]\\d{1}",
	EL_YYYYMMDDHHMMSS = "(\\d{4})-(0\\d{1}|1[0-2])-(0[1-9]|[12]\\d{1}|3[01]) (0\\d{1}|1\\d{1}|2[0-3]):[0-5]\\d{1}:([0-5]\\d{1})";
	
	public static <T> T checkNotNull(T reference) {
		return checkNotNull(reference, "reference");
	}
	
	public static <T> T checkNotNull(T reference, Object errorMessage) {
		if (reference == null) {
			throw new NullPointerException(String.valueOf(errorMessage) + " can't be null");
		} else if(reference instanceof String && reference.equals("")) {
			throw new NullPointerException(String.valueOf(errorMessage) + " can't be null | ''");
		}
		return reference;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>void checkNotNull(T... references) {
		for(T reference : references) checkNotNull(reference);
	}
	
	/**
	 * 暂时只支持yyyy-MM-dd | yyyy-MM-dd hh:mm | yyyy-MM-dd hh:mm:ss
	 * @param dateStr
	 * @param format
	 */
	public static boolean checkDateFormat(String dateStr, String format) {
		Matcher matcher = null;
		switch (format) {
		case "yyyy-MM-dd":
			matcher = Pattern.compile(EL_YYYYMMDD).matcher(dateStr);
			break;
		case "yyyy-MM-dd hh:mm":
			matcher = Pattern.compile(EL_YYYYMMDDHHMM).matcher(dateStr);
			break;
		case "yyyy-MM-dd hh:mm:ss":
			matcher = Pattern.compile(EL_YYYYMMDDHHMMSS).matcher(dateStr);
			break;
		default:
			throw new UnsupportedOperationException("Oop! Unsupported this format: " + format + " try to use (yyyy-MM-dd | yyyy-MM-dd hh:mm | yyyy-MM-dd hh:mm:ss)");
		}
		if(matcher == null) throw new NullPointerException("Oop! matcher is null.");
		if(matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
}
