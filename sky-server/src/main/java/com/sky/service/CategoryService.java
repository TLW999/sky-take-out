package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;

public interface CategoryService {
    void setCategory(CategoryDTO categoryDTO);

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void updatecategory(CategoryDTO categoryDTO);

    void ableOrenable(Integer status, Long id);

    void deleteCategory(Long id);
}
