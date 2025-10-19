package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper {

    @AutoFill(OperationType.INSERT)
    void addDish(Dish dish);

    Integer countDish(Long id);

    Page<DishVO> list(DishPageQueryDTO dishdto);

    void deleteDish(List<Long> ids);

    Dish selectById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    void updatestatus(Integer status, Long id);
}
