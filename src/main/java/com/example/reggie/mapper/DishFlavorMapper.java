package com.example.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

/**
 * @date 2022/7/14- 1:07
 */
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
