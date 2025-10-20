package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    void addSetMealDish(List<SetmealDish> setmealDishes);

    void deletemeals(List<Long> mealids);

    List<SetmealDish> selectid(Long id);

    void delete(Long id);
}
