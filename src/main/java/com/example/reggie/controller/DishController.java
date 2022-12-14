package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.Service.CategoryService;
import com.example.reggie.Service.DishFlavorService;
import com.example.reggie.Service.DishService;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @date 2022/7/15- 1:35
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody  DishDto dishDto){
       dishService.saveWithFlavor(dishDto);

        String key="dish:"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("菜品添加成功");
    }
    @PutMapping
    public R<String> update(@RequestBody  DishDto dishDto){
        Long id = dishDto.getId();
        Dish byId = dishService.getById(id);
        Long originId = byId.getCategoryId();

        dishService.updateWithFlavor(dishDto);
        //获取并清理以"dish:"开头的数据
//        Set keys = redisTemplate.keys("dish:*");
//        redisTemplate.delete(keys);
        //精确删除
        String key="dish:"+dishDto.getCategoryId()+"_1";
        String oriKey="dish:"+originId+"_1";
        if(key.equals(oriKey)){
            redisTemplate.delete(key);
        }else {
            redisTemplate.delete(key);
            redisTemplate.delete(oriKey);
        }

        return R.success("菜品更新成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageinfo = new Page(page,pageSize);
        Page<DishDto> pagedto = new Page();

        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

       //执行分页查询
        dishService.page(pageinfo,lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageinfo,pagedto,"records");

        //
        List<Dish> records = pageinfo.getRecords();

        List<DishDto> dishDtos=new ArrayList<>();
        for (Dish item: records){
            DishDto dto = new DishDto();
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
     * 根据请求的id回显菜品信息和口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto withFlavors = dishService.getByIdWithFlavors(id);
        return R.success(withFlavors);
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dtoList=null;
        //动态构造key
        String key="dish:"+dish.getCategoryId()+"_"+dish.getStatus();
        //先从redis中获取数据
        dtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);

        if(dtoList!=null){
            //若存在，则直接返回数据，无需查询数据库
            return R.success(dtoList);
        }
        //若不存在，则查询数据库，并存入redis
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(queryWrapper);
        dtoList=new ArrayList<>();
        for (Dish item: list){
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item,dto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dto.setCategoryName(categoryName);

            //当前菜品ID
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper();
            wrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> flavors = dishFlavorService.list(wrapper);
            dto.setFlavors(flavors);
            dtoList.add(dto);
        }
        redisTemplate.opsForValue().set(key,dtoList,60, TimeUnit.MINUTES);
        return R.success(dtoList);
    }
    /**
     * 售卖与否
     */
    @PostMapping("/status/{status}")
    public R<String> Cstatus(@RequestParam List<Long> ids,@PathVariable Integer status){

        dishService.changeStatus(ids,status);
        return R.success("状态更改成功");
    }
}
