package com.bfh;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author benfeihu
 */
@Getter @Setter @ToString
public class AirPlane {
    private String engine;
    private String wingLength;
    private Integer capacity;
    private String captainName;
    private String copilotName;
}
