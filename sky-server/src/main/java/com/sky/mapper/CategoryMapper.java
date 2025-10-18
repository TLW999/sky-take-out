package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    void addCategory(Category category);


    Page<Category> list(CategoryPageQueryDTO categoryPageQueryDTO);

    void updatecategory(Category category);


    void deleteCategory(Long id);
}
