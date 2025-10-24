package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrdersService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());
        if(addressBook == null){
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        User user =  userMapper.getById(userId);
        if(user == null){
            throw new OrderBusinessException(MessageConstant.USER_NOT_LOGIN);
        }
        //查询购物车列表数据--只查询自己名下的购物车数据
        List<ShoppingCart> cartList = shoppingCartMapper.list(userId);
        if(cartList == null || cartList.isEmpty()){
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //1，构造订单数据，参入orders表中
        Orders orders = new Orders();

        //拷贝属性值
        BeanUtils.copyProperties(dto, orders);
        //补充缺失的属性值
        orders.setNumber(System.currentTimeMillis()+"");  //订单编号
        orders.setStatus(Orders.PENDING_PAYMENT);         //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
        orders.setUserId(userId);                         //下单人id
        orders.setOrderTime(LocalDateTime.now());           //下单时间
        orders.setPayStatus(Orders.UN_PAID);                //支付状态 0 未支付 1已支付 2退款
        orders.setPhone(addressBook.getPhone());            //收货人手机号
        orders.setAddress(addressBook.getDetail());         //收获地址
        orders.setConsignee(addressBook.getConsignee());     //收货人
        orders.setUserName(user.getName());                 //下单人姓名
        ordersMapper.insert(orders);

        //2.构造订单明细数据，存入order_detail表中
        List<OrderDetail> orderDetailList = new ArrayList<>();
        cartList.forEach(cart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail,"id");
            //关联订单id
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        });
        //批量插入订单明细数据
        orderDetailMapper.insert(orderDetailList);

        //3.清空购物车
        shoppingCartMapper.clean(userId);
        //4.构造OrderSubmitVO对象，并返回
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }
}
