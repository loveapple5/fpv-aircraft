package com.synseaero.util;

import android.util.Log;

import java.util.Vector;

public class CoordinateUtils {

    //地球赤道半径（单位：米）
    public static final double Re = 6378137;

    //地球极地半径（单位：米）
    public static final double Rp = 6356752;



    //椭圆矫正参数
    public static final double E = (Re * Re - Rp * Rp) / (Re * Re);

    /***
     * 根据纬度校正后的地球半径
     * @param latitude
     * @return
     */
    public static double getRadiusByLatitude(double latitude) {
        double sinLa = Math.sin(latitude);
        double W = Math.sqrt(1 - E * E * sinLa * sinLa);

        Log.d("CoordinateUtils", "sinLa"+ sinLa);
        Log.d("CoordinateUtils", "W"+ W);

        double result = Re / W;
        Log.d("CoordinateUtils", "result"+ result);
        return result;
    }

    /***
     * 通过经纬度海拔获取地心坐标
     */
    public static Vector<Double> getGeoCoordinateByLBH(Vector<Double> LBH) {
        double longitude = LBH.get(0);
        double latitude = LBH.get(1);
        double height = LBH.get(2);
        double N = getRadiusByLatitude(latitude);

        Log.d("CoordinateUtils", "N"+ N);

        double X = (N + height) * Math.cos(latitude) * Math.sin(longitude);
        double Y = (N * (1 - E * E) + height) * Math.sin(latitude);
        double Z = (N + height) * Math.cos(latitude) * Math.cos(longitude);

        Log.d("CoordinateUtils", "X"+ X);
        Log.d("CoordinateUtils", "Y"+ Y);
        Log.d("CoordinateUtils", "Z"+ Z);

        Vector<Double> geoCoordinate = new Vector<>();
        geoCoordinate.add(X);
        geoCoordinate.add(Y);
        geoCoordinate.add(Z);
        return geoCoordinate;
    }


    /**
     * 获取向量AB在坐标系中的方向角
     * @param A
     * @param B
     * @return
     */
    public static Vector<Double> getAngleByVector(Vector<Double> A, Vector<Double> B) {
        double X = B.get(0) - A.get(0);
        double Y = B.get(1) - A.get(1);
        double Z = B.get(2) - A.get(2);
        double length = Math.sqrt(X * X + Y * Y + Z * Z);
        double Ax = Math.acos(X / length);
        double Ay = Math.acos(Y / length);
        double Az = Math.acos(Z / length);
        Vector<Double> angle = new Vector<>();
        angle.add(Ax);
        angle.add(Ay);
        angle.add(Az);
        return angle;
    }


    /**
     * 将手机的PRY转换到地心坐标系下的方向角
     * @param PRY
     * @param LBH
     * @return
     */
    public static Vector<Double> getAngleByPRYAndLBH(Vector<Double> PRY, Vector<Double> LBH) {
        double pitch = PRY.get(0);
        double roll = PRY.get(1);
        double yaw = PRY.get(2);

        double longitude = LBH.get(0);
        double latitude = LBH.get(1);
        double height = LBH.get(2);

        // x = cosL * cosP + sinL * (sinB * cosR + cosB * cosY)
        double X = Math.cos(longitude) * Math.cos(pitch) + Math.sin(longitude) * (Math.sin(latitude) * Math.cos(roll) +  Math.cos(latitude) * Math.cos(yaw));
        // y = cosL * cosR - sinB * cosY
        double Y = Math.cos(latitude) * Math.cos(roll) - Math.sin(latitude) * Math.cos(yaw);
        // z = -sinL * cosP + cosL * (sinB * cosR + cosL * cosY)
        double Z = -Math.sin(longitude) * Math.cos(pitch) + Math.cos(longitude) * (Math.sin(latitude) * Math.cos(roll) + Math.cos(latitude) * Math.cos(yaw));
        Vector<Double> vector = new Vector<>();
        vector.add(X);
        vector.add(Y);
        vector.add(Z);

        Vector<Double> core = new Vector<>();
        core.add(0d);
        core.add(0d);
        core.add(0d);
        Vector angle = getAngleByVector(core, vector);
        return angle;
    }

    public static Vector<Double> getSurfaceAngleByCoreAngle(Vector<Double> LBH, Vector<Double> cAngle) {
        double aX = cAngle.get(0);
        double aY = cAngle.get(1);
        double aZ = cAngle.get(2);

        double longitude = LBH.get(0);
        double latitude = LBH.get(1);
        double height = LBH.get(2);

        //通过经纬度海拔数据和地心方向角计算地表单位向量
        // x = cosL * cos(aX) - sinL * cos(aZ)
        double vX = Math.cos(longitude) * Math.cos(aX) - Math.sin(longitude) * Math.cos(aZ);
        // y = sinL * sinB * cos(aX) + cosB * cos(aY) + cosL * sinB * cos(aZ)
        double vY = Math.sin(longitude) * Math.sin(latitude) * Math.cos(aX) + Math.cos(latitude) * Math.cos(aY) + Math.cos(longitude) * Math.sin(latitude) * Math.cos(aZ);
        // z = sinL * cosB * cos(aX) - sinB * cos(aY) + cosL * cosB * cos(aZ)
        double vZ = Math.sin(longitude) * Math.cos(latitude) * Math.cos(aX) - Math.sin(latitude) * Math.cos(aY) + Math.cos(longitude) * Math.cos(latitude) * Math.cos(aZ);

        Log.d("CoordinateUtils", "vX:" + vX);
        Log.d("CoordinateUtils", "vY:" + vY);
        Log.d("CoordinateUtils", "vZ:" + vZ);

        Vector<Double> vector = new Vector<>();
        vector.add(vX);
        vector.add(vY);
        vector.add(vZ);

        Vector<Double> core = new Vector<>();
        core.add(0d);
        core.add(0d);
        core.add(0d);
        Vector angle = getAngleByVector(core, vector);
        return angle;
    }
}
