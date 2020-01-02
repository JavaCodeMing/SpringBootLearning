package com.example.publisher.mapper;

import com.example.common.domain.BrokerMessageLog;
import com.example.common.domain.BrokerMessageLogExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BrokerMessageLogMapper {
    long countByExample(BrokerMessageLogExample example);

    int deleteByExample(BrokerMessageLogExample example);

    int deleteByPrimaryKey(String message_id);

    int insert(BrokerMessageLog record);

    int insertSelective(BrokerMessageLog record);

    List<BrokerMessageLog> selectByExample(BrokerMessageLogExample example);

    BrokerMessageLog selectByPrimaryKey(String message_id);

    int updateByExampleSelective(@Param("record") BrokerMessageLog record, @Param("example") BrokerMessageLogExample example);

    int updateByExample(@Param("record") BrokerMessageLog record, @Param("example") BrokerMessageLogExample example);

    int updateByPrimaryKeySelective(BrokerMessageLog record);

    int updateByPrimaryKey(BrokerMessageLog record);
}