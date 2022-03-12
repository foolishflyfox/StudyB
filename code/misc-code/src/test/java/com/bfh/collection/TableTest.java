package com.bfh.collection;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Test;

import java.util.Set;

/**
 * @author benfeihu
 */
public class TableTest {
    @Test
    public void test01() {
        Table<Object, Object, Object> table = HashBasedTable.create();
        table.put("jack", "java", 98);
        table.put("jack", "php", 65);
        table.put("jack", "ui", 80);
        table.put("jack", "mysql", 86);

        Set<Table.Cell<Object, Object, Object>> cells = table.cellSet();
        cells.forEach(c -> System.out.println(c.getRowKey() + "-" + c.getColumnKey() + " : " + c.getValue()));
        System.out.println(table.get("java", "ui"));
    }
}
