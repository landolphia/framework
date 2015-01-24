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
	private GLRenderer renderer;
	public	GLFWKeyCallback keyCallback;

	private HashMap<Integer, Action> actions;


	public InputManager (EventLogger l, GLRenderer r, int h, int w) {
		logger = l;
		logger.flow("InputManager init.");

		renderer = r;

		keyCallback = this.getCallback();

		actions = new HashMap<Integer, Action>();
		//General
		actions.put(GLFW.GLFW_KEY_ESCAPE, new Action(new Quit(logger, "Escape key pressed"), TRIGGER.DOWN));
		//GL
		actions.put(GLFW.GLFW_KEY_F1, new Action(new DepthClamp(logger, renderer, "Toggling DepthClamp"), TRIGGER.DOWN));

		//Hierarchy
		actions.put(GLFW.GLFW_KEY_F2, new Action(new HierarchyLog(logger, renderer.hierarchy, "printing hierarchy info"), TRIGGER.DOWN));

		actions.put(GLFW.GLFW_KEY_Q, new Action(new HierarchyBase(logger, renderer.hierarchy, "turning base clockwise", true), TRIGGER.DOWN));
		actions.put(GLFW.GLFW_KEY_W, new Action(new HierarchyBase(logger, renderer.hierarchy, "turning base counterclockwise", false), TRIGGER.DOWN));
		
		actions.put(GLFW.GLFW_KEY_A, new Action(new HierarchyUpperArm(logger, renderer.hierarchy, "upper arm up", true), TRIGGER.DOWN));
		actions.put(GLFW.GLFW_KEY_S, new Action(new HierarchyUpperArm(logger, renderer.hierarchy, "upper arm down", false), TRIGGER.DOWN));

		actions.put(GLFW.GLFW_KEY_Z, new Action(new HierarchyLowerArm(logger, renderer.hierarchy, "lower arm up", true), TRIGGER.DOWN));
		actions.put(GLFW.GLFW_KEY_X, new Action(new HierarchyLowerArm(logger, renderer.hierarchy, "lower arm down", false), TRIGGER.DOWN));
		
		actions.put(GLFW.GLFW_KEY_E, new Action(new HierarchyWristPitch(logger, renderer.hierarchy, "wrist pitch clockwise", true), TRIGGER.DOWN));
		actions.put(GLFW.GLFW_KEY_R, new Action(new HierarchyWristPitch(logger, renderer.hierarchy, "wrist pitch counterclockwise", false), TRIGGER.DOWN));

		actions.put(GLFW.GLFW_KEY_D, new Action(new HierarchyWristRoll(logger, renderer.hierarchy, "wrist roll clockwise", true), TRIGGER.DOWN));
		actions.put(GLFW.GLFW_KEY_F, new Action(new HierarchyWristRoll(logger, renderer.hierarchy, "wrist roll counterclockwise", false), TRIGGER.DOWN));
		
		actions.put(GLFW.GLFW_KEY_C, new Action(new HierarchyFinger(logger, renderer.hierarchy, "finger open", true), TRIGGER.DOWN));
		actions.put(GLFW.GLFW_KEY_V, new Action(new HierarchyFinger(logger, renderer.hierarchy, "finger close", false), TRIGGER.DOWN));
	}

	private GLFWKeyCallback getCallback() {
		return new GLFWKeyCallback() {
			@Override
			public void invoke (long window, int key, int scancode, int action, int mods) {
				Action a = actions.get(key);
				if (a != null) {
					//TODO, compare action's TRIGGER (to redo), with method's (int action)
					a.command.execute();
				}
			}
		};
	}
}
