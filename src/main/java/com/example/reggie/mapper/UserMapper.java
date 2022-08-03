package com.example.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @date 2022/7/9- 22:02
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
