package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.DTO.SetmealDto;
import com.example.reggie.Service.CategoryService;
import com.example.reggie.Service.SetMealService;
import com.example.reggie.Service.SetmealDishService;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @date 2022/7/18- 1:05
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setMealService.save(setmealDto);
        Long id = setmealDto.getId();
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes.stream().map((item)->{
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> get(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Setmeal> pageinfo = new Page(page,pageSize);
        Page<SetmealDto> pagedto = new Page();

        //条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName,name);

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分页查询
        setMealService.page(pageinfo,lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageinfo,pagedto,"records");

        //
        List<Setmeal> records = pageinfo.getRecords();

        List<SetmealDto> dishDtos=new ArrayList<>();
        for (Setmeal item: records){
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item,dto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dto.setCategoryName(categoryName);
            dishDtos.add(dto);
        }
        pagedto.setRecords(dishDtos);

        return R.success(pagedto);
    }
    /**
     * 删除套餐
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setMealService.deleteWithDish(ids);
        return R.success("套餐删除成功");
    }
    /**
     * 售卖与否
     */
    @PostMapping("/status/{status}")
    public R<String> Cstatus(@RequestParam List<Long> ids,@PathVariable Integer status){
       setMealService.statusChange(ids,status);
        log.info("ids:{}",ids);
        log.info("status:{}",status);
        return R.success("状态更改成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,1);
        List<Setmeal> list = setMealService.list(queryWrapper);
        return R.success(list);
    }
}
