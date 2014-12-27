package framework;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

//TODO update when new util is available
//import org.lwjgl.util.vector.Matrix4f;
//import org.lwjgl.util.vector.Matrix3f;
//import org.lwjgl.util.vector.Vector3f;

import org.lwjgl.glfw.*;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

import java.util.ArrayList;

import framework.GLShaders;


public class GLRenderer {
	private EventLogger 	logger;
	private GLShaders	shaders;

	private long window;

	private int theProgram;
	private int vertexBufferObject;
	private int vao;

	private static float[] vertexData = {
		-0.5f, -0.5f, 0.0f, 1.0f,
		0.5f, -0.5f, 0.0f, 1.0f,
		0.0f, 0.5f, 0.0f, 1.0f,

		-0.5f, 0.75f, 0.0f, 1.0f,
		0.5f, 0.75f, 0.0f, 1.0f,
		0.0f, 0.95f, 0.0f, 1.0f,
	};

	public GLRenderer (EventLogger l, long w) {
		logger = l;
		logger.flow("GLRenderer init.");

		shaders = new GLShaders(logger);

		window = w;

		init();
	}

	private void initializeProgram () {
		ArrayList<Integer> shaderList = new ArrayList<Integer>();

		shaderList.add(shaders.loadShader(GL20.GL_VERTEX_SHADER, "default.vert"));
		shaderList.add(shaders.loadShader(GL20.GL_FRAGMENT_SHADER, "default.frag"));
		theProgram = shaders.createProgram(shaderList);
		//XXX why removed in Tut 02?
		//for(int shader : shaderList) {
		//      GL20.glDeleteShader(shader);
		//} 
	}

	private void initializeVertexBuffer() {
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		verticesBuffer.put(vertexData);
		verticesBuffer.flip();

		vertexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void initializeVAO() {
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
	}

	private GLFWFramebufferSizeCallback getFramebufferSizeCallback() {
		return new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke (long window, int w, int h) {
				logger.debug("Reshaping window [" + w + ", " + h + "]");
				reshape(h, w);
			}
		};
	}

	private void reshape (int height, int width)
	//http://stackoverflow.com/questions/4338729/preserve-aspect-ratio-of-2d-object-on-window-resize
	//http://gamedev.stackexchange.com/questions/49674/opengl-resizing-display-and-glortho-glviewport
	//http://www.acamara.es/blog/tag/lwjgl/
	{
		GL11.glViewport(0, 0, width, height);
	}

	private void init () {
		initializeProgram();
		initializeVertexBuffer();
		initializeVAO();

		GLFW.glfwSetFramebufferSizeCallback(window,  getFramebufferSizeCallback());
	}

	public void display () {
		GL11.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL20.glUseProgram(theProgram);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

		GL20.glDisableVertexAttribArray(0);
		GL20.glUseProgram(0);
	}
}
