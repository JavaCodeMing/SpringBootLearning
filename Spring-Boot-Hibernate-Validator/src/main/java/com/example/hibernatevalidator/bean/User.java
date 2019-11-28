package com.example.hibernatevalidator.bean;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by dengzhiming on 2019/6/29
 */
public class User implements Serializable {
    private static final long serialVersionUID = -2731598327208972274L;

    @NotNull(message = "{required}")
    private String name;
    @Email(message = "{invalid}")
    private String email;

    public User() {
    }

    public User(@NotNull(message = "{required}") String name, @Email(message = "{invalid}") String email) {
        this.name = name;
        this.email = email;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
