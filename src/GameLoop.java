package framework;


import framework.EventLogger;
import framework.GLWrapper;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.File;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.LWJGLUtil.Platform;

import framework.Entity;


public class GameLoop {
	private EventLogger logger;
	private boolean silent, verbose;

	private GLWrapper GLW;
	private Entity entity;

	final private String APP_VERSION = "0.0.0";
	final private String APP_NAME = "framework";
	final private double S_PER_UPDATE = 0.01;

	double current, previous, lag;
	int done;
	
	public void setSilentLogger () { silent = true;}
	public void setVerboseLogger () { verbose = true;}

	public void run(String[] args) {
		System.out.println(APP_NAME + " is launching...\n");

		init(args);
		gameloop();

		logger.flow("Bye.");
	}

	private String setNatives() {
		//TODO also find out x86/x64
		File lib;
		Platform platform = LWJGLUtil.getPlatform();
		switch (platform) {
			case WINDOWS:
				lib = new File("../native/windows/x64");
				break;
			case LINUX:
				lib = new File("../native/linux/x64");
				break;
			case MACOSX:
				lib = new File("../native/macosx/x64");
				break;
			default:
				throw new RuntimeException("Unsupported OS: " + System.getProperty("os.name"));
		}

		System.setProperty("org.lwjgl.librarypath", lib.getAbsolutePath());

		return platform.getName();
	}

	private void init(String[] args) {
		silent = false;
		verbose = false;
		boolean argsRes = !CLIArguments.parseArgs(this, args);
		if (argsRes) logger.info("Invalid argument found (and ignored).");

		logger = new EventLogger(APP_VERSION, silent, verbose);
		logger.flow("Framework init.");
		logger.info("Current platform = " + setNatives());

		GLW = new GLWrapper(logger);
		entity = new Entity(logger, 2.1, true);

		done = 0;
		previous = GLFW.glfwGetTime();
		lag = 0;
	}

	private void gameloop() {
		while (done == 0) {
			current = GLFW.glfwGetTime();
			double elapsed = current - previous;
			previous = current;
			lag += elapsed;

			int updatePasses = 0;
			while ( lag >= S_PER_UPDATE ) {
				if (updatePasses >= 20) break;
				updatePasses++;
				lag -= S_PER_UPDATE;
				GLW.update(S_PER_UPDATE);
				entity.update(S_PER_UPDATE);
			}

			entity.display(lag / S_PER_UPDATE);
			done = GLW.display(lag / S_PER_UPDATE);
			//TODO find a better way to yield
			try { Thread.sleep(1);} catch (Exception e) { logger.error("Something happened while trying to yield: " + e.getMessage());}
		}

		GLW.release();
	}

	public static void main(String[] args) {
		new GameLoop().run(args);
	}
}
