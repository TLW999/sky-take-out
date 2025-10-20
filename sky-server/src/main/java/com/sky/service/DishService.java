package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDish(DishDTO dishDTO);

    PageResult pageDish(DishPageQueryDTO dishdto);

    void deleteDish(List<Long> ids);

    DishVO getById(Long id);

    void update(DishDTO dishdto);

    void ableOrEnable(Integer status, Long id);

    List<Dish> dishlist(Long categoryId);
}
