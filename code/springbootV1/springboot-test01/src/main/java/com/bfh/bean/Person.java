package com.bfh.bean;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 将配置文件中配置的每个属性值映射到该组件中
 *
 */
//@ConfigurationProperties(prefix = "person")
@Component
@Data
public class Person {
    @Value("${person.lastName}")
//    @Email  // lastName 必须是邮箱格式
    private String lastName;
    @Value("#{2*3}")
    private Integer age;
    private Boolean boss;
    private Date birth;
    // @Value("${person.maps}")  // 不支持复杂类型
    private Map<String, Object> maps;
    private List<Object> lists;
    private Dog dog;
}
