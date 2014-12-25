package framework;

import framework.GameLoop;

public class CLIArguments {
	public static boolean parseArgs(GameLoop game, String[] args) {
		boolean result = true;
		String s = null;
		for (int i = 0; i < args.length; i++) {
			s = args[i];
			switch (s) {
				case "verbose":
				case "v":
					game.setVerboseLogger();
					break;
				case "silent":
				case "s":
					game.setSilentLogger();
					break;
				case "help":
				case "h":
					System.out.println("\nHelp\n----\n");
					System.out.println("v, verbose: logs every single message to the console. This should only be used when debugging.");
					System.out.println("s, silent : only logs basic flow.");
					System.out.println("h, help   : this page.");
					System.exit(0);
					break;
				default: result = false;
					 break;
			}
		}

		return result;
	}
}
