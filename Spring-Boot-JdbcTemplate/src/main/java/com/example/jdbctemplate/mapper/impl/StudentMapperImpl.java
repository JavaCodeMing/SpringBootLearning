package com.example.jdbctemplate.mapper.impl;

import com.example.jdbctemplate.bean.Student;
import com.example.jdbctemplate.bean.StudentObj;
import com.example.jdbctemplate.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dengzhiming on 2019/3/24
 */
@Repository("StudentMapper")
public class StudentMapperImpl implements StudentMapper {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int add(Student student) {
        // 较少字段使用: jdbcTemplate
        // String sql = "insert into student(sno,sname,ssex) values(?,?,?)";
        // Object[] args = { student.getSno(), student.getName(), student.getSex() };
        // int[] argTypes = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
        // return this.jdbcTemplate.update(sql, args, argTypes);
        // 较多字段使用: NamedParameterJdbcTemplate; 返回结果,可直接使用List<Map<String, Object>>来接收
        String sql = "insert into student(sno,sname,ssex) values(:sno,:name,:sex)";
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(Objects.requireNonNull(this.jdbcTemplate.getDataSource()));
        return npjt.update(sql, new BeanPropertySqlParameterSource(student));
    }

    @Override
    public int update(Student student) {
        String sql = "update student set sname = ?,ssex = ? where sno = ?";
        Object[] args = {student.getName(), student.getSex(), student.getSno()};
        int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
        return this.jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public int deleteBysno(String sno) {
        String sql = "delete from student where sno = ?";
        Object[] args = {sno};
        int[] argTypes = {Types.VARCHAR};
        return this.jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public List<Map<String, Object>> queryStudentsListMap() {
        String sql = "select * from student";
        return this.jdbcTemplate.queryForList(sql);
    }

    @Override
    public Student queryStudentBySno(String sno) {
        String sql = "select * from student where sno = ?";
        Object[] args = {sno};
        int[] argTypes = {Types.VARCHAR};
        List<Student> studentList = this.jdbcTemplate.query(sql, args, argTypes, new StudentObj());
        if (studentList.size() > 0) {
            return studentList.get(0);
        } else {
            return null;
        }
    }
}
