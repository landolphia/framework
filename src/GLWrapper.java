package framework;


import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.Iterator;

import framework.GLRenderer;
import framework.InputManager;
import framework.EventLogger;
//import framework.util.Point;


public class GLWrapper {
	private EventLogger logger;

	private InputManager	input;
	private GLRenderer 	renderer;

	//private WorldGrid world;
	//private static final Integer MAP_HEIGHT = 50;
	//private static final Integer MAP_WIDTH = 50;

	private int 	WIDTH = 800;
	private int 	HEIGHT = 600;
	private boolean	fullscreen;

	private int	totalFrames, frames, fps;
	private double	fpsFlushTime;

	private GLFWErrorCallback 	errorCallback;
	private long 			window;

	public GLWrapper (EventLogger l, boolean f) {
		logger = l;
		logger.flow("GLWrapper init.");
		logger.info("LWJGL " + Sys.getVersion());

		fullscreen = f;

		fpsFlushTime = glfwGetTime();
		frames = 0;
		totalFrames = frames;
		fps = 0;

		initGL();

		//world = new WorldGrid(logger, MAP_WIDTH, MAP_HEIGHT);
	}

	private void initGL() {
		//TODO set to use EventLogger helper (not written yet)
		//glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
		glfwSetErrorCallback(errorCallback = new GLFWErrorCallback() {
			@Override
			public void invoke (int error, long description) {
				logger.debug("GLWF ERROR: " + error + ",desc@" + description);
			}
		});

		//window is set after this
		setDisplayMode(fullscreen);
		
		//BOKBOK
		
		// I'm working on a live fullscreen toggle
		// It seemd it would mean creating a new renderer (since the window changes)
		// Unless I can just swap window's value in the renderer
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(
				window,
				(GLFWvidmode.width(vidmode) - WIDTH) / 2,
				(GLFWvidmode.height(vidmode) - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		GLContext.createFromCurrent();

		renderer = new GLRenderer(logger, window);

		input = new InputManager(logger, renderer, this, HEIGHT, WIDTH);
		glfwSetKeyCallback(window, input.keyCallback);
				
	}

	public void switchDisplayMode() {
		fullscreen = !fullscreen;
		glfwDestroyWindow(window);
		setDisplayMode(fullscreen);
		renderer.updateWindow(window);
	}

	private void setDisplayMode(boolean f) {
		fullscreen = f;
		setDisplayMode();
	}

	private void setDisplayMode() {
		if (glfwInit() != GL11.GL_TRUE) {
			throw new IllegalStateException("Unable to initialize GL.");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		if (fullscreen) {
			window = glfwCreateWindow(WIDTH, HEIGHT, "REPLACE THIS TEXT WITH APP NAME", glfwGetPrimaryMonitor(), NULL);
		} else {
			window = glfwCreateWindow(WIDTH, HEIGHT, "REPLACE THIS TEXT WITH APP NAME", NULL, NULL);
		}


		if ( window == NULL ) {
			throw new RuntimeException("Failed to create the GL window.");
		}
	}

	public void updateFPS() {
		double current = GLFW.glfwGetTime();
		if (current >= fpsFlushTime) {
			fps = frames;
			frames = 0;
			fpsFlushTime += 1.0;
			logger.debug(fps + " FPS" + "[" + totalFrames + " total]");
		}

		totalFrames++;
		frames++;
	}
	
	public void update (double delta) {
		renderer.update(delta);
	}

	public int display (double interpol) {
		updateFPS();
		//TODO Interpolation
		renderer.display();
		glfwSwapBuffers(window);
		glfwPollEvents();


		//for (GridCell c : world.model()) {
			//renderer.render(c.model);
		//}

		return glfwWindowShouldClose(window); //TODO return done boolean
	}

	public void release () {
			glfwDestroyWindow(window);
			input.keyCallback.release();
			glfwTerminate();
			errorCallback.release();
	}
}
