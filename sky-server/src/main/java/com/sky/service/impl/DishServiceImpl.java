package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional //开启事务（涉及到多张表的增删改查操作需要开事务）
    public void addDish(DishDTO dishDTO) {
        //1.构造菜品基本信息数据，将其存入dish表中
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.addDish(dish);
        log.info("菜品Id:{}",dish.getId());
        //2.构造菜品口味列表数据，将其存入dish_flavor表中
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
            dishFlavorMapper.addFlavor(flavors);
        }

    }

    @Override
    public PageResult pageDish(DishPageQueryDTO dishdto) {
        //设置分页参数
        PageHelper.startPage(dishdto.getPage(),dishdto.getPageSize());
        //调用mapper查询
        Page<DishVO> page = dishMapper.list(dishdto);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteDish(List<Long> ids) {
        //1.删除菜品前，需要判断菜品是否起售，起售中不允许删除
        ids.forEach(id -> {
            Dish dish = dishMapper.selectById(id);
            log.info("状态:{}",dish.getStatus());
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        //2.需要判断菜品是否被套餐关联，关联了不允许删除
        Integer count = setmealMapper.countByDishId(ids);
        if(count > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3.删除菜品基本信息
        dishMapper.deleteDish(ids);
        //4.删除菜品口味
        dishFlavorMapper.deleteFlavor(ids);
    }

    /**
     * 菜品回显
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = new DishVO();
        //1.调用mapper获取菜品基本信息
        Dish dish = dishMapper.selectById(id);
        BeanUtils.copyProperties(dish, dishVO);
        //2.调用mapper获取菜品口味
        List<DishFlavor> flavors = dishFlavorMapper.selectById(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Transactional
    @Override
    public void update(DishDTO dishdto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishdto, dish);
        //1.修改菜品的基本信息
        dishMapper.update(dish);
        //2.修改菜品口味
        //由于口味数据可能增加、可能删除、还可能修改口味的值，涉及到增删改查操作，所以先全部删除旧数据，再添加新数据
        dishFlavorMapper.deleteByDishId(dishdto.getId());

        List<DishFlavor> flavors = dishdto.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {
                flavor.setDishId(dish.getId());
            });
            dishFlavorMapper.addFlavor(flavors);
        }
    }

    @Override
    public void ableOrEnable(Integer status, Long id) {
        dishMapper.updatestatus(status, id);
    }

    @Override
    public List<Dish> dishlist(Long categoryId) {
        return dishMapper.listdish(categoryId);
    }

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> listdish = dishMapper.listdish(dish.getId());
        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish d : listdish) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);
            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectById(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }
}
