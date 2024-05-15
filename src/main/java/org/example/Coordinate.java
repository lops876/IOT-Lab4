package org.example;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.Serializable;

public class Coordinate implements Serializable{
    private static final Coordinate ORIGIN = new Coordinate();
    // 校区坐标
    double longitude = 118.308847;
    double latitude = 24.605333;
    // 赤道半径(单位m)
    private static final double EARTH_RADIUS = 6371000;

    public String toString(){
        return String.format("(%s,%s)",longitude,latitude);
    }
    public void move(double lo, double la){
        longitude = BigDecimal.valueOf(longitude + lo).setScale(6,RoundingMode.UP).doubleValue();
        latitude = BigDecimal.valueOf(latitude + la).setScale(6,RoundingMode.UP).doubleValue();
    }
     // 转为为弧度(rad)
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
    public double distance(){
        double radLat1 = rad(latitude);
        double radLat2 = rad(ORIGIN.latitude);
        double a = radLat1 - radLat2;
        double b = rad(longitude) - rad(ORIGIN.longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = (double) Math.round(s * 10000) / 10000;
        return s;
    }

}
