package com.bfh.mapper;

import com.bfh.model.Course;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @author benfeihu
 */
public interface CourseMapper {

    int batchIncSortByMonitorAndClassRoom(List<Course> courseList);

    List<Integer> queryClassRoomWithMonitor(@Param("monitor") Integer monitor,
                                            @Param("beginDt") String beginDt, @Param("endDt") String endDt);

}
