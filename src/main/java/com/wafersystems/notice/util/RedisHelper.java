package com.wafersystems.notice.util;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Redis操作
 */
public class RedisHelper {
  private RedisHelper() {
    throw new IllegalStateException("Helper class");
  }

  /**
   * scan 实现
   *
   * @param pattern  表达式
   * @param consumer 对迭代到的key进行操作
   */
  public static void scan(RedisTemplate redis, String pattern, Consumer<byte[]> consumer) {
    redis.execute((RedisConnection connection) -> {
      try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
        cursor.forEachRemaining(consumer);
        return null;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * 获取符合条件的key
   *
   * @param pattern 表达式
   * @return
   */
  public static List<String> keys(RedisTemplate redis, String pattern) {
    List<String> keys = new ArrayList<>();
    RedisHelper.scan(redis, pattern, item -> {
      //符合条件的key
      String key = new String(item, StandardCharsets.UTF_8);
      keys.add(key);
    });
    return keys;
  }


  /**
   * 设置值
   */
  public static void set(RedisTemplate<String, String> redis, final String key, final String value) {
    redis.opsForValue().set(key, value);
  }

  /**
   * 设置值
   */
  public static void set(RedisTemplate<String, String> redis, final String key, final Object object) {
    set(redis, key, JSON.toJSONString(object));
  }

  /**
   * 设置值-并设置过期时间
   */
  public static void set(RedisTemplate<String, String> redis,
                         final String key,
                         final String value,
                         final Long timeout,
                         final TimeUnit unit) {
    redis.opsForValue().set(key, value, timeout, unit);
  }

  /**
   * 设置值-并设置过期时间
   */
  public static void set(RedisTemplate<String, String> redis,
                         final String key,
                         final Object object,
                         final Long timeout,
                         final TimeUnit unit) {
    set(redis, key, JSON.toJSONString(object), timeout, unit);
  }

  /**
   * 获取值
   */
  public static String get(RedisTemplate<String, String> redis, final String key) {
    return redis.opsForValue().get(key);
  }

  /**
   * 获取值
   */
  public static <T> T get(RedisTemplate<String, String> redis,
                          final String key, Class<T> clazz)
    throws InstantiationException, IllegalAccessException {
    String str = get(redis, key);
    if (!StringUtils.isEmpty(str)) {
      return JSON.parseObject(str, clazz);
    }
    return clazz.newInstance();
  }

  /**
   * 批量获取值
   *
   * @param redis
   * @param keys
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> List<T> multiGet(RedisTemplate<String, String> redis, final Collection keys, final Class<T> clazz) {
    List<String> list = redis.opsForValue().multiGet(keys);
    if (CollUtil.isNotEmpty(list)) {
      return list.stream().map(ss -> JSON.parseObject(ss, clazz)).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  /**
   * hash设置值
   *
   * @param redis
   * @param key
   * @param hkey
   * @param value
   */
  public static void hPut(RedisTemplate<String, String> redis,
                          final String key,
                          final String hkey,
                          final String value) {
    redis.opsForHash().put(key, hkey, value);
  }

  /**
   * hash设置值
   *
   * @param redis
   * @param key
   * @param hkey
   * @param object
   */
  public static void hPut(RedisTemplate<String, String> redis,
                          final String key,
                          final String hkey,
                          final Object object) {
    hPut(redis, key, hkey, JSON.toJSONString(object));
  }

  /**
   * hash 批量设置值
   *
   * @param redis
   * @param key
   * @param map
   */
  public static void hPutAll(RedisTemplate<String, String> redis, final String key, final Map<String, Object> map) {
    final Map<String, String> collect = map.entrySet().stream().collect(
      Collectors.toMap(
        Map.Entry::getKey,
        e -> {
          Object obj = e.getValue();
          if (obj instanceof String) {
            return (String) obj;
          }
          return JSON.toJSONString(obj);
        }));
    if (CollUtil.isNotEmpty(collect)) {
      redis.opsForHash().putAll(key, collect);
    }
  }

  /**
   * hash 获取值
   *
   * @param redis
   * @param key
   * @param hkey
   * @return String
   */
  public static String hGet(RedisTemplate<String, String> redis, final String key, final String hkey) {
    return (String) redis.opsForHash().get(key, hkey);
  }

  /**
   * hash 获取值
   *
   * @param redis
   * @param key
   * @param hkey
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T hGet(RedisTemplate<String, String> redis,
                           final String key,
                           final String hkey,
                           final Class<T> clazz) {
    return JSON.parseObject(hGet(redis, key, hkey), clazz);
  }

  /**
   * hash 批量获取值
   *
   * @param redis
   * @param key
   * @param hkeys
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> List<T> hMultiGet(RedisTemplate<String, String> redis,
                                      final String key,
                                      final Collection hkeys,
                                      final Class<T> clazz) {
    final List<String> list = redis.opsForHash().multiGet(key, hkeys);
    if (CollUtil.isNotEmpty(list)) {
      return list.stream().map(ss -> JSON.parseObject(ss, clazz)).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  /**
   * hash 获取所有values
   *
   * @param redis
   * @param key
   * @return
   */
  public static List<String> hValues(RedisTemplate redis, final String key) {
    return redis.opsForHash().values(key);
  }

  /**
   * hash 获取所有values
   *
   * @param redis
   * @param key
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> List<T> hValues(RedisTemplate redis, final String key, final Class<T> clazz) {
    List<String> values = redis.opsForHash().values(key);
    if (CollUtil.isNotEmpty(values)) {
      return values.stream().map(ss -> JSON.parseObject(ss, clazz)).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  /**
   * 删除
   */
  public static void delete(RedisTemplate<String, String> redis, String key) {
    redis.delete(key);
  }

  /**
   * 设置值,如果存在，则忽略并返回false
   */
  public static boolean setNX(RedisTemplate<String, String> redis, final String hkey, final String value) {
    return redis.opsForValue().setIfAbsent(hkey, value);
  }

  /**
   * 设置值-并设置过期时间,如果存在，则忽略并返回false
   */
  public static boolean setNX(RedisTemplate<String, String> redis,
                              final String hkey,
                              final String value,
                              final Integer timeout) {
    return setNX(redis, hkey, value, timeout, TimeUnit.SECONDS);
  }

  /**
   * 设置值-并设置过期时间,如果存在，则忽略并返回false
   */
  public static boolean setNX(RedisTemplate<String, String> redis,
                              final String hkey,
                              final String value,
                              final Integer timeout,
                              TimeUnit timeUnit) {
    return redis.execute(new SessionCallback<Boolean>() {
      @Override
      public <K, V> Boolean execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
        redisOperations.multi();
        redis.opsForValue().setIfAbsent(hkey, value);
        redis.expire(hkey, timeout, timeUnit);
        List<Object> exec = redisOperations.exec();
        if (!exec.isEmpty()) {
          return (Boolean) exec.get(0);
        }
        return false;
      }
    });
  }

  /**
   * 过期key
   */
  public static boolean expire(RedisTemplate<String, String> redis, final String key, Long timeout) {
    return redis.expire(key, timeout, TimeUnit.SECONDS);
  }

  /**
   * 过期key
   */
  public static boolean expire(RedisTemplate<String, String> redis, final String key) {
    return redis.expire(key, 0, TimeUnit.SECONDS);
  }

  /**
   * 验证是否过期
   */
  public static boolean isExpired(RedisTemplate<String, String> redis, final String key) {
    return redis.hasKey(key) && redis.getExpire(key) > 0;
  }

  /**
   * 获取唯一Id
   *
   * @param key
   * @param delta 增加量（不传采用1）
   * @return
   * @throws Exception
   */
  public static String incrementString(RedisTemplate<String, String> redis, String key, Long delta) {
    try {
      if (null == delta) {
        delta = 1L;
      }
      return redis.opsForValue().increment(key, delta).toString();
    } catch (Exception e) {
      //redis宕机时采用uuid的方式生成唯一id
      int first = new Random(10).nextInt(8) + 1;
      int randNo = UUID.randomUUID().hashCode();
      if (randNo < 0) {
        randNo = -randNo;
      }
      return first + String.format("%16d", randNo);
    }
  }

  /**
   * 自增值
   *
   * @param redis
   * @param key
   * @param delta 增加量（不传采用1）
   * @return
   */
  public static Long increment(RedisTemplate<String, String> redis, String key, Long delta) {
    try {
      if (null == delta) {
        delta = 1L;
      }
      return redis.opsForValue().increment(key, delta);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 自增值1
   *
   * @param redis
   * @param key
   * @return
   */
  public static Long increment(RedisTemplate<String, String> redis, String key) {
    return increment(redis, key, 1L);
  }

  /**
   * 返回集合中所有元素
   *
   * @return
   * @paramkey
   */
  public static Set<String> members(RedisTemplate<String, String> redis, String key) {
    return redis.opsForSet().members(key);
  }

  /**
   * 添加set元素
   *
   * @return
   * @paramkey
   * @paramvalues
   */
  public static Long addSet(RedisTemplate<String, String> redis, String key, String... values) {
    return redis.opsForSet().add(key, values);
  }

  /**
   * 判断set集合中是否有value
   *
   * @return
   * @paramkey
   * @paramvalue
   */
  public static boolean isMember(RedisTemplate<String, String> redis, String key, Object value) {
    return redis.opsForSet().isMember(key, value);
  }

  /**
   * 指定list从左入栈
   *
   * @paramkey
   * @return当前队列的长度
   */
  public static Long leftPush(RedisTemplate<String, String> redis, String key, String value) {
    return redis.opsForList().leftPush(key, value);
  }

  /**
   * 指定list从左出栈
   * 如果列表没有元素,会在100毫秒内返回空，或者获取到100毫秒内的新值
   *
   * @paramkey
   * @return出栈的值
   */
  public static String leftPop(RedisTemplate<String, String> redis, String key) {
    return redis.opsForList().leftPop(key, 100L, TimeUnit.MILLISECONDS);
  }

  /**
   * 指定list从左入栈
   *
   * @paramkey
   * @return当前队列的长度
   */
  public static Long leftPushAll(RedisTemplate<String, String> redis, String key, Collection<String> value) {
    return redis.opsForList().leftPushAll(key, value);
  }

  /**
   * 获取队列的长度
   *
   * @paramkey
   * @return出栈的值
   */
  public static Long listSize(RedisTemplate<String, String> redis, String key) {
    return redis.opsForList().size(key);
  }

}