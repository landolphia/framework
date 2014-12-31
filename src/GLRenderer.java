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
import java.nio.ShortBuffer;
import org.lwjgl.BufferUtils;

import java.util.ArrayList;

import framework.GLShaders;


public class GLRenderer {
	private EventLogger 	logger;
	private GLShaders	shaders;

	private long window;

	private static int FLOAT_BYTES = 4;

	private int theProgram;
	private int vertexBufferObject, indexBufferObject;
	private int vao1, vao2;

	private static final int numvertices = 36;

	private static final float RIGHT_EXTENT = 0.8f;
	private static final float LEFT_EXTENT = -RIGHT_EXTENT;
	private static final float TOP_EXTENT = 0.2f;
	private static final float MIDDLE_EXTENT = 0.0f;
	private static final float BOTTOM_EXTENT = -TOP_EXTENT;
	private static final float FRONT_EXTENT = -1.25f;
	private static final float REAR_EXTENT = -1.75f;

	private static float[] vertexData = {
		//Obj 1
		LEFT_EXTENT, TOP_EXTENT, REAR_EXTENT,
		LEFT_EXTENT, MIDDLE_EXTENT, FRONT_EXTENT,
		RIGHT_EXTENT, MIDDLE_EXTENT, FRONT_EXTENT,
		RIGHT_EXTENT, TOP_EXTENT, REAR_EXTENT,

		LEFT_EXTENT, BOTTOM_EXTENT, REAR_EXTENT,
		LEFT_EXTENT, MIDDLE_EXTENT, FRONT_EXTENT,
		RIGHT_EXTENT, MIDDLE_EXTENT, FRONT_EXTENT,
		RIGHT_EXTENT, BOTTOM_EXTENT, REAR_EXTENT,

		LEFT_EXTENT, TOP_EXTENT, REAR_EXTENT,
		LEFT_EXTENT, MIDDLE_EXTENT,  FRONT_EXTENT,
		LEFT_EXTENT, BOTTOM_EXTENT,  REAR_EXTENT,

		RIGHT_EXTENT,   TOP_EXTENT,             REAR_EXTENT,
		RIGHT_EXTENT,   MIDDLE_EXTENT,  FRONT_EXTENT,
		RIGHT_EXTENT,   BOTTOM_EXTENT,  REAR_EXTENT,

		LEFT_EXTENT,    BOTTOM_EXTENT,  REAR_EXTENT,
		LEFT_EXTENT,    TOP_EXTENT,             REAR_EXTENT,
		RIGHT_EXTENT,   TOP_EXTENT,             REAR_EXTENT,
		RIGHT_EXTENT,   BOTTOM_EXTENT,  REAR_EXTENT,

		//Obj 2
		TOP_EXTENT,             RIGHT_EXTENT,   REAR_EXTENT,
		MIDDLE_EXTENT,  RIGHT_EXTENT,   FRONT_EXTENT,
		MIDDLE_EXTENT,  LEFT_EXTENT,    FRONT_EXTENT,
		TOP_EXTENT,             LEFT_EXTENT,    REAR_EXTENT,

		BOTTOM_EXTENT,  RIGHT_EXTENT,   REAR_EXTENT,
		MIDDLE_EXTENT,  RIGHT_EXTENT,   FRONT_EXTENT,
		MIDDLE_EXTENT,  LEFT_EXTENT,    FRONT_EXTENT,
		BOTTOM_EXTENT,  LEFT_EXTENT,    REAR_EXTENT,

		TOP_EXTENT,             RIGHT_EXTENT,   REAR_EXTENT,
		MIDDLE_EXTENT,  RIGHT_EXTENT,   FRONT_EXTENT,
		BOTTOM_EXTENT,  RIGHT_EXTENT,   REAR_EXTENT,

		TOP_EXTENT,             LEFT_EXTENT,    REAR_EXTENT,
		MIDDLE_EXTENT,  LEFT_EXTENT,    FRONT_EXTENT,
		BOTTOM_EXTENT,  LEFT_EXTENT,    REAR_EXTENT,

		BOTTOM_EXTENT,  RIGHT_EXTENT,   REAR_EXTENT,
		TOP_EXTENT,             RIGHT_EXTENT,   REAR_EXTENT,
		TOP_EXTENT,             LEFT_EXTENT,    REAR_EXTENT,
		BOTTOM_EXTENT,  LEFT_EXTENT,    REAR_EXTENT,

		//Obj 1 cols
		0.75f, 0.75f, 1.0f, 1.0f,
		0.75f, 0.75f, 1.0f, 1.0f,
		0.75f, 0.75f, 1.0f, 1.0f,
		0.75f, 0.75f, 1.0f, 1.0f,

		0.0f, 0.5f, 0.0f, 1.0f,
		0.0f, 0.5f, 0.0f, 1.0f,
		0.0f, 0.5f, 0.0f, 1.0f,
		0.0f, 0.5f, 0.0f, 1.0f,

		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,

		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,

		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,

		//Obj 2 cols
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,

		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,
		0.5f, 0.5f, 0.0f, 1.0f,

		0.0f, 0.5f, 0.0f, 1.0f,
		0.0f, 0.5f, 0.0f, 1.0f,
		0.0f, 0.5f, 0.0f, 1.0f,

		0.75f, 0.75f, 1.0f, 1.0f,
		0.75f, 0.75f, 1.0f, 1.0f,
		0.75f, 0.75f, 1.0f, 1.0f,

		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,
		0.8f, 0.8f, 0.8f, 1.0f,
	};

	private static final short[] indexData = {
		0, 2, 1,
		3, 2, 0,

		4, 5, 6,
		6, 7, 4,

		8, 9, 10,
		11, 13, 12,

		14, 16, 15,
		17, 16, 14,
	};

	private int offsetUniform, perspectiveMatrixUniform;
	private int positionAttrib, colorAttrib;
	float[] perspectiveMatrix;
	float fFrustumScale;


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

		fFrustumScale = 1.0f;
		float fzNear = 0.5f;
		float fzFar = 3.0f;

		perspectiveMatrix = new float[16];
		for (int i = 0; i < perspectiveMatrix.length; i++)
			perspectiveMatrix[i] = 0;

		perspectiveMatrix[0] = fFrustumScale;
		perspectiveMatrix[5] = fFrustumScale;
		perspectiveMatrix[10] = (fzFar + fzNear) / (fzNear - fzFar);
		perspectiveMatrix[14] = (2 * fzFar * fzNear) / (fzNear - fzFar);
		perspectiveMatrix[11] = -1.0f;

		FloatBuffer theMat = BufferUtils.createFloatBuffer(perspectiveMatrix.length);
		theMat.put(perspectiveMatrix);
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


		ShortBuffer indexesBuffer = BufferUtils.createShortBuffer(indexData.length);
		indexesBuffer.put(indexData);
		indexesBuffer.flip();

		indexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferObject);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private void initializeVAO() {
		vao1 = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao1);

		int colorDataOffset = FLOAT_BYTES * 3 * numvertices;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL20.glEnableVertexAttribArray(positionAttrib);
		GL20.glEnableVertexAttribArray(colorAttrib);
		GL20.glBindAttribLocation(theProgram, positionAttrib, "position");
		GL20.glBindAttribLocation(theProgram, colorAttrib, "color");
		GL20.glVertexAttribPointer(positionAttrib, 3, GL11.GL_FLOAT, false, 0, 0);
		GL20.glVertexAttribPointer(colorAttrib, 4, GL11.GL_FLOAT, false, 0, colorDataOffset);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferObject);

		GL30.glBindVertexArray(0);

		vao2 = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao2);

		int posDataOffset = FLOAT_BYTES * 3 * (numvertices / 2);
		colorDataOffset += FLOAT_BYTES * 4 * (numvertices / 2);
		GL20.glEnableVertexAttribArray(positionAttrib);
		GL20.glEnableVertexAttribArray(colorAttrib);
		GL20.glBindAttribLocation(theProgram, positionAttrib, "position");
		GL20.glBindAttribLocation(theProgram, colorAttrib, "color");
		GL20.glVertexAttribPointer(positionAttrib, 3, GL11.GL_FLOAT, false, 0, posDataOffset);
		GL20.glVertexAttribPointer(colorAttrib, 4, GL11.GL_FLOAT, false, 0, colorDataOffset);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferObject);

		GL30.glBindVertexArray(0);
	}

	private GLFWFramebufferSizeCallback getFramebufferSizeCallback() {
		return new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke (long window, int w, int h) {
				reshape(h, w);
			}
		};
	}

	private void reshape (int height, int width)
		//http://stackoverflow.com/questions/4338729/preserve-aspect-ratio-of-2d-object-on-window-resize
		//http://gamedev.stackexchange.com/questions/49674/opengl-resizing-display-and-glortho-glviewport
		//http://www.acamara.es/blog/tag/lwjgl/
	{
		logger.debug("Reshaping window [" + width + ", " + height + "] " + ((float) width / height));
		perspectiveMatrix[0] = fFrustumScale / ((float) width / height);
		perspectiveMatrix[5] = fFrustumScale;

		FloatBuffer theMat = BufferUtils.createFloatBuffer(perspectiveMatrix.length);
		theMat.put(perspectiveMatrix);
		theMat.flip();

		GL20.glUseProgram(theProgram);
		GL20.glUniformMatrix4(perspectiveMatrixUniform, false, theMat);
		GL20.glUseProgram(0);

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

		GL30.glBindVertexArray(vao1);
		GL20.glUniform3f(offsetUniform, 0.0f, 0.0f, 0.0f);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexData.length, GL11.GL_UNSIGNED_SHORT, 0);

		GL30.glBindVertexArray(vao2);
		GL20.glUniform3f(offsetUniform, 0.0f, 0.0f, -1.0f);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexData.length, GL11.GL_UNSIGNED_SHORT, 0);

		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
}
