package com.example.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.Category;
import org.springframework.stereotype.Service;

/**
 * @date 2022/7/13- 17:01
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
