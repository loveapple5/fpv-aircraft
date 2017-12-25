package com.synseaero.fpv.opengl;


import android.hardware.GeomagneticField;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.synseaero.util.CoordinateUtils;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TPVGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "TPVGLRenderer";

    private band1 mBand1;
    private band2 mBand2;
    private ring10 mring10;
    private ring20 mring20;

    private ring11 mring11;
    private ring22 mring22;

    //人的LBH
    private Vector<Double> vPhoneLBH;
    //飞机的LBH
    private Vector<Double> vAircraftLBH;
    //人的PRY
    private Vector<Double> aPhonePRY;

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

        vPhoneLBH = new Vector<Double>();
        vPhoneLBH.add(116.21426328 * Math.PI / 180);
        vPhoneLBH.add(39.6073601 * Math.PI / 180);
        vPhoneLBH.add(0.0);
        vAircraftLBH = new Vector<Double>();
        vAircraftLBH.add(116.21426328 * Math.PI / 180);
        vAircraftLBH.add(39.6068601 * Math.PI / 180);
//        vAircraftLBH.add(39.982224 * Math.PI / 180);
        vAircraftLBH.add(20.0);
        aPhonePRY = new Vector<Double>();
        aPhonePRY.add(0.0);
        aPhonePRY.add(0.0);
        aPhonePRY.add(0.0);
    }

    private long time = 0;
    private int index = 0;

    private float declination = 0;

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        float[] scratch2 = new float[16];
        float[] plusmatrix = {1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 15f, 240, 1}; //135+190

//        float[] Aircraftmatrix = {1, 0, 0, 0,
//                0,  1, 0, 0,
//                0,  0, 1, 0,
//                0,  -1.15f, 3, 1}; //135+190

        Vector<Double> aAPR = getaAPR();

        setPitch((float) (aAPR.get(0) * 180 / Math.PI));
        setRoll((float) (aAPR.get(1) * 180 / Math.PI));
        setAngle((float) (aAPR.get(2) * 180 / Math.PI));
//        setAngle(0);
//        if(System.currentTimeMillis() - time >= 5000) {
//            time = System.currentTimeMillis();
//            setAngle((45 * (++index)) % 360);
//         }


        Log.d(TAG, "mAngle" + mAngle);
        Log.d(TAG, "mPitch" + mPitch);
        Log.d(TAG, "mRoll" + mRoll);
        // clear buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Set the camera position (View matrix)

        // camera position
//        Float ex =0.0f;
//        Float ey = -2.5f;
//        Float ez = 2.5f;
        Float ex = 0.0f;
        Float ey = 0.0f;
        Float ez = 0.0f;
        // focus
        Float x = 0.0f;
        Float y = 0.0f;     //2.0f
        Float z = 35f;

        Float upx = 0.0f;
        Float upy = 1.0f;    // 1.0f
        Float upz = 0.0f;    // 0.0f

        Matrix.setLookAtM(mViewMatrix, 0, ex, ey, ez, x, y, z, upx, upy, upz);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw static square
//        Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 1.0f, 0f);
//        Matrix.rotateM(mRotationMatrix,0,-mPitch,1f,0,0);
//        Matrix.rotateM(mRotationMatrix,0,mRoll,0f,0f,1f);

//        Matrix.multiplyMM(mRotationMatrix,0,Aircraftmatrix,0, mRotationMatrix,0);



/* matrix for grahpic postion*/

        Matrix.setRotateM(mRotationMatrixZ, 0, mAngle, 0f, 1f, 0f);
//
        Matrix.rotateM(mRotationMatrixZ, 0, mPitch, 1f, 0, 0);
        Matrix.rotateM(mRotationMatrixZ, 0, mRoll, 0f, 0f, 1f);

        Matrix.multiplyMM(mRotationMatrixZ, 0, plusmatrix, 0, mRotationMatrixZ, 0);
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
     * <p>
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

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
     * <p>
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     * <p>
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

    public void setRoll(float roll) {
        mRoll = roll;
    }

    public synchronized void setvPhoneLBH(Vector<Double> vPhoneLBH) {
        this.vPhoneLBH = vPhoneLBH;
        long curTime = System.currentTimeMillis();
        float lat = (float)(this.vPhoneLBH.get(1) * 180 / Math.PI);
        float lon = (float)(this.vPhoneLBH.get(0) * 180 / Math.PI);
        GeomagneticField geoField = new GeomagneticField(lat,lon, 0f, curTime);
        declination = geoField.getDeclination();
    }

    public synchronized void setvAircraftLBH(Vector<Double> vAircraftLBH) {
        this.vAircraftLBH = vAircraftLBH;
    }

    public synchronized void setaPhonePRY(Vector<Double> aPhonePRY) {
        this.aPhonePRY = aPhonePRY;
        this.aPhonePRY.set(2, this.aPhonePRY.get(2) + declination);
    }

//    public synchronized Vector<Double> getaAPR() {
//
//        Log.d(TAG, "vPhoneLBH0:" + vPhoneLBH.get(0).floatValue());
//        Log.d(TAG, "vPhoneLBH1:" + vPhoneLBH.get(1).floatValue());
//        Log.d(TAG, "vPhoneLBH2:" + vPhoneLBH.get(2).floatValue());
//
//        Log.d(TAG, "vAircraftLBH0:" + vAircraftLBH.get(0).floatValue());
//        Log.d(TAG, "vAircraftLBH1:" + vAircraftLBH.get(1).floatValue());
//        Log.d(TAG, "vAircraftLBH2:" + vAircraftLBH.get(2).floatValue());
//
//        Log.d(TAG, "aPhonePRY0:" + vAircraftLBH.get(0).floatValue());
//        Log.d(TAG, "aPhonePRY1:" + vAircraftLBH.get(1).floatValue());
//        Log.d(TAG, "aPhonePRY2:" + vAircraftLBH.get(2).floatValue());
//
//        //通过手机的LBH得到手机的地心坐标
//        Vector<Double> cPhone = CoordinateUtils.getGeoCoordinateByLBH(this.vPhoneLBH);
//
//        Log.d(TAG, "cPhone0:" + cPhone.get(0).floatValue());
//        Log.d(TAG, "cPhone1:" + cPhone.get(1).floatValue());
//        Log.d(TAG, "cPhone2:" + cPhone.get(2).floatValue());
//
//        //通过飞机的LBH得到飞机的地心坐标
//        Vector<Double> cAircraft = CoordinateUtils.getGeoCoordinateByLBH(this.vAircraftLBH);
//
//        Log.d(TAG, "cAircraft0:" + cAircraft.get(0).floatValue());
//        Log.d(TAG, "cAircraft1:" + cAircraft.get(1).floatValue());
//        Log.d(TAG, "cAircraft2:" + cAircraft.get(2).floatValue());
//
//        //1.得到P和A在地心坐标系下的PA向量的方向角
//        Vector<Double> aPA = CoordinateUtils.getAngleByVector(cPhone, cAircraft);
//
//        Log.d(TAG, "aPA0:" + aPA.get(0).floatValue());
//        Log.d(TAG, "aPA1:" + aPA.get(1).floatValue());
//        Log.d(TAG, "aPA2:" + aPA.get(2).floatValue());
//
//        //2.将1的结果角转到地表坐标系下
//        Vector<Double> aSurfacePA = CoordinateUtils.getSurfaceAngleByCoreAngle(this.vPhoneLBH, aPA);
//
//        Log.d(TAG, "aSurfacePA0:" + aSurfacePA.get(0).floatValue());
//        Log.d(TAG, "aSurfacePA1:" + aSurfacePA.get(1).floatValue());
//        Log.d(TAG, "aSurfacePA2:" + aSurfacePA.get(2).floatValue());
//
//        //2.通过手机的PRY转换到地心坐标系下的方向角
//        //Vector<Double> aP = CoordinateUtils.getAngleByPRYAndLBH(this.aPhonePRY, this.vPhoneLBH);
//
//        //3.求2向量房间角和手机的PRY之间的夹角
//        Vector<Double> aDeflection = new Vector<>();
//        aDeflection.add(aSurfacePA.get(0) - this.aPhonePRY.get(0));
//        aDeflection.add(aSurfacePA.get(1) - this.aPhonePRY.get(1));
//        aDeflection.add(aSurfacePA.get(2) - this.aPhonePRY.get(2));
//
//        Log.d(TAG, "aDeflection0:" + aDeflection.get(0).floatValue());
//        Log.d(TAG, "aDeflection1:" + aDeflection.get(1).floatValue());
//        Log.d(TAG, "aDeflection2:" + aDeflection.get(2).floatValue());
//
//        //4.将3的结果角转到地表坐标系下
//        //Vector<Double> aFinal = CoordinateUtils.getSurfaceAngleByCoreAngle(this.vPhoneLBH, aDeflection);
//        return aDeflection;
//
//    }

//    public synchronized Vector<Double> getaAPR() {
//
//        // Double DisNorth = R_earth*(BH-BA);
//        //Double DisEast = R_earth*Math.cos(BA)*(LH-LA);
//        double vNorth = -CoordinateUtils.Re * (vAircraftLBH.get(1) - vPhoneLBH.get(1));
//        double vEast = CoordinateUtils.Re * Math.cos(vAircraftLBH.get(1)) * (vAircraftLBH.get(0) - vPhoneLBH.get(0));
//        double vCore = vAircraftLBH.get(2) - vPhoneLBH.get(2);
//        //得到PA向量
//        Vector<Double> vPA = new Vector<>();
//        vPA.add(vNorth);
//        vPA.add(vEast);
//        vPA.add(vCore);
//
//        Log.d(TAG, "vPA0:" + vPA.get(0).floatValue());
//        Log.d(TAG, "vPA1:" + vPA.get(1).floatValue());
//        Log.d(TAG, "vPA2:" + vPA.get(2).floatValue());
//
//        //得到PA向量角
//        Vector<Double> core = new Vector<>();
//        core.add(0d);
//        core.add(0d);
//        core.add(0d);
//        Vector<Double> aPA = CoordinateUtils.getAngleByVector(core, vPA);
//
//        Log.d(TAG, "aPA0:" + aPA.get(0).floatValue());
//        Log.d(TAG, "aPA1:" + aPA.get(1).floatValue());
//        Log.d(TAG, "aPA2:" + aPA.get(2).floatValue());
//
//        Vector<Double> aDeflection = new Vector<>();
//        aDeflection.add(aPhonePRY.get(0) - aPA.get(0));
//        aDeflection.add(aPhonePRY.get(1) - aPA.get(1));
//        aDeflection.add(aPhonePRY.get(2) - aPA.get(2));
//
//        //地表坐标系偏转角
//        Log.d(TAG, "aDeflection0:" + aDeflection.get(0).floatValue());
//        Log.d(TAG, "aDeflection1:" + aDeflection.get(1).floatValue());
//        Log.d(TAG, "aDeflection2:" + aDeflection.get(2).floatValue());
//
//        return aDeflection;
//    }

    public synchronized Vector<Double> getaAPR() {

        // Double DisNorth = R_earth*(BH-BA);
        //Double DisEast = R_earth*Math.cos(BA)*(LH-LA);
        double vNorth = CoordinateUtils.Re * (vAircraftLBH.get(1) - vPhoneLBH.get(1));
        double vEast = CoordinateUtils.Re * Math.cos(vAircraftLBH.get(1)) * (vAircraftLBH.get(0) - vPhoneLBH.get(0));
        double vCore = vAircraftLBH.get(2) - vPhoneLBH.get(2);
//        double vCore = 200;
        //得到PA向量
        Vector<Double> vPA = new Vector<>();
        vPA.add(vEast);
        vPA.add(vNorth);
        vPA.add(vCore);

        Log.d(TAG, "vPA0:" + vPA.get(0).floatValue());
        Log.d(TAG, "vPA1:" + vPA.get(1).floatValue());
        Log.d(TAG, "vPA2:" + vPA.get(2).floatValue());

        //通过PA向量得到大地球坐标
        double Fa = 0;
        if(vNorth >= 0) {
            Fa = Math.asin(vEast /  Math.sqrt(vNorth * vNorth + vEast * vEast));
        } else {
            Fa = (Math.PI / 2) + Math.acos(vEast /  Math.sqrt(vNorth * vNorth + vEast * vEast));
        }
//        Fa = -Fa;
        double Si = -Math.asin(vCore / Math.sqrt(vNorth * vNorth + vEast * vEast + vCore * vCore));

        Log.d(TAG, "Fa:" + Fa * 180 / Math.PI);
        Log.d(TAG, "Si:" + Si * 180 / Math.PI);

        //手机方向角放入头盔后需要转换的角度
        double pitch = aPhonePRY.get(1) + 102 * Math.PI / 180;
        double roll =  aPhonePRY.get(0);
        double yaw = aPhonePRY.get(2) + 90 * Math.PI / 180;

        Log.d(TAG, "pitch:" + pitch * 180 / Math.PI);
        Log.d(TAG, "yaw:" + yaw * 180 / Math.PI);


        //获取最终的偏转角
        Vector<Double> aDeflection = new Vector<Double>();
        aDeflection.add(pitch - Si);
        aDeflection.add(0d);
        aDeflection.add(yaw - Fa + Math.PI);

        Log.d(TAG, "aDeflection0:" + aDeflection.get(0).floatValue() * 180 / Math.PI);
        Log.d(TAG, "aDeflection1:" + aDeflection.get(1).floatValue() * 180 / Math.PI);
        Log.d(TAG, "aDeflection2:" + aDeflection.get(2).floatValue() * 180 / Math.PI);

        return aDeflection;
    }
}
