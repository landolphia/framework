package framework;

import framework.EventLogger;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GameLoop {
	private EventLogger logger;
	private boolean silent, verbose;

	final private String APP_VERSION = "0.0.0";
	final private String APP_NAME = "FrameWork";
	final private long MS_PER_UPDATE = 10;

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

	private void init(String[] args) {
		silent = false;
		verbose = false;
		boolean argsRes = !CLIArguments.parseArgs(this, args);
		logger = new EventLogger(APP_VERSION, silent, verbose);
		logger.flow("Framework init.");
		if (argsRes) logger.info("Invalid argument found (and ignored).");


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
			while ( lag >= MS_PER_UPDATE ) {
				if (updatePasses >= 20) break;
				updatePasses++;
				lag -= MS_PER_UPDATE;
			}

			done = 1;
			//TODO find a better way to yield
			try { Thread.sleep(1);} catch (Exception e) { logger.error("Something happened while trying to yield: " + e.getMessage());}
		}
	}

	public static void main(String[] args) {
		new GameLoop().run(args);
	}
}
