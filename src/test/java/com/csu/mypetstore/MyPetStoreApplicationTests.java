package com.csu.mypetstore;

import com.csu.mypetstore.domain.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class MyPetStoreApplicationTests {


    @Resource
    RedisTemplate<String, Cart> redisTemplate;

    @Test
    void contextLoads() {

    }

    @Test
    void test1() {

    }

}
