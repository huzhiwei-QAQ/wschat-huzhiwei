package cn.molu.app.utils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

/**
 * redis工具类
 *
 * @author 陌路
 * @Description
 * @date 2022-05-02 下午1:53:20
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /******************************* String *******************************/

    /**
     * @param Duration.ofDays(0);     天
     * @param Duration.ofHours(0L);   小时
     * @param Duration.ofMinutes(0L); 分钟
     * @param Duration.ofSeconds(0L); 秒
     * @param Duration.ofMillis(0L);  毫秒
     * @title 存储为JSON类型，并设置失效时间
     */
    public void setJsonObj(String key, Object obj, Duration timeout) {
        String jsonStr = ObjectUtils.toJSON(obj);
        if (ObjectUtils.isEmpty(timeout)) {
            this.redisTemplate.opsForValue().set(key, jsonStr);
        } else {
            this.redisTemplate.opsForValue().set(key, jsonStr, timeout);
        }
    }

    /**
     * @param Duration.ofDays(0);     天
     * @param Duration.ofHours(0L);   小时
     * @param Duration.ofMinutes(0L); 分钟
     * @param Duration.ofSeconds(0L); 秒
     * @param Duration.ofMillis(0L);  毫秒
     * @title 存储为对象类型，并设置失效时间
     */
    public void setObj(String key, Object obj, Duration timeout) {
        if (ObjectUtils.isEmpty(timeout)) {
            this.redisTemplate.opsForValue().set(key, obj);
        } else {
            this.redisTemplate.opsForValue().set(key, obj, timeout);
        }
    }

    /**
     * @param Duration.ofDays(0);     天
     * @param Duration.ofHours(0L);   小时
     * @param Duration.ofMinutes(0L); 分钟
     * @param Duration.ofSeconds(0L); 秒
     * @param Duration.ofMillis(0L);  毫秒
     * @title 存储为Long类型，并设置失效时间
     */
    public void setLong(String key, Long num, Duration timeout) {
        this.redisTemplate.opsForValue().set(key, num, timeout);
    }

    /**
     * @param Duration.ofDays(0);     天
     * @param Duration.ofHours(0L);   小时
     * @param Duration.ofMinutes(0L); 分钟
     * @param Duration.ofSeconds(0L); 秒
     * @param Duration.ofMillis(0L);  毫秒
     * @title 存储为String类型，并设置失效时间
     */
    public void setStr(String key, String val, Duration timeout) {
        this.redisTemplate.opsForValue().set(key, val, timeout);
    }

    /**
     * @param map.put("name", "zs"); map.put("age", 18); // key - val
     * @title 存储多条数据
     */
    public void setMap(Map<String, Object> map) {
        this.redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * @param keys.add("name"); keys.add("age"); // 需要获取的key
     * @title 获取多条数据
     */
    public List<Object> getMap(Collection<String> keys) {
        List<Object> multiGet = this.redisTemplate.opsForValue().multiGet(keys);
        return multiGet;
    }

    /**
     * @title 存储为Long类型
     */
    public void setLong(String key, Long num) {
        setLong(key, num, null);
    }

    /**
     * @title 存储为对象类型
     */
    public void setObj(String key, Object obj) {
        setObj(key, obj, null);
    }

    /**
     * @title 存储为JSON类型
     */
    public void setJsonObj(String key, Object obj) {
        setJsonObj(key, obj, null);
    }

    /**
     * @title 存储为String类型
     */
    public void setStr(String key, String val) {
        setJsonObj(key, val);
    }

    /**
     * @title 根据key获取String
     */
    public String getStr(String key) {
        return ObjectUtils.getStr(this.redisTemplate.opsForValue().get(key));
    }

    /**
     * @title 根据key获取JSON
     */
    public String getJson(String key) {
        return getStr(key);
    }

    /**
     * @title 根据key获取Object对象
     */
    public Object getObj(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    /**
     * @title 根据key删除数据
     */
    public boolean remove(String key) {
        if (ObjectUtils.isEmpty(key)) {
            return false;
        }
        return this.redisTemplate.delete(key);
    }

    /**
     * @title 删除多条缓存数据
     */
    public boolean remove(Collection<String> keys) {
        Long del = this.redisTemplate.delete(keys);
        if (del > 0) {
            return true;
        }
        return false;
    }

    /**
     * @title 删除多条缓存数据
     */
    public boolean remove(String... keys) {
        if (ObjectUtils.checkArr(keys)) {
            return false;
        }
        Set<String> set = new HashSet<String>(Arrays.asList(keys));
        return remove(set);
    }

    /******************************* List *******************************/

    /**
     * @title list->左添加
     */
    public void lPush(String key, Object val) {
        this.redisTemplate.opsForList().leftPush(key, val);
    }

    /**
     * @return 添加条数
     * @title list->左添加，添加多条数据
     */
    public Long lPushAll(String key, Object... val) {
        Long count = this.redisTemplate.opsForList().leftPushAll(key, val);
        if (ObjectUtils.isBlank(count)) {
            return 0L;
        }
        return count;
    }

    /**
     * @return 添加条数
     * @title list->左添加，将newVal添加到existVal的左边
     * @Desc 左添加 第三个参数会被添加到第二个参数的左边
     */
    public Long lPush(String key, Object existVal, Object newVal) {
        Long count = this.redisTemplate.opsForList().leftPush(key, existVal, newVal);
        return count;
    }

    /**
     * @title list->右添加
     */
    public void rPush(String key, Object val) {
        this.redisTemplate.opsForList().rightPush(key, val);
    }

    /**
     * @return 添加条数
     * @title list->右添加，添加多条数据
     */
    public Long rPushAll(String key, Object... val) {
        Long count = this.redisTemplate.opsForList().rightPushAll(key, val);
        if (ObjectUtils.isBlank(count)) {
            return 0L;
        }
        return count;
    }

    /**
     * @return 添加条数
     * @title list->右添加，将newVal添加到existVal的右边
     * @Desc 右添加 第三个参数会被添加到第二个参数的右边
     */
    public Long rPush(String key, Object existVal, Object newVal) {
        Long count = this.redisTemplate.opsForList().rightPush(key, existVal, newVal);
        return count;
    }

    /**
     * @return 获取到的数据集合
     * @title list->获取指定索引位置的数据
     * @Desc redis的key，开始索引，结束索引
     */
    public List<Object> getList(String key, long startIndex, long endIndex) {
        List<Object> list = this.redisTemplate.opsForList().range(key, startIndex, endIndex);
        return list;
    }

    /**
     * @return 获取到的数据集合
     * @title list->获取List中的所有数据
     */
    public List<Object> getListAll(String key) {
        List<Object> list = this.redisTemplate.opsForList().range(key, 0, -1);
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T> T getListAlls(String key) {
        return (T) this.redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * @return 数据条数
     * @title list->获取List中的数据条数
     */
    public Long getListSize(String key) {
        Long size = this.redisTemplate.opsForList().size(key);
        return ObjectUtils.isBlank(size) ? 0L : size;
    }

    /**
     * @param redis的key，count删除的条数，val删除的值
     * @return 删除的数据条数
     * @title list->删除List中的数据
     */
    public Long removeList(String key, Long count, Object val) {
        Long rem = this.redisTemplate.opsForList().remove(key, count, val);
        return ObjectUtils.isBlank(rem) ? 0L : rem;
    }

    /**
     * @param redis的key，val删除的值，默认只删除一条
     * @return 删除成功返回true，删除失败返回false
     * @title list->删除List中的数据
     */
    public boolean removeList(String key, Object val) {
        Long rem = this.redisTemplate.opsForList().remove(key, 1, val);
        return ObjectUtils.isBlank(rem);
    }

    /**
     * @param redis的key
     * @return 返回值为删除的值
     * @title list->删除左边的第一条数据
     */
    public Object lPop(String key) {
        Object obj = this.redisTemplate.opsForList().leftPop(key);
        return obj;
    }

    /**
     * @param redis的key
     * @return 返回值为删除的值
     * @title list->删除右边的第一条数据
     */
    public Object rPop(String key) {
        Object obj = this.redisTemplate.opsForList().rightPop(key);
        return obj;
    }

    /******************************* Set *******************************/

    /**
     * @param redis的key，数组
     * @return 返回值为添加的条数
     * @title set->添加数据
     * @Desc 数据类型 ("redisKey", "val1", "val2", "val3"...)
     */
    public Long sAdd(String key, Object[] obj) {
        if (ObjectUtils.isBlank(obj)) {
            return 0L;
        }
        return sAdds(key, obj);
    }

    /**
     * @param redis的key，数据值...
     * @return 返回值为添加的条数
     * @title set->多数据添加数据
     * @Desc 数据类型 ("redisKey", "val1", "val2", "val3"...)
     */
    public Long sAdds(String key, Object... obj) {
        if (ObjectUtils.isBlank(obj)) {
            return 0L;
        }
        Long count = this.redisTemplate.opsForSet().add(key, obj);
        return count;
    }

    /**
     * @param redis的key
     * @return 返回Set集合
     * @title set->获取数据
     * @Desc 数据类型 ("redisKey", "val1", "val2", "val3"...)
     */
    public Set<Object> getSet(String key) {
        if (ObjectUtils.isBlank(key)) {
            return new HashSet<Object>();
        }
        Set<Object> set = this.redisTemplate.opsForSet().members(key);
        return set;
    }

    /**
     * @param redis的key
     * @return 返回Set集合的数据条数
     * @title set->获取set集合中的数据条数
     * @Desc 数据类型 ("redisKey", "val1", "val2", "val3"...)
     */
    public Long getSetSize(String key) {
        if (ObjectUtils.isBlank(key)) {
            return 0L;
        }
        Long size = this.redisTemplate.opsForSet().size(key);
        return ObjectUtils.isBlank(size) ? 0L : size;
    }

    /**
     * @param redis的key
     * @return 返回删除的条数
     * @title set->删除set集合中的数据
     */
    public Long removeSet(String key, Object... obj) {
        if (ObjectUtils.isBlank(obj)) {
            return 0L;
        }
        Long rem = this.redisTemplate.opsForSet().remove(key, obj);
        return ObjectUtils.isBlank(rem) ? 0L : rem;
    }

    /******************************* ZSet *******************************/

    /**
     * @param redis的key
     * @return 成功返回true，失败返回false
     * @title ZSet->添加ZSet数据
     */
    public boolean addZSet(String key, Object obj, double val) {
        if (ObjectUtils.isBlank(key)) {
            return false;
        }
        return this.redisTemplate.opsForZSet().add(key, obj, val);
    }

    /**
     * @param redis的key
     * @return 成功返回添加的条数
     * @title ZSet->添加ZSet数据
     */
    public Long addZSet(String key, Map<String, Double> map) {
        if (ObjectUtils.isBlank(map)) {
            return 0L;
        }
        Set<ZSetOperations.TypedTuple<Object>> tupleSet = new HashSet<>();
        for (String k : map.keySet()) {
            tupleSet.add(new DefaultTypedTuple<>(k, map.get(k)));
        }
        Long count = this.redisTemplate.opsForZSet().add(key, tupleSet);
        return ObjectUtils.isBlank(count) ? 0L : count;
    }

    /**
     * @param redis的key，开始索引，结束索引
     * @return 获取到的数据集合
     * @title ZSet->获取指定的ZSet数据
     */
    public Set<Object> getZSet(String key, long startIndex, long endIndex) {
        if (ObjectUtils.isBlank(key)) {
            return new HashSet<Object>();
        }
        Set<Object> set = this.redisTemplate.opsForZSet().range(key, startIndex, endIndex);
        return set;
    }

    /**
     * @param redis的key
     * @return 获取到的数据集合
     * @title ZSet->获取所有ZSet数据
     */
    public Set<Object> getZSetAll(String key) {
        if (ObjectUtils.isBlank(key)) {
            return new HashSet<Object>();
        }
        Set<Object> set = getZSet(key, 0, -1);
        return set;
    }

    /**
     * @return 成功返回删除的条数
     * @title ZSet->删除数据
     */
    public Long removeZSet(String key, Object[] obj) {
        if (ObjectUtils.isBlank(obj)) {
            return 0L;
        }
        Long rem = this.redisTemplate.opsForZSet().remove(key, obj);
        return ObjectUtils.isBlank(rem) ? 0L : rem;
    }

    /**
     * @param remove("key", "val1", "val2");
     * @return 成功返回删除的条数
     * @title ZSet->删除数据
     */
    public Long removeZSets(String key, Object... obj) {
        if (ObjectUtils.isBlank(obj)) {
            return 0L;
        }
        return removeZSet(key, obj);
    }

    /******************************* Expire *******************************/

    /**
     * @param TimeUnit.DAYS;         天
     * @param TimeUnit.HOURS;        小时
     * @param TimeUnit.MINUTES;      分钟
     * @param TimeUnit.SECONDS;      秒
     * @param TimeUnit.MILLISECONDS; 毫秒
     * @title 给指定的缓存数据添加失效时间
     */
    public void setExpire(String key, Long timeout, TimeUnit unit) {
        this.redisTemplate.expire(key, timeout, unit);
    }

    /**
     * @param TimeUnit.DAYS;         天
     * @param TimeUnit.HOURS;        小时
     * @param TimeUnit.MINUTES;      分钟
     * @param TimeUnit.SECONDS;      秒
     * @param TimeUnit.MILLISECONDS; 毫秒
     * @title 给指定的缓存数据添加失效时间
     */
    public void setTimeout(String key, Long timeout, TimeUnit unit) {
        setExpire(key, timeout, unit);
    }

    /**
     * @param redis的key
     * @return 失效时长
     * @title 获取失效时间
     */
    public Long getExpire(String key) {
        if (ObjectUtils.isBlank(key)) {
            return 0L;
        }
        Long timeout = this.redisTemplate.getExpire(key);
        return ObjectUtils.isBlank(timeout) ? 0L : timeout;
    }

    /**
     * @param TimeUnit.DAYS;         天
     * @param TimeUnit.HOURS;        小时
     * @param TimeUnit.MINUTES;      分钟
     * @param TimeUnit.SECONDS;      秒
     * @param TimeUnit.MILLISECONDS; 毫秒
     * @title 获取指定单位的失效时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        if (ObjectUtils.isBlank(key)) {
            return 0L;
        }
        Long timeout = this.redisTemplate.getExpire(key, unit);
        return ObjectUtils.isBlank(timeout) ? 0L : timeout;
    }

    /******************************* Key *******************************/
    /**
     * @return Boolean
     * @Title 判断redis中是否包含这个Key
     */
    public Boolean isExists(String key) {
        if (StringUtils.isNotBlank(key)) {
            return this.redisTemplate.hasKey(key);
        }
        return false;
    }

    /******************************* END *******************************/

    /**
     * @title 获取JSON转为对象后的数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getJsonToObj(String key, Class<T> obj) {
        String jsonStr = ObjectUtils.getStr(this.redisTemplate.opsForValue().get(key));
        if (ObjectUtils.isNotEmpty(jsonStr)) {
            return ObjectUtils.toObj(jsonStr, obj);
        }
        return (T) obj.getClass();
    }

}
