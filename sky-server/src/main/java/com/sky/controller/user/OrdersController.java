package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@Slf4j
@Api(tags = "订单状态接口")
@RestController
@RequestMapping("/user/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("submit")
    public Result submit(@RequestBody OrdersSubmitDTO dto) {
        log.info("用户下单：{}",dto);
        OrderSubmitVO vo= ordersService.submit(dto);
        return Result.success(vo);
    }
}
