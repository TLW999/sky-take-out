package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealMapper {
    Integer countsetMeal(Long id);

    Integer countByDishId(List<Long> dishids);
}
