package com.synseaero.fpv.model;


public class FlightInformation {

    private static final int DEFAULT_EXPIRED_TIME = 5000;

    //等级
    public int level;
    //类型 0正常 1警告 2异常
    public int type;

    public String information;

    public long expiredTime;


    public FlightInformation() {
        expiredTime = System.currentTimeMillis() + DEFAULT_EXPIRED_TIME;
    }

    public boolean equals(Object other) {
        if (other instanceof FlightInformation) {
            return ((FlightInformation) other).level == level;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return level;
    }
}
