package framework;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.ARBShaderObjects;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;

import java.util.ArrayList;


public class GLShaders {
	private EventLogger logger;

	private int theProgram;
	private int vertexBufferObject;
	private int vao;

	public GLShaders (EventLogger l) {
		logger = l;
		logger.flow("GLShaders init.");
	}

	private String readShader (String filename) throws Exception {
		filename = "../resources/shaders/" + filename;
		String result = "", line;
		BufferedReader reader = new BufferedReader (new InputStreamReader (new FileInputStream(filename)));
		while (((line = reader.readLine())) != null) {
			result += line + "\n";
		}
		
		return result;
	}

	private static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	public int loadShader (int eShaderType, String shaderFileName) {
		int shader = GL20.glCreateShader(eShaderType);

		String shaderCode = null;
		try
		{
			shaderCode = readShader(shaderFileName);
		} catch (Exception e) {
			logger.debug("loadShader(" + eShaderType + ", " + shaderFileName + ") failed:\n" + e.getMessage()); e.printStackTrace();
			System.exit(2);
		}

		GL20.glShaderSource(shader, shaderCode);
		GL20.glCompileShader(shader);

		int status = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);

		if (status == 0) {
			String type = null;
			switch (eShaderType) {
				case GL20.GL_VERTEX_SHADER: type = "vertex"; break;
				case GL32.GL_GEOMETRY_SHADER: type = "geometry"; break;
				case GL20.GL_FRAGMENT_SHADER: type = "fragment"; break;
				default: type = "UNKNOWN"; break;
			}

			logger.error("Failed to compile " + type + " shader: \n" + getLogInfo(shader));
		}

		return shader;
	}

	public int createProgram (ArrayList<Integer> shaderList) {
		int program = GL20.glCreateProgram();

		for (int shader : shaderList) {
			GL20.glAttachShader(program, shader);
		}

		GL20.glLinkProgram(program);

		int status = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);

		if (status == 0) {
			logger.error("Linker failure: " + getLogInfo(program));
		}

		for (int shader : shaderList) {
			GL20.glDetachShader(program, shader);
		}

		return program;
	}
}
