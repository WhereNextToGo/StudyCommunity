package com.cs_liudi.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class redisTests {
    @Autowired
    private RedisTemplate<String,Object> template;

    @Test public void testStringValue(){
        String Stringkey = "test:count";
        template.opsForValue().set(Stringkey,1);

        System.out.println(template.opsForValue().get(Stringkey));
        System.out.println(template.opsForValue().increment(Stringkey));
        System.out.println(template.opsForValue().increment(Stringkey));
        System.out.println(template.opsForValue().decrement(Stringkey));

    }
    @Test public void testHashValue(){
        String hashKey = "test:user";
        template.opsForHash().put(hashKey,"id",1);
        template.opsForHash().put(hashKey,"username","zhangsan");
        System.out.println(template.opsForHash().get(hashKey,"id"));
        System.out.println(template.opsForHash().get(hashKey,"username"));
    }
    @Test public void testListValue(){
        String Listkey = "test:ids";
        template.opsForList().leftPush(Listkey,101);
        template.opsForList().leftPush(Listkey,102);
        template.opsForList().leftPush(Listkey,103);

        System.out.println(template.opsForList().index(Listkey,0));
        System.out.println(template.opsForList().range(Listkey,0,2));
        System.out.println(template.opsForList().size(Listkey));

    }
    @Test public void testSetValue(){
        String Setkey = "test:students";
        template.opsForSet().add(Setkey,"aaa","bbb","ccc","ddd","eee");

        System.out.println(template.opsForSet().size(Setkey));
        System.out.println(template.opsForSet().pop(Setkey));
        System.out.println(template.opsForSet().pop(Setkey));
        System.out.println(template.opsForSet().members(Setkey));

    }
    @Test
    public void testZsetValue(){
        String ZSetkey = "test:teachers";
        template.opsForZSet().add(ZSetkey,"aaa",100);
        template.opsForZSet().add(ZSetkey,"bbb",200);
        template.opsForZSet().add(ZSetkey,"ccc",300);
        template.opsForZSet().add(ZSetkey,"ddd",400);
        template.opsForZSet().add(ZSetkey,"eee",500);

        System.out.println(template.opsForZSet().zCard(ZSetkey));
        System.out.println(template.opsForZSet().score(ZSetkey,"ccc"));
        System.out.println(template.opsForZSet().rank(ZSetkey,"ccc"));
        System.out.println(template.opsForZSet().reverseRank(ZSetkey,"ccc"));
        System.out.println(template.opsForZSet().range(ZSetkey,0,2));

    }
    @Test
    public void testkeys(){
        template.delete("test:user");
        System.out.println(template.hasKey("test:user"));
        template.expire("test:ids",10, TimeUnit.SECONDS);
    }
    //多次访问同一个key
    @Test
    public void testKeyBound(){
        String redisKey = "test:count";
        BoundValueOperations bound = template.boundValueOps(redisKey);
        bound.increment();
        bound.increment();
        bound.increment();
        bound.increment();
        bound.increment();
        bound.increment();
        System.out.println(bound.get());
    }


    //事务
    @Test
    public void testRedisContractions(){
        Object o = template.execute(new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                operations.multi();
                operations.opsForSet().add(redisKey,"zhansan");
                operations.opsForSet().add(redisKey,"lisi");
                operations.opsForSet().add(redisKey,"wangwu");
                System.out.println(operations.opsForSet().members(redisKey));
                return operations.exec();
            }
        });
        System.out.println(o);
    }
}
