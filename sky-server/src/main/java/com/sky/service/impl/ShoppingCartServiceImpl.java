package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    @Override
    public void addCart(ShoppingCartDTO dto) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(dto, shoppingCart);

        //1。判断该商品是否已经存在购物车--条件：dishId+dishFlavor+userId
        //只查当前用户自己的购物车
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart cart = shoppingCartMapper.selectBy(shoppingCart);
        if (cart == null) { //代表购物车没有该商品
            //2.补充缺失的属性值
            //判断是新增套餐还是新增菜品
            if (dto.getDishId() != null) {//代表是新增菜品
                //根据菜品的id查询菜品表，获取菜品相关信息
                Dish dish = dishMapper.selectById(dto.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            } else { //代表新增的是套餐
                Setmeal selectid = setmealMapper.selectid(dto.getSetmealId());
                shoppingCart.setName(selectid.getName());
                shoppingCart.setImage(selectid.getImage());
                shoppingCart.setAmount(selectid.getPrice());
            }
            shoppingCart.setNumber(1);//数量--->到底是1还是加1？判断该商品是否已经存在购物车
            shoppingCart.setCreateTime(LocalDateTime.now());
            //3.将商品数据存入到shopping_cart表中
            shoppingCartMapper.insert(shoppingCart);

        } else {
            //4.将原来的购物车商品数量+1，调用mapper更新方法
            shoppingCart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(shoppingCart);
        }


    }

    @Override
    public List<ShoppingCart> list() {
        //注意：只能查看自己名下的购物车
        return shoppingCartMapper.list(BaseContext.getCurrentId());
    }

    @Override
    public void delete(ShoppingCartDTO dto) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(dto, shoppingCart);

        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart cart = shoppingCartMapper.selectBy(shoppingCart);

        shoppingCart.setNumber(cart.getNumber() - 1);
        shoppingCartMapper.update(shoppingCart);


    }
}
