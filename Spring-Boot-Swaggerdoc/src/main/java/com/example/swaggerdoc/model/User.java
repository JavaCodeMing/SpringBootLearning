package com.example.swaggerdoc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "User", description = "用户信息对象")
public class User implements Serializable {

    @ApiModelProperty(value = "用户id",name="id",dataType="int",required = false)
    private int id;

    @ApiModelProperty(value = "用户名",name="name",dataType="String",required = false)
    private String name;

    @ApiModelProperty(value ="年龄",name="age",dataType="int",required = false)
    private int age;

    @ApiModelProperty(value = "性别",name="sex",dataType="String",required = false)
    private String sex;

    @ApiModelProperty(value = "家庭住址",name="address",dataType="String",required = false)
    private String address;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
