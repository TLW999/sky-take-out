package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    @Transactional
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.addSetmeal(setmeal);

        log.info("setmeal_id:{}", setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.addSetMealDish(setmealDishes);
    }

    @Override
    public PageResult pageSetmeal(SetmealPageQueryDTO setmealdto) {
        //1.设置分页参数
        PageHelper.startPage(setmealdto.getPage(),setmealdto.getPageSize());
        //2.调用mapper查询
        Page<SetmealVO> page = setmealMapper.pageSetmeal(setmealdto);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        //查询套餐中是否有停售的菜品
         List<Dish> dishs = setmealMapper.selectNoSale(id);
        Setmeal setmeal = setmealMapper.selectid(id);
        if(setmeal.getStatus() == StatusConstant.DISABLE) {
            dishs.forEach(dish -> {
                if (dish.getStatus() == StatusConstant.DISABLE) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        setmealMapper.updateStatus(status,id);
    }

    @Transactional
    @Override
    public void deletesetmeal(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal =  setmealMapper.selectid(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        //删除套餐与菜品关系
        setmealDishMapper.deletemeals(ids);
        //删除套餐基本属性
        setmealMapper.deletemeal(ids);

    }

    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        Setmeal setmeal = setmealMapper.selectid(id);
        BeanUtils.copyProperties(setmeal, setmealVO);

        List<SetmealDish> setmealDish =  setmealDishMapper.selectid(id);
        setmealVO.setSetmealDishes(setmealDish);
        return setmealVO;
    }

    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        setmealDishMapper.delete(setmealDTO.getId());

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmeal.getId());
            });
        }
        setmealDishMapper.addSetMealDish(setmealDishes);
    }

}
