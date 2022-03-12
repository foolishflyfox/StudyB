package com.bfh.factory;

import com.bfh.AirPlane;

/**
 * 实例工厂
 * @author benfeihu
 */
public class AirPlaneInstanceFactory {
    public AirPlane getAirPlan(String captainName) {
        System.out.println("AirPlanInstanceFactory create airplane ...");
        AirPlane airPlane = new AirPlane();
        airPlane.setEngine("太行");
        airPlane.setCopilotName("abc");
        airPlane.setCopilotName(captainName);
        airPlane.setCapacity(300);
        airPlane.setWingLength("30m");
        return airPlane;
    }
}
