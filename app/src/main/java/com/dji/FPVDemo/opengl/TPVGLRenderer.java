package com.dji.FPVDemo.opengl;


import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TPVGLRenderer implements GLSurfaceView.Renderer{

    private static final String TAG = "TPVGLRenderer";

    private band1 mBand1;
    private band2 mBand2;
    private ring10 mring10;
    private ring20 mring20;

    private ring11 mring11;
    private ring22 mring22;


    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];


    private float[] mRotationMatrixZ = new float[16];

    private float mAngle;

    private float mPitch;
    private float mRoll;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // set graphic transparency
        GLES20.glEnable(GL10.GL_BLEND);
        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

//        mRing1 = new ring1();

        mBand1 = new band1();
        mBand2 = new band2();

        mring10 = new ring10();
        mring20 = new ring20();

        mring11 = new ring11();
        mring22 = new ring22();

    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        float[] scratch2 = new float[16];
        float[] plusmatrix = {1, 0, 0, 0,
                0,  1, 0, 0,
                0,  0, 1, 0,
                0,  15f, 240, 1}; //135+190

//        float[] Aircraftmatrix = {1, 0, 0, 0,
//                0,  1, 0, 0,
//                0,  0, 1, 0,
//                0,  -1.15f, 3, 1}; //135+190


        // clear buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Set the camera position (View matrix)

        // camera position
//        Float ex =0.0f;
//        Float ey = -2.5f;
//        Float ez = 2.5f;
        Float ex =0.0f;
        Float ey = 0.0f;
        Float ez = 0.0f;
        // focus
        Float x =0.0f;
        Float y =0.0f;     //2.0f
        Float z =35f;

        Float upx =0.0f;
        Float upy =1.0f;    // 1.0f
        Float upz =0.0f;    // 0.0f

        Matrix.setLookAtM(mViewMatrix,0,ex,ey,ez,x,y,z,upx,upy,upz);

/* matrix for aircraft postion*/
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw static square
//        Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 1.0f, 0f);
//        Matrix.rotateM(mRotationMatrix,0,-mPitch,1f,0,0);
//        Matrix.rotateM(mRotationMatrix,0,mRoll,0f,0f,1f);

//        Matrix.multiplyMM(mRotationMatrix,0,Aircraftmatrix,0, mRotationMatrix,0);



/* matrix for grahpic postion*/
        Matrix.setRotateM(mRotationMatrixZ,0,mAngle,0f,1f,0f);
//
        Matrix.rotateM(mRotationMatrixZ,0,mPitch,1f,0,0);
        Matrix.rotateM(mRotationMatrixZ,0,mRoll,0f,0f,1f);

        Matrix.multiplyMM(mRotationMatrixZ,0,plusmatrix,0, mRotationMatrixZ,0);
//        Matrix.translateM(mRotationMatrixZ,0,0,0f,-190f); // 移回原位，绕设定轴转



        // zoom in and zoom out effect
//        Matrix.translateM(mRotationMatrixZ,0,0,-1.0f,0);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(scratch2, 0, mMVPMatrix, 0, mRotationMatrixZ, 0);


        // Draw triangle

//        mSquare.draw(scratch);
//        mAircraftmodel.draw(mMVPMatrix);
//        mTriangle.draw(mMVPMatrix);



        mBand1.draw(scratch2);
        mBand2.draw(scratch2);
        mring10.draw(scratch2);
        mring20.draw(scratch2);
        mring11.draw(scratch2);
        mring22.draw(scratch2);

//        mBand1.draw(scratch);
//        mBand2.draw(scratch);
//        mring10.draw(scratch);
//        mring20.draw(scratch);

//        mAircraftmodel.draw(scratch);


    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1.94f, 10000);  // n=1.94 : FOV =54.43 , n=h/(2tan(FOV))

//        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2, 7);



    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void setPitch(float pitch) {
        mPitch = pitch;
    }

    public void setRoll(float roll){
        mRoll = roll;
    }
}
