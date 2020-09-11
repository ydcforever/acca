package com.btw.parser.controller;

import com.fate.schedule.SteerableScheduleManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ydc on 2019/12/10.
 */
@Api(value = "定时任务管理")
@Controller
@RequestMapping("/job")
public class ScheduleController {

    private SteerableScheduleManager manager;

    @Autowired
    public void setManager(SteerableScheduleManager manager) {
        this.manager = manager;
    }

    @ApiOperation(value = "开启所有定时")
    @RequestMapping(value = "/startAll.do", method = RequestMethod.POST)
    @ResponseBody
    public void start() {
        manager.startSchedule();
    }

    @ApiOperation(value = "停止定时")
    @RequestMapping(value = "/stop.do", method = RequestMethod.POST)
    @ResponseBody
    public void stop(@RequestParam("id")String id) {
        manager.stopJob(id, true);
    }

    @ApiOperation(value = "开启定时")
    @RequestMapping(value = "/start.do", method = RequestMethod.POST)
    @ResponseBody
    public void start(@RequestParam("id")String id) {
        manager.startJob(id);
    }

    @ApiOperation(value = "修改定时时间")
    @RequestMapping(value = "/modify.do", method = RequestMethod.POST)
    @ResponseBody
    public void modify(@RequestParam("id")String id, @RequestParam("cron") String cron) {
        manager.modifyJob(id, cron);
    }
}
