package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @AutoFill(OperationType.INSERT)
    void addCategory(Category category);


    Page<Category> list(CategoryPageQueryDTO categoryPageQueryDTO);

    void updatecategory(Category category);

    @AutoFill(OperationType.UPDATE)
    void deleteCategory(Long id);

    List<Category> typeSelect(Integer type);
}
