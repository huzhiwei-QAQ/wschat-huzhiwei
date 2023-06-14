package cn.molu.app.vo;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @title 数据返回类
 * @Description
 * @author 陌路
 * @date 2022-04-16 下午3:51:04
 */
public class R extends HashMap<String, Object> implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 只返回code值 */
	public static final R OK = R.set("code", 200);
	/** 返回flag和code值 */
	public static final R ok = R.set("code", 200).put("flag", true);

	public static final String PARAM_ERROR = "输入参数错误!";
	public static final String UNKNOW_ERROR = "未知的错误请联系管理员!";

	public R() {
	}

	/**
	 * @title 返回默认信息 flag msg code data
	 * @return R
	 */
	public static R ok() {
		R r = new R();
		r.put("flag", true);
		r.put("msg", "成功！");
		r.put("code", 200);
		r.put("data", null);
		return r;
	}

	/**
	 * @param <T>
	 * @title 添加返回值对象data
	 * @return R
	 */
	public static <T> R ok(T data) {
		R r = new R();
		r.put("flag", true);
		r.put("msg", "成功！");
		r.put("code", 200);
		r.put("data", data);
		return r;
	}

	/**
	 * @title 添加返回值信息
	 * @return R
	 */
	public static R ok(String msg) {
		R r = new R();
		r.put("flag", true);
		r.put("msg", msg);
		r.put("code", 200);
		r.put("data", null);
		return r;
	}

	/**
	 * @title 自定返回值对象key value
	 * @return R
	 */
	public static R ok(String key, String value) {
		R r = new R();
		r.put("flag", true);
		r.put("msg", "成功！");
		r.put("code", 200);
		r.put("data", null);
		r.put(key, value);
		return r;
	}

	/**
	 * @title 添加返回值对象和提示信息
	 * @return R
	 */
	public static R ok(Object data, String msg) {
		R r = new R();
		r.put("flag", true);
		r.put("msg", msg);
		r.put("code", 200);
		r.put("data", data);
		return r;
	}

	/**
	 * @title 返回默认错误信息 flag msg code
	 * @return R
	 */
	public static R err() {
		R r = new R();
		r.put("flag", false);
		r.put("msg", "服务器返回失败！");
		r.put("code", 500);
		r.put("data", null);
		return r;
	}

	/**
	 * @title 添加自定义错误信息 msg
	 * @return R
	 */
	public static R err(String msg) {
		R r = new R();
		r.put("flag", false);
		r.put("msg", msg);
		r.put("code", 500);
		return r;
	}

	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	/**
	 * @title 定义静态方法
	 * @param key
	 * @param value
	 * @return R
	 */
	public static R set(String key, Object value) {
		R r = new R();
		r.put(key, value);
		return r;
	}
}
