package com.bfh.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
//@Component
//@ConfigurationProperties(prefix = "mycar")
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    private String brand;
    private Integer price;

}
