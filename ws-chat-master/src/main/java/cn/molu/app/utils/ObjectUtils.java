package cn.molu.app.utils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;

import cn.molu.app.vo.R;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author 陌路
 * @title 对象工具类封装
 * @Description
 * @date 2022-04-28 下午7:38:23
 */
public class ObjectUtils {
    private final static Logger log = LogManager.getLogger(ObjectUtils.class);

    /**
     * @title 获取字符串
     * @Desc 为空则返回空字符串
     */
    public static String getStr(Object obj) {
        return Objects.nonNull(obj) ? String.valueOf(obj).trim() : "";
    }

    /**
     * @Title 获取对象数据
     * @Desc 为空则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObj(Object obj) {
        return Objects.nonNull(obj) ? (T) JSONObject.toJSON(obj) : null;
    }

    /**
     * @param obj 字符串、对象、数组、集合、基本类型
     * @return 为空时返回true，否则返回false
     * @title 严格校验, 值为""或者null认为是空
     * @Desc 数组、集合中，若值为（null、""）则认为是空
     */
    public static boolean isBlank(Object obj) {
		if (null == obj) {
			return true;
		} else if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		} else if (obj instanceof Object[]) {
			return Arrays.asList((Object[]) obj).isEmpty();
		} else if (obj instanceof Collection) {
			return ((Collection<?>) obj).isEmpty();
		} else {
			return obj instanceof Map && ((Map<?, ?>) obj).isEmpty();
		}
    }

    /**
     * @param obj 数组校验，为空返回true
     * @return 为空时返回true，否则返回false
     * @title 严格校验，（null、""）则认为是空
     * @Desc 校验数组是否为空，若值为（""）也认为是空
     */
    public static boolean checkArr(Object[] obj) {
        if (obj == null) {
            return true;
        }
        if (obj.length < 1) {
            return true;
        }
        int i = 0;
        for (Object o : obj) {
            if (StringUtils.isBlank(String.valueOf(o).trim())) {
                ++i;
            }
        }
        return i == obj.length;
    }

    /**
     * @param obj 严格校验，（null、""）则认为是空
     * @return 为空时返回true，否则返回false
     * @title 对象校验，为空返回true
     * @Desc 数组、集合的值为（""）时则认为是空
     */
    public static boolean isEmpty(Object obj) {
        return isBlank(obj);
    }

    /**
     * @param obj （null、""），有一个为空则返回true
     * @return 为空时返回true, 否则返回false
     * @title 多数据校验，为空返回true
     * @Desc 数组、集合中，若值为（null、""）则认为是空
     */
    @SuppressWarnings("unused")
    public static boolean isEmpty(Object... obj) {
        if (obj == null) {
            return true;
        }
        for (Object o : obj) {
            if (isBlank(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return 对象不为空时返回true
     * @title 对象校验
     */
    public static boolean isNotEmpty(Object obj) {
        return !isBlank(obj);
    }

    /**
     * @title JSON转Map集合
     */
    @SuppressWarnings("unused")
    private static Map<?, ?> jsonToMap(String jsonStr) {
        if (isBlank(jsonStr)) {
            return new HashMap<String, Object>(2);
        }
        return JSONObject.parseObject(jsonStr, Map.class);
    }

    /**
     * @param map 集合
     * @param obj 对象
     * @title Map集合转为对象
     */
    @SuppressWarnings("unused")
    public static <T> T mapToObj(Map<String, Object> map, Class<T> obj) {
        if (!isBlank(map)) {
            String jsonString = toJSON(map);
            return JSONObject.parseObject(jsonString, obj);
        }
        return null;
    }

    /**
     * @title JSONString转Object对象
     */
    public static <T> T toObj(String jsonStr, Class<T> obj) {
        if (!isBlank(jsonStr)) {
            return JSONObject.parseObject(jsonStr, obj);
        }
        return null;
    }

    /**
     * @title 对象数据转JSONString数据
     * @Desc 实现了 SerializeConfig.globalInstance
     */
    public static String toJSON(Object obj) {
        if (!isBlank(obj)) {
            Object json = JSONObject.toJSON(obj, SerializeConfig.globalInstance);
            return getStr(json);
        }
        return "";
    }

    /**
     * @param res 数据检验，为空则结束执行，并抛出errMsg异常
     * @title 严格校验，（null、""）则认为是空
     * @Desc 数组、集合中，若值为（null、""）则认为是空
     */
    public static void checkNull(HttpServletResponse res, Object params, String errMsg) {
        if (StringUtils.isEmpty(errMsg)) {
            errMsg = R.PARAM_ERROR;
        }
        if (isBlank(params)) {
            errMsg(res, errMsg);
            log.info("数据校验失败。。。");
            throw new NullPointerException(errMsg);
        }
    }

    /**
     * @param res 多参数检验，有一个为空则运行结束，并抛出errMsg异常
     * @title 严格校验，值为（null、""）则认为是空
     * @Desc 数组、集合中，若值为（null、""）则认为是空
     */
    public static void checkNull(HttpServletResponse res, String errMsg, Object... params) {
        if (StringUtils.isEmpty(errMsg)) {
            errMsg = R.PARAM_ERROR;
        }
        for (Object object : params) {
            if (isBlank(object)) {
                errMsg(res, errMsg);
                log.info("数据校验失败。。。{}", object);
                throw new NullPointerException(errMsg);
            }
        }
    }

    /**
     * @param res 对象，msg异常提示信息
     * @title 输出错误异常信息
     */
    public static void errMsg(HttpServletResponse res, String msg) {
        printJsonMsg(res, false, msg, null, 500);
    }

    /**
     * @param msg 提示信息，data数据，code代码
     * @Title 输入JSON数据
     */
    @SuppressWarnings("unused")
    public static void printMsg(HttpServletResponse res, String msg, Object data, Integer code) {
        printJsonMsg(res, true, msg, data, code);
    }

    /**
     * @param flag 状态标示 请求处理成功 或失败
     * @param msg  该结果的返回提示信息
     * @Title 输出json
     */
    public static void printJsonMsg(HttpServletResponse res, Object flag, String msg, Object data, Integer coed) {
        JSONObject jo = new JSONObject();
        jo.put("flag", flag);
        if (StringUtils.isNotBlank(msg)) {
            jo.put("msg", msg);
        }
        if (data != null) {
            jo.put("data", data);
        }
        if (coed != null) {
            jo.put("coed", coed);
        }
        res.setContentType("text/json;charset=UTF-8");
        try {
            res.getWriter().print(jo);
        } catch (IOException e) {
            log.info("printJsonMsg写出数据时出现异常...{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @Title 输出JSON数据
     */
    public static void printJsonMsg(HttpServletResponse res, Object flag, Object msg, Object data) {
        res.setContentType("text/json;charset=UTF-8");
        JSONObject jo = new JSONObject();
        jo.put("flag", flag);
        jo.put("data", data);
        jo.put("msg", msg);
        try {
            res.getWriter().print(jo);
        } catch (IOException e) {
            log.info("printJsonMsg写出数据时出现异常...{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @return String
     * @Title 日期格式化
     */
    @SuppressWarnings("unused")
    public static String dateFormat(Date date) {
        String str = "";
        if (null == date || StringUtils.isBlank(String.valueOf(date))) {
            date = new Date();
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = format.format(date);
        } catch (Exception e) {
            log.info("调用ObjectUtils类中的dateFormat方法时出错....{}", e.getMessage());
            e.getStackTrace();
        }
        return str;
    }

    /**
     * @return 第一个非unknown有效的IP为真实IP
     * @title 被代理时, 获取真实IP
     * @Desc 通过代理获取到的x-forwarded-for 是一串字符,真实有效的IP为第一个非unknown有效的IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null, unknown = "unknown";
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // 使用代理，则获取第一个IP地址
            if (StringUtils.isNotEmpty(ip) && ip.replace(" ", "").replace("unknown,", "").indexOf(",") > 0) {
                log.error("IP地址：{}", ip);
                ip = ip.split(",")[0];
            }
        } catch (Exception e) {
            log.error("IPUtils getIpAddr ERROR {}  --->  {}", e.getMessage(), e);
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

}
