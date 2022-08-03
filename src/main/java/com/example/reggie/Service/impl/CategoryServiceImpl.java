package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.Service.CategoryService;
import com.example.reggie.Service.DishService;
import com.example.reggie.Service.SetMealService;
import com.example.reggie.common.CustomerException;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2022/7/13- 17:02
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
   @Autowired
    private DishService dishService;
   @Autowired
    private SetMealService setMealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishqueryWrapper = new LambdaQueryWrapper<>();
        dishqueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishqueryWrapper);
        if(count1>0){
        throw new CustomerException("此类型已关联菜品，不可删除");
        }
        LambdaQueryWrapper<Setmeal> setmealquerymapper = new LambdaQueryWrapper<>();
        setmealquerymapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setmealquerymapper);
        if(count2>0){
            throw new CustomerException("此类型已关联套餐，不可删除");
        }
        super.removeById(id);
    }
}
