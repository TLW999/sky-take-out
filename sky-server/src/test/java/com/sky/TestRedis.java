package com.sky;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate;

    //测试String类似操作
    @Test
    public void testString() {
        //1.获取ValueOperations对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //2.通过ValueOperations对象，操作string类型数据
        valueOperations.set("name","ikunkun"); //对应的是set命令
        System.out.println(valueOperations.get("name")); //对应的是get命令

        //需求：存储一个验证码，并且设置过期时间为10s
        valueOperations.set("code","1234",10, TimeUnit.SECONDS);
        System.out.println(valueOperations.get("code"));
    }

    @Test
    public void testZSet(){
        //1.获取ZSetOperations对象
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        zSetOperations.add("zset01","zhangshan",88);
        zSetOperations.add("zset01","lisi",77);
        zSetOperations.add("zset01","wangwu",99);

        Set zset1 = zSetOperations.range("zset01", 0, -1);
        System.out.println(zset1);

        Set zset2 = zSetOperations.rangeWithScores("zset01", 0, -1);
        System.out.println(zset2);


    }
}
