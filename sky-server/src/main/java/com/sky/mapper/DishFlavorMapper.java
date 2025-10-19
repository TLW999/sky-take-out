package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void addFlavor(List<DishFlavor> flavors);

    void deleteFlavor(List<Long> dishflavorIds);

    List<DishFlavor> selectById(Long DishId);

    void deleteByDishId(Long dishId);
}
