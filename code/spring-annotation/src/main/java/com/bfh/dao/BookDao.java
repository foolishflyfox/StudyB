package com.bfh.dao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

/**
 * @author benfeihu
 */
@Repository
@Data
public class BookDao {
    private String label = "1";
}
