package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.Service.AddressBookService;
import com.example.reggie.Service.CategoryService;
import com.example.reggie.Service.DishService;
import com.example.reggie.Service.SetMealService;
import com.example.reggie.common.CustomerException;
import com.example.reggie.entity.AddressBook;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.mapper.AddressBookMapper;
import com.example.reggie.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2022/7/13- 17:02
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
