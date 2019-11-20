package com.example.test.bean;

import java.util.Date;


/**
 * Created by dengzhiming on 2019/4/25
 */
public class User {

    private Long id;
    private String username;
    private String passwd;
    private Date createTime;
    private String status;

    public User() {
    }

    public User(Long id, String username, String passwd, Date createTime, String status) {
        this.id = id;
        this.username = username;
        this.passwd = passwd;
        this.createTime = createTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
