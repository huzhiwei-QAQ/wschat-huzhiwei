package cn.molu.app.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.io.Serializable;

/**
 * (User)实体类
 *
 * @author 陌路
 * @since 2022-04-21 16:53:50
 */
@TableName("tb_user")
public class User implements Serializable {
    private static final long serialVersionUID = 477020946096486016L;
    /**
     * 用户id
     */
    private Integer id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 是否删除: 0-正常,1-删除
     */
    private String deleted;
    /**
     * 用户状态 0-正常,1-冻结,2-停用
     */
    private String state;
    /**
     * 用户类型: 0-新注册,1-已注册,未完善信息,2-已完善信息
     */
    private String type;
    /**
     * 用户代码
     */
    private String userCode;
    /**
     * 创建时间
     */
    private Date created;
    /**
     * 更新时间
     */
    private Date updated;



    /**
     * 上次的修改时间
     */



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", deleted='" + deleted + '\'' +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", userCode='" + userCode + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}

