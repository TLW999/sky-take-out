package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("DishUserController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(long categotyId){
        //缓存优化
        //查询MYSQL数据库前，先判断redis缓存中是否存在数据
        String key = "dish_" + categotyId;
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //如果有缓存数据，直接返回
        if(dishVOList == null && dishVOList.size() > 0){
            return Result.success(dishVOList);
        }
        //如果没有缓存数据，查询MySQL数据库
        Dish dish = new Dish();
        dish.setCategoryId(categotyId);
        dish.setStatus(StatusConstant.ENABLE); //查询起售中的菜品
        List<DishVO> list = dishService.listWithFlavor(dish);

        //将MYSQL数据库查寻到的数据，存入redis缓存中
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);

    }
}
