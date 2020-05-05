package com.springboot.service.impl;

import com.springboot.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author dengzhiming
 * @date 2020/5/5 14:18
 */
@Slf4j
@Service
public class ZookeeperServiceImpl implements ZookeeperService {

    @Resource
    CuratorFramework client;

    @Override
    public String getNodeData(String path) throws Exception {
        // 数据读取和转换
        byte[] dataByte = client.getData().forPath(path);
        return new String(dataByte, StandardCharsets.UTF_8);
    }

    @Override
    public boolean isExistNode(String path) throws Exception {
        client.sync();
        Stat stat = client.checkExists().forPath(path);
        return stat != null;
    }

    @Override
    public void createNode(int flag, String path) throws Exception {
        // 递归创建所需父节点
        client.create().creatingParentsIfNeeded().withMode(CreateMode.fromFlag(flag)).forPath(path);
    }

    @Override
    public void setNodeData(String path, String nodeData) throws Exception {
        // 设置节点数据
        client.setData().forPath(path, nodeData.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void createNodeAndData(int flag, String path, String nodeData) throws Exception {
        // 创建节点，关联数据
        client.create().creatingParentsIfNeeded().withMode(CreateMode.fromFlag(flag))
                .forPath(path, nodeData.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public List<String> getNodeChild(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    @Override
    public void deleteNode(String path, Boolean recursive) throws Exception {
        if (recursive) {
            // 递归删除节点
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
        } else {
            // 删除单个节点
            client.delete().guaranteed().forPath(path);
        }
    }

    @Override
    public InterProcessReadWriteLock getReadWriteLock(String path) throws Exception {
        // 写锁互斥、读写互斥
        return new InterProcessReadWriteLock(client, path);
    }
}
