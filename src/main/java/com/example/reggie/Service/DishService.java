package com.example.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.common.R;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Employee;

import java.util.List;

/**
 * @date 2022/7/9- 22:12
 */
public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavors(Long id);

    void updateWithFlavor(DishDto dishDto);

    public void changeStatus(List<Long> ids,Integer status);
}
