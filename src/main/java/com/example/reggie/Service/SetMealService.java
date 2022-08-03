package com.example.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.DTO.SetmealDto;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.Setmeal;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;

/**
 * @date 2022/7/9- 22:12
 */
public interface SetMealService extends IService<Setmeal>{

    public void saveWithDishes(SetmealDto setmealDto);

    public void deleteWithDish(List<Long> ids);

    public void statusChange(List<Long> ids,Integer status);
}
