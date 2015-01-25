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

class Action {
	public	Command command;
	public	int trigger;

	public Action (Command c, int t) {
		command = c;
		trigger = t;
	}
}

public class InputManager {
	private EventLogger logger;
	private GLRenderer renderer;
	private GLWrapper wrapper;
	public	GLFWKeyCallback keyCallback;

	private HashMap<Integer, Action> actions;


	public InputManager (EventLogger l, GLRenderer r, GLWrapper wr, int h, int w) {
		logger = l;
		logger.flow("InputManager init.");

		renderer = r;
		wrapper = wr;

		keyCallback = this.getCallback();

		actions = new HashMap<Integer, Action>();
		//General
		actions.put(GLFW.GLFW_KEY_ESCAPE, new Action(new Quit(logger, "Escape key pressed"), GLFW.GLFW_RELEASE));
		//GL
		actions.put(GLFW.GLFW_KEY_F1, new Action(new DepthClamp(logger, renderer, "Toggling DepthClamp"), GLFW.GLFW_RELEASE));
		actions.put(GLFW.GLFW_KEY_P, new Action(new Fullscreen(logger, "Toggling Fullscreen", wrapper), GLFW.GLFW_RELEASE));

		//Hierarchy
		actions.put(GLFW.GLFW_KEY_F2, new Action(new HierarchyLog(logger, renderer.hierarchy, "printing hierarchy info"), GLFW.GLFW_RELEASE));

		actions.put(GLFW.GLFW_KEY_Q, new Action(new HierarchyBase(logger, renderer.hierarchy, "turning base clockwise", true), GLFW.GLFW_PRESS));
		actions.put(GLFW.GLFW_KEY_W, new Action(new HierarchyBase(logger, renderer.hierarchy, "turning base counterclockwise", false), GLFW.GLFW_PRESS));
		
		actions.put(GLFW.GLFW_KEY_A, new Action(new HierarchyUpperArm(logger, renderer.hierarchy, "upper arm up", true), GLFW.GLFW_PRESS));
		actions.put(GLFW.GLFW_KEY_S, new Action(new HierarchyUpperArm(logger, renderer.hierarchy, "upper arm down", false), GLFW.GLFW_PRESS));

		actions.put(GLFW.GLFW_KEY_Z, new Action(new HierarchyLowerArm(logger, renderer.hierarchy, "lower arm up", true), GLFW.GLFW_PRESS));
		actions.put(GLFW.GLFW_KEY_X, new Action(new HierarchyLowerArm(logger, renderer.hierarchy, "lower arm down", false), GLFW.GLFW_PRESS));
		
		actions.put(GLFW.GLFW_KEY_E, new Action(new HierarchyWristPitch(logger, renderer.hierarchy, "wrist pitch clockwise", true), GLFW.GLFW_PRESS));
		actions.put(GLFW.GLFW_KEY_R, new Action(new HierarchyWristPitch(logger, renderer.hierarchy, "wrist pitch counterclockwise", false), GLFW.GLFW_PRESS));

		actions.put(GLFW.GLFW_KEY_D, new Action(new HierarchyWristRoll(logger, renderer.hierarchy, "wrist roll clockwise", true), GLFW.GLFW_PRESS));
		actions.put(GLFW.GLFW_KEY_F, new Action(new HierarchyWristRoll(logger, renderer.hierarchy, "wrist roll counterclockwise", false), GLFW.GLFW_PRESS));
		
		actions.put(GLFW.GLFW_KEY_C, new Action(new HierarchyFinger(logger, renderer.hierarchy, "finger open", true), GLFW.GLFW_PRESS));
		actions.put(GLFW.GLFW_KEY_V, new Action(new HierarchyFinger(logger, renderer.hierarchy, "finger close", false), GLFW.GLFW_PRESS));
	}

	private GLFWKeyCallback getCallback() {
		return new GLFWKeyCallback() {
			@Override
			public void invoke (long window, int key, int scancode, int action, int mods) {
				Action a = actions.get(key);
				if (a != null) {
					//TODO, compare action's trigger with method's (int action)
					logger.debug("action invoked\n" + 
						     "> window = " + window + "\n" +
						     "> key = " + key + "\n" +
						     "> scancode = " + scancode + "\n" +
						     "> action = " + action + "\n" +
						     "> mods = " + mods + "\n\n" +
						     "The action's trigger is : " + a.trigger);
					if ( action == a.trigger ) {
						logger.debug("Command executed!!!");
						a.command.execute();
					}
				}
			}
		};
	}
}
