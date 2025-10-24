package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Set;


/**
 * SpringCache使用：
 * 1.pom文件中导入坐标：spring-boot-starter-cache
 * 2.启动类上添加注解：@EnableCaching //作用：开启缓存注解功能
 * 3.在目标方法上使用下列注解
 *
 * @Cacheable:作用--调用目标方法之前，先查询缓存，如果有缓存数据直接返回；否则调用目标方法，并且将返回的数据存入缓存
 * @Cacheput：作用--将目标方法的返回值，存入到缓存【一般不使用】
 * @CacheEvict：作用--清理缓存--可以清理一条也可以清理多条
 */


@Api(tags = "菜品相关接口")
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    private RedisTemplate redisTemplate;

    /** cacheNames|value:定义缓存的名称
     * key：键值
     * #result 取返回值作为key，如果result是对象，可以通过result.xx获取属性值
     * #参数名   取指定名字的参数值
     * #root.args[0] 取第一个参数的值
     * #p0/p1 取第一个参数的值/取第二个参数的值
     * #a0/a1 取第一个参数的值/取第二个参数的值
     * 最终生成的key等于 cacheNames::key
     */
    //@CachePut(cacheNames = "admin",key = "#result.id")
    @PostMapping
    public Result addDish(@RequestBody DishDTO dishdto) {
        log.info("新增菜品：{}", dishdto);
        dishService.addDish(dishdto);


        //缓存优化--清理缓存
        redisTemplate.delete("dish_" + dishdto.getCategoryId());

        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> pageDish(DishPageQueryDTO dishdto) {
        log.info("菜品套餐查询：{}", dishdto);
        PageResult pageResult = dishService.pageDish(dishdto);
        return Result.success(pageResult);
    }

    /**
     * @CacheEvict注解使用：
     *  allEntries属性，默认值为false,设置为true代表删除全部
     * @param ids
     * @return
     */
//    @CacheEvict(cacheNames = "admin" , allEntries = true)
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids) {
        log.info("删除菜品的ids:{}", ids);
        dishService.deleteDish(ids);

        // //缓存优化--清理缓存--将菜品缓存全部删除
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);


        return Result.success();
    }


    /**
     * @param id
     * @return
     * @Cacheable使用： 属性介绍：
     * cacheNames|value:定义缓存的名称
     * key：键值
     * #参数名   取指定名字的参数值
     * #root.args[0] 取第一个参数的值
     * #p0/p1 取第一个参数的值/取第二个参数的值
     * #a0/a1 取第一个参数的值/取第二个参数的值
     * 最终生成的key等于 cacheNames::key
     */

    //    @Cacheable(cacheNames = "admin" , key = "#id")
    //    @Cacheable(cacheNames = "user" , key = "#p0")
    //    @Cacheable(cacheNames = "user" , key = "#id")
    @GetMapping("/{id}")
    public Result<DishVO> getByID(@PathVariable Long id) {
        log.info("回显的菜品id:{}", id);
        DishVO dishVo = dishService.getById(id);
        return Result.success(dishVo);
    }

    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishdto) {
        log.info("修改的菜品：{}", dishdto);
        dishService.update(dishdto);

        //缓存优化--清理缓存--将菜品缓存全部删除
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);


        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result ableOrEnable(@PathVariable Integer status, Long id) {
        log.info("修改菜品的状态：{},{}", status, id);
        dishService.ableOrEnable(status, id);

        // 缓存优化--清理缓存--将菜品缓存全部删除
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);


        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> dishList(Long categoryId) {
        log.info("菜品id:{}", categoryId);
        List<Dish> dishs = dishService.dishlist(categoryId);
        return Result.success(dishs);
    }

}
