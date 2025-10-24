package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    Integer countsetMeal(Long id);

    Integer countByDishId(List<Long> dishids);

    @AutoFill(OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    Page<SetmealVO> pageSetmeal(SetmealPageQueryDTO setmealdto);

    void updateStatus(Integer status, Long id);

    Setmeal selectid(Long id);

    List<Dish> selectNoSale(Long id);

    void deletemeal(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);


    @Select("select sd.name,sd.copies,d.image,d.description" +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id" +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    List<Setmeal> list(Setmeal setmeal);
}
