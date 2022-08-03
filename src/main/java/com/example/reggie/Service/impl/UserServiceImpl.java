package com.example.reggie.Service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.Service.EmployeeService;
import com.example.reggie.Service.UserService;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.User;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @date 2022/7/9- 22:38
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
