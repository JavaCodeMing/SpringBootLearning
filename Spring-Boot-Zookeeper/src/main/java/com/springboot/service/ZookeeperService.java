package com.springboot.service;

import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.List;

/**
 * @author dengzhiming
 * @date 2020/5/5 14:17
 */
public interface ZookeeperService {
    /**
     * 判断节点是否存在
     */
    boolean isExistNode (final String path) throws Exception;
    /**
     * 创建节点
     */
    void createNode (int flag,String path ) throws Exception;
    /**
     * 设置节点数据
     */
    void setNodeData (String path, String nodeData) throws Exception;
    /**
     * 创建节点
     */
    void createNodeAndData (int flag, String path , String nodeData) throws Exception;
    /**
     * 获取节点数据
     */
    String getNodeData (String path) throws Exception;
    /**
     * 获取节点下数据
     */
    List<String> getNodeChild (String path) throws Exception;
    /**
     * 是否递归删除节点
     */
    void deleteNode (String path,Boolean recursive) throws Exception;
    /**
     * 获取读写锁
     */
    InterProcessReadWriteLock getReadWriteLock (String path) throws Exception;
}
