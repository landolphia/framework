package framework;

//TODO update when new util is out
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import java.util.Stack;

public class GLMatrixStack {
	private EventLogger logger;
	private Stack<Matrix4f> matrices;
	private Matrix4f currentMatrix;

	public GLMatrixStack (EventLogger l) {
		logger = l;
		//logger.flow("GLMatrixStack init");

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
