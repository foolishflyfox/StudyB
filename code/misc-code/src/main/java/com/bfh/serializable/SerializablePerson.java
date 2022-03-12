package com.bfh.serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author benfeihu
 */
@Data
@AllArgsConstructor
public class SerializablePerson implements Serializable {

    private String name;

    private Integer age;
}
