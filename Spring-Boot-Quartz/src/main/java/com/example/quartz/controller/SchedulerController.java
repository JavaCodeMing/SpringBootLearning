package com.example.quartz.controller;

import com.example.quartz.entity.JobAndTrigger;
import com.example.quartz.service.JobAndTriggerService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/8/1
 */
@RestController
@RequestMapping("/job")
public class SchedulerController {

    private final JobAndTriggerService service;

    public SchedulerController(JobAndTriggerService service) {
        this.service = service;
    }

    // 查询任务列表
    @GetMapping("/queryjob")
    public Map<String, Object> queryjob(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<JobAndTrigger> list = service.getTriggerAndDetails();
        PageInfo<JobAndTrigger> page = new PageInfo<>(list);
        Map<String, Object> map = new HashMap<>();
        map.put("JobAndTrigger", page);
        map.put("number", page.getTotal());
        return map;
    }

    // 添加任务
    @PostMapping("/addjob")
    public void addjob(@RequestBody JobAndTrigger jobAndTrigger) {
        this.service.addjob(jobAndTrigger);
    }

    //暂停任务
    @PostMapping("/pausejob")
    public void pausejob(@RequestParam("triggerName") String triggerName, @RequestParam("triggerGroup") String triggerGroup) {
        this.service.pausejob(triggerName, triggerGroup);
    }

    //恢复任务
    @PostMapping("/resumejob")
    public void resumejob(@RequestParam("triggerName") String triggerName, @RequestParam("triggerGroup") String triggerGroup) {
        this.service.resumejob(triggerName, triggerGroup);
    }

    //删除任务
    @PostMapping("/deletejob")
    public void deletejob(@RequestParam("triggerName") String triggerName, @RequestParam("triggerGroup") String triggerGroup) {
        this.service.deletejob(triggerName, triggerGroup);
    }

    //更新任务
    @PostMapping("/reschedulejob")
    public void reschedulejob(@RequestBody JobAndTrigger jobAndTrigger) {
        this.service.reschedulejob(jobAndTrigger);
    }
}
