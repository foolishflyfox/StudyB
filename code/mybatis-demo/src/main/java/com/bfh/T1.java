package com.bfh;

import lombok.SneakyThrows;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * @author benfeihu
 */
public class T1 {

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            Reader reader = Resources.getResourceAsReader("mapper/SqlMapConfig.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException ignore) {
            ignore.printStackTrace();
        }
    }

    public static int insertStudent(StudentModel student) {
        try {
            SqlSession sqlSession = sqlSessionFactory.openSession();
            sqlSession.insert("student.insert", student);
            sqlSession.commit();
            sqlSession.close();
            return student.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @SneakyThrows
    public static void selectAllStudent() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        List<StudentModel> selectAll = sqlSession.selectList("selectAll");
        System.out.println(selectAll);
    }

    public static void main(String[] args) {
//         insertStudent(new StudentModel(2, "a", 10));
        selectAllStudent();
    }
}
