package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.Service.DishFlavorService;
import com.example.reggie.Service.DishService;
import com.example.reggie.Service.SetMealService;
import com.example.reggie.Service.SetmealDishService;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @date 2022/7/13- 17:02
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetMealService setMealService;
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavors(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新基本字段
        this.updateById(dishDto);
        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> list = dishDto.getFlavors();
//        List<DishFlavor> dishFlavors=new ArrayList<>();
//        for (DishFlavor item: list){
//            DishFlavor dishFlavor = new DishFlavor();
//            BeanUtils.copyProperties(item,dishFlavor);
//            System.out.println("-----------------------------------------------------"+dishFlavor.getDishId());
//            dishFlavor.setDishId(dishDto.getId());
//            System.out.println("-------------+++++++++++++++++++++++++++++++++++++----------"+dishFlavor.getDishId());
//            dishFlavors.add(dishFlavor);
//        }
        list= list.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(list);
    }

    @Override
    public void changeStatus(List<Long> ids, Integer status) {
        List<Dish> dishes=new ArrayList<>();
        for (Long id:ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            dishes.add(dish);


        }
        this.updateBatchById(dishes);
        //第一种：菜品状态更改后，套餐一并更改
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        List<Long> setmeals=new ArrayList<>();
        for (SetmealDish setmealDish: list) {
            Long setmealId = setmealDish.getSetmealId();
            setmeals.add(setmealId);
        }
        List<Long> setmeales = setmeals.stream().distinct().collect(Collectors.toList());
//        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
//        wrapper.in(Setmeal::getId,setmeales);
//        List<Setmeal> setmealList = setMealService.list(wrapper);
//        for (Setmeal setmeal:setmealList){
//            setmeal.setStatus(status);
//        }
//        setMealService.updateBatchById(setmealList);
        setMealService.statusChange(setmeales,status);
    }
}
