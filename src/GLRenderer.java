package framework;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.ARBShaderObjects;

//TODO update when new util is available
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import org.lwjgl.glfw.*;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferUtils;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.Stack;
import java.awt.Point;

import framework.EventLogger;

class MatrixStack {
	private EventLogger logger;
	private Stack<Matrix4f> matrices;
	private Matrix4f currentMatrix;

	public MatrixStack (EventLogger l) {
		logger = l;

		matrices = new Stack<Matrix4f>();
		currentMatrix = new Matrix4f();
	}

	public Matrix4f top() { return currentMatrix;}

	private Matrix4f fill4With3(Matrix3f in) {
		Matrix4f out = new Matrix4f();
		out.setZero();
		out.m33 = 1.0f;

		out.m00 = in.m00; out.m01 = in.m01; out.m02 = in.m02;
		out.m10 = in.m10; out.m11 = in.m11; out.m12 = in.m12;
		out.m20 = in.m20; out.m21 = in.m21; out.m22 = in.m22;
		 return out;
	}

	private Matrix3f RotateXHelper (float angle) {
		angle = (float)Math.toRadians(angle);
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);

		Matrix3f result = new Matrix3f();
		result.m11 = cos;
		result.m12 = sin;
		result.m21 = -sin;
		result.m22 = cos;

		return result;
	}

	private Matrix3f RotateYHelper (float angle) {
		angle = (float)Math.toRadians(angle);
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);

		Matrix3f result = new Matrix3f();
		result.m00 = cos;
		result.m02 = -sin;
		result.m20 = sin;
		result.m22 = cos;

		return result;
	}

	private Matrix3f RotateZHelper (float angle) {
		angle = (float)Math.toRadians(angle);
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);

		Matrix3f result = new Matrix3f();
		result.m00 = cos;
		result.m01 = sin;
		result.m10 = -sin;
		result.m11 = cos;

		return result;
	}

	public void rotateX (float angle) {
		Matrix4f rotationMatrix = fill4With3(RotateXHelper(angle));
		currentMatrix = Matrix4f.mul(currentMatrix, rotationMatrix, null);
	}

	public void rotateY (float angle) {
		Matrix4f rotationMatrix = fill4With3(RotateYHelper(angle));
		currentMatrix = Matrix4f.mul(currentMatrix, rotationMatrix, null);
	}

	public void rotateZ (float angle) {
		Matrix4f rotationMatrix = fill4With3(RotateZHelper(angle));
		currentMatrix = Matrix4f.mul(currentMatrix, rotationMatrix, null);
	}

	public void scale (Vector3f scale) {
		Matrix4f scaleMatrix = new Matrix4f();
		scaleMatrix.m00 = scale.x;
		scaleMatrix.m11 = scale.y;
		scaleMatrix.m22 = scale.z;

		currentMatrix = Matrix4f.mul(currentMatrix, scaleMatrix, null);
	}

	public void translate (Vector3f offset) {
		Matrix4f translateMatrix = new Matrix4f();
		translateMatrix.m30 = offset.x;
		translateMatrix.m31 = offset.y;
		translateMatrix.m32 = offset.z;
		translateMatrix.m33 = 1.0f;

		currentMatrix = Matrix4f.mul(currentMatrix, translateMatrix, null);
	}

	public void push() {
		matrices.push(currentMatrix);
	}

	public void pop() {
		currentMatrix = matrices.pop();
	}
}

class GLHierarchy {
	private EventLogger logger;

	private Vector3f posBase;
	private float angBase;
	
	private Vector3f posBaseLeft, posBaseRight;
	private float scaleBaseZ;

	private float angUpperArm, sizeUpperArm;

	private Vector3f posLowerArm;
	private float angLowerArm, lenLowerArm, widthLowerArm;

	private Vector3f posWrist;
	private float angWristRoll, angWristPitch, lenWrist, widthWrist;

	private Vector3f posLeftFinger, posRightFinger;
	private float angFingerOpen, lenFinger, widthFinger, angLowerFinger;

	int theProgram, vao;
	int modelToCameraMatrixUniform;
	int indexDataLength;

	public GLHierarchy (EventLogger l, int p, int v, int m, int i) {
		logger = l;
		logger.flow("GLHierarchy init.");

		theProgram = p;
		vao = v;
		modelToCameraMatrixUniform = m;
		indexDataLength = i;

		posBase = new Vector3f(3.0f, -5.0f, -40.0f);
		angBase = -45.0f;
		posBaseLeft = new Vector3f(2.0f, 0.0f, 0.0f);
		posBaseRight = new Vector3f(-2.0f, 0.0f, 0.0f);
		scaleBaseZ = 3.0f;
		angUpperArm = -78.75f;
		sizeUpperArm = 9.0f;
		posLowerArm = new Vector3f(0.0f, 0.0f, 8.0f);
		angLowerArm = 67.5f;
		lenLowerArm = 5.0f;
		widthLowerArm = 1.5f;
		posWrist = new Vector3f(0.0f, 0.0f, 5.0f);
		angWristPitch = -11.25f;
		angWristRoll = 56.25f;
		lenWrist = 2.0f;
		widthWrist = 2.0f;
		posLeftFinger = new Vector3f(1.0f, 0.0f, 1.0f);
		posRightFinger = new Vector3f(-1.0f, 0.0f, 1.0f);
		angFingerOpen = 45.0f;
		lenFinger = 2.0f;
		widthFinger = 0.5f;
		angLowerFinger = 45.0f;
	}

	private float clamp(float value,float min,float max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}

	private void drawHelper(Matrix4f modelToMatrixCameraMatrix) {
		FloatBuffer theMatrix = BufferUtils.createFloatBuffer(16);
		modelToMatrixCameraMatrix.store(theMatrix);
		theMatrix.flip();

		GL20.glUniformMatrix4(modelToCameraMatrixUniform, false, theMatrix);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexDataLength, GL11.GL_UNSIGNED_SHORT, 0);
	}

	public void draw() {
		MatrixStack modelToCameraStack = new MatrixStack(logger);

		GL20.glUseProgram(theProgram);
		GL30.glBindVertexArray(vao);

		modelToCameraStack.translate(posBase);
		modelToCameraStack.rotateY(angBase);

		//Left base.
		logger.debug("left base");
		modelToCameraStack.push();
		modelToCameraStack.translate(posBaseLeft);
		modelToCameraStack.scale(new Vector3f(1.0f, 1.0f, scaleBaseZ));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		//Right base.
		logger.debug("right base");
		modelToCameraStack.push();
		modelToCameraStack.translate(posBaseRight);
		modelToCameraStack.scale(new Vector3f(1.0f, 1.0f, scaleBaseZ));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		drawUpperArm(modelToCameraStack);

		GL20.glUseProgram(0);
		GL30.glBindVertexArray(0);
	}

	private final static float STANDARD_ANGLE_INCREMENT = 11.25f;
	private final static float SMALL_ANGLE_INCREMENT = 9.0f;

	public void adjBase (boolean inc) {
		angBase += (inc? STANDARD_ANGLE_INCREMENT : - STANDARD_ANGLE_INCREMENT);
		angBase = angBase % 360.0f;
	}

	public void adjUpperArm (boolean inc) {
		angUpperArm += (inc? STANDARD_ANGLE_INCREMENT : - STANDARD_ANGLE_INCREMENT);
		angUpperArm = clamp(angUpperArm, -90.0f, 0.0f);
	}

	public void adjLowerArm (boolean inc) {
		angLowerArm += (inc? STANDARD_ANGLE_INCREMENT : - STANDARD_ANGLE_INCREMENT);
		angLowerArm = clamp(angLowerArm, 0.0f, 146.25f);
	}

	public void adjWristPitch (boolean inc) {
		angWristPitch += (inc? STANDARD_ANGLE_INCREMENT : - STANDARD_ANGLE_INCREMENT);
		angWristPitch = angWristPitch % 360.0f;
	}

	public void adjWristRoll (boolean inc) {
		angWristRoll += (inc? STANDARD_ANGLE_INCREMENT : - STANDARD_ANGLE_INCREMENT);
		angWristRoll = angWristRoll % 360.0f;
	}

	public void adjFingerOpen (boolean inc) {
		angFingerOpen += (inc? STANDARD_ANGLE_INCREMENT : - STANDARD_ANGLE_INCREMENT);
		angFingerOpen = clamp(angFingerOpen, 9.0f, 180.0f);
	}

	public void writePose() {
		logger.info("angBase: " + angBase);
		logger.info("angUpperArm: " + angUpperArm);
		logger.info("angLowerArm: " + angLowerArm);
		logger.info("angWristPitch: " + angWristPitch);
		logger.info("angWristRoll: " + angWristRoll);
		logger.info("angFingerOpen: " + angFingerOpen);
		logger.info("");
	}

	private void drawFingers (MatrixStack modelToCameraStack) {
		//Left finger.
		modelToCameraStack.push();
		modelToCameraStack.translate(posLeftFinger);
		modelToCameraStack.rotateY(angFingerOpen);

		modelToCameraStack.push();
		modelToCameraStack.translate(new Vector3f(0.0f, 0.0f, lenFinger / 2.0f));
		modelToCameraStack.scale(new Vector3f(widthFinger / 2.0f, widthFinger / 2.0f, lenFinger / 2.0f));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		//Left lower finger;
		modelToCameraStack.push();
		modelToCameraStack.translate(new Vector3f(0.0f, 0.0f, lenFinger));
		modelToCameraStack.rotateY(-angLowerFinger);

		modelToCameraStack.push();
		modelToCameraStack.translate(new Vector3f(0.0f, 0.0f, lenFinger / 2.0f));
		modelToCameraStack.scale(new Vector3f(widthFinger / 2.0f, widthFinger / 2.0f, lenFinger / 2.0f));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		modelToCameraStack.pop();

		modelToCameraStack.pop();

		//Right finger.
		modelToCameraStack.push();
		modelToCameraStack.translate(posRightFinger);
		modelToCameraStack.rotateY(-angFingerOpen);

		modelToCameraStack.push();
		modelToCameraStack.translate(new Vector3f(0.0f, 0.0f, lenFinger / 2.0f));
		modelToCameraStack.scale(new Vector3f(widthFinger / 2.0f, widthFinger / 2.0f, lenFinger / 2.0f));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		//Right lower finger;
		modelToCameraStack.push();
		modelToCameraStack.translate(new Vector3f(0.0f, 0.0f, lenFinger));
		modelToCameraStack.rotateY(angLowerFinger);

		modelToCameraStack.push();
		modelToCameraStack.translate(new Vector3f(0.0f, 0.0f, lenFinger / 2.0f));
		modelToCameraStack.scale(new Vector3f(widthFinger / 2.0f, widthFinger / 2.0f, lenFinger / 2.0f));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		modelToCameraStack.pop();

		modelToCameraStack.pop();
	}

	private void drawWrist (MatrixStack modelToCameraStack) {
		modelToCameraStack.push();
		modelToCameraStack.translate(posWrist);
		modelToCameraStack.rotateZ(angWristRoll);
		modelToCameraStack.rotateX(angWristPitch);

		modelToCameraStack.push();
		modelToCameraStack.scale(new Vector3f(widthWrist / 2.0f, widthWrist / 2.0f, lenWrist / 2.0f));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		drawFingers(modelToCameraStack);

		modelToCameraStack.pop();
	}

	private void drawLowerArm (MatrixStack modelToCameraStack) {
		modelToCameraStack.push();
		modelToCameraStack.translate(posLowerArm);
		modelToCameraStack.rotateX(angLowerArm);

		modelToCameraStack.push();
		modelToCameraStack.translate(new Vector3f(0.0f, 0.0f, lenLowerArm / 2.0f));
		modelToCameraStack.scale(new Vector3f(widthLowerArm / 2.0f, widthLowerArm / 2.0f, lenLowerArm / 2.0f));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		drawWrist(modelToCameraStack);

		modelToCameraStack.pop();
	}

	private void drawUpperArm (MatrixStack modelToCameraStack) {
		modelToCameraStack.push();
		modelToCameraStack.rotateX(angUpperArm);
		
		modelToCameraStack.push();
		modelToCameraStack.translate(new Vector3f(0.0f, 0.0f, (sizeUpperArm / 2.0f) - 1.0f));
		modelToCameraStack.scale(new Vector3f(1.0f, 1.0f, sizeUpperArm / 2.0f));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		drawLowerArm(modelToCameraStack);

		modelToCameraStack.pop();
	}
}

public class GLRenderer {
	private EventLogger logger;
	//private PivotalInput input;

	private long window;
	private int width;
	private int height;
	float elapsedTime;

	private int theProgram;
	int positionAttrib, colorAttrib;
	int modelToCameraMatrixUniform, cameraToClipMatrixUniform;
	Matrix4f cameraToClipMatrix;

	private float CalcFrustumScale(float fovDeg) {
		float fovRad = (float)Math.toRadians(fovDeg);
		return 1.0f / (float)Math.tan(fovRad / 2.0f);
	}

	static float fFrustumScale;


	public GLRenderer (EventLogger l, long w) {
		logger = l;
		logger.flow("GLRenderer init.");

		window = w;

		init();
		//input = new PivotalInput(logger, this);
	}

	public Point screen() { return new Point(width, height);}

	private void initializeProgram() {
		ArrayList<Integer> shaderList = new ArrayList<Integer>();

		shaderList.add(loadShader(GL20.GL_VERTEX_SHADER, "FragPosition.vert"));
		shaderList.add(loadShader(GL20.GL_FRAGMENT_SHADER, "FragPosition.frag"));
		theProgram = createProgram(shaderList);

		positionAttrib = GL20.glGetAttribLocation(theProgram, "position");
		colorAttrib = GL20.glGetAttribLocation(theProgram, "color");

		modelToCameraMatrixUniform = GL20.glGetUniformLocation(theProgram, "modelToCameraMatrix");
		cameraToClipMatrixUniform = GL20.glGetUniformLocation(theProgram, "cameraToClipMatrix");

		float fzNear = 1.0f;
		float fzFar = 100.0f;

		cameraToClipMatrix = new Matrix4f();
		cameraToClipMatrix.setIdentity();
		//cameraToClipMatrix.setZero();

		cameraToClipMatrix.m00 = fFrustumScale;
		cameraToClipMatrix.m11 = fFrustumScale;
		cameraToClipMatrix.m22 = (fzFar + fzNear) / (fzNear - fzFar);
		cameraToClipMatrix.m32 = -1.0f;
		cameraToClipMatrix.m23 = (2 * fzFar * fzNear) / (fzNear - fzFar);

		//FloatBuffer theMatrix = BufferUtils.createFloatBuffer(cameraToClipMatrix.length);
		FloatBuffer theMatrix = BufferUtils.createFloatBuffer(16);
		cameraToClipMatrix.store(theMatrix);
		//theMatrix.put(cameraToClipMatrix);
		theMatrix.flip();

		GL20.glUseProgram(theProgram);
		GL20.glUniformMatrix4(cameraToClipMatrixUniform, false, theMatrix);
		GL20.glUseProgram(0);
	}

	private final static int numberOfVertices = 24;

	private final float[] vertexData = {
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

		0.0f, 0.8f, 0.0f, 1.0f,
		0.0f, 0.8f, 0.0f, 1.0f,
		0.0f, 0.8f, 0.0f, 1.0f,
		0.0f, 0.8f, 0.0f, 1.0f,

		0.0f, 0.0f, 0.8f, 1.0f,
		0.0f, 0.0f, 0.8f, 1.0f,
		0.0f, 0.0f, 0.8f, 1.0f,
		0.0f, 0.0f, 0.8f, 1.0f,

		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f,

		1.0f, 0.5f, 0.0f, 1.0f,
		1.0f, 0.5f, 0.0f, 1.0f,
		1.0f, 0.5f, 0.0f, 1.0f,
		1.0f, 0.5f, 0.0f, 1.0f,

		1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f, 1.0f,

		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 1.0f, 1.0f,
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

	int vertexBufferObject;
	int indexBufferObject;
	int vao;

	
	private void initializeVAO() {
		//XXX lwjgl
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		verticesBuffer.put(vertexData);
		verticesBuffer.flip();
		//XXX lgjwl
		vertexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


		//XXX lwjgl
		ShortBuffer indexBuffer = BufferUtils.createShortBuffer(indexData.length);
		indexBuffer.put(indexData);
		indexBuffer.flip();
		//XXX lgjwl
		indexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBufferObject);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);


		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		int colorDataOffset = 4 * 3 * numberOfVertices;
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
				logger.debug("Reshaping window works");
				width = w;
				height = h;
				reshape();
			}
		};
	}

	private void reshape () {
		cameraToClipMatrix.m00 = fFrustumScale / (height / (float) width);
		cameraToClipMatrix.m11 = fFrustumScale;

		//FloatBuffer theMatrix = BufferUtils.createFloatBuffer(perspectiveMatrix.length);
		FloatBuffer theMatrix = BufferUtils.createFloatBuffer(16);
		//theMatrix.put(cameraToClipMatrix);
		cameraToClipMatrix.store(theMatrix);
		theMatrix.flip();

		GL20.glUseProgram(theProgram);
		GL20.glUniformMatrix4(cameraToClipMatrixUniform, false, theMatrix);
		GL20.glUseProgram(0);

		GL11.glViewport(0, 0, width, height);
	}

	private GLHierarchy gArma;

	private void init() {
		fFrustumScale = CalcFrustumScale(45.0f);
		initializeProgram();
		initializeVAO();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CW);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDepthRange(0.0f, 1.0f);
		//GL11.glEnable(GL32.GL_DEPTH_CLAMP);
		elapsedTime = 0.0f;
		gArma = new GLHierarchy(logger, theProgram, vao, modelToCameraMatrixUniform, indexData.length);

		//TODO GLFWWindowResizeCallback
		GLFW.glfwSetFramebufferSizeCallback(window,  getFramebufferSizeCallback());
		width = 0;
		height = 0;
	}

	public void update (int delta) {
		//input.update(delta, null);
		elapsedTime += (float)delta / 1000.0f;
	}

	public String readShader (String filename) throws Exception {
		//XXX shady code, replace
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

	private int loadShader (int eShaderType, String shaderFileName) {
		int shader = GL20.glCreateShader(eShaderType);

		String shaderCode = null;
		try {
			shaderCode = readShader(shaderFileName);
		} catch (Exception e) {
			logger.debug("loadShader(" + eShaderType + ", " + shaderFileName + ") failed:\n" + e.getMessage());
			e.printStackTrace();
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

	private int createProgram (ArrayList<Integer> shaderList) {
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

	//Input
	public void adjBase(boolean b) { gArma.adjBase(b);}
	public void adjUpperArm(boolean b) { gArma.adjUpperArm(b);}
	public void adjLowerArm(boolean b) { gArma.adjLowerArm(b);}
	public void adjWristPitch(boolean b) { gArma.adjWristPitch(b);}
	public void adjWristRoll(boolean b) { gArma.adjWristRoll(b);}
	public void adjFingerOpen(boolean b) {gArma.adjFingerOpen(b); }
	public void logPose() { gArma.writePose();}
	//tupnI

	public void display () {
		logger.debug("display");
		//http://stackoverflow.com/questions/4338729/preserve-aspect-ratio-of-2d-object-on-window-resize
		//http://gamedev.stackexchange.com/questions/49674/opengl-resizing-display-and-glortho-glviewport
		//http://www.acamara.es/blog/tag/lwjgl/

		GL11.glClearColor(0.1f, 0.1f, 0.3f, 0.0f);
		GL11.glClearDepth(1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		gArma.draw();
	}
}
