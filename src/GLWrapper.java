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

	public GLWrapper (EventLogger l) {
		logger = l;
		logger.flow("GLWrapper init.");
		logger.info("LWJGL " + Sys.getVersion());

		fullscreen = false;

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

		setDisplayMode(fullscreen);

		
		input = new InputManager(logger, HEIGHT, WIDTH);
		glfwSetKeyCallback(window, input.keyCallback);
				
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(
				window,
				(GLFWvidmode.width(vidmode) - WIDTH) / 2,
				(GLFWvidmode.height(vidmode) - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		GLContext.createFromCurrent();
		glClearColor(0.5f, 0.1f, 0.4f, 0.0f);

		renderer = new GLRenderer(logger, window);
	}

	public void switchDisplayMode() {
		fullscreen = !fullscreen;
		setDisplayMode(fullscreen);
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

		window = glfwCreateWindow(WIDTH, HEIGHT, "REPLACE THIS TEXT", NULL, NULL);

		if ( window == NULL ) {
			throw new RuntimeException("Failed to create the GL window.");
		}
	}

	private void ready3D() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glClearColor(0.2f, 0.2f, 0.4f, 1.0f);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		//GLU.gluPerspective(45.0f, ((float) WIDTH) / ((float) HEIGHT), 0.1f, 100.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glLoadIdentity();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glLoadIdentity();
	}

	private void ready2DText() { ready2D(true);}
	private void ready2D() { ready2D(false);}

	private void ready2D(boolean text) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glClearColor(0.2f, 0.4f, 0.2f, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		if (text)
			GL11.glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
		else	GL11.glOrtho(0, WIDTH, 0, HEIGHT, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public void updateFPS() {
		double current = GLFW.glfwGetTime();
		if (current >= fpsFlushTime) {
			fps = frames;
			frames = 0;
			fpsFlushTime += 1.0;
			logger.debug(fps + " FPS");
		}

		totalFrames++;
		frames++;
	}
	
	public void update () {
		updateFPS();
		//bubbles.update(delta);
		//factory.update(delta);
	}

	public int display (float interpol) {
		//XXX The reason update is here, and not in the main game loop is
		//that the update performed here, while time dependent, does
		//not have the same requirements as the game logic and input's
		//GLWrapper should do its own time keeping (?)
		//updates
		update();
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
