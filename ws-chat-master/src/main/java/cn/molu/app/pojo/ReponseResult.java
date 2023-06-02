package cn.molu.app.pojo;

public class ReponseResult {
    /**
     * 操作成功code
     */
    public final static String SUCCESS_CODE = "200";

    /**
     * 操作失败code
     */
    public final static String FAIL_CODE = "500";

    private String code;

    private Object data;

    private String msg;


    public ReponseResult(String code, Object data,String msg) {
        super();
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static ReponseResult ok(Object data){
        return new ReponseResult(SUCCESS_CODE,data,null);
    }

    public static ReponseResult fail(Object data){
        return new ReponseResult(FAIL_CODE,null, (String) data);
    }


}
