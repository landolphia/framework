package framework;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

//TODO update when new util is available or find bsd licensed lib
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import org.lwjgl.glfw.*;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferUtils;

import java.util.ArrayList;

import framework.GLShaders;
import framework.GLHierarchy;


public class GLRenderer {
	private EventLogger 	logger;
	private GLShaders	shaders;
	public	GLHierarchy	hierarchy;

	private long window;

	private static int FLOAT_BYTES = 4;

	private int theProgram;
	private int vertexBufferObject, indexBufferObject;
	private int vao;

	private static final int numvertices = 24;

	private static float[] vertexData = {
		//Front
		1.0f, 1.0f, 1.0f,
		1.0f, -1.0f, 1.0f,
		-1.0f, -1.0f, 1.0f,
		-1.0f, 1.0f, 1.0f,

		//Top
		1.0f, 1.0f, 1.0f,
		-1.0f, 1.0f, 1.0f,
		-1.0f, 1.0f, -1.0f,
		1.0f, 1.0f, -1.0f,

		//Left
		1.0f, 1.0f, 1.0f,
		1.0f, 1.0f, -1.0f,
		1.0f, -1.0f, -1.0f,
		1.0f, -1.0f, 1.0f,

		//Back
		1.0f, 1.0f, -1.0f,
		-1.0f, 1.0f, -1.0f,
		-1.0f, -1.0f, -1.0f,
		1.0f, -1.0f, -1.0f,

		//Bottom
		1.0f, -1.0f, 1.0f,
		1.0f, -1.0f, -1.0f,
		-1.0f, -1.0f, -1.0f,
		-1.0f, -1.0f, 1.0f,

		//Right
		-1.0f, 1.0f, 1.0f,
		-1.0f, -1.0f, 1.0f,
		-1.0f, -1.0f, -1.0f,
		-1.0f, 1.0f, -1.0f,

		0.0f, 1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f, 1.0f,

		0.0f, 0.0f, 1.0f, 1.0f,
		0.0f, 0.0f, 1.0f, 1.0f,
		0.0f, 0.0f, 1.0f, 1.0f,
		0.0f, 0.0f, 1.0f, 1.0f,

		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,

		1.0f, 1.0f, 0.0f, 1.0f,
		1.0f, 1.0f, 0.0f, 1.0f,
		1.0f, 1.0f, 0.0f, 1.0f,
		1.0f, 1.0f, 0.0f, 1.0f,

		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,

		1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f, 1.0f,
	};

	private static final short[] indexData = {
		0, 1, 2,
		2, 3, 0,

		4, 5, 6,
		6, 7, 4,

		8, 9, 10, 
		10, 11, 8,

		12, 13, 14, 
		14, 15, 12, 

		16, 17, 18, 
		18, 19, 16, 

		20, 21, 22, 
		22, 23, 20, 
	};

	private int modelToCameraMatrixUnif, cameraToClipMatrixUnif;
	private int positionAttrib, colorAttrib;
	float[] cameraToClipMatrix;

	private float calcFrustumScale (float fov) /*DEG*/ {
		final float degToRad = 3.14159f * 2.0f / 360.0f;
		float rad = fov * degToRad;
		return 1.0f / (float) Math.tan(rad / 2.0f);
	}

	float fFrustumScale = calcFrustumScale(45.0f);


	public GLRenderer (EventLogger l, long w) {
		logger = l;
		logger.flow("GLRenderer init.");

		shaders = new GLShaders(logger);

		window = w;

		init();

		hierarchy = new GLHierarchy(logger, theProgram, vao, modelToCameraMatrixUnif, indexData.length);
	}

	private void initializeProgram () {
		ArrayList<Integer> shaderList = new ArrayList<Integer>();

		shaderList.add(shaders.loadShader(GL20.GL_VERTEX_SHADER, "default.vert"));
		shaderList.add(shaders.loadShader(GL20.GL_FRAGMENT_SHADER, "default.frag"));
		theProgram = shaders.createProgram(shaderList);

		positionAttrib = GL20.glGetAttribLocation(theProgram, "position");
		colorAttrib = GL20.glGetAttribLocation(theProgram, "color");

		modelToCameraMatrixUnif = GL20.glGetUniformLocation(theProgram, "modelToCameraMatrix");
		cameraToClipMatrixUnif = GL20.glGetUniformLocation(theProgram, "cameraToClipMatrix");

		//fFrustumScale = 1.0f;
		float fzNear = 1.0f;
		float fzFar = 45.0f;

		cameraToClipMatrix = new float[16];
		for (int i = 0; i < cameraToClipMatrix.length; i++)
			cameraToClipMatrix[i] = 0;

		cameraToClipMatrix[0] = fFrustumScale;
		cameraToClipMatrix[5] = fFrustumScale;
		cameraToClipMatrix[10] = (fzFar + fzNear) / (fzNear - fzFar);
		cameraToClipMatrix[14] = (2 * fzFar * fzNear) / (fzNear - fzFar);
		cameraToClipMatrix[11] = -1.0f;

		FloatBuffer theMat = BufferUtils.createFloatBuffer(cameraToClipMatrix.length);
		theMat.put(cameraToClipMatrix);
		theMat.flip();

		GL20.glUseProgram(theProgram);
		GL20.glUniformMatrix4(cameraToClipMatrixUnif, false, theMat);
		GL20.glUseProgram(0);
	}

	private void initializeVAO() {
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		vertexBuffer.put(vertexData);
		vertexBuffer.flip();

		vertexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);



		ShortBuffer indexBuffer = BufferUtils.createShortBuffer(indexData.length);
		indexBuffer.put(indexData);
		indexBuffer.flip();

		indexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferObject);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);



		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

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
		cameraToClipMatrix[0] = fFrustumScale / ((float) width / height);
		cameraToClipMatrix[5] = fFrustumScale;

		FloatBuffer theMat = BufferUtils.createFloatBuffer(cameraToClipMatrix.length);
		theMat.put(cameraToClipMatrix);
		theMat.flip();

		GL20.glUseProgram(theProgram);
		GL20.glUniformMatrix4(cameraToClipMatrixUnif, false, theMat);
		GL20.glUseProgram(0);

		GL11.glViewport(0, 0, width, height);
	}

	private void init () {
		initializeProgram();
		initializeVAO();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CW);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDepthRange(0.0f, 1.0f);

		GLFW.glfwSetFramebufferSizeCallback(window,  getFramebufferSizeCallback());
	}

	public void updateWindow (long w) {
		logger.debug("Trying to just change the value and set the frame buffer size callback");
		window = w;
		GLFW.glfwSetFramebufferSizeCallback(window,  getFramebufferSizeCallback());
	}

	public void switchDepthClamp(boolean on) {
		if (on) GL11.glEnable(GL32.GL_DEPTH_CLAMP);
		else 	GL11.glDisable(GL32.GL_DEPTH_CLAMP);
	}

	public void update (double delta) {
	}


	public void display () {
		//XXX This is where I stopped, nothing works
		//I've just finished the init (vao and such)
		//Now I have to work on the actual hierarchy
		//http://www.arcsynthesis.org/gltut/Positioning/Tut06%20Fun%20with%20Matrices.html
		GL11.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
		GL11.glClearDepth(1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		hierarchy.draw();
	}
}
