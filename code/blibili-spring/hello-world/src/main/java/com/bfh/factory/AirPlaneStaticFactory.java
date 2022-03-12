package com.bfh.factory;

import com.bfh.AirPlane;

/**
 * 静态工厂
 * @author benfeihu
 */
public class AirPlaneStaticFactory {
    public static AirPlane getAirPlane(String captainName) {
        System.out.println("AirPlanStaticFactory create airplane ...");
        AirPlane airPlane = new AirPlane();
        airPlane.setEngine("太行");
        airPlane.setCopilotName("abc");
        airPlane.setCopilotName(captainName);
        airPlane.setCapacity(300);
        airPlane.setWingLength("30m");
        return airPlane;
    }
}
