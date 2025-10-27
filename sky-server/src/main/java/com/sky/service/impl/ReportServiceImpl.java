package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //1.准备日期列表数据dataList
        List<LocalDate> dataList = getDateList(begin, end);

        //2.准备营业额列表数据turnoverList
        List<Double> turnoverList = new ArrayList<>();

        dataList.forEach(date -> {
            Map map = new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("beginTime",LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime",LocalDateTime.of(date, LocalTime.MAX));
            Double turnover =  ordersMapper.sum(map);
            turnover = turnover ==null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });
        //3.构造TurnoverStatistic对象并返回
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dataList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //1.准备日期列表数据dataList
        List<LocalDate> dataList = getDateList(begin, end);

        //2.构造newUserList数据，新增用户列表
        List<Integer> newUserList = new ArrayList<>();
        //3.totalUserList数据，总用户列表
        List<Integer> totalUserList = new ArrayList<>();

        //循环遍历日期列表统计每日的新增用户列表---user
        dataList.forEach(date -> {
            Map map = new HashMap();
            map.put("beginTime",LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime",LocalDateTime.of(date, LocalTime.MAX));
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);

            map.put("beginTime",null);
            map.put("endTime",LocalDateTime.of(date, LocalTime.MAX));
            Integer tatalUserList = userMapper.countByMap(map);
            totalUserList.add(tatalUserList);
        });

        return UserReportVO.builder()
                .dateList(StringUtils.join(dataList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        //1.准备日期列表数据dataList
        List<LocalDate> dataList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer totalValidOrderCount = 0;
        for (LocalDate localDate : dataList) {
            //2.获取每日总订单列表数orderCountList
            Map map = new HashMap();
            map.put("beginTime",LocalDateTime.of(localDate, LocalTime.MIN));
            map.put("endTime",LocalDateTime.of(localDate, LocalTime.MAX));
            Integer ordercoubtList = ordersMapper.countByMap(map);
            orderCountList.add(ordercoubtList);
            //3.获取每日有效订单列表数 validOrderCountList
            map.put("status", Orders.COMPLETED);
            Integer validordercoubtList = ordersMapper.countByMap(map);
            validOrderCountList.add(validordercoubtList);
            //4.获取订单总数 totalOrderCount
            totalOrderCount += ordercoubtList;
            //5.获取有效订单数 validOrderCount
            totalValidOrderCount += validordercoubtList;
        }
        //6.计算完成率 orderCompletionRate
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = (totalValidOrderCount + 0.0) / totalOrderCount;
        }
        //7.封装OrderReportVO对象并返回
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dataList,","))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderCount)
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .build();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        //1.构造nameList,商品名称列表
        List<String> nameList = new ArrayList<>();

        //2.构造numberList,商品销量（份数）列表
        List salesList = new ArrayList<>();

        //查询订单明细表order_detail + 订单表orders,条件：订单状态--已完成，下单时间
        Map map = new HashMap();
        map.put("status", Orders.COMPLETED);
        map.put("beginTime",LocalDateTime.of(begin, LocalTime.MIN));
        map.put("endTime",LocalDateTime.of(end,LocalTime.MAX));
        List<GoodsSalesDTO> list = ordersMapper.sumTop10(map);

        for (GoodsSalesDTO dto : list) {
            nameList.add(dto.getName());
            salesList.add(dto.getNumber());
        }

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(salesList,","))
                .build();
    }


    public List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dataList = new ArrayList();
        while (begin.isBefore(end)) {
            dataList.add(begin);
            begin = begin.plusDays(1);
        }
        return dataList;
    }
}
