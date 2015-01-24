package framework;


public interface Command {
	public abstract void execute();
}

class Quit implements Command {
	private EventLogger logger;
	private String message;

	public Quit (EventLogger l, String m) {
		logger = l;
		message = new String(m);
	}

	public void execute () {
		//TODO, set renderer's flag to done
		logger.debug("User requested to quit:\t" + message);
	}
}

class DepthClamp implements Command {
	private EventLogger logger;
	private String message;
	private GLRenderer renderer;
	private boolean on;

	public DepthClamp (EventLogger l, GLRenderer r, String m) {
		logger = l;
		renderer = r;
		message = new String(m);
		on = false;
	}

	public void execute () {
		renderer.switchDepthClamp(on);
		logger.debug("DepthClamp: " + on + "\n\t" + message);
		on = !on;
	}
}

class HierarchyLog implements Command {
	private EventLogger logger;
	private String message;
	private GLHierarchy hierarchy;

	public HierarchyLog (EventLogger l, GLHierarchy h, String m) {
		logger = l;
		hierarchy = h;
		message = new String(m);
	}

	public void execute () {
		logger.debug("HierarchyLog: " + message);
		logger.info(hierarchy.toString());
	}
}

class HierarchyBase implements Command {
	private EventLogger logger;
	private String message;
	private GLHierarchy hierarchy;
	private boolean clockwise;

	public HierarchyBase (EventLogger l, GLHierarchy h, String m, boolean cw) {
		logger = l;
		hierarchy = h;
		message = new String(m);
		clockwise = cw;
	}

	public void execute () {
		hierarchy.adjBase(clockwise);
		logger.debug("HierarchyBase[" + clockwise + "]: " + message);
	}
}

class HierarchyUpperArm implements Command {
	private EventLogger logger;
	private String message;
	private GLHierarchy hierarchy;
	private boolean up;

	public HierarchyUpperArm (EventLogger l, GLHierarchy h, String m, boolean u) {
		logger = l;
		hierarchy = h;
		message = new String(m);
		up = u;
	}

	public void execute () {
		hierarchy.adjUpperArm(up);
		logger.debug("HierarchyUpperArm[" + up+ "]: " + message);
	}
}

class HierarchyLowerArm implements Command {
	private EventLogger logger;
	private String message;
	private GLHierarchy hierarchy;
	private boolean up;

	public HierarchyLowerArm (EventLogger l, GLHierarchy h, String m, boolean u) {
		logger = l;
		hierarchy = h;
		message = new String(m);
		up = u;
	}

	public void execute () {
		hierarchy.adjLowerArm(up);
		logger.debug("HierarchyLowerArm[" + up + "]: " + message);
	}
}

class HierarchyWristPitch implements Command {
	private EventLogger logger;
	private String message;
	private GLHierarchy hierarchy;
	private boolean clockwise;

	public HierarchyWristPitch (EventLogger l, GLHierarchy h, String m, boolean cw) {
		logger = l;
		hierarchy = h;
		message = new String(m);
		clockwise = cw;
	}

	public void execute () {
		hierarchy.adjWristPitch(clockwise);
		logger.debug("HierarchyWristPitch[" + clockwise + "]: " + message);
	}
}

class HierarchyWristRoll implements Command {
	private EventLogger logger;
	private String message;
	private GLHierarchy hierarchy;
	private boolean clockwise;

	public HierarchyWristRoll (EventLogger l, GLHierarchy h, String m, boolean cw) {
		logger = l;
		hierarchy = h;
		message = new String(m);
		clockwise = cw;
	}

	public void execute () {
		hierarchy.adjWristRoll(clockwise);
		logger.debug("HierarchyWristRoll[" + clockwise + "]: " + message);
	}
}

class HierarchyFinger implements Command {
	private EventLogger logger;
	private String message;
	private GLHierarchy hierarchy;
	private boolean open;

	public HierarchyFinger (EventLogger l, GLHierarchy h, String m, boolean o) {
		logger = l;
		hierarchy = h;
		message = new String(m);
		open = o;
	}

	public void execute () {
		hierarchy.adjFingerOpen(open);
		logger.debug("HierarchyFinger[" + open + "]: " + message);
	}
}
