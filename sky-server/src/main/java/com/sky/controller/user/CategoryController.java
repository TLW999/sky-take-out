package com.sky.controller.user;


import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.ResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("CategoryUserController")
@RequestMapping("/user/category")
@Api(tags = "C端-分类接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result list(Integer Type) {
        log.info("条件查询");
        categoryService.typeSelect(Type);
        return Result.success();
    }

}
