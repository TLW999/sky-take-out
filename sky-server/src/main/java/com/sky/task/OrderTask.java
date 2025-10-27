package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;
    /**
     * 每分钟检查一次是否存在超时未支付订单（）
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processOutTimeOrder() {

        //1.查询数据库orders表，条件：状态-待付款，下单时间 < 当前时间 - 15分钟
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> orderList =  ordersMapper.selectByStatusAndOrderTime(Orders.PENDING_PAYMENT,time);

        //2.如果查询到了数据，代表存在超时未完成订单，需要修改订单的状态为“status = 6”
        if(orderList != null && orderList.size() > 0) {
            orderList.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                ordersMapper.update(order);
            });
        }
    }



    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        //1.查询数据库orders表，条件：状态-派送中，下单时间 < 当前时间 - 1小时
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> orderList =  ordersMapper.selectByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS,time);

        //2.如果查询到了数据，代表存在派送中的订单，需要修改订单的状态为“status = 5”
        if(orderList != null && orderList.size() > 0) {
            orderList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                order.setDeliveryTime(time);
                ordersMapper.update(order);
            });
        }
    }
}
