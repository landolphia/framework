package framework;

import java.io.IOException;

import java.util.logging.LogRecord;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class ConsoleFormatter extends Formatter {
	private static final String RESET	= "\u001B[0m";
	private static final String BELL	= "\u001B[007";
	private static final String CLEAR	= "\u001B[2J";
	private static final String HOME	= "\u001B[H";
	private static final String BLACK	= "\u001B[30m";
	private static final String RED		= "\u001B[31m";
	private static final String GREEN	= "\u001B[32m";
	private static final String YELLOW	= "\u001B[33m";
	private static final String BLUE	= "\u001B[34m";
	private static final String PURPLE	= "\u001B[35m";
	private static final String CYAN	= "\u001B[36m";
	private static final String WHITE	= "\u001B[37m";

	public String format (LogRecord rec) {
		Level l = rec.getLevel();
		String color = BLACK;
		if (l == Level.FINE) {	color = PURPLE;}
		else if (l == Level.INFO) { color = GREEN;}
		else if (l == Level.WARNING) { color = BLUE;}
		else if (l == Level.SEVERE) { color = RED;}
		else if (l == Level.CONFIG) { color = CYAN;}
		
		StringBuffer buffer = new StringBuffer();

		//if (l != Level.WARNING) {
		//	buffer.append("[" + rec.getLevel() + "]");
		//}

		//while (buffer.length() < 12) {
		//	buffer.append(' ');
		//}

		buffer.insert(0, color);
		buffer.append(formatMessage(rec) + RESET+ "\n");

		return buffer.toString();
	}
}
	
class FileFormatter extends Formatter {
	public String getDate (long t) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss:SSSS");
		Date date = new Date(t);
		return df.format(date);
	}

	public String getHead (Handler h) {
		long t = System.currentTimeMillis();
		
		return "" + getDate(t) + " Event logger -> log start.\n\n";
	}

	public String getTail (Handler h) {
		long t = System.currentTimeMillis();
		
		return "\n\n" + getDate(t) + " Event logger -> log end.";
	}

	public String format (LogRecord rec) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getDate(rec.getMillis()));	
		buffer.append("[" + rec.getLevel() + "]: ");
		buffer.append(formatMessage(rec) + "\n");

		return buffer.toString();
	}
}

public class EventLogger {
	Logger logger;

	public EventLogger () { this("");}
	public EventLogger (String version) { this(version, false, false);}
	public EventLogger (String version, boolean silent, boolean verbose) {
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINEST);

		try {
			FileHandler fileHandler = new FileHandler("framework.log");
			//TODO Level.FINE if debug==true
			fileHandler.setLevel(Level.FINE);
			fileHandler.setFormatter(new FileFormatter());
			logger.addHandler(fileHandler);
		} catch (IOException e) {
			System.out.println("Failed to create the log file.");
			System.exit(2);
		}

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new ConsoleFormatter());
		if (silent) consoleHandler.setLevel(Level.SEVERE);
		else if (verbose) consoleHandler.setLevel(Level.FINE);
		else consoleHandler.setLevel(Level.WARNING);
		logger.addHandler(consoleHandler);

		if (version == null) version = "NO_VERSION";
		this.flow("EventLogger init.");
		this.info("Version " + version);
	}

	public void debug (int i) { log(Level.FINE, Thread.currentThread().getStackTrace(), Integer.toString(i));}
	public void debug (float f) { log(Level.FINE, Thread.currentThread().getStackTrace(), Float.toString(f));}
	public void debug (String m) { log(Level.FINE, Thread.currentThread().getStackTrace(), m);}

	public void info (int i) { log(Level.WARNING, Thread.currentThread().getStackTrace(), Integer.toString(i));}
	public void info (float f) { log(Level.WARNING, Thread.currentThread().getStackTrace(), Float.toString(f));}
	public void info (String m) { log(Level.WARNING, Thread.currentThread().getStackTrace(), m);}

	public void flow (int i) { log(Level.INFO, Thread.currentThread().getStackTrace(), Integer.toString(i));}
	public void flow (float f) { log(Level.INFO, Thread.currentThread().getStackTrace(), Float.toString(f));}
	public void flow (String m) { log(Level.INFO, Thread.currentThread().getStackTrace(), m);}

	public void error (int i) { log(Level.SEVERE, Thread.currentThread().getStackTrace(), Integer.toString(i));}
	public void error (float f) { log(Level.SEVERE, Thread.currentThread().getStackTrace(),Float.toString(f));}
	public void error (String m) { log(Level.SEVERE, Thread.currentThread().getStackTrace(), m);}

	public void lua (int i) { log(Level.CONFIG, Thread.currentThread().getStackTrace(), Integer.toString(i));}
	public void lua (float f) { log(Level.CONFIG, Thread.currentThread().getStackTrace(),Float.toString(f));}
	public void lua (String m) { log(Level.CONFIG, Thread.currentThread().getStackTrace(), m);}

	private void log (Level l, StackTraceElement[] stack, int i) { log(l, stack, Integer.toString(i));}
	private void log (Level l, StackTraceElement[] stack, float f) { log(l, stack, Float.toString(f));}
	private void log (Level l, StackTraceElement[] stack, String m) {
		String source = stack[2].getClassName();
		source = source.substring(source.lastIndexOf(".") + 1);
		String method = stack[2].getMethodName();
		Integer line = stack[2].getLineNumber();
		if (l != Level.WARNING || l != Level.CONFIG) {
			String from = "(" + source + ")@" + line.toString() + " -> " + method;
			logger.log(l, from + ": " + m); 
		} else { logger.log(l, m);}
	}
}
