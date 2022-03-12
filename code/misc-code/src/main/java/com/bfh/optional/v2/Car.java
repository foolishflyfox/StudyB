package com.bfh.optional.v2;

import lombok.Getter;

import java.util.Optional;

/**
 * @author benfeihu
 */
public class Car {

    @Getter
    private Optional<Insurance> insurance;

}
