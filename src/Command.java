package framework;


public interface Command {
	public abstract void execute();
}

class Test implements Command {
	private EventLogger logger;
	private String message;

	public Test (EventLogger l, String m) {
		logger = l;
		message = new String(m);
	}

	public void execute () {
		logger.debug("Test action:\t" + message);
	}
}

class Quit implements Command {
	private EventLogger logger;
	private String message;

	public Quit (EventLogger l, String m) {
		logger = l;
		message = new String(m);
	}

	public void execute () {
		logger.debug("User requested to quit:\t" + message);
	}
}
