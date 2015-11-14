package com.bng.core.utils;

public class Util {

	public static void sysLog(int severity, String applicationName, String message)
    {
		Logger.sysLog(severity, applicationName, message);
    }
}
