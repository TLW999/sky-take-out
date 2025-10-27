package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper {
    void insert(Orders orders);


    @Select("select * from orders where status = #{status} and order_time <  #{time} ")
    List<Orders> selectByStatusAndOrderTime(Integer status, LocalDateTime time);


    void update(Orders order);

    @Select("select sum(amount) from orders where status = #{status} and order_time between #{beginTime} and #{endTime} ")
    Double sum(Map map);

    Integer countByMap(Map map);

    List<GoodsSalesDTO> sumTop10(Map map);
}
