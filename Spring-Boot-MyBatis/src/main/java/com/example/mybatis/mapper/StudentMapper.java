package com.example.mybatis.mapper;

import com.example.mybatis.bean.Student;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by dengzhiming on 2019/3/21
 */
@Repository
@Mapper
public interface StudentMapper {
    //@Insert("insert into student(sno,sname,ssex) values(#{sno},#{name},#{sex})")
    int add(Student student);

    //@Update("update student set sname=#{name},ssex=#{sex} where sno=#{sno}")
    int update(Student student);

    //@Delete("delete from student where sno=#{sno}")
    int deleteById(String sno);

    //@Select("select * from student where sno=#{sno}")
    /*@Results(id = "student", value = {
            @Result(property = "sno", column = "sno", javaType = String.class),
            @Result(property = "name", column = "sname", javaType = String.class),
            @Result(property = "sex", column = "ssex", javaType = String.class)
    })*/
    Student queryStudentById(String sno);
}
