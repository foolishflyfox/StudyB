package com.bfh.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author benfeihu
 */
@Component
//@AllArgsConstructor  // 自动注入属性 field，不需要 Autowired，@Value 会
//@RequiredArgsConstructor
@Data
public class Family {
    private final Car car;
    private final User user;
//    @Value("${family.id}")
//    private final Integer id;
}
