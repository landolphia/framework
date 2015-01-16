package framework;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.lwjgl.util.vector.Vector3f;

//TODO update when new version rolls out
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

import framework.GLMatrixStack;


public class GLHierarchy {
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

	private int theProgram, vao;
	private int modelToCameraMatrixUniform;
	private int indexDataLength;

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
		GLMatrixStack modelToCameraStack = new GLMatrixStack(logger);

		GL20.glUseProgram(theProgram);
		GL30.glBindVertexArray(vao);

		modelToCameraStack.translate(posBase);
		modelToCameraStack.rotateY(angBase);

		//Left base.
		modelToCameraStack.push();
		modelToCameraStack.translate(posBaseLeft);
		modelToCameraStack.scale(new Vector3f(1.0f, 1.0f, scaleBaseZ));
		drawHelper(modelToCameraStack.top());
		modelToCameraStack.pop();

		//Right base.
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

	public String toString() {
		return	"angBase: " + angBase +
			"\nangUpperArm: " + angUpperArm +
			"\nangLowerArm: " + angLowerArm +
			"\nangWristPitch: " + angWristPitch +
			"\nangWristRoll: " + angWristRoll +
			"\nangFingerOpen: " + angFingerOpen;
	}

	private void drawFingers (GLMatrixStack modelToCameraStack) {
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

	private void drawWrist (GLMatrixStack modelToCameraStack) {
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

	private void drawLowerArm (GLMatrixStack modelToCameraStack) {
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

	private void drawUpperArm (GLMatrixStack modelToCameraStack) {
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
