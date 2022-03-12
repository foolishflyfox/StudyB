package com.bfh.mapper;

import com.bfh.model.SysRole;
import com.bfh.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    SysUser selectById(Long id);

    List<SysUser> selectAll();

    List<SysRole> selectRolesByUserId(Long userId);

    int insert(SysUser user);

    int insert2(SysUser user);

    int updateById(SysUser user);

    // deleteById(SysUser user) 也是正确的
    int deleteById(Long id);

    /**
     * 根据用户 id 和角色 enabled 状态获取用户角色
     */
    List<SysRole> selectRolesByUserIdAndRoleEnabled(@Param("userId") Long userId, @Param("enabled") Integer enabled);

    List<SysUser> selectByUser(@Param("userName") String userName, @Param("userEmail") String userEmail);

    SysUser selectByIdOrUserName(@Param("id") Long id, @Param("userName") String userName);

    List<SysUser> selectByUser2(@Param("userName") String userName, @Param("userEmail") String userEmail);

    int updateByIdSelective(SysUser user);

    List<SysUser> selectByIdList(List<Long> idList);

    int insertList(List<SysUser> userList);

    int updateByMap(Map<String, Object> map);

}