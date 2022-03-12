package com.bfh;

import com.bfh.BaseMapperTest;
import com.bfh.mapper.CourseMapper;
import com.bfh.model.Course;
import org.apache.ibatis.session.SqlSession;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author benfeihu
 */
public class CourseMapperTest extends BaseMapperTest {

    private void testBatchIncSortByIdAndName(List<Course> courses) {
        SqlSession sqlSession = getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            int result = courseMapper.batchIncSortByMonitorAndClassRoom(courses);
            System.out.println("update " + result + " rows");
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    private List<Integer> testGroupByQueryMonitor(Integer monitor, String beginDt) {
        SqlSession sqlSession = getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<Integer> classRooms = courseMapper.queryClassRoomWithMonitor(monitor,
                    beginDt, null);
//            List<Integer> classRooms = courseMapper.queryClassRoomWithMonitor(monitor);
            return classRooms;
        } finally {
            sqlSession.close();
        }
    }

    public static void main(String[] args) {
        CourseMapperTest courseMapperTest = new CourseMapperTest();
//        List<Course> courseList = IntStream.range(0, 2).boxed().map(v -> new Course()).collect(Collectors.toList());
//        courseList.get(0).setMonitor(11);
//        courseList.get(0).setClassRoom(100);
//        courseList.get(1).setMonitor(10);
//        courseList.get(1).setClassRoom(200);
//        courseMapperTest.testBatchIncSortByIdAndName(courseList);

        System.out.println(courseMapperTest.testGroupByQueryMonitor(10, "2019-01-01 16:12:12"));
    }
}
