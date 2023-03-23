package com.csu.mypetstore.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
* @Description: redis工具类
* @Author: LZY
* @Date: 2021/4/16 10:49
*/
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> template;

    /*
    * @Description: 设置缓存失效时间
    * @Author: LZY
    * @Date: 2021/4/16 10:50
    * @Params:
    * @Return:
    */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                template.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    * @Description: 获取过期时间
    * @Author: LZY
    * @Date: 2021/4/16 10:52
    * @Params: key值，不能为null
    * @Return: 时间（秒），返回0表示永久有效
    */
    public long getExpire(String key) {
        return template.getExpire(key, TimeUnit.SECONDS);
    }

    /*
    * @Description: 判断key是否存在
    * @Author: LZY
    * @Date: 2021/4/16 10:54
    */
    public boolean hasKey(String key){
        try {
            return template.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    * @Description: 删除redis中的key值
    * @Author: LZY
    * @Date: 2021/4/16 10:55
    * @Params: 可以传递多个key
    * @Return:
    */
    public void deleteKey(String... key){
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                template.delete(key[0]);
            } else {
                template.delete(Arrays.asList(key));
            }
        }
    }

    /*
    * @Description: 普通缓存获取
    * @Author: LZY
    * @Date: 2021/4/16 11:08
    * @Params:
    * @Return:
    */
    public Object get(String key) {
        return key == null ? null : template.opsForValue().get(key);
    }

    /*
    * @Description: 普通缓存放入
    * @Author: LZY
    * @Date: 2021/4/16 11:09
    * @Params:
    * @Return:
    */
    public boolean set(String key, Object value) {
        try {
            template.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    * @Description: 普通缓存放入并设置时间
    * @Author: LZY
    * @Date: 2021/4/16 11:11
    * @Params: key 键, value 值, time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
    * @Return: true成功 false 失败
    */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                template.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    * @Description:
    * @Author: LZY
    * @Date: 2021/4/16 11:11
    * @Params: [key, delta]  key 键,delta 要增加几(大于0)
    * @Return: long
    */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return template.opsForValue().increment(key, delta);
    }

    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return template.opsForValue().increment(key, -delta);
    }

    /*
    * @Description: HashGet
    * @Author: LZY
    * @Date: 2021/4/16 11:14
    */
    public Object hget(String key, String item) {
        return template.opsForHash().get(key, item);
    }

    /*
    * @Description:获取hashKey对应的所有键值
    * @Author: LZY
    * @Date: 2021/4/16 11:15
    * @Params:
    * @Return:
    */
    public Map<Object, Object> hmget(String key) {
        return template.opsForHash().entries(key);
    }

    /*
    * @Description: HashSet
    * @Author: LZY
    * @Date: 2021/4/16 11:15
    * @Params: [key, map] key 键, map 对应多个键值
    * @Return: boolean, true 成功 false 失败
    */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            template.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    * @Description: HashSet 并设置时间
    * @Author: LZY
    * @Date: 2021/4/16 11:16
    */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            template.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
