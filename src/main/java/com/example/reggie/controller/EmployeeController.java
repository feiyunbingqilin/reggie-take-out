package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.Service.EmployeeService;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @date 2022/7/9- 23:01
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService service;

    /**
     * 登录以及验证
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employee1 = service.getOne(queryWrapper);

        if(employee1==null){
          return   R.error("登陆失败");
        }
        if(!employee1.getPassword().equals(password)){
            return R.error("密码错误");
        }
        if(employee1.getStatus()==0){
            return R.error("此人已封禁");
        }

        HttpSession session = request.getSession();
        session.setAttribute("employee", employee1.getId());
        return R.success(employee1);
    }

    /**
     * 退出登录
     * @param servletRequest
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest servletRequest){
        //清理session里保存的员工id
        servletRequest.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("此次新增的员工：{}",employee.toString());
        //将默认密码进行MD5加密
        String hex = DigestUtils.md5DigestAsHex("123456".getBytes());
        //设置各类字段值
        employee.setPassword(hex);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//       Long emId= (Long) request.getSession().getAttribute("employee");
//       employee.setCreateUser(emId);
//       employee.setUpdateUser(emId);
        service.save(employee);
        return  R.success("新增员工成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        service.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 更新员工数据
     * @param servletRequest
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest servletRequest,@RequestBody Employee employee){
        log.info(employee.toString());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) servletRequest.getSession().getAttribute("employee"));
        service.updateById(employee);
        return R.success("员工状态更新已完成");
    }

    /**
     * 根据id获取员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("查询对象id：{}",id);
        Employee employee = service.getById(id);
        return R.success(employee);
    }
}
