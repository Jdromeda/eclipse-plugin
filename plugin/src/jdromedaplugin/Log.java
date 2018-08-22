package jdromedaplugin;

import org.eclipse.core.runtime.Status;

public class Log {
	private Log() {
	}

	public static void info(String msg) {
		log(Status.INFO, msg, null);
	}

	public static void error(String msg, Throwable t) {
		log(Status.ERROR, msg, t);
	}

	public static void error(Throwable t) {
		log(Status.ERROR, t.getMessage(), t);
	}

	public static void error(String msg) {
		log(Status.ERROR, msg, null);
	}

	public static void warning(String msg, Throwable t) {
		log(Status.WARNING, msg, t);
	}

	public static void warning(Throwable t) {
		log(Status.WARNING, t.getMessage(), t);
	}

	public static void warning(String msg) {
		log(Status.WARNING, msg, null);
	}

	public static void log(int severity, String msg, Throwable t) {
		JdromedaPlugin.getDefault().getLog().log(new Status(severity, JdromedaPlugin.ID, Status.OK, msg, t));
	}
}
