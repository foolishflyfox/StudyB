package com.bfh;

import com.bfh.mapper.UserMapper;
import com.bfh.model.SysRole;
import com.bfh.model.SysUser;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author benfeihu
 */
public class UserMapperTest extends BaseMapperTest {

    public void selectById(Long id) {
        System.out.println("selectById(" + id + "):");
        try (SqlSession sqlSession = getSqlSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            SysUser user = userMapper.selectById(id);
            System.out.println(user);
        }
    }

    public void selectAll() {
        System.out.println("selectAll: ");
        try (SqlSession sqlSession = getSqlSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<SysUser> sysUsers = userMapper.selectAll();
            System.out.println(sysUsers);
        }
    }

    private void selectRolesByUserId(Long userId) {
        System.out.println("selectRolesByUserId(" + userId + "):");
        try (SqlSession sqlSession = getSqlSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<SysRole> sysRoles = userMapper.selectRolesByUserId(userId);
            System.out.println(sysRoles);
        }
    }

    private void insert(SysUser user) {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int cnt = userMapper.insert(user);
            sqlSession.commit();
            System.out.println("insert " + cnt + " rows");
        } catch (RuntimeException e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
    }

    private void insert2(SysUser user) {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int cnt = userMapper.insert2(user);
            long id = user.getId();
            System.out.println(String.format("insert %d row, id = %d", cnt, id));
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    private void update(SysUser user) {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int result = userMapper.updateById(user);
            System.out.println("update " + result + " rows");
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    private void updateByIdSelective(SysUser user) {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int result = userMapper.updateByIdSelective(user);
            System.out.println("update " + result + " rows");
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    private void delete(Long id) {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int result = userMapper.deleteById(id);
            System.out.println("delete " + result + " row");
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    private void selectRolesByUserIdAndRoleEnabled(Long id, int enabled) {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<SysRole> sysRoles = userMapper.selectRolesByUserIdAndRoleEnabled(id, enabled);
            System.out.println(sysRoles);
        } finally {
            sqlSession.close();
        }
    }

    private void selectByUser() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 只查询用户名时
            System.out.println("query1: " + userMapper.selectByUser("ad", null));
            // 只用邮箱查询时
            System.out.println("query2: " + userMapper.selectByUser(null, "test@mybatis.xx"));
            // 同时通过用户名与邮箱查询
            System.out.println("query3: " + userMapper.selectByUser("ad", "test@mybatis.xx"));
        } finally {
            sqlSession.close();
        }
    }

    private void selectByUser2() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 只查询用户名时
            System.out.println("query1: " + userMapper.selectByUser2("ad", null));
            // 只用邮箱查询时
            System.out.println("query2: " + userMapper.selectByUser2(null, "test@mybatis.xx"));
            // 同时通过用户名与邮箱查询
            System.out.println("query3: " + userMapper.selectByUser2("ad", "test@mybatis.xx"));
        } finally {
            sqlSession.close();
        }
    }

    private void selectByIdOrUserName() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            System.out.println("selectByIdOrUserName query1: " + userMapper.selectByIdOrUserName(1L, "admin"));
            System.out.println("selectByIdOrUserName query2: " + userMapper.selectByIdOrUserName(null, "admin"));
            System.out.println("selectByIdOrUserName query3: " + userMapper.selectByIdOrUserName(null, null));
        } finally {
            sqlSession.close();
        }
    }

    private void selectByIdList(List<Long> idList) {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<SysUser> sysUsers = userMapper.selectByIdList(idList);
            System.out.println(sysUsers);
        } finally {
            sqlSession.close();
        }
    }

    private void insertList(List<SysUser> sysUserList) {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int result = userMapper.insertList(sysUserList);
            System.out.println("insert " + result + " rows");
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    private void updateByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("user_password", "xxxxxx");
        map.put("id", 1011L);
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.updateByMap(map);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }


    public static void main(String[] args) {
        UserMapperTest test = new UserMapperTest();
//        test.selectById(1L);
//        test.selectAll();
//        test.selectRolesByUserId(1L);

//        测试 insert 操作
//        test.insert2(SysUser.builder()
//                .userName("test1")
//                .userPassword("123456")
//                .userEmail("test@mybatis.tk")
//                .userInfo("test info")
//                .headImg(new byte[]{1,2,3})
//                .createTime(new Date(System.currentTimeMillis()))
//                .build()
//        );

//        测试 update 操作
//        test.update(SysUser.builder().id(1006L).userName("updatedName").build());

//        测试 delete 操作
//        test.delete(1005L);

//        测试多参数
//        test.selectRolesByUserIdAndRoleEnabled(1L, 1);

//        choose 测试
//        test.selectByIdOrUserName();

//        if 测试
//        test.selectByUser();

//        where 测试
//        test.selectByUser2();

//        set 测试
//        test.updateByIdSelective(SysUser.builder().id(1007L).userName("updatedName").build());

//        foreach 测试：批量查询
//        test.selectByIdList(Arrays.asList(1001L, 1L));

//        foreach 测试：批量插入
//        test.insertList(Arrays.asList(SysUser.builder().userName("insert1-a").build(),
//                SysUser.builder().userName("insert1-b").build()));

//        foreach 测试：通过 Map 更新
        test.updateByMap();

    }
}
