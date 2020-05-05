package com.springboot.controller;

import com.springboot.service.ZookeeperService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dengzhiming
 * @date 2020/5/5 14:15
 */
@RestController
public class ZookeeperController {
    @Resource
    private ZookeeperService zookeeperService;

    //查询节点数据
    @GetMapping("/getNodeData/{path}")
    public String getNodeData(@PathVariable("path") String path) throws Exception {
        return zookeeperService.getNodeData("/" + path);
    }

    //判断节点是否存在
    @GetMapping("/isExistNode/{path}")
    public boolean isExistNode(final @PathVariable("path") String path) throws Exception {
        return zookeeperService.isExistNode("/" + path);
    }

    //创建节点
    @PutMapping("/createNode/{path}/{flag}")
    public String createNode(@PathVariable("flag") int flag, @PathVariable("path") String path) throws Exception {
        // flag: 0(PERSISTENT),1(EPHEMERAL),2(PERSISTENT_SEQUENTIAL),3(EPHEMERAL_SEQUENTIAL)
        zookeeperService.createNode(flag, "/" + path);
        return "success";
    }

    //设置节点数据
    @PutMapping("/setNodeData/{path}/{nodeData}")
    public String setNodeData(@PathVariable("path") String path, @PathVariable("nodeData") String nodeData) throws Exception {
        zookeeperService.setNodeData("/" + path, nodeData);
        return "success";
    }

    //创建并设置节点数据
    @PutMapping("/createNodeAndData/{path}/{nodeData}/{flag}")
    public String createNodeAndData(@PathVariable("flag") int flag, @PathVariable("path") String path, @PathVariable("nodeData") String nodeData) throws Exception {
        zookeeperService.createNodeAndData(flag, "/" + path, nodeData);
        return "success";
    }

    //递归获取节点数据
    @DeleteMapping("/getNodeChild/{path}")
    public List<String> getNodeChild(@PathVariable("path") String path) throws Exception {
        return zookeeperService.getNodeChild("/" + path);
    }

    //是否递归删除节点
    @GetMapping("/deleteNode/{path}")
    public String deleteNode(@PathVariable("path") String path, @PathVariable("path") String flag) throws Exception {
        if ("true".equals(flag.trim())) {
            zookeeperService.deleteNode("/" + path, true);
        } else {
            zookeeperService.deleteNode("/" + path, false);
        }
        return "success";
    }
}
