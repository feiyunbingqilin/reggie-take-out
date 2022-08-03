package com.example.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @date 2022/7/13- 17:00
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
