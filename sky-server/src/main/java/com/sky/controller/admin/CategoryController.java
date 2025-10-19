package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "套餐相关接口")
@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param categoryDTO
     * @return
     */
    @PostMapping
    public Result setCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增套餐：{}", categoryDTO);
        categoryService.setCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("套餐分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    @ApiOperation("修改套餐")
    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改套餐属性：{}", categoryDTO);
        categoryService.updatecategory(categoryDTO);
        return Result.success();
    }

    @ApiOperation("修改套餐状态")
    @PostMapping("/status/{status}")
    public Result ableOrenable(@PathVariable Integer status,Long id) {
        log.info("修改套餐状态：{}，{}", status,id);
        categoryService.ableOrenable(status,id);
        return Result.success();
    }

    @ApiOperation("删除套餐")
    @DeleteMapping
    public Result deleteCategory(Long id) {
        log.info("删除的套餐id:{}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Category>> typeSelect(Integer type) {
        log.info("套餐type:{}", type);
        List<Category> category = categoryService.typeSelect(type);
        return Result.success(category);
    }
}
