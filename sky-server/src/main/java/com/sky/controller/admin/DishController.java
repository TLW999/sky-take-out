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
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "菜品相关接口")
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public Result addDish(@RequestBody DishDTO dishdto) {
        log.info("新增菜品：{}", dishdto);
        dishService.addDish(dishdto);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> pageDish(DishPageQueryDTO dishdto) {
        log.info("菜品套餐查询：{}", dishdto);
        PageResult pageResult = dishService.pageDish(dishdto);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids) {
        log.info("删除菜品的ids:{}", ids);
        dishService.deleteDish(ids);
        return Result.success();
    }
    @GetMapping("/{id}")
    public Result<DishVO> getByID(@PathVariable Long id) {
        log.info("回显的菜品id:{}", id);
        DishVO dishVo =  dishService.getById(id);
        return Result.success(dishVo);
    }

    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishdto) {
        log.info("修改的菜品：{}", dishdto);
        dishService.update(dishdto);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result ableOrEnable(@PathVariable Integer status,Long id) {
        log.info("修改菜品的状态：{},{}",status,id);
        dishService.ableOrEnable(status,id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> dishList(Long categoryId) {
        log.info("菜品id:{}", categoryId);
        List<Dish> dishs = dishService.dishlist(categoryId);
        return Result.success(dishs);
    }

}
