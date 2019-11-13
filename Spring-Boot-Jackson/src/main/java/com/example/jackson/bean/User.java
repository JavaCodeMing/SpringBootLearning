package com.example.jackson.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dengzhiming on 2019/4/23
 */
//@JsonIgnoreProperties({ "password", "age" })
//@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
//@JsonSerialize(using = UserSerializer.class)
//@JsonDeserialize(using = UserDeserializer.class)
public class User implements Serializable {
    private static final long serialVersionUID = 6222176558369919436L;

    public interface UserBaseView {
    }

    ;

    public interface AllUserFieldView extends UserBaseView {
    }

    ;
    //@JsonView(UserBaseView.class)
    private String userName;
    //@JsonView(UserBaseView.class)
    private int age;
    //@JsonIgnore
    //@JsonView(AllUserFieldView.class)
    private String password;
    //@JsonProperty("bth")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonView(AllUserFieldView.class)
    private Date birthday;

    public User() {
    }

    public User(String userName, int age, String password, Date birthday) {
        this.userName = userName;
        this.age = age;
        this.password = password;
        this.birthday = birthday;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
