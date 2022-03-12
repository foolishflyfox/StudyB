package com.bfh.txdetail;

import org.springframework.jdbc.core.JdbcTemplate;

public class BookDao {

    JdbcTemplate jdbcTemplate;
    /**
     * 减余额 update account set balance=balance-? where user_name = ?
     */
    public int updateBalance(String userName, int price) {
        String sql = "update account set balance=balance-? where username=?";
        return jdbcTemplate.update(sql, price, userName);
    }

    public int getPrice(String isbn) {
        String sql = "select price from book where isbn=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, isbn);
    }

    public int updateStock(String isbn) {
        String sql = "update book_stock set stock=stock-1 where isbn=?";
        return jdbcTemplate.update(sql, isbn);
    }
}
