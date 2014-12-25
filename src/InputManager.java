package framework;


import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.Callbacks.*;

import java.awt.Rectangle;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import framework.EventLogger;
import framework.Command;

enum TRIGGER {REPEAT /*FIXED DELAY FOR NOW*/, DOWN, UP, HOLD};

class Action {
	public	Command command;
	public	TRIGGER	trigger;

	public Action (Command c, TRIGGER t) {
		command = c;
		trigger = t;
	}
}

public class InputManager {
	private EventLogger logger;
	public	GLFWKeyCallback keyCallback;

	private HashMap<Integer, Action> actions;


	public InputManager (EventLogger l, int h, int w) {
		logger = l;
		logger.flow("InputManager init.");


		keyCallback = this.getCallback();

		actions = new HashMap<Integer, Action>();
		actions.put(GLFW.GLFW_KEY_SPACE, new Action(new Test(logger, "It works!"), TRIGGER.DOWN));
		actions.put(GLFW.GLFW_KEY_ESCAPE, new Action(new Quit(logger, "Escape key pressed"), TRIGGER.DOWN));
	}

	private GLFWKeyCallback getCallback() {
		return new GLFWKeyCallback() {
			@Override
			public void invoke (long window, int key, int scancode, int action, int mods) {
				Action a = actions.get(key);
				if (a != null) {
					//TODO, compare action's TRIGGER (to redo), with method's (int action)
					a.command.execute();
				} else {
					logger.debug("This key is not linked to any action. Move along.");
				}
			}
		};
	}
}
