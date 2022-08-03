package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.Service.DishFlavorService;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
 * @date 2022/7/13- 17:02
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
