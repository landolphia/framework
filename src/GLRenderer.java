package framework;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

//TODO update when new util is available or find bsd licensed lib
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

	private static int FLOAT_BYTES = 4;

	private int theProgram;
	private int vertexBufferObject;
	private int vao;

	private static float[] vertexData = {
		0.25f,  0.25f, -1.25f, 1.0f,
		0.25f, -0.25f, -1.25f, 1.0f,
		-0.25f,  0.25f, -1.25f, 1.0f,

		0.25f, -0.25f, -1.25f, 1.0f,
		-0.25f, -0.25f, -1.25f, 1.0f,
		-0.25f,  0.25f, -1.25f, 1.0f,

		0.25f,  0.25f, -2.75f, 1.0f,
		-0.25f,  0.25f, -2.75f, 1.0f,
		0.25f, -0.25f, -2.75f, 1.0f,

		0.25f, -0.25f, -2.75f, 1.0f,
		-0.25f,  0.25f, -2.75f, 1.0f,
		-0.25f, -0.25f, -2.75f, 1.0f,

		-0.25f,  0.25f, -1.25f, 1.0f,
		-0.25f, -0.25f, -1.25f, 1.0f,
		-0.25f, -0.25f, -2.75f, 1.0f,

		-0.25f,  0.25f, -1.25f, 1.0f,
		-0.25f, -0.25f, -2.75f, 1.0f,
		-0.25f,  0.25f, -2.75f, 1.0f,

		0.25f,  0.25f, -1.25f, 1.0f,
		0.25f, -0.25f, -2.75f, 1.0f,
		0.25f, -0.25f, -1.25f, 1.0f,

		0.25f,  0.25f, -1.25f, 1.0f,
		0.25f,  0.25f, -2.75f, 1.0f,
		0.25f, -0.25f, -2.75f, 1.0f,

		0.25f,  0.25f, -2.75f, 1.0f,
		0.25f,  0.25f, -1.25f, 1.0f,
		-0.25f,  0.25f, -1.25f, 1.0f,

		0.25f,  0.25f, -2.75f, 1.0f,
		-0.25f,  0.25f, -1.25f, 1.0f,
		-0.25f,  0.25f, -2.75f, 1.0f,

		0.25f, -0.25f, -2.75f, 1.0f,
		-0.25f, -0.25f, -1.25f, 1.0f,
		0.25f, -0.25f, -1.25f, 1.0f,

		0.25f, -0.25f, -2.75f, 1.0f,
		-0.25f, -0.25f, -2.75f, 1.0f,
		-0.25f, -0.25f, -1.25f, 1.0f,


		0.0f, 0.0f, 1.0f, 1.0f,
		0.0f, 0.0f, 1.0f, 1.0f,
		0.0f, 0.0f, 1.0f, 1.0f,

		0.0f, 0.0f, 1.0f, 1.0f,
		0.0f, 0.0f, 1.0f, 1.0f,
		0.0f, 0.0f, 1.0f, 1.0f,

		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,

		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,

		0.0f, 1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f, 1.0f,

		0.0f, 1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f, 1.0f,

		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,

		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,

		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,

		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,

		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,

		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,
	};

	private int offsetUniform, perspectiveMatrixUniform;
	private int positionAttrib, colorAttrib;


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

		positionAttrib = GL20.glGetAttribLocation(theProgram, "position");
		colorAttrib = GL20.glGetAttribLocation(theProgram, "color");

		offsetUniform = GL20.glGetUniformLocation(theProgram, "offset");
		perspectiveMatrixUniform = GL20.glGetUniformLocation(theProgram, "perspectiveMatrix");

		float fFrustumScale = 1.0f;
		float fzNear = 0.5f;
		float fzFar = 3.0f;

		float[] theMatrix = new float[16];
		for (int i = 0; i < theMatrix.length; i++)
			theMatrix[i] = 0;

		theMatrix[0] = fFrustumScale;
		theMatrix[5] = fFrustumScale;
		theMatrix[10] = (fzFar + fzNear) / (fzNear - fzFar);
		theMatrix[14] = (2 * fzFar * fzNear) / (fzNear - fzFar);
		theMatrix[11] = -1.0f;

		FloatBuffer theMat = BufferUtils.createFloatBuffer(theMatrix.length);
		theMat.put(theMatrix);
		theMat.flip();

		GL20.glUseProgram(theProgram);
		GL20.glUniformMatrix4(perspectiveMatrixUniform, false, theMat);
		GL20.glUseProgram(0);
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

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CW);

		GLFW.glfwSetFramebufferSizeCallback(window,  getFramebufferSizeCallback());
	}



	public void update (double delta) {
	}


	public void display () {
		GL11.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL20.glUseProgram(theProgram);

		GL20.glUniform2f(offsetUniform, 0.5f, 0.5f);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL20.glEnableVertexAttribArray(positionAttrib);
		GL20.glEnableVertexAttribArray(colorAttrib);
		GL20.glBindAttribLocation(theProgram, positionAttrib, "position");
		GL20.glBindAttribLocation(theProgram, colorAttrib, "color");
		GL20.glVertexAttribPointer(positionAttrib, 4, GL11.GL_FLOAT, false, 0, 0);
		GL20.glVertexAttribPointer(colorAttrib, 4, GL11.GL_FLOAT, false, 0, 4*4*3*12);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glUseProgram(0);
	}
}
