package com.bfh.serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author benfeihu
 */
@Data
@AllArgsConstructor
public class SerializableImpl implements Serializable {
    private transient NormalPerson normalPerson;
    private SerializablePerson serializablePerson;
}
